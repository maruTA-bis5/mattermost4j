/*
 * @(#) net.bis5.mattermost.model.config.ServiceSettings
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
package net.bis5.mattermost.model.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import net.bis5.mattermost.model.config.consts.AllowEditPost;
import net.bis5.mattermost.model.config.consts.ConnectionSecurity;
import net.bis5.mattermost.model.config.consts.PermissionsDeletePost;
import net.bis5.mattermost.model.config.consts.RestrictEmojiCreation;
import net.bis5.mattermost.model.config.consts.WebServerMode;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/08
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceSettings {

	private String siteUrl;
	private String licenseFileLocation;
	private String listenAddress;
	private ConnectionSecurity connectionSecurity;
	private String tlsCertFile;
	private String tlsKeyFile;
	private boolean useLetsEncrypt;
	private String letsEncryptCertificateCacheFile;
	private boolean forward80To443;
	private int readTimeout;
	private int writeTimeout;
	private int maximumLoginAttempts;
	private String googleDeveloperKey;
	private boolean enableOAuthServiceProvider;
	private boolean enableIncomingWebhooks;
	private boolean enableOutgoingWebhooks;
	private boolean enableCommands;
	private boolean enableOnlyAdminIntegrations;
	private boolean enablePostUsernameOverride;
	private boolean enablePostIconOverride;
	private boolean enableLinkPreviews;
	private boolean enableTesting;
	private boolean enableDeveloper;
	private boolean enableSecurityFixAlert;
	private boolean enableInsecureOutgoingConnections;
	private boolean enableMultifactorAuthentication;
	private boolean enforceMultifactorAuthentication;
	private String allowCorsFrom;
	private int sessionLengthWebInDays;
	private int sessionLengthMobileInDays;
	private int sessionLengthSSOInDays;
	private int sessionCacheInMinutes;
	private int websocketSecurePort;
	private int websocketPort;
	private WebServerMode webServerMode;
	private boolean enableCustomEmoji;
	private RestrictEmojiCreation restrictCustomEmojiCreation;
	private PermissionsDeletePost restrictPostDelete; // XXX really?
	private AllowEditPost allowEditPost;
	private int postEditTimeLimit;
	private long timeBetweenUserTypingUpdatesMilliseconds;
	private boolean enablePostSearch;
	private boolean enableUserTypingMessages;
	private boolean enableUserStatuses;
	private int clusterLogTimeoutMilliseconds;
	/** @since Mattermost Server 3.10 */
	private int goroutineHealthThreshould;
	/** @since Mattermost Server 4.0 */
	private boolean enableEmojiPicker;
	/** @since Mattermost Server 4.0 */
	private boolean enableChannelViewedMessages;
	/** @since Mattermost Server 4.0 */
	private boolean enableAPIv3;

}
