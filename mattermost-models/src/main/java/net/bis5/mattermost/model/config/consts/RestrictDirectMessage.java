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

package net.bis5.mattermost.model.config.consts;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import lombok.Getter;
import net.bis5.mattermost.model.HasCode;
import net.bis5.mattermost.model.config.consts.RestrictDirectMessage.RestrictDirectMessageDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * Options to restrict direct message receive to target range.
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = RestrictDirectMessageDeserializer.class)
@Getter
public enum RestrictDirectMessage implements HasCode<RestrictDirectMessage> {
  ANY, TEAM;
  private final String code;

  RestrictDirectMessage() {
    this.code = name().toLowerCase();
  }

  public static RestrictDirectMessage of(String code) {
    return HasCode.of(RestrictDirectMessage::values, code, null);
  }

  public static class RestrictDirectMessageDeserializer
      extends JsonDeserializer<RestrictDirectMessage> {

    @Override
    public RestrictDirectMessage deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      String jsonValue = p.getText();
      return of(jsonValue);
    }
  }

}
