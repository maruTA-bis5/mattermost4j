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
import java.util.Arrays;
import net.bis5.mattermost.model.ComplianceType.ComplianceTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of compliance report.
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = ComplianceTypeDeserializer.class)
public enum ComplianceType implements HasCode<ComplianceType> {

  Daily("daily"), Adhoc("adhoc");
  private final String code;

  ComplianceType(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  public static ComplianceType of(String code) {
    return Arrays.asList(ComplianceType.values()).stream().filter(t -> t.getCode().equals(code))
        .findFirst().orElse(null);
  }

  public static class ComplianceTypeDeserializer extends JsonDeserializer<ComplianceType> {

    @Override
    public ComplianceType deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      String code = p.getText();
      return ComplianceType.of(code);
    }

  }

}
