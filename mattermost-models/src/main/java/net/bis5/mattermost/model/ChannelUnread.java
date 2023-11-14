/*
 * Copyright (c) 2017-present, Takayuki Maruyama
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
import java.util.Map;
import lombok.Data;

/**
 * Channel unread counts.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class ChannelUnread {

  @JsonProperty("team_id")
  private String teamId;
  @JsonProperty("channel_id")
  private String channelId;
  @JsonProperty("msg_count")
  private long msgCount;
  @JsonProperty("mention_count")
  private long mentionCount;
  @JsonProperty("urgent_mention_count")
  private long urgentMentionCount;
  private Map<String, String> notifyProps;
  /** @since Mattermost Server 5.35 */
  private long mentionCountRoot;
  /** @since Mattermost Server 5.35 */
  private long msgCountRoot;

}
