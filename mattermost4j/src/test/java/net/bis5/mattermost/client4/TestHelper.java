/*
 * @(#) net.bis5.mattermost.client4.TestHelper
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

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.bis5.mattermost.client4.model.ApiError;
import net.bis5.mattermost.model.Channel;
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
 * @author Maruyama Takayuki
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
		// TODO un-comment these lines when Dockerfile setup.
		// config.getEmailSettings().setSendEmailNotifications(true);
		// config.getEmailSettings().setSmtpServer("localhost");
		// config.getEmailSettings().setSmtpPort("9000");
		// config.getEmailSettings().setFeedbackEmail("test@example.com");
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

	protected String newId() {
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
		user.setPassword("Password1");
		user.setRoles(Arrays.asList(Role.ROLE_SYSTEM_ADMIN, Role.ROLE_SYSTEM_USER).stream().map(r -> r.getId())
				.collect(Collectors.joining(" ")));

		user = checkNoError(client.createUser(user)).readEntity();
		user.setPassword("Password1");
		return user;
	}

	public User createUser() {
		String id = newId();

		User user = new User();
		user.setEmail(generateTestEmail());
		user.setUsername(generateTestUsername());
		user.setNickname("nn_" + id);
		user.setFirstName("f_" + id);
		user.setLastName("l_" + id);
		user.setPassword("Password1");

		user = checkNoError(client.createUser(user)).readEntity();
		user.setPassword("Password1");
		return user;
	}

	public Team createTeam() {
		String id = newId();

		Team team = new Team();
		team.setDisplayName("dn_" + id);
		team.setName(generateTestTeamName());
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

	protected String newRandomString(int length) {
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

	protected TestHelper loginAs(User user) {
		client.login(user.getEmail(), user.getPassword());
		return this;
	}

	protected TestHelper linkUserToTeam(User user, Team team) {
		checkNoError(client.addTeamMembers(team.getId(), user.getId()));
		return this;
	}

	public TestHelper updateUserRoles(String userId, Role... roles) {
		checkNoError(client.updateUserRoles(userId, roles));
		return this;
	}

	public TestHelper updateUserToNonTeamAdmin(User user, Team team) {
		checkNoError(client.updateTeamMemberRoles(team.getId(), user.getId(), Role.ROLE_TEAM_USER));
		return this;
	}

	// FIXME ApiResponeの責務
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
