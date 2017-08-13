/*
 * @(#) net.bis5.mattermost.client4.TestHelper
 * Copyright (c) 2017 Maruyama Takayuki <bis5.wsys@gmail.com>
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

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.bis5.mattermost.client4.model.ApiError;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelMember;
import net.bis5.mattermost.model.ChannelType;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamType;
import net.bis5.mattermost.model.User;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki <bis5.wsys@gmail.com>
 * @since 2017/06/19
 */
@Accessors(chain = true, fluent = true)
@Getter
public class TestHelper {

	private MattermostClient client;

	public TestHelper(MattermostClient client) {
		this.client = client;
	}

	TestHelper changeClient(MattermostClient client) {
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

	public TestHelper setup() throws InterruptedException, ExecutionException {
		initSystemAdmin();
		Config config = client.getConfig().thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
		config.getTeamSettings().setMaxUsersPerTeam(50);
		config.getRateLimitSettings().setEnable(false);
		// TODO un-comment these lines when Dockerfile setup.
		// config.getEmailSettings().setSendEmailNotifications(true);
		// config.getEmailSettings().setSmtpServer("localhost");
		// config.getEmailSettings().setSmtpPort("9000");
		// config.getEmailSettings().setFeedbackEmail("test@example.com");
		config.getTeamSettings().setEnableOpenServer(true);
		client.updateConfig(config).toCompletableFuture().get();
		client.logout();
		return this;
	}

	public TestHelper initBasic() throws InterruptedException, ExecutionException {
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
		CompletableFuture<ApiResponse<ChannelMember>> basicUserCh1 = client.addChannelMember(basicChannel.getId(),
				basicUser.getId()).toCompletableFuture();
		CompletableFuture<ApiResponse<ChannelMember>> basicUser2Ch1 = client
				.addChannelMember(basicChannel.getId(), basicUser2.getId()).toCompletableFuture();
		CompletableFuture<ApiResponse<ChannelMember>> basicUserCh2 = client
				.addChannelMember(basicChannel2.getId(), basicUser.getId()).toCompletableFuture();
		CompletableFuture<ApiResponse<ChannelMember>> basicUser2Ch2 = client
				.addChannelMember(basicChannel2.getId(), basicUser2.getId()).toCompletableFuture();
		CompletableFuture<ApiResponse<ChannelMember>> basicUserPrivCh = client
				.addChannelMember(basicPrivateChannel.getId(), basicUser.getId()).toCompletableFuture();
		CompletableFuture<ApiResponse<ChannelMember>> basicUser2PrivCh = client
				.addChannelMember(basicPrivateChannel.getId(), basicUser2.getId()).toCompletableFuture();
		CompletableFuture
				.allOf(basicUserCh1, basicUser2Ch1, basicUserCh2, basicUser2Ch2, basicUserPrivCh, basicUser2PrivCh)
				.get();

		// linkUserToTeam(systemAdminUser, basicTeam);
		loginBasic();
		return this;
	}

	public TestHelper initSystemAdmin() throws InterruptedException, ExecutionException {
		if (systemAdminUser != null) {
			loginAs(systemAdminUser);
			return this;
		}
		systemAdminUser = createSystemAdminUser();
		loginAs(systemAdminUser);
		return this;
	}

	protected String newId() {
		return newRandomString(26);
	}

	public User createSystemAdminUser() throws InterruptedException, ExecutionException {
		String id = newId();

		User user = new User();
		user.setEmail(generateTestEmail());
		user.setUsername(generateTestUsername());
		user.setNickname("nn_" + id);
		user.setFirstName("f_" + id);
		user.setLastName("l_" + id);
		user.setPassword("Password1");
		user.setRoles(Arrays.asList(Role.ROLE_SYSTEM_ADMIN, Role.ROLE_SYSTEM_USER).stream().map(r -> r.getId())
				.collect(Collectors.joining(" ")));

		user = client.createUser(user).thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
		user.setPassword("Password1");
		return user;
	}

	public User createUser() throws InterruptedException, ExecutionException {
		String id = newId();

		User user = new User();
		user.setEmail(generateTestEmail());
		user.setUsername(generateTestUsername());
		user.setNickname("nn_" + id);
		user.setFirstName("f_" + id);
		user.setLastName("l_" + id);
		user.setPassword("Password1");

		user = client.createUser(user).thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
		user.setPassword("Password1");
		return user;
	}

	public Team createTeam() throws InterruptedException, ExecutionException {
		String id = newId();

		Team team = new Team();
		team.setDisplayName("dn_" + id);
		team.setName(generateTestTeamName());
		team.setType(TeamType.OPEN);
		team.setAllowOpenInvite(true);

		team = client.createTeam(team).thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
		return team;
	}

	public Channel createPublicChannel() throws InterruptedException, ExecutionException {
		return createChannel(ChannelType.Open);
	}

	public Channel createPrivateChannel() throws InterruptedException, ExecutionException {
		return createChannel(ChannelType.Private);
	}

	protected Channel createChannel(ChannelType type) throws InterruptedException, ExecutionException {
		String id = newId();

		Channel channel = new Channel();
		channel.setDisplayName("dn_" + id);
		channel.setName(generateTestChannelName());
		channel.setType(type);
		channel.setTeamId(basicTeam.getId());

		channel = client.createChannel(channel).thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
		return channel;
	}

	public Post createPost(Channel channel) throws InterruptedException, ExecutionException {
		String id = newId();

		Post post = new Post();
		post.setChannelId(channel.getId());
		post.setMessage("message_" + id);

		post = client.createPost(post).thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
		return post;
	}

	public Post createPinnedPost(String channelId) throws InterruptedException, ExecutionException {
		String id = newId();

		Post post = new Post();
		post.setChannelId(channelId);
		post.setMessage("message_" + id);
		post.setPinned(true);

		post = client.createPost(post).thenApply(this::checkNoError).toCompletableFuture().get().readEntity();
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

	protected String newRandomString(int length) {
		return RandomStringUtils.randomAlphanumeric(length).toLowerCase();
	}

	public TestHelper loginTeamAdmin() throws InterruptedException, ExecutionException {
		loginAs(teamAdminUser);
		return this;
	}

	public TestHelper loginBasic() throws InterruptedException, ExecutionException {
		loginAs(basicUser);
		return this;
	}

	public TestHelper loginBasic2() throws InterruptedException, ExecutionException {
		loginAs(basicUser2);
		return this;
	}

	public TestHelper loginSystemAdmin() throws InterruptedException, ExecutionException {
		loginAs(systemAdminUser);
		return this;
	}

	public TestHelper logout() throws InterruptedException, ExecutionException {
		client.logout().toCompletableFuture().get();
		return this;
	}

	protected TestHelper loginAs(User user) throws InterruptedException, ExecutionException {
		client.login(user.getEmail(), user.getPassword()).toCompletableFuture().get();
		return this;
	}

	protected TestHelper linkUserToTeam(User user, Team team) throws InterruptedException, ExecutionException {
		client.addTeamMembers(team.getId(), user.getId()).thenApply(this::checkNoError).toCompletableFuture().get();
		return this;
	}

	public TestHelper updateUserRoles(String userId, Role... roles) throws InterruptedException, ExecutionException {
		client.updateUserRoles(userId, roles).thenApply(this::checkNoError).toCompletableFuture().get();
		return this;
	}

	public TestHelper updateUserToNonTeamAdmin(User user, Team team) throws InterruptedException, ExecutionException {
		client.updateTeamMemberRoles(team.getId(), user.getId(), Role.ROLE_TEAM_USER).thenApply(this::checkNoError)
				.toCompletableFuture().get();
		return this;
	}

	public <T> ApiResponse<T> checkNoError(ApiResponse<T> response) {
		response.getRawResponse().bufferEntity();
		try {
			ApiError error = response.readError();
			throw new AssertionError("Expected no error, got " + error);
		} catch (ProcessingException ex) {
			// no error
		}
		return response;
	}
}
