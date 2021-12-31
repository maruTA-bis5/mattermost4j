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
import java.util.Map;
import lombok.Data;

@Data
public class User {

  @JsonProperty("id")
  private String id;
  @JsonProperty("create_at")
  private long createAt;
  @JsonProperty("update_at")
  private long updateAt;
  @JsonProperty("delete_at")
  private long deleteAt;
  @JsonProperty("username")
  private String username;
  @JsonProperty("password")
  private String password;
  @JsonProperty("auth_data")
  private String authData;
  @JsonProperty("auth_service")
  private AuthService authService;
  @JsonProperty("email")
  private String email;
  @JsonProperty("email_verified")
  private boolean emailVerified;
  @JsonProperty("nickname")
  private String nickname;
  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  @JsonProperty("position")
  private String position;
  @JsonProperty("roles")
  private String roles;
  @JsonProperty("allow_marketing")
  private boolean allowMarketing;
  @JsonProperty("props")
  private Map<String, String> props;
  @JsonProperty("notify_props")
  private Map<String, String> notifyProps;
  @JsonProperty("last_password_update")
  private long lastPasswordUpdate;
  @JsonProperty("last_picture_update")
  private long lastPictureUpdate;
  @JsonProperty("failed_attempts")
  private int failedAttempts;
  @JsonProperty("locale")
  private String locale;
  @JsonProperty("mfa_active")
  private boolean mfaActive;
  @JsonProperty("mfa_secret")
  private String mfaSecret;
  @JsonProperty("last_activity_at")
  private long lastActivityAt;
  /* @since Mattermost Server 4.9 */
  private Map<String, String> timezone;
  /* @since Mattermost Server 5.12 */
  @JsonProperty("is_bot")
  private boolean isBot;
  /* @since Mattermost Server 5.12 */
  private String botDescription;
  /* @since Mattermost Server 5.35 */
  @JsonProperty("disable_welcome_email")
  private boolean disableWelcomeEmail;

}
