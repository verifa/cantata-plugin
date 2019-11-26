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
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

import static io.verifa.cantata.utils.CantataUtils.executeCommand;
import static io.verifa.cantata.utils.CantataUtils.getCantataCommand;

/**
 * @author ksoranko@verifa.io
 */
public class CantataRunTestBuilder extends Builder implements SimpleBuildStep {

    private String cantataExecDir;
    private String customArguments;
    private boolean execute;
    private boolean pushToServer;
    private boolean appendToTopLevelLog;
    private boolean outputToConsole;

    @DataBoundConstructor
    public CantataRunTestBuilder() {
    }

    @DataBoundSetter
    public void setCantataExecDir(String cantataExecDir) {
        this.cantataExecDir = cantataExecDir;
    }

    @DataBoundSetter
    public void setCustomArguments(String customArguments) {
        this.customArguments = customArguments;
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
    public String getCustomArguments() { return customArguments; }
    public boolean isExecute() { return execute; }
    public boolean isPushToServer() { return pushToServer; }
    public boolean isAppendToTopLevelLog() { return appendToTopLevelLog; }
    public boolean isOutputToConsole() { return outputToConsole; }

    @Override
    public void perform(Run<?, ?> build, @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
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
        logger.logMessage("Starting Cantata test run step");

        ArgumentListBuilder command = getCantataCommand(this);
        FilePath directory = new FilePath(workspace, getCantataExecDir());
        logger.logMessage("Executing " + command + " in " + directory);
        executeCommand(launcher, listener, directory, envVars, command);
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

        @Nonnull
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
