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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.client4.model.AnalyticsCategory;
import net.bis5.mattermost.model.AnalyticsRow;
import net.bis5.mattermost.model.AnalyticsRows;
import net.bis5.mattermost.model.Team;

// System
@ExtendWith(MattermostClientTestExtension.class)
class SystemApiTest implements MattermostClientTest {
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
  void databaseRecycle() {
    th.logout().loginSystemAdmin();

    ApiResponse<Boolean> result = assertNoError(client.databaseRecycle());
    assertTrue(result.readEntity());
  }

  @Test
  void getAnalytics() {
    th.logout().loginSystemAdmin();
    AnalyticsRows analyticsRows = assertNoError(client.getAnalytics()).readEntity();

    assertFalse(analyticsRows.isEmpty());
    assertThat(analyticsRows.get(0).getValue(), is(not(nullValue())));
  }

  @Test
  void getAnalyticsSpecifiedCategory() {
    th.logout().loginSystemAdmin();
    AnalyticsRows analyticsRows = assertNoError(client.getAnalytics(AnalyticsCategory.EXTRA_COUNTS)).readEntity();

    assertFalse(analyticsRows.isEmpty());
    Set<String> rowNames = analyticsRows.stream() //
        .map(AnalyticsRow::getName) //
        .map(String::toLowerCase) //
        .collect(Collectors.toSet());
    assertTrue(rowNames.contains("session_count"));
    assertFalse(rowNames.contains("user_counts_with_posts_day"));
  }

  @Test
  void getAnalyticsSpecifiedTeam() {
    th.logout().loginSystemAdmin();
    Team basicTeam = th.basicTeam();
    AnalyticsRows analyticsRows = assertNoError(client.getAnalytics(basicTeam.getId())).readEntity();

    assertFalse(analyticsRows.isEmpty());
    AnalyticsRow userCountRow = analyticsRows.stream() //
        .filter(r -> r.getName().equals("unique_user_count")) //
        .findAny().get();
    // BasicTeam has 3 users. see TestHelper
    assertThat(userCountRow.getValue().intValue(), is(3));

    // AdditionalTeam has 1 user (team creator).
    Team additionalTeam = th.createTeam();
    analyticsRows = assertNoError(client.getAnalytics(additionalTeam.getId())).readEntity();

    userCountRow = analyticsRows.stream() //
        .filter(r -> r.getName().equals("unique_user_count")) //
        .findAny().get();
    assertThat(userCountRow.getValue().intValue(), is(1));
  }

  @Test
  void getOldClientConfig() {
    ApiResponse<Map<String, String>> response = assertNoError(client.getOldClientConfig());
    Map<String, String> clientConfig = response.readEntity();
    assertTrue(clientConfig.containsKey("Version"));
  }

  @Test
  void getOldClientLicense() {
    ApiResponse<Map<String, String>> response = assertNoError(client.getOldClientLicense());
    Map<String, String> clientLicense = response.readEntity();
    assertTrue(clientLicense.containsKey("IsLicensed"));
  }

  @Test
  void ping() {
    ApiResponse<Boolean> result = assertNoError(client.getPing());
    assertTrue(result.readEntity());
  }

  @Test
  void invalidateCache() {
    th.logout().loginSystemAdmin();

    ApiResponse<Boolean> result = assertNoError(client.invalidateCaches());
    assertTrue(result.readEntity());
  }

  @Test
  void reloadConfig() {
    th.logout().loginSystemAdmin();

    ApiResponse<Boolean> result = assertNoError(client.reloadConfig());
    assertTrue(result.readEntity());
  }

  @Test
  void testEmail() {
    th.logout().loginSystemAdmin();

    ApiResponse<Boolean> result = assertNoError(client.testEmail());
    assertTrue(result.readEntity());
  }

  @Test
  void uploadLicenseFile() throws IOException {
    Path licenseFile = Files.createTempFile(null, null); // invalid contents
    licenseFile.toFile().deleteOnExit();

    th.logout().loginBasic();
    assertStatus(client.uploadLicenseFile(licenseFile), Status.FORBIDDEN);

    th.logout().loginSystemAdmin();
    assertStatus(client.uploadLicenseFile(licenseFile), Status.BAD_REQUEST);
  }

  @Test
  void removeLicense() {
    th.logout().loginBasic();
    assertStatus(client.removeLicense(), Status.FORBIDDEN);

    th.logout().loginSystemAdmin();
    assertNoError(client.removeLicense());
  }
}