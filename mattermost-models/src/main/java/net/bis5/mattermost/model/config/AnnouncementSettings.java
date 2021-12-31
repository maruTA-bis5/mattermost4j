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

package net.bis5.mattermost.model.config;

import lombok.Data;

/**
 * Announcement settings.
 * 
 * @author Takayuki Maruyama
 * @since Mattermost Server 3.10 (E10)
 */
@Data
public class AnnouncementSettings {

  private boolean enableBanner;
  private String bannerText;
  private String bannerColor;
  private String bannerTextColor;
  private boolean allowBannerDismissal;
  /** @since Mattermost Server 5.28 */
  private boolean adminNoticesEnabled = true;
  /** @since Mattermost Server 5.28 */
  private boolean userNoticesEnabled = true;
  /** @since Mattermost Server 5.30 */
  private String noticesUrl;
  /** @since Mattermost Server 5.30 */
  private int noticesFetchFrequency;
  /** @since Mattermost Server 5.30 */
  private boolean noticesSkipCache;

}
