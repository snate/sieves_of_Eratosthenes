#!/bin/bash
cp -rf src/main/resources/application_test.conf src/main/resources/application.conf
sbt test
