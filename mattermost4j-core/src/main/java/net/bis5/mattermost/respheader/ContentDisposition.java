package net.bis5.mattermost.respheader;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import lombok.Value;

/**
 * Content-Disposition http response header parser
 */
@Value
public class ContentDisposition {

  /** filename field */
  private final String fileName;

  /**
   * Parse Content-Disposition header
   * @param headerValue {@code Content-Disposition} response header's value.
   */
  public ContentDisposition(String headerValue) {
    Map<String, String> headerEntries = Arrays.stream(headerValue.split(";"))
      .map(String::strip)
      .map(this::toPair)
      .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    this.fileName = resolveFileName(headerEntries);
  }

  private Pair<String, String> toPair(String entry) {
    int firstEqualPos = entry.indexOf('=');
    if (firstEqualPos == -1) {
      return Pair.of(entry.toLowerCase(), "true");
    }
    String key = entry.substring(0, firstEqualPos).strip();
    String value = entry.substring(Math.min(firstEqualPos + 1, entry.length()));

    return Pair.of(key, value);
  }

  private String resolveFileName(Map<String, String> headerEntries) {
    if (headerEntries.containsKey("filename*")) {
      String encodedFileName = StringUtils.removeStartIgnoreCase(headerEntries.get("filename*"), "utf-8''");
      return URLDecoder.decode(encodedFileName);
    } else if (headerEntries.containsKey("filename")) {
      return StringUtils.unwrap(headerEntries.get("filename"), "\"");
    }
    return null;
  }
}
