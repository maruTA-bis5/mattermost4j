/*
 * Copyright (c) 2017 Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.bis5.mattermost.client4.api;

import java.util.Collection;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamExists;
import net.bis5.mattermost.model.TeamList;
import net.bis5.mattermost.model.TeamMember;
import net.bis5.mattermost.model.TeamMemberList;
import net.bis5.mattermost.model.TeamPatch;
import net.bis5.mattermost.model.TeamSearch;
import net.bis5.mattermost.model.TeamStats;
import net.bis5.mattermost.model.TeamUnread;

/**
 * Team API.
 * 
 * @author Takayuki Maruyama
 */
public interface TeamApi {

  /**
   * creates a team in the system based on the provided team object.
   */
  ApiResponse<Team> createTeam(Team team);

  /**
   * returns a team based on the provided team id string.
   */
  default ApiResponse<Team> getTeam(String teamId) {
    return getTeam(teamId, null);
  }

  /**
   * returns a team based on the provided team id string.
   */
  ApiResponse<Team> getTeam(String teamId, String etag);

  /**
   * returns all teams based on permssions.
   */
  default ApiResponse<TeamList> getAllTeams() {
    return getAllTeams(Pager.defaultPager());
  }

  /**
   * returns all teams based on permssions.
   */
  default ApiResponse<TeamList> getAllTeams(Pager pager) {
    return getAllTeams(pager, null);
  }

  /**
   * returns all teams based on permssions.
   */
  ApiResponse<TeamList> getAllTeams(Pager pager, String etag);

  /**
   * returns a team based on the provided team name string.
   */
  default ApiResponse<Team> getTeamByName(String name) {
    return getTeamByName(name, null);
  }

  /**
   * returns a team based on the provided team name string.
   */
  ApiResponse<Team> getTeamByName(String name, String etag);

  /**
   * returns teams matching the provided search term.
   */
  ApiResponse<TeamList> searchTeams(TeamSearch search);

  /**
   * returns true or false if the team exist or not.
   */
  default ApiResponse<TeamExists> teamExists(String name) {
    return teamExists(name, null);
  }

  /**
   * returns true or false if the team exist or not.
   */
  ApiResponse<TeamExists> teamExists(String name, String etag);

  /**
   * returns a list of teams a user is on. Must be logged in as the user or be a system
   * administrator.
   */
  default ApiResponse<TeamList> getTeamsForUser(String userId) {
    return getTeamsForUser(userId, null);
  }

  /**
   * returns a list of teams a user is on. Must be logged in as the user or be a system
   * administrator.
   */
  ApiResponse<TeamList> getTeamsForUser(String userId, String etag);

  /**
   * returns a team member based on the provided team and user id strings.
   */
  default ApiResponse<TeamMember> getTeamMember(String teamId, String userId) {
    return getTeamMember(teamId, userId, null);
  }

  /**
   * returns a team member based on the provided team and user id strings.
   */
  ApiResponse<TeamMember> getTeamMember(String teamId, String userId, String etag);

  /**
   * will update the roles on a team for a user.
   */
  default ApiResponse<Boolean> updateTeamMemberRoles(String teamId, String userId,
      Collection<Role> newRoles) {
    return updateTeamMemberRoles(teamId, userId, newRoles.toArray(new Role[0]));
  }

  /**
   * will update the roles on a team for a user.
   */
  ApiResponse<Boolean> updateTeamMemberRoles(String teamId, String userId, Role... newROles);

  /**
   * will update a team.
   */
  ApiResponse<Team> updateTeam(Team team);

  /**
   * partially updates a team. Any missing fields are not updated.
   */
  ApiResponse<Team> patchTeam(String teamId, TeamPatch patch);

  /**
   * deletes the team softly (archive only, not permanent delete).
   * 
   * @see #deleteTeam(String, boolean)
   */
  default ApiResponse<Boolean> deleteTeam(String teamId) {
    return deleteTeam(teamId, false);
  }

  /**
   * deletes the team.
   * 
   * @param permanent {@code true}: Permanently delete the team, to be used for compliance reasons
   *        only.
   * @see #deleteTeam(String)
   */
  ApiResponse<Boolean> deleteTeam(String teamId, boolean permanent);

  /**
   * returns team members based on the provided team id string.
   */
  default ApiResponse<TeamMemberList> getTeamMembers(String teamId) {
    return getTeamMembers(teamId, Pager.defaultPager());
  }

  /**
   * returns team members based on the provided team id string.
   */
  default ApiResponse<TeamMemberList> getTeamMembers(String teamId, Pager pager) {
    return getTeamMembers(teamId, pager, null);
  }

  /**
   * returns team members based on the provided team id string.
   */
  ApiResponse<TeamMemberList> getTeamMembers(String teamId, Pager pager, String etag);

  /**
   * returns the team member for a user.
   */
  default ApiResponse<TeamMemberList> getTeamMembersforUser(String userId) {
    return getTeamMembersForUser(userId, null);
  }

  /**
   * returns the team member for a user.
   */
  ApiResponse<TeamMemberList> getTeamMembersForUser(String userId, String etag);

  /**
   * will return an array of team members based on the team id and a list of user ids provided. Must
   * be authenticated.
   */
  default ApiResponse<TeamMemberList> getTeamMembersByIds(String teamId,
      Collection<String> userIds) {
    return getTeamMembersByIds(teamId, userIds.toArray(new String[0]));
  }

  /**
   * will return an array of team members based on the team id and a list of user ids provided. Must
   * be authenticated.
   */
  ApiResponse<TeamMemberList> getTeamMembersByIds(String teamId, String... userIds);

  /**
   * add user to a team and return a team member.
   */
  ApiResponse<TeamMember> addTeamMember(TeamMember teamMemberToAdd);

  /**
   * Should not use this API because server api changed.
   * 
   * @deprecated API Change on Mattermost 4.0
   */
  @Deprecated // API Change on Mattermost 4.0
  ApiResponse<TeamMember> addTeamMember(String teamId, String userId, String hash,
      String dataToHash, String inviteId);

  /**
   * adds user to a team and return a team member.
   * 
   * @since Mattermost 4.0
   */
  ApiResponse<TeamMember> addTeamMember(String hash, String dataToHash, String inviteId);

  /**
   * adds a number of users to a team and returns the team members.
   */
  default ApiResponse<TeamMemberList> addTeamMembers(String teamId, Collection<String> userIds) {
    return addTeamMembers(teamId, userIds.toArray(new String[0]));
  }

  /**
   * adds a number of users to a team and returns the team members.
   */
  ApiResponse<TeamMemberList> addTeamMembers(String teamId, String... userIds);

  /**
   * will remove a user from a team.
   */
  default ApiResponse<Boolean> removeTeamMember(TeamMember teamMember) {
    return removeTeamMember(teamMember.getTeamId(), teamMember.getUserId());
  }

  /**
   * will remove a user from a team.
   */
  ApiResponse<Boolean> removeTeamMember(String teamId, String userId);

  /**
   * returns a team stats based on the team id string. Must be authenticated.
   */
  default ApiResponse<TeamStats> getteamStats(String teamId) {
    return getTeamStats(teamId, null);
  }

  /**
   * returns a team stats based on the team id string. Must be authenticated.
   */
  ApiResponse<TeamStats> getTeamStats(String teamId, String etag);

  /**
   * will return a TeamUnread object that contains the amount of unread messages and mentions the
   * user has for the specified team. Must be authenticated.
   */
  ApiResponse<TeamUnread> getTeamUnread(String teamId, String userId);

  /**
   * will import an exported team from other app into a existing team.
   */
  ApiResponse<byte[]> importTeam(byte[] data, int filesize, String importFrom, String fileName,
      String teamId);

  /**
   * invite users by email to the team.
   */
  ApiResponse<Boolean> inviteUsersToTeam(String teamId, Collection<String> userEmails);

}
