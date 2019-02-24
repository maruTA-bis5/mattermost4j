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

import java.sql.Date;
import java.time.ZonedDateTime;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.FileInfo;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;
import net.bis5.mattermost.model.PostSearchResults;

/**
 * Post API.
 * 
 * @author Takayuki Maruyama
 */
public interface PostApi {

  /**
   * creates a post based on the provided post object.
   */
  ApiResponse<Post> createPost(Post post);

  /**
   * updates a post based on the provided post object.
   */
  default ApiResponse<Post> updatePost(Post post) {
    return updatePost(post.getId(), post);
  }

  /**
   * updates a post based on the provided post object.
   */
  ApiResponse<Post> updatePost(String postId, Post post);

  /**
   * partially updates a post. Any missing fields are not updated.
   */
  ApiResponse<Post> patchPost(String postId, PostPatch patch);

  /**
   * pin a post based on proviced post id string.
   */
  ApiResponse<Boolean> pinPost(String postId);

  /**
   * unpin a post based on provided post id string.
   */
  ApiResponse<Boolean> unpinPost(String postId);

  /**
   * gets a single post.
   */
  default ApiResponse<Post> getPost(String postId) {
    return getPost(postId, null);
  }

  /**
   * gets a single post.
   */
  ApiResponse<Post> getPost(String postId, String etag);

  /**
   * deletes a post from the provided post id string.
   */
  ApiResponse<Boolean> deletePost(String postId);

  /**
   * gets a post with all the other posts in the same thread.
   */
  default ApiResponse<PostList> getPostThread(String postId) {
    return getPostThread(postId, null);
  }

  /**
   * gets a post with all the other posts in the same thread.
   */
  ApiResponse<PostList> getPostThread(String postId, String etag);

  /**
   * gets a page of posts with an array for ordering for a channel.
   */
  default ApiResponse<PostList> getPostsForChannel(String channelId) {
    return getPostsForChannel(channelId, Pager.defaultPager());
  }

  /**
   * gets a page of posts with an array for ordering for a channel.
   */
  default ApiResponse<PostList> getPostsForChannel(String channelId, Pager pager) {
    return getPostsForChannel(channelId, pager, null);
  }

  /**
   * gets a page of posts with an array for ordering for a channel.
   */
  ApiResponse<PostList> getPostsForChannel(String channelId, Pager pager, String etag);

  /**
   * returns flagges posts of a user based on user id string.
   */
  default ApiResponse<PostList> getFlaggedPostsForUser(String userId) {
    return getFlaggedPostsForUser(userId, Pager.defaultPager());
  }

  /**
   * returns flagges posts of a user based on user id string.
   */
  ApiResponse<PostList> getFlaggedPostsForUser(String userId, Pager pager);

  /**
   * returns flagged posts in team of a user based on user id string.
   */
  default ApiResponse<PostList> getFlaggedPostsForUserInTeam(String userId, String teamId) {
    return getFlaggedPostsForUserInTeam(userId, teamId, Pager.defaultPager());
  }

  /**
   * returns flagged posts in team of a user based on user id string.
   */
  ApiResponse<PostList> getFlaggedPostsForUserInTeam(String userId, String teamId, Pager pager);

  /**
   * returns flagged posts in channel of a user based on user id string.
   */
  default ApiResponse<PostList> getFlaggedPostsForUserInChannel(String userId, String channelId) {
    return getFlaggedPostsForUserInChannel(userId, channelId, Pager.defaultPager());
  }

  /**
   * returns flagged posts in channel of a user based on user id string.
   */
  ApiResponse<PostList> getFlaggedPostsForUserInChannel(String userId, String channelId,
      Pager pager);

  /**
   * gets posts created after a specified time as Unix time in milliseconds.
   */
  default ApiResponse<PostList> getPostsSince(String channelId, Date since) {
    return getPostsSince(channelId, since.getTime());
  }

  /**
   * gets posts created after a specified time as Unix time in milliseconds.
   */
  default ApiResponse<PostList> getPostsSince(String channelId, ZonedDateTime since) {
    return getPostsSince(channelId, since.toInstant().toEpochMilli());
  }

  /**
   * gets posts created after a specified time as Unix time in milliseconds.
   */
  ApiResponse<PostList> getPostsSince(String channelId, long since);

  /**
   * gets a page of posts that were posted after the post provided.
   */
  default ApiResponse<PostList> getPostsAfter(String channelId, String postId) {
    return getPostsAfter(channelId, postId, Pager.defaultPager());
  }

  /**
   * gets a page of posts that were posted after the post provided.
   */
  default ApiResponse<PostList> getPostsAfter(String channelId, String postId, Pager pager) {
    return getPostsAfter(channelId, postId, pager, null);
  }

  /**
   * gets a page of posts that were posted after the post provided.
   */
  ApiResponse<PostList> getPostsAfter(String channelId, String postId, Pager pager, String etag);

  /**
   * gets a page of posts that were posted before the post provided.
   */
  default ApiResponse<PostList> getPostsBefore(String channelId, String postId) {
    return getPostsBefore(channelId, postId, Pager.defaultPager());
  }

  /**
   * gets a page of posts that were posted before the post provided.
   */
  default ApiResponse<PostList> getPostsBefore(String channelId, String postId, Pager pager) {
    return getPostsBefore(channelId, postId, pager, null);
  }

  /**
   * gets a page of posts that were posted before the post provided.
   */
  ApiResponse<PostList> getPostsBefore(String channelId, String postId, Pager pager, String etag);

  /**
   * returns any posts with matching term string.
   */
  default ApiResponse<PostSearchResults> searchPosts(String teamId, String terms) {
    return searchPosts(teamId, terms, false);
  }

  /**
   * returns any posts with matching term string.
   */
  ApiResponse<PostSearchResults> searchPosts(String teamId, String terms, boolean isOrSearch);

  /**
   * get a list of file info attached the post.
   */
  ApiResponse<FileInfo[]> getFileInfoForPost(String postId);

}
