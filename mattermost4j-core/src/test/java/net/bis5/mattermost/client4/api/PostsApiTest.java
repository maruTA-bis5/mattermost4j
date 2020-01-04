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
import static net.bis5.mattermost.client4.Assertions.isSupportVersion;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
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
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.client4.model.FileUploadResult;
import net.bis5.mattermost.model.FileInfo;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostImage;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;
import net.bis5.mattermost.model.PostSearchResults;
import net.bis5.mattermost.model.PostType;
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;

// Posts
@ExtendWith(MattermostClientTestExtension.class)
class PostsApiTest implements MattermostClientTest {
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
  public void createPost() {
    Post post = new Post();
    post.setChannelId(th.basicChannel().getId());
    post.setMessage("Hello World!");

    ApiResponse<Post> response = assertNoError(client.createPost(post));
    Post postedPost = response.readEntity();

    assertThat(postedPost.getMessage(), is(post.getMessage()));
    assertThat(postedPost.getChannelId(), is(post.getChannelId()));
  }

  @Test
  public void createEphemeralPost() {
    // create_post_ephemeral permission currently only given to system admin
    th.logout().loginSystemAdmin();

    Post post = new Post();
    post.setChannelId(th.basicChannel().getId());
    post.setMessage("Hello World!");

    ApiResponse<Post> response = assertNoError(client.createEphemeralPost(th.basicUser().getId(), post));
    Post postedPost = response.readEntity();

    assertThat(postedPost.getMessage(), is(post.getMessage()));
    assertThat(postedPost.getChannelId(), is(post.getChannelId()));
    assertThat(postedPost.getType(), is(PostType.EPHEMERAL));
  }

  @Test
  public void getPost() {
    String postId = th.basicPost().getId();

    ApiResponse<Post> response = assertNoError(client.getPost(postId, null));
    Post post = response.readEntity();

    assertThat(post.getId(), is(postId));
  }

  @Test
  public void getPostHasEmbedImage() throws IOException, URISyntaxException {
    String channelId = th.basicChannel().getId();
    Post post = new Post();
    post.setChannelId(channelId);
    post.setMessage("logo file from: https://github.com/hmhealey/test-files/raw/master/logoVertical.png");

    ApiResponse<Post> response = assertNoError(client.createPost(post));
    if (isNotSupportVersion("5.8.0", response)) {
      return;
    }
    Post createdPost = response.readEntity();
    response = assertNoError(client.getPost(createdPost.getId()));
    createdPost = response.readEntity();

    PostImage image = createdPost.getMetadata().getImages().values().stream().findFirst().get();
    assertNotEquals(0, image.getWidth());
    assertNotEquals(0, image.getHeight());
    if (isSupportVersion("5.11.0", response)) {
      assertEquals("png", image.getFormat());
      assertEquals(0, image.getFrameCount()); // for gif: number of frames, other format: 0
    }
  }

  @Test
  public void deletePost() {
    String postId = th.createPost(th.basicChannel()).getId();

    ApiResponse<Boolean> response = assertNoError(client.deletePost(postId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void updatePost() {
    Post post = th.createPost(th.basicChannel());
    post.setMessage("UPDATE:" + post.getMessage());

    ApiResponse<Post> response = assertNoError(client.updatePost(post.getId(), post));
    Post updatedPost = response.readEntity();

    assertThat(updatedPost.getMessage(), is(post.getMessage()));
  }

  @Test
  public void patchPost() {
    String postId = th.createPost(th.basicChannel()).getId();
    PostPatch patch = new PostPatch();
    patch.setMessage("NEW MESSAGE");

    ApiResponse<Post> response = assertNoError(client.patchPost(postId, patch));
    Post updatedPost = response.readEntity();

    assertThat(updatedPost.getMessage(), is(patch.getMessage()));
  }

  @Test
  public void getThread() {
    String postId = th.basicPost().getId();

    ApiResponse<PostList> response = assertNoError(client.getPostThread(postId, null));
    PostList posts = response.readEntity();

    assertThat(posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet()), hasItem(postId));
  }

  @Test
  public void getFlaggedPosts() {
    Post post = th.basicPost();

    // XXX "Flag post" operation need make more simple?
    Preferences prefs = new Preferences();
    Preference flag = new Preference();
    flag.setUserid(th.basicUser().getId());
    flag.setCategory(PreferenceCategory.FLAGGED_POST);
    flag.setName(post.getId());
    flag.setValue(Boolean.toString(true));
    prefs.add(flag);
    assertNoError(client.updatePreferences(th.basicUser().getId(), prefs));

    PostList flaggedPosts = assertNoError(client.getFlaggedPostsForUser(th.basicUser().getId())).readEntity();
    assertThat(flaggedPosts.getOrder().contains(post.getId()), is(true));

    flaggedPosts = assertNoError(
        client.getFlaggedPostsForUserInChannel(th.basicUser().getId(), post.getChannelId())).readEntity();
    assertThat(flaggedPosts.getOrder().contains(post.getId()), is(true));

    flaggedPosts = assertNoError(
        client.getFlaggedPostsForUserInTeam(th.basicUser().getId(), th.basicTeam().getId())).readEntity();
    assertThat(flaggedPosts.getOrder().contains(post.getId()), is(true));
  }

  @Test
  public void getFileInfoForPost() throws IOException, URISyntaxException {
    Path file1 = th.getResourcePath(TestHelper.EMOJI_CONSTRUCTION);
    Path file2 = th.getResourcePath(TestHelper.EMOJI_GLOBE);
    String channelId = th.basicChannel().getId();
    FileUploadResult uploadResult = assertNoError(client.uploadFile(channelId, file1, file2)).readEntity();
    FileInfo fileInfo1 = uploadResult.getFileInfos()[0];
    FileInfo fileInfo2 = uploadResult.getFileInfos()[1];

    Post post = new Post(channelId, "file attached");
    post.setFileIds(Arrays.asList(fileInfo1.getId(), fileInfo2.getId()));
    post = assertNoError(client.createPost(post)).readEntity();
    String postId = post.getId();

    FileInfo[] fileInfosForPost = assertNoError(client.getFileInfoForPost(postId)).readEntity();
    assertEquals(2, fileInfosForPost.length);
    Set<String> fileIds = new HashSet<>(Arrays.asList(fileInfo1.getId(), fileInfo2.getId()));
    assertAll(() -> fileIds.contains(fileInfosForPost[0].getId()),
        () -> fileIds.contains(fileInfosForPost[1].getId()));
  }

  @Test
  public void getPostsForChannel() {
    String channelId = th.basicChannel().getId();

    ApiResponse<PostList> response = assertNoError(client.getPostsForChannel(channelId, Pager.of(0, 60), null));
    PostList posts = response.readEntity();

    assertThat(posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
        hasItem(channelId));
  }

  @Test
  public void getPostsForChannel_Since() {
    String channelId = th.basicChannel().getId();
    ZonedDateTime since = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    ApiResponse<PostList> response = assertNoError(client.getPostsSince(channelId, since.toEpochSecond()));
    PostList posts = response.readEntity();

    assertThat(posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet()),
        hasItem(channelId));
  }

  @Test
  public void getPostsForChannel_Before() {
    String channelId = th.basicChannel().getId();
    Post post1 = th.createPost(th.basicChannel());
    Post post2 = th.createPost(th.basicChannel());
    Post post3 = th.createPost(th.basicChannel());

    ApiResponse<PostList> response = assertNoError(
        client.getPostsBefore(channelId, post2.getId(), Pager.of(0, 60), null));
    PostList posts = response.readEntity();

    Set<String> channelIds = posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet());
    Set<String> postIds = posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet());
    assertThat(channelIds, hasItem(channelId));
    assertThat(postIds, hasItem(post1.getId()));
    assertThat(postIds, not(hasItem(post2.getId())));
    assertThat(postIds, not(hasItem(post3.getId())));
  }

  @Test
  public void getPostsForChannel_After() {
    String channelId = th.basicChannel().getId();
    Post post1 = th.createPost(th.basicChannel());
    Post post2 = th.createPost(th.basicChannel());
    Post post3 = th.createPost(th.basicChannel());

    ApiResponse<PostList> response = assertNoError(
        client.getPostsAfter(channelId, post2.getId(), Pager.of(0, 60), null));
    PostList posts = response.readEntity();

    Set<String> channelIds = posts.getPosts().values().stream().map(Post::getChannelId).collect(Collectors.toSet());
    Set<String> postIds = posts.getPosts().values().stream().map(Post::getId).collect(Collectors.toSet());
    assertThat(channelIds, hasItem(channelId));
    assertThat(postIds, not(hasItem(post1.getId())));
    assertThat(postIds, not(hasItem(post2.getId())));
    assertThat(postIds, hasItem(post3.getId()));
  }

  @Test
  public void searchForTeamPosts() {
    String teamId = th.basicTeam().getId();
    String channelId = th.basicChannel().getId();
    Post post1 = new Post(channelId, "hello");
    Post post2 = new Post(channelId, "mattermost");
    Post post3 = new Post(channelId, "world");
    post1 = assertNoError(client.createPost(post1)).readEntity();
    post2 = assertNoError(client.createPost(post2)).readEntity();
    post3 = assertNoError(client.createPost(post3)).readEntity();

    ApiResponse<PostSearchResults> response = assertNoError(client.searchPosts(teamId, "hello", false));
    PostList posts = response.readEntity();

    assertThat(posts.getPosts().keySet(), hasItem(post1.getId()));
    assertThat(posts.getPosts().keySet(), not(hasItems(post2.getId(), post3.getId())));

    response = assertNoError(client.searchPosts(teamId, "hello world", true));
    posts = response.readEntity();

    assertThat(posts.getPosts().keySet(), hasItems(post1.getId(), post3.getId()));
    assertThat(posts.getPosts().keySet(), not(hasItem(post2.getId())));
  }

  @Test
  public void pinPostToChannel() {
    Post post = th.createPost(th.basicChannel());

    ApiResponse<Boolean> response = assertNoError(client.pinPost(post.getId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));

    Post pinnedPost = post;

    response = assertNoError(client.pinPost(pinnedPost.getId()));
    result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  public void unPinPost() {
    Post post = th.createPost(th.basicChannel());
    assertNoError(client.pinPost(post.getId()));

    ApiResponse<Boolean> response = assertNoError(client.unpinPost(post.getId()));
    boolean result = response.readEntity();

    assertThat(result, is(true));

    Post unpinnedPost = post;

    response = assertNoError(client.unpinPost(unpinnedPost.getId()));
    result = response.readEntity();

    assertThat(result, is(true));
  }
}
