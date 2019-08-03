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

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.client4.model.GetBotsOption;
import net.bis5.mattermost.model.Bot;
import net.bis5.mattermost.model.BotPatch;
import net.bis5.mattermost.model.Bots;

/**
 * Bots API.
 * 
 * @author Takayuki Maruyama
 * @since Mattermost Server 5.12
 */
public interface BotsApi {

  /**
   * Create a bot.
   */
  ApiResponse<Bot> createBot(BotPatch bot);

  /**
   * Patch a bot.
   */
  ApiResponse<Bot> patchBot(String botUserId, BotPatch patch);

  /**
   * Get a bot exclude deleted.
   */
  default ApiResponse<Bot> getBot(String botUserId) {
    return getBot(botUserId, false);
  }

  /**
   * Get a bot.
   */
  ApiResponse<Bot> getBot(String botUserId, boolean includeDeleted);

  /**
   * Get bots.
   * 
   * @see GetBotsOption
   */
  default ApiResponse<Bots> getBots() {
    return getBots(Pager.defaultPager(), GetBotsOption.defaultInstance());
  }

  /**
   * Get bots.
   */
  default ApiResponse<Bots> getBots(GetBotsOption option) {
    return getBots(Pager.defaultPager(), option);
  }

  /**
   * Get bots.
   */
  ApiResponse<Bots> getBots(Pager pager, GetBotsOption option);

  /**
   * Disable a bot.
   */
  ApiResponse<Bot> disableBot(String botUserId);

  /**
   * Enable a bot.
   */
  ApiResponse<Bot> enableBot(String botUserId);

  /**
   * Assign a bot to a user.
   */
  ApiResponse<Bot> assignBotToUser(String botUserId, String ownerUserId);
}
