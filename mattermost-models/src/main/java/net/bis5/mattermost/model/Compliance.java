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
 * Compliance
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Compliance {

	@JsonProperty("id")
	private String id;
	@JsonProperty("create_at")
	private long createAt;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("status")
	private ComplianceStatus status;
	@JsonProperty("count")
	private int count;
	@JsonProperty("desc")
	private String desc;
	@JsonProperty("type")
	private ComplianceType type;
	@JsonProperty("start_at")
	private long startAt;
	@JsonProperty("end_at")
	private long endAt;
	@JsonProperty("keywords")
	private String keywords;
	@JsonProperty("emails")
	private String emails;

}
