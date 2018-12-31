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
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import net.bis5.mattermost.model.serialize.MattermostPropertyNamingStrategy;

/**
 * Mattermost datamodel mapper
 * 
 * @author Takayuki Maruyama
 */
@Provider
public class MattermostModelMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper defaultObjectMapper = createDefaultObjectMapper();

  protected ObjectMapper createDefaultObjectMapper() {
    return new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .setSerializationInclusion(Include.NON_EMPTY)
        .setPropertyNamingStrategy(new MattermostPropertyNamingStrategy());
  }

  /**
   * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
   */
  @Override
  public ObjectMapper getContext(Class<?> type) {
    return defaultObjectMapper;
  }

}
