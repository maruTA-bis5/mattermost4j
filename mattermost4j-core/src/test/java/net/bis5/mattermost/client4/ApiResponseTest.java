package net.bis5.mattermost.client4;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import net.bis5.mattermost.client4.hook.IncomingWebhookClient;
import net.bis5.mattermost.model.IncomingWebhookRequest;

class ApiResponseTest {

  private MattermostClient client;
  private NanoHTTPD dummyServer;
  private int listenPort;

  private String getApplicationUrl() {
    return "http://localhost:" + listenPort;
  }

  @BeforeEach
  void setup() throws IOException {
    // find available port
    try (ServerSocket socket = new ServerSocket(0)) {
      listenPort = socket.getLocalPort();
    }

    client = new MattermostClient(getApplicationUrl());
  }

  private NanoHTTPD createFixedResponseHttpd(int listenPort, String contentType, String body) {
    return new NanoHTTPD("0.0.0.0", listenPort) {
      @Override
      public Response serve(IHTTPSession session) {
        return newFixedLengthResponse(Status.OK, contentType, body);
      }
    };
  }

  @AfterEach
  void tearDown() {
    client.close();
    if (dummyServer != null) {
      dummyServer.closeAllConnections();
      dummyServer = null;
    }
  }

  @Test
  void testCheckStatusOkForTextPlainFormatWithCharset() throws IOException {
    dummyServer = createFixedResponseHttpd(listenPort, "text/plain; charset=UTF-8", "ok");
    dummyServer.start();

    IncomingWebhookClient client = new IncomingWebhookClient("http://localhost:" + listenPort+"/hook/foobar");
    IncomingWebhookRequest payload = new IncomingWebhookRequest();
    payload.setText("Hello World");
    // ApiResponse#checkStatusOK call
    assertDoesNotThrow(() -> client.postByIncomingWebhook(payload));
  }

  @Test
  void testCheckStatusOkForTextPlainFormat() throws IOException {
    dummyServer = createFixedResponseHttpd(listenPort, "text/plain", "ok");
    dummyServer.start();

    IncomingWebhookClient client = new IncomingWebhookClient("http://localhost:" + listenPort+"/hook/foobar");
    IncomingWebhookRequest payload = new IncomingWebhookRequest();
    payload.setText("Hello World");
    // ApiResponse#checkStatusOK call
    assertDoesNotThrow(() -> client.postByIncomingWebhook(payload));
  }

  @Test
  void testCheckStatusOkForJsonFormatWithCharset() throws IOException {
    dummyServer = createFixedResponseHttpd(listenPort, "application/json; charset=UTF-8", "{\"status\":\"ok\"}");
    dummyServer.start();

    // ApiResponse#checkStatusOK call
    assertDoesNotThrow(() -> client.logout());
  }
  
  @Test
  void testCheckStatusOkForJsonFormat() throws IOException {
    dummyServer = createFixedResponseHttpd(listenPort, "application/json", "{\"status\":\"ok\"}");
    dummyServer.start();

    // ApiResponse#checkStatusOK call
    assertDoesNotThrow(() -> client.logout());
  }
}
