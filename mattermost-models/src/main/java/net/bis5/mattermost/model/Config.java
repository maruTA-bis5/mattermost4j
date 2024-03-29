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

import java.util.Map;

import lombok.Data;
import net.bis5.mattermost.model.config.AnalyticsSettings;
import net.bis5.mattermost.model.config.AnnouncementSettings;
import net.bis5.mattermost.model.config.BleveSettings;
import net.bis5.mattermost.model.config.ClientRequirements;
import net.bis5.mattermost.model.config.CloudSettings;
import net.bis5.mattermost.model.config.ClusterSettings;
import net.bis5.mattermost.model.config.ComplianceSettings;
import net.bis5.mattermost.model.config.DataRetentionSettings;
import net.bis5.mattermost.model.config.DisplaySettings;
import net.bis5.mattermost.model.config.ElasticsearchSettings;
import net.bis5.mattermost.model.config.EmailSettings;
import net.bis5.mattermost.model.config.ExperimentalAuditSettings;
import net.bis5.mattermost.model.config.ExperimentalSettings;
import net.bis5.mattermost.model.config.ExportSettings;
import net.bis5.mattermost.model.config.ExtensionSettings;
import net.bis5.mattermost.model.config.FileSettings;
import net.bis5.mattermost.model.config.GuestAccountsSettings;
import net.bis5.mattermost.model.config.ImageProxySettings;
import net.bis5.mattermost.model.config.ImportSettings;
import net.bis5.mattermost.model.config.JobSettings;
import net.bis5.mattermost.model.config.LdapSettings;
import net.bis5.mattermost.model.config.LocalizationSettings;
import net.bis5.mattermost.model.config.LogSettings;
import net.bis5.mattermost.model.config.MessageExportSettings;
import net.bis5.mattermost.model.config.MetricsSettings;
import net.bis5.mattermost.model.config.NativeAppSettings;
import net.bis5.mattermost.model.config.NotificationLogSettings;
import net.bis5.mattermost.model.config.Office365Settings;
import net.bis5.mattermost.model.config.PasswordSettings;
import net.bis5.mattermost.model.config.PluginSettings;
import net.bis5.mattermost.model.config.PrivacySettings;
import net.bis5.mattermost.model.config.RateLimitSettings;
import net.bis5.mattermost.model.config.SamlSettings;
import net.bis5.mattermost.model.config.ServiceSettings;
import net.bis5.mattermost.model.config.SqlSettings;
import net.bis5.mattermost.model.config.SsoSettings;
import net.bis5.mattermost.model.config.SupportSettings;
import net.bis5.mattermost.model.config.TeamSettings;
import net.bis5.mattermost.model.config.ThemeSettings;
import net.bis5.mattermost.model.config.TimezoneSettings;

/**
 * Server Configurations.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Config {

  private ServiceSettings serviceSettings;
  private TeamSettings teamSettings;
  private SqlSettings sqlSettings;
  private LogSettings logSettings;
  private PasswordSettings passwordSettings;
  private FileSettings fileSettings;
  private EmailSettings emailSettings;
  private RateLimitSettings rateLimitSettings;
  private PrivacySettings privacySettings;
  private SupportSettings supportSettings;
  private SsoSettings gitLabSettings;
  private SsoSettings googleSettings;
  private Office365Settings office365Settings;
  private LdapSettings ldapSettings;
  private ComplianceSettings complianceSettings;
  private LocalizationSettings localizationSettings;
  private SamlSettings samlSettings;
  private NativeAppSettings nativeAppSettings;
  private ClusterSettings clusterSettings;
  private MetricsSettings metricsSettings;
  private AnalyticsSettings analyticsSettings;
  /* @since Mattermost Server 3.10 (E10) */
  private AnnouncementSettings announcementSettings;
  /* @since Mattermost Server 4.1 (Enterprise Edition) */
  private ElasticsearchSettings elasticsearchSettings;
  /* @since Mattermost Server 4.1 (Enterprise Edition) */
  private JobSettings jobSettings;
  /* @Since Mattermost Server 4.2 (Enterprise Edition) */
  private ThemeSettings themeSettings;
  /*
   * @since Mattermost Server 4.2 but experimental. see <a href=
   * "https://github.com/mattermost/mattermost-server/pull/7220">https://github.com/mattermost/
   * mattermost-server/pull/7220</a>
   */
  private ClientRequirements clientRequirements;
  /* @since Mattermost Server 4.3 (Enterprise Edition) */
  private DataRetentionSettings dataRetentionSettings;
  /* @since Mattermost Server 4.4 */
  private PluginSettings pluginSettings;
  /* @since Mattermost Server 4.5 (Enterprise Edition) */
  private MessageExportSettings messageExportSettings;
  /* @since Mattermost Server 4.9 */
  private DisplaySettings displaySettings;
  /* @since Mattermost Server 4.9 */
  private TimezoneSettings timezoneSettings;
  /* @since Mattermost Server 5.1 */
  private ExperimentalSettings experimentalSettings;
  /* @since Mattermost Server 5.2 */
  private ExtensionSettings extensionSettings;
  /* @since Mattermost Server 5.8 */
  private ImageProxySettings imageProxySettings;
  /* @since Mattermost Server 5.12 */
  private NotificationLogSettings notificationLogSettings;
  /* @since Mattermost Server 5.16 */
  private GuestAccountsSettings guestAccountsSettings;
  /* @since Mattermost Servre 5.22 */
  private ExperimentalAuditSettings experimentalAuditSettings;
  /** @since Mattermost Server 5.33 */
  private SsoSettings openIdSettings;
  /** @since Mattermost Server 5.24 */
  private BleveSettings bleveSettings;
  /** @since Mattermost Server 5.30 */
  private CloudSettings cloudSettings;
  /** @since Mattermost Server 5.30 */
  private Map<String, Object> featureFlags;
  /** @since Mattermost Server 5.32 */
  private ImportSettings importSettings;
  /** @since Mattermost Server 5.34 */
  private ExportSettings exportSettings;

  /**
   * Should not use this.
   * 
   * @deprecated This is typo. Please use {@link #getClientRequirements()}
   */
  @Deprecated
  public ClientRequirements getClientRequrements() {
    return getClientRequirements();
  }

  /**
   * Should not use this.
   * 
   * @deprecated This is typo. Please use {@link #setClientRequirements(ClientRequirements)}
   */
  @Deprecated
  public void setClientRequremenets(ClientRequirements clientRequirements) {
    setClientRequirements(clientRequirements);
  }
}
