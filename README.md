# applause-a11y-fixer-plugin-intellij

(*if you are looking for the curated version of the plug-in please visit:
https://www.applause.com/accessibility-tool#solutionsContact and fill out the
form*)

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
