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

package net.bis5.mattermost.client4;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Query parameter builder.
 * 
 * @author Takayuki Maruyama
 */
public class QueryBuilder {

  private static final String PREFIX = "?";
  private static final String DELIMITER = "&";
  private static final String EQUALS = "=";
  private final Map<String, String> parameters = new HashMap<>();

  /**
   * Set string parameter.
   */
  public QueryBuilder set(String key, String value) {
    if (StringUtils.isEmpty(key)) {
      throw new IllegalArgumentException("key");
    }
    parameters.put(key, StringUtils.stripToEmpty(value));
    return this;
  }

  /**
   * Set integer parameter.
   */
  public QueryBuilder set(String key, int value) {
    return set(key, String.valueOf(value));
  }

  /**
   * Set boolean parameter.
   */
  public QueryBuilder set(String key, boolean value) {
    return set(key, Boolean.toString(value));
  }

  /**
   * Set pager parameter.
   */
  public QueryBuilder set(Pager pager) {
    set("page", pager.getPage());
    set("per_page", pager.getPerPage());
    return this;
  }

  /**
   * Set string parameter.
   * 
   * @deprecated Use {@link #set(String, String)} instead,
   */
  @Deprecated
  public QueryBuilder append(String key, String value) {
    return set(key, value);
  }


  /**
   * Set integer parameter.
   * 
   * @deprecated Use {@link #set(String, int)} instead.
   */
  @Deprecated
  public QueryBuilder append(String key, int value) {
    return append(key, String.valueOf(value));
  }

  /**
   * Set boolean parameter.
   * 
   * @deprecated Use {@link #set(String, boolean)} instead.
   */
  @Deprecated
  public QueryBuilder append(String key, boolean value) {
    return append(key, Boolean.toString(value));
  }

  @Override
  public String toString() {
    return parameters.entrySet().stream().map(e -> e.getKey() + EQUALS + e.getValue())
        .collect(Collectors.joining(DELIMITER, PREFIX, ""));
  }

}
