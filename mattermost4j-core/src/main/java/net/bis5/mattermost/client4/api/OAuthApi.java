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

import java.util.List;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.AuthorizeRequest;
import net.bis5.mattermost.model.OAuthApp;

/**
 * OAuth API
 * 
 * @author Takayuki Maruyama
 */
public interface OAuthApi {

  ApiResponse<OAuthApp> createOAuthApp(OAuthApp app);

  default ApiResponse<List<OAuthApp>> getOAuthApps() {
    return getOAuthApps(Pager.defaultPager());
  }

  ApiResponse<List<OAuthApp>> getOAuthApps(Pager pager);

  ApiResponse<OAuthApp> getOAuthApp(String appId);

  ApiResponse<OAuthApp> getOAuthAppInfo(String appId);

  ApiResponse<Boolean> deleteOAuthApp(String appId);

  ApiResponse<OAuthApp> regenerateOAuthAppSecret(String appId);

  default ApiResponse<List<OAuthApp>> getAuthorizedOAuthAppsForUser(String userId) {
    return getAuthorizedOAuthAppsForUser(userId, Pager.defaultPager());
  }

  ApiResponse<List<OAuthApp>> getAuthorizedOAuthAppsForUser(String userId, Pager pager);

  String authorizeOAuthApp(AuthorizeRequest authRequest);

  ApiResponse<Boolean> deauthorizeOAuthApp(String appId);

}
