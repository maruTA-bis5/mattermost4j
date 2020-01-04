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
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelList;
import net.bis5.mattermost.model.ChannelMember;
import net.bis5.mattermost.model.ChannelMembers;
import net.bis5.mattermost.model.ChannelPatch;
import net.bis5.mattermost.model.ChannelStats;
import net.bis5.mattermost.model.ChannelType;
import net.bis5.mattermost.model.ChannelUnread;
import net.bis5.mattermost.model.ChannelView;
import net.bis5.mattermost.model.ChannelViewResponse;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.User;

@ExtendWith(MattermostClientTestExtension.class)
class ChannelsApiTest implements MattermostClientTest {

  private TestHelper th;
  private MattermostClient client;

  @Override
  public void setHelper(TestHelper helper) {
    this.th = helper;
  }

  @BeforeEach
  public void setup() {
    client = createClient();
    th.changeClient(client).initBasic();
  }

  @AfterEach
  public void tearDown() {
    th.logout();
    client.close();
  }

  @Test
  public void createChannel_Open_Required() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
    createChannel_Success(channel);
  }

  @Test
  public void createChannel_Open_All() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
    channel.setPurpose("purpose");
    channel.setHeader("header");
    createChannel_Success(channel);
  }

  @Test
  public void createChannel_Private_Required() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
    createChannel_Success(channel);
  }

  @Test
  public void createChannel_Private_All() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
    channel.setPurpose("purpose");
    channel.setHeader("header");
    createChannel_Success(channel);
  }

  @Test
  public void createChannel_Fail_Direct() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Direct, th.basicTeam().getId());

    assertStatus(client.createChannel(channel), Status.BAD_REQUEST);
  }

  private void createChannel_Success(Channel channel) {
    String teamId = channel.getTeamId();
    String name = channel.getName();
    String displayName = channel.getDisplayName();
    ChannelType type = channel.getType();
    String purpose = channel.getPurpose();
    String header = channel.getHeader();

    ApiResponse<Channel> response = assertNoError(client.createChannel(channel));
    channel = response.readEntity();

    assertThat(channel.getTeamId(), is(teamId));
    assertThat(channel.getName(), is(name));
    assertThat(channel.getDisplayName(), is(displayName));
    assertThat(channel.getType(), is(type));
    // optional properties
    assertThat(channel.getPurpose(), purpose == null ? isEmptyOrNullString() : is(purpose));
    assertThat(channel.getHeader(), header == null ? isEmptyOrNullString() : is(header));
  }

  @Test
  public void createDirectChannel() {
    User user1 = th.basicUser();
    User user2 = th.basicUser2();

    ApiResponse<Channel> response = assertNoError(client.createDirectChannel(user1.getId(), user2.getId()));
    Channel channel = response.readEntity();

    assertThat(channel, is(notNullValue()));
  }

  @Test
  public void createDirectChannel_OneUser() {
    assertStatus(client.createDirectChannel(th.basicUser().getId(), null), Status.BAD_REQUEST);
  }

  @Test
  public void createGroupChannel() {
    User user1 = th.basicUser();
    User user2 = th.basicUser2();
    User user3 = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user3, th.basicTeam()).loginBasic();

    ApiResponse<Channel> response = assertNoError(
        client.createGroupChannel(user1.getId(), user2.getId(), user3.getId()));
    Channel channel = response.readEntity();

    assertThat(channel, is(notNullValue()));
  }

  @Test
  public void createGroupChannel_Fail_TwoUsers() {
    User user1 = th.basicUser();
    User user2 = th.basicUser2();

    assertStatus(client.createGroupChannel(user1.getId(), user2.getId()), Status.BAD_REQUEST);
  }

  @Test
  public void channelListByTeamId() {
    Team theTeam = th.loginSystemAdmin().createTeam();
    User theUser = th.createUser();
    th.linkUserToTeam(theUser, theTeam);
    Channel channel1 = new Channel("displayname1", "name1", ChannelType.Open, theTeam.getId());
    Channel channel2 = new Channel("displayname2", "name2", ChannelType.Open, theTeam.getId());
    channel1 = client.createChannel(channel1).readEntity();
    channel2 = client.createChannel(channel2).readEntity();
    th.loginAs(theUser);

    ApiResponse<ChannelList> response = assertNoError(
        client.getPublicChannelsByIdsForTeam(theTeam.getId(), channel1.getId(), channel2.getId()));
    ChannelList channels = response.readEntity();

    List<String> ids = channels.stream().map(Channel::getId).collect(Collectors.toList());
    assertThat(ids.size(), is(2));
    assertThat(ids, containsInAnyOrder(channel1.getId(), channel2.getId()));
  }

  @Test
  public void getAChannel() {
    String channelId = th.basicChannel().getId();

    ApiResponse<Channel> response = assertNoError(client.getChannel(channelId, null));
    Channel channel = response.readEntity();

    assertThat(channel.getId(), is(channelId));
  }

  @Test
  public void updateChannel() {
    String channelId = th.basicChannel().getId();
    String newName = "new-channel-name";
    String newDispName = "New Display Name";
    String newPurpose = "New Purpose";
    String newHeader = "New Header";
    Channel newChannel = new Channel();
    newChannel.setId(channelId);
    newChannel.setName(newName);
    newChannel.setDisplayName(newDispName);
    newChannel.setPurpose(newPurpose);
    newChannel.setHeader(newHeader);

    ApiResponse<Channel> response = assertNoError(client.updateChannel(newChannel));
    newChannel = response.readEntity();

    assertThat(newChannel.getName(), is(newName));
    assertThat(newChannel.getDisplayName(), is(newDispName));
    assertThat(newChannel.getPurpose(), is(newPurpose));
    assertThat(newChannel.getHeader(), is(newHeader));
  }

  @Test
  public void updateChannel_ChannelNotFound() {
    String channelId = th.newId();
    String newName = "new-channel-name";
    String newDispName = "New Display Name";
    String newPurpose = "New Purpose";
    String newHeader = "New Header";
    Channel newChannel = new Channel();
    newChannel.setId(channelId);
    newChannel.setName(newName);
    newChannel.setDisplayName(newDispName);
    newChannel.setPurpose(newPurpose);
    newChannel.setHeader(newHeader);

    assertStatus(client.updateChannel(newChannel), Status.NOT_FOUND);
  }

  @Test
  @Disabled
  // Since 5.18.0, 5.17.2, 5.16.4, 5.15.4 and 5.9.7, removed ability to change
  // type via
  // updateChannel
  public void updateChannel_ChangeType() {
    String channelId = th.basicChannel().getId();
    assertThat(th.basicChannel().getType(), is(ChannelType.Open));
    ChannelType newType = ChannelType.Private;
    Channel newChannel = new Channel();
    newChannel.setId(channelId);
    newChannel.setType(newType);

    ApiResponse<Channel> response = assertNoError(client.updateChannel(newChannel));
    newChannel = response.readEntity();

    assertThat(newChannel.getType(), is(newType));
  }

  @Test
  public void deleteChannel() {
    String channelId = th.basicChannel().getId();

    ApiResponse<Boolean> deleteResponse = assertNoError(client.deleteChannel(channelId));
    boolean deleteResult = deleteResponse.readEntity().booleanValue();

    assertThat(deleteResult, is(true));
    ApiResponse<Channel> response = assertNoError(client.getChannel(channelId, null));
    assertThat(response.readEntity().getDeleteAt(), is(greaterThan(0l)));
  }

  @Test
  public void patchChannel() {
    String channelId = th.basicChannel().getId();
    String newDispName = "new Display name";
    ChannelPatch patch = new ChannelPatch();
    patch.setDisplayName(newDispName);

    ApiResponse<Channel> response = assertNoError(client.patchChannel(channelId, patch));
    Channel newChannel = response.readEntity();

    assertThat(newChannel.getDisplayName(), is(newDispName));
  }

  @Test
  public void patchChannel_ChannelNotFound() {
    String channelId = th.newId();
    String newDispName = "new Display name";
    ChannelPatch patch = new ChannelPatch();
    patch.setDisplayName(newDispName);

    assertStatus(client.patchChannel(channelId, patch), Status.NOT_FOUND);
  }

  @Test
  public void restoreChannel() {
    Channel channel = th.createPublicChannel();
    ApiResponse<Boolean> deleteResult = assertNoError(client.deleteChannel(channel.getId()));
    assertTrue(deleteResult.readEntity());

    th.logout().loginTeamAdmin();
    ApiResponse<Channel> restoreResult = assertNoError(client.restoreChannel(channel.getId()));
    Channel restoredChannel = restoreResult.readEntity();
    assertEquals(restoredChannel.getId(), channel.getId());

    // in prior of Mattermost 5.5, archive/unarchive operations did not flush
    // server-side cache.
    {
      th.logout().loginSystemAdmin();
      assertNoError(client.invalidateCaches());
      th.logout().loginTeamAdmin();
    }

    restoredChannel = client.getChannel(channel.getId()).readEntity();
    assertThat(restoredChannel.getDeleteAt(), is(0l));
  }

  @Test
  public void getChannelStatistics() {
    String channelId = th.basicChannel().getId();

    ApiResponse<ChannelStats> response = assertNoError(client.getChannelStats(channelId, null));
    ChannelStats stats = response.readEntity();

    assertThat(stats.getChannelId(), is(channelId));
  }

  @Test
  public void getChannelPinnedPosts() {
    String channelId = th.basicChannel().getId();
    Post pinned = th.createPinnedPost(channelId);

    ApiResponse<PostList> response = assertNoError(client.getPinnedPosts(channelId, null));
    PostList posts = response.readEntity();

    assertThat(posts.size(), is(1));
    assertThat(posts.getPosts().get(pinned.getId()), is(notNullValue()));
  }

  @Test
  public void getChannelByName() {
    String channelName = th.basicChannel().getName();

    ApiResponse<Channel> response = assertNoError(client.getChannelByName(channelName, th.basicTeam().getId(), null));
    Channel channel = response.readEntity();

    assertThat(channel.getId(), is(th.basicChannel().getId()));
  }

  @Test
  public void getChannelByName_ChannelNotFound() {
    String channelName = "fake-channel-name";

    assertStatus(client.getChannelByName(channelName, th.basicTeam().getId(), null), Status.NOT_FOUND);
  }

  @Test
  public void getChannelByNameAndTeamName() {
    String channelName = th.basicChannel().getName();
    String teamName = th.basicTeam().getName();

    ApiResponse<Channel> response = assertNoError(client.getChannelByNameForTeamName(channelName, teamName, null));
    Channel channel = response.readEntity();

    assertThat(channel.getId(), is(th.basicChannel().getId()));
  }

  @Test
  public void getChannelByNameAndTeamName_ChannelNotFound() {
    String channelName = "fake-channel-name";
    String teamName = th.basicTeam().getName();

    assertStatus(client.getChannelByNameForTeamName(channelName, teamName, null), Status.NOT_FOUND);
  }

  @Test
  public void getChannelByNameAndTeamName_TeamNotFound() {
    String channelName = "fake-channel-name";
    String teamName = "fake-team-name";

    assertStatus(client.getChannelByNameForTeamName(channelName, teamName, null), Status.NOT_FOUND);
  }

  @Test
  public void getChannelMembers() {
    User user1 = th.createUser();
    User user2 = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam()).linkUserToTeam(user2, th.basicTeam()).loginAs(user1);
    Channel channel = th.createPublicChannel();
    client.addChannelMember(channel.getId(), user1.getId());
    client.addChannelMember(channel.getId(), user2.getId());

    ApiResponse<ChannelMembers> response = assertNoError(
        client.getChannelMembers(channel.getId(), Pager.of(0, 60), null));
    ChannelMembers members = response.readEntity();

    assertThat(members.size(), is(2));
    assertThat(members.stream().map(m -> m.getUserId()).collect(Collectors.toSet()),
        containsInAnyOrder(user1.getId(), user2.getId()));
  }

  @Test
  public void addUserToChannel() {
    Channel channel = th.basicChannel();
    User user = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginBasic();

    ApiResponse<ChannelMember> response = assertNoError(client.addChannelMember(channel.getId(), user.getId()));
    ChannelMember member = response.readEntity();

    assertThat(member.getChannelId(), is(channel.getId()));
    assertThat(member.getUserId(), is(user.getId()));
  }

  @Test
  public void getChannelMembersByIds() {
    Channel channel = th.createPublicChannel();
    User user1 = th.createUser();
    User user2 = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam()).linkUserToTeam(user2, th.basicTeam()).loginBasic();
    client.addChannelMember(channel.getId(), user1.getId());
    client.addChannelMember(channel.getId(), user2.getId());

    ApiResponse<ChannelMembers> response = assertNoError(
        client.getChannelMembersByIds(channel.getId(), user1.getId(), user2.getId()));
    ChannelMembers members = response.readEntity();

    assertThat(members.size(), is(2));
    assertThat(members.stream().map(m -> m.getUserId()).collect(Collectors.toSet()),
        containsInAnyOrder(user1.getId(), user2.getId()));
  }

  @Test
  public void getChannelMember() {
    Channel channel = th.basicChannel();
    User user = th.basicUser();

    ApiResponse<ChannelMember> response = assertNoError(client.getChannelMember(channel.getId(), user.getId(), null));
    ChannelMember member = response.readEntity();

    assertThat(member.getChannelId(), is(channel.getId()));
    assertThat(member.getUserId(), is(user.getId()));
  }

  @Test
  public void removeUserFromChannel() {
    Channel channel = th.basicChannel();
    User user = th.basicUser2();

    // logged-in as basicUser

    ApiResponse<Boolean> response = assertNoError(client.removeUserFromChannel(channel.getId(), user.getId()));
    boolean result = response.readEntity().booleanValue();

    assertThat(result, is(true));
  }

  @Test
  public void updateChannelRoles() {
    User channelAdmin = th.basicUser();
    User channelUser = th.basicUser2();
    Channel channel = th.loginAs(channelAdmin).createPublicChannel();
    client.addChannelMember(channel.getId(), channelUser.getId());

    ApiResponse<Boolean> response = assertNoError(
        client.updateChannelRoles(channel.getId(), channelUser.getId(), Role.CHANNEL_ADMIN, Role.CHANNEL_USER));
    boolean result = response.readEntity().booleanValue();

    assertThat(result, is(true));
  }

  @Test
  @Disabled
  public void updateChannelNotifications() {
    // TODO props定数を作る
  }

  @Test
  public void viewChannel() {
    User user = th.basicUser();
    Channel channel = th.basicChannel2();
    ChannelView view = new ChannelView(channel.getId());

    ApiResponse<ChannelViewResponse> response = assertNoError(client.viewChannel(user.getId(), view));
    boolean result = response.readEntity().isOk();

    assertThat(result, is(true));
  }

  @Test
  public void getChannelMembersForUser() {
    User user = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginAs(user);
    Channel channel1 = th.createPublicChannel();
    Channel channel2 = th.createPublicChannel();
    client.addChannelMember(channel1.getId(), user.getId());
    client.addChannelMember(channel2.getId(), user.getId());

    ApiResponse<ChannelMembers> response = assertNoError(
        client.getChannelMembersForUser(user.getId(), th.basicTeam().getId(), null));
    ChannelMembers members = response.readEntity();

    assertThat(members.stream().map(m -> m.getChannelId()).collect(Collectors.toSet()),
        hasItems(channel1.getId(), channel2.getId()));
  }

  @Test
  public void getChannelsForUser() {
    User user = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginAs(user);
    Channel channel1 = th.createPublicChannel();
    Channel channel2 = th.createPublicChannel();
    client.addChannelMember(channel1.getId(), user.getId());
    client.addChannelMember(channel2.getId(), user.getId());

    ApiResponse<ChannelList> response = assertNoError(
        client.getChannelsForTeamForUser(th.basicTeam().getId(), user.getId(), null));
    ChannelList channels = response.readEntity();

    assertThat(channels.stream().map(c -> c.getId()).collect(Collectors.toSet()),
        hasItems(channel1.getId(), channel2.getId()));
  }

  @Test
  public void getUnreadMessages() {
    User user = th.basicUser();
    Channel channel = th.basicChannel();

    ApiResponse<ChannelUnread> response = assertNoError(client.getChannelUnread(channel.getId(), user.getId()));
    ChannelUnread unread = response.readEntity();

    assertThat(unread.getChannelId(), is(channel.getId()));
  }

  /**
   * Test case for issue#99
   */
  @Test
  public void getChannelHasChannelMentionsProp() {
    Channel channelPublic1 = new Channel("Public 1", "public1", ChannelType.Open, th.basicTeam().getId());
    ApiResponse<Channel> createChannelResponse = client.createChannel(channelPublic1);
    if (isNotSupportVersion("5.1.0", createChannelResponse)) {
      // skip
      return;
    }
    channelPublic1 = assertNoError(createChannelResponse).readEntity();

    Channel channelPublic2 = new Channel("Public 2", "public2", ChannelType.Open, th.basicTeam().getId());
    channelPublic2 = assertNoError(client.createChannel(channelPublic2)).readEntity();

    Channel channelPrivate = new Channel("Private", "private", ChannelType.Private, th.basicTeam().getId());
    channelPrivate = assertNoError(client.createChannel(channelPrivate)).readEntity();

    Team otherTeam = th.createTeam();
    Channel channelOtherTeam = new Channel("Other Team Channel", "other-team", ChannelType.Open, otherTeam.getId());
    channelOtherTeam = assertNoError(client.createChannel(channelOtherTeam)).readEntity();

    Channel channelNoReference = th.createPublicChannel();
    channelNoReference.setHeader("No references");
    channelNoReference.setPurpose("No references");
    assertNoError(client.updateChannel(channelNoReference));
    channelNoReference = assertNoError(client.getChannel(channelNoReference.getId())).readEntity();
    assertThat(channelNoReference.getProps(), is(nullValue()));

    Channel channelOnBasicTeam = th.createPublicChannel();
    channelOnBasicTeam.setHeader("~public1, ~private, ~other-team");
    channelOnBasicTeam.setPurpose("~public2, ~private, ~other-team");
    assertNoError(client.updateChannel(channelOnBasicTeam));
    channelOnBasicTeam = assertNoError(client.getChannel(channelOnBasicTeam.getId())).readEntity();
    assertThat(channelOnBasicTeam.getProps().containsKey("channel_mentions"), is(true));
    @SuppressWarnings("unchecked")
    Map<String, Map<String, String>> channelMentions = (Map<String, Map<String, String>>) channelOnBasicTeam.getProps()
        .get("channel_mentions");
    assertThat(channelMentions.containsKey("public1"), is(true));
    assertThat(channelMentions.get("public1").get("display_name"), is(channelPublic1.getDisplayName()));

  }

  @Test
  public void getDeletedChannels() {
    Channel deleted1 = th.createPublicChannel();
    Channel deleted2 = th.createPublicChannel();
    client.deleteChannel(deleted1.getId());
    client.deleteChannel(deleted2.getId());

    th.logout().loginTeamAdmin();
    ChannelList channels = assertNoError(client.getDeletedChannels(th.basicTeam().getId())).readEntity();
    Set<String> channelIds = channels.stream().map(Channel::getId).collect(Collectors.toSet());

    assertThat(channelIds, containsInAnyOrder(deleted1.getId(), deleted2.getId()));
  }

  @Test
  public void convertChannelToPrivate() {
    Channel publicChannel = th.createPublicChannel();
    String channelId = publicChannel.getId();

    th.logout().loginTeamAdmin();
    Channel privateChannel = assertNoError(client.convertChannelToPrivate(channelId)).readEntity();

    assertEquals(channelId, privateChannel.getId());
    assertEquals(ChannelType.Private, privateChannel.getType());
  }

}
