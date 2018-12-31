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

package net.bis5.mattermost.client4;

import lombok.Value;

/**
 * Paging Options.
 * 
 * @author Takayuki Maruyama
 */
@Value(staticConstructor = "of")
public class Pager {

  private final int page;
  private final int perPage;

  private static final Pager DEFAULT = of(0, 60);

  public static Pager defaultPager() {
    return DEFAULT;
  }

  public Pager nextPage() {
    return Pager.of(page + 1, perPage);
  }

  public String toQuery() {
    return toQuery(true);
  }

  public String toQuery(boolean isHead) {
    return (isHead ? "?" : "&") + String.format("page=%d&per_page=%d", page, perPage);
  }

}
