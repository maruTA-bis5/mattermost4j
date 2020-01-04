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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.vdurmont.semver4j.Semver;
import com.vdurmont.semver4j.Semver.SemverType;
import com.vdurmont.semver4j.Semver.VersionDiff;
import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import net.bis5.mattermost.client4.model.ApiError;

/**
 * Custom assertions methods.
 */
public class Assertions {

  public static void assertSameFile(Path expected, Path actual) throws IOException {
    String expectedHash = DigestUtils.sha1Hex(Files.readAllBytes(expected));
    String actualHash = DigestUtils.sha1Hex(Files.readAllBytes(actual));

    assertEquals(expectedHash, actualHash);
  }

  public static <T> ApiResponse<T> assertNoError(ApiResponse<T> response) {
    return checkNoError(response);
  }

  public static <T> ApiResponse<T> assertStatus(ApiResponse<T> response, Status status) {
    Response rawResponse = response.getRawResponse();

    assertThat(rawResponse.getStatus(), is(status.getStatusCode()));

    return response;
  }

  public static class HasError<T extends ApiResponse<U>, U> extends BaseMatcher<T> {

    private ApiResponse<U> actual;

    /**
     * @see org.hamcrest.Matcher#matches(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object actual) {
      if (actual instanceof ApiResponse) {
        this.actual = (ApiResponse<U>) actual;
        return this.actual.hasError();
      }
      return false;
    }

    /**
     * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
     */
    @Override
    public void describeTo(Description description) {
      description.appendText("Should have failed");
    }

    public static <U> Matcher<? extends ApiResponse<U>> hasError() {
      return new HasError<>();
    }

  }

  public static boolean isNotSupportVersion(String minimumRequirement, ApiResponse<?> response) {
    Semver serverVersion = new Semver(response.getRawResponse().getHeaderString("X-Version-Id"));
    Semver requirement = new Semver(minimumRequirement);
    return serverVersion.compareTo(requirement) < 0;
  }

  public static boolean isSupportVersion(String minimumRequirement, ApiResponse<?> response) {
    return !isNotSupportVersion(minimumRequirement, response);
  }

  public static boolean isMajorMinorVersionMatches(String majorMinorVersion, ApiResponse<?> response) {
    Semver serverVersion = new Semver(response.getRawResponse().getHeaderString("X-Version-Id"));
    Semver majorMinorSemver = new Semver(majorMinorVersion, SemverType.LOOSE);
    return !EnumSet.of(VersionDiff.MAJOR, VersionDiff.MINOR)
        .contains(majorMinorSemver.diff(serverVersion));
  }


  public static <T> ApiResponse<T> checkNoError(ApiResponse<T> response) {
    response.getRawResponse().bufferEntity();
    try {
      // if ignoreUnknownProperty is true, no exception will be thrown
      ApiError error = response.readError();
      Status.Family responseStatus = Status.Family.familyOf(error.getStatusCode());
      if (responseStatus == Status.Family.CLIENT_ERROR
          || responseStatus == Status.Family.SERVER_ERROR) {
        throw new AssertionError("Expected no error, got " + error);
      }
      // no error
    } catch (ProcessingException ex) {
      // no error
    }
    return response;
  }
}
