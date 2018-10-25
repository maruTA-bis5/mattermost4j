/*
 * Copyright (c) 2017-present, Takayuki Maruyama
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
package net.bis5.mattermost.client4;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Query parameter builder
 * 
 * @author Takayuki Maruyama
 */
public class QueryBuilder {

	private static final String PREFIX = "?";
	private static final String DELIMITER = "&";
	private static final String EQUALS = "=";
	private final Map<String, String> parameters = new HashMap<>();

	public QueryBuilder append(String key, String value) {
		if (StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("key");
		}
		parameters.put(key, StringUtils.stripToEmpty(value));
		return this;
	}

	public QueryBuilder append(String key, int value) {
		return append(key, String.valueOf(value));
	}

	public QueryBuilder append(String key, boolean value) {
		return append(key, Boolean.toString(value));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return parameters.entrySet().stream()
				.map(e -> e.getKey() + EQUALS + e.getValue())
				.collect(Collectors.joining(DELIMITER, PREFIX, ""));
	}

}
