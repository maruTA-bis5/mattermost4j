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
 * Push notification
 * 
 * @author Takayuki Maruyama
 */
@Data
public class PushNotification {

  @JsonProperty("platform")
  private String platform;
  @JsonProperty("server_id")
  private String serverId;
  @JsonProperty("device_id")
  private String deviceId;
  @JsonProperty("category")
  private String category;
  @JsonProperty("sound")
  private String sound;
  @JsonProperty("message")
  private String message;
  @JsonProperty("badge")
  private int badge;
  @JsonProperty("cont_ava")
  private int contentAvailable;
  @JsonProperty("channel_id")
  private String channelId;
  @JsonProperty("channel_name")
  private String channelName;
  @JsonProperty("type")
  private PushType type;
  @JsonProperty("sender_id")
  private String senderId;
  @JsonProperty("override_username")
  private String overrideUsername;
  @JsonProperty("override_icon_url")
  private String overrideIconUrl;
  @JsonProperty("from_webhook")
  private String fromWebhook;

}
