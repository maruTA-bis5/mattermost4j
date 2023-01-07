package net.bis5.mattermost.respheader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentDispositionTest {

  @ParameterizedTest(name = "Content-Disposition: {0} | expectedFileName = {1}")
  @CsvSource(nullValues = "null", value = {
    "inline, null",
    "attachment; filename=\"foobar.csv\", foobar.csv",
    "attachment; filename*=utf-8''%E6%97%A5%E6%9C%AC%E8%AA%9E%E3%83%95%E3%82%A1%E3%82%A4%E3%83%AB%E5%90%8D.txt, 日本語ファイル名.txt",
    "attachment; filename=\"dummy.txt\"; filename*=utf-8''%E6%97%A5%E6%9C%AC%E8%AA%9E%E3%83%95%E3%82%A1%E3%82%A4%E3%83%AB%E5%90%8D.txt, 日本語ファイル名.txt",
  })
  void testParse(String headerValue, String expectedFileName) {
    var contentDisposition = new ContentDisposition(headerValue);

    assertEquals(expectedFileName, contentDisposition.getFileName());
  }

}
