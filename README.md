# applause-a11y-fixer-plugin-intellij

## Dependencies

**Update dependency submodules**
```bash
$ git submodule update --remote
```


## Setup

**Build and Package the Node.js dependency**
```bash
$ ./gradlew a11tFixerPackage
```

**Build the Intellij Plugin**
```bash
$ ./gradlew buildPlugin
```

**Run Intellij Dev IDE**
```bash
$ ./gradlew runIde
```
