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

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.model.SwitchAccountTypeResult;
import net.bis5.mattermost.model.SwitchRequest;
import net.bis5.mattermost.model.User;

/**
 * Authentication API.
 * 
 * @author Takayuki Maruyama
 */
public interface AuthenticationApi {

  /**
   * authenticates a user by user id and password.
   */
  ApiResponse<User> loginById(String id, String password);

  /**
   * authenticates a user by login id, which can be username, email, or some sort of SSO identifier
   * based on server configuration, and a password.
   */
  ApiResponse<User> login(String loginId, String password);

  /**
   * authenticates a user by LDAP id and password.
   */
  ApiResponse<User> loginByLdap(String loginId, String password);

  /**
   * authenticates a user by login id (username, email or some sort of SSO identifier based on
   * configuration), password and attaches a device id to the session.
   */
  ApiResponse<User> loginWithDevice(String loginId, String password, String deviceId);

  /**
   * terminates the current user's session.
   */
  ApiResponse<Boolean> logout();

  /**
   * changes a user's login type from one type to another.
   */
  ApiResponse<SwitchAccountTypeResult> switchAccountType(SwitchRequest switchRequest);
}
