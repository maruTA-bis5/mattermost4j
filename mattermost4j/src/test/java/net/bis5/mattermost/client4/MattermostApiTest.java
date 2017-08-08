/*
 * @(#) net.bis5.mattermost.client4.MattermostApiTest
 * Copyright (c) 2017 Maruyama Takayuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package net.bis5.mattermost.client4;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Session;
import net.bis5.mattermost.model.SwitchRequest;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamExists;
import net.bis5.mattermost.model.TeamMember;
import net.bis5.mattermost.model.TeamPatch;
import net.bis5.mattermost.model.TeamSearch;
import net.bis5.mattermost.model.TeamStats;
import net.bis5.mattermost.model.TeamType;
import net.bis5.mattermost.model.TeamUnread;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;

/**
 * Mattermost API call test
 * 
 * @author Maruyama Takayuki
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
	public static void initHelper() throws InterruptedException, ExecutionException {
		th = new TestHelper(new MattermostClient(APPLICATION)).setup();
	}

	@Before
	public void setup() throws InterruptedException, ExecutionException {
		client = new MattermostClient(APPLICATION);
		th.changeClient(client).initBasic();
	}

	@After
	public void tearDown() throws InterruptedException, ExecutionException {
		try {
			th.logout();
		} catch (InterruptedException | ExecutionException ex) {
			// avoid errors
		}
	}

	private <T> ApiResponse<T> checkNoError(ApiResponse<T> response) {
		return th.checkNoError(response);
	}

	private <T> ApiResponse<T> checkStatus(ApiResponse<T> response, Status status) {
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
	public void testChannels_CreateChannel_Open_Required() throws InterruptedException, ExecutionException {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
		testChannels_CreateChannel_Success(channel);
	}

	@Test
	public void testChannels_CreateChannel_Open_All() throws InterruptedException, ExecutionException {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Open, th.basicTeam().getId());
		channel.setPurpose("purpose");
		channel.setHeader("header");
		testChannels_CreateChannel_Success(channel);
	}

	@Test
	public void testChannels_CreateChannel_Private_Required() throws InterruptedException, ExecutionException {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
		testChannels_CreateChannel_Success(channel);
	}

	@Test
	public void testChannels_CreateChannel_Private_All() throws InterruptedException, ExecutionException {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
		channel.setPurpose("purpose");
		channel.setHeader("header");
		testChannels_CreateChannel_Success(channel);
	}

	@Test
	public void testChannels_CreateChannel_Fail_Direct() {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Direct, th.basicTeam().getId());

		client.createChannel(channel)
				.thenApply(r -> checkStatus(r, Status.BAD_REQUEST));
	}

	private void testChannels_CreateChannel_Success(Channel channel) throws InterruptedException, ExecutionException {
		String teamId = channel.getTeamId();
		String name = channel.getName();
		String displayName = channel.getDisplayName();
		ChannelType type = channel.getType();
		String purpose = channel.getPurpose();
		String header = channel.getHeader();

		channel = client.createChannel(channel)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channel.getTeamId(), is(teamId));
		assertThat(channel.getName(), is(name));
		assertThat(channel.getDisplayName(), is(displayName));
		assertThat(channel.getType(), is(type));
		// optional properties
		assertThat(channel.getPurpose(), purpose == null ? isEmptyOrNullString() : is(purpose));
		assertThat(channel.getHeader(), header == null ? isEmptyOrNullString() : is(header));
	}

	@Test
	public void testChannels_CreateDirectChannel() throws InterruptedException, ExecutionException {
		User user1 = th.basicUser();
		User user2 = th.basicUser2();

		Channel channel = client.createDirectChannel(user1.getId(), user2.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channel, is(notNullValue()));
	}

	@Test
	public void testChannels_CreateDirectChannel_OneUser() throws InterruptedException, ExecutionException {
		client.createDirectChannel(th.basicUser().getId(), null)
				.thenApply(r -> this.checkStatus(r, Status.BAD_REQUEST))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_CreateGroupChannel() throws InterruptedException, ExecutionException {
		User user1 = th.basicUser();
		User user2 = th.basicUser2();
		User user3 = th.createUser();
		th.loginSystemAdmin().linkUserToTeam(user3, th.basicTeam()).loginBasic();

		client.createGroupChannel(user1.getId(), user2.getId(), user3.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_CreateGroupChannel_Fail_TwoUsers() throws InterruptedException, ExecutionException {
		User user1 = th.basicUser();
		User user2 = th.basicUser2();

		client.createGroupChannel(user1.getId(), user2.getId())
				.thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_ChannelListByTeamId() throws InterruptedException, ExecutionException {
		Team theTeam = th.loginSystemAdmin().createTeam();
		User theUser = th.createUser();
		th.linkUserToTeam(theUser, theTeam);
		Channel channel1 = new Channel("displayname1", "name1", ChannelType.Open, theTeam.getId());
		Channel channel2 = new Channel("displayname2", "name2", ChannelType.Open, theTeam.getId());
		channel1 = client.createChannel(channel1).thenApply(ApiResponse::readEntity).toCompletableFuture().get();
		channel2 = client.createChannel(channel2).thenApply(ApiResponse::readEntity).toCompletableFuture().get();
		th.loginAs(theUser);

		ChannelList channels = client.getPublicChannelsByIdsForTeam(theTeam.getId(), channel1.getId(), channel2.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		List<String> ids = channels.stream()
				.map(Channel::getId)
				.collect(Collectors.toList());
		assertThat(ids.size(), is(2));
		assertThat(ids, containsInAnyOrder(channel1.getId(), channel2.getId()));
	}

	@Test
	public void testChannels_GetAChannel() throws InterruptedException, ExecutionException {
		String channelId = th.basicChannel().getId();

		Channel channel = client.getChannel(channelId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channel.getId(), is(channelId));
	}

	@Test
	public void testChannels_UpdateChannel() throws InterruptedException, ExecutionException {
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

		newChannel = client.updateChannel(newChannel)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(newChannel.getName(), is(newName));
		assertThat(newChannel.getDisplayName(), is(newDispName));
		assertThat(newChannel.getPurpose(), is(newPurpose));
		assertThat(newChannel.getHeader(), is(newHeader));
	}

	@Test
	public void testChannels_UpdateChannel_ChannelNotFound() throws InterruptedException, ExecutionException {
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

		client.updateChannel(newChannel)
				.thenApply(r -> checkStatus(r, Status.NOT_FOUND))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_UpdateChannel_ChangeType() throws InterruptedException, ExecutionException {
		String channelId = th.basicChannel().getId();
		assertThat(th.basicChannel().getType(), is(ChannelType.Open));
		ChannelType newType = ChannelType.Private;
		Channel newChannel = new Channel();
		newChannel.setId(channelId);
		newChannel.setType(newType);

		newChannel = client.updateChannel(newChannel)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(newChannel.getType(), is(newType));
	}

	@Test
	public void testChannels_DeleteChannel() throws InterruptedException, ExecutionException {
		String channelId = th.basicChannel().getId();

		boolean deleteResult = client.deleteChannel(channelId)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(deleteResult, is(true));
		client.getChannel(channelId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenAccept(ch -> assertThat(ch.getDeleteAt(), is(greaterThan(0l))))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_PatchChannel() throws InterruptedException, ExecutionException {
		String channelId = th.basicChannel().getId();
		String newDispName = "new Display name";
		ChannelPatch patch = new ChannelPatch();
		patch.setDisplayName(newDispName);

		Channel newChannel = client.patchChannel(channelId, patch)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(newChannel.getDisplayName(), is(newDispName));
	}

	@Test
	public void testChannels_PatchChannel_ChannelNotFound() throws InterruptedException, ExecutionException {
		String channelId = th.newId();
		String newDispName = "new Display name";
		ChannelPatch patch = new ChannelPatch();
		patch.setDisplayName(newDispName);

		client.patchChannel(channelId, patch)
				.thenApply(r -> checkStatus(r, Status.NOT_FOUND))
				.toCompletableFuture().get();
	}

	@Test
	@Ignore
	public void testChannels_RestoreChannel() {
		// TODO since 3.10
	}

	@Test
	public void testChannels_GetChannelStatistics() throws InterruptedException, ExecutionException {
		String channelId = th.basicChannel().getId();

		ChannelStats stats = client.getChannelStats(channelId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(stats.getChannelId(), is(channelId));
	}

	@Test
	public void testChannels_GetChannelPinnedPosts() throws InterruptedException, ExecutionException {
		String channelId = th.basicChannel().getId();
		Post pinned = th.createPinnedPost(channelId);

		PostList posts = client.getPinnedPosts(channelId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(posts.size(), is(1));
		assertThat(posts.getPosts().get(pinned.getId()), is(notNullValue()));
	}

	@Test
	public void testChannels_GetChannelByName() throws InterruptedException, ExecutionException {
		String channelName = th.basicChannel().getName();

		Channel channel = client.getChannelByName(channelName, th.basicTeam().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channel.getId(), is(th.basicChannel().getId()));
	}

	@Test
	public void testChannels_GetChannelByName_ChannelNotFound() throws InterruptedException, ExecutionException {
		String channelName = "fake-channel-name";

		client.getChannelByName(channelName, th.basicTeam().getId(), null)
				.thenApply(r -> checkStatus(r, Status.NOT_FOUND))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_GetChannelByNameAndTeamName() throws InterruptedException, ExecutionException {
		String channelName = th.basicChannel().getName();
		String teamName = th.basicTeam().getName();

		Channel channel = client.getChannelByNameForTeamName(channelName, teamName, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channel.getId(), is(th.basicChannel().getId()));
	}

	@Test
	public void testChannels_GetChannelByNameAndTeamName_ChannelNotFound()
			throws InterruptedException, ExecutionException {
		String channelName = "fake-channel-name";
		String teamName = th.basicTeam().getName();

		client.getChannelByNameForTeamName(channelName, teamName, null)
				.thenApply(r -> checkStatus(r, Status.NOT_FOUND))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_GetChannelByNameAndTeamName_TeamNotFound()
			throws InterruptedException, ExecutionException {
		String channelName = "fake-channel-name";
		String teamName = "fake-team-name";

		client.getChannelByNameForTeamName(channelName, teamName, null)
				.thenApply(r -> checkStatus(r, Status.NOT_FOUND))
				.toCompletableFuture().get();
	}

	@Test
	public void testChannels_GetChannelMembers() throws InterruptedException, ExecutionException {
		User user1 = th.createUser();
		User user2 = th.createUser();
		th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam()).linkUserToTeam(user2, th.basicTeam())
				.loginAs(user1);
		Channel channel = th.createPublicChannel();
		CompletableFuture.allOf(client.addChannelMember(channel.getId(), user1.getId()).toCompletableFuture(),
				client.addChannelMember(channel.getId(), user2.getId()).toCompletableFuture()).get();

		ChannelMembers members = client.getChannelMembers(channel.getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(members.size(), is(2));
		assertThat(members.stream().map(m -> m.getUserId()).collect(Collectors.toSet()),
				containsInAnyOrder(user1.getId(), user2.getId()));
	}

	@Test
	public void testChannels_AddUser() throws InterruptedException, ExecutionException {
		Channel channel = th.basicChannel();
		User user = th.createUser();
		th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginBasic();

		ChannelMember member = client.addChannelMember(channel.getId(), user.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(member.getChannelId(), is(channel.getId()));
		assertThat(member.getUserId(), is(user.getId()));
	}

	@Test
	public void testChannels_GetChannelMembersByIds() throws InterruptedException, ExecutionException {
		Channel channel = th.createPublicChannel();
		User user1 = th.createUser();
		User user2 = th.createUser();
		th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam()).linkUserToTeam(user2, th.basicTeam()).loginBasic();
		CompletableFuture.allOf(client.addChannelMember(channel.getId(), user1.getId()).toCompletableFuture(),
				client.addChannelMember(channel.getId(), user2.getId()).toCompletableFuture()).get();

		ChannelMembers members = client.getChannelMembersByIds(channel.getId(), user1.getId(), user2.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(members.size(), is(2));
		assertThat(members.stream().map(m -> m.getUserId()).collect(Collectors.toSet()),
				containsInAnyOrder(user1.getId(), user2.getId()));
	}

	@Test
	public void testChannels_GetChannelMember() throws InterruptedException, ExecutionException {
		Channel channel = th.basicChannel();
		User user = th.basicUser();

		ChannelMember member = client.getChannelMember(channel.getId(), user.getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(member.getChannelId(), is(channel.getId()));
		assertThat(member.getUserId(), is(user.getId()));
	}

	@Test
	public void testChannels_RemoveUserFromChannel() throws InterruptedException, ExecutionException {
		Channel channel = th.basicChannel();
		User user = th.basicUser2();

		// logged-in as basicUser

		boolean result = client.removeUserFromChannel(channel.getId(), user.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testChannels_UpdateChannelRoles() throws InterruptedException, ExecutionException {
		User channelAdmin = th.basicUser();
		User channelUser = th.basicUser2();
		Channel channel = th.loginAs(channelAdmin).createPublicChannel();
		client.addChannelMember(channel.getId(), channelUser.getId()).toCompletableFuture().get();

		boolean result = client
				.updateChannelRoles(channel.getId(), channelUser.getId(), Role.ROLE_CHANNEL_ADMIN,
						Role.ROLE_CHANNEL_USER)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	@Ignore
	public void testChannels_UpdateChannelNotifications() {
		// TODO props定数を作る
	}

	@Test
	public void testChannels_ViewChannel() throws InterruptedException, ExecutionException {
		User user = th.basicUser();
		Channel channel = th.basicChannel2();
		ChannelView view = new ChannelView(channel.getId());

		boolean result = client.viewChannel(user.getId(), view)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testChannels_GetChannelMembersForUser() throws InterruptedException, ExecutionException {
		User user = th.createUser();
		th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginAs(user);
		Channel channel1 = th.createPublicChannel();
		Channel channel2 = th.createPublicChannel();
		CompletableFuture.allOf(client.addChannelMember(channel1.getId(), user.getId()).toCompletableFuture(),
				client.addChannelMember(channel2.getId(), user.getId()).toCompletableFuture()).get();

		ChannelMembers members = client.getChannelMembersForUser(user.getId(), th.basicTeam().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(members.stream().map(m -> m.getChannelId()).collect(Collectors.toSet()),
				hasItems(channel1.getId(), channel2.getId()));
	}

	@Test
	public void testChannels_GetChannelsForUser() throws InterruptedException, ExecutionException {
		User user = th.createUser();
		th.loginSystemAdmin().linkUserToTeam(user, th.basicTeam()).loginAs(user);
		Channel channel1 = th.createPublicChannel();
		Channel channel2 = th.createPublicChannel();
		CompletableFuture.allOf(client.addChannelMember(channel1.getId(), user.getId()).toCompletableFuture(),
				client.addChannelMember(channel2.getId(), user.getId()).toCompletableFuture()).get();

		ChannelList channels = client.getChannelsForTeamForUser(th.basicTeam().getId(), user.getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channels.stream().map(c -> c.getId()).collect(Collectors.toSet()),
				hasItems(channel1.getId(), channel2.getId()));
	}

	@Test
	public void testChannels_GetUnreadMessages() throws InterruptedException, ExecutionException {
		User user = th.basicUser();
		Channel channel = th.basicChannel();

		ChannelUnread unread = client.getChannelUnread(channel.getId(), user.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(unread.getChannelId(), is(channel.getId()));
	}

	// Users

	@Test
	public void testUsers_CreateUser() throws InterruptedException, ExecutionException {
		User user = new User();
		user.setEmail(th.generateTestEmail());
		user.setUsername(th.generateTestUsername());
		user.setPassword("PASSWD");

		User created = client.createUser(user)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(created.getEmail(), is(user.getEmail()));
		assertThat(created.getUsername(), is(user.getUsername()));
	}

	@Test
	public void testUsers_GetUsers() throws InterruptedException, ExecutionException {
		List<User> users = client.getUsers(0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users, is(not(emptyIterable())));
	}

	@Test
	public void testUsers_GetUsers_InChannel() throws InterruptedException, ExecutionException {
		th.loginBasic();
		Channel channel = th.createPublicChannel();
		client.addChannelMember(channel.getId(), th.basicUser2().getId()).thenApply(this::checkNoError)
				.toCompletableFuture().get();

		List<User> users = client.getUsersInChannel(channel.getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testUsers_GetUsers_NotInChannel() throws InterruptedException, ExecutionException {
		Set<String> notInChannelUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(),
				th.teamAdminUser().getId()));
		th.loginBasic();
		Channel channel = th.createPublicChannel();
		notInChannelUserIds.remove(th.basicUser().getId());

		List<User> users = client.getUsersNotInChannel(th.basicTeam().getId(), channel.getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				hasItems(notInChannelUserIds.toArray(new String[0])));
	}

	@Test
	public void testUsers_GetUsers_InTeam() throws InterruptedException, ExecutionException {
		User notInTeamUser = th.loginSystemAdmin().createUser();
		Set<String> inTeamUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(),
				th.teamAdminUser().getId()));
		th.loginBasic();

		List<User> users = client.getUsersInTeam(th.basicTeam().getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		Set<String> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
		assertThat(userIds, not(hasItems(notInTeamUser.getId())));
		assertThat(userIds, hasItems(inTeamUserIds.toArray(new String[0])));
	}

	@Test
	public void testUsers_GetUsers_NotInTeam() throws InterruptedException, ExecutionException {
		th.loginBasic();
		Set<String> inTeamUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(),
				th.systemAdminUser().getId(), th.teamAdminUser().getId()));

		List<User> users = client.getUsersNotInTeam(th.basicTeam().getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				not(hasItems(inTeamUserIds.toArray(new String[0]))));
	}

	@Test
	public void testUsers_GetUsers_WithoutTeam() throws InterruptedException, ExecutionException {
		User withoutTeamUser = th.loginSystemAdmin().createUser();
		th.loginSystemAdmin();

		List<User> users = client.getUsersWithoutTeam(0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				hasItem(withoutTeamUser.getId()));
	}

	@Test
	public void testUsers_GetUsersByIds() throws InterruptedException, ExecutionException {

		List<User> users = client.getUsersByIds(th.basicUser().getId(), th.basicUser2().getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testUsers_GetUsersByUsernames() throws InterruptedException, ExecutionException {

		List<User> users = client.getUsersByUsernames(th.basicUser().getUsername(), th.basicUser2().getUsername())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testUsers_SearchUsers() throws InterruptedException, ExecutionException {
		UserSearch criteria = UserSearch.builder().term(th.basicUser().getUsername()).teamId(th.basicTeam().getId())
				.build();

		List<User> users = client.searchUsers(criteria)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(users.stream().map(User::getUsername).collect(Collectors.toSet()),
				hasItem(th.basicUser().getUsername()));
	}

	@Test
	public void testUsers_AutocompleteUsers() throws InterruptedException, ExecutionException {

		UserAutocomplete autocompleteUsers = client.autocompleteUsers(th.basicUser().getUsername(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
				hasItem(th.basicUser().getId()));
	}

	@Test
	public void testUsers_AutocompleteUsers_InTeam() throws InterruptedException, ExecutionException {

		UserAutocomplete autocompleteUsers = client
				.autocompleteUsersInTeam(th.basicTeam().getId(), th.basicUser().getUsername(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
				hasItem(th.basicUser().getId()));
	}

	@Test
	public void testUsers_AutocompleteUsers_InChannel() throws InterruptedException, ExecutionException {
		Channel channel = th.createPublicChannel();

		UserAutocomplete autocompleteUsers = client
				.autocompleteUsersInChannel(th.basicTeam().getId(), channel.getId(), null, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(autocompleteUsers.getUsers().stream().map(User::getId).collect(Collectors.toSet()),
				hasItem(th.basicUser().getId()));
		assertThat(autocompleteUsers.getOutOfChannel().stream().map(User::getId).collect(Collectors.toSet()),
				hasItem(th.basicUser2().getId()));
	}

	@Test
	public void testUsers_GetUser() throws InterruptedException, ExecutionException {
		String userId = th.basicUser().getId();

		User user = client.getUser(userId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(user.getId(), is(userId));
	}

	@Test
	public void testUsers_UpdateUser() throws InterruptedException, ExecutionException {
		User user = th.basicUser();
		String firstName = "newFirst" + user.getFirstName();
		String lastName = "newLast" + user.getLastName();
		user.setFirstName(firstName);
		user.setLastName(lastName);

		user = client.updateUser(user)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(user.getFirstName(), is(firstName));
		assertThat(user.getLastName(), is(lastName));
	}

	@Test
	public void testUsers_Deactivate() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();
		String userId = th.basicUser().getId();

		boolean result = client.deleteUser(userId)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_PatchUser() throws InterruptedException, ExecutionException {
		UserPatch patch = new UserPatch();
		String firstName = "newFirst" + th.basicUser().getFirstName();
		String lastName = "newLast" + th.basicUser().getLastName();
		patch.setFirstName(firstName);
		patch.setLastName(lastName);

		User user = client.patchUser(th.basicUser().getId(), patch)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(user.getFirstName(), is(firstName));
		assertThat(user.getLastName(), is(lastName));
	}

	@Test
	public void testUsers_UpdateUserRoles() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();

		boolean result = client.updateUserRoles(th.basicUser().getId(), Role.ROLE_SYSTEM_ADMIN, Role.ROLE_SYSTEM_USER)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_UpdateUserActiveStatus() throws InterruptedException, ExecutionException {

		boolean result = client.updateUserActive(th.basicUser().getId(), false)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_GetUserProfileImage()
			throws InterruptedException, ExecutionException, FileNotFoundException, IOException {

		byte[] image = client.getProfileImage(th.basicUser().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		Path tempFile = Files.createTempFile("mm", ".png");
		System.out.println(tempFile);
		try (FileOutputStream fout = new FileOutputStream(tempFile.toFile())) {
			fout.write(image);
		}
	}

	@Test
	public void testUsers_SetUserProfileImage() throws InterruptedException, ExecutionException, URISyntaxException {
		Path image = Paths.get(getClass().getResource("/noto-emoji_u1f310.png").toURI());

		boolean result = client.setProfileImage(th.basicUser().getId(), image)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_GetUserByName() throws InterruptedException, ExecutionException {
		String username = th.basicUser().getUsername();

		User user = client.getUserByUsername(username, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(user.getId(), is(th.basicUser().getId()));
	}

	@Test
	public void testUsers_ResetPassword() throws InterruptedException, ExecutionException {

		client.resetPassword(th.newRandomString(64), "passwd")
				// invalid token
				.thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
				.toCompletableFuture().get();
	}

	@Test
	public void testUsers_UpdateUserMfa() throws InterruptedException, ExecutionException {

		client.updateUserMfa(th.basicUser().getId(), null, false)
				// Enterprise Edition required
				.thenApply(r -> checkStatus(r, Status.NOT_IMPLEMENTED))
				.toCompletableFuture().get();
	}

	@Test
	public void testUsers_GenerateMfaSecret() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();

		client.generateMfaSecret(th.basicUser().getId())
				// Enterprise Edition required
				.thenApply(r -> checkStatus(r, Status.NOT_IMPLEMENTED))
				.toCompletableFuture().get();
	}

	@Test
	public void testUsers_CheckMfa() throws InterruptedException, ExecutionException {
		boolean mfaRequired = client.checkUserMfa(th.basicUser().getId())
				.toCompletableFuture().get();

		assertThat(mfaRequired, is(false));
	}

	@Test
	public void testUsers_UpdateUserPassword() throws InterruptedException, ExecutionException {
		String userId = th.basicUser().getId();
		String currentPassword = th.basicUser().getPassword();
		String newPassword = "new" + currentPassword;

		boolean result = client.updateUserPassword(userId, currentPassword, newPassword)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_SendPasswordResetEmail() throws InterruptedException, ExecutionException {
		String email = th.basicUser().getEmail();

		boolean result = client.sendPasswordResetEmail(email)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_GetUserByEmail() throws InterruptedException, ExecutionException {
		String email = th.basicUser().getEmail();

		User user = client.getUserByEmail(email, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(user.getId(), is(th.basicUser().getId()));
	}

	@Test
	public void testUsers_GetUserSessions() throws InterruptedException, ExecutionException {
		String userId = th.basicUser().getId();

		List<Session> sessions = client.getSessions(userId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(sessions.stream().findAny().map(Session::getUserId).get(), is(userId));
	}

	@Test
	public void testUsers_RevokeUserSession() throws InterruptedException, ExecutionException {
		Session session = client.getSessions(th.basicUser().getId(), null)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get()
				.stream()
				.findAny()
				.get();

		boolean result = client.revokeSession(session.getUserId(), session.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_AttachMobileDevice() throws InterruptedException, ExecutionException {
		boolean result = client.attachDeviceId(th.newId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_GetAudits() throws InterruptedException, ExecutionException {

		Audits audits = client.getUserAudits(th.basicUser().getId(), 0, 50, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(audits.stream().findAny().map(Audit::getId).get(), is(not(nullValue())));
	}

	@Test
	public void testUsers_VerifyEmail() throws InterruptedException, ExecutionException {

		client.verifyUserEmail(th.newId())
				// invalid token
				.thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
				.toCompletableFuture().get();
	}

	@Test
	public void testUsers_SendVerificationEmail() throws InterruptedException, ExecutionException {

		boolean result = client.sendVerificationEmail(th.basicUser().getEmail())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testUsers_SwitchLoginMethod() throws InterruptedException, ExecutionException {
		SwitchRequest request = new SwitchRequest();
		request.setCurrentService(AuthService.Email);
		request.setNewService(AuthService.GitLab);
		request.setEmail(th.basicUser().getEmail());
		request.setCurrentPassword(th.basicUser().getPassword());
		request.setPassword(th.basicUser().getPassword()); // for 4.0+
		th.loginBasic();

		client.switchAccountType(request)
				.thenApply(r -> checkStatus(r, Status.NOT_IMPLEMENTED))
				.toCompletableFuture().get();
	}

	// Teams

	@Test
	public void testTeams_CreateTeam() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();
		Team team = new Team();
		final String teamName = th.generateTestTeamName();
		final String teamDisplayName = "dn_" + teamName;
		team.setName(teamName);
		team.setDisplayName(teamDisplayName);
		team.setType(TeamType.OPEN);

		team = client.createTeam(team)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(team.getName(), is(teamName));
		assertThat(team.getDisplayName(), is(teamDisplayName));
		assertThat(team.getType(), is(TeamType.OPEN));
		assertThat(team.getId(), is(not(nullValue())));
	}

	@Test
	public void testTeams_GetTeams() throws InterruptedException, ExecutionException {
		Team team = th.loginSystemAdmin().createTeam();

		List<Team> teams = client.getAllTeams(null, 0, 60)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(team.getId()));
	}

	@Test
	public void testTeams_getTeam() throws InterruptedException, ExecutionException {

		Team team = client.getTeam(th.basicTeam().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(team.getId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_UpdateTeam() throws InterruptedException, ExecutionException {
		th.loginTeamAdmin();
		Team team = th.basicTeam();
		final String teamId = team.getId();
		final String newDispName = "new" + team.getDisplayName();
		team.setDisplayName(newDispName);

		team = client.updateTeam(team)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(team.getId(), is(teamId));
		assertThat(team.getDisplayName(), is(newDispName));
	}

	@Test
	public void testTeams_DeleteTeam_Soft() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();

		boolean result = client.deleteTeam(th.basicTeam().getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testTeams_DeleteTeam_Permanent() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();

		boolean result = client.deleteTeam(th.basicTeam().getId(), true)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testTeams_PatchTeam() throws InterruptedException, ExecutionException {
		th.loginTeamAdmin();
		TeamPatch patch = new TeamPatch();
		final String newDisplayName = "new" + th.basicTeam().getDisplayName();
		patch.setDisplayName(newDisplayName);

		Team team = client.patchTeam(th.basicTeam().getId(), patch)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(team.getDisplayName(), is(newDisplayName));
	}

	@Test
	public void testTeams_GetTeamByName() throws InterruptedException, ExecutionException {
		final String teamId = th.basicTeam().getId();
		final String teamName = th.basicTeam().getName();

		Team team = client.getTeamByName(teamName, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(team.getId(), is(teamId));
	}

	@Test
	public void testTeams_SearchTeams() throws InterruptedException, ExecutionException {
		TeamSearch search = new TeamSearch();
		search.setTerm(th.basicTeam().getName());

		List<Team> teams = client.searchTeams(search)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_TeamExists_Exists() throws InterruptedException, ExecutionException {

		TeamExists exists = client.teamExists(th.basicTeam().getName(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(exists.isExists(), is(true));
	}

	@Test
	public void testTeams_TeamExists_NotExists() throws InterruptedException, ExecutionException {

		TeamExists exists = client.teamExists("fake" + th.generateTestTeamName(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(exists.isExists(), is(false));
	}

	@Test
	public void testTeams_GetUsersTeams() throws InterruptedException, ExecutionException {
		String userId = th.basicUser().getId();

		List<Team> teams = client.getTeamsForUser(userId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_GetTeamMembers() throws InterruptedException, ExecutionException {

		List<TeamMember> teamMembers = client.getTeamMembers(th.basicTeam().getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(teamMembers.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
				hasItems(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testTeams_AddUserToTeam() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();
		User user = th.createUser();
		th.loginTeamAdmin();
		TeamMember teamMemberToAdd = new TeamMember(th.basicTeam().getId(), user.getId());

		TeamMember teamMember = client.addTeamMember(teamMemberToAdd)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(teamMember.getTeamId(), is(teamMemberToAdd.getTeamId()));
		assertThat(teamMember.getUserId(), is(teamMemberToAdd.getUserId()));
	}

	@Test
	@Ignore // API for 4.0+
	public void testTeams_AddUserToTeamFromInvite() throws InterruptedException, ExecutionException {

		client.addTeamMember("hash", "dataToHash", "inviteId")
				.thenApply(r -> checkStatus(r, Status.BAD_REQUEST))
				.toCompletableFuture().get();

	}

	@Test
	public void testTeams_AddMultipleUsersToTeam() throws InterruptedException, ExecutionException {
		th.loginSystemAdmin();
		User user1 = th.createUser();
		User user2 = th.createUser();
		th.loginTeamAdmin();

		List<TeamMember> members = client.addTeamMembers(th.basicTeam().getId(), user1.getId(), user2.getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
				containsInAnyOrder(user1.getId(), user2.getId()));
	}

	@Test
	public void testTeams_GetTeamMembersForUser() throws InterruptedException, ExecutionException {

		List<TeamMember> members = client.getTeamMembersForUser(th.basicUser().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
				hasItems(th.basicUser().getId()));
	}

	@Test
	public void testTeams_GetTeamMember() throws InterruptedException, ExecutionException {
		String teamId = th.basicTeam().getId();
		String userId = th.basicUser2().getId();

		TeamMember member = client.getTeamMember(teamId, userId, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(member.getTeamId(), is(teamId));
		assertThat(member.getUserId(), is(userId));
	}

	@Test
	public void testTeams_RemoveUserFromTeam() throws InterruptedException, ExecutionException {
		th.loginTeamAdmin();
		String teamId = th.basicTeam().getId();
		String userId = th.basicUser2().getId();

		boolean result = client.removeTeamMember(teamId, userId)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testTeams_GetTeamMembersByIds() throws InterruptedException, ExecutionException {

		List<TeamMember> members = client
				.getTeamMembersByIds(th.basicTeam().getId(), th.basicUser().getId(), th.basicUser2().getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
				hasItems(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testTeams_GetTeamStats() throws InterruptedException, ExecutionException {

		TeamStats stats = client.getTeamStats(th.basicTeam().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(stats.getTeamId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_UpdateTeamMemberRoles() throws InterruptedException, ExecutionException {
		th.loginTeamAdmin();

		boolean result = client
				.updateTeamMemberRoles(th.basicTeam().getId(), th.basicUser().getId(), Role.ROLE_TEAM_ADMIN)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	public void testTeams_GetTeamUnreadsForUser() throws InterruptedException, ExecutionException {

		List<TeamUnread> unreads = client.getTeamUnreadForUser(th.basicUser().getId(), null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		unreads.stream().findFirst().ifPresent(u -> assertThat(u.getTeamId(), is(th.basicTeam().getId())));
	}

	@Test
	public void testTeams_GetTeamUnreadsForTeam() throws InterruptedException, ExecutionException {

		TeamUnread unread = client.getTeamUnread(th.basicTeam().getId(), th.basicUser().getId())
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(unread.getTeamId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_InviteUsersToTheTeamByEmail() throws InterruptedException, ExecutionException {

		boolean result = client
				.inviteUsersToTeam(th.basicTeam().getId(), Collections.singletonList(th.generateTestEmail()))
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.thenApply(Boolean::booleanValue)
				.toCompletableFuture().get();

		assertThat(result, is(true));
	}

	@Test
	@Ignore // Not Implemented
	public void testTeams_ImportTeamFromOtherApplication() {
	}

	@Test
	public void testTeams_GetPublicChannels() throws InterruptedException, ExecutionException {

		List<Channel> channels = client.getPublicChannelsForTeam(th.basicTeam().getId(), 0, 60, null)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_SearchChannels() throws InterruptedException, ExecutionException {
		ChannelSearch search = new ChannelSearch();
		search.setTerm(th.basicChannel().getName());

		List<Channel> channels = client.searchChannels(th.basicTeam().getId(), search)
				.thenApply(this::checkNoError)
				.thenApply(ApiResponse::readEntity)
				.toCompletableFuture().get();

		assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
	}

}
