# Preparation to encrypt the artifact before pushing to Maven Central

Initial ticket to create the space at Maven Central: https://issues.sonatype.org/browse/OSSRH-60820
 
#### Generate a GPG key
- https://central.sonatype.org/pages/working-with-pgp-signatures.html#distributing-your-public-key
```shell script
gpg --gen-key
```
Use 1 year validity, the rest leave default

#### Take the key id from the output, will be needed by travis and to push the pub key to public servers
```text
pub   _____/KKKKKKKK --------------------------------
uid                  -------------------------------------------------------------------------------------
sub   _____/________ --------------------------------
```
in this case **KKKKKKKK** is the key id

#### Export the public key to a file
```shell script
gpg --export --armor the_email@email.com > signingkey.asc
```

#### Export the private key to the same file (mind the >>)
```shell script
gpg --export-secret-keys --armor the_email@email.com >> signingkey.asc
```

#### Push the public key to public repos
```shell script
gpg --send-keys --keyserver pool.sks-keyservers.net ${KEY_ID}
gpg --send-keys --keyserver keyserver.ubuntu.com ${KEY_ID}
gpg --send-keys --keyserver pgp.mit.edu ${KEY_ID}
```

# Provision Travis with the secrets

#### Encrypt the sonatype username
```shell script
travis encrypt --pro SONATYPE_USERNAME=${sonatype_username} --add
```

#### Encrypt the sonatype password
```shell script
export SONATYPE_PASSWORD_B64=$(echo ${password} | base64 -w 0)
travis encrypt --pro SONATYPE_PASSWORD_B64=${SONATYPE_PASSWORD_B64} --add
```
- There is a problem when encrypting values containing quotes
- I tried this but it didn't work:
    - https://stackoverflow.com/questions/54538254/travis-ci-how-do-i-escape-my-password-for-travis-encrypt-to-work
- so what ended up working was base64-ing the value, encrypting that, and then decoding it where it is needed

#### Encrypt the gpg passphrase
```shell script
travis encrypt --pro GPG_PASSPHRASE=${gpg_passphrase} --add
```

#### Encrypt the gpg key (read the command output and take the line and put it in .travis.yml)
```shell script
travis encrypt-file --pro signingkey.asc travis/signingkey.asc.enc
```
- https://docs.travis-ci.com/user/encrypting-files/

# Miscellaneous notes
 - https://itnext.io/publishing-artifact-to-maven-central-b160634e5268
 - had to use ubuntu bionic to bypass a issue with gpg2 
   - https://stackoverflow.com/questions/53992950/maven-gpg-plugin-failing-with-inappropriate-ioctl-for-device-when-running-unde
