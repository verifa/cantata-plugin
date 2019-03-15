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
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

import static io.verifa.cantata.CantataConstants.ALL;
import static io.verifa.cantata.CantataConstants.MAKE;

/**
 * @author ksoranko@verifa.io
 */
public class CantataRunTestBuilder extends Builder implements SimpleBuildStep {

    private final String cantataExecDir;
    private final String arguments;

    @DataBoundConstructor
    public CantataRunTestBuilder(String cantataExecDir, String arguments) {
        this.cantataExecDir = cantataExecDir;
        this.arguments = arguments;
    }

    public String getCantataExecDir() { return cantataExecDir; }
    public String getArguments() { return arguments; }

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

        ArgumentListBuilder command = getMakeAllCmd(getArguments());
        FilePath directory = new FilePath(workspace, cantataExecDir);
        logger.logMessage("Executing " + command + " in " + directory);
        CantataUtils.executeCommand(launcher, listener, directory, envVars, command);
    }

    private ArgumentListBuilder getMakeAllCmd(String argsString) {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(MAKE);
        args.add(ALL);
        String[] items = argsString.split("\\s*,\\s*");
        for (String item : items) {
            args.add(item);
        }
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
