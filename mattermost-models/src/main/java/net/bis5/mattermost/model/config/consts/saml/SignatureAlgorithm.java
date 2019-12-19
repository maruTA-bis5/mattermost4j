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

package net.bis5.mattermost.model.config.consts.saml;

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
import net.bis5.mattermost.model.config.consts.saml.SignatureAlgorithm.SignatureAlgorithmDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * SAML Signature Algorithm.
 * 
 * @author Takayuki Maruyama
 */
@RequiredArgsConstructor
@Getter
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = SignatureAlgorithmDeserializer.class)
public enum SignatureAlgorithm implements HasCode<SignatureAlgorithm> {
  SHA1("RSAwithSHA1"), SHA256("RSAwithSHA256"), SHA512("RSAwithSHA512");

  private final String code;

  public static SignatureAlgorithm of(String code) {
    return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
  }

  public static class SignatureAlgorithmDeserializer extends JsonDeserializer<SignatureAlgorithm> {

    @Override
    public SignatureAlgorithm deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      String jsonValue = p.getText();
      return of(jsonValue);
    }
  }

}
