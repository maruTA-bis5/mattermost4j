/*
 * @(#) net.bis5.mattermost.client4.MattermostApiTest
 * Copyright (c) 2017-present, Maruyama Takayuki
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import net.bis5.mattermost.model.PostPatch;
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
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
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
	public static void initHelper() {
		th = new TestHelper(new MattermostClient(APPLICATION)).setup();
	}

	@Before
	public void setup() {
		client = new MattermostClient(APPLICATION);
		th.changeClient(client).initBasic();
	}

	@After
	public void tearDown() {
		th.logout();
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
		Channel channel = new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
		testChannels_CreateChannel_Success(channel);
	}

	@Test
	public void testChannels_CreateChannel_Private_All() {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Private, th.basicTeam().getId());
		channel.setPurpose("purpose");
		channel.setHeader("header");
		testChannels_CreateChannel_Success(channel);
	}

	@Test
	public void testChannels_CreateChannel_Fail_Direct() {
		Channel channel = new Channel("DisplayName", "name", ChannelType.Direct, th.basicTeam().getId());

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

		ApiResponse<Channel> response = assertNoError(client.createDirectChannel(user1.getId(), user2.getId()));
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

		ApiResponse<Channel> response = assertNoError(
				client.createGroupChannel(user1.getId(), user2.getId(), user3.getId()));
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

		List<String> ids = channels.stream()
				.map(Channel::getId)
				.collect(Collectors.toList());
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
	@Ignore
	public void testChannels_RestoreChannel() {
		// TODO since 3.10
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

		ApiResponse<Channel> response = assertNoError(
				client.getChannelByName(channelName, th.basicTeam().getId(), null));
		Channel channel = response.readEntity();

		assertThat(channel.getId(), is(th.basicChannel().getId()));
	}

	@Test
	public void testChannels_GetChannelByName_ChannelNotFound() {
		String channelName = "fake-channel-name";

		assertStatus(client.getChannelByName(channelName, th.basicTeam().getId(), null), Status.NOT_FOUND);
	}

	@Test
	public void testChannels_GetChannelByNameAndTeamName() {
		String channelName = th.basicChannel().getName();
		String teamName = th.basicTeam().getName();

		ApiResponse<Channel> response = assertNoError(client.getChannelByNameForTeamName(channelName, teamName, null));
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
		th.loginSystemAdmin().linkUserToTeam(user1, th.basicTeam()).linkUserToTeam(user2, th.basicTeam())
				.loginAs(user1);
		Channel channel = th.createPublicChannel();
		client.addChannelMember(channel.getId(), user1.getId());
		client.addChannelMember(channel.getId(), user2.getId());

		ApiResponse<ChannelMembers> response = assertNoError(client.getChannelMembers(channel.getId(), 0, 60, null));
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

		ApiResponse<ChannelMember> response = assertNoError(client.addChannelMember(channel.getId(), user.getId()));
		ChannelMember member = response.readEntity();

		assertThat(member.getChannelId(), is(channel.getId()));
		assertThat(member.getUserId(), is(user.getId()));
	}

	@Test
	public void testChannels_GetChannelMembersByIds() {
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
	public void testChannels_GetChannelMember() {
		Channel channel = th.basicChannel();
		User user = th.basicUser();

		ApiResponse<ChannelMember> response = assertNoError(
				client.getChannelMember(channel.getId(), user.getId(), null));
		ChannelMember member = response.readEntity();

		assertThat(member.getChannelId(), is(channel.getId()));
		assertThat(member.getUserId(), is(user.getId()));
	}

	@Test
	public void testChannels_RemoveUserFromChannel() {
		Channel channel = th.basicChannel();
		User user = th.basicUser2();

		// logged-in as basicUser

		ApiResponse<Boolean> response = assertNoError(client.removeUserFromChannel(channel.getId(), user.getId()));
		boolean result = response.readEntity().booleanValue();

		assertThat(result, is(true));
	}

	@Test
	public void testChannels_UpdateChannelRoles() {
		User channelAdmin = th.basicUser();
		User channelUser = th.basicUser2();
		Channel channel = th.loginAs(channelAdmin).createPublicChannel();
		client.addChannelMember(channel.getId(), channelUser.getId());

		ApiResponse<Boolean> response = assertNoError(
				client.updateChannelRoles(channel.getId(), channelUser.getId(), Role.ROLE_CHANNEL_ADMIN,
						Role.ROLE_CHANNEL_USER));
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

		ApiResponse<Boolean> response = assertNoError(client.viewChannel(user.getId(), view));
		boolean result = response.readEntity().booleanValue();

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

		ApiResponse<ChannelMembers> response = assertNoError(
				client.getChannelMembersForUser(user.getId(), th.basicTeam().getId(), null));
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

		ApiResponse<ChannelList> response = assertNoError(
				client.getChannelsForTeamForUser(th.basicTeam().getId(), user.getId(), null));
		ChannelList channels = response.readEntity();

		assertThat(channels.stream().map(c -> c.getId()).collect(Collectors.toSet()),
				hasItems(channel1.getId(), channel2.getId()));
	}

	@Test
	public void testChannels_GetUnreadMessages() {
		User user = th.basicUser();
		Channel channel = th.basicChannel();

		ApiResponse<ChannelUnread> response = assertNoError(client.getChannelUnread(channel.getId(), user.getId()));
		ChannelUnread unread = response.readEntity();

		assertThat(unread.getChannelId(), is(channel.getId()));
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
		ApiResponse<UserList> response = assertNoError(client.getUsers(0, 60, null));
		List<User> users = response.readEntity();

		assertThat(users, is(not(emptyIterable())));
	}

	@Test
	public void testUsers_GetUsers_InChannel() {
		th.loginBasic();
		Channel channel = th.createPublicChannel();
		assertNoError(client.addChannelMember(channel.getId(), th.basicUser2().getId()));

		ApiResponse<UserList> response = assertNoError(client.getUsersInChannel(channel.getId(), 0, 60, null));
		List<User> users = response.readEntity();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				containsInAnyOrder(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testUsers_GetUsers_NotInChannel() {
		Set<String> notInChannelUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(),
				th.teamAdminUser().getId()));
		th.loginBasic();
		Channel channel = th.createPublicChannel();
		notInChannelUserIds.remove(th.basicUser().getId());

		ApiResponse<UserList> response = assertNoError(
				client.getUsersNotInChannel(th.basicTeam().getId(), channel.getId(), 0, 60, null));
		List<User> users = response.readEntity();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				hasItems(notInChannelUserIds.toArray(new String[0])));
	}

	@Test
	public void testUsers_GetUsers_InTeam() {
		User notInTeamUser = th.loginSystemAdmin().createUser();
		Set<String> inTeamUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(),
				th.teamAdminUser().getId()));
		th.loginBasic();

		ApiResponse<UserList> response = assertNoError(client.getUsersInTeam(th.basicTeam().getId(), 0, 60, null));
		List<User> users = response.readEntity();

		Set<String> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
		assertThat(userIds, not(hasItems(notInTeamUser.getId())));
		assertThat(userIds, hasItems(inTeamUserIds.toArray(new String[0])));
	}

	@Test
	public void testUsers_GetUsers_NotInTeam() {
		th.loginBasic();
		Set<String> inTeamUserIds = new HashSet<>(Arrays.asList(th.basicUser().getId(), th.basicUser2().getId(),
				th.systemAdminUser().getId(), th.teamAdminUser().getId()));

		ApiResponse<UserList> response = assertNoError(client.getUsersNotInTeam(th.basicTeam().getId(), 0, 60, null));
		List<User> users = response.readEntity();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				not(hasItems(inTeamUserIds.toArray(new String[0]))));
	}

	@Test
	public void testUsers_GetUsers_WithoutTeam() {
		User withoutTeamUser = th.loginSystemAdmin().createUser();
		th.loginSystemAdmin();

		ApiResponse<UserList> response = assertNoError(client.getUsersWithoutTeam(0, 60, null));
		List<User> users = response.readEntity();

		assertThat(users.stream().map(User::getId).collect(Collectors.toSet()),
				hasItem(withoutTeamUser.getId()));
	}

	@Test
	public void testUsers_GetUsersByIds() {

		ApiResponse<UserList> response = assertNoError(
				client.getUsersByIds(th.basicUser().getId(), th.basicUser2().getId()));
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
		UserSearch criteria = UserSearch.builder().term(th.basicUser().getUsername()).teamId(th.basicTeam().getId())
				.build();

		ApiResponse<UserList> response = assertNoError(client.searchUsers(criteria));
		List<User> users = response.readEntity();

		assertThat(users.stream().map(User::getUsername).collect(Collectors.toSet()),
				hasItem(th.basicUser().getUsername()));
	}

	@Test
	public void testUsers_AutocompleteUsers() {

		ApiResponse<UserAutocomplete> response = assertNoError(
				client.autocompleteUsers(th.basicUser().getUsername(), null));
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
		assertThat(autocompleteUsers.getOutOfChannel().stream().map(User::getId).collect(Collectors.toSet()),
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

		ApiResponse<Boolean> response = assertNoError(
				client.updateUserRoles(th.basicUser().getId(), Role.ROLE_SYSTEM_ADMIN, Role.ROLE_SYSTEM_USER));
		boolean result = response.readEntity();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_UpdateUserActiveStatus() {

		ApiResponse<Boolean> response = assertNoError(client.updateUserActive(th.basicUser().getId(), false));
		boolean result = response.readEntity();

		assertThat(result, is(true));
	}

	@Test
	public void testUsers_GetUserProfileImage() throws FileNotFoundException, IOException {

		ApiResponse<byte[]> response = assertNoError(client.getProfileImage(th.basicUser().getId(), null));
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

		ApiResponse<Boolean> response = assertNoError(client.setProfileImage(th.basicUser().getId(), image));
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

		ApiResponse<Boolean> response = assertNoError(client.updateUserPassword(userId, currentPassword, newPassword));
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
		ApiResponse<SessionList> response = assertNoError(client.getSessions(th.basicUser().getId(), null));
		Session session = response.readEntity()
				.stream()
				.findAny()
				.get();

		ApiResponse<Boolean> response2 = assertNoError(client.revokeSession(session.getUserId(), session.getId()));
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

		ApiResponse<Audits> response = assertNoError(client.getUserAudits(th.basicUser().getId(), 0, 50, null));
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

		ApiResponse<Boolean> response = assertNoError(client.sendVerificationEmail(th.basicUser().getEmail()));
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

		ApiResponse<TeamList> response = assertNoError(client.getAllTeams(null, 0, 60));
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

		assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_TeamExists_Exists() {

		ApiResponse<TeamExists> response = assertNoError(client.teamExists(th.basicTeam().getName(), null));
		TeamExists exists = response.readEntity();

		assertThat(exists.isExists(), is(true));
	}

	@Test
	public void testTeams_TeamExists_NotExists() {

		ApiResponse<TeamExists> response = assertNoError(client.teamExists("fake" + th.generateTestTeamName(), null));
		TeamExists exists = response.readEntity();

		assertThat(exists.isExists(), is(false));
	}

	@Test
	public void testTeams_GetUsersTeams() {
		String userId = th.basicUser().getId();

		ApiResponse<TeamList> response = assertNoError(client.getTeamsForUser(userId, null));
		List<Team> teams = response.readEntity();

		assertThat(teams.stream().map(Team::getId).collect(Collectors.toSet()), hasItem(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_GetTeamMembers() {

		ApiResponse<TeamMemberList> response = assertNoError(
				client.getTeamMembers(th.basicTeam().getId(), 0, 60, null));
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

		ApiResponse<TeamMemberList> response = assertNoError(
				client.addTeamMembers(th.basicTeam().getId(), user1.getId(), user2.getId()));
		List<TeamMember> members = response.readEntity();

		assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
				containsInAnyOrder(user1.getId(), user2.getId()));
	}

	@Test
	public void testTeams_GetTeamMembersForUser() {

		ApiResponse<TeamMemberList> response = assertNoError(
				client.getTeamMembersForUser(th.basicUser().getId(), null));
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

		ApiResponse<TeamMemberList> response = assertNoError(
				client.getTeamMembersByIds(th.basicTeam().getId(), th.basicUser().getId(), th.basicUser2().getId()));
		List<TeamMember> members = response.readEntity();

		assertThat(members.stream().map(TeamMember::getUserId).collect(Collectors.toSet()),
				hasItems(th.basicUser().getId(), th.basicUser2().getId()));
	}

	@Test
	public void testTeams_GetTeamStats() {

		ApiResponse<TeamStats> response = assertNoError(client.getTeamStats(th.basicTeam().getId(), null));
		TeamStats stats = response.readEntity();

		assertThat(stats.getTeamId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_UpdateTeamMemberRoles() {
		th.loginTeamAdmin();

		ApiResponse<Boolean> response = assertNoError(
				client.updateTeamMemberRoles(th.basicTeam().getId(), th.basicUser().getId(), Role.ROLE_TEAM_ADMIN));
		boolean result = response.readEntity();

		assertThat(result, is(true));
	}

	@Test
	public void testTeams_GetTeamUnreadsForUser() {

		ApiResponse<TeamUnreadList> response = assertNoError(client.getTeamUnreadForUser(th.basicUser().getId(), null));
		List<TeamUnread> unreads = response.readEntity();

		unreads.stream().findFirst().ifPresent(u -> assertThat(u.getTeamId(), is(th.basicTeam().getId())));
	}

	@Test
	public void testTeams_GetTeamUnreadsForTeam() {

		ApiResponse<TeamUnread> response = assertNoError(
				client.getTeamUnread(th.basicTeam().getId(), th.basicUser().getId()));
		TeamUnread unread = response.readEntity();

		assertThat(unread.getTeamId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_InviteUsersToTheTeamByEmail() {

		ApiResponse<Boolean> response = assertNoError(
				client.inviteUsersToTeam(th.basicTeam().getId(), Collections.singletonList(th.generateTestEmail())));
		boolean result = response.readEntity();

		assertThat(result, is(true));
	}

	@Test
	@Ignore // Not Implemented
	public void testTeams_ImportTeamFromOtherApplication() {
	}

	@Test
	public void testTeams_GetPublicChannels() {

		ApiResponse<ChannelList> response = assertNoError(
				client.getPublicChannelsForTeam(th.basicTeam().getId(), 0, 60, null));
		List<Channel> channels = response.readEntity();

		assertThat(channels.stream().findAny().get().getTeamId(), is(th.basicTeam().getId()));
	}

	@Test
	public void testTeams_SearchChannels() {
		ChannelSearch search = new ChannelSearch();
		search.setTerm(th.basicChannel().getName());

		ApiResponse<ChannelList> response = assertNoError(client.searchChannels(th.basicTeam().getId(), search));
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

		assertThat(posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet()), hasItem(postId));
	}

	@Test
	@Ignore // TODO
	public void testPosts_GetFlaggedPosts() {
	}

	@Test
	@Ignore // TODO
	public void testPosts_GetFileInfoForPost() {
	}

	@Test
	public void testPosts_GetPostsForChannel() {
		String channelId = th.basicChannel().getId();

		ApiResponse<PostList> response = assertNoError(client.getPostsForChannel(channelId, 0, 60, null));
		PostList posts = response.readEntity();

		assertThat(posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
				hasItem(channelId));
	}

	@Test
	public void testPosts_GetPostsForChannel_Since() {
		String channelId = th.basicChannel().getId();

		ApiResponse<PostList> response = assertNoError(client.getPostsSince(channelId,
				OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toEpochSecond()));
		PostList posts = response.readEntity();

		assertThat(posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
				hasItem(channelId));
	}

}
