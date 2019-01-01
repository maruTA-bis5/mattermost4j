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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Invite infos.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Invites implements Iterable<Map<String, String>> {

  @JsonProperty("invites")
  private List<Map<String, String>> invites;

  @Override
  public Iterator<Map<String, String>> iterator() {
    return invites.iterator();
  }

}
