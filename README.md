mattermost4j
============

[![CircleCI](https://circleci.com/gh/maruTA-bis5/mattermost4j.svg?style=svg)](https://circleci.com/gh/maruTA-bis5/mattermost4j)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.bis5.mattermost4j/mattermost4j-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.bis5.mattermost4j/mattermost4j-core)
[![Javadocs](http://javadoc.io/badge/net.bis5.mattermost4j/mattermost4j-core.svg)](http://javadoc.io/doc/net.bis5.mattermost4j/mattermost4j-core)

Mattermost API v4 client for Java.

## Requirement
- JDK 8 (9+ will support soon)
- Mattermost Server
    - ESR (currently 4.10)
    - or Latest (5.4)

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
	<version>0.1.1</version>
</dependency>
```

### Gradle:
```
compile 'net.bis5.mattermost4j:mattermost4j-core:0.1.1'
```

## Contribution
1. Fork it ( https://github.com/maruTA-bis5/mattermost4j/fork )
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request

## License
[Apache Software License, Version 2.0](LICENSE.txt)

