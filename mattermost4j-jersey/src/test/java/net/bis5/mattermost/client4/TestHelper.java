/*
 * Copyright (c) 2017-present, Takayuki Maruyama
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
package net.bis5.mattermost.client4;

import static net.bis5.mattermost.client4.Assertions.checkNoError;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelType;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamType;
import net.bis5.mattermost.model.User;

/**
 * Mattermost API Call Test Helper
 * 
 * @author Takayuki Maruyama
 */
@Accessors(chain = true, fluent = true)
@Getter
public class TestHelper {

  private MattermostClient client;

  public TestHelper(MattermostClient client) {
    this.client = client;
  }

  public TestHelper changeClient(MattermostClient client) {
    this.client = client;
    return this;
  }

  private User systemAdminUser;
  private User teamAdminUser;
  private Team basicTeam;
  private Channel basicChannel;
  private Channel basicPrivateChannel;
  private Channel basicChannel2;
  private Post basicPost;
  private User basicUser;
  private User basicUser2;

  /**
   * The password compatible default (5.14+) requirements (min length:10,
   * required: 1 uppercase, 1 lowercase, 1 number and 1 symbol).
   */
  public static final String DEFAULT_PASSWORD = "The_Passw0rd";

  public static final String EMOJI_GLOBE = "/noto-emoji_u1f310.png";
  public static final String EMOJI_CONSTRUCTION = "/noto-emoji_u1f6a7.png";

  public Path getResourcePath(String name) throws URISyntaxException {
    return Paths.get(getClass().getResource(name).toURI());
  }

  public TestHelper setup() {
    initSystemAdmin();
    Config config = client.getConfig().readEntity();
    config.getTeamSettings().setMaxUsersPerTeam(50);
    config.getRateLimitSettings().setEnable(false);
    config.getServiceSettings().setEnableCustomEmoji(true);
    config.getServiceSettings().setEnableOnlyAdminIntegrations(false);
    config.getServiceSettings().setEnableIncomingWebhooks(true);
    config.getServiceSettings().setEnableOutgoingWebhooks(true);
    config.getServiceSettings().setEnableCommands(true);
    config.getServiceSettings().setEnableEmailInvitations(true);
    config.getServiceSettings().setEnableUserAccessTokens(true);
    config.getServiceSettings().setEnableLinkPreviews(true);
    config.getServiceSettings().setEnablePostIconOverride(true);
    config.getFileSettings().setEnablePublicLink(true);
    config.getTeamSettings().setEnableOpenServer(true);
    config = checkNoError(client.updateConfig(config)).readEntity();
    client.logout();
    return this;
  }

  public TestHelper initBasic() {
    teamAdminUser = createUser();
    loginTeamAdmin();
    basicTeam = createTeam();
    basicChannel = createPublicChannel();
    basicPrivateChannel = createPrivateChannel();
    basicChannel2 = createPublicChannel();
    basicPost = createPost(basicChannel);
    basicUser = createUser();
    linkUserToTeam(basicUser, basicTeam);
    basicUser2 = createUser();
    linkUserToTeam(basicUser2, basicTeam);
    // TODO app.~
    checkNoError(client.addChannelMember(basicChannel.getId(), basicUser.getId()));
    checkNoError(client.addChannelMember(basicChannel.getId(), basicUser2.getId()));
    checkNoError(client.addChannelMember(basicChannel2.getId(), basicUser.getId()));
    checkNoError(client.addChannelMember(basicChannel2.getId(), basicUser2.getId()));
    checkNoError(client.addChannelMember(basicPrivateChannel.getId(), basicUser.getId()));
    checkNoError(client.addChannelMember(basicPrivateChannel.getId(), basicUser2.getId()));

    // linkUserToTeam(systemAdminUser, basicTeam);
    loginBasic();
    return this;
  }

  public TestHelper initSystemAdmin() {
    if (systemAdminUser != null) {
      loginAs(systemAdminUser);
      return this;
    }
    systemAdminUser = createSystemAdminUser();
    loginAs(systemAdminUser);
    return this;
  }

  public String newId() {
    return newRandomString(26);
  }

  public User createSystemAdminUser() {
    String id = newId();

    User user = new User();
    user.setEmail(generateTestEmail());
    user.setUsername(generateTestUsername());
    user.setNickname("nn_" + id);
    user.setFirstName("f_" + id);
    user.setLastName("l_" + id);
    user.setPassword(DEFAULT_PASSWORD);
    user.setRoles(Arrays.asList(Role.SYSTEM_ADMIN, Role.SYSTEM_USER).stream().map(Role::getRoleName)
        .collect(Collectors.joining(" ")));

    user = checkNoError(client.createUser(user)).readEntity();
    user.setPassword(DEFAULT_PASSWORD);
    return user;
  }

  public User createUser(String username) {
    String id = newId();

    User user = new User();
    user.setEmail(generateTestEmail());
    user.setUsername(username);
    user.setNickname("nn_" + id);
    user.setFirstName("f_" + id);
    user.setLastName("l_" + id);
    user.setPassword(DEFAULT_PASSWORD);

    user = checkNoError(client.createUser(user)).readEntity();
    user.setPassword(DEFAULT_PASSWORD);
    return user;
  }

  public User createUser() {
    return createUser(generateTestUsername());
  }

  public Team createTeam() {
    String id = newId();

    Team team = new Team();
    team.setDisplayName("dn_" + id);
    team.setName(generateTestTeamName());
    team.setDescription("desc_" + id);
    team.setType(TeamType.OPEN);
    team.setAllowOpenInvite(true);

    team = checkNoError(client.createTeam(team)).readEntity();
    return team;
  }

  public Channel createPublicChannel() {
    return createChannel(ChannelType.Open);
  }

  public Channel createPrivateChannel() {
    return createChannel(ChannelType.Private);
  }

  protected Channel createChannel(ChannelType type) {
    String id = newId();

    Channel channel = new Channel();
    channel.setDisplayName("dn_" + id);
    channel.setName(generateTestChannelName());
    channel.setType(type);
    channel.setTeamId(basicTeam.getId());

    channel = checkNoError(client.createChannel(channel)).readEntity();
    return channel;
  }

  public Post createPost(Channel channel) {
    String id = newId();

    Post post = new Post();
    post.setChannelId(channel.getId());
    post.setMessage("message_" + id);

    post = checkNoError(client.createPost(post)).readEntity();
    return post;
  }

  public Post createPinnedPost(String channelId) {
    String id = newId();

    Post post = new Post();
    post.setChannelId(channelId);
    post.setMessage("message_" + id);
    post.setPinned(true);

    post = checkNoError(client.createPost(post)).readEntity();
    return post;
  }

  public String generateTestEmail() {
    return "success+" + newId() + "@inbucket.local".toLowerCase();
  }

  public String generateTestUsername() {
    return "fakeuser" + newRandomString(10);
  }

  public String generateTestTeamName() {
    return "faketeam" + newRandomString(10);
  }

  public String generateTestChannelName() {
    return "fakechannel" + newRandomString(10);
  }

  public String newRandomString(int length) {
    return RandomStringUtils.randomAlphanumeric(length).toLowerCase();
  }

  public TestHelper loginTeamAdmin() {
    loginAs(teamAdminUser);
    return this;
  }

  public TestHelper loginBasic() {
    loginAs(basicUser);
    return this;
  }

  public TestHelper loginBasic2() {
    loginAs(basicUser2);
    return this;
  }

  public TestHelper loginSystemAdmin() {
    loginAs(systemAdminUser);
    return this;
  }

  public TestHelper logout() {
    client.logout();
    return this;
  }

  public TestHelper loginAs(User user) {
    client.login(user.getEmail(), user.getPassword());
    return this;
  }

  public TestHelper linkUserToTeam(User user, Team team) {
    checkNoError(client.addTeamMembers(team.getId(), user.getId()));
    return this;
  }

  public TestHelper updateUserRoles(String userId, Role... roles) {
    checkNoError(client.updateUserRoles(userId, roles));
    return this;
  }

  public TestHelper updateUserToNonTeamAdmin(User user, Team team) {
    checkNoError(client.updateTeamMemberRoles(team.getId(), user.getId(), Role.TEAM_USER));
    return this;
  }
}
