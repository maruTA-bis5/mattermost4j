#!/bin/bash -xe

mvn clean deploy -DskipTests=true -Darguments="-DskipTests=true" -DskipRemoteStaging=true
mvn nexus:deploy-staged
mvn nexus-staging:release

