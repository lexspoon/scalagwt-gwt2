/*
 * Copyright 2010 Google Inc.
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
package org.hibernate.jsr303.tck.tests.validation.graphnavigation;

import com.google.gwt.junit.client.GWTTestCase;

import org.hibernate.jsr303.tck.util.client.Failing;

/**
 * Test wrapper for {@link ValidationTest}.
 */
public class GraphNavigationGwtTest extends GWTTestCase {

  private final GraphNavigationTest delegate = new GraphNavigationTest();

  @Override
  public String getModuleName() {
    return "org.hibernate.jsr303.tck.tests.validation.graphnavigation.TckTest";
  }

  @Failing(issue = 5946)
  public void testContainedIterable() {
    delegate.testContainedIterable();
  }

  @Failing(issue = 5946)
  public void testContainedMap() {
    delegate.testContainedMap();
  }

  @Failing(issue = 5946)
  public void testContainedSet() {
    delegate.testContainedSet();
  }

  @Failing(issue = 5946)
  public void testFullGraphValidationBeforeNextGroupInSequence() {
    delegate.testFullGraphValidationBeforeNextGroupInSequence();
  }

  @Failing(issue = 5946)
  public void testGraphNavigationDeterminism() {
    delegate.testGraphNavigationDeterminism();
  }

  @Failing(issue = 5946)
  public void testNoEndlessLoop() {
    delegate.testNoEndlessLoop();
  }

  public void testNullReferencesGetIgnored() {
    delegate.testNullReferencesGetIgnored();
  }

  @Failing(issue = 5946)
  public void testTypeOfContainedValueIsDeterminedAtRuntime() {
    delegate.testTypeOfContainedValueIsDeterminedAtRuntime();
  }

  @Failing(issue = 5946)
  public void testTypeOfContainedValuesIsDeterminedAtRuntime() {
    delegate.testTypeOfContainedValuesIsDeterminedAtRuntime();
  }
}
