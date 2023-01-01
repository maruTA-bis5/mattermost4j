mattermost4j
============

![CI Status](https://github.com/maruTA-bis5/mattermost4j/workflows/CI/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.bis5.mattermost4j/mattermost4j-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.bis5.mattermost4j/mattermost4j-core)
[![Javadocs](http://javadoc.io/badge/net.bis5.mattermost4j/mattermost4j-core.svg)](http://javadoc.io/doc/net.bis5.mattermost4j/mattermost4j-core)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.bis5.mattermost4j%3Amattermost4j-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=net.bis5.mattermost4j%3Amattermost4j-parent)

Mattermost API v4 client for Java.

## Requirement
- JDK 11 or later
	- Additional dependencies required for Java SE runtime environment. These dependencies is not required for Jakarta EE server environment.
		- Jakarta XML Binding (`jakarta.xml.bind:jakarta.xml.bind-api`)
		- Jakarta Activation (`jakarta.activation:jakarta.activation-api`)
- Mattermost Server
    - Please check mattermost4j version compatible with your server instance:
    https://github.com/maruTA-bis5/mattermost4j/wiki#what-version-shoud-i-use
    
## Usage
### Basic API Client
```java
// Create client instance
MattermostClient client;
// case 1. use constructor - log disable and prohibit unknown properties
client = new MattermostClient("YOUR-MATTERMOST-URL");
// case 2. use builder
client = MattermostClient.builder()
    .url("YOUR-MATTERMOST-URL")
	.logLevel(Level.INFO)
	.ignoreUnknownProperties()
	.build();

// Login by id + password
client.login(loginId, password);
// Login by Personal Access Token
client.setAccessToken(token);
```

### Use Incoming Webhook
```
// You can also use builder for create client instance.
IncomingWebhookClient client = new IncomingWebhookClient("YOUR-MATTERMOST-URL");

IncomingWebhookRequest payload = new IncomingWebhookRequest();
payload.setText("Hello World!");
payload.setUsername("Override Username");

client.postByIncomingWebhook(payload);
```

## Install
### Apache Maven:
```xml
<dependency>
	<groupId>net.bis5.mattermost4j</groupId>
	<artifactId>mattermost4j-core</artifactId>
	<version>${mattermost4j.version}</version>
</dependency>
```

### Gradle:
```
compile 'net.bis5.mattermost4j:mattermost4j-core:${mattermost4j.version}'
```

## Contribution
1. Fork it ( https://github.com/maruTA-bis5/mattermost4j/fork )
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request

### Code Formatter
use https://github.com/google/styleguide/ {intellij,eclipse}-java-google-style.xml .

### CheckStyle
Currently, use CheckStyle's built-in `google_checks.xml`.

## Test
### Unit test
- `mvn test`

### Integration with Mattermost Server
1. `docker-compose up`
2. `mvn verify`

## Contact
- Create GitHub Issue (https://github.com/maruTA-bis5/primefaces-excella-exporter/issues/new)
- or Start new Discussion (https://github.com/maruTA-bis5/primefaces-excella-exporter/discussions/new)
- or DM me on the Mattermost Community Server (https://community.mattermost.com/core/messages/@maruta-bis5)

## License
[Apache Software License, Version 2.0](LICENSE.txt)

