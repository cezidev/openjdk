/*
 * Copyright (c) 2018, Google and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package MyPackage;

import java.util.List;

/**
 * @test
 * @build Frame HeapMonitor
 * @summary Verifies the default GC with the Heap Monitor event system.
 * @compile HeapMonitorGCTest.java
 * @requires vm.gc == "G1" | vm.gc == "null"
 * @run main/othervm/native -agentlib:HeapMonitorTest MyPackage.HeapMonitorGCTest
 */

/**
 * This test is checking that various GCs work as intended: events are sent, forcing GC works, etc.
 */
public class HeapMonitorGCTest {
  public static void main(String[] args) {
    if (!HeapMonitor.eventStorageIsEmpty()) {
      throw new RuntimeException("Statistics should be null to begin with.");
    }

    HeapMonitor.enableSamplingEvents();

    List<Frame> frameList = HeapMonitor.allocate();
    frameList.add(new Frame("main", "([Ljava/lang/String;)V", "HeapMonitorGCTest.java", 48));
    Frame[] frames = frameList.toArray(new Frame[0]);

    if (!HeapMonitor.obtainedEvents(frames)) {
      throw new RuntimeException("No expected events were found.");
    }

    HeapMonitor.forceGarbageCollection();

    if (!HeapMonitor.garbageContains(frames)) {
      throw new RuntimeException("Forcing GC did not work, not a single object was collected.");
    }
  }
}
