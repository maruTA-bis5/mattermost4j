/*
 * @(#) net.bis5.mattermost.model.config.LdapSettings
 * Copyright (c) 2016 takayuki
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

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/09
 */
@Data
public class LdapSettings {

	// Basic
	private boolean enable;
	private String ldapServer;
	private int ldapPort;
	private String connectionSecurity; // XXX ConnectionSecurity?
	private String baseDN;
	private String bindUsername;
	private String bindPassword;

	// Filtering
	private String userFilter;

	// User Mapping
	private String firstNameAttribute;
	private String lastNameAttribute;
	private String emailAttribute;
	private String usernameAttribute;
	private String nicknameAttribute;
	private String idAttribute;
	private String positionAttribute;

	// Syncronization
	private int syncIntervalMinutes;

	// Advanced
	private boolean skipCertificateVerification;
	private int queryTimeout;
	private int maxPageSize;

	// Customization
	private String loginFieldName;

}
