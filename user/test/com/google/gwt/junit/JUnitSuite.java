/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.junit;

import com.google.gwt.junit.client.GWTTestCaseTest;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * Tests of the junit package.
 */
public class JUnitSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test for suite for com.google.gwt.junit");

    suite.addTestSuite(FakeMessagesMakerTest.class);

    // client
    // Suppressed due to flakiness on Linux
    // suite.addTestSuite(BenchmarkTest.class);
    suite.addTestSuite(GWTTestCaseTest.class);

    // These two are intended only to be run manually. See class comments
    // suite.addTestSuite(ParallelRemoteTest.class);
    // suite.addTestSuite(TestManualAsync.class);

    // remote
    // Run manually only, launches servers that die on port contention
    // suite.addTestSuite(BrowserManagerServerTest.class);

    return suite;
  }
}