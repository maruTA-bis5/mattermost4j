/*
 * @(#) net.bis5.mattermost.model.ApiStatus
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
package net.bis5.mattermost.model;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/23
 */
@Data
public class ApiStatus {

	@JsonProperty("status")
	private ApiStatus.Status status;
	@JsonProperty("SUCCESS")
	private boolean success;

	@JsonSerialize(using = HasCodeSerializer.class)
	@JsonDeserialize(using = ChannelTypeDeserializer.class)
	public static enum Status implements HasCode<Status> {
		OK, FAIL;

		/**
		 * @see net.bis5.mattermost.model.HasCode#getCode()
		 */
		@Override
		public String getCode() {
			return name();
		}

		public static Status of(String code) {
			for (Status status : Status.values()) {
				if (status.name().equals(code)) {
					return status;
				}
			}
			return null;
		}
	}

	public static class ChannelTypeDeserializer extends JsonDeserializer<Status> {

		/**
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 *      com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public Status deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Status.of(p.getText());
		}
	}

}
