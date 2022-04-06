/*
 * Copyright (c) 2016-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bis5.mattermost.client4;

import java.util.logging.Level;

public interface MattermostClientTest {
  void setHelper(TestHelper helper);

  default String getApplicationUrl() {
    return getEnv("MATTERMOST_URL", "http://localhost:8065");
  }

  default String getEnv(String varName, String defaultValue) {
    String val = System.getenv(varName);
    return val != null ? val : defaultValue;
  }

  default MattermostClient createClient() {
    return MattermostClient.builder() //
        .url(getApplicationUrl()) //
        .logLevel(Level.INFO) //
        .build();
  }
}
