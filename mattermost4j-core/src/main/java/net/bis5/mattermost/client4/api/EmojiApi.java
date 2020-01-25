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
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.model.SearchEmojiRequest;
import net.bis5.mattermost.model.Emoji;
import net.bis5.mattermost.model.EmojiList;

/**
 * Emoji API.
 * 
 * @author Takayuki Maruyama
 */
public interface EmojiApi {

  /**
   * will save an emoji to the server if the current user has permission to do so. If successful,
   * the provided emoji will be returned with its Id field filled in. Otherwise, an error will be
   * returned.
   */
  ApiResponse<Emoji> createEmoji(Emoji emoji, Path imageFile);

  /**
   * returns a list of custom emoji in the system.
   */
  default ApiResponse<EmojiList> getEmojiList() {
    return getEmojiList(Pager.defaultPager());
  }

  /**
   * returns a list of custom emoji in the system.
   * 
   * @param pager add in Mattermost Server 4.1, in older version, ignore.
   */
  ApiResponse<EmojiList> getEmojiList(Pager pager);

  /**
   * returns a list of custom emoji in the system sorted by name.
   */
  default ApiResponse<EmojiList> getEmojiListSorted() {
    return getEmojiListSorted(Pager.defaultPager());
  }

  /**
   * returns a list of custom emoji in the system sorted by name.
   * 
   * @param pager
   * @since Mattermost Server 4.7
   */
  ApiResponse<EmojiList> getEmojiListSorted(Pager pager);

  /**
   * delete an custom emoji on the provided emoji id string.
   */
  ApiResponse<Boolean> deleteEmoji(String emojiId);

  /**
   * returns a custom emoji in the system on the provided emoji id string.
   */
  ApiResponse<Emoji> getEmoji(String emojiId);

  /**
   * returns the emoji image.
   */
  ApiResponse<Path> getEmojiImage(String emojiId) throws IOException;

  /**
   * get a custom emoji by name.
   */
  ApiResponse<Emoji> getEmojiByName(String emojiName);

  /**
   * search custom emoji based on request.
   */
  ApiResponse<EmojiList> searchEmoji(SearchEmojiRequest searchRequest);

  /**
   * get a list of custom emoji that name starts with or matching provided name.
   */
  ApiResponse<EmojiList> autocompleteEmoji(String name);

}
