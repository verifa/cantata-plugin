/**
 * Copyright (c) Verifa Oy, 2019.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * SPDX-License-Identifier: MIT
 */
package io.verifa.cantata;

import java.io.PrintStream;

/**
 * @author ksoranko@verifa.io
 */
public class CantataLogger {

    private String msgPrefix = null;
    private PrintStream printStream = null;

    public CantataLogger(String id, PrintStream printStream) {
        this.msgPrefix = "[Cantata " + id + "] - ";
        this.printStream = printStream;
    }

    public void logMessage(String message) {
        printStream.println(msgPrefix + message);
    }
}
