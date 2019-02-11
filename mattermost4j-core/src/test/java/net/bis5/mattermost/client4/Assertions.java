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

package net.bis5.mattermost.client4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Custom assertions methods.
 */
public class Assertions {

  public static void assertSameFile(Path expected, Path actual) throws IOException {
    String expectedHash = DigestUtils.sha1Hex(Files.readAllBytes(expected));
    String actualHash = DigestUtils.sha1Hex(Files.readAllBytes(actual));

    assertEquals(expectedHash, actualHash);
  }

}
