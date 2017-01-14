#!/bin/bash
limit=$1
port=$2
cp -rf src/main/resources/application_run.conf src/main/resources/application.conf
sbt "run-main soeclient.SoeClient $limit $port"
