/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.junit.client.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;

/**
 * Specifies an Enum containing the entire range of values for a parameter
 * to a {@link com.google.gwt.junit.client.Benchmark} method.
 * 
 */
@Target(ElementType.PARAMETER)
@Documented
public @interface RangeEnum {

  /**
   * An <code>Enum</code> that contains the range of values that will be
   * supplied to the test method.
   * 
   * @return For example, {@code MyEnum.class}
   */
  Class<? extends Enum<?>> value();
}
