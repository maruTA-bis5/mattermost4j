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

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.bis5.mattermost.model.ComplianceStatus.ComplianceStatusDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of compliance report creation status
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = ComplianceStatusDeserializer.class)
public enum ComplianceStatus implements HasCode<ComplianceStatus> {

	Created("created"), Running("running"), Finished("finished"), Failed("failed"), Removed("removed");
	private final String code;

	ComplianceStatus(String code) {
		this.code = code;
	}

	/**
	 * @see net.bis5.mattermost.model.HasCode#getCode()
	 */
	@Override
	public String getCode() {
		return code;
	}

	public static ComplianceStatus of(String code) {
		return Arrays.asList(ComplianceStatus.values()).stream()
				.filter(s -> s.getCode().equals(code))
				.findFirst()
				.orElse(null);
	}

	public static class ComplianceStatusDeserializer extends JsonDeserializer<ComplianceStatus> {

		/**
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 *      com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public ComplianceStatus deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String code = p.getText();
			return ComplianceStatus.of(code);
		}
	}
}
