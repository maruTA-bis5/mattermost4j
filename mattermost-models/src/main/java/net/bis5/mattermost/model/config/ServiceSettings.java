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

import java.util.List;
import lombok.Data;
import net.bis5.mattermost.model.config.consts.AllowEditPost;
import net.bis5.mattermost.model.config.consts.ConnectionSecurity;
import net.bis5.mattermost.model.config.consts.GroupUnreadChannels;
import net.bis5.mattermost.model.config.consts.ImageProxyType;
import net.bis5.mattermost.model.config.consts.PermissionsDeletePost;
import net.bis5.mattermost.model.config.consts.RestrictEmojiCreation;
import net.bis5.mattermost.model.config.consts.WebServerMode;

/**
 * Service settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
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
  private int sessionLengthSsoInDays;
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
  /* @since Mattermost Server 3.10 */
  private int goroutineHealthThreshold;
  /* @since Mattermost Server 4.0 */
  private boolean enableEmojiPicker;
  /* @since Mattermost Server 4.0 */
  private boolean enableChannelViewedMessages;
  /* @since Mattermost Server 4.0, change default to false in 4.8 */
  private boolean enableApiv3 = false;
  /* @since Mattermost Server 4.1 */
  private boolean enableUserAccessTokens;
  /* @since Mattermost Server 4.2 */
  private String allowedUntrustedInternalConnections;
  /* @since Mattermost Server 4.3 (Enterprise Edition) */
  private int sessionIdleTimeoutInMinutes;
  /* @since Mattermost Server 4.4 */
  private boolean closeUnusedDirectMessages;
  /* @since Mattermost Server 4.5 */
  private boolean enablePreviewFeatures = true;
  /* @since Mattermost Server 4.5 */
  private boolean experimentalEnableAuthenticationTransfer = true;
  /* @since Mattermost Server 4.6 */
  private boolean enableTutorial = true;
  /* @since Mattermost Server 4.7 */
  private ImageProxyType imageProxyType;
  /* @since Mattermost Server 5.10 */
  private int minimumHashtagLength = 3;
  /* @since Mattermost Server 5.10 */
  private boolean disableBotsWhenOwnerIsDeactivated = true;

  /**
   * Set the image proxy type.
   * 
   * @Deprecated Changed to {@link ImageProxySettings#setImageProxyType(ImageProxyType)} for
   *             Mattermost Server 5.8+
   * @since Mattermost Server 4.7
   */
  @Deprecated
  public void setImageProxyType(ImageProxyType imageProxyType) {
    this.imageProxyType = imageProxyType;
  }

  /**
   * Get the image proxy type.
   * 
   * @Deprecated Changed to {@link ImageProxySettings#getImageProxyType()} for Mattermost Server
   *             5.8+
   * @since Mattermost Server 4.7
   */
  @Deprecated
  public ImageProxyType getImageProxyType() {
    return imageProxyType;
  }

  /* @since Mattermost Server 4.7 */
  private String imageProxyOptions;

  /**
   * Set the image proxy options.
   * 
   * @deprecated Changed to {@link ImageProxySettings#setRemoteImageProxyOptions(String)} for
   *             Mattermost Server 5.8+
   * @since Mattermost Server 4.7
   */
  @Deprecated
  public void setImageProxyOptions(String imageProxyOptions) {
    this.imageProxyOptions = imageProxyOptions;
  }

  /**
   * Get the image proxy options.
   * 
   * @deprecated Changed to {@link ImageProxySettings#getRemoteImageProxyOptions()} for Mattermost
   *             Server 5.8+
   * @since Mattermost Server 4.7
   */
  @Deprecated
  public String getImageProxyOptions() {
    return imageProxyOptions;
  }

  /* @since Mattermost Server 4.7 */
  private String imageProxyUrl;

  /**
   * Set the image proxy url.
   * 
   * @deprecated Changed to {@link ImageProxySettings#setRemoteImageProxyUrl(String)} for Mattermost
   *             Server 5.8+
   * @since Mattermost Server 4.7
   */
  @Deprecated
  public void setImageProxyUrl(String imageProxyUrl) {
    this.imageProxyUrl = imageProxyUrl;
  }

  /**
   * Get the image proxy url.
   * 
   * @deprecated Changed to {@link ImageProxySettings#getRemoteImageProxyUrl()} for Mattermost
   *             Server 5.8+
   * @since Mattermost Server 4.7
   */
  @Deprecated
  public String getImageProxyUrl() {
    return imageProxyUrl;
  }

  /* @since Mattermost Server 4.7 */
  private GroupUnreadChannels experimentalGroupUnreadChannels = GroupUnreadChannels.DISABLED;
  /* @since Mattermost Server 4.7 */
  private boolean experimentalEnableDefaultChannelLeaveJoinMessages = true;
  /* @since Mattermost 4.8 */
  private boolean allowCookiesForSubdomains;
  /* @since Mattermost 4.8 */
  private String websocketUrl;
  /* @since Mattermost Server XXX what ver? */
  private boolean enableEmailInvitations;
  /* @since Mattermost Server 5.0 */
  private boolean enableApiTeamDeletion;
  /* @since Mattermost Server 5.0 */
  private boolean experimentalEnableHardenedMode;
  /* @since Mattermost Server 5.1 */
  private boolean enableGifPicker;
  /* @since Mattermpst Server 5.1 */
  private String gfycatApiKey;
  /* @since Mattermost Server 5.1 */
  private String gfycatApiSecret;
  /* @since Mattermost Server 5.1 */
  private boolean experimentalLimitClientConfig;
  /* @since Mattermost Server 5.2 */
  private String corsExposedHeaders;
  /* @since Mattermost Server 5.2 */
  private boolean corsAllowCredentials;
  /* @since Mattermost Server 5.2 */
  private boolean corsDebug;
  /* @since Mattermost Server 5.2 */
  private boolean experimentalChannelOrganization;
  /* @since Mattermost Server 5.6 */
  private String tlsMinVer;
  /* @since Mattermost Server 5.6 */
  private boolean tlsStrictTransport;
  /* @since Mattermost Server 5.6 */
  private long tlsStrictTransportMaxAge;
  /* @since Mattermost Server 5.6 */
  private List<String> tlsOverwriteCiphers;
  /* @since Mattermost Server 5.8 */
  private boolean experimentalLdapGroupSync;
  /* @since Mattermost Server 5.8 */
  private boolean experimentalStrictCsrfEnforcement;
  /* @since Mattermost Server 5.9 */
  private boolean disableLegacyMfa;

  /**
   * This method should not use.
   * 
   * @deprecated This is typo. Please use {@link #getGoroutineHealthThreshold()}
   */
  @Deprecated
  public int getGoroutineHealthThreshould() {
    return getGoroutineHealthThreshold();
  }

  /**
   * This method should not use.
   * 
   * @deprecated This is typo. Please use {@link #setGoroutineHealthThreshold(int)}
   */
  @Deprecated
  public void setGoroutineHealthThreshould(int goroutineHealthThresould) {
    setGoroutineHealthThreshold(goroutineHealthThresould);
  }

  /**
   * This method should not use.
   * 
   * @deprecated This is typo. Please use {@link #isEnableUserAccessTokens()}
   */
  @Deprecated
  public boolean isEnableUserAccessToken() {
    return isEnableUserAccessTokens();
  }

  /**
   * This method should not use.
   * 
   * @deprecated This is typo. Please use {@link #setEnableUserAccessToken(boolean)}
   */
  @Deprecated
  public void setEnableUserAccessToken(boolean enableUserAccessToken) {
    setEnableUserAccessTokens(enableUserAccessToken);
  }
}
