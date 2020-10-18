#!/bin/bash

type=$1

interactive=$([[ $2 == "it" ]] && echo "-it --entrypoint /bin/bash")

[[ ! -d .m2 ]] && mkdir .m2

docker pull robertbaboi/basic-java8-release

docker run \
   ${interactive} \
   -e RELEASE_TYPE=${type} \
   -e GITHUB_REPO=github.com/brobert83/cucumber_base_http_java8.git \
   -e GITHUB_BRANCH=master \
   -e GITHUB_USERNAME=brobert83 \
   -e GITHUB_EMAIL=robert.baboi@gmail.com \
   -e GITHUB_TOKEN_FILE=/work/secrets/github_token \
   -e SIGNING_KEY_FILE=/work/secrets/signingkey.asc \
   -e GPG_KEYNAME_FILE=/work/secrets/gpg_keyname \
   -e GPG_KEY_PASSPHRASE=/work/secrets/gpg_passphrase \
   -e SONATYPE_USERNAME=/work/secrets/sonatype_username \
   -e SONATYPE_PASSWORD=/work/secrets/sonatype_password \
   -v $(pwd)/../secrets:/work/secrets \
   -v $(pwd)/.m2:/root/.m2 \
  robertbaboi/basic-java8-release
