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
import static net.bis5.mattermost.client4.integrationtest.Assertions.isNotSupportVersion;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTest;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTestExtension;
import net.bis5.mattermost.client4.integrationtest.TestHelper;
import net.bis5.mattermost.client4.model.SearchEmojiRequest;
import net.bis5.mattermost.model.Emoji;
import net.bis5.mattermost.model.EmojiList;

// Emoji
@ExtendWith(MattermostClientTestExtension.class)
class EmojiApiTest implements MattermostClientTest {
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
  void createCustomEmoji() throws URISyntaxException, IOException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    String emojiName = "custom" + th.newId();
    emoji.setName(emojiName);
    emoji.setCreatorId(th.basicUser().getId());

    ApiResponse<Emoji> response = client.createEmoji(emoji, image);
    if (isNotSupportVersion("5.4.0", response)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    Emoji createdEmoji = assertNoError(response).readEntity();

    assertThat(createdEmoji.getName(), is(emojiName));
    assertThat(createdEmoji.getId(), is(not(nullValue())));
  }

  @Test
  void getCustomEmojiList() throws URISyntaxException, IOException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji1 = new Emoji();
    emoji1.setName("custom_" + th.newId());
    emoji1.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> resp1 = client.createEmoji(emoji1, image);
    if (isNotSupportVersion("5.4.0", resp1)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji1 = assertNoError(resp1).readEntity();
    Emoji emoji2 = new Emoji();
    emoji2.setName("custom_" + th.newId());
    emoji2.setCreatorId(th.basicUser().getId());
    emoji2 = client.createEmoji(emoji2, image).readEntity();

    ApiResponse<EmojiList> response = assertNoError(client.getEmojiList());
    List<Emoji> emojiList = response.readEntity();

    assertThat(emojiList.stream().map(Emoji::getId).collect(Collectors.toList()),
        hasItems(emoji1.getId(), emoji2.getId()));
  }

  @Test
  void getCustomEmojiListInOrder() throws URISyntaxException, IOException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji1 = new Emoji();
    emoji1.setName("custom1_" + th.newId());
    emoji1.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> resp1 = client.createEmoji(emoji1, image);
    if (isNotSupportVersion("5.4.0", resp1)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji1 = assertNoError(resp1).readEntity();
    Emoji emoji2 = new Emoji();
    emoji2.setName("custom2_" + th.newId());
    emoji2.setCreatorId(th.basicUser().getId());
    emoji2 = client.createEmoji(emoji2, image).readEntity();

    ApiResponse<EmojiList> response = assertNoError(client.getEmojiListSorted());
    List<Emoji> emojiList = response.readEntity();

    Set<String> validIds = new HashSet<>(Arrays.asList(emoji1.getId(), emoji2.getId()));
    List<String> returnedEmojiIdOrder = emojiList.stream() //
        .map(Emoji::getId) //
        .filter(i -> validIds.contains(i)) //
        .collect(Collectors.toList());
    assertThat(returnedEmojiIdOrder, contains(emoji1.getId(), emoji2.getId()));
  }

  @Test
  void getCustomEmoji() throws URISyntaxException, IOException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> resp1 = client.createEmoji(emoji, image);
    if (isNotSupportVersion("5.4.0", resp1)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji = assertNoError(resp1).readEntity();
    String emojiId = emoji.getId();

    ApiResponse<Emoji> response = assertNoError(client.getEmoji(emojiId));
    Emoji responseEmoji = response.readEntity();

    assertThat(responseEmoji.getName(), is(emoji.getName()));
  }

  @Test
  void deleteCustomEmoji() throws URISyntaxException, IOException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> resp1 = client.createEmoji(emoji, image);
    if (isNotSupportVersion("5.4.0", resp1)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji = assertNoError(resp1).readEntity();
    String emojiId = emoji.getId();

    ApiResponse<Boolean> response = assertNoError(client.deleteEmoji(emojiId));
    Boolean result = response.readEntity();

    assertThat(result, is(true));
    assertThat( //
        client.getEmojiList().readEntity() //
            .stream() //
            .map(Emoji::getId) //
            .collect(Collectors.toSet()), //
        is(not(hasItem(emojiId))));
  }

  @Test
  void getCustomEmojiImage() throws URISyntaxException, IOException {
    Path originalImage = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> response = client.createEmoji(emoji, originalImage);
    if (isNotSupportVersion("5.4.0", response)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji = assertNoError(response).readEntity();
    String emojiId = emoji.getId();

    ApiResponse<Path> emojiImage = assertNoError(client.getEmojiImage(emojiId));
    Path downloadedFile = emojiImage.readEntity();

    // file contents equals?
    assertSameFile(originalImage, downloadedFile);
  }

  @Test
  void getCustomEmojiByName() throws URISyntaxException, IOException {
    Path image = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    emoji.setName("custom" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> resp1 = client.createEmoji(emoji, image);
    if (isNotSupportVersion("5.4.0", resp1)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji = assertNoError(resp1).readEntity();
    String emojiName = emoji.getName();

    ApiResponse<Emoji> response = assertNoError(client.getEmojiByName(emojiName));
    Emoji responseEmoji = response.readEntity();

    assertEquals(emoji.getId(), responseEmoji.getId());
  }

  @Test
  void searchEmoji() throws URISyntaxException, IOException {
    Path emojiGlobe = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    emoji.setName("customGlobe" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> createEmojiResponse = client.createEmoji(emoji, emojiGlobe);
    if (isNotSupportVersion("5.4.0", createEmojiResponse)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji = assertNoError(createEmojiResponse).readEntity();

    SearchEmojiRequest criteria = SearchEmojiRequest.builder().term("Globe").build();

    ApiResponse<EmojiList> searchResponse = assertNoError(client.searchEmoji(criteria));
    EmojiList searchResult = searchResponse.readEntity();
    assertEquals(1, searchResult.size());
    assertEquals(emoji.getId(), searchResult.get(0).getId());

    criteria = SearchEmojiRequest.builder().term("Globe").prefixOnly(true).build();
    searchResponse = assertNoError(client.searchEmoji(criteria));
    searchResult = searchResponse.readEntity();
    assertTrue(searchResult.isEmpty());
  }

  @Test
  void autocompleteEmoji() throws URISyntaxException, IOException {
    Path emojiGlobe = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    Emoji emoji = new Emoji();
    emoji.setName("customAutocompleteGlobe" + th.newId());
    emoji.setCreatorId(th.basicUser().getId());
    ApiResponse<Emoji> createEmojiResponse = client.createEmoji(emoji, emojiGlobe);
    if (isNotSupportVersion("5.4.0", createEmojiResponse)) {
      // CreateEmoji call fail between 4.8 and 5.3
      return;
    }
    emoji = assertNoError(createEmojiResponse).readEntity();

    String name = "customAuto";
    ApiResponse<EmojiList> autocompleteResponse = assertNoError(client.autocompleteEmoji(name));
    EmojiList autocompleteList = autocompleteResponse.readEntity();

    assertEquals(1, autocompleteList.size());
    assertEquals(emoji.getId(), autocompleteList.get(0).getId());
  }

}