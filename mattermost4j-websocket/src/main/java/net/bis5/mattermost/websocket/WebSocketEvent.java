/*
 * Copyright (c) 2019 Takayuki Maruyama
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

package net.bis5.mattermost.websocket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Arrays;
import lombok.Getter;
import net.bis5.mattermost.model.HasCode;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;
import net.bis5.mattermost.websocket.WebSocketEvent.WebSocketEventDeserializer;
import net.bis5.mattermost.websocket.model.EventPayload;
import net.bis5.mattermost.websocket.model.EventPayload.GenericEventPayload;
import net.bis5.mattermost.websocket.model.PostDeletedEventPayload;
import net.bis5.mattermost.websocket.model.PostEditedEventPayload;
import net.bis5.mattermost.websocket.model.PostedEventPayload;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
@Getter
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = WebSocketEventDeserializer.class)
public enum WebSocketEvent implements HasCode<WebSocketEvent> {

  ADDED_TO_TEAM, //
  AUTHENTICATION_CHALLENGE, //
  CHANNEL_CONVERTED, //
  CHANNEL_CREATED, //
  CHANNEL_DELETED, //
  CHANNEL_MEMBER_UPDATED, //
  CHANNEL_UPDATED, //
  CHANNEL_VIEWED, //
  CONFIG_CHANGED, //
  DELETE_TEAM, //
  DIRECT_ADDED, //
  EMOJI_ADDED, //
  EPHEMERAL_MESSAGE, //
  GROUP_ADDED, //
  HELLO, //
  LEAVE_TEAM, //
  LICENSE_CHANGED, //
  MEMBERROLE_UPDATED, //
  NEW_USER, //
  PLUGIN_DISABLED, //
  PLUGIN_ENABLED, //
  PLUGIN_STATUSES_CHANGED, //
  POST_DELETED(PostDeletedEventPayload.class), //
  POST_EDITED(PostEditedEventPayload.class), //
  POSTED(PostedEventPayload.class), //
  PREFERENCE_CHANGED, //
  PREFERENCES_CHANGED, //
  PREFERENCES_DELETED, //
  REACTION_ADDED, //
  REACTION_REMOVED, //
  RESPONSE, //
  ROLE_UPDATED, //
  STATUS_CHANGE, //
  TYPING, //
  UPDATE_TEAM, //
  USER_ADDED, //
  USER_REMOVED, //
  USER_ROLE_UPDATED, //
  USER_UPDATED, //
  DIALOG_OPENED, //
  /** Plugin specified, or unknown events. */
  CUSTOM, //
  ;
  private final String code;
  private final Class<? extends EventPayload<?>> payloadType;

  WebSocketEvent() {
    this(null, null);
  }

  WebSocketEvent(Class<? extends EventPayload<?>> payloadType) {
    this(null, payloadType);
  }

  WebSocketEvent(String code, Class<? extends EventPayload<?>> payloadType) {
    this.code = code != null ? code : name().toLowerCase();
    this.payloadType = payloadType != null ? payloadType : GenericEventPayload.class;
  }

  static WebSocketEvent of(String code) {
    return Arrays.stream(WebSocketEvent.values()) //
        .filter(e -> e.getCode().equals(code)) //
        .findFirst() //
        .orElse(null);
  }

  Class<? extends EventPayload<?>> getPayloadType() {
    return payloadType;
  }

  static class WebSocketEventDeserializer extends JsonDeserializer<WebSocketEvent> {

    @Override
    public WebSocketEvent deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      final String jsonValue = p.getText();
      return WebSocketEvent.of(jsonValue);
    }

  }
}
