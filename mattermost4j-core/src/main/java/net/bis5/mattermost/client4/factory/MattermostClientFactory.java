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

package net.bis5.mattermost.client4.factory;

import java.util.ServiceLoader;
import java.util.logging.Level;

import jakarta.ws.rs.client.ClientBuilder;
import net.bis5.mattermost.client4.MultiPartAdapter;
import net.bis5.mattermost.client4.spi.MattermostClientProvider;

/**
 * Factory class of MattermostClient
 */
public class MattermostClientFactory {

  private static ServiceLoader<MattermostClientProvider> providerLoader = ServiceLoader.load(MattermostClientProvider.class);

  public static ClientBuilder createClientBuilder(boolean ignoreUnknownProperty, Level clientLogLevel) {
    for (MattermostClientProvider provider : providerLoader) {
      return provider.createClientBuilder(ignoreUnknownProperty, clientLogLevel);
    }
    return null;
  }

  public static MultiPartAdapter createMultiPartAdapter() {
    for (MattermostClientProvider provider : providerLoader) {
      return provider.createMultiPartAdapter();
    }
    return null;
  }
}
