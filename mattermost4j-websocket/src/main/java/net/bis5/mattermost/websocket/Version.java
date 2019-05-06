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

package net.bis5.mattermost.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
public class Version {

  private static final Version INSTANCE = new Version();
  private static final String versionString;
  private static final String componentName = "mattermost4j";

  static {
    String version = "Unknown";
    Properties props = new Properties();
    try (InputStream is = Version.class.getResourceAsStream("version.properties")) {
      props.load(is);
      version = props.getProperty("version", "Unknown");
    } catch (IOException e) {
      e.printStackTrace();
    }
    versionString = version;
  }

  public static Version getInstance() {
    return INSTANCE;
  }

  public String toUserAgent() {
    String ua = componentName + "/" + versionString;
    return ua + " " + clientImplVersion().toUserAgent();

  }

  private WsClientImplVersion clientImplVersion() {
    return WsClientImplVersion.INSTANCE;
  }

  static class WsClientImplVersion {
    private static final WsClientImplVersion INSTANCE = new WsClientImplVersion();
    private static final String implementationProductName;
    private static final String implementationVersion;

    static {
      String version = "Unknown";
      String product = "Unknown";
      Properties props = new Properties();
      try (InputStream is = Version.class.getResourceAsStream("version.properties")) {
        props.load(is);
        product = props.getProperty("clientimpl.product");
        version = props.getProperty("clientimpl.version");
      } catch (IOException e) {
        e.printStackTrace();
      }
      implementationProductName = product;
      implementationVersion = version;
    }

    public String toUserAgent() {
      return implementationProductName + "/" + implementationVersion;
    }
  }
}
