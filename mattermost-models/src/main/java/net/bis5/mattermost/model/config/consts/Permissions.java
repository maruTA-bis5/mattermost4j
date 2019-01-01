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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Arrays;
import lombok.Getter;
import net.bis5.mattermost.model.HasCode;
import net.bis5.mattermost.model.config.consts.Permissions.PermissionsDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of permissions.
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = PermissionsDeserializer.class)
@Getter
public enum Permissions implements HasCode<Permissions> {

  ALL("all"), CHANNEL_ADMIN("channel_admin"), TEAM_ADMIN("team_admin"), SYSTEM_ADMIN(
      "system_admin");
  private final String code;

  private Permissions(String code) {
    this.code = code;
  }

  public static Permissions of(String code) {
    return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
  }

  public static class PermissionsDeserializer extends JsonDeserializer<Permissions> {

    @Override
    public Permissions deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      String jsonValue = p.getText();
      return of(jsonValue);
    }
  }

}
