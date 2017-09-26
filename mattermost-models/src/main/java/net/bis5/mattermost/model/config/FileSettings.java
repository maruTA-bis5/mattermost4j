/*
 * @(#) net.bis5.mattermost.model.config.FileSettings
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
public class FileSettings {

	private boolean enableFileAttachments;
	private long maxFileSize;
	private String driverName; // XXX ImageDriver ?
	private String directory;
	private boolean enablePublicLink;
	private String publicLinkSalt;
	/** @deprecated removed in Mattermost Server 4.0 */
	@Deprecated
	private int thumbnailWidth;
	/** @deprecated removed in Mattermost Server 4.0 */
	@Deprecated
	private int thumbnailHeight;
	/** @deprecated removed in Mattermost Server 4.0 */
	@Deprecated
	private int previewWidth;
	/** @deprecated removed in Mattermost Server 4.0 */
	@Deprecated
	private int previewHeight;
	/** @deprecated removed in Mattermost Server 4.0 */
	@Deprecated
	private int profileWidth;
	/** @deprecated removed in Mattermost Server 4.0 */
	@Deprecated
	private int profileHeight;
	private String initialFont;
	private String amazonS3AccessKeyId;
	private String amazonS3SecretAccessKey;
	private String amazonS3Bucket;
	private String amazonS3Region;
	private String amazonS3Endpoint;
	private boolean amazonS3SSL;
	/** @since Mattermost Server 3.10 */
	private boolean amazonS3SignV2;

}
