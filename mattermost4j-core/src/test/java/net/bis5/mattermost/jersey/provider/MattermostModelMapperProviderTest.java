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

package net.bis5.mattermost.jersey.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.ws.rs.ext.ContextResolver;
import net.bis5.mattermost.model.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link MattermostModelMapperProvider}.
 */
class MattermostModelMapperProviderTest {

  @Nested
  static class IgnoreUnknownPropertiesTest {
    private static final String JSON_INCLUDE_UNKNOWN_PROPERTY = //
        "{\"UNKNOWN_PROPERTY\":\"value\",\"id\":\"qjwhr6gcq3d8d883cgsuk17h9a\"}";

    @Test
    void ignoreUnknownPropertyCorrectly()
        throws JsonParseException, JsonMappingException, IOException {
      ContextResolver<ObjectMapper> provider = new MattermostModelMapperProvider(true);
      ObjectMapper objectMapper = provider.getContext(ObjectMapper.class);

      User user = objectMapper.readValue(JSON_INCLUDE_UNKNOWN_PROPERTY, User.class);
      assertNotNull(user);
    }

    @Test
    void throwExceptionStrict() {
      ContextResolver<ObjectMapper> provider = new MattermostModelMapperProvider(false);
      ObjectMapper objectMapper = provider.getContext(ObjectMapper.class);

      assertThrows(JsonMappingException.class,
          () -> objectMapper.readValue(JSON_INCLUDE_UNKNOWN_PROPERTY, User.class));
    }
  }
}
