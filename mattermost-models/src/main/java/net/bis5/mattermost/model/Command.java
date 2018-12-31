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
import lombok.NoArgsConstructor;

/**
 * Command
 * 
 * @author Takayuki Maruyama
 */
@Data
@NoArgsConstructor
public class Command {

  public Command(String creatorId, String teamId, String url, CommandMethod method,
      String trigger) {
    this.creatorId = creatorId;
    this.teamId = teamId;
    this.url = url;
    this.method = method;
    this.trigger = trigger;
  }

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
  @JsonProperty("team_id")
  private String teamId;
  @JsonProperty("trigger")
  private String trigger;
  @JsonProperty("method")
  private CommandMethod method;
  @JsonProperty("username")
  private String userName;
  @JsonProperty("icon_url")
  private String iconUrl;
  @JsonProperty("auto_complete")
  private boolean autoComplete;
  @JsonProperty("auto_complete_desc")
  private String autoCompleteDesc;
  @JsonProperty("auto_complete_hint")
  private String autoCompleteHint;
  @JsonProperty("display_name")
  private String displayName;
  @JsonProperty("description")
  private String description;
  @JsonProperty("url")
  private String url;
}
