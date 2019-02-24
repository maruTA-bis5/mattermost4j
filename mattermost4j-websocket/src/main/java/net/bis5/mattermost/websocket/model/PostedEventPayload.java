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

package net.bis5.mattermost.websocket.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.bis5.mattermost.model.ChannelType;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.websocket.model.PostedEventPayload.PostedEventData;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
public class PostedEventPayload extends EventPayload<PostedEventData> {

  @Data
  public static class PostedEventData {
    private String channelDisplayName;
    private String channelName;
    private ChannelType channelType;
    private String[] mentions;
    private Post post;
    private String senderName;
    private String teamId;
  }

  @RequiredArgsConstructor
  @Getter
  public static enum PostedEventDataStateKey implements KeyMarker {
    // CHECKSTYLE: OFF
    channelDisplayName(String.class), channelName(String.class), channelType(
        ChannelType.class), mentions(
            String[].class), post(Post.class), senderName(String.class), teamId(String.class);
    // CHECKSTYLE: ON
    final Class<?> valueClass;
  }

  @Value(staticConstructor = "of")
  @JsonDeserialize(using = MentionIdsDeserializer.class)
  public static class MentionIds {
    private final String[] ids;
  }

  public static class MentionIdsDeserializer extends JsonDeserializer<MentionIds> {

    @Override
    public MentionIds deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      // 1枚剥かないとJson String Arrayとして認識できない・・・
      String mentions = p.getText();
      // だから、[から始まって,区切りで]で終わることをざっくり確認すれば解釈できる
      if (mentions.charAt(0) == '[' && mentions.charAt(mentions.length() - 1) == ']') {
        mentions = mentions.substring(1, mentions.length() - 2);
        mentions = mentions.replaceAll("\"", "");

        String[] ids = mentions.split(",");
        return MentionIds.of(ids);
      }
      throw JsonMappingException.from(ctxt, "mentions is not array of string");
    }
  }

  @Override
  public Class<? extends Enum<? extends KeyMarker>> getMarkerEnum() {
    return PostedEventDataStateKey.class;
  }

}
