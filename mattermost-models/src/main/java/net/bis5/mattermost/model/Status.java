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
import lombok.Data;

/**
 * User status.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Status {

  @JsonProperty("user_id")
  private String userId;
  @JsonProperty("status")
  private String status;
  @JsonProperty("manual")
  private boolean manual;
  @JsonProperty("last_activity_at")
  private long lastActivityAt;
  @JsonProperty("active_channel")
  private String activeChannel;
  /* @since Mattermost Server 5.36 */
  @JsonProperty("dnd_end_time")
  private long dndEndTime;

}
