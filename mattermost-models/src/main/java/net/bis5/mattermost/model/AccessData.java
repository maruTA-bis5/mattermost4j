/*
 * Copyright (c) 2016-present, Takayuki Maruyama
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
 * Access data
 * 
 * @author Takayuki Maruyama
 */
@Data
public class AccessData {

	@JsonProperty("client_id")
	private String clientId;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("token")
	private String token;
	@JsonProperty("refresh_token")
	private String refreshToken;
	@JsonProperty("redirect_url")
	private String redirectUri;
	@JsonProperty("expires_at")
	private long expiresAt;
	@JsonProperty("scope")
	private String scope;

}
