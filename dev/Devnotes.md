# Regular development
- branch from `master`
- PR to `master`
- (optional) same on [cucumber_base_http_java8_springboot_test](https://github.com/brobert83/cucumber_base_http_java8_springboot_test)
  - made updates if needed
  - if no changes needed, trigger a build on develop after the snapshot has been pushed

# To push a new version to Maven Central
- branch from `master` to a branch called `release`
- update pom.xml: 
    - remove `-SNAPSHOT` from the version
- push the branch (Travis will build and push it to Maven Central)
- update pom.xml:
    - increment version to a `-SNAPSHOT` version
- push again (Travis will build and push it to Maven Central - snapshot repo)
- PR to master (delete the `release` branch after)
- PR from master to develop

# Todos:
 - the release process needs to git tag
 - create a test project that does not use Spring/Springboot
