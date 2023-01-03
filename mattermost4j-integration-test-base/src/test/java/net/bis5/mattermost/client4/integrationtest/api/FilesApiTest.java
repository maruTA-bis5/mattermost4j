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
import static net.bis5.mattermost.client4.integrationtest.Assertions.assertSameFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTest;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTestExtension;
import net.bis5.mattermost.client4.integrationtest.TestHelper;
import net.bis5.mattermost.client4.model.FileUploadResult;
import net.bis5.mattermost.model.FileInfo;
import net.bis5.mattermost.model.Post;

// Files
@ExtendWith(MattermostClientTestExtension.class)
class FilesApiTest implements MattermostClientTest {
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
  void uploadNoFileThrowsException() {
    String channelId = th.basicChannel().getId();
    assertThrows(IllegalArgumentException.class, () -> client.uploadFile(channelId));
  }

  @Test
  void uplaodFile() throws URISyntaxException, IOException {
    Path filePath = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    String channelId = th.basicChannel().getId();

    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, filePath)).readEntity();

    assertEquals(1, uploadResult.getFileInfos().length);
    assertEquals(filePath.getFileName().toString(), uploadResult.getFileInfos()[0].getName());
  }

  @Test
  void uploadMultipleFile() throws URISyntaxException, IOException {
    Path file1 = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Path file2 = th.getResourcePath(TestHelper.EMOJI_CONSTRUCTION);
    String channelId = th.basicChannel().getId();

    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, file1, file2)).readEntity();

    assertEquals(2, uploadResult.getFileInfos().length);
    boolean foundFile1 = false;
    boolean foundFile2 = false;
    for (FileInfo fileInfo : uploadResult.getFileInfos()) {
      foundFile1 = foundFile1 || fileInfo.getName().equals(file1.getFileName().toString());
      foundFile2 = foundFile2 || fileInfo.getName().equals(file2.getFileName().toString());
    }
    assertTrue(foundFile1);
    assertTrue(foundFile2);
  }

  @Test
  void getFile() throws URISyntaxException, IOException {
    Path filePath = th.getResourcePath("/LICENSE.txt");
    String channelId = th.basicChannel().getId();
    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, filePath)).readEntity();
    FileInfo fileInfo = uploadResult.getFileInfos()[0];
    String fileId = fileInfo.getId();

    Path receivedFile = assertNoError(client.getFile(fileId)).readEntity();

    assertSameFile(filePath, receivedFile);
  }

  @Test
  void getFileThumbnail() throws URISyntaxException, IOException {
    Path filePath = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    String channelId = th.basicChannel().getId();
    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, filePath)).readEntity();
    FileInfo fileInfo = uploadResult.getFileInfos()[0];
    String fileId = fileInfo.getId();

    Path thumbnail = assertNoError(client.getFileThumbnail(fileId)).readEntity();

    assertTrue(thumbnail.toFile().exists());
  }

  @Test
  void getFilePreview() throws URISyntaxException, IOException {
    Path filePath = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    String channelId = th.basicChannel().getId();
    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, filePath)).readEntity();
    FileInfo fileInfo = uploadResult.getFileInfos()[0];
    String fileId = fileInfo.getId();

    Path preview = assertNoError(client.getFilePreview(fileId)).readEntity();

    assertTrue(preview.toFile().exists());
  }

  @Test
  void getPublicFileLink() throws URISyntaxException, IOException {
    Path filePath = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    String channelId = th.basicChannel().getId();
    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, filePath)).readEntity();
    FileInfo fileInfo = uploadResult.getFileInfos()[0];
    String fileId = fileInfo.getId();

    Post post = new Post(channelId, "file attached post");
    post.setFileIds(Collections.singletonList(fileId));
    post = assertNoError(client.createPost(post)).readEntity();

    String publicLink = assertNoError(client.getPublicFileLink(fileId)).readEntity();

    Client jaxrsClient = ClientBuilder.newClient();
    WebTarget target = jaxrsClient.target(publicLink);
    Response response = target.request().get();
    InputStream downloadFile = response.readEntity(InputStream.class);
    Path tempFile = Files.createTempFile(null, null);
    Files.copy(downloadFile, tempFile, StandardCopyOption.REPLACE_EXISTING);

    assertSameFile(filePath, tempFile);
  }

  @Test
  void getFileMetadata() throws URISyntaxException, IOException {
    Path filePath = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    String channelId = th.basicChannel().getId();
    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, filePath)).readEntity();
    FileInfo fileInfo = uploadResult.getFileInfos()[0];
    String fileId = fileInfo.getId();

    FileInfo metadata = assertNoError(client.getFileMetadata(fileId)).readEntity();
    assertEquals(filePath.getFileName().toString(), metadata.getName());
  }

}