/*
 * @(#) net.bis5.mattermost.model.config.SamlSettings
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
package net.bis5.mattermost.model.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/09
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SamlSettings {

	// Basic
	private boolean enable;
	private boolean verify;
	private boolean encrypt;

	private String idpUrl;
	private String idpDescriptorUrl;
	private String assertionConsumerServiceUrl;

	private String idpCertificateFile;
	private String publicCertificateFile;
	private String privateKeyFile;

	// User Mapping
	private String firstNameAttribute;
	private String lastNameAttribute;
	private String emailAttribute;
	private String usernameAttribute;
	private String nicknameAttribute;
	private String localeAttribute;
	private String positionAttribute;

	private String loginButtonText;

}
