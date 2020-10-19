# applause-a11y-fixer-plugin-intellij

## Deploy

**Upload Plugin Package to AWS S3**

You need a local file in the repository root named: `local.properties`. The file
should contain values as follows (**NOTE:** do not quote the values or the
upload will not work)
```properties
aws.bucket=<bucket-name>
aws.accessKeyId=<aws-key-id>
aws.secretKey=<aws-secret-key>
```

Run the Gradle command (will overwrite!):
```bash
$ ./gradlew a11yFixerS3uploadBuild
```
