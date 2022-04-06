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

package net.bis5.mattermost.client4.api;

import static net.bis5.mattermost.client4.Assertions.assertNoError;
import static net.bis5.mattermost.client4.Assertions.isNotSupportVersion;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;

// Brand
@ExtendWith(MattermostClientTestExtension.class)
class BrandApiTest implements MattermostClientTest {
  private TestHelper th;
  private MattermostClient client;

  @Override
  public void setHelper(TestHelper helper) {
    this.th = helper;
  }

  @BeforeEach
  void setup() {
    client = createClient();
    th.changeClient(client).initBasic();
  }

  @AfterEach
  void tearDown() {
    th.logout();
    client.close();
  }

  @Test
  void getBrandImageForNotEmpty() throws URISyntaxException, IOException {
    th.logout().loginSystemAdmin();
    Path brandImage = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    ApiResponse<Boolean> uploadResponse = client.uploadBrandImage(brandImage);
    if (isNotSupportVersion("5.0.0", uploadResponse)) {
      return;
    }
    assertNoError(uploadResponse);
    assertTrue(uploadResponse.readEntity());
    th.logout().loginBasic();

    assertNoError(client.getBrandImage());
  }

  @Test
  void uploadBrandImage() throws URISyntaxException, IOException {
    th.logout().loginSystemAdmin();
    Path brandImage = th.getResourcePath(TestHelper.EMOJI_GLOBE);

    ApiResponse<Boolean> uploadResponse = client.uploadBrandImage(brandImage);
    if (isNotSupportVersion("5.0.0", uploadResponse)) {
      return;
    }
    assertNoError(uploadResponse);

    assertTrue(uploadResponse.readEntity());
  }

  @Test
  void deleteBrandImage() throws URISyntaxException, IOException {
    th.logout().loginSystemAdmin();
    Path brandImage = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    ApiResponse<Boolean> uploadResponse = client.uploadBrandImage(brandImage);
    if (isNotSupportVersion("5.6.0", uploadResponse)) {
      return;
    }

    ApiResponse<Boolean> deleteResponse = assertNoError(client.deleteBrandImage());

    assertTrue(deleteResponse.readEntity());
  }
}