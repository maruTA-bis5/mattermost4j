package net.bis5.mattermost.client4;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.ClientProperties;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Tests if {@link net.bis5.mattermost.client4.MattermostClient.MattermostClientBuilder#httpConfig(Consumer)}
 * is applied.
 */
public class HttpConfigTest {

  @Test
  void testHttpConfig()
    throws InterruptedException, NoSuchFieldException, IllegalAccessException
  {

    final int timeout = 1234;

    final MattermostClient client = MattermostClient.builder()
      .url("http://localhost:815")
      .logLevel(Level.INFO)
      .httpConfig(clientBuilder ->
      {
        clientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
      })
      .build();

    final Field httpClientField = MattermostClient.class.getDeclaredField("httpClient");
    httpClientField.setAccessible(true);

    final Client rsClient = (Client) httpClientField.get(client);

    assertThat(
      "Internally used httpClient uses configured read timeouts",
      rsClient.getConfiguration().getProperty(ClientProperties.READ_TIMEOUT),
      is(timeout)
    );

    assertThat(
      "Internally used httpClient uses configured connect timeouts",
      rsClient.getConfiguration().getProperty(ClientProperties.CONNECT_TIMEOUT),
      is(timeout)
    );
  }
}
