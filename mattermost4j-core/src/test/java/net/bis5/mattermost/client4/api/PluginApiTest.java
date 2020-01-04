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
import static net.bis5.mattermost.client4.Assertions.assertStatus;
import static net.bis5.mattermost.client4.Assertions.isNotSupportVersion;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.model.PluginManifest;
import net.bis5.mattermost.model.Plugins;

@ExtendWith(MattermostClientTestExtension.class)
class PluginApiTest implements MattermostClientTest {
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

  private static Path simpleLockPluginArchivePath;
  private static Path drawPluginArchivePath;

  @BeforeAll
  public static void setupPluginArchivePath() throws IOException {
    InputStream resource = PluginApiTest.class //
        .getResourceAsStream("/net.bis5.mattermost.simplelock-0.0.1.tar.gz");
    simpleLockPluginArchivePath = Files.createTempFile(null, null);
    IOUtils.copy(resource,
        Files.newOutputStream(simpleLockPluginArchivePath, StandardOpenOption.TRUNCATE_EXISTING));

    resource = PluginApiTest.class //
        .getResourceAsStream("/com.mattermost.draw-plugin-0.0.1.tar.gz");
    drawPluginArchivePath = Files.createTempFile(null, null);
    IOUtils.copy(resource, Files.newOutputStream(drawPluginArchivePath, StandardOpenOption.TRUNCATE_EXISTING));
  }

  @AfterAll
  public static void deletePluginArchive() {
    if (simpleLockPluginArchivePath != null) {
      simpleLockPluginArchivePath.toFile().delete();
    }
    if (drawPluginArchivePath != null) {
      drawPluginArchivePath.toFile().delete();
    }
  }

  @Test
  public void getPlugins() {
    th.logout().loginSystemAdmin();

    ApiResponse<Plugins> response = assertNoError(client.getPlugins());
    // 5.2.0 is server plugin release candidate version
    if (isNotSupportVersion("5.2.0", response)) {
      return;
    }
    Plugins plugins = response.readEntity();

    assertAll(() -> {
      Optional<PluginManifest> zoomPlugin = Arrays.asList(plugins.getInactive()).stream()
          .filter(p -> p.getId().equals("zoom")).findFirst();
      assertTrue(zoomPlugin.isPresent(), "zoom");
    }, () -> {
      Optional<PluginManifest> jiraPlugin = Arrays.asList(plugins.getInactive()).stream()
          .filter(p -> p.getId().equals("jira")).findFirst();
      assertTrue(jiraPlugin.isPresent(), "jira");
    });
  }

  @Test
  public void uploadPlugin() {
    th.logout().loginSystemAdmin();
    ApiResponse<Plugins> response = client.getPlugins();
    // 5.2.0 is server plugin release candidate version
    if (isNotSupportVersion("5.2.0", response)) {
      return;
    }

    ApiResponse<PluginManifest> uploadResult = assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
    uploadResult.readEntity();

    // same plugin id is already uploaded
    ApiResponse<PluginManifest> blockedUploadResult = client.uploadPlugin(simpleLockPluginArchivePath);
    assertStatus(blockedUploadResult, Status.BAD_REQUEST);
    assertTrue(blockedUploadResult.readError().getMessage().contains("A plugin with the same ID"));

    if (!isNotSupportVersion("5.8.0", blockedUploadResult)) {
      // force upload
      ApiResponse<PluginManifest> forceUploadResult = assertNoError(
          client.uploadPlugin(simpleLockPluginArchivePath, true));
      forceUploadResult.readEntity();
    }

    // cleanup
    client.removePlugin(uploadResult.readEntity().getId());
  }

  @Test
  public void removePlugin() {
    th.logout().loginSystemAdmin();
    ApiResponse<Plugins> response = client.getPlugins();
    // 5.2.0 is server plugin release candidate version
    if (isNotSupportVersion("5.2.0", response)) {
      return;
    }
    ApiResponse<PluginManifest> uploadResult = assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
    PluginManifest plugin = uploadResult.readEntity();
    String pluginId = plugin.getId();

    ApiResponse<Boolean> removeResult = assertNoError(client.removePlugin(pluginId));

    assertTrue(removeResult.readEntity());

    // cleanup
    client.removePlugin(pluginId);
  }

  @Test
  public void enablePlugin() {
    th.logout().loginSystemAdmin();
    ApiResponse<Plugins> response = client.getPlugins();
    // 5.2.0 is server plugin release candidate version
    if (isNotSupportVersion("5.2.0", response)) {
      return;
    }
    ApiResponse<PluginManifest> uploadResult = assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
    PluginManifest plugin = uploadResult.readEntity();
    String pluginId = plugin.getId();

    ApiResponse<Boolean> enableResult = assertNoError(client.enablePlugin(pluginId));

    assertTrue(enableResult.readEntity());

    // cleanup
    client.removePlugin(pluginId);
  }

  @Test
  public void disablePlugin() {
    th.logout().loginSystemAdmin();
    ApiResponse<Plugins> response = client.getPlugins();
    // 5.2.0 is server plugin release candidate version
    if (isNotSupportVersion("5.2.0", response)) {
      return;
    }
    ApiResponse<PluginManifest> uploadResult = assertNoError(client.uploadPlugin(simpleLockPluginArchivePath));
    PluginManifest plugin = uploadResult.readEntity();
    String pluginId = plugin.getId();
    assertNoError(client.enablePlugin(pluginId));

    ApiResponse<Boolean> disableResult = assertNoError(client.disablePlugin(pluginId));

    assertTrue(disableResult.readEntity());

    // cleanup
    client.removePlugin(pluginId);
  }

  @Test
  public void getWebappPlugins() {
    th.logout().loginSystemAdmin();
    ApiResponse<Plugins> response = client.getPlugins();
    // 5.2.0 is server plugin release candidate version
    if (isNotSupportVersion("5.2.0", response)) {
      return;
    }
    ApiResponse<PluginManifest> uploadResult = assertNoError(client.uploadPlugin(drawPluginArchivePath));
    PluginManifest plugin = uploadResult.readEntity();
    String pluginId = plugin.getId();
    assertNoError(client.enablePlugin(pluginId));

    ApiResponse<PluginManifest[]> webappResponse = assertNoError(client.getWebappPlugins());
    PluginManifest[] webapps = webappResponse.readEntity();

    PluginManifest drawWebapp = Arrays.stream(webapps) //
        .filter(m -> m.getId().equals(pluginId)) //
        .findFirst().get();
    assertEquals(pluginId, drawWebapp.getId());

    // cleanup
    client.removePlugin(pluginId);
  }

}