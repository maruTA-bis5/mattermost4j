package net.bis5.mattermost.respheader;

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
    this.fileName = headerEntries.get("filename");
  }

  private Pair<String, String> toPair(String entry) {
    int firstEqualPos = entry.indexOf('=');
    if (firstEqualPos == -1) {
      return Pair.of(entry.toLowerCase(), "true");
    }
    String key = entry.substring(0, firstEqualPos).strip();
    String value = StringUtils.unwrap(entry.substring(firstEqualPos), "\"");

    return Pair.of(key, value);
  }
}
