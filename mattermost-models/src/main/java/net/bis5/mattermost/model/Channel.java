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
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Channel.
 * 
 * @author Takayuki Maruyama
 */
@Data
@NoArgsConstructor
public class Channel {

  public static final String DEFAULT_CHANNEL_NAME = "town-square";

  /**
   * The convenience constructor to create new channel.
   */
  public Channel(String displayName, String name, ChannelType type, String teamId) {
    this.displayName = displayName;
    this.name = name;
    this.type = type;
    this.teamId = teamId;
  }

  @JsonProperty("id")
  private String id;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("update_at")
  private long updateAt;
  @JsonProperty("delete_at")
  private long deleteAt;
  @JsonProperty("team_id")
  private String teamId;
  @JsonProperty("type")
  private ChannelType type;
  @JsonProperty("display_name")
  private String displayName;
  @JsonProperty("name")
  private String name;
  @JsonProperty("header")
  private String header;
  @JsonProperty("purpose")
  private String purpose;
  @JsonProperty("last_post_at")
  private long lastPostat;
  @JsonProperty("total_msg_count")
  private long totalMsgCount;
  @JsonProperty("extra_update_at")
  private long extraUpdateAt;
  @JsonProperty("creator_id")
  private String creatorId;
  /* @since Mattermost Server XXX what ver? */
  private String schemeId;
  /* @since Mattermost Server XXX what ver? */
  private Map<String, Object> props;
  /* @since Mattermost Server 5.10 */
  private boolean groupConstrained;
  /* @since Mattermost Server 5.32 */
  private boolean shared;

}
