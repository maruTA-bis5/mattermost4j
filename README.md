mattermost4j
============

[![CircleCI](https://circleci.com/gh/maruTA-bis5/mattermost4j.svg?style=svg)](https://circleci.com/gh/maruTA-bis5/mattermost4j)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.bis5.mattermost4j/mattermost4j-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.bis5.mattermost4j/mattermost4j-core)
[![Javadocs](http://javadoc.io/badge/net.bis5.mattermost4j/mattermost4j-core.svg)](http://javadoc.io/doc/net.bis5.mattermost4j/mattermost4j-core)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=net.bis5.mattermost4j%3Amattermost4j-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=net.bis5.mattermost4j%3Amattermost4j-parent)

Mattermost API v4 client for Java.

## Requirement
- JDK 8 (9+ will support soon)
- Mattermost Server
    - ESR (currently 4.10)
    - or Latest (5.6)

## Usage
### Basic API Client
```java
MattermostClient client = new MattermostClient("YOUR-MATTERMOST-URL");

// Login by id + password
client.login(loginId, password);
// Login by Personal Access Token
client.setAccessToken(token);
```

### Use Incoming Webhook
```
MattermostClient client = new MattermostClient("YOUR-MATTERMOST-URL")

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
	<version>0.5.0</version>
</dependency>
```

### Gradle:
```
compile 'net.bis5.mattermost4j:mattermost4j-core:0.5.0'
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

## License
[Apache Software License, Version 2.0](LICENSE.txt)

