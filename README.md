# applause-a11y-fixer-plugin-intellij

## Dependencies

**Update dependency submodules**
```bash
$ git submodule update --remote
```


## Setup

**Build and Package the Node.js dependency**
```bash
$ ./gradlew a11yFixerPackage
```

**Run Intellij IDE in Development Mode**

This will build the plugin and load it in the Dev IDE.
```bash
$ ./gradlew runIde
```

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
