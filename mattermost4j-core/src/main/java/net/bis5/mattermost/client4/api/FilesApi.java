/*
 * Copyright (c) 2019 Takayuki Maruyama
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
import net.bis5.mattermost.client4.model.FileUploadResult;
import net.bis5.mattermost.model.FileInfo;

/**
 * Files API.
 */
public interface FilesApi {

  /**
   * Upload a file to specified channel.
   * 
   * @param channelId channel id will be upload file to.
   * @param filePath file path to upload.
   * @throws IOException If an I/O error occurs
   * @throws IllegalArgumentException If no filePath specified
   */
  ApiResponse<FileUploadResult> uploadFile(String channelId, Path... filePath) throws IOException;

  /**
   * Get a file content.
   * 
   * @param fileId the file id to get
   * @throws IOException If an I/O error occurs
   */
  ApiResponse<Path> getFile(String fileId) throws IOException;

  /**
   * Get a file thumbnail.
   * 
   * @param fileId the file id to get thumbnail
   * @throws IOException If an I/O error occurs
   */
  ApiResponse<Path> getFileThumbnail(String fileId) throws IOException;

  /**
   * Get a file preview.
   * 
   * @param fileId the file id to get preview
   * @throws IOException If an I/O error occurs
   */
  ApiResponse<Path> getFilePreview(String fileId) throws IOException;

  /**
   * Get a public link can be access without logging in to Mattermost.
   * 
   * @param fileId the file id to get public link
   */
  ApiResponse<String> getPublicFileLink(String fileId);

  /**
   * Get a file metadata.
   * 
   * @param fileId the file id to get metadata
   */
  ApiResponse<FileInfo> getFileMetadata(String fileId);

}
