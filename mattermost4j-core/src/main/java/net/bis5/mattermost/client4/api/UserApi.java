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

import java.nio.file.Path;
import java.util.Collection;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.model.UsersOrder;
import net.bis5.mattermost.model.Audits;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Session;
import net.bis5.mattermost.model.SessionList;
import net.bis5.mattermost.model.TeamUnreadList;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAccessToken;
import net.bis5.mattermost.model.UserAccessTokenList;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;
import net.bis5.mattermost.model.license.MfaSecret;

/**
 * User API.
 * 
 * @author Takayuki Maruyama
 */
public interface UserApi {

  ApiResponse<User> createUser(User user);

  default ApiResponse<User> getMe() {
    return getMe(null);
  }

  ApiResponse<User> getMe(String etag);

  default ApiResponse<User> getUser(String userId) {
    return getUser(userId, null);
  }

  ApiResponse<User> getUser(String userId, String etag);

  default ApiResponse<User> getUserByUsername(String username) {
    return getUserByUsername(username, null);
  }

  ApiResponse<User> getUserByUsername(String username, String etag);

  default ApiResponse<User> getUserByEmail(String email) {
    return getUserByEmail(email, null);
  }

  ApiResponse<User> getUserByEmail(String email, String etag);

  default ApiResponse<UserAutocomplete> autocompleteUsersInTeam(String teamId, String username) {
    return autocompleteUsersInTeam(teamId, username, null);
  }

  ApiResponse<UserAutocomplete> autocompleteUsersInTeam(String teamId, String username,
      String etag);

  default ApiResponse<UserAutocomplete> autocompleteUsersInChannel(String teamId, String channelId,
      String username) {
    return autocompleteUsersInChannel(teamId, channelId, username, null);
  }

  ApiResponse<UserAutocomplete> autocompleteUsersInChannel(String teamId, String channelId,
      String username, String etag);

  default ApiResponse<UserAutocomplete> autocompleteUsers(String username) {
    return autocompleteUsers(username, null);
  }

  ApiResponse<UserAutocomplete> autocompleteUsers(String username, String etag);

  default ApiResponse<byte[]> getProfileImage(String userId) {
    return getProfileImage(userId, null);
  }

  ApiResponse<byte[]> getProfileImage(String userId, String etag);

  default ApiResponse<UserList> getUsers() {
    return getUsers(Pager.defaultPager());
  }

  default ApiResponse<UserList> getUsers(Pager pager) {
    return getUsers(pager, null);
  }

  ApiResponse<UserList> getUsers(Pager pager, String etag);

  default ApiResponse<UserList> getUsersInTeam(String teamId) {
    return getUsersInTeam(teamId, Pager.defaultPager());
  }

  default ApiResponse<UserList> getUsersInTeam(String teamId, Pager pager) {
    return getUsersInTeam(teamId, UsersOrder.InTeam.NONE, pager);
  }

  default ApiResponse<UserList> getUsersInTeam(String teamId, UsersOrder.InTeam order,
      Pager pager) {
    return getUsersInTeam(teamId, order, pager, null);
  }

  default ApiResponse<UserList> getUsersInTeam(String teamId, Pager pager, String etag) {
    return getUsersInTeam(teamId, UsersOrder.InTeam.NONE, pager, etag);
  }

  ApiResponse<UserList> getUsersInTeam(String teamId, UsersOrder.InTeam order, Pager pager,
      String etag);

  default ApiResponse<UserList> getUsersNotInTeam(String teamId) {
    return getUsersNotInTeam(teamId, Pager.defaultPager());
  }

  default ApiResponse<UserList> getUsersNotInTeam(String teamId, Pager pager) {
    return getUsersNotInTeam(teamId, pager, null);
  }

  ApiResponse<UserList> getUsersNotInTeam(String teamId, Pager pager, String etag);

  default ApiResponse<UserList> getUsersInChannel(String channelId) {
    return getUsersInChannel(channelId, Pager.defaultPager());
  }

  default ApiResponse<UserList> getUsersInChannel(String channelId, Pager pager) {
    return getUsersInChannel(channelId, UsersOrder.InChannel.NONE, pager);
  }

  default ApiResponse<UserList> getUsersInChannel(String channelId, UsersOrder.InChannel order,
      Pager pager) {
    return getUsersInChannel(channelId, order, pager, null);
  }

  default ApiResponse<UserList> getUsersInChannel(String channelId, Pager pager, String etag) {
    return getUsersInChannel(channelId, UsersOrder.InChannel.NONE, pager, etag);
  }

  ApiResponse<UserList> getUsersInChannel(String channelId, UsersOrder.InChannel order, Pager pager,
      String etag);

  default ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId) {
    return getUsersNotInChannel(teamId, channelId, Pager.defaultPager());
  }

  default ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId, Pager pager) {
    return getUsersNotInChannel(teamId, channelId, pager, null);
  }

  ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId, Pager pager,
      String etag);

  default ApiResponse<UserList> getUsersWithoutTeam() {
    return getUsersWithoutTeam(Pager.defaultPager());
  }

  default ApiResponse<UserList> getUsersWithoutTeam(Pager pager) {
    return getUsersWithoutTeam(pager, null);
  }

  ApiResponse<UserList> getUsersWithoutTeam(Pager pager, String etag);

  default ApiResponse<UserList> getUsersByIds(Collection<String> userIds) {
    return getUsersByIds(userIds.toArray(new String[0]));
  }

  ApiResponse<UserList> getUsersByIds(String... userIds);

  default ApiResponse<UserList> getUsersByUsernames(Collection<String> usernames) {
    return getUsersByUsernames(usernames.toArray(new String[0]));
  }

  ApiResponse<UserList> getUsersByUsernames(String... usernames);

  ApiResponse<UserList> searchUsers(UserSearch search);

  ApiResponse<User> updateUser(User user);

  ApiResponse<User> patchUser(String userId, UserPatch patch);

  ApiResponse<Boolean> updateUserMfa(String userId, String code, boolean activate);

  boolean checkUserMfa(String loginId);

  ApiResponse<MfaSecret> generateMfaSecret(String userId);

  ApiResponse<Boolean> updateUserPassword(String userId, String currentPassword,
      String newPassword);

  default ApiResponse<Boolean> updateUserRoles(String userId, Collection<Role> roles) {
    return updateUserRoles(userId, roles.toArray(new Role[0]));
  }

  ApiResponse<Boolean> updateUserRoles(String userId, Role... roles);

  ApiResponse<Boolean> updateUserActive(String userId, boolean active);

  ApiResponse<Boolean> deleteUser(String userId);

  ApiResponse<Boolean> sendPasswordResetEmail(String email);

  ApiResponse<Boolean> resetPassword(String token, String newPassword);

  default ApiResponse<SessionList> getSessions(String userId) {
    return getSessions(userId, null);
  }

  ApiResponse<SessionList> getSessions(String userId, String etag);

  default ApiResponse<Boolean> revokeSession(Session session) {
    return revokeSession(session.getUserId(), session.getId());
  }

  ApiResponse<Boolean> revokeSession(String userId, String sessionId);

  ApiResponse<Boolean> attachDeviceId(String deviceId);

  default ApiResponse<TeamUnreadList> getTeamUnreadForUser(String userId) {
    return getTeamUnreadForUser(userId, null);
  }

  ApiResponse<TeamUnreadList> getTeamUnreadForUser(String userId, String teamIdToExclude);

  default ApiResponse<Audits> getUserAudits(String userId) {
    return getUserAudits(userId, Pager.defaultPager());
  }

  default ApiResponse<Audits> getUserAudits(String userId, Pager pager) {
    return getUserAudits(userId, pager, null);
  }

  ApiResponse<Audits> getUserAudits(String userId, Pager pager, String etag);

  ApiResponse<Boolean> verifyUserEmail(String token);

  ApiResponse<Boolean> sendVerificationEmail(String email);

  ApiResponse<Boolean> setProfileImage(String userId, Path imageFilePath);

  ApiResponse<UserAccessToken> createUserAccessToken(String userId, String description);

  default ApiResponse<UserAccessTokenList> getUserAccessTokens(String userId) {
    return getUserAccessTokens(userId, Pager.defaultPager());
  }

  ApiResponse<UserAccessTokenList> getUserAccessTokens(String userId, Pager pager);

  default ApiResponse<UserAccessTokenList> getUserAccessTokensAllUsers() {
    return getUserAccessTokensAllUsers(Pager.defaultPager());
  }

  ApiResponse<UserAccessTokenList> getUserAccessTokensAllUsers(Pager pager);

  ApiResponse<Boolean> revokeUserAccessToken(String tokenId);

  ApiResponse<UserAccessToken> getUserAccessToken(String tokenId);

  ApiResponse<Boolean> disableUserAccessToken(String tokenId);

  ApiResponse<Boolean> enableUserAccessToken(String tokenId);

  ApiResponse<UserAccessTokenList> searchTokens(String term);

}
