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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * File info.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class FileInfo {

  @JsonProperty("id")
  private String id;
  @JsonProperty("user_id")
  private String creatorId;
  @JsonProperty("post_id")
  private String postId;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("update_at")
  private long updateAt;
  @JsonProperty("delete_at")
  private long deleteAt;
  private transient String path; // not sent back to the client
  private transient String thumbnailPath; // not sent back to the client
  private transient String previewPath; // not sent back to the client
  @JsonProperty("name")
  private String name;
  @JsonProperty("extension")
  private String extension;
  @JsonProperty("size")
  private long size;
  @JsonProperty("mime_type")
  private String mimeType;
  @JsonProperty("width")
  private int width;
  @JsonProperty("height")
  private int height;
  @JsonProperty("has_preview_image")
  private boolean hasPreviewImage;
  /** @since Mattermost Server 5.30 */
  private byte[] miniPreview;
  /** @since Mattermost Server 5.35 */
  private String channelId;
  /** @since Mattermost Server 5.35 */
  private String remoteId;

}
