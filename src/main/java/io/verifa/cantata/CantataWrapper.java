/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.tasks.SimpleBuildWrapper;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * @author ksoranko@verifa.io
 */
public class CantataWrapper extends SimpleBuildWrapper {

    private String lservrc;
    private String lsforcehost;
    private String cantataPath;
    private String cantataServer;

    @DataBoundConstructor
    public CantataWrapper() {
    }

    @DataBoundSetter
    public void setLservrc(String lservrc) {
        this.lservrc = lservrc;
    }

    @DataBoundSetter
    public void setLsforcehost(String lsforcehost) {
        this.lsforcehost = lsforcehost;
    }

    @DataBoundSetter
    public void setCantataPath(String cantataPath) {
        this.cantataPath = cantataPath;
    }

    @DataBoundSetter
    public void setCantataServer(String cantataServer) {
        this.cantataServer = cantataServer;
    }

    public String getLservrc() { return lservrc; }
    public String getLsforcehost() { return lsforcehost; }
    public String getCantataPath() { return cantataPath; }
    public String getCantataServer() { return cantataServer; }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher,
                      TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {

        final CantataLogger logger = new CantataLogger("BuildWrapper", listener.getLogger());
        logger.logMessage("Setting up environment for Cantata ...");

        String lservrcEnv = (lservrc == null) ? getDescriptor().getGlobalLservrc() : lservrc;
        if (lservrcEnv != null) {
            logger.logMessage("Setting LSERVRC environment variable:" + lservrcEnv);
            context.env(CantataConstants.LSERVRC, lservrcEnv);
        }

        String lsforcehostEnv = (lsforcehost == null) ? getDescriptor().getGlobalLsforcehost() : lsforcehost;
        if (lsforcehostEnv != null) {
            logger.logMessage("Setting LSFORCEHOST environment variable:" + lsforcehostEnv);
            context.env(CantataConstants.LSFORCEHOST, lsforcehostEnv);
        }

        String cantataPathEnv = (cantataPath == null) ? getDescriptor().getGlobalCantataPath() : cantataPath;
        if (cantataPathEnv != null) {
            String separator = (launcher.isUnix()) ? ":" : ";";
            String path = initialEnvironment.get( "PATH" );
            logger.logMessage("Adding Cantata directory to $PATH:" + cantataPathEnv);
            context.env("PATH", cantataPathEnv + separator + path);
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    @Symbol("cantataWrapper")
    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        private String globalLservrc;
        private String globalLsforcehost;
        private String globalCantataPath;
        private String globalCantataServer;

        public String getGlobalLservrc() { return globalLservrc; }
        public String getGlobalLsforcehost() { return globalLsforcehost; }
        public String getGlobalCantataPath() { return globalCantataPath; }
        public String getGlobalCantataServer() { return globalCantataServer; }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            globalLservrc = formData.getString("globalLservrc");
            globalLsforcehost = formData.getString("globalLsforcehost");
            globalCantataPath = formData.getString("globalCantataPath");
            globalCantataServer = formData.getString("globalCantataServer");
            save();
            return super.configure(req,formData);
        }

        public String getDisplayName() {
            return CantataConstants.CANTATA;
        }

        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }
    }
}
