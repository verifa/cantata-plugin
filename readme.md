# cantata-plugin

Cantata plugin for Jenkins.

Can be used from Jenkinsfile as well as from freestyle job.

## Example pipeline syntax

```groovy
cantataWrapper() {
    cantataRunTest customArguments: 'APPEND_TO_TOP_LEVEL_LOG=1,EXECUTE=1,OUTPUT_TO_CONSOLE=1', cantataExecDir: 'Cantata/tests'
}
```

### How to build

Prequisites:

- JDK 8
- Maven

Build with `mvn install`.

Debug locally with `mvn hpi:run`.

### How to deploy to Jenkins

Package with `mvn package` and then `target/cantata-plugin.hpi`-file is created and that file can bve imported to Jenkins from Manage Jenkins -> Manage Plugins -> Advanced -> Upload Plugin.

### License

Copyright (C) Verifa Oy, 2019.

See the [LICENSE](./LICENSE) file in the root of this project for license details.

SPDX-License-Identifier: MIT
