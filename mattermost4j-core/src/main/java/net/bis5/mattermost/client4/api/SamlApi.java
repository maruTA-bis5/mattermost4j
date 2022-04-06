/*
 * Copyright (c) 2017 Takayuki Maruyama
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

package net.bis5.mattermost.client4.api;

import java.io.IOException;
import java.nio.file.Path;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.model.SamlCertificateStatus;

/**
 * SAML API.
 * 
 * @author Takayuki Maruyama
 */
public interface SamlApi {

  /**
   * returns metadata for the SAML configuration.
   */
  ApiResponse<Path> getSamlMetadata() throws IOException;

  /**
   * will upload an IDP certificate for SAML and set the config to use it.
   * @throws IOException
   */
  ApiResponse<Boolean> uploadSamlIdpCertificate(Path dataFile, String fileName) throws IOException;

  /**
   * will upload a public certificate for SAML and set the config to use it.
   * @throws IOException
   */
  ApiResponse<Boolean> uploadSamlPublicCertificate(Path dataFile, String fileName) throws IOException;

  /**
   * will upload a private key for SAML and set the config to use it.
   * @throws IOException
   */
  ApiResponse<Boolean> uploadSamlPrivateCertificate(Path dataFile, String fileName) throws IOException;

  /**
   * deletes the SAML IDP certificate from the server and updates the config to not use it and
   * disable SAML.
   */
  ApiResponse<Boolean> deleteSamlIdpCertificate();

  /**
   * deletes the saml IDP certificate from the server and updates the config to not use it and
   * disable SAML.
   */
  ApiResponse<Boolean> deleteSamlPublicCertificate();

  /**
   * deletes the SAML IDP certificate from the server and updates the config to not use it and
   * disable SAML.
   */
  ApiResponse<Boolean> deleteSamlPrivateCertificate();

  /**
   * returns metadata for the SAML configuration.
   */
  ApiResponse<SamlCertificateStatus> getSamlCertificateStatus();

}
