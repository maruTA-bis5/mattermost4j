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

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;

/**
 * Preferences API.
 * 
 * @author Takayuki Maruyama
 */
public interface PreferencesApi {

  /**
   * returns the user's preferences.
   */
  ApiResponse<Preferences> getPreferences(String userId);

  /**
   * saves the user's preferences.
   */
  ApiResponse<Boolean> updatePreferences(String userId, Preferences perferences);

  /**
   * deletes the user's preferences.
   */
  ApiResponse<Boolean> deletePreferences(String userId, Preferences preferences);

  /**
   * returns the user's preferences from the provided category string.
   */
  ApiResponse<Preferences> getPreferencesByCategory(String userId, PreferenceCategory category);

  /**
   * returns the user's preferences from the provided category and preference name string.
   */
  ApiResponse<Preference> getPreferenceByCategoryAndName(String userId, PreferenceCategory category,
      String preferenceName);
}
