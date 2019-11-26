/*
 * Copyright (c) 2019 Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.bis5.mattermost.websocket;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig.Configurator;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
public class MattermostWsConfigurator extends Configurator {

  /**
   * @see javax.websocket.ClientEndpointConfig.Configurator#beforeRequest(java.util.Map)
   */
  @Override
  public void beforeRequest(Map<String, List<String>> headers) {
    headers.put("User-Agent", Collections.singletonList(Version.getInstance().toUserAgent()));
    Logger.getLogger(getClass().getName()).info("beforeRequest: " + headers);
  }
}
