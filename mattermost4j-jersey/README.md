# Mattermost4J - Jersey Client Adapter

JPMS Module: `net.bis5.mattermost4j.jersey`

## Install
Add these dependencies to your project:
- Always required
  - net.bis5.mattermost4j:mattermost4j-core
  - net.bis5.mattermost4j:mattermost4j-jersey
- Optional: If your runtime environment provides these dependencies, you MUST add. Otherwise don't needed.
  - org.glassfish.jersey.core:jersey-client
  - org.glassfish.jersey.inject:jersey-hk2
  - org.glassfish.jersey.media:jersey-media-json-jackson
  - org.glassfish.jersey.media:jersey-media-multipart
  - jakarta.inject:jakarta.inject-api
  - jakarta.annotation:jakarta.annotation-api
  - org.jvnet.mimeull:mimepull
