mattermost4j
============

Mattermost API v4 client for Java.

## Requirement
JDK 8 (9+ will support soon)

## Usage
```java
MattermostClient client = new MattermostClient("YOUR-MATTERMOST-URL");

// Login by id + password
client.login(loginId, password);
// Login by Personal Access Token
client.setAccessToken(token);

```

## Install
- `git clone https://github.com/maruTA-bis5/mattermost4j.git`
- `cd mattermost4j && mvn package`
- Put `mattermost4j/target/mattermost4j.jar` and dependencies into your libraries directory 
    - We have plan to publish Maven Central

## Contribution
1. Fork it ( https://github.com/maruTA-bis5/mattermost4j/fork )
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -am 'Add some feature')
4. Push to the branch (git push origin my-new-feature)
5. Create new Pull Request

## License
[Apache Software License, Version 2.0](LICENSE.txt)

