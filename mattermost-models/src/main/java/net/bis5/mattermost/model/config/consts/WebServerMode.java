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

package net.bis5.mattermost.model.config.consts;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Arrays;
import lombok.Getter;
import net.bis5.mattermost.model.HasCode;
import net.bis5.mattermost.model.config.consts.WebServerMode.WebServerModeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of web server mode.
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = WebServerModeDeserializer.class)
@Getter
public enum WebServerMode implements HasCode<WebServerMode> {

  REGULAR("regular"), GZIP("gzip"), DISABLED("disabled");
  private final String code;

  private WebServerMode(String code) {
    this.code = code;
  }

  public static WebServerMode of(String code) {
    return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst()
        .orElse(REGULAR);
  }

  public static class WebServerModeDeserializer extends JsonDeserializer<WebServerMode> {

    @Override
    public WebServerMode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      String jsonValue = p.getText();
      return of(jsonValue);
    }
  }

}
