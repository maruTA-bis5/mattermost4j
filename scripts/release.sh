#!/bin/bash -xe

mvn clean release:prepare release:perform -DskipTests=true -Darguments="-DskipTests=true" 

