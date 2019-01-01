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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Arrays;
import lombok.Getter;
import net.bis5.mattermost.model.PostType.PostTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of {@link Post}.
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = PostTypeDeserializer.class)
@Getter
public enum PostType implements HasCode<PostType> {

  DEFAULT(""), //
  SLACK_ATTACHMENT("slack_attachment"), //
  SYSTEM_GENERIC("system_generic"), //
  /**
   * This post type is not used currently.
   * 
   * @deprecated use {@link POST_JOIN_CHANNEL} or {@link POST_LEAVE_CHANNEL} instead.
   */
  @Deprecated
  JOIN_LEAVE("system_join_leave"), //
  JOIN_CHANNEL("system_join_channel"), //
  LEAVE_CHANNEL("system_leave_channel"), //
  /**
   * This post type is not used currently.
   * 
   * @deprecated use {@link POST_ADD_TO_CHANNEL} or {@link POST_REMOVE_FROM_CHANNEL} instead.
   */
  @Deprecated
  ADD_REMOVE("system_add_remove"), //
  ADD_TO_CHANNEL("system_add_to_channel"), //
  REMOVE_FROM_CHANNEL("system_remove_from_channel"), //
  HEADER_CHANGE("system_header_change"), //
  DISPLAYNAME_CHANGE("system_displayname_change"), //
  PURPOSE_CHANGE("system_purpose_change"), //
  CHANNEL_DELETED("system_channel_deleted"), //
  EPHEMERAL("system_ephemeral");
  private final String code;

  private PostType(String code) {
    this.code = code;
  }

  public static PostType of(String code) {
    return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst()
        .orElse(DEFAULT);
  }

  static class PostTypeDeserializer extends JsonDeserializer<PostType> {

    @Override
    public PostType deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      String jsonValue = p.getText();
      return of(jsonValue);
    }
  }

}
