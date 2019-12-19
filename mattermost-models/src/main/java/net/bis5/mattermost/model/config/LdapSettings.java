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

package net.bis5.mattermost.model.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * LDAP settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class LdapSettings {

  // Basic
  private boolean enable;
  private String ldapServer;
  private int ldapPort;
  private String connectionSecurity; // XXX ConnectionSecurity?
  private String baseDn;
  private String bindUsername;
  private String bindPassword;

  // Filtering
  private String userFilter;

  // User Mapping
  private String firstNameAttribute;
  private String lastNameAttribute;
  private String emailAttribute;
  private String usernameAttribute;
  private String nicknameAttribute;
  private String idAttribute;
  private String positionAttribute;

  // Syncronization
  private int syncIntervalMinutes;

  // Advanced
  private boolean skipCertificateVerification;
  private int queryTimeout;
  private int maxPageSize;

  // Customization
  private String loginFieldName;

  /* @since Mattermost Server 4.4 (Enterprise Edition) */
  private boolean enableSync;
  /* @since Mattermost Server 4.6 (Enterprise Edition) */
  private String loginButtonColor;
  /* @since Mattermost Server 4.6 (Enterprise Edition) */
  private String loginButtonBorderColor;
  /* @since Mattermost Server 4.6 (Enterprise Edition) */
  private String loginButtonTextColor;
  /* @since Mattermost Server 5.0 */
  private String loginIdAttribute;
  /* @since Mattermost Server 5.8 */
  private String groupFilter;
  /* @since Mattermost Server 5.8 */
  private String groupDisplayNameAttribute;
  /* @since Mattermost Server 5.8 */
  private String groupIdAttribute;
  /* @since Mattermost Server 5.14 */
  private boolean trace;
  /* @since Mattermost Server 5.18 */
  private String guestFilter;

  @Getter(onMethod = @__({@Deprecated}))
  @Setter(onMethod = @__({@Deprecated}))
  private boolean enableSyncWithLdap;
}
