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
package org.hibernate.jsr303.tck.tests.constraints.groups.inheritance;

import com.google.gwt.junit.client.GWTTestCase;

import org.hibernate.jsr303.tck.util.client.Failing;
import org.hibernate.jsr303.tck.util.client.NonTckTest;

/**
 * Test wrapper for {@link GroupInheritanceTest}.
 */
public class GroupInheritanceGwtTest extends GWTTestCase {
  // TODO(nchalko) causes com.google.gwt.dev.jjs.InternalCompilerException:
  // see http://code.google.com/p/google-web-toolkit/issues/detail?id=6508
  // private final GroupInheritanceTest delegate = new GroupInheritanceTest();

  @Override
  public String getModuleName() {
    return "org.hibernate.jsr303.tck.tests.constraints.groups.inheritance.TckTest";
  }


  @NonTckTest
  public void testDummy() {
    // There must be at least one passing test.
  }

  @Failing(issue = 5801)
  public void testGroupCanInheritGroupsViaInterfaceInheritance() {
    // delegate.testGroupCanInheritGroupsViaInterfaceInheritance();
    fail("See http://code.google.com/p/google-web-toolkit/issues/detail?id=6508");
  }

  @Failing(issue = 5801)
  public void testGroupMembership() {
    // delegate.testGroupMembership();
    fail("See http://code.google.com/p/google-web-toolkit/issues/detail?id=6508");
  }
}
