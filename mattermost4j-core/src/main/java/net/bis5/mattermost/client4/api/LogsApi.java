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

import java.util.List;
import java.util.Map;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;

/**
 * Log API.
 * 
 * @author Takayuki Maruyama
 */
public interface LogsApi {

  /**
   * page of logs as a string list.
   */
  default ApiResponse<List<String>> getLogs() {
    return getLogs(Pager.defaultPager());
  }

  /**
   * page of logs as a string list.
   */
  ApiResponse<List<String>> getLogs(Pager pager);

  /**
   * This method is a convenience Web Service call so clients can log messages into the server-side
   * logs. For example we typically log javascript error messages into the server-side. It returns
   * the log message if the logging was successful.
   */
  ApiResponse<Map<String, String>> postLog(Map<String, String> message);

}
