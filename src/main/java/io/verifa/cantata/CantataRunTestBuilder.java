/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata;

import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import io.verifa.cantata.utils.CantataUtils;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * @author ksoranko@verifa.io
 */
public class CantataRunTestBuilder extends Builder implements SimpleBuildStep {

    private final String cantataExecDir;
    private String arguments;
    private boolean execute;
    private boolean pushToServer;
    private boolean appendToTopLevelLog;
    private boolean outputToConsole;

    @DataBoundConstructor
    public CantataRunTestBuilder(String cantataExecDir) {
        this.cantataExecDir = cantataExecDir;
    }

    @DataBoundSetter
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @DataBoundSetter
    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    @DataBoundSetter
    public void setPushToServer(boolean pushToServer) {
        this.pushToServer = pushToServer;
    }

    @DataBoundSetter
    public void setAppendToTopLevelLog(boolean appendToTopLevelLog) {
        this.appendToTopLevelLog = appendToTopLevelLog;
    }

    @DataBoundSetter
    public void setOutputToConsole(boolean outputToConsole) {
        this.outputToConsole = outputToConsole;
    }

    public String getCantataExecDir() { return cantataExecDir; }
    public String getArguments() { return arguments; }
    public boolean isExecute() { return execute; }
    public boolean isPushToServer() { return pushToServer; }
    public boolean isAppendToTopLevelLog() { return appendToTopLevelLog; }
    public boolean isOutputToConsole() { return outputToConsole; }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws AbortException {
        EnvVars envVars = null;
        try {
            envVars = build.getEnvironment(listener);
        } catch (IOException | InterruptedException ex) {
            throw new AbortException(ex.getMessage());
        }
        perform(build, envVars, workspace, launcher, listener);
    }

    public void perform(Run<?, ?> build, EnvVars envVars, FilePath workspace, Launcher launcher, TaskListener listener)
            throws AbortException {
        CantataLogger logger = new CantataLogger("RunTestBuilder", listener.getLogger());
        logger.logMessage("Starting Cantata test runner step");

        ArgumentListBuilder command = getMakeAllCmd(getArguments(), isExecute(), isPushToServer(), isAppendToTopLevelLog(), isOutputToConsole());
        FilePath directory = new FilePath(workspace, getCantataExecDir());
        logger.logMessage("Executing " + command + " in " + directory);
        CantataUtils.executeCommand(launcher, listener, directory, envVars, command);
    }

    private ArgumentListBuilder getMakeAllCmd(String argsString, boolean execute, boolean pushToServer, boolean appendToTopLevelLog, boolean outputToConsole) {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(CantataConstants.MAKE);
        args.add(CantataConstants.ALL);
        String[] items;
        if (!StringUtils.isEmpty(argsString)) {
            items = argsString.split("\\s*,\\s*");
            for (String item : items) {
                args.add(item);
            }
        }
        String arg1 = execute ? CantataConstants.EXECUTE_1 : null;
        args.add(arg1);
        String arg2 = pushToServer ? CantataConstants.PUSH_TO_SERVER_1 : null;
        args.add(arg2);
        String arg3 = appendToTopLevelLog ? CantataConstants.OUTPUT_TO_CONSOLE_1 : null;
        args.add(arg3);
        String arg4 = outputToConsole ? CantataConstants.APPEND_TO_TOP_LEVEL_LOG_1 : null;
        args.add(arg4);
        return args;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Extension
    @Symbol("cantataRunTest")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public String getDisplayName() {
            return CantataConstants.CANTATA;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }
    }

}
