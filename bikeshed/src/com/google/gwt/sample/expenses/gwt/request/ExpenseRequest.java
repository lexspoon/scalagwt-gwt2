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
package com.google.gwt.sample.expenses.gwt.request;

import com.google.gwt.requestfactory.shared.Instance;
import com.google.gwt.requestfactory.shared.RecordListRequest;
import com.google.gwt.requestfactory.shared.RequestObject;
import com.google.gwt.requestfactory.shared.Service;
import com.google.gwt.sample.expenses.server.domain.Expense;
import com.google.gwt.valuestore.shared.PropertyReference;

/**
 * "API Generated" request selector interface implemented by objects that give
 * client access to the methods of
 * {@link com.google.gwt.sample.expenses.server.domain.Expense}.
 * <p>
 * IRL this class will be generated by a JPA-savvy tool run before compilation.
 */
@Service(Expense.class)
public interface ExpenseRequest {

  /**
   * @return a request object
   */
  RecordListRequest<ExpenseRecord> findAllExpenses();

  /**
   * @return a request object
   */
  RecordListRequest<ExpenseRecord> findExpense(
      PropertyReference<String> id);

  /**
   * @return a request object
   */
  RecordListRequest<ExpenseRecord> findExpensesByReport(
      PropertyReference<String> reportId);

  // TODO: persist() and remove() methods are hacks for now.
  /**
   * @return a request object
   */
  @Instance
  RequestObject<Void> persist();

 /**
  * @return a request object
  */
  @Instance
  RequestObject<Void> remove();

}
