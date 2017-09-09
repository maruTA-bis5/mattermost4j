/// *
// * @(#) net.bis5.mattermost.client4.MattermostClientTest
// * Copyright (c) 2017 Maruyama Takayuki <bis5.wsys@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
// package net.bis5.mattermost.client4;
//
// import static
/// net.bis5.mattermost.client4.MattermostClientTest.HasError.hasError;
// import static org.hamcrest.CoreMatchers.hasItem;
// import static org.hamcrest.CoreMatchers.is;
// import static org.hamcrest.CoreMatchers.not;
// import static org.hamcrest.CoreMatchers.nullValue;
// import static org.junit.Assert.assertThat;
// import static org.junit.Assert.fail;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
// import java.util.concurrent.ExecutionException;
//
// import javax.ws.rs.core.Response;
// import javax.ws.rs.core.Response.Status;
//
// import org.hamcrest.BaseMatcher;
// import org.hamcrest.Description;
// import org.hamcrest.Matcher;
// import org.junit.After;
// import org.junit.Before;
// import org.junit.BeforeClass;
// import org.junit.Ignore;
// import org.junit.Test;
//
// import net.bis5.mattermost.client4.model.ApiError;
// import net.bis5.mattermost.model.Channel;
// import net.bis5.mattermost.model.ChannelList;
// import net.bis5.mattermost.model.ChannelMember;
// import net.bis5.mattermost.model.ChannelMembers;
// import net.bis5.mattermost.model.ChannelPatch;
// import net.bis5.mattermost.model.ChannelSearch;
// import net.bis5.mattermost.model.ChannelStats;
// import net.bis5.mattermost.model.ChannelType;
// import net.bis5.mattermost.model.ChannelUnread;
// import net.bis5.mattermost.model.ChannelView;
// import net.bis5.mattermost.model.ClusterInfo;
// import net.bis5.mattermost.model.Command;
// import net.bis5.mattermost.model.CommandMethod;
// import net.bis5.mattermost.model.Config;
// import net.bis5.mattermost.model.Post;
// import net.bis5.mattermost.model.PostList;
// import net.bis5.mattermost.model.Role;
// import net.bis5.mattermost.model.Team;
// import net.bis5.mattermost.model.User;
// import net.bis5.mattermost.model.config.consts.Permissions;
//
/// **
// * TODO 型の説明
// *
// * @author Maruyama Takayuki
// * @since 2017/06/19
// */
// public class MattermostClientTest {
//
// private static final String APPLICATION = getApplicationUrl();
// private MattermostClient client;
// private static TestHelper th;
//
// private static String getApplicationUrl() {
// String url = System.getenv("MATTERMOST_URL");
// return url != null ? url : "http://localhost:8065";
// }
//
// @BeforeClass
// public static void initHelper() throws InterruptedException,
/// ExecutionException {
// th = new TestHelper(new MattermostClient(APPLICATION)).setup();
// }
//
// @Before
// public void setup() throws InterruptedException, ExecutionException {
// client = new MattermostClient(APPLICATION);
// th.changeClient(client).initBasic();
// }
//
// @After
// public void tearDown() throws InterruptedException, ExecutionException {
// try {
// th.logout();
// } catch (InterruptedException | ExecutionException ex) {
// // avoid errors
// }
// }
//
// @Test
// public void testPing() throws InterruptedException, ExecutionException {
// client.getPing().toCompletableFuture().get();
// }
//
// // brand_test.go
//
// @Test
// @Ignore // FIXME
// public void testGetBrandImage() throws InterruptedException,
/// ExecutionException {
// Object data = client.getBrandImage().toCompletableFuture().get();
// assertThat(data, is(not(nullValue())));
// }
//
// @Test
// @Ignore // FIXME
// public void testGetBrandImage_noLogin() throws InterruptedException,
/// ExecutionException {
// th.logout();
//
// Object data = client.getBrandImage().toCompletableFuture().get();
//
// assertThat(data, is(nullValue()));
// }
//
// @Test
// @Ignore // FIXME
// public void testUploadBrandImage() {
// fail(); // FIXME
// }
//
// // channel_test.go
//
// private <T> ApiResponse<T> checkNoError(ApiResponse<T> response) {
// return th.checkNoError(response);
// }
//
// private <T> ApiResponse<T> checkStatus(ApiResponse<T> response, Status
/// status) {
// Response rawResponse = response.getRawResponse();
//
// assertThat(rawResponse.getStatus(), is(status.getStatusCode()));
//
// return response;
// }
//
// private <T> ApiResponse<T> checkErrorMessage(ApiResponse<T> response, String
/// message) {
// ApiError error = response.readError();
//
// assertThat("incorrect error message", error.getId(), is(message));
//
// return response;
// }
//
// @Test
// public void testCreateChannel() throws InterruptedException,
/// ExecutionException {
// Team team = th.basicTeam();
//
// Channel channel = new Channel("Test API Name", th.generateTestChannelName(),
/// ChannelType.Open, team.getId());
//
// Channel resultChannel = client.createChannel(channel)
// .thenApply(this::checkNoError)
// .thenApply(r -> checkStatus(r, Status.CREATED))
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// // assertThat("names did not match", resultChannel.getName(),
// // is(channel.getName()));
// // assertThat("display names did not match",
// // resultChannel.getDisplayName(), is(channel.getDisplayName()));
// // assertThat("team ids did not match", resultChannel.getTeamId(),
// // is(channel.getTeamId()));
//
// Channel privateChannel = new Channel("Test API Name",
/// th.generateTestChannelName(), ChannelType.Private,
// team.getId());
//
// Channel resultPriv = client.createChannel(privateChannel)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// // assertThat("names did not match", resultPriv.getName(),
// // is(privateChannel.getName()));
// // assertThat("wrong channel type", resultPriv.getType(),
// // is(ChannelType.Private));
// // assertThat("wrong creator id", resultPriv.getCreatorId(),
// // is(th.basicUser().getId()));
//
// client.createChannel(channel)
// .thenApply(r -> checkErrorMessage(r,
/// "store.sql_channel.save_channel.exists.app_error"))
// .toCompletableFuture().get();
//
// Channel direct = new Channel("Test API Name", th.generateTestChannelName(),
/// ChannelType.Direct, team.getId());
// client.createChannel(direct)
// .thenApply(r -> checkErrorMessage(r,
/// "api.channel.create_channel.direct_channel.app_error"))
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// th.logout();
// client.createChannel(channel)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User userNotOnTeam = th.createUser();
// client.login(userNotOnTeam.getEmail(),
/// userNotOnTeam.getPassword()).toCompletableFuture().get();
//
// client.createChannel(channel)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.createChannel(privateChannel)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.loginBasic();
//
// // TODO Check permissions with policy config changes
//
// }
//
// @Test
// public void testUpdateChannel() throws InterruptedException,
/// ExecutionException {
// Team team = th.basicTeam();
// Channel channel = new Channel("Test API Name", th.generateTestChannelName(),
/// ChannelType.Open, team.getId());
// Channel privChannel = new Channel("Test API Name",
/// th.generateTestChannelName(), ChannelType.Private,
// team.getId());
//
// channel = client.createChannel(channel)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// privChannel = client.createChannel(privChannel)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// // Update a open channel
// channel.setDisplayName("My new display name");
// channel.setHeader("My fancy header");
// channel.setPurpose("Mattermost ftw!");
//
// Channel newChannel = client.updateChannel(channel)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// // assertThat("Update failed for DisplayName",
// // newChannel.getDisplayName(), is(channel.getDisplayName()));
// // assertThat("Update failed for Header", newChannel.getHeader(),
// // is(channel.getHeader()));
// // assertThat("Update failed for Purpose", newChannel.getPurpose(),
// // is(channel.getPurpose()));
//
// // Update a private channel
// privChannel.setDisplayName("My new display name for private channel");
// privChannel.setHeader("My fancy private header");
// privChannel.setPurpose("Mattermost ftw! in private mode");
//
// Channel newPrivateChannel = client.updateChannel(privChannel)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// // assertThat("Update failed for DisplayName in private channel",
// // newPrivateChannel.getDisplayName(),
// // is(privChannel.getDisplayName()));
// // assertThat("Update failed for Header in private channel",
// // newPrivateChannel.getHeader(),
// // is(privChannel.getHeader()));
// // assertThat("Update failed for Purpose in private channel",
// // newPrivateChannel.getPurpose(),
// // is(privChannel.getPurpose()));
//
// // Non existing channel
// Channel nonExist = new Channel("Test API Name for apiv4",
/// th.generateTestChannelName(), ChannelType.Open,
// team.getId());
// client.updateChannel(nonExist)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// // Try to update with not logged user
// th.logout();
// client.updateChannel(channel)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// // Try to Update using another user
// User user = th.createUser();
// client.login(user.getEmail(),
/// user.getPassword()).toCompletableFuture().get();
// nonExist.setDisplayName("Should not update");
// client.updateChannel(channel)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
// }
//
// @Test
// public void testPatchChannel() throws InterruptedException,
/// ExecutionException {
// ChannelPatch patch = new ChannelPatch();
// patch.setName(th.newId());
// patch.setDisplayName(th.newId());
// patch.setHeader(th.newId());
// patch.setPurpose(th.newId());
//
// Channel channel = client.patchChannel(th.basicChannel().getId(), patch)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// assertThat("do not match", patch.getName(), is(channel.getName()));
// assertThat("do not match", patch.getDisplayName(),
/// is(channel.getDisplayName()));
// assertThat("do not match", patch.getHeader(), is(channel.getHeader()));
// assertThat("do not match", patch.getPurpose(), is(channel.getPurpose()));
//
// patch.setName(null);
// String oldName = channel.getName();
// channel = client.patchChannel(th.basicChannel().getId(), patch)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// assertThat("should not have updated", channel.getName(), is(oldName));
//
// client.patchChannel("junk", patch)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.patchChannel(th.newId(), patch)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// client.login(user.getEmail(),
/// user.getPassword()).toCompletableFuture().get();
// client.patchChannel(th.basicChannel().getId(), patch)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// th.loginSystemAdmin();
// client.patchChannel(th.basicChannel().getId(), patch)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.patchChannel(th.basicPrivateChannel().getId(), patch)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testCreateDirectChannel() throws InterruptedException,
/// ExecutionException {
// User user1 = th.basicUser();
// User user2 = th.basicUser2();
// User user3 = th.createUser();
//
// Channel dm = client.createDirectChannel(user1.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// String channelName;
// if (user1.getId().compareTo(user2.getId()) < 0) {
// channelName = user1.getId() + "__" + user2.getId();
// } else {
// channelName = user2.getId() + "__" + user1.getId();
// }
// assertThat("dm name didn't match", dm.getName(), is(channelName));
//
// client.createDirectChannel("junk", user2.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.createDirectChannel(user1.getId(), th.newId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.createDirectChannel(th.newId(), user1.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.createDirectChannel(th.newId(), user2.getId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.doApiPost("/channels/direct", "garbage")
// .thenApply(r -> r.getRawResponse())
// .thenAccept(r -> assertThat("erong status code", r.getStatus(),
/// is(Status.BAD_REQUEST.getStatusCode())))
// .toCompletableFuture().get();
//
// th.logout();
// client.createDirectChannel(th.newId(), user2.getId())
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.logout();
// th.loginSystemAdmin();
// client.createDirectChannel(user3.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testCreateGroupChannel() throws InterruptedException,
/// ExecutionException {
// User user = th.basicUser();
// User user2 = th.basicUser2();
// User user3 = th.createUser();
//
// Channel grpChannel = client.createGroupChannel(user.getId(), user2.getId(),
/// user3.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> checkStatus(r, Status.CREATED))
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// assertThat("should have create a group channel", grpChannel,
/// is(not(nullValue())));
// assertThat("should have created a channel of group type",
/// grpChannel.getType(), is(ChannelType.Group));
//
// // TODO **sなオブジェクトはIterableかつint size()を持たせる
// ChannelMembers members = client.getChannelMembers(grpChannel.getId(), 0, 10,
/// null)
// .thenApply(r -> {
// return r.readEntity();
// })
// .toCompletableFuture().get();
// assertThat("should have 3 channel members", members.size(), is(3));
//
// // saving duplicate group channel
// Channel grpChannel2 = client.createGroupChannel(user.getId(), user2.getId(),
/// user3.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should have returned existing channel", grpChannel.getId(),
/// is(grpChannel2.getId()));
//
// ChannelMembers members2 = client.getChannelMembers(grpChannel2.getId(), 0,
/// 10, null)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// // TODO reflect.DeepEqual相当のアサーション
//
// client.createGroupChannel(user2.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// User user4 = th.createUser();
// User user5 = th.createUser();
// User user6 = th.createUser();
// User user7 = th.createUser();
// User user8 = th.createUser();
// User user9 = th.createUser();
//
// client.createGroupChannel(user.getId(), user2.getId(), user3.getId(),
/// user4.getId(), user5.getId(),
// user6.getId(), user7.getId(), user8.getId(), user9.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
// // v BAD REQUESTだからreadEntityすると型が違って死ぬ
// // assertThat("should return null", grpChannel, is(nullValue()));
//
// client.createGroupChannel(user.getId(), user2.getId(), user3.getId(),
/// th.newId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.createGroupChannel(user.getId(), user2.getId(), user3.getId(), "junk")
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// th.logout();
//
// client.createGroupChannel(user.getId(), user2.getId(), user3.getId())
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.logout();
// th.loginSystemAdmin();
// client.createGroupChannel(user.getId(), user2.getId(), user3.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannel() throws InterruptedException, ExecutionException
/// {
// Channel channel = client.getChannel(th.basicChannel().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("idx did not match", channel.getId(),
/// is(th.basicChannel().getId()));
//
// client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser().getId())
// .toCompletableFuture().get();
// client.getChannel(th.basicChannel().getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// Channel privChannel = client.getChannel(th.basicPrivateChannel().getId(),
/// null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("idx did not match", privChannel.getId(),
/// is(th.basicPrivateChannel().getId()));
//
// client.removeUserFromChannel(th.basicPrivateChannel().getId(),
/// th.basicUser().getId())
// .toCompletableFuture().get();
// client.getChannel(th.basicPrivateChannel().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getChannel(th.newId(), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannel(th.basicChannel().getId(), null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getChannel(th.basicChannel().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// th.loginAs(th.systemAdminUser());
// client.getChannel(th.basicChannel().getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.getChannel(th.basicPrivateChannel().getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.getChannel(th.basicUser().getId(), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetPublicChannelsForTeam() throws InterruptedException,
/// ExecutionException {
// Team team = th.basicTeam();
// Channel publicChannel1 = th.basicChannel();
// Channel publicChannel2 = th.basicChannel2();
//
// ChannelList channels = client.getPublicChannelsForTeam(team.getId(), 0, 100,
/// null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("wrong length", channels.size(), is(4));
//
// for (int i = 0; i < channels.size(); i++) {
// Channel channel = channels.get(i);
// assertThat("should include open channel only", channel.getType(),
/// is(ChannelType.Open));
// // only check the created 2 public channels
// if (i < 2 &&
/// !(channel.getDisplayName().equals(publicChannel1.getDisplayName())
// || channel.getDisplayName().equals(publicChannel2.getDisplayName()))) {
// fail("should match public channel display name only. "
// + String.format("channel %d: %s", i, channel.getDisplayName()));
// }
// }
//
// Channel privateChannel = th.createPrivateChannel();
// channels = client.getPublicChannelsForTeam(team.getId(), 0, 100, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("wrong length", channels.size(), is(4));
//
// for (Channel channel : channels) {
// assertThat("should not include private channel", channel.getType(),
/// is(ChannelType.Open));
// assertThat("should not match private channel display name",
/// channel.getDisplayName(),
// is(not(privateChannel.getDisplayName())));
// }
//
// channels = client.getPublicChannelsForTeam(team.getId(), 0, 1, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should be one channel per page", channels.size(), is(1));
//
// channels = client.getPublicChannelsForTeam(team.getId(), 1, 1, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should be one channel per page", channels.size(), is(1));
//
// channels = client.getPublicChannelsForTeam(team.getId(), 10000, 100, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("shpuuld be no channel", channels.size(), is(0));
//
// client.getPublicChannelsForTeam("junk", 0, 100, null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getPublicChannelsForTeam(th.newId(), 0, 100, null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// client.getPublicChannelsForTeam(team.getId(), 0, 100, null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getPublicChannelsForTeam(team.getId(), 0, 100, null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getPublicChannelsForTeam(team.getId(), 0, 100, null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetPublicChannelsByIdsForTeam() throws InterruptedException,
/// ExecutionException {
// String teamId = th.basicTeam().getId();
// List<String> input = new
/// ArrayList<>(Arrays.asList(th.basicChannel().getId()));
// List<String> output = new
/// ArrayList<>(Arrays.asList(th.basicChannel().getDisplayName()));
//
// ChannelList channels = client.getPublicChannelsByIdsForTeam(teamId,
/// input.toArray(new String[0]))
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should return 1 channel", channels.size(), is(1));
// assertThat("missing channel", channels.get(0).getDisplayName(),
/// is(output.get(0)));
//
// input.add(th.newId());
// input.add(th.basicChannel2().getId());
// input.add(th.basicPrivateChannel().getId());
// output.add(th.basicChannel2().getDisplayName());
// Collections.sort(output);
//
// channels = client.getPublicChannelsByIdsForTeam(teamId, input.toArray(new
/// String[0]))
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should return 2 channels", channels.size(), is(2));
//
// for (int i = 0; i < channels.size(); i++) {
// Channel channel = channels.get(i);
// assertThat("missing channel", channel.getDisplayName(), is(output.get(i)));
// }
//
// client.getPublicChannelsByIdsForTeam(th.newId(), input.toArray(new
/// String[0]))
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getPublicChannelsByIdsForTeam(teamId)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getPublicChannelsByIdsForTeam(teamId, "junk")
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getPublicChannelsByIdsForTeam(teamId, th.newId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getPublicChannelsByIdsForTeam(teamId,
/// th.basicPrivateChannel().getId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// th.logout();
//
// client.getPublicChannelsByIdsForTeam(teamId, input.toArray(new String[0]))
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.loginSystemAdmin();
// client.getPublicChannelsByIdsForTeam(teamId, input.toArray(new String[0]))
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelsForTeamForUser() throws InterruptedException,
/// ExecutionException {
// ApiResponse<ChannelList> channelsResponse = client
// .getChannelsForTeamForUser(th.basicTeam().getId(), th.basicUser().getId(),
/// null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// String etag = channelsResponse.getEtag();
// ChannelList channels = channelsResponse.readEntity();
// boolean[] found = new boolean[3];
// for (Channel channel : channels) {
// if (channel.getId().equals(th.basicChannel().getId())) {
// found[0] = true;
// } else if (channel.getId().equals(th.basicChannel2().getId())) {
// found[1] = true;
// } else if (channel.getId().equals(th.basicPrivateChannel().getId())) {
// found[2] = true;
// }
// }
// for (boolean f : found) {
// assertThat("missing a channel", f, is(true));
// }
//
// channels = client.getChannelsForTeamForUser(th.basicTeam().getId(),
/// th.basicUser().getId(), etag)
// .thenApply(this::checkEtag)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// client.getChannelsForTeamForUser(th.basicTeam().getId(), "junk", null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelsForTeamForUser("junk", th.basicUser().getId(), null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelsForTeamForUser(th.basicTeam().getId(),
/// th.basicUser2().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getChannelsForTeamForUser(th.newId(), th.basicUser().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getChannelsForTeamForUser(th.basicTeam().getId(),
/// th.basicUser().getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// }
//
// @Test
// public void testSearchChannels() throws InterruptedException,
/// ExecutionException {
// ChannelSearch search = new ChannelSearch(th.basicChannel().getName());
// ChannelList channels = client.searchChannels(th.basicTeam().getId(), search)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// boolean found = false;
// for (Channel channel : channels) {
// assertThat("should only return public channels", channel.getType(),
/// is(ChannelType.Open));
// if (channel.getId().equals(th.basicChannel().getId())) {
// found = true;
// }
// }
// assertThat("didn't find channel", found, is(true));
//
// search.setTerm(th.basicPrivateChannel().getName());
// channels = client.searchChannels(th.basicTeam().getId(), search)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
//
// found = false;
// for (Channel channel : channels) {
// if (channel.getId().equals(th.basicPrivateChannel().getId())) {
// found = true;
// }
// }
// assertThat("shouldn't find private channel", found, is(false));
//
// search.setTerm(null);
// client.searchChannels(th.basicTeam().getId(), search)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// search.setTerm(th.basicChannel().getName());
// client.searchChannels(th.newId(), search)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.searchChannels("junk", search)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.searchChannels(th.basicTeam().getId(), search)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testDeleteChannel() throws InterruptedException,
/// ExecutionException {
// Team team = th.basicTeam();
// User user = th.basicUser();
// User user2 = th.basicUser2();
//
// // successful delete of public channel
// Channel publicChannel = th.createPublicChannel();
// boolean pass = client.deleteChannel(publicChannel.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should have passed", pass, is(true));
//
// ApiResponse<Channel> deletedChannelResp =
/// client.getChannel(publicChannel.getId(), null)
// .toCompletableFuture().get();
// if (deletedChannelResp.hasError()) {
// // ok
// } else {
// Channel ch = deletedChannelResp.readEntity();
// assertThat("should have failed to get deleted entity", ch.getDeleteAt(),
/// is(not(0)));
// ApiResponse<ChannelMember> res = client.addChannelMember(ch.getId(),
/// user2.getId())
// .toCompletableFuture().get();
// assertThat("should have failed to join deleted channel", res.hasError(),
/// is(true));
// }
//
// Post post1 = new Post(publicChannel.getId(), "a" + th.newId() + "a");
// client.createPost(post1)
// .thenAccept(r -> assertThat(r, hasError()))
// .toCompletableFuture().get();
//
// // successful delete of private channel
// Channel privateChannel2 = th.createPrivateChannel();
// client.deleteChannel(privateChannel2.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // successful delete of channel with multiple members
// Channel publicChannel3 = th.createPublicChannel();
// client.addChannelMember(publicChannel3.getId(),
/// user2.getId()).toCompletableFuture().get();
// client.deleteChannel(publicChannel3.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // successful delete by TeamAdmin of channel created by user
// Channel publicChannel4 = th.createPublicChannel();
// th.logout().loginTeamAdmin();
// client.deleteChannel(publicChannel4.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // default channel cannot be deleted.
// Channel defaultChannel =
/// client.getChannelByName(Channel.DEFAULT_CHANNEL_NAME, team.getId(), null)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// ApiResponse<Boolean> delDefaultResult =
/// client.deleteChannel(defaultChannel.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
// if (!delDefaultResult.hasError()) {
// boolean result = delDefaultResult.readEntity().booleanValue();
// assertThat("should have failed", result, is(false));
// }
//
// // check system admin can delete a channel without any appropriate team
// // or channel membership
// Team sdTeam = th.createTeam();
// Channel sdPublicChannel = new Channel("dn_" + th.newId(),
/// th.generateTestChannelName(), ChannelType.Open,
// sdTeam.getId());
// sdPublicChannel = client.createChannel(sdPublicChannel)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// th.logout().loginSystemAdmin();
// client.deleteChannel(sdPublicChannel.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginTeamAdmin();
// Channel sdPrivateChannel = new Channel("dn_" + th.newId(),
/// th.generateTestChannelName(), ChannelType.Private,
// sdTeam.getId());
// sdPrivateChannel = client.createChannel(sdPrivateChannel)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// th.logout().loginSystemAdmin();
// client.deleteChannel(sdPrivateChannel.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic();
// Channel publicChannel5 = th.createPublicChannel();
// th.logout();
//
// // XXX user2 has authority... why UNAUTHORIZED?
// // th.loginAs(user2);
// // client.deleteChannel(publicChannel5.getId())
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
// //
// // client.deleteChannel("junk")
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
//
// th.logout();
// client.deleteChannel(th.newId())
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.deleteChannel(publicChannel5.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // TODO isLicensed
// }
//
// @Test
// public void testGetChannelByName() throws InterruptedException,
/// ExecutionException {
// Channel channel = client.getChannelByName(th.basicChannel().getName(),
/// th.basicTeam().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("names did not match", channel.getName(),
/// is(th.basicChannel().getName()));
//
// channel = client.getChannelByName(th.basicPrivateChannel().getName(),
/// th.basicTeam().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("names did not match", channel.getName(),
/// is(th.basicPrivateChannel().getName()));
//
// client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser().getId())
// .toCompletableFuture().get();
// client.getChannelByName(th.basicChannel().getName(), th.basicTeam().getId(),
/// null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.removeUserFromChannel(th.basicPrivateChannel().getId(),
/// th.basicUser().getId())
// .toCompletableFuture().get();
// client.getChannelByName(th.basicPrivateChannel().getName(),
/// th.basicTeam().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getChannelByName(th.generateTestChannelName(), th.basicTeam().getId(),
/// null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelByName(th.generateTestChannelName(), "junk", null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelByName(th.basicChannel().getName(), th.basicTeam().getId(),
/// null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getChannelByName(th.basicChannel().getName(), th.basicTeam().getId(),
/// null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getChannelByName(th.basicChannel().getName(), th.basicTeam().getId(),
/// null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelByNameForTeamName() throws InterruptedException,
/// ExecutionException {
// th.logout().loginSystemAdmin();
// Channel channel = client
// .getChannelByNameForTeamName(th.basicChannel().getName(),
/// th.basicTeam().getName(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("names did not match", channel.getName(),
/// is(th.basicChannel().getName()));
//
// th.logout().loginBasic();
// client.getChannelByNameForTeamName(th.basicChannel().getName(),
/// th.basicTeam().getName(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.getChannelByNameForTeamName(th.basicChannel().getName(),
/// th.newRandomString(15), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelByNameForTeamName(th.generateTestChannelName(),
/// th.basicTeam().getName(), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelByNameForTeamName(th.basicChannel().getName(),
/// th.basicTeam().getName(), null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getChannelByNameForTeamName(th.basicChannel().getName(),
/// th.basicTeam().getName(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelMembers() throws InterruptedException,
/// ExecutionException {
// ChannelMembers members = client.getChannelMembers(th.basicChannel().getId(),
/// 0, 60, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should only be 3 users in channel", members.size(), is(3));
//
// members = client.getChannelMembers(th.basicChannel().getId(), 0, 2, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should only be 2 users", members.size(), is(2));
//
// members = client.getChannelMembers(th.basicChannel().getId(), 1, 1, null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should only be 1 user", members.size(), is(1));
//
// members = client.getChannelMembers(th.basicChannel().getId(), 1000, 10000,
/// null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should be 0 users", members.size(), is(0));
//
// client.getChannelMembers(null, 0, 60, null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMembers("junk", 0, 60, null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMembers(th.newId(), 0, 60, null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelMembers(th.basicChannel().getId(), 0, 60, null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getChannelMembers(th.basicChannel().getId(), 0, 60, null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getChannelMembers(th.basicChannel().getId(), 0, 60, null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelMembersByIds() throws InterruptedException,
/// ExecutionException {
// ChannelMembers members =
/// client.getChannelMembersByIds(th.basicChannel().getId(),
/// th.basicUser().getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("returned wrong user", members.get(0).getUserId(),
/// is(th.basicUser().getId()));
//
// client.getChannelMembersByIds(th.basicChannel().getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// ChannelMembers members1 =
/// client.getChannelMembersByIds(th.basicChannel().getId(), "junk")
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("no users should be returned", members1.size(), is(0));
//
// members1 = client.getChannelMembersByIds(th.basicChannel().getId(), "junk",
/// th.basicUser().getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("1 member should be returned", members1.size(), is(1));
//
// members1 = client
// .getChannelMembersByIds(th.basicChannel().getId(), th.basicUser2().getId(),
/// th.basicUser().getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("2 members should be returned", members1.size(), is(2));
//
// client.getChannelMembersByIds("junk", th.basicUser().getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMembersByIds(th.newId(), th.basicUser().getId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelMembersByIds(th.basicChannel().getId(),
/// th.basicUser().getId())
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.loginSystemAdmin();
// client.getChannelMembersByIds(th.basicChannel().getId(),
/// th.basicUser2().getId(), th.basicUser().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelMember() throws InterruptedException,
/// ExecutionException {
// ChannelMember member = client.getChannelMember(th.basicChannel().getId(),
/// th.basicUser().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("wrong channel id", member.getChannelId(),
/// is(th.basicChannel().getId()));
// assertThat("wrong user id", member.getUserId(), is(th.basicUser().getId()));
//
// client.getChannelMember(null, th.basicUser().getId(), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelMember("junk", th.basicUser().getId(), null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMember(th.newId(), th.basicUser().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getChannelMember(th.basicChannel().getId(), null, null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelMember(th.basicChannel().getId(), "junk", null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMember(th.basicChannel().getId(), th.newId(), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelMember(th.basicChannel().getId(), th.basicUser().getId(),
/// null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getChannelMember(th.basicChannel().getId(), th.basicUser().getId(),
/// null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getChannelMember(th.basicChannel().getId(), th.basicUser().getId(),
/// null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelMembersForUser() throws InterruptedException,
/// ExecutionException {
// ChannelMembers members =
/// client.getChannelMembersForUser(th.basicUser().getId(),
/// th.basicTeam().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should have 5 members on team", members.size(), is(5));
//
// client.getChannelMembersForUser(null, th.basicTeam().getId(), null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelMembersForUser("junk", th.basicTeam().getId(), null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMembersForUser(th.newId(), th.basicTeam().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getChannelMembersForUser(th.basicUser().getId(), "", null)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelMembersForUser(th.basicUser().getId(), "junk", null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelMembersForUser(th.basicUser().getId(), th.newId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelMembersForUser(th.basicUser().getId(),
/// th.basicTeam().getId(), null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// User user = th.createUser();
// th.loginAs(user);
// client.getChannelMembersForUser(th.basicUser().getId(),
/// th.basicTeam().getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getChannelMembersForUser(th.basicUser().getId(),
/// th.basicTeam().getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testViewChannel() throws InterruptedException, ExecutionException
/// {
// ChannelView view = new ChannelView(th.basicChannel().getId());
// boolean pass = client.viewChannel(th.basicUser().getId(), view)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should have passed", pass, is(true));
//
// view.setPrevChannelId(th.basicChannel().getId());
// client.viewChannel(th.basicUser().getId(), view)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// view.setPrevChannelId(null);
// client.viewChannel(th.basicUser().getId(), view)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// view.setPrevChannelId("Junk");
// client.viewChannel(th.basicUser().getId(), view)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// ChannelMember member = client.getChannelMember(th.basicChannel().getId(),
/// th.basicUser().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// Channel channel = client.getChannel(th.basicChannel().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should match message counts", member.getMsgCount(),
/// is(channel.getTotalMsgCount()));
// assertThat("should have no mentions", member.getMentionCount(), is(0l));
//
// client.viewChannel("junk", view)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.viewChannel(th.basicUser2().getId(), view)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.doApiPost(String.format("/channels/members/%s/view",
/// th.basicUser().getId()), "garbage")
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// th.logout();
// client.viewChannel(th.basicUser().getId(), view)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.loginSystemAdmin();
// client.viewChannel(th.basicUser().getId(), view)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelUnread() throws InterruptedException,
/// ExecutionException {
// User user = th.basicUser();
// Channel channel = th.basicChannel();
//
// ChannelUnread unread = client.getChannelUnread(channel.getId(), user.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("wrong team id returned for a regular user call",
/// unread.getTeamId(), is(th.basicTeam().getId()));
// assertThat("wrong channel id returned for a regular user call",
/// unread.getChannelId(), is(channel.getId()));
//
// client.getChannelUnread("junk", user.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelUnread(channel.getId(), "junk")
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelUnread(channel.getId(), th.newId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getChannelUnread(th.newId(), user.getId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// User newUser = th.createUser();
// th.loginAs(newUser);
// client.getChannelUnread(th.basicChannel().getId(), user.getId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// th.loginSystemAdmin();
//
// client.getChannelUnread(channel.getId(), user.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.getChannelUnread(th.newId(), user.getId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.getChannelUnread(channel.getId(), th.newId())
// .thenAccept(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetChannelState() throws InterruptedException,
/// ExecutionException {
// Channel channel = th.createPrivateChannel();
//
// ChannelStats stats = client.getChannelStats(channel.getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("couldn't get extra info", stats.getChannelId(),
/// is(channel.getId()));
// assertThat("got incorrect member count", stats.getMemberCount(), is(1l));
//
// client.getChannelStats("junk", null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.getChannelStats(th.newId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout();
// client.getChannelStats(channel.getId(), null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.loginBasic2();
// client.getChannelStats(channel.getId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getChannelStats(channel.getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testGetPinnedPosts() throws InterruptedException,
/// ExecutionException {
// Channel channel = th.basicChannel();
//
// PostList posts = client.getPinnedPosts(channel.getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should not have gotten a pinned post", posts.size(), is(0));
//
// Post pinnedPost = th.createPinnedPost(th.basicChannel().getId());
// ApiResponse<PostList> postsResp = client.getPinnedPosts(channel.getId(),
/// null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// posts = postsResp.readEntity();
// assertThat("should have returned 1 pinned post", posts.size(), is(1));
// assertThat("missing pinned post", posts.getPosts().keySet(),
/// hasItem(pinnedPost.getId()));
//
// client.getPinnedPosts(channel.getId(), postsResp.getEtag())
// .thenApply(this::checkEtag)
// .toCompletableFuture().get();
//
// client.getPinnedPosts(th.newId(), null)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// client.getPinnedPosts("junk", null)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// th.logout();
// client.getPinnedPosts(channel.getId(), null)
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.getPinnedPosts(channel.getId(), null)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// }
//
// @Test
// public void testUpdateChannelRoles() throws InterruptedException,
/// ExecutionException {
// // User 1 creates a channel, making them channel admin by default.
// Channel channel = th.createPublicChannel();
//
// // Adds User 2 to the channel, making them a channel member by default.
// client.addChannelMember(channel.getId(),
/// th.basicUser2().getId()).toCompletableFuture().get();
//
// // User 1 promotes User 2
// boolean pass = client.updateChannelRoles(channel.getId(),
/// th.basicUser2().getId(), Role.ROLE_CHANNEL_USER,
// Role.ROLE_CHANNEL_ADMIN)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .thenApply(b -> b.booleanValue())
// .toCompletableFuture().get();
// assertThat("should have passed", pass, is(true));
//
// ChannelMember member = client.getChannelMember(channel.getId(),
/// th.basicUser2().getId(), null)
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// if (!(Role.ROLE_CHANNEL_ADMIN.is(member.getRoles()) &&
/// Role.ROLE_CHANNEL_USER.is(member.getRoles()))) {
// fail("roles didn't match");
// }
//
// // User 1 demotes User 2
// client.updateChannelRoles(channel.getId(), th.basicUser2().getId(),
/// Role.ROLE_CHANNEL_USER)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic2();
//
// // User 2 cannot demote User 1
// client.updateChannelRoles(channel.getId(), th.basicUser().getId(),
/// Role.ROLE_CHANNEL_USER)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// // User 2 cannot promote self
// client.updateChannelRoles(channel.getId(), th.basicUser2().getId(),
/// Role.ROLE_CHANNEL_USER,
// Role.ROLE_CHANNEL_ADMIN)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginBasic();
//
// // User 1 demotes self
// client.updateChannelRoles(channel.getId(), th.basicUser().getId(),
/// Role.ROLE_CHANNEL_USER)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // System Admin promotes User 1
// th.logout().loginSystemAdmin();
// client.updateChannelRoles(channel.getId(), th.basicUser().getId(),
/// Role.ROLE_CHANNEL_ADMIN,
// Role.ROLE_CHANNEL_USER)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // System Admin demotes User 1
// client.updateChannelRoles(channel.getId(), th.basicUser().getId(),
/// Role.ROLE_CHANNEL_ADMIN)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // System admin promote User 1
// client.updateChannelRoles(channel.getId(), th.basicUser().getId(),
/// Role.ROLE_CHANNEL_ADMIN,
// Role.ROLE_CHANNEL_USER)
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic();
//
// // mattermodt client driver java cannot set "junk" role
// // client.updateChannelRoles(channel.getId(), th.basicUser().getId(),
// // "junk");
//
// client.updateChannelRoles(channel.getId(), "junk", Role.ROLE_CHANNEL_USER)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.updateChannelRoles("junk", th.basicUser().getId(),
/// Role.ROLE_CHANNEL_USER)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.updateChannelRoles(channel.getId(), th.newId(),
/// Role.ROLE_CHANNEL_USER)
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.updateChannelRoles(th.newId(), th.basicUser().getId(),
/// Role.ROLE_CHANNEL_USER)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
// }
//
// @Test
// @Ignore
// public void testUpdateChannelNotifyProps() {
// // TODO Key, Valueの定数を作ってから
// fail();
// }
//
// @Test
// public void testAddChannelMember() throws InterruptedException,
/// ExecutionException {
// User user = th.basicUser();
// User user2 = th.basicUser2();
// Team team = th.basicTeam();
// Channel publicChannel = th.createPublicChannel();
// Channel privateChannel = th.createPrivateChannel();
//
// th.logout().loginSystemAdmin();
// User user3 = th.createUser();
// client.addTeamMember(team.getId(), user3.getId(), null, null,
/// team.getInviteId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic();
// ChannelMember member = client.addChannelMember(publicChannel.getId(),
/// user2.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> checkStatus(r, Status.CREATED))
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should have returned exact channel", member.getChannelId(),
/// is(publicChannel.getId()));
// assertThat("should have returned exact user added to public channel",
/// member.getUserId(), is(user2.getId()));
//
// member = client.addChannelMember(privateChannel.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should have returned exact channel", member.getChannelId(),
/// is(privateChannel.getId()));
// assertThat("should have returned exact user added to private channel",
/// member.getUserId(), is(user2.getId()));
//
// client.removeUserFromChannel(publicChannel.getId(), user.getId())
// .toCompletableFuture().get();
// client.addChannelMember(publicChannel.getId(), user.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.addChannelMember(publicChannel.getId(), "junk")
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.addChannelMember(publicChannel.getId(), th.newId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.addChannelMember("junk", user2.getId())
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.addChannelMember(th.newId(), user2.getId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// User otherUser = th.createUser();
// Channel otherChannel = th.createPublicChannel();
// th.logout();
// th.loginAs(user2);
//
// // skip: in Mattermost TE 3.9.0, loginでこけてるのでUnauthorizedになるが、こけなければ404
// // client.addChannelMember(publicChannel.getId(), otherUser.getId())
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
// //
// // client.addChannelMember(privateChannel.getId(), otherUser.getId())
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
// //
// // client.addChannelMember(otherChannel.getId(), otherUser.getId())
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
//
// th.logout().loginAs(user);
//
// // skip: in Mattermost TE 3.9.0, loginでこけてるのでUnauthorizedになるが、こけなければ404
// // // should fail adding user who is not a member of the team
// // client.addChannelMember(otherChannel.getId(), otherUser.getId())
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
// //
// // client.deleteChannel(otherChannel.getId());
// //
// // // should fail adding user to a deleted channel
// // client.addChannelMember(otherChannel.getId(), user2.getId())
// // .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// // .toCompletableFuture().get();
//
// th.logout();
// client.addChannelMember(publicChannel.getId(), user2.getId())
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// client.addChannelMember(privateChannel.getId(), user2.getId())
// .thenApply(r -> checkStatus(r, Status.UNAUTHORIZED))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.addChannelMember(publicChannel.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.addChannelMember(privateChannel.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// // Test policy does not apply to TE.
// Config config = client.getConfig()
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// final Permissions restrictPrivateChannel =
/// config.getTeamSettings().getRestrictPrivateChannelManageMembers();
// try {
// config.getTeamSettings().setRestrictPrivateChannelManageMembers(Permissions.CHANNEL_ADMIN);
// client.updateConfig(config).toCompletableFuture().get();
//
// th.loginAs(user2);
// privateChannel = th.createPrivateChannel();
// client.addChannelMember(privateChannel.getId(), user.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout();
// th.loginAs(user);
// client.addChannelMember(privateChannel.getId(), user3.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// th.logout();
// } finally {
// th.loginSystemAdmin();
// client.getConfig()
// .thenApply(r -> r.readEntity())
// .thenApply(c -> {
// c.getTeamSettings().setRestrictPrivateChannelManageMembers(restrictPrivateChannel);
// return c;
// }).thenApply(client::updateConfig)
// .toCompletableFuture().get();
// }
//
// // TODO Add a license
// }
//
// @Test
// public void testRemoveChannelMember() throws InterruptedException,
/// ExecutionException {
// User user1 = th.basicUser();
// User user2 = th.basicUser2();
// Team team = th.basicTeam();
//
// boolean pass = client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser2().getId())
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .thenApply(r -> r.booleanValue())
// .toCompletableFuture().get();
// assertThat("should have passed", pass, is(true));
//
// client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser2().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.removeUserFromChannel(th.basicChannel().getId(), "junk")
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .toCompletableFuture().get();
//
// client.removeUserFromChannel(th.basicChannel().getId(), th.newId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// client.removeUserFromChannel(th.newId(), th.basicUser2().getId())
// .thenApply(r -> checkStatus(r, Status.NOT_FOUND))
// .toCompletableFuture().get();
//
// th.logout().loginBasic2();
// client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser().getId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.addChannelMember(th.basicChannel().getId(),
/// th.basicUser2().getId()).toCompletableFuture().get();
// th.logout().loginBasic2();
// client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// client.removeUserFromChannel(th.basicChannel2().getId(),
/// th.basicUser().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.removeUserFromChannel(th.basicChannel().getId(),
/// th.basicUser().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic();
// Channel privateChannel = th.createPrivateChannel();
// client.addChannelMember(privateChannel.getId(),
/// th.basicUser2().getId()).toCompletableFuture().get();
//
// client.removeUserFromChannel(privateChannel.getId(), th.basicUser2().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic2();
// client.removeUserFromChannel(privateChannel.getId(), th.basicUser().getId())
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// client.removeUserFromChannel(privateChannel.getId(), th.basicUser().getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginTeamAdmin().updateUserToNonTeamAdmin(user1,
/// team).logout().loginBasic();
//
// // Test policy does not apply to TE.
// th.logout().loginSystemAdmin();
// Config config = client.getConfig().thenApply(r ->
/// r.readEntity()).toCompletableFuture().get();
// Permissions currentPermission =
/// config.getTeamSettings().getRestrictPrivateChannelManageMembers();
// config.getTeamSettings().setRestrictPrivateChannelManageMembers(Permissions.CHANNEL_ADMIN);
// client.updateConfig(config).toCompletableFuture().get();
// try {
// privateChannel = th.logout().loginSystemAdmin().createPrivateChannel();
// th.logout().loginSystemAdmin();
// client.addChannelMember(privateChannel.getId(), user1.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// client.addChannelMember(privateChannel.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
//
// th.logout().loginBasic();
// client.removeUserFromChannel(privateChannel.getId(), user2.getId())
// .thenApply(this::checkNoError)
// .toCompletableFuture().get();
// } finally {
// th.logout().loginSystemAdmin();
// config.getTeamSettings().setRestrictPrivateChannelManageMembers(currentPermission);
// client.updateConfig(config).toCompletableFuture().get();
// }
//
// // TODO Add a license
// }
//
// // cluster_test.go
//
// @Test
// public void testGetClusterStatus() throws InterruptedException,
/// ExecutionException {
// client.getClusterStatus()
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// List<ClusterInfo> infos = client.getClusterStatus()
// .thenApply(this::checkNoError)
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("should not be null", infos, is(not(nullValue())));
// }
//
// // command_test.go
//
// @Test
// public void testCreateCommand() throws InterruptedException,
/// ExecutionException {
// th.logout().loginSystemAdmin();
// client.getConfig().thenApply(r -> r.readEntity()).thenAccept(c -> {
// c.getServiceSettings().setEnableCommands(true);
// try {
// client.updateConfig(c).toCompletableFuture().get();
// } catch (InterruptedException | ExecutionException e) {
// throw new RuntimeException(e);
// }
// }).toCompletableFuture().get();
// try {
// th.logout().loginBasic();
//
// Command newCmd = new Command(th.basicUser().getId(), th.basicTeam().getId(),
/// "http://nowhere.com",
// CommandMethod.POST, "trigger");
//
// client.createCommand(newCmd)
// .thenApply(r -> checkStatus(r, Status.FORBIDDEN))
// .toCompletableFuture().get();
//
// th.logout().loginSystemAdmin();
// Command createdCmd = client.createCommand(newCmd)
// .thenApply(this::checkNoError)
// .thenApply(r -> checkStatus(r, Status.CREATED))
// .thenApply(r -> r.readEntity())
// .toCompletableFuture().get();
// assertThat("user ids didn't match", createdCmd.getCreatorId(),
/// is(th.systemAdminUser().getId()));
// assertThat("team ids didn't match", createdCmd.getTeamId(),
/// is(th.basicTeam().getId()));
//
// client.createCommand(newCmd)
// .thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
// .thenApply(r -> checkErrorMessage(r,
/// "api.command.duplicate_trigger.app_error"))
// .toCompletableFuture().get();
//
// newCmd.setMethod(null);
// newCmd.setTrigger("test");
// client.createCommand(newCmd)
// .thenApply(r -> checkStatus(r, Status.INTERNAL_SERVER_ERROR))
// .thenApply(r -> checkErrorMessage(r,
/// "model.command.is_valid.method.app_error"))
// .toCompletableFuture().get();
//
// client.getConfig().thenApply(r -> r.readEntity()).thenAccept(c -> {
// c.getServiceSettings().setEnableCommands(false);
// try {
// client.updateConfig(c).toCompletableFuture().get();
// } catch (InterruptedException | ExecutionException e) {
// throw new RuntimeException(e);
// }
// }).toCompletableFuture().get();
// newCmd.setMethod(CommandMethod.POST);
// newCmd.setTrigger("test");
// client.createCommand(newCmd)
// .thenApply(r -> checkStatus(r, Status.NOT_IMPLEMENTED))
// .thenApply(r -> checkErrorMessage(r, "api.command.disabled.app_error"))
// .toCompletableFuture().get();
// } finally {
// th.logout().loginSystemAdmin();
// client.getConfig().thenApply(r -> r.readEntity()).thenAccept(c -> {
// c.getServiceSettings().setEnableCommands(false);
// try {
// client.updateConfig(c).toCompletableFuture().get();
// } catch (InterruptedException | ExecutionException e) {
// throw new RuntimeException(e);
// }
// }).toCompletableFuture().get();
// }
// }
//
// @Test
// public void testUpdateCommand() throws InterruptedException,
/// ExecutionException {
// User user = th.systemAdminUser();
// Team team = th.basicTeam();
//
// th.logout().loginSystemAdmin();
// client.getConfig().thenApply(r -> r.readEntity()).thenAccept(c -> {
// c.getServiceSettings().setEnableCommands(true);
// try {
// client.updateConfig(c).toCompletableFuture().get();
// } catch (InterruptedException | ExecutionException e) {
// throw new RuntimeException(e);
// }
// }).toCompletableFuture().get();
// try {
// Command cmd1 = new Command(user.getId(), team.getId(), "http://nowhere.com",
/// CommandMethod.POST,
// "trigger1");
// cmd1 =
/// client.createCommand(cmd1).thenApply(ApiResponse::readEntity).toCompletableFuture().get();
//
// Command cmd2 = new Command(null, team.getId(), "http://nowhere.com/change",
/// CommandMethod.GET, "trigger2");
// cmd2.setCreatorId(th.newId());
// cmd2.setId(cmd1.getId());
// cmd2.setToken("tokenchange");
//
// Command rcmd = client.updateCommand(cmd2)
// .thenApply(this::checkNoError)
// .thenApply(ApiResponse::readEntity)
// .toCompletableFuture().get();
//
// } finally {
// th.logout().loginSystemAdmin();
// client.getConfig().thenApply(r -> r.readEntity()).thenAccept(c -> {
// c.getServiceSettings().setEnableCommands(false);
// try {
// client.updateConfig(c).toCompletableFuture().get();
// } catch (InterruptedException | ExecutionException e) {
// throw new RuntimeException(e);
// }
// }).toCompletableFuture().get();
// }
// }
//
// private <T> ApiResponse<T> checkEtag(ApiResponse<T> apiResponse) {
// apiResponse.getRawResponse().bufferEntity();
// T data = apiResponse.readEntity();
// assertThat("etag data was not null", data, is(nullValue()));
// assertThat("wrong status code for etag",
/// apiResponse.getRawResponse().getStatus(),
// is(Status.NOT_MODIFIED.getStatusCode()));
// return apiResponse;
// }
//
// public static class HasError<T extends ApiResponse<U>, U> extends
/// BaseMatcher<T> {
//
// private ApiResponse<U> actual;
//
// /**
// * @see org.hamcrest.Matcher#matches(java.lang.Object)
// */
// @SuppressWarnings("unchecked")
// @Override
// public boolean matches(Object actual) {
// if (actual instanceof ApiResponse) {
// this.actual = (ApiResponse<U>) actual;
// return this.actual.hasError();
// }
// return false;
// }
//
// /**
// * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
// */
// @Override
// public void describeTo(Description description) {
// description.appendText("Should have failed");
// }
//
// public static <U> Matcher<? extends ApiResponse<U>> hasError() {
// return new HasError<>();
// }
//
// }
// }
