# cantata-plugin

Cantata plugin for Jenkins.

Can be used from Jenkinsfile as well as from freestyle job.

### Pipeline syntax

```groovy
cantataWrapper(lservrc, lsforsehost, cantataPath) {
    cantataRunTest('path/to/makefile', 'MY-ARGUMENT1,MY-ARGUMEN2')
}
```

### How to build

Prequisites:

- JDK 8
- Maven

Build with `mvn install -DskipTests -Dfindbugs.skip=true -Denforcer.skip=true` (skipping tests and findbugx/enforcer).

Debug locally with `mvn hpi:run -DskipTests -Dfindbugs.skip=true -Denforcer.skip=true`.

### How to deploy to Jenkins

Package with `mvn package -DskipTests -Dfindbugs.skip=true -Denforcer.skip=true` and then (`target/cantata-plugin.hpi`) 
file is created and that file can bve imported to Jenkins from Manage Jenkins -> Manage Plugins -> Advanced 
-> Upload Plugin.

### License

Copyright (C) Verifa Oy, 2019.

See the [LICENSE](./LICENSE) file in the root of this project for license details.

SPDX-License-Identifier: MIT
