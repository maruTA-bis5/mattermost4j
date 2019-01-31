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

package net.bis5.mattermost.client4.api;

import java.nio.file.Path;
import java.util.Map;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.model.AnalyticsCategory;
import net.bis5.mattermost.model.AnalyticsRows;
import net.bis5.mattermost.model.Config;

/**
 * General API.
 * 
 * @author Takayuki Maruyama
 */
public interface GeneralApi {

  /**
   * will ping the server and to see if it is up and running.
   */
  ApiResponse<Boolean> getPing();

  /**
   * will attempt to connect to the configured SMTP server.
   */
  ApiResponse<Boolean> testEmail();

  /**
   * will retrieve the server config with some sanitized items.
   */
  ApiResponse<Config> getConfig();

  /**
   * will reload the server configuration.
   */
  ApiResponse<Boolean> reloadConfig();

  /**
   * will retrieve the parts of the server configuration needed by the client, formatted in the old
   * format.
   */
  default ApiResponse<Map<String, String>> getOldClientConfig() {
    return getOldClientConfig(null);
  }

  /**
   * will retrieve the parts of the server configuration needed by the client, formatted in the old
   * format.
   */
  ApiResponse<Map<String, String>> getOldClientConfig(String etag);

  /**
   * will retrieve the parts of the server license needed by the client, formatted in the old
   * format.
   */
  default ApiResponse<Map<String, String>> getOldClientLicense() {
    return getOldClientLicense(null);
  }

  /**
   * will retrieve the parts of the server license needed by the client, formatted in the old
   * format.
   */
  ApiResponse<Map<String, String>> getOldClientLicense(String etag);

  /**
   * will recycle the connections. Discard current connection and get new one.
   */
  ApiResponse<Boolean> databaseRecycle();

  /**
   * will purge the cache and can affect the performance while is cleaning.
   */
  ApiResponse<Boolean> invalidateCaches();

  /**
   * will update the server configuration.
   */
  ApiResponse<Config> updateConfig(Config config);

  default ApiResponse<AnalyticsRows> getAnalytics() {
    return getAnalytics(AnalyticsCategory.STANDARD);
  }

  default ApiResponse<AnalyticsRows> getAnalytics(AnalyticsCategory category) {
    return getAnalytics(category, null);
  }

  default ApiResponse<AnalyticsRows> getAnalytics(String teamId) {
    return getAnalytics(AnalyticsCategory.STANDARD, teamId);
  }

  ApiResponse<AnalyticsRows> getAnalytics(AnalyticsCategory category, String teamId);

  ApiResponse<Boolean> uploadLicenseFile(Path licenseFile);

  ApiResponse<Boolean> removeLicense();

}
