/*
 * @(#) net.bis5.mattermost.model.Config
 * Copyright (c) 2016-present, Maruyama Takayuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package net.bis5.mattermost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import net.bis5.mattermost.model.config.AnalyticsSettings;
import net.bis5.mattermost.model.config.AnnouncementSettings;
import net.bis5.mattermost.model.config.ClientRequirements;
import net.bis5.mattermost.model.config.ClusterSettings;
import net.bis5.mattermost.model.config.ComplianceSettings;
import net.bis5.mattermost.model.config.DataRetentionSettings;
import net.bis5.mattermost.model.config.DisplaySettings;
import net.bis5.mattermost.model.config.ElasticsearchSettings;
import net.bis5.mattermost.model.config.EmailSettings;
import net.bis5.mattermost.model.config.FileSettings;
import net.bis5.mattermost.model.config.JobSettings;
import net.bis5.mattermost.model.config.LdapSettings;
import net.bis5.mattermost.model.config.LocalizationSettings;
import net.bis5.mattermost.model.config.LogSettings;
import net.bis5.mattermost.model.config.MessageExportSettings;
import net.bis5.mattermost.model.config.MetricsSettings;
import net.bis5.mattermost.model.config.NativeAppSettings;
import net.bis5.mattermost.model.config.PasswordSettings;
import net.bis5.mattermost.model.config.PluginSettings;
import net.bis5.mattermost.model.config.PrivacySettings;
import net.bis5.mattermost.model.config.RateLimitSettings;
import net.bis5.mattermost.model.config.SSOSettings;
import net.bis5.mattermost.model.config.SamlSettings;
import net.bis5.mattermost.model.config.ServiceSettings;
import net.bis5.mattermost.model.config.SqlSettings;
import net.bis5.mattermost.model.config.SupportSettings;
import net.bis5.mattermost.model.config.TeamSettings;
import net.bis5.mattermost.model.config.ThemeSettings;
import net.bis5.mattermost.model.config.WebrtcSettings;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/09
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
	private SSOSettings gitLabSettings;
	private SSOSettings googleSettings;
	private SSOSettings office365Settings;
	private LdapSettings ldapSettings;
	private ComplianceSettings complianceSettings;
	private LocalizationSettings localizationSettings;
	private SamlSettings samlSettings;
	private NativeAppSettings nativeAppSettings;
	private ClusterSettings clusterSettings;
	private MetricsSettings metricsSettings;
	private AnalyticsSettings analyticsSettings;
	private WebrtcSettings webrtcSettings;
	/** @since Mattermost Server 3.10 (E10) */
	private AnnouncementSettings announcementSettings;
	/** @since Mattermost Server 4.1 (Enterprise Edition) */
	private ElasticsearchSettings elasticsearchSettings;
	/** @since Mattermost Server 4.1 (Enterprise Edition) */
	private JobSettings jobSettings;
	/** @Since Mattermost Server 4.2 (Enterprise Edition) */
	private ThemeSettings themeSettings;
	/**
	 * @since Mattermost Server 4.2 but experimental. see <a href=
	 *        "https://github.com/mattermost/mattermost-server/pull/7220">https://github.com/mattermost/mattermost-server/pull/7220</a>
	 */
	private ClientRequirements clientRequrements;
	/** @since Mattermost Server 4.3 (Enterprise Edition) */
	private DataRetentionSettings dataRetentionSettings;
	/** @since Mattermost Server 4.4 */
	private PluginSettings pluginSettings;
	/** @since Mattermost Server 4.5 (Enterprise Edition) */
	private MessageExportSettings messageExportSettings;
	/** @since Mattermost Server 4.9 */
	private DisplaySettings displaySettings;
}
