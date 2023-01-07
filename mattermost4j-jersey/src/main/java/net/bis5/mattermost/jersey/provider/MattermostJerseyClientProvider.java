package net.bis5.mattermost.jersey.provider;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import jakarta.ws.rs.client.ClientBuilder;
import net.bis5.mattermost.client4.MultiPartAdapter;
import net.bis5.mattermost.client4.spi.MattermostClientProvider;
import net.bis5.mattermost.provider.MattermostModelMapperProvider;

public class MattermostJerseyClientProvider implements MattermostClientProvider {

  @Override
  public ClientBuilder createClientBuilder(boolean ignoreUnknownProperties) {
    return ClientBuilder.newBuilder()
        .register(new MattermostModelMapperProvider(ignoreUnknownProperties))
        .register(JacksonFeature.class).register(MultiPartFeature.class)
        // needs for PUT request with null entity
        // (/commands/{command_id}/regen_token)
        .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
  }

  @Override
  public MultiPartAdapter createMultiPartAdapter() {
    return new JerseyMultiPartAdapter();
  }
  
}
