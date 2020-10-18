# Preparation to encrypt the artifact before pushing to Maven Central

Initial ticket to create the space at Maven Central: https://issues.sonatype.org/browse/OSSRH-60820
 
#### Generate a GPG key
- https://central.sonatype.org/pages/working-with-pgp-signatures.html#distributing-your-public-key
```shell script
gpg --gen-key
```
Use 1 year validity, the rest leave default

#### Take the key id from the output 
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

# Performing a release

**Currently this needs to run from a local env, it is not yet ready for CI**

Create/place the following files in the `secrets` directory:

- signingkey.asc: This is the file containing both the public and private gpg keys  
- gpg_keyname
- gpg_passphrase
 
- github_token

- sonatype_username
- sonatype_password

Apart from signingkey.asc, all the others need to contain the credentials their named after.

Then run one of the scripts `./patch.sh`,`./minor.sh` or `./major.sh`

# Post-release

After the release has finished, go to github and create a PR from the release branch to master, merge and that's it.