/*
 * @(#) net.bis5.mattermost.model.SwitchRequest
 * Copyright (c) 2017 Maruyama Takayuki <bis5.wsys@gmail.com>
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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/09
 */
@Data
public class SwitchRequest {

	@JsonProperty("current_service")
	private AuthService currentService;
	@JsonProperty("new_service")
	private AuthService newService;
	@JsonProperty("email")
	private String email;
	/** @deprecated Mattermost 4.0 or lator: use {@link #password} instead. */
	@JsonProperty("current_password")
	private String currentPassword;
	@JsonProperty("password")
	private String password;
	@JsonProperty("new_password")
	private String newPassword;
	@JsonProperty("mfa_code")
	private String mfaCode;
	@JsonProperty("ldap_id")
	private String ldapId;

}
