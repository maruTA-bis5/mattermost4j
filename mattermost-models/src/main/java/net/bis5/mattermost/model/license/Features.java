/*
 * @(#) net.bis5.mattermost.model.license.Features
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
package net.bis5.mattermost.model.license;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/09
 */
@Data
public class Features {

	@JsonProperty("users")
	private int users;
	@JsonProperty("ldap")
	private boolean ldap;
	@JsonProperty("mfa")
	private boolean mfa;
	@JsonProperty("google_oauth")
	private boolean googleOAuth;
	@JsonProperty("office365_oauth")
	private boolean office365OAuth;
	@JsonProperty("compliance")
	private boolean compliance;
	@JsonProperty("cluster")
	private boolean cluster;
	@JsonProperty("metrics")
	private boolean metrics;
	@JsonProperty("custom_brand")
	private boolean customBrand;
	@JsonProperty("mhpns")
	private boolean mhpns;
	@JsonProperty("saml")
	private boolean saml;
	@JsonProperty("password_requirements")
	private boolean passwordRequirements;
	// after we enabled more features for webrtc we'll need to control them with
	// this
	@JsonProperty("future_features")
	private boolean future_features;

}
