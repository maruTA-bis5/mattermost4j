/*
 * Copyright (c) 2022-present, Takayuki Maruyama
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

package net.bis5.mattermost.client4.spi;

import java.util.logging.Level;

import jakarta.ws.rs.client.ClientBuilder;
import net.bis5.mattermost.client4.MultiPartAdapter;

/**
 * The SPI interface to provide factory methods to create Mattermost client.
 */
public interface MattermostClientProvider {

  /**
   * Create {@link ClientBuilder} instance.
   * @param ignoreUnknownProperties
   * @param clientLogLevel
   * @return Preconfigured {@link ClientBuilder}
   */
  ClientBuilder createClientBuilder(boolean ignoreUnknownProperties, Level clientLogLevel);

  /**
   * Create {@link MultiPartAdapter} instance to use to process multi-part request/response.
   * @return
   */
  MultiPartAdapter createMultiPartAdapter();
}
