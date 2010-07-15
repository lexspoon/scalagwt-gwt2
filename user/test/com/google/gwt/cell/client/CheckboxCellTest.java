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
package com.google.gwt.cell.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;

/**
 * Tests for {@link CheckboxCell}.
 */
public class CheckboxCellTest extends EditableCellTestBase<Boolean, Boolean> {

  public void testOnBrowserEventChecked() {
    NativeEvent event = Document.get().createChangeEvent();
    testOnBrowserEvent("<input type=\"checkbox\" checked/>", event, false, null,
        Boolean.TRUE, true);
  }

  public void testOnBrowserEventUnchecked() {
    NativeEvent event = Document.get().createChangeEvent();
    testOnBrowserEvent(
        "<input type=\"checkbox\"/>", event, true, null, Boolean.FALSE, false);
  }

  @Override
  protected boolean consumesEvents() {
    return true;
  }

  @Override
  protected CheckboxCell createCell() {
    return new CheckboxCell();
  }

  @Override
  protected Boolean createCellValue() {
    return true;
  }

  @Override
  protected Boolean createCellViewData() {
    return false;
  }

  @Override
  protected boolean dependsOnSelection() {
    return false;
  }

  @Override
  protected String getExpectedInnerHtml() {
    return "<input type=\"checkbox\" checked/>";
  }

  @Override
  protected String getExpectedInnerHtmlNull() {
    return "<input type=\"checkbox\"/>";
  }

  @Override
  protected String getExpectedInnerHtmlViewData() {
    return "<input type=\"checkbox\"/>";
  }
}
