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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.junit.client.GWTTestCase.TestModuleInfo;
import com.google.gwt.junit.client.impl.JUnitHost.TestInfo;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Tests of {@link BatchingStrategy}.
 */
public class BatchingStrategyTest extends TestCase {

  /**
   * The synthetic name of the module used for this test.
   */
  private static final String FAKE_MODULE_SYNTHETIC_NAME = "FakeModuleName.Fake";

  /**
   * The fake key in {@link GWTTestCase#ALL_GWT_TESTS}.
   */
  private static TestModuleInfo fakeModuleInfo;

  private static final TestInfo TEST_INFO_0_0 = new TestInfo(
      FAKE_MODULE_SYNTHETIC_NAME, "testClass0", "testMethod0");
  private static final TestInfo TEST_INFO_0_1 = new TestInfo(
      FAKE_MODULE_SYNTHETIC_NAME, "testClass0", "testMethod1");
  private static final TestInfo TEST_INFO_1_2 = new TestInfo(
      FAKE_MODULE_SYNTHETIC_NAME, "testClass1", "testMethod2");
  private static final TestInfo TEST_INFO_1_3 = new TestInfo(
      FAKE_MODULE_SYNTHETIC_NAME, "testClass1", "testMethod3");
  private static final TestInfo TEST_INFO_1_4 = new TestInfo(
      FAKE_MODULE_SYNTHETIC_NAME, "testClass1", "testMethod4");
  private static final TestInfo TEST_INFO_2_5 = new TestInfo(
      FAKE_MODULE_SYNTHETIC_NAME, "testClass2", "testMethod5");

  public void testClassBatchingStrategy() {
    List<TestInfo[]> testBlocks = new ArrayList<TestInfo[]>();
    testBlocks.add(new TestInfo[] {TEST_INFO_0_0, TEST_INFO_0_1});
    testBlocks.add(new TestInfo[] {TEST_INFO_1_2, TEST_INFO_1_3, TEST_INFO_1_4});
    testBlocks.add(new TestInfo[] {TEST_INFO_2_5});
    testBatchingStrategy(new ClassBatchingStrategy(), testBlocks);
  }

  public void testModuleBatchingStrategy() {
    List<TestInfo[]> testBlocks = new ArrayList<TestInfo[]>();
    testBlocks.add(new TestInfo[] {
        TEST_INFO_0_0, TEST_INFO_0_1, TEST_INFO_1_2, TEST_INFO_1_3,
        TEST_INFO_1_4, TEST_INFO_2_5});
    testBatchingStrategy(new ModuleBatchingStrategy(), testBlocks);
  }

  public void testNoBatchingStrategy() {
    List<TestInfo[]> testBlocks = new ArrayList<TestInfo[]>();
    testBlocks.add(new TestInfo[] {TEST_INFO_0_0});
    testBlocks.add(new TestInfo[] {TEST_INFO_0_1});
    testBlocks.add(new TestInfo[] {TEST_INFO_1_2});
    testBlocks.add(new TestInfo[] {TEST_INFO_1_3});
    testBlocks.add(new TestInfo[] {TEST_INFO_1_4});
    testBlocks.add(new TestInfo[] {TEST_INFO_2_5});
    testBatchingStrategy(new NoBatchingStrategy(), testBlocks);
  }

  /**
   * Add a fake entry to {@link GWTTestCase#ALL_GWT_TESTS} for testing.
   */
  private void populateAllGwtTests() {
    if (fakeModuleInfo == null) {
      fakeModuleInfo = new TestModuleInfo("FakeModuleName",
          FAKE_MODULE_SYNTHETIC_NAME, null);
      Set<TestInfo> tests = fakeModuleInfo.getTests();
      tests.add(TEST_INFO_0_0);
      tests.add(TEST_INFO_0_1);
      tests.add(TEST_INFO_1_2);
      tests.add(TEST_INFO_1_3);
      tests.add(TEST_INFO_1_4);
      tests.add(TEST_INFO_2_5);
    }
    GWTTestCase.ALL_GWT_TESTS.put(fakeModuleInfo.getSyntheticModuleName(),
        fakeModuleInfo);
  }

  /**
   * Remove the fake entry in {@link GWTTestCase#ALL_GWT_TESTS} for testing.
   */
  private void depopulateAllGwtTests() {
    GWTTestCase.ALL_GWT_TESTS.remove(FAKE_MODULE_SYNTHETIC_NAME);
  }

  private void testBatchingStrategy(BatchingStrategy strategy,
      List<TestInfo[]> expectedBlocks) {
    try {
      populateAllGwtTests();

      List<TestInfo[]> actualBlocks = strategy.getTestBlocks(FAKE_MODULE_SYNTHETIC_NAME);
      assertEquals(expectedBlocks.size(), actualBlocks.size());
      for (int i = 0; i < expectedBlocks.size(); i++) {
        TestInfo[] expectedBlock = expectedBlocks.get(i);
        TestInfo[] actualBlock = actualBlocks.get(i);
        assertEquals(expectedBlock.length, actualBlock.length);
        for (int j = 0; j < expectedBlock.length; j++) {
          assertEquals(expectedBlock[j], actualBlock[j]);
        }
      }
    } finally {
      depopulateAllGwtTests();
    }
  }
}