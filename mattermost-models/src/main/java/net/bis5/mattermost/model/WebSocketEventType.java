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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import net.bis5.mattermost.model.WebSocketEventType.WebSocketEventTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * Type of Websocket event
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = WebSocketEventTypeDeserializer.class)
public enum WebSocketEventType implements HasCode<WebSocketEventType> {

  Typing("typing"), //
  Posted("posted"), //
  PostEdited("post_edited"), //
  PostDeleted("post_deleted"), //
  ChannelDeleted("channel_deleted"), //
  ChannelCreated("channel_created"), //
  DirectAdded("direct_added"), //
  GroupAdded("group_added"), //
  NewUser("new_user"), //
  AddedToTeam("added_to_team"), //
  LeaveTeam("leave_team"), //
  UpdateTeam("update_team"), //
  UserAdded("user_added"), //
  UserUpdated("user_updated"), //
  UserRemoved("user_removed"), //
  PreferenceChanged("preference_changed"), //
  PreferencesChanged("preferences_changed"), //
  PreferencesDeleted("preferences_deleted"), //
  EphemeralMessage("ephemeral_message"), //
  StatusChange("status_change"), //
  Hello("hello"), //
  Webrtc("webrtc"), //
  ReactionAdded("reaction_added"), //
  ReactionRemoved("reaction_removed"), //
  Response("response");

  private final String code;

  WebSocketEventType(String code) {
    this.code = code;
  }

  /**
   * @see net.bis5.mattermost.model.HasCode#getCode()
   */
  @Override
  public String getCode() {
    return code;
  }

  public static WebSocketEventType of(String code) {
    for (WebSocketEventType type : WebSocketEventType.values()) {
      if (type.getCode().equals(code)) {
        return type;
      }
    }
    return null;
  }

  public static class WebSocketEventTypeDeserializer extends JsonDeserializer<WebSocketEventType> {

    /**
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
     *      com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public WebSocketEventType deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      String code = p.getText();
      return WebSocketEventType.of(code);
    }

  }
}
