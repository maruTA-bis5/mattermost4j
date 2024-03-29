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
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Session.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Session {

  @JsonProperty("id")
  private String id;
  @JsonProperty("token")
  private String token;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("expires_at")
  private long expiresAt;
  @JsonProperty("last_activity_at")
  private long lastActivityAt;
  @JsonProperty("user_id")
  private String userId;
  @JsonProperty("device_id")
  private String deviceId;
  @JsonProperty("roles")
  private String roles;
  @JsonProperty("is_oauth")
  private boolean isOAuth;
  @JsonProperty("props")
  private Map<String, String> props;
  @JsonProperty("team_members")
  private List<TeamMember> teamMembers;
  /** @since Mattermost Server 5.24 */
  private boolean local;
  /** @since Mattermost Server 5.26 */
  private boolean expiredNotify;

}
