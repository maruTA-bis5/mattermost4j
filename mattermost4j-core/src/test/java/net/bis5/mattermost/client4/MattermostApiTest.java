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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import com.vdurmont.semver4j.Semver;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.bis5.mattermost.client4.hook.IncomingWebhookClient;
import net.bis5.mattermost.model.Audit;
import net.bis5.mattermost.model.Audits;
import net.bis5.mattermost.model.AuthService;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelList;
import net.bis5.mattermost.model.ChannelMember;
import net.bis5.mattermost.model.ChannelMembers;
import net.bis5.mattermost.model.ChannelPatch;
import net.bis5.mattermost.model.ChannelSearch;
import net.bis5.mattermost.model.ChannelStats;
import net.bis5.mattermost.model.ChannelType;
import net.bis5.mattermost.model.ChannelUnread;
import net.bis5.mattermost.model.ChannelView;
import net.bis5.mattermost.model.ChannelViewResponse;
import net.bis5.mattermost.model.Command;
import net.bis5.mattermost.model.CommandList;
import net.bis5.mattermost.model.CommandMethod;
import net.bis5.mattermost.model.CommandResponse;
import net.bis5.mattermost.model.CommandResponseType;
import net.bis5.mattermost.model.ContentType;
import net.bis5.mattermost.model.Emoji;
import net.bis5.mattermost.model.EmojiList;
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookList;
import net.bis5.mattermost.model.IncomingWebhookRequest;
import net.bis5.mattermost.model.OutgoingWebhook;
import net.bis5.mattermost.model.OutgoingWebhookList;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Session;
import net.bis5.mattermost.model.SessionList;
import net.bis5.mattermost.model.SwitchRequest;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamExists;
import net.bis5.mattermost.model.TeamList;
import net.bis5.mattermost.model.TeamMember;
import net.bis5.mattermost.model.TeamMemberList;
import net.bis5.mattermost.model.TeamPatch;
import net.bis5.mattermost.model.TeamSearch;
import net.bis5.mattermost.model.TeamStats;
import net.bis5.mattermost.model.TeamType;
import net.bis5.mattermost.model.TeamUnread;
import net.bis5.mattermost.model.TeamUnreadList;
import net.bis5.mattermost.model.TriggerWhen;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;
import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Mattermost API call test
 * 
 * @author Takayuki Maruyama
 */
public class MattermostApiTest {

  private static final String APPLICATION = getApplicationUrl();
  private MattermostClient client;
  private static TestHelper th;

  private static String getApplicationUrl() {
    String url = System.getenv("MATTERMOST_URL");
    return url != null ? url : "http://localhost:8065";
  }

  @BeforeClass
  public static void initHelper() {
    th = new TestHelper(new MattermostClient(APPLICATION)).setup();
  }

  @Before
  public void setup() {
    client = new MattermostClient(APPLICATION, Level.WARNING);
    th.changeClient(client).initBasic();
  }

  @After
  public void tearDown() throws Exception {
    th.logout();
    client.close();
  }

  private <T> ApiResponse<T> assertNoError(ApiResponse<T> response) {
    return th.checkNoError(response);
  }

  private <T> ApiResponse<T> assertStatus(ApiResponse<T> response, Status status) {
    Response rawResponse = response.getRawResponse();

    assertThat(rawResponse.getStatus(), is(status.getStatusCode()));

    return response;
  }

  public static class HasError<T extends ApiResponse<U>, U> extends BaseMatcher<T> {

    private ApiResponse<U> actual;

    /**
     * @see org.hamcrest.Matcher#matches(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object actual) {
      if (actual instanceof ApiResponse) {
        this.actual = (ApiResponse<U>) actual;
        return this.actual.hasError();
      }
      return false;
    }

    /**
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    @Override
    public void describeTo(Description description) {
      description.appendText("Should have failed");
    }

    public static <U> Matcher<? extends ApiResponse<U>> hasError() {
      return new HasError<>();
    }

  }

  // Channels

  @Test
  public void testChannels_CreateChannel_Open_Required() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
    testChannels_CreateChannel_Success(channel);
  }

  @Test
  public void testChannels_CreateChannel_Open_All() {
    Channel channel = new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
    channel.setPurpose("purpose");
    channel.setHeader("header");
    testChannels_CreateChannel_Success(channel);
  }

  @Test
  public void testChannels_CreateChannel_Private_Required() {
    Channel channel =
        new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
    testChannels_CreateChannel_Success(channel);
  }

  @Test
  public void testChannels_CreateChannel_Private_All() {
    Channel channel =
        new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
    channel.setPurpose("purpose");
    channel.setHeader("header");
    testChannels_CreateChannel_Success(channel);
  }

  @Test
  public void testChannels_CreateChannel_Fail_Direct() {
    Channel channel =
        new Channel("DisplayName", "name", ChannelType.Direct, th.basicTeam().getId());

    assertStatus(client.createChannel(channel), Status.BAD_REQUEST);
  }

  private void testChannels_CreateChannel_Success(Channel channel) {
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
  public void testChannels_CreateDirectChannel() {
    User user1 = th.basicUser();
    User user2 = th.basicUser2();

    ApiResponse<Channel> response =
        assertNoError(client.createDirectChannel(user1.getId(), user2.getId()));
    Channel channel = response.readEntity();

    assertThat(channel, is(notNullValue()));
  }

  @Test
  public void testChannels_CreateDirectChannel_OneUser() {
    assertStatus(client.createDirectChannel(th.basicUser().getId(), null), Status.BAD_REQUEST);
  }

  @Test
  public void testChannels_CreateGroupChannel() {
    User user1 = th.basicUser();
    User user2 = th.basicUser2();
    User user3 = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user3, th.basicTeam()).loginBasic();

    ApiResponse<Channel> response =
        assertNoError(client.createGroupChannel(user1.getId(), user2.getId(), user3.getId()));
    Channel channel = response.readEntity();

    assertThat(channel, is(notNullValue()));
  }

  @Test
  public void testChannels_CreateGroupChannel_Fail_TwoUsers() {
    User user1 = th.basicUser();
    User user2 = th.basicUser2();

    assertStatus(client.createGroupChannel(user1.getId(), user2.getId()), Status.BAD_REQUEST);
  }

  @Test
  public void testChannels_ChannelListByTeamId() {
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
  public void testChannels_GetAChannel() {
    String channelId = th.basicChannel().getId();

    ApiResponse<Channel> response = assertNoError(client.getChannel(channelId, null));
    Channel channel = response.readEntity();

    assertThat(channel.getId(), is(channelId));
  }

  @Test
  public void testChannels_UpdateChannel() {
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
  public void testChannels_UpdateChannel_ChannelNotFound() {
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
  public void testChannels_UpdateChannel_ChangeType() {
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
  public void testChannels_DeleteChannel() {
    String channelId = th.basicChannel().getId();

    ApiResponse<Boolean> deleteResponse = assertNoError(client.deleteChannel(channelId));
    boolean deleteResult = deleteResponse.readEntity().booleanValue();

    assertThat(deleteResult, is(true));
    ApiResponse<Channel> response = assertNoError(client.getChannel(channelId, null));
    assertThat(response.readEntity().getDeleteAt(), is(greaterThan(0l)));
  }

  @Test
  public void testChannels_PatchChannel() {
    String channelId = th.basicChannel().getId();
    String newDispName = "new Display name";
    ChannelPatch patch = new ChannelPatch();
    patch.setDisplayName(newDispName);

    ApiResponse<Channel> response = assertNoError(client.patchChannel(channelId, patch));
    Channel newChannel = response.readEntity();

    assertThat(newChannel.getDisplayName(), is(newDispName));
  }

  @Test
  public void testChannels_PatchChannel_ChannelNotFound() {
    String channelId = th.newId();
    String newDispName = "new Display name";
    ChannelPatch patch = new ChannelPatch();
    patch.setDisplayName(newDispName);

    assertStatus(client.patchChannel(channelId, patch), Status.NOT_FOUND);
  }

  @Test
  public void testChannels_RestoreChannel() {
    Channel channel = th.createPublicChannel();
    ApiResponse<Boolean> deleteResult = assertNoError(client.deleteChannel(channel.getId()));
    assertTrue(deleteResult.readEntity());

    th.logout().loginTeamAdmin();
    ApiResponse<Channel> restoreResult = assertNoError(client.restoreChannel(channel.getId()));
    Channel restoredChannel = restoreResult.readEntity();
    assertEquals(restoredChannel.getId(), channel.getId());

    // in prior of Mattermost 5.5, archive/unarchive operations did not flush server-side cache.
    {
      th.logout().loginSystemAdmin();
      assertNoError(client.invalidateCaches());
      th.logout().loginTeamAdmin();
    }

    restoredChannel = client.getChannel(channel.getId()).readEntity();
    assertThat(restoredChannel.getDeleteAt(), is(0l));
  }

  @Test
  public void testChannels_GetChannelStatistics() {
    String channelId = th.basicChannel().getId();

    ApiResponse<ChannelStats> response = assertNoError(client.getChannelStats(channelId, null));
    ChannelStats stats = response.readEntity();

    assertThat(stats.getChannelId(), is(channelId));
  }

  @Test
  public void testChannels_GetChannelPinnedPosts() {
    String channelId = th.basicChannel().getId();
    Post pinned = th.createPinnedPost(channelId);

    ApiResponse<PostList> response = assertNoError(client.getPinnedPosts(channelId, null));
    PostList posts = response.readEntity();

    assertThat(posts.size(), is(1));
    assertThat(posts.getPosts().get(pinned.getId()), is(notNullValue()));
  }

  @Test
  public void testChannels_GetChannelByName() {
    String channelName = th.basicChannel().getName();

    ApiResponse<Channel> response =
        assertNoError(client.getChannelByName(channelName, th.basicTeam().getId(), null));
    Channel channel = response.readEntity();

    assertThat(channel.getId(), is(th.basicChannel().getId()));
  }

  @Test
  public void testChannels_GetChannelByName_ChannelNotFound() {
    String channelName = "fake-channel-name";

    assertStatus(client.getChannelByName(channelName, th.basicTeam().getId(), null),
        Status.NOT_FOUND);
  }

  @Test
  public void testChannels_GetChannelByNameAndTeamName() {
    String channelName = th.basicChannel().getName();
    String teamName = th.basicTeam().getName();

    ApiResponse<Channel> response =
        assertNoError(client.getChannelByNameForTeamName(channelName, teamName, null));
    Channel channel = response.readEntity();

    assertThat(channel.getId(), is(th.basicChannel().getId()));
  }

  @Test
  public void testChannels_GetChannelByNameAndTeamName_ChannelNotFound() {
    String channelName = "fake-channel-name";
    String teamName = th.basicTeam().getName();

    assertStatus(client.getChannelByNameForTeamName(channelName, teamName, null), Status.NOT_FOUND);
  }

  @Test
  public void testChannels_GetChannelByNameAndTeamName_TeamNotFound() {
    String channelName = "fake-channel-name";
    String teamName = "fake-team-name";

    assertStatus(client.getChannelByNameForTeamName(channelName, teamName, null), Status.NOT_FOUND);
  }

  @Test
  public void testChannels_GetChannelMembers() {
    User user1 = th.createUser();
    User user2 = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam())
        .linkUserToTeam(user2, th.basicTeam()).loginAs(user1);
    Channel channel = th.createPublicChannel();
    client.addChannelMember(channel.getId(), user1.getId());
    client.addChannelMember(channel.getId(), user2.getId());

    ApiResponse<ChannelMembers> response =
        assertNoError(client.getChannelMembers(channel.getId(), Pager.of(0, 60), null));
    ChannelMembers members = response.readEntity();

    assertThat(members.size(), is(2));
    assertThat(members.stream().map(m -> m.getUserId()).collect(Collectors.toSet()),
        containsInAnyOrder(user1.getId(), user2.getId()));
  }

  @Test
  public void testChannels_AddUser() {
    Channel channel = th.basicChannel();
    User user = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginBasic();

    ApiResponse<ChannelMember> response =
        assertNoError(client.addChannelMember(channel.getId(), user.getId()));
    ChannelMember member = response.readEntity();

    assertThat(member.getChannelId(), is(channel.getId()));
    assertThat(member.getUserId(), is(user.getId()));
  }

  @Test
  public void testChannels_GetChannelMembersByIds() {
    Channel channel = th.createPublicChannel();
    User user1 = th.createUser();
    User user2 = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam())
        .linkUserToTeam(user2, th.basicTeam()).loginBasic();
    client.addChannelMember(channel.getId(), user1.getId());
    client.addChannelMember(channel.getId(), user2.getId());

    ApiResponse<ChannelMembers> response =
        assertNoError(client.getChannelMembersByIds(channel.getId(), user1.getId(), user2.getId()));
    ChannelMembers members = response.readEntity();

    assertThat(members.size(), is(2));
    assertThat(members.stream().map(m -> m.getUserId()).collect(Collectors.toSet()),
        containsInAnyOrder(user1.getId(), user2.getId()));
  }

  @Test
  public void testChannels_GetChannelMember() {
    Channel channel = th.basicChannel();
    User user = th.basicUser();

    ApiResponse<ChannelMember> response =
        assertNoError(client.getChannelMember(channel.getId(), user.getId(), null));
    ChannelMember member = response.readEntity();

    assertThat(member.getChannelId(), is(channel.getId()));
    assertThat(member.getUserId(), is(user.getId()));
  }

  @Test
  public void testChannels_RemoveUserFromChannel() {
    Channel channel = th.basicChannel();
    User user = th.basicUser2();

    // logged-in as basicUser

    ApiResponse<Boolean> response =
        assertNoError(client.removeUserFromChannel(channel.getId(), user.getId()));
    boolean result = response.readEntity().booleanValue();

    assertThat(result, is(true));
  }

  @Test
  public void testChannels_UpdateChannelRoles() {
    User channelAdmin = th.basicUser();
    User channelUser = th.basicUser2();
    Channel channel = th.loginAs(channelAdmin).createPublicChannel();
    client.addChannelMember(channel.getId(), channelUser.getId());

    ApiResponse<Boolean> response = assertNoError(client.updateChannelRoles(channel.getId(),
        channelUser.getId(), Role.ROLE_CHANNEL_ADMIN, Role.ROLE_CHANNEL_USER));
    boolean result = response.readEntity().booleanValue();

    assertThat(result, is(true));
  }

  @Test
  @Ignore
  public void testChannels_UpdateChannelNotifications() {
    // TODO props定数を作る
  }

  @Test
  public void testChannels_ViewChannel() {
    User user = th.basicUser();
    Channel channel = th.basicChannel2();
    ChannelView view = new ChannelView(channel.getId());

    ApiResponse<ChannelViewResponse> response =
        assertNoError(client.viewChannel(user.getId(), view));
    boolean result = response.readEntity().isOk();

    assertThat(result, is(true));
  }

  @Test
  public void testChannels_GetChannelMembersForUser() {
    User user = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginAs(user);
    Channel channel1 = th.createPublicChannel();
    Channel channel2 = th.createPublicChannel();
    client.addChannelMember(channel1.getId(), user.getId());
    client.addChannelMember(channel2.getId(), user.getId());

    ApiResponse<ChannelMembers> response =
        assertNoError(client.getChannelMembersForUser(user.getId(), th.basicTeam().getId(), null));
    ChannelMembers members = response.readEntity();

    assertThat(members.stream().map(m -> m.getChannelId()).collect(Collectors.toSet()),
        hasItems(channel1.getId(), channel2.getId()));
  }

  @Test
  public void testChannels_GetChannelsForUser() {
    User user = th.createUser();
    th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginAs(user);
    Channel channel1 = th.createPublicChannel();
    Channel channel2 = th.createPublicChannel();
    client.addChannelMember(channel1.getId(), user.getId());
    client.addChannelMember(channel2.getId(), user.getId());

    ApiResponse<ChannelList> response =
        assertNoError(client.getChannelsForTeamForUser(th.basicTeam().getId(), user.getId(), null));
    ChannelList channels = response.readEntity();

    assertThat(channels.stream().map(c -> c.getId()).collect(Collectors.toSet()),
        hasItems(channel1.getId(), channel2.getId()));
  }

  @Test
  public void testChannels_GetUnreadMessages() {
    User user = th.basicUser();
    Channel channel = th.basicChannel();

    ApiResponse<ChannelUnread> response =
        assertNoError(client.getChannelUnread(channel.getId(), user.getId()));
    ChannelUnread unread = response.readEntity();

    assertThat(unread.getChannelId(), is(channel.getId()));
  }

  /**
   * Test case for issue#99
   */
  @Test
  public void testChannels_GetChannelHasChannelMentionsProp() {
    Channel channelPublic1 =
        new Channel("Public 1", "public1", ChannelType.Open, th.basicTeam().getId());
    ApiResponse<Channel> createChannelResponse = client.createChannel(channelPublic1);
    if (isNotSupportVersion("5.1.0", createChannelResponse)) {
      // skip
      return;
    }
    channelPublic1 = assertNoError(createChannelResponse).readEntity();

    Channel channelPublic2 =
        new Channel("Public 2", "public2", ChannelType.Open, th.basicTeam().getId());
    channelPublic2 = assertNoError(client.createChannel(channelPublic2)).readEntity();

    Channel channelPrivate =
        new Channel("Private", "private", ChannelType.Private, th.basicTeam().getId());
    channelPrivate = assertNoError(client.createChannel(channelPrivate)).readEntity();

    Team otherTeam = th.createTeam();
    Channel channelOtherTeam =
        new Channel("Other Team Channel", "other-team", ChannelType.Open, otherTeam.getId());
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
    Map<String, Map<String, String>> channelMentions =
        (Map<String, Map<String, String>>) channelOnBasicTeam.getProps().get("channel_mentions");
    assertThat(channelMentions.containsKey("public1"), is(true));
    assertThat(channelMentions.get("public1").get("display_name"),
        is(channelPublic1.getDisplayName()));

  }

  private boolean isNotSupportVersion(String minimumRequirement, ApiResponse<?> response) {
    Semver serverVersion = new Semver(response.getRawResponse().getHeaderString("X-Version-Id"));
    Semver requirement = new Semver(minimumRequirement);
    return serverVersion.compareTo(requirement) < 0;
  }

  // Users

  @Test
  public void testUsers_CreateUser() {
    User user = new User();
    user.setEmail(th.generateTestEmail());
    user.setUsername(th.generateTestUsername());
    user.setPassword("PASSWD");

    ApiResponse<User> response = assertNoError(client.createUser(user));
    User created = response.readEntity();

    assertThat(created.getEmail(), is(user.getEmail()));
    assertThat(created.getUsername(), is(user.getUsername()));
  }

  @Test
  public void testUsers_GetUsers() {
    ApiResponse<UserList> response = assertNoError(client.getUsers(Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users, is(not(emptyIterable())));
  }

  @Test
  public void testUsers_GetUsers_InChannel() {
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
  public void testUsers_GetUsers_NotInChannel() {
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
  public void testUsers_GetUsers_InTeam() {
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
  public void testUsers_GetUsers_NotInTeam() {
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
  public void testUsers_GetUsers_WithoutTeam() {
    User withoutTeamUser = th.loginSystemAdmin().createUser();
    th.loginSystemAdmin();

    ApiResponse<UserList> response =
        assertNoError(client.getUsersWithoutTeam(Pager.of(0, 60), null));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(withoutTeamUser.getId()));
  }

  @Test
  public void testUsers_GetUsersByIds() {

    ApiResponse<UserList> response =
        assertNoError(client.getUsersByIds(th.basicUser().getId(), th.basicUser2().getId()));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  public void testUsers_GetUsersByUsernames() {

    ApiResponse<UserList> response = assertNoError(
        client.getUsersByUsernames(th.basicUser().getUsername(), th.basicUser2().getUsername()));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
        containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  public void testUsers_SearchUsers() {
    UserSearch criteria = UserSearch.builder().term(th.basicUser().getUsername())
        .teamId(th.basicTeam().getId()).build();

    ApiResponse<UserList> response = assertNoError(client.searchUsers(criteria));
    List<User> users = response.readEntity();

    assertThat(users.stream().map(User::getUsername).collect(Collectors.toSet()),
        hasItem(th.basicUser().getUsername()));
  }

  @Test
  public void testUsers_AutocompleteUsers() {

    ApiResponse<UserAutocomplete> response =
        assertNoError(client.autocompleteUsers(th.basicUser().getUsername(), null));
    UserAutocomplete autocompleteUsers = response.readEntity();

    assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(th.basicUser().getId()));
  }

  @Test
  public void testUsers_AutocompleteUsers_InTeam() {

    ApiResponse<UserAutocomplete> response = assertNoError(
        client.autocompleteUsersInTeam(th.basicTeam().getId(), th.basicUser().getUsername(), null));
    UserAutocomplete autocompleteUsers = response.readEntity();

    assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
        hasItem(th.basicUser().getId()));
  }

  @Test
  public void testUsers_AutocompleteUsers_InChannel() {
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
  public void testUsers_GetUser() {
    String userId = th.basicUser().getId();

    ApiResponse<User> response = assertNoError(client.getUser(userId, null));
    User user = response.readEntity();

    assertThat(user.getId(), is(userId));
  }

  @Test
  public void testUsers_UpdateUser() {
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
  public void testUsers_Deactivate() {
    th.loginSystemAdmin();
    String userId = th.basicUser().getId();

    ApiResponse<Boolean> response = assertNoError(client.deleteUser(userId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_PatchUser() {
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
  public void testUsers_UpdateUserRoles() {
    th.loginSystemAdmin();

    ApiResponse<Boolean> response = assertNoError(client.updateUserRoles(th.basicUser().getId(),
        Role.ROLE_SYSTEM_ADMIN, Role.ROLE_SYSTEM_USER));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_UpdateUserActiveStatus() {
    th.logout().loginSystemAdmin();
    ApiResponse<Boolean> response =
        assertNoError(client.updateUserActive(th.basicUser().getId(), false));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_GetUserProfileImage() throws FileNotFoundException, IOException {

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
  public void testUsers_SetUserProfileImage() throws URISyntaxException {
    Path image = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());

    ApiResponse<Boolean> response =
        assertNoError(client.setProfileImage(th.basicUser().getId(), image));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_GetUserByName() {
    String username = th.basicUser().getUsername();

    ApiResponse<User> response = assertNoError(client.getUserByUsername(username, null));
    User user = response.readEntity();

    assertThat(user.getId(), is(th.basicUser().getId()));
  }

  @Test
  public void testUsers_ResetPassword() {

    // invalid token
    assertStatus(client.resetPassword(th.newRandomString(64), "passwd"), Status.BAD_REQUEST);
  }

  @Test
  public void testUsers_UpdateUserMfa() {

    // Enterprise Edition required
    assertStatus(client.updateUserMfa(th.basicUser().getId(), null, false), Status.NOT_IMPLEMENTED);
  }

  @Test
  public void testUsers_GenerateMfaSecret() {
    th.loginSystemAdmin();

    // Enterprise Edition required
    assertStatus(client.generateMfaSecret(th.basicUser().getId()), Status.NOT_IMPLEMENTED);
  }

  @Test
  public void testUsers_CheckMfa() {
    boolean mfaRequired = client.checkUserMfa(th.basicUser().getId());

    assertThat(mfaRequired, is(false));
  }

  @Test
  public void testUsers_UpdateUserPassword() {
    String userId = th.basicUser().getId();
    String currentPassword = th.basicUser().getPassword();
    String newPassword = "new" + currentPassword;

    ApiResponse<Boolean> response =
        assertNoError(client.updateUserPassword(userId, currentPassword, newPassword));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_SendPasswordResetEmail() {
    String email = th.basicUser().getEmail();

    ApiResponse<Boolean> response = assertNoError(client.sendPasswordResetEmail(email));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_GetUserByEmail() {
    String email = th.basicUser().getEmail();

    ApiResponse<User> response = assertNoError(client.getUserByEmail(email, null));
    User user = response.readEntity();

    assertThat(user.getId(), is(th.basicUser().getId()));
  }

  @Test
  public void testUsers_GetUserSessions() {
    String userId = th.basicUser().getId();

    ApiResponse<SessionList> response = assertNoError(client.getSessions(userId, null));
    List<Session> sessions = response.readEntity();

    assertThat(sessions.stream().findAny().map(Session::getUserId).get(), is(userId));
  }

  @Test
  public void testUsers_RevokeUserSession() {
    ApiResponse<SessionList> response =
        assertNoError(client.getSessions(th.basicUser().getId(), null));
    Session session = response.readEntity().stream().findAny().get();

    ApiResponse<Boolean> response2 =
        assertNoError(client.revokeSession(session.getUserId(), session.getId()));
    boolean result = response2.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_AttachMobileDevice() {
    ApiResponse<Boolean> response = assertNoError(client.attachDeviceId(th.newId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testUsers_GetAudits() {

    ApiResponse<Audits> response =
        assertNoError(client.getUserAudits(th.basicUser().getId(), Pager.of(0, 50), null));
    Audits audits = response.readEntity();

    assertThat(audits.stream().findAny().map(Audit::getId).get(), is(not(nullValue())));
  }

  @Test
  public void testUsers_VerifyEmail() {

    // invalid token
    assertStatus(client.verifyUserEmail(th.newId()), Status.BAD_REQUEST);
  }

  @Test
  public void testUsers_SendVerificationEmail() {

    ApiResponse<Boolean> response =
        assertNoError(client.sendVerificationEmail(th.basicUser().getEmail()));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testUsers_SwitchLoginMethod() {
    SwitchRequest request = new SwitchRequest();
    request.setCurrentService(AuthService.Email);
    request.setNewService(AuthService.GitLab);
    request.setEmail(th.basicUser().getEmail());
    request.setCurrentPassword(th.basicUser().getPassword());
    request.setPassword(th.basicUser().getPassword()); // for 4.0+
    th.loginBasic();

    assertStatus(client.switchAccountType(request), Status.NOT_IMPLEMENTED);
  }

  // Teams

  @Test
  public void testTeams_CreateTeam() {
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
  public void testTeams_GetTeams() {
    Team team = th.loginSystemAdmin().createTeam();

    ApiResponse<TeamList> response = assertNoError(client.getAllTeams(Pager.of(0, 60), null));
    List<Team> teams = response.readEntity();

    assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(team.getId()));
  }

  @Test
  public void testTeams_getTeam() {

    ApiResponse<Team> response = assertNoError(client.getTeam(th.basicTeam().getId(), null));
    Team team = response.readEntity();

    assertThat(team.getId(), is(th.basicTeam().getId()));
  }

  @Test
  public void testTeams_UpdateTeam() {
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
  public void testTeams_DeleteTeam_Soft() {
    th.loginSystemAdmin();

    ApiResponse<Boolean> response = assertNoError(client.deleteTeam(th.basicTeam().getId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testTeams_DeleteTeam_Permanent() {
    th.loginSystemAdmin();

    ApiResponse<Boolean> response = assertNoError(client.deleteTeam(th.basicTeam().getId(), true));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testTeams_PatchTeam() {
    th.loginTeamAdmin();
    TeamPatch patch = new TeamPatch();
    final String newDisplayName = "new" + th.basicTeam().getDisplayName();
    patch.setDisplayName(newDisplayName);

    ApiResponse<Team> response = assertNoError(client.patchTeam(th.basicTeam().getId(), patch));
    Team team = response.readEntity();

    assertThat(team.getDisplayName(), is(newDisplayName));
  }

  @Test
  public void testTeams_GetTeamByName() {
    final String teamId = th.basicTeam().getId();
    final String teamName = th.basicTeam().getName();

    ApiResponse<Team> response = assertNoError(client.getTeamByName(teamName, null));
    Team team = response.readEntity();

    assertThat(team.getId(), is(teamId));
  }

  @Test
  public void testTeams_SearchTeams() {
    TeamSearch search = new TeamSearch();
    search.setTerm(th.basicTeam().getName());

    ApiResponse<TeamList> response = assertNoError(client.searchTeams(search));
    List<Team> teams = response.readEntity();

    assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
        hasItem(th.basicTeam().getId()));
  }

  @Test
  public void testTeams_TeamExists_Exists() {

    ApiResponse<TeamExists> response =
        assertNoError(client.teamExists(th.basicTeam().getName(), null));
    TeamExists exists = response.readEntity();

    assertThat(exists.isExists(), is(true));
  }

  @Test
  public void testTeams_TeamExists_NotExists() {

    ApiResponse<TeamExists> response =
        assertNoError(client.teamExists("fake" + th.generateTestTeamName(), null));
    TeamExists exists = response.readEntity();

    assertThat(exists.isExists(), is(false));
  }

  @Test
  public void testTeams_GetUsersTeams() {
    String userId = th.basicUser().getId();

    ApiResponse<TeamList> response = assertNoError(client.getTeamsForUser(userId, null));
    List<Team> teams = response.readEntity();

    assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
        hasItem(th.basicTeam().getId()));
  }

  @Test
  public void testTeams_GetTeamMembers() {

    ApiResponse<TeamMemberList> response =
        assertNoError(client.getTeamMembers(th.basicTeam().getId(), Pager.of(0, 60), null));
    List<TeamMember> teamMembers = response.readEntity();

    assertThat(teamMembers.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        hasItems(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  public void testTeams_AddUserToTeam() {
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
  @Ignore // API for 4.0+
  public void testTeams_AddUserToTeamFromInvite() {

    assertStatus(client.addTeamMember("hash", "dataToHash", "inviteId"), Status.BAD_REQUEST);
  }

  @Test
  public void testTeams_AddMultipleUsersToTeam() {
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
  public void testTeams_GetTeamMembersForUser() {

    ApiResponse<TeamMemberList> response =
        assertNoError(client.getTeamMembersForUser(th.basicUser().getId(), null));
    List<TeamMember> members = response.readEntity();

    assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        hasItems(th.basicUser().getId()));
  }

  @Test
  public void testTeams_GetTeamMember() {
    String teamId = th.basicTeam().getId();
    String userId = th.basicUser2().getId();

    ApiResponse<TeamMember> response = assertNoError(client.getTeamMember(teamId, userId, null));
    TeamMember member = response.readEntity();

    assertThat(member.getTeamId(), is(teamId));
    assertThat(member.getUserId(), is(userId));
  }

  @Test
  public void testTeams_RemoveUserFromTeam() {
    th.loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String userId = th.basicUser2().getId();

    ApiResponse<Boolean> response = assertNoError(client.removeTeamMember(teamId, userId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testTeams_GetTeamMembersByIds() {

    ApiResponse<TeamMemberList> response =
        assertNoError(client.getTeamMembersByIds(th.basicTeam().getId(), th.basicUser().getId(),
            th.basicUser2().getId()));
    List<TeamMember> members = response.readEntity();

    assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
        hasItems(th.basicUser().getId(), th.basicUser2().getId()));
  }

  @Test
  public void testTeams_GetTeamStats() {

    ApiResponse<TeamStats> response =
        assertNoError(client.getTeamStats(th.basicTeam().getId(), null));
    TeamStats stats = response.readEntity();

    assertThat(stats.getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  public void testTeams_UpdateTeamMemberRoles() {
    th.loginTeamAdmin();

    ApiResponse<Boolean> response =
        assertNoError(client.updateTeamMemberRoles(th.basicTeam().getId(), th.basicUser().getId(),
            Role.ROLE_TEAM_ADMIN));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testTeams_GetTeamUnreadsForUser() {

    ApiResponse<TeamUnreadList> response =
        assertNoError(client.getTeamUnreadForUser(th.basicUser().getId(), null));
    List<TeamUnread> unreads = response.readEntity();

    unreads.stream().findFirst()
        .ifPresent(u -> assertThat(u.getTeamId(), is(th.basicTeam().getId())));
  }

  @Test
  public void testTeams_GetTeamUnreadsForTeam() {

    ApiResponse<TeamUnread> response =
        assertNoError(client.getTeamUnread(th.basicTeam().getId(), th.basicUser().getId()));
    TeamUnread unread = response.readEntity();

    assertThat(unread.getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  public void testTeams_InviteUsersToTheTeamByEmail() {

    ApiResponse<Boolean> response = assertNoError(client.inviteUsersToTeam(th.basicTeam().getId(),
        Collections.singletonList(th.generateTestEmail())));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  @Ignore // Not Implemented
  public void testTeams_ImportTeamFromOtherApplication() {}

  @Test
  public void testTeams_GetPublicChannels() {

    ApiResponse<ChannelList> response = assertNoError(
        client.getPublicChannelsForTeam(th.basicTeam().getId(), Pager.of(0, 60), null));
    List<Channel> channels = response.readEntity();

    assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
  }

  @Test
  public void testTeams_SearchChannels() {
    ChannelSearch search = new ChannelSearch();
    search.setTerm(th.basicChannel().getName());

    ApiResponse<ChannelList> response =
        assertNoError(client.searchChannels(th.basicTeam().getId(), search));
    List<Channel> channels = response.readEntity();

    assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
  }

  // Posts

  @Test
  public void testPosts_CreatePost() {
    Post post = new Post();
    post.setChannelId(th.basicChannel().getId());
    post.setMessage("Hello World!");

    ApiResponse<Post> response = assertNoError(client.createPost(post));
    Post postedPost = response.readEntity();

    assertThat(postedPost.getMessage(), is(post.getMessage()));
    assertThat(postedPost.getChannelId(), is(post.getChannelId()));
  }

  @Test
  public void testPosts_GetPost() {
    String postId = th.basicPost().getId();

    ApiResponse<Post> response = assertNoError(client.getPost(postId, null));
    Post post = response.readEntity();

    assertThat(post.getId(), is(postId));
  }

  @Test
  public void testPosts_DeletePost() {
    String postId = th.createPost(th.basicChannel()).getId();

    ApiResponse<Boolean> response = assertNoError(client.deletePost(postId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testPosts_UpdatePost() {
    Post post = th.createPost(th.basicChannel());
    post.setMessage("UPDATE:" + post.getMessage());

    ApiResponse<Post> response = assertNoError(client.updatePost(post.getId(), post));
    Post updatedPost = response.readEntity();

    assertThat(updatedPost.getMessage(), is(post.getMessage()));
  }

  @Test
  public void testPosts_PatchPost() {
    String postId = th.createPost(th.basicChannel()).getId();
    PostPatch patch = new PostPatch();
    patch.setMessage("NEW MESSAGE");

    ApiResponse<Post> response = assertNoError(client.patchPost(postId, patch));
    Post updatedPost = response.readEntity();

    assertThat(updatedPost.getMessage(), is(patch.getMessage()));
  }

  @Test
  public void testPosts_GetThread() {
    String postId = th.basicPost().getId();

    ApiResponse<PostList> response = assertNoError(client.getPostThread(postId, null));
    PostList posts = response.readEntity();

    assertThat(posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet()),
        hasItem(postId));
  }

  @Test
  @Ignore // TODO
  public void testPosts_GetFlaggedPosts() {}

  @Test
  @Ignore // TODO
  public void testPosts_GetFileInfoForPost() {}

  @Test
  public void testPosts_GetPostsForChannel() {
    String channelId = th.basicChannel().getId();

    ApiResponse<PostList> response =
        assertNoError(client.getPostsForChannel(channelId, Pager.of(0, 60), null));
    PostList posts = response.readEntity();

    assertThat(
        posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
        hasItem(channelId));
  }

  @Test
  public void testPosts_GetPostsForChannel_Since() {
    String channelId = th.basicChannel().getId();
    OffsetDateTime since = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    ApiResponse<PostList> response =
        assertNoError(client.getPostsSince(channelId, since.toEpochSecond()));
    PostList posts = response.readEntity();

    assertThat(
        posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
        hasItem(channelId));
  }

  @Test
  public void testPosts_GetPostsForChannel_Before() {
    String channelId = th.basicChannel().getId();
    Post post1 = th.createPost(th.basicChannel());
    Post post2 = th.createPost(th.basicChannel());
    Post post3 = th.createPost(th.basicChannel());

    ApiResponse<PostList> response =
        assertNoError(client.getPostsBefore(channelId, post2.getId(), Pager.of(0, 60), null));
    PostList posts = response.readEntity();

    Set<String> channelIds =
        posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet());
    Set<String> postIds =
        posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet());
    assertThat(channelIds, hasItem(channelId));
    assertThat(postIds, hasItem(post1.getId()));
    assertThat(postIds, not(hasItem(post2.getId())));
    assertThat(postIds, not(hasItem(post3.getId())));
  }

  @Test
  public void testPosts_GetPostsForChannel_After() {
    String channelId = th.basicChannel().getId();
    Post post1 = th.createPost(th.basicChannel());
    Post post2 = th.createPost(th.basicChannel());
    Post post3 = th.createPost(th.basicChannel());

    ApiResponse<PostList> response =
        assertNoError(client.getPostsAfter(channelId, post2.getId(), Pager.of(0, 60), null));
    PostList posts = response.readEntity();

    Set<String> channelIds =
        posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet());
    Set<String> postIds =
        posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet());
    assertThat(channelIds, hasItem(channelId));
    assertThat(postIds, not(hasItem(post1.getId())));
    assertThat(postIds, not(hasItem(post2.getId())));
    assertThat(postIds, hasItem(post3.getId()));
  }

  @Test
  @Ignore // FIXME work correctly from 4.0
  public void testPosts_SearchForTeamPosts() {
    String teamId = th.basicTeam().getId();
    String channelId = th.basicChannel().getId();
    Post post1 = new Post(channelId, "hello");
    Post post2 = new Post(channelId, "mattermost");
    Post post3 = new Post(channelId, "world");
    post1 = assertNoError(client.createPost(post1)).readEntity();
    post2 = assertNoError(client.createPost(post2)).readEntity();
    post3 = assertNoError(client.createPost(post3)).readEntity();

    ApiResponse<PostList> response = assertNoError(client.searchPosts(teamId, "hello", false));
    PostList posts = response.readEntity();

    assertThat(posts.getPosts().keySet(), hasItem(post1.getId()));
    assertThat(posts.getPosts().keySet(), not(hasItems(post2.getId(), post3.getId())));

    response = assertNoError(client.searchPosts(teamId, "hello world", true));
    posts = response.readEntity();

    assertThat(posts.getPosts().keySet(), hasItems(post1.getId(), post2.getId()));
    assertThat(posts.getPosts().keySet(), not(hasItem(post3.getId())));
  }

  @Test
  public void testPosts_PinPostToChannel() {
    Post post = th.createPost(th.basicChannel());

    ApiResponse<Boolean> response = assertNoError(client.pinPost(post.getId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));

    Post pinnedPost = post;

    response = assertNoError(client.pinPost(pinnedPost.getId()));
    result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testPosts_UnPinPost() {
    Post post = th.createPost(th.basicChannel());
    assertNoError(client.pinPost(post.getId()));

    ApiResponse<Boolean> response = assertNoError(client.unpinPost(post.getId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));

    Post unpinnedPost = post;

    response = assertNoError(client.unpinPost(unpinnedPost.getId()));
    result = response.readEntity();

    assertThat(result, is(true));
  }

  // Preferences

  @Test
  public void testPreferences_GetPreferences() {
    String userId = th.basicUser().getId();

    ApiResponse<Preferences> response = assertNoError(client.getPreferences(userId));
    Preferences preferences = response.readEntity();

    assertThat(preferences.isEmpty(), is(false));
    assertThat(preferences.stream().map(Preference::getUserid).collect(Collectors.toSet()),
        containsInAnyOrder(userId));
  }

  @Test
  public void testPreferences_SavePreferences() {
    String userId = th.basicUser().getId();
    Preferences preferences = client.getPreferences(userId).readEntity();
    Preference preference = preferences.stream()
        .filter(p -> p.getCategory() == PreferenceCategory.TUTORIAL_STEPS).findAny().get();
    preference.setValue("1"); // 2nd tutorial step

    ApiResponse<Boolean> response = assertNoError(client.updatePreferences(userId, preferences));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testPreferences_DeletePreference() {
    String userId = th.basicUser().getId();
    Preferences currentPreferences = client.getPreferences(userId).readEntity();
    Preference tutorial = currentPreferences.stream()
        .filter(p -> p.getCategory() == PreferenceCategory.TUTORIAL_STEPS).findAny().get();

    Preferences deleteTargets = new Preferences();
    deleteTargets.add(tutorial);
    ApiResponse<Boolean> response = assertNoError(client.deletePreferences(userId, deleteTargets));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void testPreferences_GetPreferencesByCategory() {
    String userId = th.basicUser().getId();
    PreferenceCategory category = PreferenceCategory.TUTORIAL_STEPS;

    ApiResponse<Preferences> response =
        assertNoError(client.getPreferencesByCategory(userId, category));
    Preferences preferences = response.readEntity();

    assertThat(preferences.isEmpty(), is(false));
  }

  @Test
  public void testPreferences_GetPreference() {
    String userId = th.basicUser().getId();
    PreferenceCategory category = PreferenceCategory.DISPLAY_SETTINGS;
    String name = Preference.Name.ChannelDisplayMode.getKey();
    String value = "full";
    {
      Preference preference = new Preference();
      preference.setUserid(userId);
      preference.setCategory(category);
      preference.setName(name);
      preference.setValue(value);
      Preferences preferences = new Preferences();
      preferences.add(preference);
      assertNoError(client.updatePreferences(userId, preferences));
    }

    ApiResponse<Preference> response =
        assertNoError(client.getPreferenceByCategoryAndName(userId, category, name));
    Preference preference = response.readEntity();

    assertThat(preference.getCategory(), is(PreferenceCategory.DISPLAY_SETTINGS));
    assertThat(preference.getName(), is(name));
    assertThat(preference.getValue(), is(value));
  }

  // Emoji

  @Test
  @Ignore // emoji upload fail from MM 4.8
  public void testEmoji_CreateCustomEmoji() throws URISyntaxException {
    Path image = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());
    Emoji emoji = new Emoji();
    String emojiName = "custom" + th.newId();
    emoji.setName(emojiName);
    emoji.setCreatorId(th.basicUser().getId());

    ApiResponse<Emoji> response = assertNoError(client.createEmoji(emoji, image));
    Emoji createdEmoji = response.readEntity();

    assertThat(createdEmoji.getName(), is(emojiName));
    assertThat(createdEmoji.getId(), is(not(nullValue())));
  }

  @Test
  @Ignore // emoji upload fail from MM 4.8
  public void testEmoji_GetCustomEmojiList() throws URISyntaxException {
    Path image = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());
    Emoji emoji1 = new Emoji();
    emoji1.setName("custom" + th.newId());
    emoji1.setCreatorId(th.basicUser().getId());
    emoji1 = client.createEmoji(emoji1, image).readEntity();
    Emoji emoji2 = new Emoji();
    emoji2.setName("custom" + th.newId());
    emoji2.setCreatorId(th.basicUser().getId());
    emoji2 = client.createEmoji(emoji2, image).readEntity();

    ApiResponse<EmojiList> response = assertNoError(client.getEmojiList());
    List<Emoji> emojiList = response.readEntity();

    assertThat(emojiList.stream().map(Emoji::getId).collect(Collectors.toSet()),
        hasItems(emoji1.getId(), emoji2.getId()));
  }

  @Test
  @Ignore // emoji upload fail from MM 4.8
  public void testEmoji_GetCustomEmoji() throws URISyntaxException {
    Path image = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    emoji = client.createEmoji(emoji, image).readEntity();
    String emojiId = emoji.getId();

    ApiResponse<Emoji> response = assertNoError(client.getEmoji(emojiId));
    Emoji responseEmoji = response.readEntity();

    assertThat(responseEmoji.getName(), is(emoji.getName()));
  }

  @Test
  @Ignore // emoji upload fail from MM 4.8
  public void testEmoji_DeleteCustomEmoji() throws URISyntaxException {
    Path image = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    emoji = assertNoError(client.createEmoji(emoji, image)).readEntity();
    String emojiId = emoji.getId();

    ApiResponse<Boolean> response = assertNoError(client.deleteEmoji(emojiId));
    Boolean result = response.readEntity();

    assertThat(result, is(true));
    assertThat( //
        client.getEmojiList().readEntity() //
            .stream() //
            .map(Emoji::getId) //
            .collect(Collectors.toSet()), //
        is(not(hasItem(emojiId))));
  }

  @Test
  public void testEmoji_GetCustomEmojiImage() throws URISyntaxException, IOException {
    Path originalImage = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    emoji = client.createEmoji(emoji, originalImage).readEntity();
    String emojiId = emoji.getId();

    ApiResponse<Path> emojiImage = assertNoError(client.getEmojiImage(emojiId));
    Path downloadedFile = emojiImage.readEntity();

    // file contents equals?
    String originalHash = DigestUtils.sha1Hex(Files.readAllBytes(originalImage));
    String downloadedHash = DigestUtils.sha1Hex(Files.readAllBytes(downloadedFile));

    assertEquals(originalHash, downloadedHash);
  }

  // Webhooks

  @Test
  public void testWebhooks_CreateIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    String displayName = "webhook" + th.newId();
    String description = "description" + th.newId();
    IncomingWebhook webhook = new IncomingWebhook();
    webhook.setChannelId(channelId);
    webhook.setDisplayName(displayName);
    webhook.setDescription(description);

    ApiResponse<IncomingWebhook> response = assertNoError(client.createIncomingWebhook(webhook));
    webhook = response.readEntity();

    assertThat(webhook.getId(), is(not(nullValue())));
    assertThat(webhook.getChannelId(), is(channelId));
    assertThat(webhook.getDisplayName(), is(displayName));
    assertThat(webhook.getDescription(), is(description));
  }

  @Test
  public void testWebhooks_ListIncomingWebhooks() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook1 = new IncomingWebhook();
    webhook1.setChannelId(channelId);
    IncomingWebhook webhook2 = new IncomingWebhook();
    webhook2.setChannelId(channelId);
    webhook1 = assertNoError(client.createIncomingWebhook(webhook1)).readEntity();
    webhook2 = assertNoError(client.createIncomingWebhook(webhook2)).readEntity();

    th.loginSystemAdmin(); // needs permission for get other team's webhooks
    ApiResponse<IncomingWebhookList> response = assertNoError(client.getIncomingWebhooks());
    List<IncomingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.size(), is(greaterThanOrEqualTo(2)));
    assertThat(webhooks.stream().map(IncomingWebhook::getId).collect(Collectors.toSet()),
        hasItems(webhook1.getId(), webhook2.getId()));
  }

  @Test
  public void testWebhooks_ListIncomingWebhooksForTeam() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    String teamId = th.basicTeam().getId();
    IncomingWebhook webhook1 = new IncomingWebhook();
    webhook1.setChannelId(channelId);
    IncomingWebhook webhook2 = new IncomingWebhook();
    webhook2.setChannelId(channelId);
    webhook1 = assertNoError(client.createIncomingWebhook(webhook1)).readEntity();
    webhook2 = assertNoError(client.createIncomingWebhook(webhook2)).readEntity();

    ApiResponse<IncomingWebhookList> response =
        assertNoError(client.getIncomingWebhooksForTeam(teamId));
    List<IncomingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.size(), is(2));
    assertThat(webhooks.stream().map(IncomingWebhook::getId).collect(Collectors.toSet()),
        containsInAnyOrder(webhook1.getId(), webhook2.getId()));
  }

  @Test
  public void testWebhooks_GetIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook = new IncomingWebhook();
    webhook.setChannelId(channelId);
    webhook = assertNoError(client.createIncomingWebhook(webhook)).readEntity();
    String webhookId = webhook.getId();

    ApiResponse<IncomingWebhook> response = assertNoError(client.getIncomingWebhook(webhookId));
    IncomingWebhook responseWebhook = response.readEntity();

    assertThat(responseWebhook, is(not(nullValue())));
    assertThat(responseWebhook.getId(), is(webhookId));
  }

  @Test
  public void testWebhooks_UpdateIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook = new IncomingWebhook();
    {
      webhook.setChannelId(channelId);
      webhook.setDisplayName(th.newRandomString(32));
      webhook.setDescription(th.newRandomString(32));
      webhook = assertNoError(client.createIncomingWebhook(webhook)).readEntity();
    }

    String newDisplayName = "new" + webhook.getDisplayName();
    String newDescription = "new" + webhook.getDescription();
    String newChannelId = th.basicChannel2().getId();
    webhook.setDisplayName(newDisplayName);
    webhook.setDescription(newDescription);
    webhook.setChannelId(newChannelId);
    ApiResponse<IncomingWebhook> response = assertNoError(client.updateIncomingWebhook(webhook));
    IncomingWebhook updatedWebhook = response.readEntity();

    assertThat(updatedWebhook.getDisplayName(), is(webhook.getDisplayName()));
    assertThat(updatedWebhook.getDescription(), is(webhook.getDescription()));
    assertThat(updatedWebhook.getChannelId(), is(webhook.getChannelId()));
  }

  @Test
  public void testWebhooks_CreateOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String channelId = th.basicChannel().getId();
    String description = th.newRandomString(32);
    String displayName = th.newRandomString(32);
    String triggerWord = "trigger";
    TriggerWhen triggerWhen = TriggerWhen.CONTAINS;
    String callbackUrl = "http://callback-url";
    ContentType contentType = ContentType.FORM;
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(teamId);
    webhook.setChannelId(channelId);
    webhook.setDescription(description);
    webhook.setDisplayName(displayName);
    webhook.setTriggerWords(Arrays.asList(triggerWord));
    webhook.setTriggerWhen(triggerWhen);
    webhook.setCallbackUrls(Arrays.asList(callbackUrl));
    webhook.setContentType(contentType);

    ApiResponse<OutgoingWebhook> response = assertNoError(client.createOutgoingWebhook(webhook));
    OutgoingWebhook created = response.readEntity();

    assertThat(created.getId(), is(not(nullValue())));
    assertThat(created.getTeamId(), is(webhook.getTeamId()));
    assertThat(created.getChannelId(), is(webhook.getChannelId()));
    assertThat(created.getDescription(), is(webhook.getDescription()));
    assertThat(created.getDisplayName(), is(webhook.getDisplayName()));
    assertThat(created.getTriggerWords(), containsInAnyOrder(webhook.getTriggerWords().toArray()));
    assertThat(created.getTriggerWhen(), is(webhook.getTriggerWhen()));
    assertThat(created.getCallbackUrls(), containsInAnyOrder(webhook.getCallbackUrls().toArray()));
    assertThat(created.getContentType(), is(webhook.getContentType()));
  }

  @Test
  public void testWebhooks_ListOutgoingWebhooks() {
    String teamId = th.basicTeam().getId();
    String displayName = th.newRandomString(32);
    List<String> callbackUrls = Arrays.asList("http://callback-url");
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook1 = new OutgoingWebhook();
    webhook1.setTeamId(teamId);
    webhook1.setDisplayName(displayName);
    webhook1.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
    webhook1.setCallbackUrls(callbackUrls);
    webhook1 = assertNoError(client.createOutgoingWebhook(webhook1)).readEntity();
    OutgoingWebhook webhook2 = new OutgoingWebhook();
    webhook2.setTeamId(teamId);
    webhook2.setDisplayName(displayName);
    webhook2.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
    webhook2.setCallbackUrls(callbackUrls);
    webhook2 = assertNoError(client.createOutgoingWebhook(webhook2)).readEntity();

    th.loginSystemAdmin(); // needs permission for get other team's webhooks
    ApiResponse<OutgoingWebhookList> response = assertNoError(client.getOutgoingWebhooks());
    List<OutgoingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.stream().map(OutgoingWebhook::getId).collect(Collectors.toSet()),
        hasItems(webhook1.getId(), webhook2.getId()));
  }

  @Test
  public void testWebhooks_ListOutgoingWebhooksForTeam() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String displayName = th.newRandomString(32);
    List<String> triggerWords = Arrays.asList("trigger");
    List<String> callbackUrls = Arrays.asList("http://callback-url");
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(teamId);
    webhook.setDisplayName(displayName);
    webhook.setTriggerWords(triggerWords);
    webhook.setCallbackUrls(callbackUrls);
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    ApiResponse<OutgoingWebhookList> response =
        assertNoError(client.getOutgoingWebhooksForTeam(teamId));
    List<OutgoingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.stream().map(OutgoingWebhook::getId).collect(Collectors.toSet()),
        containsInAnyOrder(webhook.getId()));
  }

  @Test
  public void testWebhooks_ListOutgoingWebhooksForChannel() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String channelId = th.basicChannel().getId();
    String displayName = th.newRandomString(32);
    List<String> callbackUrls = Arrays.asList("http://callback-url");
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(teamId);
    webhook.setChannelId(channelId);
    webhook.setDisplayName(displayName);
    webhook.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
    webhook.setCallbackUrls(callbackUrls);
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();
    {
      OutgoingWebhook otherChannelHook = new OutgoingWebhook();
      otherChannelHook.setTeamId(teamId);
      otherChannelHook.setChannelId(th.basicChannel2().getId());
      otherChannelHook.setDisplayName(displayName);
      otherChannelHook.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
      otherChannelHook.setCallbackUrls(callbackUrls);
      otherChannelHook = assertNoError(client.createOutgoingWebhook(otherChannelHook)).readEntity();

      OutgoingWebhook noChannelHook = new OutgoingWebhook();
      noChannelHook.setTeamId(teamId);
      noChannelHook.setDisplayName(displayName);
      noChannelHook.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
      noChannelHook.setCallbackUrls(callbackUrls);
      noChannelHook = assertNoError(client.createOutgoingWebhook(noChannelHook)).readEntity();
    }

    ApiResponse<OutgoingWebhookList> response =
        assertNoError(client.getOutgoingWebhooksForChannel(channelId));
    List<OutgoingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.stream().map(OutgoingWebhook::getId).collect(Collectors.toSet()),
        containsInAnyOrder(webhook.getId()));
  }

  @Test
  public void testWebhooks_GetOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    String webhookId = webhook.getId();
    ApiResponse<OutgoingWebhook> response = assertNoError(client.getOutgoingWebhook(webhookId));
    OutgoingWebhook responseWebhook = response.readEntity();

    assertThat(responseWebhook.getId(), is(webhookId));
  }

  @Test
  public void testWebhooks_DeleteOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    String webhookId = webhook.getId();
    ApiResponse<Boolean> response = assertNoError(client.deleteOutgoingWebhook(webhookId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
    assertThat(client.getOutgoingWebhooksForTeam(th.basicTeam().getId()).readEntity().stream()
        .map(OutgoingWebhook::getId).collect(Collectors.toSet()), not(hasItem(webhookId)));
  }

  @Test
  public void testWebhooks_UpdateOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    webhook.setChannelId(th.basicChannel().getId());
    webhook.setDisplayName("update" + webhook.getDisplayName());
    webhook.setDescription("update");
    ApiResponse<OutgoingWebhook> response = assertNoError(client.updateOutgoingWebhook(webhook));
    OutgoingWebhook updated = response.readEntity();

    assertThat(updated.getId(), is(webhook.getId()));
    assertThat(updated.getChannelId(), is(webhook.getChannelId()));
    assertThat(updated.getDisplayName(), is(webhook.getDisplayName()));
    assertThat(updated.getDescription(), is(webhook.getDescription()));
  }

  @Test
  public void testWebhooks_RgenerateOutgoingWebhookToken() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    String currentToken = webhook.getToken();
    ApiResponse<OutgoingWebhook> response =
        assertNoError(client.regenOutgoingHookToken(webhook.getId()));
    OutgoingWebhook updated = response.readEntity();
    String newToken = updated.getToken();

    assertThat(newToken, is(not(currentToken)));
  }

  // Commands

  @Test
  public void testCommands_CreateCommand() {
    th.loginTeamAdmin();
    Command command = new Command();
    // required params
    command.setTeamId(th.basicTeam().getId());
    command.setMethod(CommandMethod.GET);
    command.setTrigger("trigger");
    command.setUrl("http://url");
    // optional params
    command.setDescription(th.newRandomString(32));
    command.setUserName("username");
    command.setIconUrl("http://icon-url");
    command.setAutoComplete(true);
    command.setAutoCompleteDesc(th.newRandomString(32));
    command.setAutoCompleteHint(th.newRandomString(32));

    ApiResponse<Command> response = assertNoError(client.createCommand(command));
    Command created = response.readEntity();

    assertThat(created.getId(), is(not(nullValue())));
    assertThat(created.getToken(), is(not(nullValue())));
    assertThat(created.getTeamId(), is(command.getTeamId()));
    assertThat(created.getMethod(), is(command.getMethod()));
    assertThat(created.getTrigger(), is(command.getTrigger()));
    assertThat(created.getUrl(), is(command.getUrl()));
    assertThat(created.getDescription(), is(command.getDescription()));
    assertThat(created.getUserName(), is(command.getUserName()));
    assertThat(created.getIconUrl(), is(command.getIconUrl()));
    assertThat(created.isAutoComplete(), is(command.isAutoComplete()));
    assertThat(created.getAutoCompleteDesc(), is(command.getAutoCompleteDesc()));
    assertThat(created.getAutoCompleteHint(), is(command.getAutoCompleteHint()));
  }

  @Test
  public void testCommands_ListCommandsForTeam() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    Command command = new Command();
    {
      command.setTeamId(teamId);
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    ApiResponse<CommandList> response = assertNoError(client.listCommands(teamId));
    List<Command> commands = response.readEntity();

    assertThat(commands.size(), is(greaterThanOrEqualTo(1)));
    assertThat(commands.stream().map(Command::getId).collect(Collectors.toSet()),
        hasItem(command.getId()));
  }

  @Test
  public void testCommands_ListCommandForTeamExcludeSystemCommands() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    Command command = new Command();
    {
      command.setTeamId(teamId);
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://ur)");
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    ApiResponse<CommandList> response = assertNoError(client.listCommands(teamId, true));
    List<Command> commands = response.readEntity();

    assertThat(commands.size(), is(1));
    assertThat(commands.stream().map(Command::getId).collect(Collectors.toSet()),
        hasItem(command.getId()));
  }

  @Test
  public void testCommands_GetAutoCompleteCommands() {
    th.loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    Command autoCompleteCmd = new Command();
    autoCompleteCmd.setAutoComplete(true);
    autoCompleteCmd.setAutoCompleteDesc(th.newRandomString(32));
    autoCompleteCmd.setAutoCompleteHint(th.newRandomString(32));
    {
      autoCompleteCmd.setTeamId(teamId);
      autoCompleteCmd.setMethod(CommandMethod.GET);
      autoCompleteCmd.setTrigger("trigger1");
      autoCompleteCmd.setUrl("http://url1");
      autoCompleteCmd = assertNoError(client.createCommand(autoCompleteCmd)).readEntity();
    }
    Command noCompleteCmd = new Command();
    {
      noCompleteCmd.setTeamId(teamId);
      noCompleteCmd.setMethod(CommandMethod.GET);
      noCompleteCmd.setTrigger("trigger2");
      noCompleteCmd.setUrl("http://url2");
      noCompleteCmd = assertNoError(client.createCommand(noCompleteCmd)).readEntity();
    }
    th.logout();

    th.loginBasic();
    ApiResponse<CommandList> response = assertNoError(client.listAutocompleteCommands(teamId));
    List<Command> commandList = response.readEntity();

    assertThat(commandList.stream().map(Command::getId).collect(Collectors.toSet()),
        hasItem(autoCompleteCmd.getId()));
  }

  @Test
  public void testCommands_UpdateCommand() {
    th.logout().loginTeamAdmin();
    Command command = new Command();
    {
      command.setTeamId(th.basicTeam().getId());
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command.setUserName(th.newRandomString(8));
      command.setIconUrl("http://icon-url");
      command.setAutoComplete(true);
      command.setAutoCompleteDesc(th.newRandomString(32));
      command.setAutoCompleteHint(th.newRandomString(32));
      command.setDisplayName(th.newRandomString(32));
      command.setDescription(th.newRandomString(32));
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    command.setMethod(CommandMethod.POST);
    command.setTrigger("new" + command.getTrigger());
    command.setUrl(command.getUrl() + "/new");
    command.setUserName("new" + command.getUserName());
    command.setIconUrl(command.getIconUrl() + "/new");
    // command.setAutoComplete(false);
    command.setAutoCompleteDesc("new" + command.getAutoCompleteDesc());
    command.setAutoCompleteHint("new" + command.getAutoCompleteHint());
    command.setDisplayName("new" + command.getDisplayName());
    command.setDescription("new" + command.getDescription());
    ApiResponse<Command> response = assertNoError(client.updateCommand(command));
    Command updated = response.readEntity();

    assertThat(updated.getId(), is(command.getId()));
    assertThat(updated.getMethod(), is(command.getMethod()));
    assertThat(updated.getTrigger(), is(command.getTrigger()));
    assertThat(updated.getUrl(), is(command.getUrl()));
    assertThat(updated.getUserName(), is(command.getUserName()));
    assertThat(updated.getIconUrl(), is(command.getIconUrl()));
    assertThat(updated.isAutoComplete(), is(command.isAutoComplete()));
    assertThat(updated.getAutoCompleteDesc(), is(command.getAutoCompleteDesc()));
    assertThat(updated.getAutoCompleteHint(), is(command.getAutoCompleteHint()));
    assertThat(updated.getDisplayName(), is(command.getDisplayName()));
    assertThat(updated.getDescription(), is(command.getDescription()));
  }

  @Test
  public void testCommands_DeleteCommand() {
    th.logout().loginTeamAdmin();
    Command command = new Command();
    {
      command.setTeamId(th.basicTeam().getId());
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    String commandId = command.getId();
    ApiResponse<Boolean> response = assertNoError(client.deleteCommand(commandId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
    assertThat(client.listCommands(th.basicTeam().getId(), true).readEntity().stream()
        .map(Command::getId).collect(Collectors.toSet()), not(hasItem(commandId)));
  }

  @Test
  public void testCommands_GenerateNewToken() {
    th.logout().loginTeamAdmin();
    Command command = new Command();
    {
      command.setTeamId(th.basicTeam().getId());
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command = assertNoError(client.createCommand(command)).readEntity();
    }
    String currentToken = command.getToken();
    String commandId = command.getId();

    ApiResponse<String> response = assertNoError(client.regenCommandToken(commandId));
    String newToken = response.readEntity();

    assertThat(newToken, is(not(currentToken)));
  }

  @Test
  public void testCommands_ExecuteCommand() {
    String inChannel = th.basicChannel().getId();
    String command = "/away";

    ApiResponse<CommandResponse> response =
        assertNoError(client.executeCommand(inChannel, command));
    CommandResponse commandResponse = response.readEntity();

    // "/away" command should return an ephemeral message.
    assertThat(commandResponse.getResponseType(), is(CommandResponseType.Ephemeral));
    assertThat(commandResponse.getText(), is(not(nullValue())));
  }

  @Test
  public void testHook_IncomingWebhook_Post() {
    th.logout().loginTeamAdmin();
    IncomingWebhook webhook = new IncomingWebhook();
    {
      String channelId = th.basicChannel().getId();
      String displayName = "webhook" + th.newId();
      String description = "description" + th.newId();
      webhook.setChannelId(channelId);
      webhook.setDisplayName(displayName);
      webhook.setDescription(description);
    }
    ApiResponse<IncomingWebhook> createWebhookResponse =
        assertNoError(client.createIncomingWebhook(webhook));
    webhook = createWebhookResponse.readEntity();
    String hookUrl = getApplicationUrl() + "/hooks/" + webhook.getId();

    IncomingWebhookRequest payload = new IncomingWebhookRequest();
    payload.setText("Hello Webhook World");
    IncomingWebhookClient webhookClient = new IncomingWebhookClient(hookUrl, Level.WARNING);

    ApiResponse<Boolean> response = webhookClient.postByIncomingWebhook(payload);

    assertNoError(response);
    assertFalse(response.hasError());
  }
}
