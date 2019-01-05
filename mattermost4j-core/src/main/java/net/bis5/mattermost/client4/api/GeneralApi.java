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

  ApiResponse<Boolean> getPing();

  ApiResponse<Boolean> testEmail();

  ApiResponse<Config> getConfig();

  ApiResponse<Boolean> reloadConfig();

  default ApiResponse<Map<String, String>> getOldClientConfig() {
    return getOldClientConfig(null);
  }

  ApiResponse<Map<String, String>> getOldClientConfig(String etag);

  default ApiResponse<Map<String, String>> getOldClientLicense() {
    return getOldClientLicense(null);
  }

  ApiResponse<Map<String, String>> getOldClientLicense(String etag);

  ApiResponse<Boolean> databaseRecycle();

  ApiResponse<Boolean> invalidateCaches();

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
