# applause-a11y-fixer-plugin-intellij

## Deploy

**Upload Plugin Package to AWS S3**

You need a local file in the repository root `local.properties`.
This should contain values as follows.
```properties
aws.bucket=<bucket-name>
aws.accessKeyId=<aws-key-id>
aws.secretKey=<aws-secret-key>
```

Run the Gradle command (will overwrite!):
```bash
$ ./gradlew uploadBuild
```
