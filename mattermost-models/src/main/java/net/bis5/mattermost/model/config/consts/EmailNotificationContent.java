/*
 * Copyright (c) 2017 Takayuki Maruyama
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
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bis5.mattermost.model.HasCode;
import net.bis5.mattermost.model.config.consts.EmailNotificationContent.EmailNotificationContentDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of body contents in email notification.
 * 
 * @author Takayuki Maruyama
 */
@Getter
@RequiredArgsConstructor
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = EmailNotificationContentDeserializer.class)
public enum EmailNotificationContent implements HasCode<EmailNotificationContent> {

  FULL("full"), GENERIC("generic");
  private final String code;

  public static EmailNotificationContent of(String code) {
    return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
  }

  static class EmailNotificationContentDeserializer
      extends JsonDeserializer<EmailNotificationContent> {

    @Override
    public EmailNotificationContent deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      String jsonValue = p.getValueAsString();
      return of(jsonValue);
    }
  }
}
