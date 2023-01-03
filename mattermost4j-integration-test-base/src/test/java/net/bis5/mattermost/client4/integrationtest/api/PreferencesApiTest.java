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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

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
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;

// Preferences
@ExtendWith(MattermostClientTestExtension.class)
class PreferencesApiTest implements MattermostClientTest {
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
  void getPreferences() {
    String userId = th.basicUser().getId();

    ApiResponse<Preferences> response = assertNoError(client.getPreferences(userId));
    Preferences preferences = response.readEntity();

    assertThat(preferences.isEmpty(), is(false));
    assertThat(preferences.stream().map(Preference::getUserid).collect(Collectors.toSet()),
        containsInAnyOrder(userId));
  }

  @Test
  void savePreferences() {
    String userId = th.basicUser().getId();
    Preferences preferences = client.getPreferences(userId).readEntity();
    Preference preference = preferences.stream().filter(p -> p.getCategory() == PreferenceCategory.TUTORIAL_STEPS)
        .findAny().get();
    preference.setValue("1"); // 2nd tutorial step

    ApiResponse<Boolean> response = assertNoError(client.updatePreferences(userId, preferences));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void deletePreference() {
    String userId = th.basicUser().getId();
    Preferences currentPreferences = client.getPreferences(userId).readEntity();
    Preference tutorial = currentPreferences.stream()
        .filter(p -> p.getCategory() == PreferenceCategory.TUTORIAL_STEPS).findAny().get();

    Preferences deleteTargets = new Preferences();
    deleteTargets.add(tutorial);
    ApiResponse<Boolean> response = assertNoError(client.deletePreferences(userId, deleteTargets));
    boolean result = response.readEntity();

    assertThat(result, is(true));
  }

  @Test
  void getPreferencesByCategory() {
    String userId = th.basicUser().getId();
    PreferenceCategory category = PreferenceCategory.TUTORIAL_STEPS;

    ApiResponse<Preferences> response = assertNoError(client.getPreferencesByCategory(userId, category));
    Preferences preferences = response.readEntity();

    assertThat(preferences.isEmpty(), is(false));
  }

  @Test
  void getPreference() {
    String userId = th.basicUser().getId();
    PreferenceCategory category = PreferenceCategory.DISPLAY_SETTINGS;
    String name = Preference.Name.ChannelDisplayMode.getKey();
    String value = "full";
    {
      Preference preference = new Preference();
      preference.setUserid(userId);
      preference.setCategory(category);
      preference.setName(name);
      preference.setValue(value);
      Preferences preferences = new Preferences();
      preferences.add(preference);
      assertNoError(client.updatePreferences(userId, preferences));
    }

    ApiResponse<Preference> response = assertNoError(client.getPreferenceByCategoryAndName(userId, category, name));
    Preference preference = response.readEntity();

    assertThat(preference.getCategory(), is(PreferenceCategory.DISPLAY_SETTINGS));
    assertThat(preference.getName(), is(name));
    assertThat(preference.getValue(), is(value));
  }
}