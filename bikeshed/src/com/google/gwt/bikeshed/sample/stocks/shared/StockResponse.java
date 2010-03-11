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
package com.google.gwt.bikeshed.sample.stocks.shared;

import java.io.Serializable;

/**
 * A response to a request for stock data.
 */
public class StockResponse implements Serializable {

  /**
   * The amount of available cash in pennies.
   */
  private int cash;
  private StockQuoteList favorites;
  private int numFavorites;
  private int numSearchResults;
  private int numSector;
  private StockQuoteList searchResults;
  private StockQuoteList sector;
  private String sectorName;

  public StockResponse(StockQuoteList searchResults, StockQuoteList favorites,
      String sectorName, StockQuoteList sector, int numSearchResults, int numFavorites, int numSector, int cash) {
    this.searchResults = searchResults;
    this.favorites = favorites;
    this.sectorName = sectorName;
    this.sector = sector;
    this.numSearchResults = numSearchResults;
    this.numFavorites = numFavorites;
    this.numSector = numSector;
    this.cash = cash;
  }

  /**
   * Used for RPC.
   */
  StockResponse() {
  }

  public int getCash() {
    return cash;
  }

  public StockQuoteList getFavorites() {
    return favorites;
  }
  
  /**
   * The sum of cash available and portfolio value. 
   */
  public int getNetWorth() {
    return cash + favorites.getValue();
  }

  public int getNumFavorites() {
    return numFavorites;
  }

  public int getNumSearchResults() {
    return numSearchResults;
  }

  public int getNumSector() {
    return numSector;
  }

  public StockQuoteList getSearchResults() {
    return searchResults;
  }

  public StockQuoteList getSector() {
    return sector;
  }
  
  public String getSectorName() {
    return sectorName;
  }
}
