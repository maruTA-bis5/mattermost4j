/*
 * Copyright (c) 2017 Takayuki Maruyama
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

package net.bis5.mattermost.client4.api;

import java.util.Collection;
import java.util.Map;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelList;
import net.bis5.mattermost.model.ChannelMember;
import net.bis5.mattermost.model.ChannelMembers;
import net.bis5.mattermost.model.ChannelPatch;
import net.bis5.mattermost.model.ChannelSearch;
import net.bis5.mattermost.model.ChannelStats;
import net.bis5.mattermost.model.ChannelUnread;
import net.bis5.mattermost.model.ChannelView;
import net.bis5.mattermost.model.ChannelViewResponse;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.Role;

/**
 * Channel API.
 * 
 * @author Takayuki Maruyama
 */
public interface ChannelApi {

  ApiResponse<Channel> createChannel(Channel channel);

  ApiResponse<Channel> updateChannel(Channel channel);

  ApiResponse<Channel> patchChannel(String channelId, ChannelPatch patch);

  ApiResponse<Channel> createDirectChannel(String userId1, String userId2);

  default ApiResponse<Channel> createGroupChannel(Collection<String> userIds) {
    return createGroupChannel(userIds.toArray(new String[0]));
  }

  ApiResponse<Channel> createGroupChannel(String... userIds);

  default ApiResponse<Channel> getChannel(String channelId) {
    return getChannel(channelId, null);
  }

  ApiResponse<Channel> getChannel(String channelId, String etag);

  default ApiResponse<ChannelStats> getChannelStats(String channelId) {
    return getChannelStats(channelId, null);
  }

  ApiResponse<ChannelStats> getChannelStats(String channelId, String etag);

  default ApiResponse<PostList> getPinnedPosts(String channelId) {
    return getPinnedPosts(channelId, null);
  }

  ApiResponse<PostList> getPinnedPosts(String channelId, String etag);

  default ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId) {
    return getPublicChannelsForTeam(teamId, Pager.defaultPager());
  }

  default ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId, Pager pager) {
    return getPublicChannelsForTeam(teamId, pager, null);
  }

  ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId, Pager pager, String etag);

  default ApiResponse<ChannelList> getPublicChannelsByIdsForTeam(String teamId,
      Collection<String> channelIds) {
    return getPublicChannelsByIdsForTeam(teamId, channelIds.toArray(new String[0]));
  }

  ApiResponse<ChannelList> getPublicChannelsByIdsForTeam(String teamId, String... channelIds);

  default ApiResponse<ChannelList> getChannelsForTeamForUser(String teamId, String userId) {
    return getChannelsForTeamForUser(teamId, userId, null);
  }

  ApiResponse<ChannelList> getChannelsForTeamForUser(String teamId, String userId, String etag);

  ApiResponse<ChannelList> searchChannels(String teamId, ChannelSearch search);

  ApiResponse<Boolean> deleteChannel(String channelId);

  default ApiResponse<Channel> getChannelByName(String channelName, String teamId) {
    return getChannelByName(channelName, teamId, null);
  }

  ApiResponse<Channel> getChannelByName(String channelName, String teamId, String etag);

  default ApiResponse<Channel> getChannelByNameForTeamName(String channelName, String teamName) {
    return getChannelByNameForTeamName(channelName, teamName, null);
  }

  ApiResponse<Channel> getChannelByNameForTeamName(String channelName, String teamName,
      String etag);

  default ApiResponse<ChannelMembers> getChannelMembers(String channelId) {
    return getChannelMembers(channelId, Pager.defaultPager());
  }

  default ApiResponse<ChannelMembers> getChannelMembers(String channelId, Pager pager) {
    return getChannelMembers(channelId, pager, null);
  }

  ApiResponse<ChannelMembers> getChannelMembers(String channelId, Pager pager, String etag);

  default ApiResponse<ChannelMembers> getChannelMembersByIds(String channelId,
      Collection<String> userIds) {
    return getChannelMembersByIds(channelId, userIds.toArray(new String[0]));
  }

  ApiResponse<ChannelMembers> getChannelMembersByIds(String channelId, String... userIds);

  default ApiResponse<ChannelMember> getChannelMember(String channelId, String userId) {
    return getChannelMember(channelId, userId, null);
  }

  ApiResponse<ChannelMember> getChannelMember(String channelId, String userId, String etag);

  default ApiResponse<ChannelMembers> getChannelMembersForUser(String userId, String teamId) {
    return getChannelMembersForUser(userId, teamId, null);
  }

  ApiResponse<ChannelMembers> getChannelMembersForUser(String userId, String teamId, String etag);

  ApiResponse<ChannelViewResponse> viewChannel(String userId, ChannelView view);

  ApiResponse<ChannelUnread> getChannelUnread(String channelId, String userId);

  default ApiResponse<Boolean> updateChannelRoles(String channelId, String userId,
      Collection<Role> roles) {
    return updateChannelRoles(channelId, userId, roles.toArray(new Role[0]));
  }

  ApiResponse<Boolean> updateChannelRoles(String channelId, String userId, Role... roles);

  ApiResponse<Boolean> updateChannelNotifyProps(String channeLId, String userId,
      Map<String, String> props);

  ApiResponse<ChannelMember> addChannelMember(String channelId, String userId);

  ApiResponse<Boolean> removeUserFromChannel(String channelId, String userId);

  ApiResponse<Channel> restoreChannel(String channelId);

  default ApiResponse<ChannelList> getDeletedChannels(String teamId) {
    return getDeletedChannels(teamId, Pager.defaultPager());
  }

  ApiResponse<ChannelList> getDeletedChannels(String teamId, Pager pager);

}
