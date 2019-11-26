/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata;

import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author ksoranko@verifa.io
 */
public class CantataRunTestBuilderTest {

    @Rule public JenkinsRule jenkins = new JenkinsRule();

    @Test public void configureTest() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject("cantata-freestyle-job-test");

        CantataRunTestBuilder before = new CantataRunTestBuilder();
        before.setCantataExecDir("Cantata/tests");
        before.setExecute(true);
        before.setPushToServer(true);
        before.setOutputToConsole(true);
        before.setAppendToTopLevelLog(true);
        before.setCustomArguments("ARG1=1,ARG2=2");
        project.getBuildersList().add(before);

        jenkins.submit(jenkins.createWebClient().getPage(project,"configure").getFormByName("config"));
        CantataRunTestBuilder after = project.getBuildersList().get(CantataRunTestBuilder.class);
        jenkins.assertEqualBeans(before,after,"cantataExecDir,execute,pushToServer,appendToTopLevelLog,outputToConsole,customArguments");
    }
}
