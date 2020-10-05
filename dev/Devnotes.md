# To push a new version to Maven Central

- create a branch called `release`
- update pom.xml: 
    - remove `-SNAPSHOT` from the version
- push the branch (Travis will build and push it to Maven Central)
- update https://github.com/brobert83/cucumber_base_http_java8_springboot_test to use the new version and make sure it works
- update pom.xml: 
    - increment version to a `-SNAPSHOT` version
- push again (Travis will build and push it to Maven Central - snapshot repo )
- PR to master (delete branch after)

# Todos:
 - the release process needs to git tag
 - replace Unirest with OkHttp
 - provide full interaction with the http library
 - create a test project that does not use Spring/Springboot
 
   
    