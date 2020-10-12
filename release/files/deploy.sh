#!/bin/bash

release_type=$(echo ${RELEASE_TYPE:-PATCH} | tr '[:lower:]' '[:upper:]')

github_repo=${GITHUB_REPO}
github_branch=${GITHUB_BRANCH:-master}
github_username=${GITHUB_USERNAME}
github_email=${GITHUB_EMAIL}
github_token=$(cat ${GITHUB_TOKEN_FILE})

signing_key_file=${SIGNING_KEY_FILE:-/work/secrets/signingkey.asc}

gpg_keyname=$(cat ${GPG_KEYNAME_FILE:-/work/secrets/gpg_keyname})
gpg_key_passphrase=$(cat ${GPG_KEY_PASSPHRASE:-/work/secrets/gpg_key_passphrase})

#sonatype_username=${SONATYPE_USERNAME}
#sonatype_password=${SONATYPE_PASSWORD}

#regex="^v_(major|minor|patch)_([0-9]+)\.([0-9]+)\.([0-9]+)$"

# ======================================================================================================================
RED="31m"
GREEN="32m"

set -x

function out(){

  local text=$1
  local color=$2

  echo -e "\n\e[${color}${text}\e[0m"
}

function info(){
  out "$1" ${GREEN}
}

echo -e "\e[31mhello\e[0m"

function nextVersion(){

  local type=$1
  local major_version=$2
  local minor_version=$3
  local patch_version=$4

  [[ "${type}" == "MAJOR" ]] && echo "$(($major_version + 1)).${minor_version}.${patch_version}"
  [[ "${type}" == "MINOR" ]] && echo "${major_version}.$(($minor_version + 1)).${patch_version}"
  [[ "${type}" == "PATCH" ]] && echo "${major_version}.${minor_version}.$(($patch_version + 1))"
}

function deploy(){

  info "Importing keys to keyring"

  gpg2 --keyring=pubring.gpg --no-default-keyring --import --batch ${signing_key_file}
  gpg2 --allow-secret-key-import --keyring=secring.gpg --no-default-keyring --import --batch ${signing_key_file}

  info "Deploing to Maven Central"

  if [[ $1 == "--dry-run" ]]; then
    info "Dry run for mvn deploy"
  else
    info deployin
#    mvn clean deploy \
#      -DskipTests=true \
#      -Prelease \
#      --settings /work/settings.xml \
#      -Dgpg.executable=gpg2 \
#      -Dgpg.keyname=${gpg_keyname} \
#      -Dgpg.passphrase=${gpg_key_passphrase} \
#      -Dgpg.publicKeyring=pubring.gpg \
#      -Dgpg.secretKeyring=secring.gpg
  fi
}

# ======================================================================================================================

{
  info "Login to github"

  git config --global user.name "${github_username}"
  git config --global user.email "${github_email}"

  git clone -b ${github_branch} https://${github_username}:${github_token}@${github_repo} repo
  cd repo

} &&

{
  info "Identifing version"

  VERSION_REGEX="^([0-9]+)\.([0-9]+)\.([0-9]+)\-SNAPSHOT$"

  # fail if the currentVersion does not match the regex
  currentVersion=$(cat pom.xml | xq -r '.project.version')
  if [[ ! "${currentVersion}" =~ ${VERSION_REGEX} ]]; then
    info "Version declard in pom.xml does not match the regular expression: ${VERSION_REGEX}"
    exit 1
  fi

  major_version="${BASH_REMATCH[1]}"
  minor_version="${BASH_REMATCH[2]}"
  patch_version="${BASH_REMATCH[3]}"

  release_version="${major_version}.${minor_version}.${patch_version}"
  next_version="$(nextVersion ${release_type} ${major_version} ${minor_version} ${patch_version})-SNAPSHOT"

  info "Release type '${release_type}': ${release_version}"
  info "New version: ${next_version}"
}&&

{
  info "Switching to branch release_${release_type}_${release_version}"
  git checkout -b release_${release_version}
}&&

{
  info "Changing version to ${release_version}"
  mvn versions:set -DnewVersion=${release_version}
} &&

{
  info "Running build"
  mvn clean package -DskipTests=true
} &&

{
  info "Deploying"
  deploy --dry-run
} &&

{
  info "Commiting version change"
  git add pom.xml
  git commit -m "Release: ${release_type} ${release_version}"
} &&

{
  info "Tagging with v${release_version}"
  git tag v${release_version}
} &&

{
  info "Updating RELEASE_LOG.md"

  echo "# ${release_tag}" >> RELEASE_LOG.md
  previous_tag=$(git tag --sort=committerdate | tail -n 2 | head -n 1)
  git log --reverse --pretty=format:"%ci; %cn \"%s\"" ${previous_tag}...${release_tag} >> RELEASE_LOG.md
  git add RELEASE_LOG.md
  git commit -m "Updated RELEASE_LOG.md"
} &&

{
  info "Increasing version to ${next_version}"
  mvn versions:set -DnewVersion=${next_version}
  git add pom.xml
  git commit -m "Updated RELEASE_LOG.md"
} &&

{
  info "Pushing changes"
  git push -u origin release_${release_version}
}&&

{
  info "Pushing tag"
  git push origin v_${release_version}
} &&

{
  info "Release complete: v_${release_version}"
}