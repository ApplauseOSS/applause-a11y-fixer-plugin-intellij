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
