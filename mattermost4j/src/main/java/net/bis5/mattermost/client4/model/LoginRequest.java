/*
 * Copyright (c) 2017-present, Takayuki Maruyama
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
package net.bis5.mattermost.client4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

/**
 * Login request payload
 * 
 * @see net.bis5.mattermost.client4.api.AuthenticationApi
 * @author Takayuki Maruyama
 */
@Value
@Builder
@JsonIgnoreProperties({ "id", "ldap_only", "device_id" })
public class LoginRequest {

	@JsonProperty("id")
	private String id;
	@JsonProperty("login_id")
	private String loginId;
	@JsonProperty("password")
	private String password;
	@JsonProperty("ldap_only")
	private boolean ldapOnly;
	@JsonProperty("device_id")
	private String deviceId;

}
