# Regular development
- branch from `develop`
- PR to `develop`
- (optional) same on [cucumber_base_http_java8_springboot_test](https://github.com/brobert83/cucumber_base_http_java8_springboot_test)
  - made updates if needed
  - if no changes needed, trigger a build on develop after the snapshot has been pushed

# To push a new version to Maven Central
- branch from `develop` to a branch called `release`
- update pom.xml: 
    - remove `-SNAPSHOT` from the version
- push the branch (Travis will build and push it to Maven Central)
- update pom.xml:
    - increment version to a `-SNAPSHOT` version
- push again (Travis will build and push it to Maven Central - snapshot repo)
- PR to master (delete the `release` branch after)
- PR from master to develop

### I don't like this develop branch but for now need to keep the README in sync, will need to figure a way without `develop` 

# Todos:
 - the release process needs to git tag
 - replace Unirest with OkHttp
 - provide full interaction with the http library
 - create a test project that does not use Spring/Springboot
