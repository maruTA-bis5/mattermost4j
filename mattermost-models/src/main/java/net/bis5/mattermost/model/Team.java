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
 * Team.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Team {

  @JsonProperty("id")
  private String id;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("update_at")
  private long updateAt;
  @JsonProperty("delete_at")
  private long deleteAt;
  @JsonProperty("display_name")
  private String displayName;
  @JsonProperty("name")
  private String name;
  @JsonProperty("description")
  private String description;
  @JsonProperty("email")
  private String email;
  @JsonProperty("type")
  private TeamType type;
  @JsonProperty("company_name")
  private String companyName;
  @JsonProperty("allowed_domains")
  private String allowedDomains;
  @JsonProperty("invite_id")
  private String inviteId;
  @JsonProperty("allow_open_invite")
  private boolean allowOpenInvite;
  /* @since Mattermost Server 4.9 */
  private long lastTeamIconUpdate;
  /* @since Mattermost Server XXX what ver? */
  private String schemeId;
  /* @since Mattermost Server 5.10 */
  private boolean groupConstrained;
  /** @since Mattermost Server 5.36 */
  private String policyId;

}
