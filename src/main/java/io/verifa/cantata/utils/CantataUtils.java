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

import java.io.IOException;

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
            listener.getLogger().println("Return code: " + Integer.toString(returnCode));
            if (!ignoreReturnCode && returnCode != 0) {
                throw new AbortException("Non-zero Return Code. Aborting.");
            } else {
                return returnCode;
            }
        } catch (IOException | InterruptedException ex) {
            throw new AbortException(ex.getMessage());
        }
    }
}
