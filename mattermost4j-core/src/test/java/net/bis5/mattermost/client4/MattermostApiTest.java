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

import static net.bis5.mattermost.client4.Assertions.assertSameFile;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;
import com.vdurmont.semver4j.Semver.VersionDiff;
import fi.iki.elonen.NanoHTTPD;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import net.bis5.mattermost.client4.hook.IncomingWebhookClient;
import net.bis5.mattermost.client4.model.AnalyticsCategory;
import net.bis5.mattermost.client4.model.FileUploadResult;
import net.bis5.mattermost.client4.model.SearchEmojiRequest;
import net.bis5.mattermost.client4.model.UsersOrder.InChannel;
import net.bis5.mattermost.client4.model.UsersOrder.InTeam;
import net.bis5.mattermost.model.AnalyticsRow;
import net.bis5.mattermost.model.AnalyticsRows;
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
import net.bis5.mattermost.model.ClusterInfo;
import net.bis5.mattermost.model.Command;
import net.bis5.mattermost.model.CommandList;
import net.bis5.mattermost.model.CommandMethod;
import net.bis5.mattermost.model.CommandResponse;
import net.bis5.mattermost.model.CommandResponseType;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.ContentType;
import net.bis5.mattermost.model.Emoji;
import net.bis5.mattermost.model.EmojiList;
import net.bis5.mattermost.model.FileInfo;
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookList;
import net.bis5.mattermost.model.IncomingWebhookRequest;
import net.bis5.mattermost.model.OutgoingWebhook;
import net.bis5.mattermost.model.OutgoingWebhookList;
import net.bis5.mattermost.model.PluginManifest;
import net.bis5.mattermost.model.Plugins;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostImage;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;
import net.bis5.mattermost.model.PostSearchResults;
import net.bis5.mattermost.model.PostType;
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;
import net.bis5.mattermost.model.Reaction;
import net.bis5.mattermost.model.ReactionList;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.SamlCertificateStatus;
import net.bis5.mattermost.model.Session;
import net.bis5.mattermost.model.SessionList;
import net.bis5.mattermost.model.StatusList;
import net.bis5.mattermost.model.StatusType;
import net.bis5.mattermost.model.SwitchRequest;
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
import net.bis5.mattermost.model.TriggerWhen;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAccessToken;
import net.bis5.mattermost.model.UserAccessTokenList;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;
import net.bis5.mattermost.model.WebappPlugin;
import net.bis5.opengraph.models.OpenGraph;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Mattermost API call test
 * 
 * @author Takayuki Maruyama
 */
public class MattermostApiTest {

  private static final String APPLICATION = getApplicationUrl();
  private static final String INBUCKET_HOST = getInbucketHost();
  private static final String INBUCKET_PORT = getInbucketPort();
  private MattermostClient client;
  private static TestHelper th;

  private static final String EMOJI_GLOBE = "/noto-emoji_u1f310.png";
  private static final String EMOJI_CONSTRUCTION = "/noto-emoji_u1f6a7.png";

  private static String getApplicationUrl() {
    return getEnv("MATTERMOST_URL", "http://localhost:8065");
  }

  private static String getEnv(String varName, String defaultValue) {
    String val = System.getenv(varName);
    return val != null ? val : defaultValue;
  }

  private static String getInbucketHost() {
    return getEnv("INBUCKET_HOST", "localhost");
  }

  private static String getInbucketPort() {
    return getEnv("INBUCKET_PORT", "2500");
  }

  private String dummyHttpServerHost() {
    return getEnv("DUMMY_HTTP_SERVER_HOST", "localhost");
  }

  private int dummyHttpServerPort() {
    return Integer.valueOf(getEnv("DUMMY_HTTP_SERVER_PORT", "8075"));
  }

  private boolean useLocalDummyServer() {
    return Boolean.valueOf(getEnv("USE_LOCAL_DUMMY_SERVER", "true"));
  }


  @BeforeAll
  public static void initHelper() {
    th = new TestHelper(createNewClient()).setup();
  }

  private static MattermostClient createNewClient() {
    return MattermostClient.builder() //
        .url(APPLICATION) //
        .logLevel(Level.WARNING) //
        .build();
  }

  @BeforeEach
  public void setup() {
    client = createNewClient();
    th.changeClient(client).initBasic().useSmtp(INBUCKET_HOST, INBUCKET_PORT);
  }

  @AfterEach
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

  private boolean isNotSupportVersion(String minimumRequirement, ApiResponse<?> response) {
    Semver serverVersion = new Semver(response.getRawResponse().getHeaderString("X-Version-Id"));
    Semver requirement = new Semver(minimumRequirement);
    return serverVersion.compareTo(requirement) < 0;
  }

  private boolean isSupportVersion(String minimumRequirement, ApiResponse<?> response) {
    return !isNotSupportVersion(minimumRequirement, response);
  }

  private boolean isMajorMinorVersionMatches(String majorMinorVersion, ApiResponse<?> response) {
    Semver serverVersion = new Semver(response.getRawResponse().getHeaderString("X-Version-Id"));
    Semver majorMinorSemver = new Semver(majorMinorVersion, SemverType.LOOSE);
    return !EnumSet.of(VersionDiff.MAJOR, VersionDiff.MINOR)
        .contains(majorMinorSemver.diff(serverVersion));
  }

  private Path getResourcePath(String name) throws URISyntaxException {
    return Paths.get(getClass().getResource(name).toURI());
  }

  // Channels
  @Nested
  class ChannelsApiTest {

    @Test
    public void createChannel_Open_Required() {
      Channel channel =
          new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
      createChannel_Success(channel);
    }

    @Test
    public void createChannel_Open_All() {
      Channel channel =
          new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
      channel.setPurpose("purpose");
      channel.setHeader("header");
      createChannel_Success(channel);
    }

    @Test
    public void createChannel_Private_Required() {
      Channel channel =
          new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
      createChannel_Success(channel);
    }

    @Test
    public void createChannel_Private_All() {
      Channel channel =
          new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
      channel.setPurpose("purpose");
      channel.setHeader("header");
      createChannel_Success(channel);
    }

    @Test
    public void createChannel_Fail_Direct() {
      Channel channel =
          new Channel("DisplayName", "name", ChannelType.Direct, th.basicTeam().getId());

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

      ApiResponse<Channel> response =
          assertNoError(client.createDirectChannel(user1.getId(), user2.getId()));
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

      ApiResponse<Channel> response =
          assertNoError(client.createGroupChannel(user1.getId(), user2.getId(), user3.getId()));
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

      ApiResponse<ChannelList> response = assertNoError(client
          .getPublicChannelsByIdsForTeam(theTeam.getId(), channel1.getId(), channel2.getId()));
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

      ApiResponse<Channel> response =
          assertNoError(client.getChannelByName(channelName, th.basicTeam().getId(), null));
      Channel channel = response.readEntity();

      assertThat(channel.getId(), is(th.basicChannel().getId()));
    }

    @Test
    public void getChannelByName_ChannelNotFound() {
      String channelName = "fake-channel-name";

      assertStatus(client.getChannelByName(channelName, th.basicTeam().getId(), null),
          Status.NOT_FOUND);
    }

    @Test
    public void getChannelByNameAndTeamName() {
      String channelName = th.basicChannel().getName();
      String teamName = th.basicTeam().getName();

      ApiResponse<Channel> response =
          assertNoError(client.getChannelByNameForTeamName(channelName, teamName, null));
      Channel channel = response.readEntity();

      assertThat(channel.getId(), is(th.basicChannel().getId()));
    }

    @Test
    public void getChannelByNameAndTeamName_ChannelNotFound() {
      String channelName = "fake-channel-name";
      String teamName = th.basicTeam().getName();

      assertStatus(client.getChannelByNameForTeamName(channelName, teamName, null),
          Status.NOT_FOUND);
    }

    @Test
    public void getChannelByNameAndTeamName_TeamNotFound() {
      String channelName = "fake-channel-name";
      String teamName = "fake-team-name";

      assertStatus(client.getChannelByNameForTeamName(channelName, teamName, null),
          Status.NOT_FOUND);
    }

    @Test
    public void getChannelMembers() {
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
    public void addUserToChannel() {
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
    public void getChannelMembersByIds() {
      Channel channel = th.createPublicChannel();
      User user1 = th.createUser();
      User user2 = th.createUser();
      th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam())
          .linkUserToTeam(user2, th.basicTeam()).loginBasic();
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

      ApiResponse<ChannelMember> response =
          assertNoError(client.getChannelMember(channel.getId(), user.getId(), null));
      ChannelMember member = response.readEntity();

      assertThat(member.getChannelId(), is(channel.getId()));
      assertThat(member.getUserId(), is(user.getId()));
    }

    @Test
    public void removeUserFromChannel() {
      Channel channel = th.basicChannel();
      User user = th.basicUser2();

      // logged-in as basicUser

      ApiResponse<Boolean> response =
          assertNoError(client.removeUserFromChannel(channel.getId(), user.getId()));
      boolean result = response.readEntity().booleanValue();

      assertThat(result, is(true));
    }

    @Test
    public void updateChannelRoles() {
      User channelAdmin = th.basicUser();
      User channelUser = th.basicUser2();
      Channel channel = th.loginAs(channelAdmin).createPublicChannel();
      client.addChannelMember(channel.getId(), channelUser.getId());

      ApiResponse<Boolean> response = assertNoError(client.updateChannelRoles(channel.getId(),
          channelUser.getId(), Role.CHANNEL_ADMIN, Role.CHANNEL_USER));
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

      ApiResponse<ChannelViewResponse> response =
          assertNoError(client.viewChannel(user.getId(), view));
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

      ApiResponse<ChannelUnread> response =
          assertNoError(client.getChannelUnread(channel.getId(), user.getId()));
      ChannelUnread unread = response.readEntity();

      assertThat(unread.getChannelId(), is(channel.getId()));
    }

    /**
     * Test case for issue#99
     */
    @Test
    public void getChannelHasChannelMentionsProp() {
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
      channelNoReference =
          assertNoError(client.getChannel(channelNoReference.getId())).readEntity();
      assertThat(channelNoReference.getProps(), is(nullValue()));

      Channel channelOnBasicTeam = th.createPublicChannel();
      channelOnBasicTeam.setHeader("~public1, ~private, ~other-team");
      channelOnBasicTeam.setPurpose("~public2, ~private, ~other-team");
      assertNoError(client.updateChannel(channelOnBasicTeam));
      channelOnBasicTeam =
          assertNoError(client.getChannel(channelOnBasicTeam.getId())).readEntity();
      assertThat(channelOnBasicTeam.getProps().containsKey("channel_mentions"), is(true));
      @SuppressWarnings("unchecked")
      Map<String, Map<String, String>> channelMentions =
          (Map<String, Map<String, String>>) channelOnBasicTeam.getProps().get("channel_mentions");
      assertThat(channelMentions.containsKey("public1"), is(true));
      assertThat(channelMentions.get("public1").get("display_name"),
          is(channelPublic1.getDisplayName()));

    }


    @Test
    public void getDeletedChannels() {
      Channel deleted1 = th.createPublicChannel();
      Channel deleted2 = th.createPublicChannel();
      client.deleteChannel(deleted1.getId());
      client.deleteChannel(deleted2.getId());

      th.logout().loginTeamAdmin();
      ChannelList channels =
          assertNoError(client.getDeletedChannels(th.basicTeam().getId())).readEntity();
      Set<String> channelIds = channels.stream().map(Channel::getId).collect(Collectors.toSet());

      assertThat(channelIds, containsInAnyOrder(deleted1.getId(), deleted2.getId()));
    }

    @Test
    public void convertChannelToPrivate() {
      Channel publicChannel = th.createPublicChannel();
      String channelId = publicChannel.getId();

      th.logout().loginTeamAdmin();
      Channel privateChannel =
          assertNoError(client.convertChannelToPrivate(channelId)).readEntity();

      assertEquals(channelId, privateChannel.getId());
      assertEquals(ChannelType.Private, privateChannel.getType());
    }

  }

  // Users
  @Nested
  class UsersApiTest {
    @Test
    public void createUser() {
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
    public void getUsers() {
      ApiResponse<UserList> response = assertNoError(client.getUsers(Pager.of(0, 60), null));
      List<User> users = response.readEntity();

      assertThat(users, is(not(emptyIterable())));
    }

    @Test
    public void getUsers_InChannel() {
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
    public void getUsers_InChannel_Order() {
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
    public void getUsers_NotInChannel() {
      Set<String> notInChannelUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(),
          th.basicUser2().getId(), th.teamAdminUser().getId()));
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
    public void getUsers_InTeam() {
      User notInTeamUser = th.loginSystemAdmin().createUser();
      Set<String> inTeamUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(),
          th.basicUser2().getId(), th.teamAdminUser().getId()));
      th.loginBasic();

      ApiResponse<UserList> response =
          assertNoError(client.getUsersInTeam(th.basicTeam().getId(), Pager.of(0, 60), null));
      List<User> users = response.readEntity();

      Set<String> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
      assertThat(userIds, not(hasItems(notInTeamUser.getId())));
      assertThat(userIds, hasItems(inTeamUserIds.toArray(new String[0])));
    }

    @Test
    public void getUsers_InTeam_Order() {
      th.logout().loginSystemAdmin();
      User user1 = th.createUser("order1_" + th.newId());
      th.logout().loginAs(user1);
      Team team = th.createTeam();
      User user2 = th.createUser("order2_" + th.newId());
      User user3 = th.createUser("order3_" + th.newId());
      th.linkUserToTeam(user2, team);
      th.linkUserToTeam(user3, team);

      // create_at order : user3, user2, user1
      List<String> expectedIdsByCreateAt =
          Arrays.asList(user3.getId(), user2.getId(), user1.getId());
      // username order: user1, user2, user3
      List<String> expectedIdsByUsername =
          Arrays.asList(user1.getId(), user2.getId(), user3.getId());

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
    public void getUsers_NotInTeam() {
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
    public void getUsers_WithoutTeam() {
      User withoutTeamUser = th.loginSystemAdmin().createUser();
      th.loginSystemAdmin();

      ApiResponse<UserList> response =
          assertNoError(client.getUsersWithoutTeam(Pager.of(0, 60), null));
      List<User> users = response.readEntity();

      assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
          hasItem(withoutTeamUser.getId()));
    }

    @Test
    public void getUsersByIds() {

      ApiResponse<UserList> response =
          assertNoError(client.getUsersByIds(th.basicUser().getId(), th.basicUser2().getId()));
      List<User> users = response.readEntity();

      assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
          containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
    }

    @Test
    public void getUsersByUsernames() {

      ApiResponse<UserList> response = assertNoError(
          client.getUsersByUsernames(th.basicUser().getUsername(), th.basicUser2().getUsername()));
      List<User> users = response.readEntity();

      assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
          containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
    }

    @Test
    public void searchUsers() {
      UserSearch criteria = UserSearch.builder().term(th.basicUser().getUsername())
          .teamId(th.basicTeam().getId()).build();

      ApiResponse<UserList> response = assertNoError(client.searchUsers(criteria));
      List<User> users = response.readEntity();

      assertThat(users.stream().map(User::getUsername).collect(Collectors.toSet()),
          hasItem(th.basicUser().getUsername()));
    }

    @Test
    public void autocompleteUsers() {

      ApiResponse<UserAutocomplete> response =
          assertNoError(client.autocompleteUsers(th.basicUser().getUsername(), null));
      UserAutocomplete autocompleteUsers = response.readEntity();

      assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
          hasItem(th.basicUser().getId()));
    }

    @Test
    public void autocompleteUsers_InTeam() {

      ApiResponse<UserAutocomplete> response = assertNoError(client
          .autocompleteUsersInTeam(th.basicTeam().getId(), th.basicUser().getUsername(), null));
      UserAutocomplete autocompleteUsers = response.readEntity();

      assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
          hasItem(th.basicUser().getId()));
    }

    @Test
    public void autocompleteUsers_InChannel() {
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
    public void getUser() {
      String userId = th.basicUser().getId();

      ApiResponse<User> response = assertNoError(client.getUser(userId, null));
      User user = response.readEntity();

      assertThat(user.getId(), is(userId));
    }

    @Test
    public void updateUser() {
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
    public void deactivateUser() {
      th.loginSystemAdmin();
      String userId = th.basicUser().getId();

      ApiResponse<Boolean> response = assertNoError(client.deleteUser(userId));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void patchUser() {
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
    public void updateUserRoles() {
      th.loginSystemAdmin();

      ApiResponse<Boolean> response = assertNoError(
          client.updateUserRoles(th.basicUser().getId(), Role.SYSTEM_ADMIN, Role.SYSTEM_USER));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void updateUserActiveStatus() {
      th.logout().loginSystemAdmin();
      ApiResponse<Boolean> response =
          assertNoError(client.updateUserActive(th.basicUser().getId(), false));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void getUserProfileImage() throws FileNotFoundException, IOException {

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
    public void setUserProfileImage() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_GLOBE);

      ApiResponse<Boolean> response =
          assertNoError(client.setProfileImage(th.basicUser().getId(), image));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void deleteUserProfileImage() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_CONSTRUCTION);
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
    public void getUserByName() {
      String username = th.basicUser().getUsername();

      ApiResponse<User> response = assertNoError(client.getUserByUsername(username, null));
      User user = response.readEntity();

      assertThat(user.getId(), is(th.basicUser().getId()));
    }

    @Test
    public void resetPassword() {

      // invalid token
      assertStatus(client.resetPassword(th.newRandomString(64), "passwd"), Status.BAD_REQUEST);
    }

    @Test
    public void updateUserMfa() {

      // Enterprise Edition required
      assertStatus(client.updateUserMfa(th.basicUser().getId(), null, false),
          Status.NOT_IMPLEMENTED);
    }

    @Test
    public void generateMfaSecret() {
      th.loginSystemAdmin();

      // Enterprise Edition required
      assertStatus(client.generateMfaSecret(th.basicUser().getId()), Status.NOT_IMPLEMENTED);
    }

    @Test
    public void checkMfa() {
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
    public void checkMfaThrowsExceptionDisableLegacyMfa() {
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
    public void updateUserPassword() {
      String userId = th.basicUser().getId();
      String currentPassword = th.basicUser().getPassword();
      String newPassword = "new" + currentPassword;

      ApiResponse<Boolean> response =
          assertNoError(client.updateUserPassword(userId, currentPassword, newPassword));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void sendPasswordResetEmail() {
      String email = th.basicUser().getEmail();

      ApiResponse<Boolean> response = assertNoError(client.sendPasswordResetEmail(email));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void getUserByEmail() {
      String email = th.basicUser().getEmail();

      ApiResponse<User> response = assertNoError(client.getUserByEmail(email, null));
      User user = response.readEntity();

      assertThat(user.getId(), is(th.basicUser().getId()));
    }

    @Test
    public void getUserSessions() {
      String userId = th.basicUser().getId();

      ApiResponse<SessionList> response = assertNoError(client.getSessions(userId, null));
      List<Session> sessions = response.readEntity();

      assertThat(sessions.stream().findAny().map(Session::getUserId).get(), is(userId));
    }

    @Test
    public void revokeUserSession() {
      ApiResponse<SessionList> response =
          assertNoError(client.getSessions(th.basicUser().getId(), null));
      Session session = response.readEntity().stream().findAny().get();

      ApiResponse<Boolean> response2 =
          assertNoError(client.revokeSession(session.getUserId(), session.getId()));
      boolean result = response2.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void attachMobileDevice() {
      ApiResponse<Boolean> response = assertNoError(client.attachDeviceId(th.newId()));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void getAudits() {

      ApiResponse<Audits> response =
          assertNoError(client.getUserAudits(th.basicUser().getId(), Pager.of(0, 50), null));
      Audits audits = response.readEntity();

      assertThat(audits.stream().findAny().map(Audit::getId).get(), is(not(nullValue())));
    }

    @Test
    public void verifyEmail() {

      // invalid token
      assertStatus(client.verifyUserEmail(th.newId()), Status.BAD_REQUEST);
    }

    @Test
    public void sendVerificationEmail() {

      ApiResponse<Boolean> response =
          assertNoError(client.sendVerificationEmail(th.basicUser().getEmail()));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void switchLoginMethod() {
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
      assertNoError(
          client.updateUserRoles(userId, Role.SYSTEM_USER_ACCESS_TOKEN, Role.SYSTEM_USER));
      th.logout().loginBasic();
    }

    @Test
    public void createUserAccessToken() {
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
    public void getUserAccessTokens() {
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
    public void getUserAccessTokensAllUsers() {
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
    public void revokeUserAccessToken() {
      String userId = th.basicUser().getId();
      setupUserAccessTokenRolesForNormalUser(userId);
      String description = userId;
      UserAccessToken uat =
          assertNoError(client.createUserAccessToken(userId, description)).readEntity();

      ApiResponse<Boolean> revokeResponse =
          assertNoError(client.revokeUserAccessToken(uat.getId()));

      assertTrue(revokeResponse.readEntity());
    }

    @Test
    public void getUserAccessToken() {
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
    public void disableUserAccessToken() {
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
    public void enableUserAccessToken() {
      String userId = th.basicUser().getId();
      setupUserAccessTokenRolesForNormalUser(userId);
      String description = userId;
      UserAccessToken uat =
          assertNoError(client.createUserAccessToken(userId, description)).readEntity();
      assertNoError(client.disableUserAccessToken(uat.getId()));
      uat = assertNoError(client.getUserAccessToken(uat.getId())).readEntity();
      assertFalse(uat.isActive());

      ApiResponse<Boolean> enableResponse =
          assertNoError(client.enableUserAccessToken(uat.getId()));

      assertTrue(enableResponse.readEntity());
    }

    @Test
    public void searchTokens() {
      setupUserAccessTokenRolesForNormalUser(th.basicUser().getId());
      setupUserAccessTokenRolesForNormalUser(th.basicUser2().getId());
      setupUserAccessTokenRolesForNormalUser(th.teamAdminUser().getId());
      client.createUserAccessToken(th.basicUser().getId(), th.basicUser().getId()).readEntity();
      th.logout().loginBasic2();
      UserAccessToken user2TokenA = client
          .createUserAccessToken(th.basicUser2().getId(), th.basicUser2().getId()).readEntity();
      UserAccessToken user2TokenB = client
          .createUserAccessToken(th.basicUser2().getId(), th.basicUser2().getId()).readEntity();
      th.logout().loginSystemAdmin();

      String term = th.basicUser2().getUsername();

      UserAccessTokenList foundTokens = assertNoError(client.searchTokens(term)).readEntity();

      assertThat(foundTokens.stream().map(UserAccessToken::getId).collect(Collectors.toSet()),
          containsInAnyOrder(user2TokenA.getId(), user2TokenB.getId()));
    }

    @Test
    public void revokeAllActiveSessionForUser() {
      User targetUser = th.basicUser2();
      try (MattermostClient theUserClient = createNewClient()) {
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

  // Teams
  @Nested
  class TeamsApiTest {
    @Test
    public void createTeam() {
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
    public void getTeams() {
      Team team = th.loginSystemAdmin().createTeam();

      ApiResponse<TeamList> response =
          assertNoError(client.getAllTeams(Pager.of(0, Integer.MAX_VALUE), null));
      List<Team> teams = response.readEntity();

      assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
          hasItem(team.getId()));
    }

    @Test
    public void getTeam() {

      ApiResponse<Team> response = assertNoError(client.getTeam(th.basicTeam().getId(), null));
      Team team = response.readEntity();

      assertThat(team.getId(), is(th.basicTeam().getId()));
    }

    @Test
    public void updateTeam() {
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
    public void deleteTeam_Soft() {
      th.loginSystemAdmin();

      ApiResponse<Boolean> response = assertNoError(client.deleteTeam(th.basicTeam().getId()));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void deleteTeam_Permanent() {
      th.loginSystemAdmin();

      ApiResponse<Boolean> response =
          assertNoError(client.deleteTeam(th.basicTeam().getId(), true));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void patchTeam() {
      th.loginTeamAdmin();
      TeamPatch patch = new TeamPatch();
      final String newDisplayName = "new" + th.basicTeam().getDisplayName();
      patch.setDisplayName(newDisplayName);

      ApiResponse<Team> response = assertNoError(client.patchTeam(th.basicTeam().getId(), patch));
      Team team = response.readEntity();

      assertThat(team.getDisplayName(), is(newDisplayName));
    }

    @Test
    public void getTeamByName() {
      final String teamId = th.basicTeam().getId();
      final String teamName = th.basicTeam().getName();

      ApiResponse<Team> response = assertNoError(client.getTeamByName(teamName, null));
      Team team = response.readEntity();

      assertThat(team.getId(), is(teamId));
    }

    @Test
    public void searchTeams() {
      TeamSearch search = new TeamSearch();
      search.setTerm(th.basicTeam().getName());

      ApiResponse<TeamList> response = assertNoError(client.searchTeams(search));
      List<Team> teams = response.readEntity();

      assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
          hasItem(th.basicTeam().getId()));
    }

    @Test
    public void teamExists_Exists() {

      ApiResponse<TeamExists> response =
          assertNoError(client.teamExists(th.basicTeam().getName(), null));
      TeamExists exists = response.readEntity();

      assertThat(exists.isExists(), is(true));
    }

    @Test
    public void teamExists_NotExists() {

      ApiResponse<TeamExists> response =
          assertNoError(client.teamExists("fake" + th.generateTestTeamName(), null));
      TeamExists exists = response.readEntity();

      assertThat(exists.isExists(), is(false));
    }

    @Test
    public void getUsersTeams() {
      String userId = th.basicUser().getId();

      ApiResponse<TeamList> response = assertNoError(client.getTeamsForUser(userId, null));
      List<Team> teams = response.readEntity();

      assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()),
          hasItem(th.basicTeam().getId()));
    }

    @Test
    public void getTeamMembers() {

      ApiResponse<TeamMemberList> response =
          assertNoError(client.getTeamMembers(th.basicTeam().getId(), Pager.of(0, 60), null));
      List<TeamMember> teamMembers = response.readEntity();

      assertThat(teamMembers.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
          hasItems(th.basicUser().getId(), th.basicUser2().getId()));
    }

    @Test
    public void addUserToTeam() {
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
    @Disabled // API for 4.0+
    public void addUserToTeamFromInvite() {

      assertStatus(client.addTeamMember("hash", "dataToHash", "inviteId"), Status.BAD_REQUEST);
    }

    @Test
    public void addMultipleUsersToTeam() {
      th.loginSystemAdmin();
      User user1 = th.createUser();
      User user2 = th.createUser();
      th.loginTeamAdmin();

      ApiResponse<TeamMemberList> response = assertNoError(
          client.addTeamMembers(th.basicTeam().getId(), user1.getId(), user2.getId()));
      List<TeamMember> members = response.readEntity();

      assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
          containsInAnyOrder(user1.getId(), user2.getId()));
    }

    @Test
    public void getTeamMembersForUser() {

      ApiResponse<TeamMemberList> response =
          assertNoError(client.getTeamMembersForUser(th.basicUser().getId(), null));
      List<TeamMember> members = response.readEntity();

      assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
          hasItems(th.basicUser().getId()));
    }

    @Test
    public void getTeamMember() {
      String teamId = th.basicTeam().getId();
      String userId = th.basicUser2().getId();

      ApiResponse<TeamMember> response = assertNoError(client.getTeamMember(teamId, userId, null));
      TeamMember member = response.readEntity();

      assertThat(member.getTeamId(), is(teamId));
      assertThat(member.getUserId(), is(userId));
    }

    @Test
    public void removeUserFromTeam() {
      th.loginTeamAdmin();
      String teamId = th.basicTeam().getId();
      String userId = th.basicUser2().getId();

      ApiResponse<Boolean> response = assertNoError(client.removeTeamMember(teamId, userId));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void getTeamMembersByIds() {

      ApiResponse<TeamMemberList> response =
          assertNoError(client.getTeamMembersByIds(th.basicTeam().getId(), th.basicUser().getId(),
              th.basicUser2().getId()));
      List<TeamMember> members = response.readEntity();

      assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
          hasItems(th.basicUser().getId(), th.basicUser2().getId()));
    }

    @Test
    public void getTeamStats() {

      ApiResponse<TeamStats> response =
          assertNoError(client.getTeamStats(th.basicTeam().getId(), null));
      TeamStats stats = response.readEntity();

      assertThat(stats.getTeamId(), is(th.basicTeam().getId()));
    }

    @Test
    public void setTeamIcon() throws URISyntaxException {
      th.logout().loginTeamAdmin();
      Path iconPath = getResourcePath(EMOJI_CONSTRUCTION);
      String teamId = th.basicTeam().getId();

      ApiResponse<Boolean> response = assertNoError(client.setTeamIcon(teamId, iconPath));
      assertTrue(response.readEntity());
    }

    @Test
    public void getTeamIcon() throws URISyntaxException, IOException {
      th.logout().loginTeamAdmin();
      Path iconPath = getResourcePath(EMOJI_CONSTRUCTION);
      String teamId = th.basicTeam().getId();
      assertNoError(client.setTeamIcon(teamId, iconPath));

      ApiResponse<Path> response = assertNoError(client.getTeamIcon(teamId));
      assertTrue(response.readEntity().toFile().exists());
    }

    @Test
    public void removeTeamIcon() throws URISyntaxException {
      th.logout().loginTeamAdmin();
      Path iconPath = getResourcePath(EMOJI_CONSTRUCTION);
      String teamId = th.basicTeam().getId();
      assertNoError(client.setTeamIcon(teamId, iconPath));

      ApiResponse<Boolean> response = assertNoError(client.removeTeamIcon(teamId));
      assertTrue(response.readEntity());
    }

    @Test
    public void updateTeamMemberRoles() {
      th.loginTeamAdmin();

      ApiResponse<Boolean> response = assertNoError(client
          .updateTeamMemberRoles(th.basicTeam().getId(), th.basicUser().getId(), Role.TEAM_ADMIN));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void getTeamUnreadsForUser() {

      ApiResponse<TeamUnreadList> response =
          assertNoError(client.getTeamUnreadForUser(th.basicUser().getId(), null));
      List<TeamUnread> unreads = response.readEntity();

      unreads.stream().findFirst()
          .ifPresent(u -> assertThat(u.getTeamId(), is(th.basicTeam().getId())));
    }

    @Test
    public void getTeamUnreadsForTeam() {

      ApiResponse<TeamUnread> response =
          assertNoError(client.getTeamUnread(th.basicTeam().getId(), th.basicUser().getId()));
      TeamUnread unread = response.readEntity();

      assertThat(unread.getTeamId(), is(th.basicTeam().getId()));
    }

    @Test
    public void tnviteUsersToTheTeamByEmail() {

      ApiResponse<Boolean> response = assertNoError(client.inviteUsersToTeam(th.basicTeam().getId(),
          Collections.singletonList(th.generateTestEmail())));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    @Disabled // Not Implemented
    public void importTeamFromOtherApplication() {}

    @Test
    public void getPublicChannels() {

      ApiResponse<ChannelList> response = assertNoError(
          client.getPublicChannelsForTeam(th.basicTeam().getId(), Pager.of(0, 60), null));
      List<Channel> channels = response.readEntity();

      assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
    }

    @Test
    public void searchChannels() {
      ChannelSearch search = new ChannelSearch();
      search.setTerm(th.basicChannel().getName());

      ApiResponse<ChannelList> response =
          assertNoError(client.searchChannels(th.basicTeam().getId(), search));
      List<Channel> channels = response.readEntity();

      assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
    }

    @Test
    public void getInviteInfo() {
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

  // Posts
  @Nested
  class PostsApiTest {
    @Test
    public void createPost() {
      Post post = new Post();
      post.setChannelId(th.basicChannel().getId());
      post.setMessage("Hello World!");

      ApiResponse<Post> response = assertNoError(client.createPost(post));
      Post postedPost = response.readEntity();

      assertThat(postedPost.getMessage(), is(post.getMessage()));
      assertThat(postedPost.getChannelId(), is(post.getChannelId()));
    }

    @Test
    public void createEphemeralPost() {
      // create_post_ephemeral permission currently only given to system admin
      th.logout().loginSystemAdmin();

      Post post = new Post();
      post.setChannelId(th.basicChannel().getId());
      post.setMessage("Hello World!");

      ApiResponse<Post> response =
          assertNoError(client.createEphemeralPost(th.basicUser().getId(), post));
      Post postedPost = response.readEntity();

      assertThat(postedPost.getMessage(), is(post.getMessage()));
      assertThat(postedPost.getChannelId(), is(post.getChannelId()));
      assertThat(postedPost.getType(), is(PostType.EPHEMERAL));
    }

    @Test
    public void getPost() {
      String postId = th.basicPost().getId();

      ApiResponse<Post> response = assertNoError(client.getPost(postId, null));
      Post post = response.readEntity();

      assertThat(post.getId(), is(postId));
    }

    @Test
    public void getPostHasEmbedImage() throws IOException, URISyntaxException {
      String channelId = th.basicChannel().getId();
      Post post = new Post();
      post.setChannelId(channelId);
      post.setMessage(
          "logo file from: https://github.com/hmhealey/test-files/raw/master/logoVertical.png");

      ApiResponse<Post> response = assertNoError(client.createPost(post));
      if (isNotSupportVersion("5.8.0", response)) {
        return;
      }
      Post createdPost = response.readEntity();
      response = assertNoError(client.getPost(createdPost.getId()));
      createdPost = response.readEntity();

      PostImage image = createdPost.getMetadata().getImages().values().stream().findFirst().get();
      assertNotEquals(0, image.getWidth());
      assertNotEquals(0, image.getHeight());
      if (isSupportVersion("5.11.0", response)) {
        assertEquals("png", image.getFormat());
        assertEquals(0, image.getFrameCount()); // for gif: number of frames, other format: 0
      }
    }

    @Test
    public void deletePost() {
      String postId = th.createPost(th.basicChannel()).getId();

      ApiResponse<Boolean> response = assertNoError(client.deletePost(postId));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void updatePost() {
      Post post = th.createPost(th.basicChannel());
      post.setMessage("UPDATE:" + post.getMessage());

      ApiResponse<Post> response = assertNoError(client.updatePost(post.getId(), post));
      Post updatedPost = response.readEntity();

      assertThat(updatedPost.getMessage(), is(post.getMessage()));
    }

    @Test
    public void patchPost() {
      String postId = th.createPost(th.basicChannel()).getId();
      PostPatch patch = new PostPatch();
      patch.setMessage("NEW MESSAGE");

      ApiResponse<Post> response = assertNoError(client.patchPost(postId, patch));
      Post updatedPost = response.readEntity();

      assertThat(updatedPost.getMessage(), is(patch.getMessage()));
    }

    @Test
    public void getThread() {
      String postId = th.basicPost().getId();

      ApiResponse<PostList> response = assertNoError(client.getPostThread(postId, null));
      PostList posts = response.readEntity();

      assertThat(posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet()),
          hasItem(postId));
    }

    @Test
    public void getFlaggedPosts() {
      Post post = th.basicPost();

      // XXX "Flag post" operation need make more simple?
      Preferences prefs = new Preferences();
      Preference flag = new Preference();
      flag.setUserid(th.basicUser().getId());
      flag.setCategory(PreferenceCategory.FLAGGED_POST);
      flag.setName(post.getId());
      flag.setValue(Boolean.toString(true));
      prefs.add(flag);
      assertNoError(client.updatePreferences(th.basicUser().getId(), prefs));

      PostList flaggedPosts =
          assertNoError(client.getFlaggedPostsForUser(th.basicUser().getId())).readEntity();
      assertThat(flaggedPosts.getOrder().contains(post.getId()), is(true));

      flaggedPosts = assertNoError(
          client.getFlaggedPostsForUserInChannel(th.basicUser().getId(), post.getChannelId()))
              .readEntity();
      assertThat(flaggedPosts.getOrder().contains(post.getId()), is(true));

      flaggedPosts = assertNoError(
          client.getFlaggedPostsForUserInTeam(th.basicUser().getId(), th.basicTeam().getId()))
              .readEntity();
      assertThat(flaggedPosts.getOrder().contains(post.getId()), is(true));
    }


    @Test
    public void getFileInfoForPost() throws IOException, URISyntaxException {
      Path file1 = getResourcePath(EMOJI_CONSTRUCTION);
      Path file2 = getResourcePath(EMOJI_GLOBE);
      String channelId = th.basicChannel().getId();
      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, file1, file2)).readEntity();
      FileInfo fileInfo1 = uploadResult.getFileInfos()[0];
      FileInfo fileInfo2 = uploadResult.getFileInfos()[1];

      Post post = new Post(channelId, "file attached");
      post.setFileIds(Arrays.asList(fileInfo1.getId(), fileInfo2.getId()));
      post = assertNoError(client.createPost(post)).readEntity();
      String postId = post.getId();

      FileInfo[] fileInfosForPost = assertNoError(client.getFileInfoForPost(postId)).readEntity();
      assertEquals(2, fileInfosForPost.length);
      Set<String> fileIds = new HashSet<>(Arrays.asList(fileInfo1.getId(), fileInfo2.getId()));
      assertAll(() -> fileIds.contains(fileInfosForPost[0].getId()),
          () -> fileIds.contains(fileInfosForPost[1].getId()));
    }

    @Test
    public void getPostsForChannel() {
      String channelId = th.basicChannel().getId();

      ApiResponse<PostList> response =
          assertNoError(client.getPostsForChannel(channelId, Pager.of(0, 60), null));
      PostList posts = response.readEntity();

      assertThat(
          posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
          hasItem(channelId));
    }

    @Test
    public void getPostsForChannel_Since() {
      String channelId = th.basicChannel().getId();
      ZonedDateTime since = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

      ApiResponse<PostList> response =
          assertNoError(client.getPostsSince(channelId, since.toEpochSecond()));
      PostList posts = response.readEntity();

      assertThat(
          posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
          hasItem(channelId));
    }

    @Test
    public void getPostsForChannel_Before() {
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
    public void getPostsForChannel_After() {
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
    public void searchForTeamPosts() {
      String teamId = th.basicTeam().getId();
      String channelId = th.basicChannel().getId();
      Post post1 = new Post(channelId, "hello");
      Post post2 = new Post(channelId, "mattermost");
      Post post3 = new Post(channelId, "world");
      post1 = assertNoError(client.createPost(post1)).readEntity();
      post2 = assertNoError(client.createPost(post2)).readEntity();
      post3 = assertNoError(client.createPost(post3)).readEntity();

      ApiResponse<PostSearchResults> response =
          assertNoError(client.searchPosts(teamId, "hello", false));
      PostList posts = response.readEntity();

      assertThat(posts.getPosts().keySet(), hasItem(post1.getId()));
      assertThat(posts.getPosts().keySet(), not(hasItems(post2.getId(), post3.getId())));

      response = assertNoError(client.searchPosts(teamId, "hello world", true));
      posts = response.readEntity();

      assertThat(posts.getPosts().keySet(), hasItems(post1.getId(), post3.getId()));
      assertThat(posts.getPosts().keySet(), not(hasItem(post2.getId())));
    }

    @Test
    public void pinPostToChannel() {
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
    public void unPinPost() {
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
  }

  // Preferences
  @Nested
  class PreferencesApiTest {
    @Test
    public void getPreferences() {
      String userId = th.basicUser().getId();

      ApiResponse<Preferences> response = assertNoError(client.getPreferences(userId));
      Preferences preferences = response.readEntity();

      assertThat(preferences.isEmpty(), is(false));
      assertThat(preferences.stream().map(Preference::getUserid).collect(Collectors.toSet()),
          containsInAnyOrder(userId));
    }

    @Test
    public void savePreferences() {
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
    public void deletePreference() {
      String userId = th.basicUser().getId();
      Preferences currentPreferences = client.getPreferences(userId).readEntity();
      Preference tutorial = currentPreferences.stream()
          .filter(p -> p.getCategory() == PreferenceCategory.TUTORIAL_STEPS).findAny().get();

      Preferences deleteTargets = new Preferences();
      deleteTargets.add(tutorial);
      ApiResponse<Boolean> response =
          assertNoError(client.deletePreferences(userId, deleteTargets));
      boolean result = response.readEntity();

      assertThat(result, is(true));
    }

    @Test
    public void getPreferencesByCategory() {
      String userId = th.basicUser().getId();
      PreferenceCategory category = PreferenceCategory.TUTORIAL_STEPS;

      ApiResponse<Preferences> response =
          assertNoError(client.getPreferencesByCategory(userId, category));
      Preferences preferences = response.readEntity();

      assertThat(preferences.isEmpty(), is(false));
    }

    @Test
    public void getPreference() {
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
  }

  // Emoji
  @Nested
  class EmojiApiTest {
    @Test
    public void createCustomEmoji() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      String emojiName = "custom" + th.newId();
      emoji.setName(emojiName);
      emoji.setCreatorId(th.basicUser().getId());

      ApiResponse<Emoji> response = client.createEmoji(emoji, image);
      if (isNotSupportVersion("5.4.0", response)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      Emoji createdEmoji = assertNoError(response).readEntity();

      assertThat(createdEmoji.getName(), is(emojiName));
      assertThat(createdEmoji.getId(), is(not(nullValue())));
    }

    @Test
    public void getCustomEmojiList() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_GLOBE);
      Emoji emoji1 = new Emoji();
      emoji1.setName("custom" + th.newId());
      emoji1.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> resp1 = client.createEmoji(emoji1, image);
      if (isNotSupportVersion("5.4.0", resp1)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji1 = assertNoError(resp1).readEntity();
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
    public void getCustomEmoji() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      emoji.setName("custom" + th.newId());
      emoji.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> resp1 = client.createEmoji(emoji, image);
      if (isNotSupportVersion("5.4.0", resp1)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji = assertNoError(resp1).readEntity();
      String emojiId = emoji.getId();

      ApiResponse<Emoji> response = assertNoError(client.getEmoji(emojiId));
      Emoji responseEmoji = response.readEntity();

      assertThat(responseEmoji.getName(), is(emoji.getName()));
    }

    @Test
    public void deleteCustomEmoji() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      emoji.setName("custom" + th.newId());
      emoji.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> resp1 = client.createEmoji(emoji, image);
      if (isNotSupportVersion("5.4.0", resp1)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji = assertNoError(resp1).readEntity();
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
    public void getCustomEmojiImage() throws URISyntaxException, IOException {
      Path originalImage = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      emoji.setName("custom" + th.newId());
      emoji.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> response = client.createEmoji(emoji, originalImage);
      if (isNotSupportVersion("5.4.0", response)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji = assertNoError(response).readEntity();
      String emojiId = emoji.getId();

      ApiResponse<Path> emojiImage = assertNoError(client.getEmojiImage(emojiId));
      Path downloadedFile = emojiImage.readEntity();

      // file contents equals?
      assertSameFile(originalImage, downloadedFile);
    }

    @Test
    public void getCustomEmojiByName() throws URISyntaxException {
      Path image = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      emoji.setName("custom" + th.newId());
      emoji.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> resp1 = client.createEmoji(emoji, image);
      if (isNotSupportVersion("5.4.0", resp1)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji = assertNoError(resp1).readEntity();
      String emojiName = emoji.getName();

      ApiResponse<Emoji> response = assertNoError(client.getEmojiByName(emojiName));
      Emoji responseEmoji = response.readEntity();

      assertEquals(emoji.getId(), responseEmoji.getId());
    }

    @Test
    public void searchEmoji() throws URISyntaxException {
      Path emojiGlobe = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      emoji.setName("customGlobe" + th.newId());
      emoji.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> createEmojiResponse = client.createEmoji(emoji, emojiGlobe);
      if (isNotSupportVersion("5.4.0", createEmojiResponse)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji = assertNoError(createEmojiResponse).readEntity();

      SearchEmojiRequest criteria = SearchEmojiRequest.builder().term("Globe").build();

      ApiResponse<EmojiList> searchResponse = assertNoError(client.searchEmoji(criteria));
      EmojiList searchResult = searchResponse.readEntity();
      assertEquals(1, searchResult.size());
      assertEquals(emoji.getId(), searchResult.get(0).getId());

      criteria = SearchEmojiRequest.builder().term("Globe").prefixOnly(true).build();
      searchResponse = assertNoError(client.searchEmoji(criteria));
      searchResult = searchResponse.readEntity();
      assertTrue(searchResult.isEmpty());
    }

    @Test
    public void autocompleteEmoji() throws URISyntaxException {
      Path emojiGlobe = getResourcePath(EMOJI_GLOBE);
      Emoji emoji = new Emoji();
      emoji.setName("customAutocompleteGlobe" + th.newId());
      emoji.setCreatorId(th.basicUser().getId());
      ApiResponse<Emoji> createEmojiResponse = client.createEmoji(emoji, emojiGlobe);
      if (isNotSupportVersion("5.4.0", createEmojiResponse)) {
        // CreateEmoji call fail between 4.8 and 5.3
        return;
      }
      emoji = assertNoError(createEmojiResponse).readEntity();

      String name = "customAuto";
      ApiResponse<EmojiList> autocompleteResponse = assertNoError(client.autocompleteEmoji(name));
      EmojiList autocompleteList = autocompleteResponse.readEntity();

      assertEquals(1, autocompleteList.size());
      assertEquals(emoji.getId(), autocompleteList.get(0).getId());
    }

  }

  // Webhooks
  @Nested
  class WebhooksApiTest {
    @Test
    public void createIncomingWebhook() {
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
    public void listIncomingWebhooks() {
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
    public void listIncomingWebhooksForTeam() {
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
    public void getIncomingWebhook() {
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
    public void updateIncomingWebhook() {
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
    public void deleteIncomingWebhook() {
      th.logout().loginTeamAdmin();
      String channelId = th.basicChannel().getId();
      IncomingWebhook webhook = new IncomingWebhook();
      {
        webhook.setChannelId(channelId);
        webhook.setDisplayName(th.newRandomString(32));
        webhook.setDescription(th.newRandomString(32));
        webhook = assertNoError(client.createIncomingWebhook(webhook)).readEntity();
      }

      ApiResponse<Boolean> deleteResult =
          assertNoError(client.deleteIncomingWebhook(webhook.getId()));

      assertTrue(deleteResult.readEntity());
    }

    @Test
    public void createOutgoingWebhook() {
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
      assertThat(created.getTriggerWords(),
          containsInAnyOrder(webhook.getTriggerWords().toArray()));
      assertThat(created.getTriggerWhen(), is(webhook.getTriggerWhen()));
      assertThat(created.getCallbackUrls(),
          containsInAnyOrder(webhook.getCallbackUrls().toArray()));
      assertThat(created.getContentType(), is(webhook.getContentType()));
    }

    @Test
    public void listOutgoingWebhooks() {
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
    public void listOutgoingWebhooksForTeam() {
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
    public void listOutgoingWebhooksForChannel() {
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
        otherChannelHook =
            assertNoError(client.createOutgoingWebhook(otherChannelHook)).readEntity();

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
    public void getOutgoingWebhook() {
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
    public void deleteOutgoingWebhook() {
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
    public void updateOutgoingWebhook() {
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
    public void regenerateOutgoingWebhookToken() {
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
  }

  // Commands
  @Nested
  class CommandsApiTest {
    @Test
    public void createCommand() {
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
    public void listCommandsForTeam() {
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
    public void listCommandForTeamExcludeSystemCommands() {
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
    public void getAutoCompleteCommands() {
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
    public void updateCommand() {
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
    public void deleteCommand() {
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
    public void generateNewToken() {
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
    public void executeCommand() {
      String inChannel = th.basicChannel().getId();
      String command = "/away";

      ApiResponse<CommandResponse> response =
          assertNoError(client.executeCommand(inChannel, command));
      CommandResponse commandResponse = response.readEntity();

      // "/away" command should return an ephemeral message.
      assertThat(commandResponse.getResponseType(), is(CommandResponseType.Ephemeral));
      assertThat(commandResponse.getText(), is(not(nullValue())));
    }
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

  // System
  @Nested
  class SystemApiTest {
    @Test
    public void databaseRecycle() {
      th.logout().loginSystemAdmin();

      ApiResponse<Boolean> result = assertNoError(client.databaseRecycle());
      assertTrue(result.readEntity());
    }

    @Test
    public void getAnalytics() {
      th.logout().loginSystemAdmin();
      AnalyticsRows analyticsRows = assertNoError(client.getAnalytics()).readEntity();

      assertFalse(analyticsRows.isEmpty());
      assertThat(analyticsRows.get(0).getValue(), is(not(nullValue())));
    }

    @Test
    public void getAnalyticsSpecifiedCategory() {
      th.logout().loginSystemAdmin();
      AnalyticsRows analyticsRows =
          assertNoError(client.getAnalytics(AnalyticsCategory.EXTRA_COUNTS)).readEntity();

      assertFalse(analyticsRows.isEmpty());
      Set<String> rowNames = analyticsRows.stream() //
          .map(AnalyticsRow::getName) //
          .map(String::toLowerCase) //
          .collect(Collectors.toSet());
      assertTrue(rowNames.contains("session_count"));
      assertFalse(rowNames.contains("user_counts_with_posts_day"));
    }

    @Test
    public void getAnalyticsSpecifiedTeam() {
      th.logout().loginSystemAdmin();
      Team basicTeam = th.basicTeam();
      AnalyticsRows analyticsRows =
          assertNoError(client.getAnalytics(basicTeam.getId())).readEntity();

      assertFalse(analyticsRows.isEmpty());
      AnalyticsRow userCountRow = analyticsRows.stream() //
          .filter(r -> r.getName().equals("unique_user_count")) //
          .findAny().get();
      // BasicTeam has 3 users. see TestHelper
      assertThat(userCountRow.getValue().intValue(), is(3));

      // AdditionalTeam has 1 user (team creator).
      Team additionalTeam = th.createTeam();
      analyticsRows = assertNoError(client.getAnalytics(additionalTeam.getId())).readEntity();

      userCountRow = analyticsRows.stream() //
          .filter(r -> r.getName().equals("unique_user_count")) //
          .findAny().get();
      assertThat(userCountRow.getValue().intValue(), is(1));
    }

    @Test
    public void getOldClientConfig() {
      ApiResponse<Map<String, String>> response = assertNoError(client.getOldClientConfig());
      Map<String, String> clientConfig = response.readEntity();
      assertTrue(clientConfig.containsKey("Version"));
    }

    @Test
    public void getOldClientLicense() {
      ApiResponse<Map<String, String>> response = assertNoError(client.getOldClientLicense());
      Map<String, String> clientLicense = response.readEntity();
      assertTrue(clientLicense.containsKey("IsLicensed"));
    }

    @Test
    public void ping() {
      ApiResponse<Boolean> result = assertNoError(client.getPing());
      assertTrue(result.readEntity());
    }

    @Test
    public void invalidateCache() {
      th.logout().loginSystemAdmin();

      ApiResponse<Boolean> result = assertNoError(client.invalidateCaches());
      assertTrue(result.readEntity());
    }

    @Test
    public void reloadConfig() {
      th.logout().loginSystemAdmin();

      ApiResponse<Boolean> result = assertNoError(client.reloadConfig());
      assertTrue(result.readEntity());
    }

    @Test
    public void testEmail() {
      th.useSmtp(INBUCKET_HOST, INBUCKET_PORT).logout().loginSystemAdmin();

      ApiResponse<Boolean> result = assertNoError(client.testEmail());
      assertTrue(result.readEntity());
    }

    @Test
    public void uploadLicenseFile() throws IOException {
      Path licenseFile = Files.createTempFile(null, null); // invalid contents
      licenseFile.toFile().deleteOnExit();

      th.logout().loginBasic();
      assertStatus(client.uploadLicenseFile(licenseFile), Status.FORBIDDEN);

      th.logout().loginSystemAdmin();
      assertStatus(client.uploadLicenseFile(licenseFile), Status.BAD_REQUEST);
    }

    @Test
    public void removeLicense() {
      th.logout().loginBasic();
      assertStatus(client.removeLicense(), Status.FORBIDDEN);

      th.logout().loginSystemAdmin();
      assertNoError(client.removeLicense());
    }
  }

  // Status
  @Nested
  class StatusApiTest {
    @Test
    public void getUserStatus() {
      String userId = th.basicUser().getId();
      assertNoError(client.viewChannel(userId, new ChannelView(th.basicChannel().getId())));

      net.bis5.mattermost.model.Status status =
          assertNoError(client.getUserStatus(userId)).readEntity();

      assertEquals(StatusType.ONLINE.getCode(), status.getStatus());
    }

    @Test
    public void getUserStatusesByIds() {
      String loginUserId = th.basicUser().getId();
      assertNoError(client.viewChannel(loginUserId, new ChannelView(th.basicChannel().getId())));
      String otherUserId = th.basicUser2().getId();

      List<String> userIds = Arrays.asList(loginUserId, otherUserId);
      StatusList statuses = assertNoError(client.getUsersStatusesByIds(userIds)).readEntity();

      assertEquals(2, statuses.size());
      for (net.bis5.mattermost.model.Status status : statuses) {
        if (status.getUserId().equals(loginUserId)) {
          assertEquals(StatusType.ONLINE.getCode(), status.getStatus());
        } else {
          assertEquals(StatusType.OFFLINE.getCode(), status.getStatus());
        }
      }
    }

    @Test
    public void updateUserStatus() {
      String userId = th.basicUser().getId();
      net.bis5.mattermost.model.Status newStatus = new net.bis5.mattermost.model.Status();
      newStatus.setUserId(userId);
      newStatus.setStatus(StatusType.DND.getCode());

      newStatus = assertNoError(client.updateUserStatus(userId, newStatus)).readEntity();

      assertEquals(StatusType.DND.getCode(), newStatus.getStatus());
    }
  }

  // Reaction
  @Nested
  class ReactionApiTest {

    @Test
    public void saveReaction() {
      Post targetPost = th.basicPost();
      User reactedUser = th.basicUser();
      Reaction reaction = new Reaction();
      reaction.setUserId(reactedUser.getId());
      reaction.setPostId(targetPost.getId());
      reaction.setEmojiName("mattermost");

      Reaction savedReaction = assertNoError(client.saveReaction(reaction)).readEntity();

      assertNotEquals(0l, savedReaction.getCreateAt());
    }

    @Test
    public void deleteReaction() {
      Post targetPost = th.basicPost();
      User reactedUser = th.basicUser();
      Reaction reaction = new Reaction();
      reaction.setUserId(reactedUser.getId());
      reaction.setPostId(targetPost.getId());
      reaction.setEmojiName("mattermost");
      reaction = assertNoError(client.saveReaction(reaction)).readEntity();

      ApiResponse<Boolean> deleteReactionResult = assertNoError(client.deleteReaction(reaction));

      assertTrue(deleteReactionResult.readEntity());
    }

    @Test
    public void getReactions() {
      Post targetPost = th.basicPost();
      User reactedUser = th.basicUser();
      Reaction reaction = new Reaction();
      reaction.setUserId(reactedUser.getId());
      reaction.setPostId(targetPost.getId());
      reaction.setEmojiName("mattermost");
      assertNoError(client.saveReaction(reaction));
      reaction.setEmojiName("+1");
      assertNoError(client.saveReaction(reaction));

      ReactionList reactions = assertNoError(client.getReactions(targetPost.getId())).readEntity();

      assertEquals(2, reactions.size());
      assertThat(reactions.stream().map(Reaction::getEmojiName).collect(Collectors.toSet()),
          containsInAnyOrder("mattermost", "+1"));
    }

  }

  // Brand
  @Nested
  class BrandApiTest {
    @Test
    public void getBrandImageForNotEmpty() throws URISyntaxException, IOException {
      th.logout().loginSystemAdmin();
      Path brandImage = getResourcePath(EMOJI_GLOBE);
      ApiResponse<Boolean> uploadResponse = client.uploadBrandImage(brandImage);
      if (isNotSupportVersion("5.0.0", uploadResponse)) {
        return;
      }
      assertNoError(uploadResponse);
      assertTrue(uploadResponse.readEntity());
      th.logout().loginBasic();

      assertNoError(client.getBrandImage());
    }

    @Test
    public void uploadBrandImage() throws URISyntaxException {
      th.logout().loginSystemAdmin();
      Path brandImage = getResourcePath(EMOJI_GLOBE);

      ApiResponse<Boolean> uploadResponse = client.uploadBrandImage(brandImage);
      if (isNotSupportVersion("5.0.0", uploadResponse)) {
        return;
      }
      assertNoError(uploadResponse);

      assertTrue(uploadResponse.readEntity());
    }

    @Test
    public void deleteBrandImage() throws URISyntaxException {
      th.logout().loginSystemAdmin();
      Path brandImage = getResourcePath(EMOJI_GLOBE);
      ApiResponse<Boolean> uploadResponse = client.uploadBrandImage(brandImage);
      if (isNotSupportVersion("5.6.0", uploadResponse)) {
        return;
      }

      ApiResponse<Boolean> deleteResponse = assertNoError(client.deleteBrandImage());

      assertTrue(deleteResponse.readEntity());
    }
  }

  // Files
  @Nested
  class FilesApiTest {

    @Test
    public void uploadNoFileThrowsException() {
      String channelId = th.basicChannel().getId();
      assertThrows(IllegalArgumentException.class, () -> client.uploadFile(channelId));
    }

    @Test
    public void uplaodFile() throws URISyntaxException, IOException {
      Path filePath = getResourcePath(EMOJI_GLOBE);
      String channelId = th.basicChannel().getId();

      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, filePath)).readEntity();

      assertEquals(1, uploadResult.getFileInfos().length);
      assertEquals(filePath.getFileName().toString(), uploadResult.getFileInfos()[0].getName());
    }

    @Test
    public void uploadMultipleFile() throws URISyntaxException, IOException {
      Path file1 = getResourcePath(EMOJI_GLOBE);
      Path file2 = getResourcePath(EMOJI_CONSTRUCTION);
      String channelId = th.basicChannel().getId();

      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, file1, file2)).readEntity();

      assertEquals(2, uploadResult.getFileInfos().length);
      boolean foundFile1 = false;
      boolean foundFile2 = false;
      for (FileInfo fileInfo : uploadResult.getFileInfos()) {
        foundFile1 = foundFile1 || fileInfo.getName().equals(file1.getFileName().toString());
        foundFile2 = foundFile2 || fileInfo.getName().equals(file2.getFileName().toString());
      }
      assertTrue(foundFile1);
      assertTrue(foundFile2);
    }

    @Test
    public void getFile() throws URISyntaxException, IOException {
      Path filePath = getResourcePath("/LICENSE.txt");
      String channelId = th.basicChannel().getId();
      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, filePath)).readEntity();
      FileInfo fileInfo = uploadResult.getFileInfos()[0];
      String fileId = fileInfo.getId();

      Path receivedFile = assertNoError(client.getFile(fileId)).readEntity();

      assertSameFile(filePath, receivedFile);
    }

    @Test
    public void getFileThumbnail() throws URISyntaxException, IOException {
      Path filePath = getResourcePath(EMOJI_GLOBE);
      String channelId = th.basicChannel().getId();
      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, filePath)).readEntity();
      FileInfo fileInfo = uploadResult.getFileInfos()[0];
      String fileId = fileInfo.getId();

      Path thumbnail = assertNoError(client.getFileThumbnail(fileId)).readEntity();

      assertTrue(thumbnail.toFile().exists());
    }

    @Test
    public void getFilePreview() throws URISyntaxException, IOException {
      Path filePath = getResourcePath(EMOJI_GLOBE);
      String channelId = th.basicChannel().getId();
      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, filePath)).readEntity();
      FileInfo fileInfo = uploadResult.getFileInfos()[0];
      String fileId = fileInfo.getId();

      Path preview = assertNoError(client.getFilePreview(fileId)).readEntity();

      assertTrue(preview.toFile().exists());
    }

    @Test
    public void getPublicFileLink() throws URISyntaxException, IOException {
      Path filePath = getResourcePath(EMOJI_GLOBE);
      String channelId = th.basicChannel().getId();
      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, filePath)).readEntity();
      FileInfo fileInfo = uploadResult.getFileInfos()[0];
      String fileId = fileInfo.getId();

      Post post = new Post(channelId, "file attached post");
      post.setFileIds(Collections.singletonList(fileId));
      post = assertNoError(client.createPost(post)).readEntity();

      String publicLink = assertNoError(client.getPublicFileLink(fileId)).readEntity();

      Client jaxrsClient = ClientBuilder.newClient();
      WebTarget target = jaxrsClient.target(publicLink);
      Response response = target.request().get();
      InputStream downloadFile = response.readEntity(InputStream.class);
      Path tempFile = Files.createTempFile(null, null);
      Files.copy(downloadFile, tempFile, StandardCopyOption.REPLACE_EXISTING);

      assertSameFile(filePath, tempFile);
    }

    @Test
    public void getFileMetadata() throws URISyntaxException, IOException {
      Path filePath = getResourcePath(EMOJI_GLOBE);
      String channelId = th.basicChannel().getId();
      FileUploadResult uploadResult =
          assertNoError(client.uploadFile(channelId, filePath)).readEntity();
      FileInfo fileInfo = uploadResult.getFileInfos()[0];
      String fileId = fileInfo.getId();

      FileInfo metadata = assertNoError(client.getFileMetadata(fileId)).readEntity();
      assertEquals(filePath.getFileName().toString(), metadata.getName());
    }

  }

  @Nested
  class ClusterApiTest {
    @Test
    public void getClusterStatus() {
      th.logout().loginSystemAdmin();

      // In Team Edition, return empty list.
      ClusterInfo[] result = assertNoError(client.getClusterStatus()).readEntity();
      assertEquals(0, result.length);
    }
  }

  @Nested
  class ElasticsearchApiTest {
    @Test
    public void testElasticsearchConfig() {
      th.logout().loginSystemAdmin();

      // Enterprise Edition required
      assertStatus(client.testElasticsearchConfiguration(), Status.NOT_IMPLEMENTED);
    }

    @Test
    public void purgeElasticsearchIndexes() {
      th.logout().loginSystemAdmin();

      // Enterprise Edition required
      assertStatus(client.purgeElasticsearchIndexes(), Status.NOT_IMPLEMENTED);
    }
  }

  private static Path simpleLockPluginArchivePath;
  private static Path drawPluginArchivePath;

  @BeforeAll
  public static void setupPluginArchivePath() throws IOException {
    InputStream resource = MattermostApiTest.class //
        .getResourceAsStream("/net.bis5.mattermost.simplelock-0.0.1.tar.gz");
    simpleLockPluginArchivePath = Files.createTempFile(null, null);
    IOUtils.copy(resource,
        Files.newOutputStream(simpleLockPluginArchivePath, StandardOpenOption.TRUNCATE_EXISTING));

    resource = MattermostApiTest.class //
        .getResourceAsStream("/com.mattermost.draw-plugin-0.0.1.tar.gz");
    drawPluginArchivePath = Files.createTempFile(null, null);
    IOUtils.copy(resource,
        Files.newOutputStream(drawPluginArchivePath, StandardOpenOption.TRUNCATE_EXISTING));
  }

  @AfterAll
  public static void deletePluginArchive() {
    if (simpleLockPluginArchivePath != null) {
      simpleLockPluginArchivePath.toFile().delete();
    }
    if (drawPluginArchivePath != null) {
      drawPluginArchivePath.toFile().delete();
    }
  }

  @Nested
  class PluginApiTest {
    @Test
    public void getPlugins() {
      th.logout().loginSystemAdmin();

      ApiResponse<Plugins> response = assertNoError(client.getPlugins());
      // 5.2.0 is server plugin release candidate version
      if (isNotSupportVersion("5.2.0", response)) {
        return;
      }
      Plugins plugins = response.readEntity();

      assertAll(() -> {
        Optional<PluginManifest> zoomPlugin = Arrays.asList(plugins.getInactive()).stream()
            .filter(p -> p.getId().equals("zoom")).findFirst();
        assertTrue(zoomPlugin.isPresent(), "zoom");
      }, () -> {
        Optional<PluginManifest> jiraPlugin = Arrays.asList(plugins.getInactive()).stream()
            .filter(p -> p.getId().equals("jira")).findFirst();
        assertTrue(jiraPlugin.isPresent(), "jira");
      });
    }

    @Test
    public void uploadPlugin() {
      th.logout().loginSystemAdmin();
      ApiResponse<Plugins> response = client.getPlugins();
      // 5.2.0 is server plugin release candidate version
      if (isNotSupportVersion("5.2.0", response)) {
        return;
      }

      ApiResponse<PluginManifest> uploadResult =
          assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
      uploadResult.readEntity();

      // same plugin id is already uploaded
      ApiResponse<PluginManifest> blockedUploadResult =
          client.uploadPlugin(simpleLockPluginArchivePath);
      assertStatus(blockedUploadResult, Status.BAD_REQUEST);
      assertTrue(
          blockedUploadResult.readError().getMessage().contains("A plugin with the same ID"));

      if (!isNotSupportVersion("5.8.0", blockedUploadResult)) {
        // force upload
        ApiResponse<PluginManifest> forceUploadResult =
            assertNoError(client.uploadPlugin(simpleLockPluginArchivePath, true));
        forceUploadResult.readEntity();
      }

      // cleanup
      client.removePlugin(uploadResult.readEntity().getId());
    }

    @Test
    public void removePlugin() {
      th.logout().loginSystemAdmin();
      ApiResponse<Plugins> response = client.getPlugins();
      // 5.2.0 is server plugin release candidate version
      if (isNotSupportVersion("5.2.0", response)) {
        return;
      }
      ApiResponse<PluginManifest> uploadResult =
          assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
      PluginManifest plugin = uploadResult.readEntity();
      String pluginId = plugin.getId();

      ApiResponse<Boolean> removeResult = assertNoError(client.removePlugin(pluginId));

      assertTrue(removeResult.readEntity());

      // cleanup
      client.removePlugin(pluginId);
    }

    @Test
    public void enablePlugin() {
      th.logout().loginSystemAdmin();
      ApiResponse<Plugins> response = client.getPlugins();
      // 5.2.0 is server plugin release candidate version
      if (isNotSupportVersion("5.2.0", response)) {
        return;
      }
      ApiResponse<PluginManifest> uploadResult =
          assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
      PluginManifest plugin = uploadResult.readEntity();
      String pluginId = plugin.getId();

      ApiResponse<Boolean> enableResult = assertNoError(client.enablePlugin(pluginId));

      assertTrue(enableResult.readEntity());

      // cleanup
      client.removePlugin(pluginId);
    }

    @Test
    public void disablePlugin() {
      th.logout().loginSystemAdmin();
      ApiResponse<Plugins> response = client.getPlugins();
      // 5.2.0 is server plugin release candidate version
      if (isNotSupportVersion("5.2.0", response)) {
        return;
      }
      ApiResponse<PluginManifest> uploadResult =
          assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
      PluginManifest plugin = uploadResult.readEntity();
      String pluginId = plugin.getId();
      assertNoError(client.enablePlugin(pluginId));

      ApiResponse<Boolean> disableResult = assertNoError(client.disablePlugin(pluginId));

      assertTrue(disableResult.readEntity());

      // cleanup
      client.removePlugin(pluginId);
    }

    @Test
    public void getWebappPlugins() {
      th.logout().loginSystemAdmin();
      ApiResponse<Plugins> response = client.getPlugins();
      // 5.2.0 is server plugin release candidate version
      if (isNotSupportVersion("5.2.0", response)) {
        return;
      }
      ApiResponse<PluginManifest> uploadResult =
          assertNoError(client.uploadPlugin(drawPluginArchivePath));
      PluginManifest plugin = uploadResult.readEntity();
      String pluginId = plugin.getId();
      assertNoError(client.enablePlugin(pluginId));

      ApiResponse<WebappPlugin[]> webappResponse = assertNoError(client.getWebappPlugins());
      WebappPlugin[] webapps = webappResponse.readEntity();

      assertEquals(1, webapps.length);
      WebappPlugin drawWebapp = webapps[0];
      assertEquals(pluginId, drawWebapp.getId());

      // cleanup
      client.removePlugin(pluginId);
    }

  }

  @Nested
  class SamlApiTest {
    // Note: Team Edition does not support SAML

    @Test
    public void getSamlMetadata() throws IOException {
      th.logout().loginSystemAdmin();

      ApiResponse<Path> response = client.getSamlMetadata();

      assertStatus(response, Status.NOT_IMPLEMENTED);
    }

    @Test
    public void uploadSamlIdpCertificate() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".crt");
      String fileName = file.getName(file.getNameCount() - 1).toString();

      ApiResponse<Boolean> response = client.uploadSamlIdpCertificate(file, fileName);

      assertTrue(response.readEntity());
    }

    @Test
    public void uploadSamlPublicCertificate() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".crt");
      String fileName = file.getName(file.getNameCount() - 1).toString();

      ApiResponse<Boolean> response = client.uploadSamlPublicCertificate(file, fileName);

      assertTrue(response.readEntity());
    }

    @Test
    public void uploadSamlPrivateCertificate() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".key");
      String fileName = file.getName(file.getNameCount() - 1).toString();

      ApiResponse<Boolean> response = client.uploadSamlPrivateCertificate(file, fileName);

      assertTrue(response.readEntity());
    }

    @Test
    public void deleteSamlIdpCertificate() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".crt");
      String fileName = file.getName(file.getNameCount() - 1).toString();
      assertNoError(client.uploadSamlIdpCertificate(file, fileName));

      ApiResponse<Boolean> response = client.deleteSamlIdpCertificate();

      assertTrue(response.readEntity());
    }

    @Test
    public void deleteSamlPublicCertificate() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".crt");
      String fileName = file.getName(file.getNameCount() - 1).toString();
      assertNoError(client.uploadSamlPublicCertificate(file, fileName));

      ApiResponse<Boolean> response = client.deleteSamlPublicCertificate();

      assertTrue(response.readEntity());
    }

    @Test
    public void deleteSamlPrivateCertificate() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".key");
      String fileName = file.getName(file.getNameCount() - 1).toString();
      assertNoError(client.uploadSamlPrivateCertificate(file, fileName));

      ApiResponse<Boolean> response = client.deleteSamlPrivateCertificate();

      assertTrue(response.readEntity());
    }

    @Test
    public void getSamlCertificateStatus() throws IOException {
      th.logout().loginSystemAdmin();

      Path file = Files.createTempFile("", ".key");
      String fileName = file.getName(file.getNameCount() - 1).toString();
      assertNoError(client.uploadSamlPrivateCertificate(file, fileName));

      ApiResponse<SamlCertificateStatus> response = client.getSamlCertificateStatus();

      // Note: 5.10/5.11 server returns true unless upload certificate

      if (isMajorMinorVersionMatches("5.10", response)
          || isMajorMinorVersionMatches("5.11", response)) {
        return;
      }

      SamlCertificateStatus status = response.readEntity();
      assertAll(() -> assertFalse(status.isIdpCertificateFile()),
          () -> assertFalse(status.isPublicCertificateFile()),
          () -> assertTrue(status.isPrivateKeyFile()));
    }
  }

  @Nested
  class LdapApiTest {
    @Test
    public void syncLdap() {
      th.logout().loginSystemAdmin();

      // Enterprise Edition required
      // Note: Server >= 5.11 returns 200 OK
      ApiResponse<Boolean> response = client.syncLdap();
      if (isSupportVersion("5.12.0", response)) {
        assertStatus(response, Status.NOT_IMPLEMENTED);
      }
    }

    @Test
    public void testLdap() {
      th.logout().loginSystemAdmin();

      // Enterprise Edition required
      // Note: Server >= 5.11 returns 200 OK
      ApiResponse<Boolean> response = client.testLdap();
      if (isSupportVersion("5.12.0", response)) {
        assertStatus(response, Status.NOT_IMPLEMENTED);
      }
    }
  }

  @Nested
  class OpenGraphApiTest {

    NanoHTTPD server;

    @BeforeEach
    public void setupServer() throws IOException {
      if (!useLocalDummyServer()) {
        return;
      }
      server = new NanoHTTPD("0.0.0.0", dummyHttpServerPort()) {
        @Override
        public Response serve(IHTTPSession session) {
          List<String> contents =
              Arrays.asList("<html><head>", "<meta property=\"og:type\" content=\"article\" />",
                  "<meta property=\"og:title\" content=\"The Great WebSite\" />",
                  "<meta property=\"og:url\" content=\"http://localhost:8888/\" />",
                  "</head><body>Hello World!</body></html>");
          return newFixedLengthResponse(
              StringUtils.join(contents, StringUtils.CR + StringUtils.LF));
        }
      };
      server.start();
    }

    @AfterEach
    public void tearDownServer() {
      if (!useLocalDummyServer()) {
        return;
      }
      server.stop();
    }

    @Test
    public void getMetadata() {
      th.logout().loginSystemAdmin();
      Config config = client.getConfig().readEntity();
      config.getServiceSettings().setAllowedUntrustedInternalConnections(
          config.getServiceSettings().getAllowedUntrustedInternalConnections() + " "
              + dummyHttpServerHost());
      assertNoError(client.updateConfig(config));
      th.logout().loginBasic();

      int port = dummyHttpServerPort();
      String url = "http://" + dummyHttpServerHost() + (port != 0 ? ":" + port : "");

      ApiResponse<OpenGraph> response = client.getOpenGraphMetadata(url);

      OpenGraph og = response.readEntity();
      assertNotNull(og);
    }
  }


}
