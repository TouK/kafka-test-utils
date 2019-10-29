#!/usr/bin/env bash
nexusPassword=$1
nexusUrl=$2

set -e

currentHashCommit=`git rev-parse HEAD`
version=${currentHashCommit}-SNAPSHOT

if [ -z "$nexusPassword" ]; then
    echo "nexusPassword missing"; exit -1
fi
if [ -z "$nexusUrl" ]; then
    echo "nexusUrl missing"; exit -1
fi

echo publishing kafka-test-utils version: $version

sbt clean
sbt -DnexusPassword=${nexusPassword} -DnexusUrl=${nexusUrl} ";set version in ThisBuild := \"$version\";set isSnapshot in ThisBuild := true" publish