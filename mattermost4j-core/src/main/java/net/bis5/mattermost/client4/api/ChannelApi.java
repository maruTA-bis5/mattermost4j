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

  /**
   * creates a channel based on the provided channel object.
   */
  ApiResponse<Channel> createChannel(Channel channel);

  /**
   * update a channel based on the provided channel object.
   */
  ApiResponse<Channel> updateChannel(Channel channel);

  /**
   * partially updates a channel. Any missing fields are not updated.
   */
  ApiResponse<Channel> patchChannel(String channelId, ChannelPatch patch);

  /**
   * creates a direct message channel based on the two user ids provided.
   */
  ApiResponse<Channel> createDirectChannel(String userId1, String userId2);

  /**
   * creates a group message channel based on userIds provided.
   */
  default ApiResponse<Channel> createGroupChannel(Collection<String> userIds) {
    return createGroupChannel(userIds.toArray(new String[0]));
  }

  /**
   * creates a group message channel based on userIds provided.
   */
  ApiResponse<Channel> createGroupChannel(String... userIds);

  /**
   * returns a channel based on the provided channel id string.
   */
  default ApiResponse<Channel> getChannel(String channelId) {
    return getChannel(channelId, null);
  }

  /**
   * returns a channel based on the provided channel id string.
   */
  ApiResponse<Channel> getChannel(String channelId, String etag);

  /**
   * returns statistics for a channel.
   */
  default ApiResponse<ChannelStats> getChannelStats(String channelId) {
    return getChannelStats(channelId, null);
  }

  /**
   * returns statistics for a channel.
   */
  ApiResponse<ChannelStats> getChannelStats(String channelId, String etag);

  /**
   * gets a list of pinned posts.
   */
  default ApiResponse<PostList> getPinnedPosts(String channelId) {
    return getPinnedPosts(channelId, null);
  }

  /**
   * gets a list of pinned posts.
   */
  ApiResponse<PostList> getPinnedPosts(String channelId, String etag);

  /**
   * returns a list of public channels based on the provided team id string.
   */
  default ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId) {
    return getPublicChannelsForTeam(teamId, Pager.defaultPager());
  }

  /**
   * returns a list of public channels based on the provided team id string.
   */
  default ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId, Pager pager) {
    return getPublicChannelsForTeam(teamId, pager, null);
  }

  /**
   * returns a list of public channels based on the provided team id string.
   */
  ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId, Pager pager, String etag);

  /**
   * returns a list of public channeld based on provided team id string.
   */
  default ApiResponse<ChannelList> getPublicChannelsByIdsForTeam(String teamId,
      Collection<String> channelIds) {
    return getPublicChannelsByIdsForTeam(teamId, channelIds.toArray(new String[0]));
  }

  /**
   * returns a list of public channeld based on provided team id string.
   */
  ApiResponse<ChannelList> getPublicChannelsByIdsForTeam(String teamId, String... channelIds);

  /**
   * returns a list channels of on a team for user.
   */
  default ApiResponse<ChannelList> getChannelsForTeamForUser(String teamId, String userId) {
    return getChannelsForTeamForUser(teamId, userId, null);
  }

  /**
   * returns a list channels of on a team for user.
   */
  ApiResponse<ChannelList> getChannelsForTeamForUser(String teamId, String userId, String etag);

  /**
   * returns the channels on a team matching the provided search term.
   */
  ApiResponse<ChannelList> searchChannels(String teamId, ChannelSearch search);

  /**
   * deletes channel based on the provided channel id string.
   */
  ApiResponse<Boolean> deleteChannel(String channelId);

  /**
   * returns a channel based on the provided channel name and team id strings.
   */
  default ApiResponse<Channel> getChannelByName(String channelName, String teamId) {
    return getChannelByName(channelName, teamId, null);
  }

  /**
   * returns a channel based on the provided channel name and team id strings.
   */
  ApiResponse<Channel> getChannelByName(String channelName, String teamId, String etag);

  /**
   * returns a channel based on the provided channel name and team name strings.
   */
  default ApiResponse<Channel> getChannelByNameForTeamName(String channelName, String teamName) {
    return getChannelByNameForTeamName(channelName, teamName, null);
  }

  /**
   * returns a channel based on the provided channel name and team name strings.
   */
  ApiResponse<Channel> getChannelByNameForTeamName(String channelName, String teamName,
      String etag);

  /**
   * gets a page of channel members.
   */
  default ApiResponse<ChannelMembers> getChannelMembers(String channelId) {
    return getChannelMembers(channelId, Pager.defaultPager());
  }

  /**
   * gets a page of channel members.
   */
  default ApiResponse<ChannelMembers> getChannelMembers(String channelId, Pager pager) {
    return getChannelMembers(channelId, pager, null);
  }

  /**
   * gets a page of channel members.
   */
  ApiResponse<ChannelMembers> getChannelMembers(String channelId, Pager pager, String etag);

  /**
   * gets the channel members in a channel for a list of user ids.
   */
  default ApiResponse<ChannelMembers> getChannelMembersByIds(String channelId,
      Collection<String> userIds) {
    return getChannelMembersByIds(channelId, userIds.toArray(new String[0]));
  }

  /**
   * gets the channel members in a channel for a list of user ids.
   */
  ApiResponse<ChannelMembers> getChannelMembersByIds(String channelId, String... userIds);

  /**
   * gets a channel memner.
   */
  default ApiResponse<ChannelMember> getChannelMember(String channelId, String userId) {
    return getChannelMember(channelId, userId, null);
  }

  /**
   * gets a channel memner.
   */
  ApiResponse<ChannelMember> getChannelMember(String channelId, String userId, String etag);

  /**
   * gets all the channel members for a user on a team.
   */
  default ApiResponse<ChannelMembers> getChannelMembersForUser(String userId, String teamId) {
    return getChannelMembersForUser(userId, teamId, null);
  }

  /**
   * gets all the channel members for a user on a team.
   */
  ApiResponse<ChannelMembers> getChannelMembersForUser(String userId, String teamId, String etag);

  /**
   * performs a view action for a user. synonymous with switching channels or marking channels as
   * read by a user.
   */
  ApiResponse<ChannelViewResponse> viewChannel(String userId, ChannelView view);

  /**
   * will return a ChannelUnread object that contains the number ofo unread messages and mentions
   * for a user.
   */
  ApiResponse<ChannelUnread> getChannelUnread(String channelId, String userId);

  /**
   * will update the roles on a channel for a user.
   */
  default ApiResponse<Boolean> updateChannelRoles(String channelId, String userId,
      Collection<Role> roles) {
    return updateChannelRoles(channelId, userId, roles.toArray(new Role[0]));
  }

  /**
   * will update the roles on a channel for a user.
   */
  ApiResponse<Boolean> updateChannelRoles(String channelId, String userId, Role... roles);

  /**
   * will update the notification properties on a channel for a user.
   */
  ApiResponse<Boolean> updateChannelNotifyProps(String channeLId, String userId,
      Map<String, String> props);

  /**
   * adds user to channel and return a channel memner.
   */
  ApiResponse<ChannelMember> addChannelMember(String channelId, String userId);

  /**
   * will delete the channel member object for a user, effectively removing the user from a channel.
   */
  ApiResponse<Boolean> removeUserFromChannel(String channelId, String userId);

  ApiResponse<Channel> restoreChannel(String channelId);

  default ApiResponse<ChannelList> getDeletedChannels(String teamId) {
    return getDeletedChannels(teamId, Pager.defaultPager());
  }

  ApiResponse<ChannelList> getDeletedChannels(String teamId, Pager pager);

  /**
   * convert channel from public to private.
   * 
   * @deprecated /convert API is no longer supported by Mattermost Server 6.x or later.
   */
  @Deprecated
  ApiResponse<Channel> convertChannelToPrivate(String channelId);

  /**
   * autocomplete channels in team based on search term.
   * @since Mattermost Server 4.7
   */
  ApiResponse<ChannelList> autocompleteChannels(String teamId, String searchTerm);

  /**
   * autocomplete your (joined) channels in team based on search term.
   * @since Mattermost Server 5.4
   */
  ApiResponse<ChannelList> autocompleteChannelsForSearch(String teamId, String searchTerm);

}
