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
import static net.bis5.mattermost.client4.Assertions.isNotSupportVersion;
import static net.bis5.mattermost.client4.Assertions.isSupportVersion;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.ws.rs.core.Response.Status;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.client4.model.UsersOrder.InChannel;
import net.bis5.mattermost.client4.model.UsersOrder.InTeam;
import net.bis5.mattermost.model.Audit;
import net.bis5.mattermost.model.Audits;
import net.bis5.mattermost.model.AuthService;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Session;
import net.bis5.mattermost.model.SessionList;
import net.bis5.mattermost.model.StatusType;
import net.bis5.mattermost.model.SwitchRequest;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAccessToken;
import net.bis5.mattermost.model.UserAccessTokenList;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;

// Users
@ExtendWith(MattermostClientTestExtension.class)
class UsersApiTest implements MattermostClientTest {
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
  void createUser() {
    User user = new User();
    user.setEmail(th.generateTestEmail());
    user.setUsername(th.generateTestUsername());
    user.setPassword(TestHelper.DEFAULT_PASSWORD);

    ApiResponse<User> response = assertNoError(client.createUser(user));
    User created = response.readEntity();

    assertThat(created.getEmail(), is(user.getEmail()));
    assertThat(created.getUsername(), is(user.getUsername()));
  }

  @Test
  void getUsers() {
    ApiResponse<UserList> response = assertNoError(client.getUsers(Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users, is(not(emptyIterable())));
  }

  @Test
  void getUsers_InChannel() {
    th.loginBasic();
    Channel channel = th.createPublicChannel();
    assertNoError(client.addChannelMember(channel.getId(), th.basicUser2().getId()));

    ApiResponse<UserList> response =
        assertNoError(client.getUsersInChannel(channel.getId(), Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  void getUsers_InChannel_Order() {
    th.logout().loginSystemAdmin();
    User user1 = th.createUser("order1_" + th.newId());
    th.linkUserToTeam(user1, th.basicTeam());
    User user2 = th.createUser("order2_" + th.newId());
    th.linkUserToTeam(user2, th.basicTeam());
    User user3 = th.createUser("order3_" + th.newId());
    th.linkUserToTeam(user3, th.basicTeam());
    User user4 = th.createUser("order4_" + th.newId());
    th.linkUserToTeam(user4, th.basicTeam());

    // state order: user4, user3, user2, user1
    net.bis5.mattermost.model.Status status = new net.bis5.mattermost.model.Status();
    status.setStatus(StatusType.ONLINE.getCode());
    status.setUserId(user4.getId());
    assertNoError(client.updateUserStatus(user4.getId(), status));
    status.setStatus(StatusType.AWAY.getCode());
    status.setUserId(user3.getId());
    assertNoError(client.updateUserStatus(user3.getId(), status));
    status.setStatus(StatusType.DND.getCode());
    status.setUserId(user2.getId());
    assertNoError(client.updateUserStatus(user2.getId(), status));
    status.setStatus(StatusType.OFFLINE.getCode());
    status.setUserId(user1.getId());
    assertNoError(client.updateUserStatus(user1.getId(), status));
    th.logout().loginAs(user1);

    List<String> expectedIdsByUsername =
        Arrays.asList(user1.getId(), user2.getId(), user3.getId(), user4.getId());
    List<String> expectedIdsByStatus =
        Arrays.asList(user4.getId(), user3.getId(), user2.getId(), user1.getId());

    Channel channel = th.createPublicChannel();
    assertNoError(client.addChannelMember(channel.getId(), user2.getId()));
    assertNoError(client.addChannelMember(channel.getId(), user3.getId()));
    assertNoError(client.addChannelMember(channel.getId(), user4.getId()));

    UserList usersByUsername = assertNoError(
        client.getUsersInChannel(channel.getId(), InChannel.NONE, Pager.defaultPager()))
            .readEntity();
    List<String> ids = usersByUsername.stream().map(User::getId).collect(Collectors.toList());
    assertIterableEquals(expectedIdsByUsername, ids);

    UserList usersByStatus = assertNoError(
        client.getUsersInChannel(channel.getId(), InChannel.STATUS, Pager.defaultPager()))
            .readEntity();
    ids = usersByStatus.stream().map(User::getId).collect(Collectors.toList());
    assertIterableEquals(expectedIdsByStatus, ids);
  }

  @Test
  void getUsers_NotInChannel() {
    Set<String> notInChannelUserIds = new HashSet<>(
        Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(), th.teamAdminUser().getId()));
    th.loginBasic();
    Channel channel = th.createPublicChannel();
    notInChannelUserIds.remove(th.basicUser().getId());

    ApiResponse<UserList> response = assertNoError(client
        .getUsersNotInChannel(th.basicTeam().getId(), channel.getId(), Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        hasItems(notInChannelUserIds.toArray(new String[0])));
  }

  @Test
  void getUsers_InTeam() {
    User notInTeamUser = th.loginSystemAdmin().createUser();
    Set<String> inTeamUserIds = new HashSet<>(
        Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(), th.teamAdminUser().getId()));
    th.loginBasic();

    ApiResponse<UserList> response =
        assertNoError(client.getUsersInTeam(th.basicTeam().getId(), Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    Set<String> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
    assertThat(userIds, not(hasItems(notInTeamUser.getId())));
    assertThat(userIds, hasItems(inTeamUserIds.toArray(new String[0])));
  }

  @Test
  void getUsers_InTeam_Order() {
    th.logout().loginSystemAdmin();
    User user1 = th.createUser("order1_" + th.newId());
    th.logout().loginAs(user1);
    Team team = th.createTeam();
    User user2 = th.createUser("order2_" + th.newId());
    User user3 = th.createUser("order3_" + th.newId());
    th.linkUserToTeam(user2, team);
    th.linkUserToTeam(user3, team);

    // create_at order : user3, user2, user1
    List<String> expectedIdsByCreateAt = Arrays.asList(user3.getId(), user2.getId(), user1.getId());
    // username order: user1, user2, user3
    List<String> expectedIdsByUsername = Arrays.asList(user1.getId(), user2.getId(), user3.getId());

    UserList users =
        assertNoError(client.getUsersInTeam(team.getId(), InTeam.CREATE_AT, Pager.defaultPager()))
            .readEntity();
    List<String> ids = users.stream().map(User::getId).collect(Collectors.toList());
    assertIterableEquals(expectedIdsByCreateAt, ids);

    users = assertNoError(client.getUsersInTeam(team.getId(), InTeam.NONE, Pager.defaultPager()))
        .readEntity();
    ids = users.stream().map(User::getId).collect(Collectors.toList());
    assertIterableEquals(expectedIdsByUsername, ids);
  }

  @Test
  void getUsers_NotInTeam() {
    th.loginBasic();
    Set<String> inTeamUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(),
        th.basicUser2().getId(), th.systemAdminUser().getId(), th.teamAdminUser().getId()));

    ApiResponse<UserList> response =
        assertNoError(client.getUsersNotInTeam(th.basicTeam().getId(), Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        not(hasItems(inTeamUserIds.toArray(new String[0]))));
  }

  @Test
  void getUsers_WithoutTeam() {
    User withoutTeamUser = th.loginSystemAdmin().createUser();
    th.loginSystemAdmin();

    ApiResponse<UserList> response =
        assertNoError(client.getUsersWithoutTeam(Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(withoutTeamUser.getId()));
  }

  @Test
  void getUsersByIds() {

    ApiResponse<UserList> response =
        assertNoError(client.getUsersByIds(th.basicUser().getId(), th.basicUser2().getId()));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  void getUsersByUsernames() {

    ApiResponse<UserList> response = assertNoError(
        client.getUsersByUsernames(th.basicUser().getUsername(), th.basicUser2().getUsername()));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  void searchUsers() {
    UserSearch criteria = UserSearch.builder().term(th.basicUser().getUsername())
        .teamId(th.basicTeam().getId()).build();

    ApiResponse<UserList> response = assertNoError(client.searchUsers(criteria));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getUsername).collect(Collectors.toSet()),
        hasItem(th.basicUser().getUsername()));
  }

  @Test
  void autocompleteUsers() {

    ApiResponse<UserAutocomplete> response =
        assertNoError(client.autocompleteUsers(th.basicUser().getUsername(), null));
    UserAutocomplete autocompleteUsers = response.readEntity();

    assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(th.basicUser().getId()));
  }

  @Test
  void autocompleteUsers_InTeam() {

    ApiResponse<UserAutocomplete> response = assertNoError(
        client.autocompleteUsersInTeam(th.basicTeam().getId(), th.basicUser().getUsername(), null));
    UserAutocomplete autocompleteUsers = response.readEntity();

    assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(th.basicUser().getId()));
  }

  @Test
  void autocompleteUsers_InChannel() {
    Channel channel = th.createPublicChannel();

    ApiResponse<UserAutocomplete> response = assertNoError(
        client.autocompleteUsersInChannel(th.basicTeam().getId(), channel.getId(), null, null));
    UserAutocomplete autocompleteUsers = response.readEntity();

    assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(th.basicUser().getId()));
    assertThat(
        autocompleteUsers.getOutOfChannel().stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(th.basicUser2().getId()));
  }

  @Test
  void getUser() {
    String userId = th.basicUser().getId();

    ApiResponse<User> response = assertNoError(client.getUser(userId, null));
    User user = response.readEntity();

    assertThat(user.getId(), is(userId));
  }

  @Test
  void updateUser() {
    User user = th.basicUser();
    String firstName = "newFirst" + user.getFirstName();
    String lastName = "newLast" + user.getLastName();
    user.setFirstName(firstName);
    user.setLastName(lastName);

    ApiResponse<User> response = assertNoError(client.updateUser(user));
    user = response.readEntity();

    assertThat(user.getFirstName(), is(firstName));
    assertThat(user.getLastName(), is(lastName));
  }

  @Test
  void deactivateUser() {
    th.loginSystemAdmin();
    String userId = th.basicUser().getId();

    ApiResponse<Boolean> response = assertNoError(client.deleteUser(userId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void patchUser() {
    UserPatch patch = new UserPatch();
    String firstName = "newFirst" + th.basicUser().getFirstName();
    String lastName = "newLast" + th.basicUser().getLastName();
    patch.setFirstName(firstName);
    patch.setLastName(lastName);

    ApiResponse<User> response = assertNoError(client.patchUser(th.basicUser().getId(), patch));
    User user = response.readEntity();

    assertThat(user.getFirstName(), is(firstName));
    assertThat(user.getLastName(), is(lastName));
  }

  @Test
  void patchEmailForCurrentUserWithPassword() {
    th.logout().loginSystemAdmin();
    UserPatch patch = new UserPatch();
    patch.setEmail("newemail" + th.systemAdminUser().getId() + "@inbucket.local");
    patch.setPassword(th.systemAdminUser().getPassword());
    try {
      ApiResponse<User> response = assertNoError(client.patchUser("me", patch));
      assertEquals(patch.getEmail(), response.readEntity().getEmail());
    } finally {
      patch.setEmail(th.systemAdminUser().getEmail());
      client.patchUser("me", patch);
    }
  }

  @Test
  void patchEmailForOtherUserWithoutPassword() {
    th.logout().loginSystemAdmin();
    UserPatch patch = new UserPatch();
    patch.setEmail("newemail" + th.basicUser().getId() + "@inbucket.local");

    try {
      ApiResponse<User> response = assertNoError(client.patchUser(th.basicUser().getId(), patch));
      assertEquals(patch.getEmail(), response.readEntity().getEmail());
    } finally {
      patch.setEmail(th.systemAdminUser().getEmail());
      client.patchUser(th.basicUser().getId(), patch);
    }
  }

  @Test
  void updateUserRoles() {
    th.loginSystemAdmin();

    ApiResponse<Boolean> response = assertNoError(
        client.updateUserRoles(th.basicUser().getId(), Role.SYSTEM_ADMIN, Role.SYSTEM_USER));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void updateUserActiveStatus() {
    th.logout().loginSystemAdmin();
    ApiResponse<Boolean> response =
        assertNoError(client.updateUserActive(th.basicUser().getId(), false));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void getUserProfileImage() throws FileNotFoundException, IOException {

    ApiResponse<byte[]> response =
        assertNoError(client.getProfileImage(th.basicUser().getId(), null));
    byte[] image = response.readEntity();

    Path tempFile = Files.createTempFile("mm", ".png");
    System.out.println(tempFile);
    try (FileOutputStream fout = new FileOutputStream(tempFile.toFile())) {
      fout.write(image);
    }
  }

  @Test
  void setUserProfileImage() throws URISyntaxException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);

    ApiResponse<Boolean> response =
        assertNoError(client.setProfileImage(th.basicUser().getId(), image));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void deleteUserProfileImage() throws URISyntaxException {
    Path image = th.getResourcePath(TestHelper.EMOJI_CONSTRUCTION);
    ApiResponse<Boolean> uploadResult =
        assertNoError(client.setProfileImage(th.basicUser().getId(), image));
    if (isNotSupportVersion("5.6.0", uploadResult)) {
      return;
    }
    assertTrue(uploadResult.readEntity());

    ApiResponse<Boolean> deleteResult =
        assertNoError(client.deleteProfileImage(th.basicUser().getId()));
    assertTrue(deleteResult.readEntity());
  }

  @Test
  void getUserByName() {
    String username = th.basicUser().getUsername();

    ApiResponse<User> response = assertNoError(client.getUserByUsername(username, null));
    User user = response.readEntity();

    assertThat(user.getId(), is(th.basicUser().getId()));
  }

  @Test
  void resetPassword() {

    // invalid token
    assertStatus(
        client.resetPassword(th.newRandomString(64), TestHelper.DEFAULT_PASSWORD.concat("_Modify")),
        Status.BAD_REQUEST);
  }

  @Test
  void updateUserMfa() {

    // Enterprise Edition required
    assertStatus(client.updateUserMfa(th.basicUser().getId(), null, false), Status.NOT_IMPLEMENTED);
  }

  @Test
  void generateMfaSecret() {
    th.loginSystemAdmin();

    // Enterprise Edition required
    assertStatus(client.generateMfaSecret(th.basicUser().getId()), Status.NOT_IMPLEMENTED);
  }

  @Test
  void checkMfa() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    Config config = configResponse.readEntity();
    if (isSupportVersion("5.9.0", configResponse)) {
      config.getServiceSettings().setDisableLegacyMfa(false);
      assertNoError(client.updateConfig(config));
    }
    try {
      th.logout().loginBasic();

      boolean mfaRequired = client.checkUserMfa(th.basicUser().getId());

      assertThat(mfaRequired, is(false));
    } finally {
      if (isSupportVersion("5.9.0", configResponse)) {
        th.logout().loginSystemAdmin();
        config = client.getConfig().readEntity();
        config.getServiceSettings().setDisableLegacyMfa(true);
        assertNoError(client.updateConfig(config));
      }
    }
  }

  @Test
  void checkMfaThrowsExceptionDisableLegacyMfa() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.9.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setDisableLegacyMfa(true);
    assertNoError(client.updateConfig(config));

    try {
      assertThrows(RuntimeException.class, () -> {
        client.checkUserMfa(th.basicUser().getId());
      });
    } finally {
      th.logout().loginSystemAdmin();
      config = client.getConfig().readEntity();
      config.getServiceSettings().setDisableLegacyMfa(false);
      assertNoError(client.updateConfig(config));
    }
  }

  @Test
  void updateUserPassword() {
    String userId = th.basicUser().getId();
    String currentPassword = th.basicUser().getPassword();
    String newPassword = "new" + currentPassword;

    ApiResponse<Boolean> response =
        assertNoError(client.updateUserPassword(userId, currentPassword, newPassword));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void sendPasswordResetEmail() {
    String email = th.basicUser().getEmail();

    ApiResponse<Boolean> response = assertNoError(client.sendPasswordResetEmail(email));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void getUserByEmail() {
    String email = th.basicUser().getEmail();

    ApiResponse<User> response = assertNoError(client.getUserByEmail(email, null));
    User user = response.readEntity();

    assertThat(user.getId(), is(th.basicUser().getId()));
  }

  @Test
  void getUserSessions() {
    String userId = th.basicUser().getId();

    ApiResponse<SessionList> response = assertNoError(client.getSessions(userId, null));
    List<Session> sessions = response.readEntity();

    assertThat(sessions.stream().findAny().map(Session::getUserId).get(), is(userId));
  }

  @Test
  void revokeUserSession() {
    ApiResponse<SessionList> response =
        assertNoError(client.getSessions(th.basicUser().getId(), null));
    Session session = response.readEntity().stream().findAny().get();

    ApiResponse<Boolean> response2 =
        assertNoError(client.revokeSession(session.getUserId(), session.getId()));
    boolean result = response2.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void attachMobileDevice() {
    ApiResponse<Boolean> response = assertNoError(client.attachDeviceId(th.newId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void getAudits() {

    ApiResponse<Audits> response =
        assertNoError(client.getUserAudits(th.basicUser().getId(), Pager.of(0, 50), null));
    Audits audits = response.readEntity();

    assertThat(audits.stream().findAny().map(Audit::getId).get(), is(not(nullValue())));
  }

  @Test
  void verifyEmail() {

    // invalid token
    assertStatus(client.verifyUserEmail(th.newId()), Status.BAD_REQUEST);
  }

  @Test
  void sendVerificationEmail() {

    ApiResponse<Boolean> response =
        assertNoError(client.sendVerificationEmail(th.basicUser().getEmail()));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void switchLoginMethod() {
    SwitchRequest request = new SwitchRequest();
    request.setCurrentService(AuthService.Email);
    request.setNewService(AuthService.GitLab);
    request.setEmail(th.basicUser().getEmail());
    request.setCurrentPassword(th.basicUser().getPassword());
    request.setPassword(th.basicUser().getPassword()); // for 4.0+
    th.loginBasic();

    assertStatus(client.switchAccountType(request), Status.NOT_IMPLEMENTED);
  }

  private void setupUserAccessTokenRolesForNormalUser(String userId) {
    th.logout().loginSystemAdmin();
    assertNoError(client.updateUserRoles(userId, Role.SYSTEM_USER_ACCESS_TOKEN, Role.SYSTEM_USER));
    th.logout().loginBasic();
  }

  @Test
  void createUserAccessToken() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description = userId + "_UserAccessTokenDesc";

    UserAccessToken token =
        assertNoError(client.createUserAccessToken(userId, description)).readEntity();

    assertEquals(userId, token.getUserId());
    assertEquals(description, token.getDescription());
    assertNotNull(token.getToken());
  }

  @Test
  void getUserAccessTokens() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description1 = th.newRandomString(32);
    UserAccessToken token1 =
        assertNoError(client.createUserAccessToken(userId, description1)).readEntity();
    String description2 = th.newRandomString(32);
    UserAccessToken token2 =
        assertNoError(client.createUserAccessToken(userId, description2)).readEntity();

    UserAccessTokenList tokens = assertNoError(client.getUserAccessTokens(userId)).readEntity();
    assertEquals(2, tokens.size());
    assertThat(tokens.stream().map(UserAccessToken::getId).collect(Collectors.toSet()),
        containsInAnyOrder(token1.getId(), token2.getId()));
  }

  @Test
  void getUserAccessTokensAllUsers() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description = userId + "_UserAccessTokenDesc";
    UserAccessToken token =
        assertNoError(client.createUserAccessToken(userId, description)).readEntity();

    th.logout().loginSystemAdmin();

    UserAccessTokenList tokens = assertNoError(client.getUserAccessTokensAllUsers()).readEntity();
    assertTrue(tokens.stream().map(UserAccessToken::getId).collect(Collectors.toSet())
        .contains(token.getId()));
  }

  @Test
  void revokeUserAccessToken() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description = userId;
    UserAccessToken uat =
        assertNoError(client.createUserAccessToken(userId, description)).readEntity();

    ApiResponse<Boolean> revokeResponse = assertNoError(client.revokeUserAccessToken(uat.getId()));

    assertTrue(revokeResponse.readEntity());
  }

  @Test
  void getUserAccessToken() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description = userId;
    UserAccessToken uat =
        assertNoError(client.createUserAccessToken(userId, description)).readEntity();

    UserAccessToken received = assertNoError(client.getUserAccessToken(uat.getId())).readEntity();

    assertEquals(uat.getId(), received.getId());
    assertNull(received.getToken()); // response does not contains actual token
  }

  @Test
  void disableUserAccessToken() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description = userId;
    UserAccessToken uat =
        assertNoError(client.createUserAccessToken(userId, description)).readEntity();

    ApiResponse<Boolean> disableResponse =
        assertNoError(client.disableUserAccessToken(uat.getId()));

    assertTrue(disableResponse.readEntity());
  }

  @Test
  void enableUserAccessToken() {
    String userId = th.basicUser().getId();
    setupUserAccessTokenRolesForNormalUser(userId);
    String description = userId;
    UserAccessToken uat =
        assertNoError(client.createUserAccessToken(userId, description)).readEntity();
    assertNoError(client.disableUserAccessToken(uat.getId()));
    uat = assertNoError(client.getUserAccessToken(uat.getId())).readEntity();
    assertFalse(uat.isActive());

    ApiResponse<Boolean> enableResponse = assertNoError(client.enableUserAccessToken(uat.getId()));

    assertTrue(enableResponse.readEntity());
  }

  @Test
  void searchTokens() {
    setupUserAccessTokenRolesForNormalUser(th.basicUser().getId());
    setupUserAccessTokenRolesForNormalUser(th.basicUser2().getId());
    setupUserAccessTokenRolesForNormalUser(th.teamAdminUser().getId());
    client.createUserAccessToken(th.basicUser().getId(), th.basicUser().getId()).readEntity();
    th.logout().loginBasic2();
    UserAccessToken user2TokenA =
        client.createUserAccessToken(th.basicUser2().getId(), th.basicUser2().getId()).readEntity();
    UserAccessToken user2TokenB =
        client.createUserAccessToken(th.basicUser2().getId(), th.basicUser2().getId()).readEntity();
    th.logout().loginSystemAdmin();

    String term = th.basicUser2().getUsername();

    UserAccessTokenList foundTokens = assertNoError(client.searchTokens(term)).readEntity();

    assertThat(foundTokens.stream().map(UserAccessToken::getId).collect(Collectors.toSet()),
        containsInAnyOrder(user2TokenA.getId(), user2TokenB.getId()));
  }

  @Test
  void revokeAllActiveSessionForUser() {
    User targetUser = th.basicUser2();
    try (MattermostClient theUserClient = createClient()) {
      theUserClient.login(targetUser.getUsername(), targetUser.getPassword());
      assertNoError(theUserClient.getUser("me"));

      th.logout().loginSystemAdmin();
      ApiResponse<Boolean> revokeResult =
          assertNoError(client.revokeAllActiveSessionForUser(targetUser.getId()));
      assertTrue(revokeResult.readEntity());

      assertStatus(theUserClient.getUser("me"), Status.UNAUTHORIZED);
    }
  }

}
