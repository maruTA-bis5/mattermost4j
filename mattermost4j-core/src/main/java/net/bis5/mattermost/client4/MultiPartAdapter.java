/*
 * Copyright (c) 2022-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bis5.mattermost.client4;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The adapter to process multi-part request/response.
 */
public interface MultiPartAdapter {
  
  @RequiredArgsConstructor
  public static class FormMultiPart {

    @Getter
    private final MediaType mediaType;
    @Getter
    private List<Part> bodyParts = new ArrayList<>();
    public FormMultiPart bodyPart(String name, Path filePath) {
      bodyParts.add(new FileBodyPart(name, filePath));
      return this;
    }
    public FormMultiPart field(String name, String value) {
      bodyParts.add(new FieldPart(name, value));
      return this;
    }
    public <T> FormMultiPart field(String name, T entity, MediaType mediaType) {
      bodyParts.add(new EntityFieldPart<T>(name, entity, mediaType));
      return this;
    }
  }

  interface Part {}

  @RequiredArgsConstructor
  @Getter
  public static class FileBodyPart implements Part {
    private final String name;
    private final Path filePath;
  }

  @RequiredArgsConstructor
  @Getter
  public static class FieldPart implements Part {
    private final String fieldName;
    private final String value;
  }

  @RequiredArgsConstructor
  @Getter
  public static class EntityFieldPart<T> implements Part {
    private final String name;
    private final T entity;
    private final MediaType mediaType;
  }

  String detectSuffix(String contentDispositionHeader);

  <T> ApiResponse<T> doApiPostMultiPart(Client httpClient, String url, String authority,  FormMultiPart multiPart, Class<T> responseType);

}
