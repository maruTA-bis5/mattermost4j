/*
 * Copyright (c) 2016-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bis5.mattermost.client4.api;

import static net.bis5.mattermost.client4.Assertions.assertNoError;
import static net.bis5.mattermost.client4.Assertions.assertStatus;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.ws.rs.core.Response.Status;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelList;
import net.bis5.mattermost.model.ChannelSearch;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamExists;
import net.bis5.mattermost.model.TeamInviteInfo;
import net.bis5.mattermost.model.TeamList;
import net.bis5.mattermost.model.TeamMember;
import net.bis5.mattermost.model.TeamMemberList;
import net.bis5.mattermost.model.TeamPatch;
import net.bis5.mattermost.model.TeamSearch;
import net.bis5.mattermost.model.TeamStats;
import net.bis5.mattermost.model.TeamType;
import net.bis5.mattermost.model.TeamUnread;
import net.bis5.mattermost.model.TeamUnreadList;
import net.bis5.mattermost.model.User;

// Teams
@ExtendWith(MattermostClientTestExtension.class)
class TeamsApiTest implements MattermostClientTest {
  private TestHelper th;
  private MattermostClient client;

  @Override
  public void setHelper(TestHelper helper) {
    this.th = helper;
  }

  @BeforeEach
  void setup() {
    client = createClient();
    th.changeClient(client).initBasic();
  }

  @AfterEach
  void tearDown() {
    th.logout();
    client.close();
  }

  @Test
  void createTeam() {
    th.loginSystemAdmin();
    Team team = new Team();
    final String teamName = th.generateTestTeamName();
    final String teamDisplayName = "dn_" + teamName;
    team.setName(teamName);
    team.setDisplayName(teamDisplayName);
    team.setType(TeamType.OPEN);

    ApiResponse<Team> response = assertNoError(client.createTeam(team));
    team = response.readEntity();

    assertThat(team.getName(), is(teamName));
    assertThat(team.getDisplayName(), is(teamDisplayName));
    assertThat(team.getType(), is(TeamType.OPEN));
    assertThat(team.getId(), is(not(nullValue())));
  }

  @Test
  void getTeams() {
    Team team = th.loginSystemAdmin().createTeam();

    Pager pager = Pager.of(0,200);
    ApiResponse<TeamList> response =
        assertNoError(client.getAllTeams(pager, null));
    List<Team> teams = response.readEntity();
    if (teams.size() == 200) {
      while(true) {
        pager = pager.nextPage();
        response = assertNoError(client.getAllTeams(pager, null));
        List<Team> additionalTeams = response.readEntity();
        if (additionalTeams.isEmpty()) {
          break;
        }
        teams.addAll(additionalTeams);
      }
    }

    assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(team.getId()));
  }

  @Test
  void getTeam() {

    ApiResponse<Team> response = assertNoError(client.getTeam(th.basicTeam().getId(), null));
    Team team = response.readEntity();

    assertThat(team.getId(), is(th.basicTeam().getId()));
  }

  @Test
  void updateTeam() {
    th.loginTeamAdmin();
    Team team = th.basicTeam();
    final String teamId = team.getId();
    final String newDispName = "new" + team.getDisplayName();
    team.setDisplayName(newDispName);

    ApiResponse<Team> response = assertNoError(client.updateTeam(team));
    team = response.readEntity();

    assertThat(team.getId(), is(teamId));
    assertThat(team.getDisplayName(), is(newDispName));
  }

  @Test
  void deleteTeam_Soft() {
    th.loginSystemAdmin();

    ApiResponse<Boolean> response = assertNoError(client.deleteTeam(th.basicTeam().getId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void deleteTeam_Permanent() {
    th.loginSystemAdmin();
    Config config = th.client().getConfig().readEntity();
    config.getServiceSettings().setEnableApiTeamDeletion(true);
    assertNoError(th.client().updateConfig(config));


    ApiResponse<Boolean> response = assertNoError(client.deleteTeam(th.basicTeam().getId(), true));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void deleteTeam_Permanent_featureDisabled() {
    th.loginSystemAdmin();
    Config config = th.client().getConfig().readEntity();
    assertFalse(config.getServiceSettings().isEnableApiTeamDeletion(), "precondition");

    assertStatus(client.deleteTeam(th.basicTeam().getId(), true), Status.UNAUTHORIZED);
  }

  @Test
  void patchTeam() {
    th.loginTeamAdmin();
    TeamPatch patch = new TeamPatch();
    final String newDisplayName = "new" + th.basicTeam().getDisplayName();
    patch.setDisplayName(newDisplayName);

    ApiResponse<Team> response = assertNoError(client.patchTeam(th.basicTeam().getId(), patch));
    Team team = response.readEntity();

    assertThat(team.getDisplayName(), is(newDisplayName));
  }

  @Test
  void getTeamByName() {
    final String teamId = th.basicTeam().getId();
    final String teamName = th.basicTeam().getName();

    ApiResponse<Team> response = assertNoError(client.getTeamByName(teamName, null));
    Team team = response.readEntity();

    assertThat(team.getId(), is(teamId));
  }

  @Test
  void searchTeams() {
    TeamSearch search = new TeamSearch();
    search.setTerm(th.basicTeam().getName());

    ApiResponse<TeamList> response = assertNoError(client.searchTeams(search));
    List<Team> teams = response.readEntity();

    assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
        hasItem(th.basicTeam().getId()));
  }

  @Test
  void teamExists_Exists() {

    ApiResponse<TeamExists> response =
        assertNoError(client.teamExists(th.basicTeam().getName(), null));
    TeamExists exists = response.readEntity();

    assertThat(exists.isExists(), is(true));
  }

  @Test
  void teamExists_NotExists() {

    ApiResponse<TeamExists> response =
        assertNoError(client.teamExists("fake" + th.generateTestTeamName(), null));
    TeamExists exists = response.readEntity();

    assertThat(exists.isExists(), is(false));
  }

  @Test
  void getUsersTeams() {
    String userId = th.basicUser().getId();

    ApiResponse<TeamList> response = assertNoError(client.getTeamsForUser(userId, null));
    List<Team> teams = response.readEntity();

    assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
        hasItem(th.basicTeam().getId()));
  }

  @Test
  void getTeamMembers() {

    ApiResponse<TeamMemberList> response =
        assertNoError(client.getTeamMembers(th.basicTeam().getId(), Pager.of(0, 60), null));
    List<TeamMember> teamMembers = response.readEntity();

    assertThat(teamMembers.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        hasItems(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  void addUserToTeam() {
    th.loginSystemAdmin();
    User user = th.createUser();
    th.loginTeamAdmin();
    TeamMember teamMemberToAdd = new TeamMember(th.basicTeam().getId(), user.getId());

    ApiResponse<TeamMember> response = assertNoError(client.addTeamMember(teamMemberToAdd));
    TeamMember teamMember = response.readEntity();

    assertThat(teamMember.getTeamId(), is(teamMemberToAdd.getTeamId()));
    assertThat(teamMember.getUserId(), is(teamMemberToAdd.getUserId()));
  }

  @Test
  void addUserToTeamFromInvite() {
    th.logout().loginSystemAdmin();
    User noTeamUser = th.createUser();
    String inviteId = th.basicTeam().getInviteId();

    th.logout().loginAs(noTeamUser);
    ApiResponse<TeamMember> response =
        assertNoError(client.addTeamMemberFromInvite(null, inviteId));

    TeamMember teamMember = response.readEntity();
    assertEquals(th.basicTeam().getId(), teamMember.getTeamId());
    assertEquals(noTeamUser.getId(), teamMember.getUserId());
  }

  @Test
  void addMultipleUsersToTeam() {
    th.loginSystemAdmin();
    User user1 = th.createUser();
    User user2 = th.createUser();
    th.loginTeamAdmin();

    ApiResponse<TeamMemberList> response =
        assertNoError(client.addTeamMembers(th.basicTeam().getId(), user1.getId(), user2.getId()));
    List<TeamMember> members = response.readEntity();

    assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        containsInAnyOrder(user1.getId(), user2.getId()));
  }

  @Test
  void getTeamMembersForUser() {

    ApiResponse<TeamMemberList> response =
        assertNoError(client.getTeamMembersForUser(th.basicUser().getId(), null));
    List<TeamMember> members = response.readEntity();

    assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        hasItems(th.basicUser().getId()));
  }

  @Test
  void getTeamMember() {
    String teamId = th.basicTeam().getId();
    String userId = th.basicUser2().getId();

    ApiResponse<TeamMember> response = assertNoError(client.getTeamMember(teamId, userId, null));
    TeamMember member = response.readEntity();

    assertThat(member.getTeamId(), is(teamId));
    assertThat(member.getUserId(), is(userId));
  }

  @Test
  void removeUserFromTeam() {
    th.loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String userId = th.basicUser2().getId();

    ApiResponse<Boolean> response = assertNoError(client.removeTeamMember(teamId, userId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void getTeamMembersByIds() {

    ApiResponse<TeamMemberList> response =
        assertNoError(client.getTeamMembersByIds(th.basicTeam().getId(), th.basicUser().getId(),
            th.basicUser2().getId()));
    List<TeamMember> members = response.readEntity();

    assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        hasItems(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  void getTeamStats() {

    ApiResponse<TeamStats> response =
        assertNoError(client.getTeamStats(th.basicTeam().getId(), null));
    TeamStats stats = response.readEntity();

    assertThat(stats.getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  void setTeamIcon() throws URISyntaxException {
    th.logout().loginTeamAdmin();
    Path iconPath = th.getResourcePath(TestHelper.EMOJI_CONSTRUCTION);
    String teamId = th.basicTeam().getId();

    ApiResponse<Boolean> response = assertNoError(client.setTeamIcon(teamId, iconPath));
    assertTrue(response.readEntity());
  }

  @Test
  void getTeamIcon() throws URISyntaxException, IOException {
    th.logout().loginTeamAdmin();
    Path iconPath = th.getResourcePath(TestHelper.EMOJI_CONSTRUCTION);
    String teamId = th.basicTeam().getId();
    assertNoError(client.setTeamIcon(teamId, iconPath));

    ApiResponse<Path> response = assertNoError(client.getTeamIcon(teamId));
    assertTrue(response.readEntity().toFile().exists());
  }

  @Test
  void removeTeamIcon() throws URISyntaxException {
    th.logout().loginTeamAdmin();
    Path iconPath = th.getResourcePath(TestHelper.EMOJI_CONSTRUCTION);
    String teamId = th.basicTeam().getId();
    assertNoError(client.setTeamIcon(teamId, iconPath));

    ApiResponse<Boolean> response = assertNoError(client.removeTeamIcon(teamId));
    assertTrue(response.readEntity());
  }

  @Test
  void updateTeamMemberRoles() {
    th.loginTeamAdmin();

    ApiResponse<Boolean> response = assertNoError(client
        .updateTeamMemberRoles(th.basicTeam().getId(), th.basicUser().getId(), Role.TEAM_ADMIN));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void getTeamUnreadsForUser() {

    ApiResponse<TeamUnreadList> response =
        assertNoError(client.getTeamUnreadForUser(th.basicUser().getId(), null));
    List<TeamUnread> unreads = response.readEntity();

    unreads.stream().findFirst()
        .ifPresent(u -> assertThat(u.getTeamId(), is(th.basicTeam().getId())));
  }

  @Test
  void getTeamUnreadsForTeam() {

    ApiResponse<TeamUnread> response =
        assertNoError(client.getTeamUnread(th.basicTeam().getId(), th.basicUser().getId()));
    TeamUnread unread = response.readEntity();

    assertThat(unread.getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  void tnviteUsersToTheTeamByEmail() {

    ApiResponse<Boolean> response = assertNoError(client.inviteUsersToTeam(th.basicTeam().getId(),
        Collections.singletonList(th.generateTestEmail())));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  @Disabled // Not Implemented
  void importTeamFromOtherApplication() {
  }

  @Test
  void getPublicChannels() {

    ApiResponse<ChannelList> response = assertNoError(
        client.getPublicChannelsForTeam(th.basicTeam().getId(), Pager.of(0, 60), null));
    List<Channel> channels = response.readEntity();

    assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  void searchChannels() {
    ChannelSearch search = new ChannelSearch();
    search.setTerm(th.basicChannel().getName());

    ApiResponse<ChannelList> response =
        assertNoError(client.searchChannels(th.basicTeam().getId(), search));
    List<Channel> channels = response.readEntity();

    assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  void getInviteInfo() {
    Team team = th.basicTeam();
    String inviteId = team.getInviteId();

    ApiResponse<TeamInviteInfo> response = assertNoError(client.getInviteInfo(inviteId));

    TeamInviteInfo inviteInfo = response.readEntity();
    assertEquals(team.getId(), inviteInfo.getId());
    assertEquals(team.getName(), inviteInfo.getName());
    assertEquals(team.getDisplayName(), inviteInfo.getDisplayName());
    assertEquals(team.getDescription(), inviteInfo.getDescription());
  }

}
