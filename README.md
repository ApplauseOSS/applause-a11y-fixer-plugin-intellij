# applause-a11y-fixer-plugin-intellij

(*if you are looking for the curated version of the plug-in please click
[here](https://www.applause.com/accessibility-tool?utm_medium=referral&utm_source=github&utm_campaign=R-NA_a11y_tool#solutionsContact)*)

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
