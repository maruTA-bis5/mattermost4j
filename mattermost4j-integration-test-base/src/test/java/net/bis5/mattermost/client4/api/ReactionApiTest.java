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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.Reaction;
import net.bis5.mattermost.model.ReactionList;
import net.bis5.mattermost.model.User;

// Reaction
@ExtendWith(MattermostClientTestExtension.class)
class ReactionApiTest implements MattermostClientTest {
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
  void saveReaction() {
    Post targetPost = th.basicPost();
    User reactedUser = th.basicUser();
    Reaction reaction = new Reaction();
    reaction.setUserId(reactedUser.getId());
    reaction.setPostId(targetPost.getId());
    reaction.setEmojiName("mattermost");

    Reaction savedReaction = assertNoError(client.saveReaction(reaction)).readEntity();

    assertNotEquals(0l, savedReaction.getCreateAt());
  }

  @Test
  void deleteReaction() {
    Post targetPost = th.basicPost();
    User reactedUser = th.basicUser();
    Reaction reaction = new Reaction();
    reaction.setUserId(reactedUser.getId());
    reaction.setPostId(targetPost.getId());
    reaction.setEmojiName("mattermost");
    reaction = assertNoError(client.saveReaction(reaction)).readEntity();

    ApiResponse<Boolean> deleteReactionResult = assertNoError(client.deleteReaction(reaction));

    assertTrue(deleteReactionResult.readEntity());
  }

  @Test
  void getReactions() {
    Post targetPost = th.basicPost();
    User reactedUser = th.basicUser();
    Reaction reaction = new Reaction();
    reaction.setUserId(reactedUser.getId());
    reaction.setPostId(targetPost.getId());
    reaction.setEmojiName("mattermost");
    assertNoError(client.saveReaction(reaction));
    reaction.setEmojiName("+1");
    assertNoError(client.saveReaction(reaction));

    ReactionList reactions = assertNoError(client.getReactions(targetPost.getId())).readEntity();

    assertEquals(2, reactions.size());
    assertThat(reactions.stream().map(Reaction::getEmojiName).collect(Collectors.toSet()),
        containsInAnyOrder("mattermost", "+1"));
  }

}