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

/**
 * Support settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class SupportSettings {

  private String termsOfServiceLink;
  private String privacyPolicyLink;
  private String aboutLink;
  private String helpLink;
  private String reportAProblemLink;
  private String supportEmail;
  /* @since Mattermost Server 5.4 */
  private boolean customTermsOfServiceEnabled;
  /* @since Mattermost Server 5.6 */
  private int customTermsOfServiceReAcceptancePeriod;
  /** @since Mattermost Server 5.26 */
  private boolean enableAskCommunityLink;

}
