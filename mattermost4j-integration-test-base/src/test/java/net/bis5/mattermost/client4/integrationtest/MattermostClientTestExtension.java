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

package net.bis5.mattermost.client4.integrationtest;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import net.bis5.mattermost.client4.MattermostClient;

public class MattermostClientTestExtension implements BeforeEachCallback, AfterEachCallback {

  private static final TestHelper HELPER = createHelper();

  private static TestHelper createHelper() {
    MattermostClient initialClient = new MattermostClientTest() {
      @Override
      public void setHelper(TestHelper helper) {
        throw new UnsupportedOperationException();
      }
    }.createClient();
    return new TestHelper(initialClient).setup();
  }

  private MattermostClientTest ensureTestInstance(ExtensionContext context) {
    return context.getTestInstance().map(MattermostClientTest.class::cast).get();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    ensureTestInstance(context).setHelper(HELPER);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    HELPER.clearTempFiles();
  }

}
