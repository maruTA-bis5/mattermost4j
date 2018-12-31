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
import java.time.OffsetDateTime;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;

/**
 * Post API
 * 
 * @author Takayuki Maruyama
 */
public interface PostApi {

  ApiResponse<Post> createPost(Post post);

  default ApiResponse<Post> updatePost(Post post) {
    return updatePost(post.getId(), post);
  }

  ApiResponse<Post> updatePost(String postId, Post post);

  ApiResponse<Post> patchPost(String postId, PostPatch patch);

  ApiResponse<Boolean> pinPost(String postId);

  ApiResponse<Boolean> unpinPost(String postId);

  default ApiResponse<Post> getPost(String postId) {
    return getPost(postId, null);
  }

  ApiResponse<Post> getPost(String postId, String etag);

  ApiResponse<Boolean> deletePost(String postId);

  default ApiResponse<PostList> getPostThread(String postId) {
    return getPostThread(postId, null);
  }

  ApiResponse<PostList> getPostThread(String postId, String etag);

  default ApiResponse<PostList> getPostsForChannel(String channelId) {
    return getPostsForChannel(channelId, Pager.defaultPager());
  }

  default ApiResponse<PostList> getPostsForChannel(String channelId, Pager pager) {
    return getPostsForChannel(channelId, pager, null);
  }

  ApiResponse<PostList> getPostsForChannel(String channelId, Pager pager, String etag);

  default ApiResponse<PostList> getFlaggedPostsForUser(String userId) {
    return getFlaggedPostsForUser(userId, Pager.defaultPager());
  }

  ApiResponse<PostList> getFlaggedPostsForUser(String userId, Pager pager);

  default ApiResponse<PostList> getFlaggedPostsForUserInTeam(String userId, String teamId) {
    return getFlaggedPostsForUserInTeam(userId, teamId, Pager.defaultPager());
  }

  ApiResponse<PostList> getFlaggedPostsForUserInTeam(String userId, String teamId, Pager pager);

  default ApiResponse<PostList> getFlaggedPostsForUserInChannel(String userId, String channelId) {
    return getFlaggedPostsForUserInChannel(userId, channelId, Pager.defaultPager());
  }

  ApiResponse<PostList> getFlaggedPostsForUserInChannel(String userId, String channelId,
      Pager pager);

  default ApiResponse<PostList> getPostsSince(String channelId, Date since) {
    return getPostsSince(channelId, since.getTime());
  }

  default ApiResponse<PostList> getPostsSince(String channelId, OffsetDateTime since) {
    return getPostsSince(channelId, since.toEpochSecond());
  }

  ApiResponse<PostList> getPostsSince(String channelId, long since);

  default ApiResponse<PostList> getPostsAfter(String channelId, String postId) {
    return getPostsAfter(channelId, postId, Pager.defaultPager());
  }

  default ApiResponse<PostList> getPostsAfter(String channelId, String postId, Pager pager) {
    return getPostsAfter(channelId, postId, pager, null);
  }

  ApiResponse<PostList> getPostsAfter(String channelId, String postId, Pager pager, String etag);

  default ApiResponse<PostList> getPostsBefore(String channelId, String postId) {
    return getPostsBefore(channelId, postId, Pager.defaultPager());
  }

  default ApiResponse<PostList> getPostsBefore(String channelId, String postId, Pager pager) {
    return getPostsBefore(channelId, postId, pager, null);
  }

  ApiResponse<PostList> getPostsBefore(String channelId, String postId, Pager pager, String etag);

  default ApiResponse<PostList> searchPosts(String teamId, String terms) {
    return searchPosts(teamId, terms, false);
  }

  ApiResponse<PostList> searchPosts(String teamId, String terms, boolean isOrSearch);

}
