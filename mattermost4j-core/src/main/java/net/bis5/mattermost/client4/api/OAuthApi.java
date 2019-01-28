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
 * OAuth API.
 * 
 * @author Takayuki Maruyama
 */
public interface OAuthApi {

  /**
   * will register a new OAuth 2.0 client application with Mattermost acting as an OAuth 2.0 service
   * provider.
   */
  ApiResponse<OAuthApp> createOAuthApp(OAuthApp app);

  /**
   * gets a page of registered OAuth 2.0 client applications with Mattermost acting as an OAuth 2.0
   * service provider.
   */
  default ApiResponse<List<OAuthApp>> getOAuthApps() {
    return getOAuthApps(Pager.defaultPager());
  }

  /**
   * gets a page of registered OAuth 2.0 client applications with Mattermost acting as an OAuth 2.0
   * service provider.
   */
  ApiResponse<List<OAuthApp>> getOAuthApps(Pager pager);

  /**
   * gets a registered OAuth 2.0 client application with Mattermost acting as an OAuth 2.0 service
   * provider.
   */
  ApiResponse<OAuthApp> getOAuthApp(String appId);

  /**
   * gets a sanitized version of a registered OAuth 2.0 client application with Mattermost acting as
   * an OAuth 2.0 service provider.
   */
  ApiResponse<OAuthApp> getOAuthAppInfo(String appId);

  /**
   * deletes a registered OAuth 2.0 client application.
   */
  ApiResponse<Boolean> deleteOAuthApp(String appId);

  /**
   * regenerates the client secret for a registered OAuth 2.0 client application.
   */
  ApiResponse<OAuthApp> regenerateOAuthAppSecret(String appId);

  /**
   * gets a page of OAuth 2.0 client applications the user authorized to use access their account.
   */
  default ApiResponse<List<OAuthApp>> getAuthorizedOAuthAppsForUser(String userId) {
    return getAuthorizedOAuthAppsForUser(userId, Pager.defaultPager());
  }

  /**
   * gets a page of OAuth 2.0 client applications the user authorized to use access their account.
   */
  ApiResponse<List<OAuthApp>> getAuthorizedOAuthAppsForUser(String userId, Pager pager);

  /**
   * will authorize an OAuth 2.0 client application to access a user's account and provide a
   * redirect link to follow.
   */
  String authorizeOAuthApp(AuthorizeRequest authRequest);

  /**
   * will deauthorize an OAuth 2.0 client application from accessing a user's account.
   */
  ApiResponse<Boolean> deauthorizeOAuthApp(String appId);

}
