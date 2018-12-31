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
import java.util.List;
import lombok.Data;

/**
 * Outgoing webhook
 * 
 * @author Takayuki Maruyama
 */
@Data
public class OutgoingWebhook {

  @JsonProperty("id")
  private String id;
  @JsonProperty("token")
  private String token;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("update_at")
  private long updateAt;
  @JsonProperty("delete_at")
  private long deleteAt;
  @JsonProperty("creator_id")
  private String creatorId;
  @JsonProperty("channel_id")
  private String channelId;
  @JsonProperty("team_id")
  private String teamId;
  @JsonProperty("trigger_words")
  private List<String> triggerWords;
  @JsonProperty("trigger_when")
  private TriggerWhen triggerWhen;
  @JsonProperty("callback_urls")
  private List<String> callbackUrls;
  @JsonProperty("display_name")
  private String displayName;
  @JsonProperty("description")
  private String description;
  @JsonProperty("content_type")
  private ContentType contentType;
  /** @since Mattermost Server XXX what ver? */
  private String username;
  /** @since Mattermost Server XXX what ver? */
  private String iconUrl;

}
