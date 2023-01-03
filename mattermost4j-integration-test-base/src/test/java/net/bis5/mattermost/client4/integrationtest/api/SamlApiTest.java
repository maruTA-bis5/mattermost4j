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

package net.bis5.mattermost.client4.integrationtest.api;

import static net.bis5.mattermost.client4.integrationtest.Assertions.assertNoError;
import static net.bis5.mattermost.client4.integrationtest.Assertions.assertStatus;
import static net.bis5.mattermost.client4.integrationtest.Assertions.isMajorMinorVersionMatches;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.Response.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTest;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTestExtension;
import net.bis5.mattermost.client4.integrationtest.TestHelper;
import net.bis5.mattermost.model.SamlCertificateStatus;

@ExtendWith(MattermostClientTestExtension.class)
class SamlApiTest implements MattermostClientTest {
  // Note: Team Edition does not support SAML

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
  void getSamlMetadata() throws IOException {
    th.logout().loginSystemAdmin();

    ApiResponse<Path> response = client.getSamlMetadata();

    assertStatus(response, Status.NOT_IMPLEMENTED);
  }

  @Test
  void uploadSamlIdpCertificate() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".crt");
    String fileName = file.getName(file.getNameCount() - 1).toString();

    ApiResponse<Boolean> response = client.uploadSamlIdpCertificate(file, fileName);

    assertTrue(response.readEntity());
  }

  @Test
  void uploadSamlPublicCertificate() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".crt");
    String fileName = file.getName(file.getNameCount() - 1).toString();

    ApiResponse<Boolean> response = client.uploadSamlPublicCertificate(file, fileName);

    assertTrue(response.readEntity());
  }

  @Test
  void uploadSamlPrivateCertificate() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".key");
    String fileName = file.getName(file.getNameCount() - 1).toString();

    ApiResponse<Boolean> response = client.uploadSamlPrivateCertificate(file, fileName);

    assertTrue(response.readEntity());
  }

  @Test
  void deleteSamlIdpCertificate() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".crt");
    String fileName = file.getName(file.getNameCount() - 1).toString();
    assertNoError(client.uploadSamlIdpCertificate(file, fileName));

    ApiResponse<Boolean> response = client.deleteSamlIdpCertificate();

    assertTrue(response.readEntity());
  }

  @Test
  void deleteSamlPublicCertificate() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".crt");
    String fileName = file.getName(file.getNameCount() - 1).toString();
    assertNoError(client.uploadSamlPublicCertificate(file, fileName));

    ApiResponse<Boolean> response = client.deleteSamlPublicCertificate();

    assertTrue(response.readEntity());
  }

  @Test
  void deleteSamlPrivateCertificate() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".key");
    String fileName = file.getName(file.getNameCount() - 1).toString();
    assertNoError(client.uploadSamlPrivateCertificate(file, fileName));

    ApiResponse<Boolean> response = client.deleteSamlPrivateCertificate();

    assertTrue(response.readEntity());
  }

  @Test
  void getSamlCertificateStatus() throws IOException {
    th.logout().loginSystemAdmin();

    Path file = Files.createTempFile("", ".key");
    String fileName = file.getName(file.getNameCount() - 1).toString();
    assertNoError(client.uploadSamlPrivateCertificate(file, fileName));

    ApiResponse<SamlCertificateStatus> response = client.getSamlCertificateStatus();

    // Note: 5.10/5.11 server returns true unless upload certificate

    if (isMajorMinorVersionMatches("5.10", response) || isMajorMinorVersionMatches("5.11", response)) {
      return;
    }

    SamlCertificateStatus status = response.readEntity();
    assertAll(() -> assertFalse(status.isIdpCertificateFile()), () -> assertFalse(status.isPublicCertificateFile()),
        () -> assertTrue(status.isPrivateKeyFile()));
  }
}