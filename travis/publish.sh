#!/bin/bash

set -e

signing_key_file=$1

gpg2 \
  --keyring=${TRAVIS_BUILD_DIR}/pubring.gpg \
  --no-default-keyring \
  --import \
  --batch \
  ${signing_key_file}

gpg2 \
  --allow-secret-key-import \
  --keyring=${TRAVIS_BUILD_DIR}/secring.gpg \
  --no-default-keyring \
  --import \
  --batch \
  ${signing_key_file}

echo "Deploing to Maven Central"

SONATYPE_PASSWORD=$(echo $SONATYPE_PASSWORD_B64 | base64 -d) \
mvn deploy \
  -DskipTests=true \
  -Prelease \
  --settings travis/settings.xml \
  -Dgpg.executable=gpg2 \
  -Dgpg.keyname=${GPG_KEYNAME} \
  -Dgpg.passphrase=${GPG_PASSPHRASE} \
  -Dgpg.publicKeyring=${TRAVIS_BUILD_DIR}/pubring.gpg \
  -Dgpg.secretKeyring=${TRAVIS_BUILD_DIR}/secring.gpg

