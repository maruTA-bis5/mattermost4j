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
import net.bis5.mattermost.model.Reaction;
import net.bis5.mattermost.model.ReactionList;

/**
 * Reaction API.
 * 
 * @author Takayuki Maruyama
 */
public interface ReactionApi {

  /**
   * saves an emoji reaction for a post. Returns the saved reaction if successful, otherwise an
   * error will be returned.
   */
  ApiResponse<Reaction> saveReaction(Reaction reaction);

  /**
   * returns a list of reactions to a post.
   */
  ApiResponse<ReactionList> getReactions(String postId);

  /**
   * deletes reaction of a user in a post.
   */
  ApiResponse<Boolean> deleteReaction(Reaction reaction);

}
