/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata.utils;

import io.verifa.cantata.CantataRunTestBuilder;
import org.junit.Test;

import static io.verifa.cantata.utils.CantataUtils.getCantataCommand;
import static org.junit.Assert.assertEquals;

/**
 * @author ksoranko@verifa.io
 */
public class CantataUtilsTest {

    @Test public void formsComplexCantataCommand() {
        CantataRunTestBuilder testBuilder = new CantataRunTestBuilder();
        testBuilder.setCantataExecDir("Cantata/tests");
        testBuilder.setExecute(true);
        testBuilder.setPushToServer(true);
        testBuilder.setOutputToConsole(true);
        testBuilder.setAppendToTopLevelLog(true);
        testBuilder.setCustomArguments("ARG1=1,ARG2=2");

        assertEquals("make all EXECUTE=1 PUSH_TO_SERVER=1 OUTPUT_TO_CONSOLE=1 APPEND_TO_TOP_LEVEL_LOG=1 ARG1=1 ARG2=2", getCantataCommand(testBuilder).toString());
    }

    @Test public void formsSimpleCantataCommand() {
        CantataRunTestBuilder testBuilder = new CantataRunTestBuilder();

        assertEquals("make all", getCantataCommand(testBuilder).toString());
    }
}
