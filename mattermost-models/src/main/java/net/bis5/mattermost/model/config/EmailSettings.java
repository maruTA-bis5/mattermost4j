/*
 * @(#) net.bis5.mattermost.model.config.EmailSettings
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
import net.bis5.mattermost.model.config.consts.ConnectionSecurity;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/09
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailSettings {

	private boolean enableSignUpWithEmail;
	private boolean enableSignInWithEmail;
	private boolean enableSignInWithUsername;
	private boolean sendEmailNotifications;
	private boolean requireEmailVerification;
	private String feedbackName;
	private String feedbackEmail;
	private String feedbackOrganization;
	private String smtpUsername;
	private String smtpPassword;
	private String smtpServer;
	private String smtpPort;
	private ConnectionSecurity connectionSecurity;
	private String inviteSalt;
	private boolean sendPushNotifications;
	private String pushNotificationServer;
	private String pushNotificationContents;
	private boolean enableEmailBatching;
	private int emailBatchingBufferSize;
	private int emailBatchingInterval;
	private boolean skipServerCertificateVerification;

}
