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

  /**
   * creates a user in the system based on the provided user object.
   */
  ApiResponse<User> createUser(User user);

  /**
   * returns the logged in user.
   */
  default ApiResponse<User> getMe() {
    return getMe(null);
  }

  /**
   * returns the logged in user.
   */
  ApiResponse<User> getMe(String etag);

  /**
   * returns a user based on the provided user id string.
   */
  default ApiResponse<User> getUser(String userId) {
    return getUser(userId, null);
  }

  /**
   * returns a user based on the provided user id string.
   */
  ApiResponse<User> getUser(String userId, String etag);


  /**
   * returns a user based pn the provided user name string.
   */
  default ApiResponse<User> getUserByUsername(String username) {
    return getUserByUsername(username, null);
  }

  /**
   * returns a user based pn the provided user name string.
   */
  ApiResponse<User> getUserByUsername(String username, String etag);

  /**
   * returns a user based on the provided user email string.
   */
  default ApiResponse<User> getUserByEmail(String email) {
    return getUserByEmail(email, null);
  }

  /**
   * returns a user based on the provided user email string.
   */
  ApiResponse<User> getUserByEmail(String email, String etag);

  /**
   * returns the users on a team based on search term.
   */
  default ApiResponse<UserAutocomplete> autocompleteUsersInTeam(String teamId, String username) {
    return autocompleteUsersInTeam(teamId, username, null);
  }

  /**
   * returns the users on a team based on search term.
   */
  ApiResponse<UserAutocomplete> autocompleteUsersInTeam(String teamId, String username,
      String etag);

  /**
   * returns the users in a channel based on search term.
   */
  default ApiResponse<UserAutocomplete> autocompleteUsersInChannel(String teamId, String channelId,
      String username) {
    return autocompleteUsersInChannel(teamId, channelId, username, null);
  }

  /**
   * returns the users in a channel based on search term.
   */
  ApiResponse<UserAutocomplete> autocompleteUsersInChannel(String teamId, String channelId,
      String username, String etag);

  /**
   * returns the users in the system based on search term.
   */
  default ApiResponse<UserAutocomplete> autocompleteUsers(String username) {
    return autocompleteUsers(username, null);
  }

  /**
   * returns the users in the system based on search term.
   */
  ApiResponse<UserAutocomplete> autocompleteUsers(String username, String etag);

  /**
   * gets user's profile image. Must be logged in or be a system administrator.
   */
  default ApiResponse<byte[]> getProfileImage(String userId) {
    return getProfileImage(userId, null);
  }

  /**
   * gets user's profile image. Must be logged in or be a system administrator.
   */
  ApiResponse<byte[]> getProfileImage(String userId, String etag);

  /**
   * returns a page of users on the system. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsers() {
    return getUsers(Pager.defaultPager());
  }

  /**
   * returns a page of users on the system. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsers(Pager pager) {
    return getUsers(pager, null);
  }

  /**
   * returns a page of users on the system. Page counting starts at 0.
   */
  ApiResponse<UserList> getUsers(Pager pager, String etag);

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInTeam(String teamId) {
    return getUsersInTeam(teamId, Pager.defaultPager());
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInTeam(String teamId, Pager pager) {
    return getUsersInTeam(teamId, UsersOrder.InTeam.NONE, pager);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInTeam(String teamId, UsersOrder.InTeam order,
      Pager pager) {
    return getUsersInTeam(teamId, order, pager, null);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInTeam(String teamId, Pager pager, String etag) {
    return getUsersInTeam(teamId, UsersOrder.InTeam.NONE, pager, etag);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  ApiResponse<UserList> getUsersInTeam(String teamId, UsersOrder.InTeam order, Pager pager,
      String etag);

  /**
   * returns a page of users who are not in a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersNotInTeam(String teamId) {
    return getUsersNotInTeam(teamId, Pager.defaultPager());
  }

  /**
   * returns a page of users who are not in a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersNotInTeam(String teamId, Pager pager) {
    return getUsersNotInTeam(teamId, pager, null);
  }

  /**
   * returns a page of users who are not in a team. Page counting starts at 0.
   */
  ApiResponse<UserList> getUsersNotInTeam(String teamId, Pager pager, String etag);

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInChannel(String channelId) {
    return getUsersInChannel(channelId, Pager.defaultPager());
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInChannel(String channelId, Pager pager) {
    return getUsersInChannel(channelId, UsersOrder.InChannel.NONE, pager);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInChannel(String channelId, UsersOrder.InChannel order,
      Pager pager) {
    return getUsersInChannel(channelId, order, pager, null);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersInChannel(String channelId, Pager pager, String etag) {
    return getUsersInChannel(channelId, UsersOrder.InChannel.NONE, pager, etag);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  ApiResponse<UserList> getUsersInChannel(String channelId, UsersOrder.InChannel order, Pager pager,
      String etag);

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId) {
    return getUsersNotInChannel(teamId, channelId, Pager.defaultPager());
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId, Pager pager) {
    return getUsersNotInChannel(teamId, channelId, pager, null);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId, Pager pager,
      String etag);

  /**
   * returns a page of users on the system that aren't on any teams. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersWithoutTeam() {
    return getUsersWithoutTeam(Pager.defaultPager());
  }

  /**
   * returns a page of users on the system that aren't on any teams. Page counting starts at 0.
   */
  default ApiResponse<UserList> getUsersWithoutTeam(Pager pager) {
    return getUsersWithoutTeam(pager, null);
  }

  /**
   * returns a page of users on the system that aren't on any teams. Page counting starts at 0.
   */
  ApiResponse<UserList> getUsersWithoutTeam(Pager pager, String etag);

  /**
   * returns a list of users based on the provided user ids.
   */
  default ApiResponse<UserList> getUsersByIds(Collection<String> userIds) {
    return getUsersByIds(userIds.toArray(new String[0]));
  }

  /**
   * returns a list of users based on the provided user ids.
   */
  ApiResponse<UserList> getUsersByIds(String... userIds);

  /**
   * returns a list of users based on the provided usernames.
   */
  default ApiResponse<UserList> getUsersByUsernames(Collection<String> usernames) {
    return getUsersByUsernames(usernames.toArray(new String[0]));
  }

  /**
   * returns a list of users based on the provided usernames.
   */
  ApiResponse<UserList> getUsersByUsernames(String... usernames);

  /**
   * returns a list of users based on some search criteria.
   */
  ApiResponse<UserList> searchUsers(UserSearch search);

  /**
   * updates a user in the system based on the provided user object.
   */
  ApiResponse<User> updateUser(User user);

  /**
   * partially updates a user in the system. Any missing fields are not updated.
   */
  ApiResponse<User> patchUser(String userId, UserPatch patch);

  /**
   * activates multi-factor authentication for a user if activate is true and a valid code is
   * provided. If activate is false, then code is not required and multi-factor authentication is
   * disabled for the user.
   */
  ApiResponse<Boolean> updateUserMfa(String userId, String code, boolean activate);

  /**
   * checks whether a user has MFA active on their account or not based on the provided login id.
   */
  boolean checkUserMfa(String loginId);

  /**
   * will generate a new MFA secret for a user and return it as a string and as a base64 encoded
   * image QR code.
   */
  ApiResponse<MfaSecret> generateMfaSecret(String userId);

  /**
   * updates a user's password. Must be logged in as the user or be a system administrator.
   */
  ApiResponse<Boolean> updateUserPassword(String userId, String currentPassword,
      String newPassword);

  /**
   * updates a user's roles in the system. A user can have "system_user" and "system_admin" roles.
   */
  default ApiResponse<Boolean> updateUserRoles(String userId, Collection<Role> roles) {
    return updateUserRoles(userId, roles.toArray(new Role[0]));
  }

  /**
   * updates a user's roles in the system. A user can have "system_user" and "system_admin" roles.
   */
  ApiResponse<Boolean> updateUserRoles(String userId, Role... roles);

  /**
   * updates status of a user whether active or not.
   */
  ApiResponse<Boolean> updateUserActive(String userId, boolean active);

  /**
   * deactivates a user in the system based on the provided user id string.
   */
  ApiResponse<Boolean> deleteUser(String userId);

  /**
   * will send a link for password resetting to a user with the provided email.
   */
  ApiResponse<Boolean> sendPasswordResetEmail(String email);

  /**
   * uses a recovery code to update reset a user's password.
   */
  ApiResponse<Boolean> resetPassword(String token, String newPassword);

  /**
   * returns a list of sessions based on the provided user id string.
   */
  default ApiResponse<SessionList> getSessions(String userId) {
    return getSessions(userId, null);
  }

  /**
   * returns a list of sessions based on the provided user id string.
   */
  ApiResponse<SessionList> getSessions(String userId, String etag);

  /**
   * revokes a user session based on the provided user id and session id strings.
   */
  default ApiResponse<Boolean> revokeSession(Session session) {
    return revokeSession(session.getUserId(), session.getId());
  }

  /**
   * revokes a user session based on the provided user id and session id strings.
   */
  ApiResponse<Boolean> revokeSession(String userId, String sessionId);

  /**
   * attaches a mobile device ID to the current session.
   */
  ApiResponse<Boolean> attachDeviceId(String deviceId);

  /**
   * will return a list with TeamUnread objects that contain the amount of unread messages and
   * mentions the current user has for the teams it belongs to. An optional team ID can be set to
   * exclude that team from the results. Must be authenticated.
   */
  default ApiResponse<TeamUnreadList> getTeamUnreadForUser(String userId) {
    return getTeamUnreadForUser(userId, null);
  }

  /**
   * will return a list with TeamUnread objects that contain the amount of unread messages and
   * mentions the current user has for the teams it belongs to. An optional team ID can be set to
   * exclude that team from the results. Must be authenticated.
   */
  ApiResponse<TeamUnreadList> getTeamUnreadForUser(String userId, String teamIdToExclude);

  /**
   * returns a list of audit based on the provided user id string.
   */
  default ApiResponse<Audits> getUserAudits(String userId) {
    return getUserAudits(userId, Pager.defaultPager());
  }

  /**
   * returns a list of audit based on the provided user id string.
   */
  default ApiResponse<Audits> getUserAudits(String userId, Pager pager) {
    return getUserAudits(userId, pager, null);
  }

  /**
   * returns a list of audit based on the provided user id string.
   */
  ApiResponse<Audits> getUserAudits(String userId, Pager pager, String etag);

  /**
   * will verify a user's email using the supplied token.
   */
  ApiResponse<Boolean> verifyUserEmail(String token);

  /**
   * will send an email to the user with the provided email addresses, if that user exists. The
   * email will contain a link that can be used to verify the user's email address.
   */
  ApiResponse<Boolean> sendVerificationEmail(String email);

  /**
   * sets profile image of the user.
   */
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

  ApiResponse<Boolean> revokeAllActiveSessionForUser(String userId);

  /**
   * delete user profile image.
   */
  ApiResponse<Boolean> deleteProfileImage(String userId);

}
