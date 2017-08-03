/*
 * @(#) net.bis5.mattermost.client4.QueryBuilder
 * Copyright (c) 2017 Maruyama Takayuki
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

import org.apache.commons.lang3.StringUtils;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 */
class QueryBuilder {

	private static final String PREFIX = "?";
	private static final String DELIMITER = "&";
	private static final String EQUALS = "=";
	private final StringBuilder builder = new StringBuilder(PREFIX);

	private boolean isFirst = true;

	public QueryBuilder append(String key, String value) {
		if (StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("key");
		}
		if (!isFirst) {
			builder.append(DELIMITER);
		} else {
			isFirst = false;
		}
		builder.append(key).append(EQUALS).append(StringUtils.defaultString(value));
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
		return builder.toString();
	}

}
