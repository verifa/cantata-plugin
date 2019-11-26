/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata.utils;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import io.verifa.cantata.CantataConstants;
import io.verifa.cantata.CantataRunTestBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author ksoranko@verifa.io
 */
public class CantataUtils {

    public static void executeCommand(Launcher launcher, TaskListener listener,
                                      FilePath buildDir, EnvVars envVars,
                                      ArgumentListBuilder cmds) throws AbortException {
        executeCommand(launcher, listener, buildDir, envVars, cmds, false);
    }

    public static int executeCommand(Launcher launcher, TaskListener listener,
                                     FilePath buildDir, EnvVars envVars,
                                     ArgumentListBuilder cmds, boolean ignoreReturnCode) throws AbortException {
        if (launcher.isUnix()) {
            cmds = new ArgumentListBuilder("/bin/sh", "-c", cmds.toString());
        } else {
            cmds.add("&&", "exit", "%%ERRORLEVEL%%");
            cmds = new ArgumentListBuilder("cmd.exe", "/C", cmds.toString());
        }
        try {
            int returnCode = launcher.launch().
                    stdout(listener).stderr(listener.getLogger()).
                    pwd(buildDir).envs(envVars).cmds(cmds)
                    .join();
            listener.getLogger().println("Return code: " + returnCode);
            if (!ignoreReturnCode && returnCode != 0) {
                throw new AbortException("Non-zero Return Code. Aborting.");
            } else {
                return returnCode;
            }
        } catch (IOException | InterruptedException ex) {
            throw new AbortException(ex.getMessage());
        }
    }

    public static ArgumentListBuilder getCantataCommand(CantataRunTestBuilder testBuilder) {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(CantataConstants.MAKE);
        args.add(CantataConstants.ALL);

        String[] argArray = new String[]{
                testBuilder.isExecute() ? CantataConstants.EXECUTE_1 : null,
                testBuilder.isPushToServer() ? CantataConstants.PUSH_TO_SERVER_1 : null,
                testBuilder.isAppendToTopLevelLog() ? CantataConstants.OUTPUT_TO_CONSOLE_1 : null,
                testBuilder.isOutputToConsole() ? CantataConstants.APPEND_TO_TOP_LEVEL_LOG_1 : null
        };
        Arrays.stream(argArray).filter(Objects::nonNull).forEach(args::add);

        if (!StringUtils.isEmpty(testBuilder.getCustomArguments())) {
            String[] items = testBuilder.getCustomArguments().split("\\s*,\\s*");
            Arrays.stream(items).forEach(args::add);
        }

        return args;
    }
}
