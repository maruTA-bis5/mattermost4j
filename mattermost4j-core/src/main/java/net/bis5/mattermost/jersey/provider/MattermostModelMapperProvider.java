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

package net.bis5.mattermost.jersey.provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.serialize.MattermostPropertyNamingStrategy;

/**
 * Mattermost datamodel mapper.
 * 
 * @author Takayuki Maruyama
 */
@Provider
public class MattermostModelMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper defaultObjectMapper;
  final ObjectMapper configObjectMapper;
  private final boolean ignoreUnknownProperties;

  public MattermostModelMapperProvider() {
    this(false);
  }

  public MattermostModelMapperProvider(boolean ignoreUnknownProperties) {
    this.ignoreUnknownProperties = ignoreUnknownProperties;
    defaultObjectMapper = createDefaultObjectMapper();
    configObjectMapper = createConfigObjectMapper();
  }


  protected ObjectMapper createDefaultObjectMapper() {
    return new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !ignoreUnknownProperties)
        .setSerializationInclusion(Include.NON_EMPTY)
        .setPropertyNamingStrategy(new MattermostPropertyNamingStrategy());
  }

  protected ObjectMapper createConfigObjectMapper() {
    return new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !ignoreUnknownProperties)
        .setSerializationInclusion(Include.ALWAYS)
        .setPropertyNamingStrategy(new MattermostPropertyNamingStrategy());
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    if (type == Config.class || type.getName().startsWith("net.bis5.mattermost.model.config")) {
      return configObjectMapper;
    }
    return defaultObjectMapper;
  }

}
