/*
 * Copyright (c) 2016-present, Takayuki Maruyama
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

package net.bis5.mattermost.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Post.
 * 
 * @author Takayuki Maruyama
 */
@Data
@NoArgsConstructor
public class Post {

  public Post(String channelId, String message) {
    this.channelId = channelId;
    this.message = message;
  }

  @JsonProperty("id")
  private String id;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("update_at")
  private long updateAt;
  @JsonProperty("edit_at")
  private long editAt;
  @JsonProperty("delete_at")
  private long deleteAt;
  @JsonProperty("is_pinned")
  private boolean isPinned;
  @JsonProperty("user_id")
  private String userId;
  @JsonProperty("channel_id")
  private String channelId;
  @JsonProperty("root_id")
  private String rootId;
  @JsonProperty("parent_id")
  private String parentId;
  @JsonProperty("original_id")
  private String originalId;
  @JsonProperty("message")
  private String message;
  @JsonProperty("type")
  private PostType type;
  @JsonProperty("props")
  private Map<String, Object> props;
  @JsonProperty("hashtags")
  private String hashtags;
  @JsonProperty("filenames")
  @Deprecated // do not use this field any more
  private List<String> filenames;
  @JsonProperty("file_ids")
  private List<String> fileIds;
  @JsonProperty("pending_post_id")
  private String pendingPostId;
  @JsonProperty("has_reactions")
  private boolean hasReactions;
  /* @since Mattermost Server 5.8 */
  private PostMetadata metadata;
  /* @since Mattermost Server 5.22 */
  private long replyCount;
  /** @since Mattermost Server 5.32 */
  private long lastReplyAt;
  /** @since Mattermost Server 5.32 */
  private List<User> participants = new ArrayList<>();

}
