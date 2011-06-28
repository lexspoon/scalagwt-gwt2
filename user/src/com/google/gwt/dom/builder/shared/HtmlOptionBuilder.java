/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.dom.builder.shared;

/**
 * Implementation of {@link OptionBuilder}.
 */
public class HtmlOptionBuilder extends HtmlElementBuilderBase<OptionBuilder>
    implements OptionBuilder {

  HtmlOptionBuilder(HtmlBuilderImpl delegate) {
    super(delegate);
  }

  @Override
  public OptionBuilder defaultSelected() {
    return attribute("defaultSelected", "defaultSelected");
  }

  @Override
  public OptionBuilder disabled() {
    return attribute("disabled", "disabled");
  }

  @Override
  public OptionBuilder label(String label) {
    return attribute("label", label);
  }

  @Override
  public OptionBuilder selected() {
    return attribute("selected", "selected");
  }

  @Override
  public OptionBuilder value(String value) {
    return attribute("value", value);
  }
}
