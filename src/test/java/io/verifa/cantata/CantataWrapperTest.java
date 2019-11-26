/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author ksoranko@verifa.io
 */
public class CantataWrapperTest {

    @Rule public JenkinsRule jenkins = new JenkinsRule();

    /** Test with pipeline with following stage:
     * node {
     *    cantataWrapper(cantataPath: 'C:\\qa_systems\\cantata\\bin', cantataServer: 'http://localhost:8085', lservrc: '$LSERVRC', lsforcehost: '$LSFORCEHOST') {
     *        cantataRunTest execute: true, pushToServer: true, appendToTopLevelLog: true, outputToConsole: true, cantataExecDir: 'Cantata/tests', arguments: ''
     *    }
     * }
     */
    @Test public void pipelineTest() throws Exception {
        WorkflowJob project = jenkins.createProject(WorkflowJob.class, "cantata-freestyle-job-test");
        project.setDefinition(new CpsFlowDefinition("" +
                "node {" +
                "   cantataWrapper(cantataPath: 'C:/qa_systems/cantata/bin', cantataServer: 'http://localhost:8085', lservrc: '$LSERVRC', lsforcehost: '$LSFORCEHOST') {" +
                "       cantataRunTest execute: true, pushToServer: true, appendToTopLevelLog: true, outputToConsole: true, cantataExecDir: 'Cantata/tests', arguments: ''" +
                "   }" +
                "}", true));

        project.scheduleBuild2(0);
        //TODO: Assert correct output
    }
}
