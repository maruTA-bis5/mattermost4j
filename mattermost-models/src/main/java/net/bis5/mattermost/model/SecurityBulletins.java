/*
 * @(#) net.bis5.mattermost.model.SecurityBulletins
 * Copyright (c) 2017-present, Maruyama Takayuki
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

import java.util.Iterator;
import java.util.List;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/09
 */
@Data
public class SecurityBulletins implements Iterable<SecurityBulletin> {
	private List<SecurityBulletin> securityBulletins;

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<SecurityBulletin> iterator() {
		return securityBulletins.iterator();
	}

}
