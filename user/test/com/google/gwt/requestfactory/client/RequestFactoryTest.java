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
package com.google.gwt.requestfactory.client;

import com.google.gwt.requestfactory.client.impl.ProxyImpl;
import com.google.gwt.requestfactory.shared.Receiver;
import com.google.gwt.requestfactory.shared.RequestObject;
import com.google.gwt.requestfactory.shared.ServerFailure;
import com.google.gwt.requestfactory.shared.SimpleBarProxy;
import com.google.gwt.requestfactory.shared.SimpleFooProxy;
import com.google.gwt.requestfactory.shared.Violation;

import java.util.Set;

/**
 * Tests for {@link com.google.gwt.requestfactory.shared.RequestFactory}.
 */
public class RequestFactoryTest extends RequestFactoryTestBase {
  /*
   * DO NOT USE finishTest(). Instead, call finishTestAndReset();
   */

  private class FailFixAndRefire<T> extends Receiver<T> {

    private final SimpleFooProxy proxy;
    private final RequestObject<T> request;
    private boolean voidReturnExpected;

    FailFixAndRefire(SimpleFooProxy proxy,
        RequestObject<T> request) {
      this.proxy = request.edit(proxy);
      this.request = request;
    }

    @Override
    public void onSuccess(T response) {
      /*
       * Make sure your class path includes:
       * 
       * tools/lib/apache/log4j/log4j-1.2.16.jar
       * tools/lib/hibernate/validator/hibernate-validator-4.1.0.Final.jar
       * tools/lib/slf4j/slf4j-api/slf4j-api-1.6.1.jar
       * tools/lib/slf4j/slf4j-log4j12/slf4j-log4j12-1.6.1.jar
       */
      fail("Violations expected (you might be missing some jars, "
          + "see the comment above this line)");
    }

    @Override
    public void onViolation(Set<Violation> errors) {
      
      // size violation expected
      
      assertEquals(1, errors.size());
      Violation error = errors.iterator().next();
      assertEquals("userName", error.getPath());
      assertEquals("size must be between 3 and 30", error.getMessage());
      assertEquals(proxy.stableId(), error.getProxyId());

      // Now re-used the request to fix the edit 
      
      proxy.setUserName("long enough");
      request.fire(new Receiver<T>() {
        @Override
        public void onSuccess(T response) {
          if (voidReturnExpected) {
            assertNull(response);
          } else {
            assertEquals(proxy.stableId(),
                ((SimpleFooProxy) response).stableId());
          }
          finishTestAndReset();
        }
      });
    }

    void doVoidTest() {
      voidReturnExpected = true;
      doTest();
    }
    
    void doTest() {
      proxy.setUserName("a"); // too short
      request.fire(this);
    }
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.requestfactory.RequestFactorySuite";
  }

  public void testDummyCreate() {
    delayTestFinish(5000);

    final SimpleFooProxy foo = req.create(SimpleFooProxy.class);
    Object futureId = foo.getId();
    assertEquals(futureId, foo.getId());
    assertTrue(((ProxyImpl) foo).isFuture());
    RequestObject<SimpleFooProxy> fooReq = req.simpleFooRequest().persistAndReturnSelf(
        foo);
    fooReq.fire(new Receiver<SimpleFooProxy>() {

      @Override
      public void onSuccess(final SimpleFooProxy returned) {
        Object futureId = foo.getId();
        assertEquals(futureId, foo.getId());
        assertTrue(((ProxyImpl) foo).isFuture());

        checkStableIdEquals(foo, returned);
        finishTestAndReset();
      }
    });
  }

  public void testFetchEntity() {
    delayTestFinish(5000);
    req.simpleFooRequest().findSimpleFooById("999L").fire(
        new Receiver<SimpleFooProxy>() {
          @Override
          public void onSuccess(SimpleFooProxy response) {
            assertEquals(42, (int) response.getIntId());
            assertEquals("GWT", response.getUserName());
            assertEquals(8L, (long) response.getLongField());
            assertEquals(com.google.gwt.requestfactory.shared.SimpleEnum.FOO,
                response.getEnumField());
            assertEquals(null, response.getBarField());
            finishTestAndReset();
          }
        });
  }

  public void testFetchEntityWithRelation() {
    delayTestFinish(5000);
    req.simpleFooRequest().findSimpleFooById("999L").with("barField").fire(
        new Receiver<SimpleFooProxy>() {
          @Override
          public void onSuccess(SimpleFooProxy response) {
            assertEquals(42, (int) response.getIntId());
            assertEquals("GWT", response.getUserName());
            assertEquals(8L, (long) response.getLongField());
            assertEquals(com.google.gwt.requestfactory.shared.SimpleEnum.FOO,
                response.getEnumField());
            assertNotNull(response.getBarField());
            finishTestAndReset();
          }
        });
  }

  public void testGetEventBus() {
    assertEquals(eventBus, req.getEventBus());
  }

  /*
   * tests that (a) any method can have a side effect that is handled correctly.
   * (b) instance methods are handled correctly and (c) a request cannot
   * be reused after a successful response is received. (Yet?)
   */
  public void testMethodWithSideEffects() {
    delayTestFinish(5000);

    req.simpleFooRequest().findSimpleFooById("999L").fire(
        new Receiver<SimpleFooProxy>() {

          @Override
          public void onSuccess(SimpleFooProxy newFoo) {
            final RequestObject<Long> mutateRequest = req.simpleFooRequest().countSimpleFooWithUserNameSideEffect(
                newFoo);
            newFoo = mutateRequest.edit(newFoo);
            newFoo.setUserName("Ray");
            mutateRequest.fire(new Receiver<Long>() {
              @Override
              public void onSuccess(Long response) {
                assertCannotFire(mutateRequest);
                assertEquals(new Long(1L), response);
                // TODO: listen to create events also

                // confirm that the instance method did have the desired
                // sideEffect.
                req.simpleFooRequest().findSimpleFooById("999L").fire(
                    new Receiver<SimpleFooProxy>() {
                      @Override
                      public void onSuccess(SimpleFooProxy finalFoo) {
                        assertEquals("Ray", finalFoo.getUserName());
                        finishTestAndReset();
                      }
                    });
              }

            });

            try {
              newFoo.setUserName("Barney");
              fail();
            } catch (IllegalStateException e) {
              /* pass, cannot change a request that is in flight */
            }
          }
        });
  }

  /*
   * TODO: all these tests should check the final values. It will be easy when
   * we have better persistence than the singleton pattern.
   */
  public void testPersistExistingEntityExistingRelation() {
    delayTestFinish(5000);

    req.simpleBarRequest().findSimpleBarById("999L").fire(
        new Receiver<SimpleBarProxy>() {
          @Override
          public void onSuccess(final SimpleBarProxy barProxy) {
            req.simpleFooRequest().findSimpleFooById("999L").fire(
                new Receiver<SimpleFooProxy>() {
                  @Override
                  public void onSuccess(SimpleFooProxy fooProxy) {
                    RequestObject<Void> updReq = req.simpleFooRequest().persist(
                        fooProxy);
                    fooProxy = updReq.edit(fooProxy);
                    fooProxy.setBarField(barProxy);
                    updReq.fire(new Receiver<Void>() {
                      @Override
                      public void onSuccess(Void response) {

                        finishTestAndReset();
                      }
                    });
                  }
                });
          }
        });
  }

  /*
   * Find Entity Create Entity2 Relate Entity2 to Entity Persist Entity
   */
  public void testPersistExistingEntityNewRelation() {
    delayTestFinish(5000);

    // Make a new bar
    SimpleBarProxy makeABar = req.create(SimpleBarProxy.class);
    RequestObject<SimpleBarProxy> persistRequest = req.simpleBarRequest().persistAndReturnSelf(
        makeABar);
    makeABar = persistRequest.edit(makeABar);
    makeABar.setUserName("Amit");

    persistRequest.fire(new Receiver<SimpleBarProxy>() {
      @Override
      public void onSuccess(final SimpleBarProxy persistedBar) {

        // It was made, now find a foo to assign it to
        req.simpleFooRequest().findSimpleFooById("999L").fire(
            new Receiver<SimpleFooProxy>() {
              @Override
              public void onSuccess(SimpleFooProxy response) {

                // Found the foo, edit it
                RequestObject<Void> fooReq = req.simpleFooRequest().persist(
                    response);
                response = fooReq.edit(response);
                response.setBarField(persistedBar);
                fooReq.fire(new Receiver<Void>() {
                  @Override
                  public void onSuccess(Void response) {

                    // Foo was persisted, fetch it again check the goods
                    req.simpleFooRequest().findSimpleFooById("999L").with(
                        "barField.userName").fire(
                        new Receiver<SimpleFooProxy>() {

                          // Here it is
                          @Override
                          public void onSuccess(SimpleFooProxy finalFooProxy) {
                            assertEquals("Amit",
                                finalFooProxy.getBarField().getUserName());
                            finishTestAndReset();
                          }
                        });
                  }
                });
              }
            });
      }
    });
  }

  /*
   * Find Entity2 Create Entity, Persist Entity Relate Entity2 to Entity Persist
   * Entity
   */
  public void testPersistNewEntityExistingRelation() {
    delayTestFinish(5000);
    SimpleFooProxy newFoo = req.create(SimpleFooProxy.class);

    final RequestObject<Void> fooReq = req.simpleFooRequest().persist(newFoo);

    newFoo = fooReq.edit(newFoo);
    newFoo.setUserName("Ray");

    final SimpleFooProxy finalFoo = newFoo;
    req.simpleBarRequest().findSimpleBarById("999L").fire(
        new Receiver<SimpleBarProxy>() {
          @Override
          public void onSuccess(SimpleBarProxy response) {
            finalFoo.setBarField(response);
            fooReq.fire(new Receiver<Void>() {
              @Override
              public void onSuccess(Void response) {
                req.simpleFooRequest().findSimpleFooById("999L").fire(
                    new Receiver<SimpleFooProxy>() {
                      @Override
                      public void onSuccess(SimpleFooProxy finalFooProxy) {
                        // newFoo hasn't been persisted, so userName is the old
                        // value.
                        assertEquals("GWT", finalFooProxy.getUserName());
                        finishTestAndReset();
                      }
                    });
              }
            });
          }
        });
  }

  /*
   * Create Entity, Persist Entity Create Entity2, Perist Entity2 relate Entity2
   * to Entity Persist
   */
  public void testPersistNewEntityNewRelation() {
    delayTestFinish(5000);
    SimpleFooProxy newFoo = req.create(SimpleFooProxy.class);
    SimpleBarProxy newBar = req.create(SimpleBarProxy.class);

    final RequestObject<SimpleFooProxy> fooReq = req.simpleFooRequest().persistAndReturnSelf(
        newFoo);

    newFoo = fooReq.edit(newFoo);
    newFoo.setUserName("Ray");

    final RequestObject<SimpleBarProxy> barReq = req.simpleBarRequest().persistAndReturnSelf(
        newBar);
    newBar = barReq.edit(newBar);
    newBar.setUserName("Amit");

    fooReq.fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onSuccess(final SimpleFooProxy persistedFoo) {
        barReq.fire(new Receiver<SimpleBarProxy>() {
          @Override
          public void onSuccess(final SimpleBarProxy persistedBar) {
            assertEquals("Ray", persistedFoo.getUserName());
            final RequestObject<Void> fooReq2 = req.simpleFooRequest().persist(
                persistedFoo);
            SimpleFooProxy editablePersistedFoo = fooReq2.edit(persistedFoo);
            editablePersistedFoo.setBarField(persistedBar);
            fooReq2.fire(new Receiver<Void>() {
              @Override
              public void onSuccess(Void response) {
                req.simpleFooRequest().findSimpleFooById("999L").with(
                    "barField.userName").fire(new Receiver<SimpleFooProxy>() {
                  @Override
                  public void onSuccess(SimpleFooProxy finalFooProxy) {
                    assertEquals("Amit",
                        finalFooProxy.getBarField().getUserName());
                    finishTestAndReset();
                  }
                });
              }
            });
          }
        });
      }
    });
  }

  public void testPersistRecursiveRelation() {
    delayTestFinish(5000);

    SimpleFooProxy rayFoo = req.create(SimpleFooProxy.class);
    final RequestObject<SimpleFooProxy> persistRay = req.simpleFooRequest().persistAndReturnSelf(
        rayFoo);
    rayFoo = persistRay.edit(rayFoo);
    rayFoo.setUserName("Ray");
    rayFoo.setFooField(rayFoo);
    persistRay.fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onSuccess(final SimpleFooProxy persistedRay) {
        finishTestAndReset();
      }
    });
  }

  public void testPersistRelation() {
    delayTestFinish(5000);

    SimpleFooProxy rayFoo = req.create(SimpleFooProxy.class);
    final RequestObject<SimpleFooProxy> persistRay = req.simpleFooRequest().persistAndReturnSelf(
        rayFoo);
    rayFoo = persistRay.edit(rayFoo);
    rayFoo.setUserName("Ray");

    persistRay.fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onSuccess(final SimpleFooProxy persistedRay) {
        SimpleBarProxy amitBar = req.create(SimpleBarProxy.class);
        final RequestObject<SimpleBarProxy> persistAmit = req.simpleBarRequest().persistAndReturnSelf(
            amitBar);
        amitBar = persistAmit.edit(amitBar);
        amitBar.setUserName("Amit");

        persistAmit.fire(new Receiver<SimpleBarProxy>() {
          @Override
          public void onSuccess(SimpleBarProxy persistedAmit) {

            final RequestObject<SimpleFooProxy> persistRelationship = req.simpleFooRequest().persistAndReturnSelf(
                persistedRay).with("barField");
            SimpleFooProxy newRec = persistRelationship.edit(persistedRay);
            newRec.setBarField(persistedAmit);

            persistRelationship.fire(new Receiver<SimpleFooProxy>() {
              @Override
              public void onSuccess(SimpleFooProxy relatedRay) {
                assertEquals("Amit", relatedRay.getBarField().getUserName());
                finishTestAndReset();
              }
            });
          }
        });
      }
    });
  }

  public void testProxysAsInstanceMethodParams() {
    delayTestFinish(5000);
    req.simpleFooRequest().findSimpleFooById("999L").fire(
        new Receiver<SimpleFooProxy>() {
          @Override
          public void onSuccess(SimpleFooProxy response) {
            SimpleBarProxy bar = req.create(SimpleBarProxy.class);
            RequestObject<String> helloReq = req.simpleFooRequest().hello(
                response, bar);
            bar = helloReq.edit(bar);
            bar.setUserName("BAR");
            helloReq.fire(new Receiver<String>() {
              @Override
              public void onSuccess(String response) {
                assertEquals("Greetings BAR from GWT", response);
                finishTestAndReset();
              }
            });
          }
        });
  }

  public void testServerFailure() {
    delayTestFinish(5000);

    SimpleFooProxy newFoo = req.create(SimpleFooProxy.class);
    final RequestObject<SimpleFooProxy> persistRequest = 
      req.simpleFooRequest().persistAndReturnSelf(newFoo);

    final SimpleFooProxy mutableFoo = persistRequest.edit(newFoo);
    mutableFoo.setPleaseCrash(42); // 42 is the crash causing magic number

    persistRequest.fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onFailure(ServerFailure error) {
        assertEquals("Server Error: THIS EXCEPTION IS EXPECTED BY A TEST", error.getMessage());
        assertEquals("", error.getExceptionType());
        assertEquals("", error.getStackTraceString());

        // Now show that we can fix the error and try again with the same
        // request

        mutableFoo.setPleaseCrash(24); // Only 42 crashes
        persistRequest.fire(new Receiver<SimpleFooProxy>() {
          @Override
          public void onSuccess(SimpleFooProxy response) {
            finishTestAndReset();
          }
        });
      }

      public void onSuccess(SimpleFooProxy response) {
        fail("Failure expected but onSuccess() was called");
      }

      @Override
      public void onViolation(Set<Violation> errors) {
        fail("Failure expected but onViolation() was called");
      }
    });
  }

  public void testStableId() {
    delayTestFinish(5000);

    final SimpleFooProxy foo = req.create(SimpleFooProxy.class);
    final Object futureId = foo.getId();
    assertTrue(((ProxyImpl) foo).isFuture());
    RequestObject<SimpleFooProxy> fooReq = req.simpleFooRequest().persistAndReturnSelf(
        foo);

    final SimpleFooProxy newFoo = fooReq.edit(foo);
    assertEquals(futureId, foo.getId());
    assertTrue(((ProxyImpl) foo).isFuture());
    assertEquals(futureId, newFoo.getId());
    assertTrue(((ProxyImpl) newFoo).isFuture());

    newFoo.setUserName("GWT basic user");
    fooReq.fire(new Receiver<SimpleFooProxy>() {

      @Override
      public void onSuccess(final SimpleFooProxy returned) {
        assertEquals(futureId, foo.getId());
        assertTrue(((ProxyImpl) foo).isFuture());
        assertEquals(futureId, newFoo.getId());
        assertTrue(((ProxyImpl) newFoo).isFuture());

        assertFalse(((ProxyImpl) returned).isFuture());

        checkStableIdEquals(foo, returned);
        checkStableIdEquals(newFoo, returned);

        RequestObject<SimpleFooProxy> editRequest = req.simpleFooRequest().persistAndReturnSelf(
            returned);
        final SimpleFooProxy editableFoo = editRequest.edit(returned);
        editableFoo.setUserName("GWT power user");
        editRequest.fire(new Receiver<SimpleFooProxy>() {

          @Override
          public void onSuccess(SimpleFooProxy returnedAfterEdit) {
            checkStableIdEquals(editableFoo, returnedAfterEdit);
            assertEquals(returnedAfterEdit.getId(), returned.getId());
            finishTestAndReset();
          }
        });
      }
    });
  }

  public void testViolationAbsent() {
    delayTestFinish(5000);

    SimpleFooProxy newFoo = req.create(SimpleFooProxy.class);
    final RequestObject<Void> fooReq = req.simpleFooRequest().persist(newFoo);

    newFoo = fooReq.edit(newFoo);
    newFoo.setUserName("Amit"); // will not cause violation.

    fooReq.fire(new Receiver<Void>() {
      @Override
      public void onSuccess(Void ignore) {
        finishTestAndReset();
      }
    });
  }

  public void testViolationsOnCreate() {
    delayTestFinish(5000);

    SimpleFooProxy newFoo = req.create(SimpleFooProxy.class);
    final RequestObject<SimpleFooProxy> create = req.simpleFooRequest().persistAndReturnSelf(
        newFoo);
    new FailFixAndRefire<SimpleFooProxy>(newFoo, create).doTest();
  }

  public void testViolationsOnCreateVoidReturn() {
    delayTestFinish(5000);

    SimpleFooProxy newFoo = req.create(SimpleFooProxy.class);
    final RequestObject<Void> create = req.simpleFooRequest().persist(newFoo);
    new FailFixAndRefire<Void>(newFoo, create).doVoidTest();
  }

  public void testViolationsOnEdit() {
    delayTestFinish(5000);

    fooCreationRequest().fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onSuccess(SimpleFooProxy returned) {
        RequestObject<SimpleFooProxy> editRequest = req.simpleFooRequest().persistAndReturnSelf(
            returned);
        new FailFixAndRefire<SimpleFooProxy>(returned, editRequest).doTest();
      }
    });
  }

  public void testViolationsOnEditVoidReturn() {
    delayTestFinish(5000);

    fooCreationRequest().fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onSuccess(SimpleFooProxy returned) {
        RequestObject<Void> editRequest = req.simpleFooRequest().persist(
            returned);
        new FailFixAndRefire<Void>(returned, editRequest).doVoidTest();
      }
    });
  }

  private void assertCannotFire(final RequestObject<Long> mutateRequest) {
    try {
      mutateRequest.fire(new Receiver<Long>() {
        public void onSuccess(Long response) {
          fail("Should not be called");
        }
      });
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      /* cannot reuse a successful request, mores the pity */
    }
  }

  private void checkStableIdEquals(SimpleFooProxy expected,
      SimpleFooProxy actual) {
    assertNotSame(expected.stableId(), actual.stableId());
    assertEquals(expected.stableId(), actual.stableId());
    assertEquals(expected.stableId().hashCode(), actual.stableId().hashCode());

    // No assumptions about the proxy objects (being proxies and all)
    assertNotSame(expected, actual);
    assertFalse(expected.equals(actual));
  }

  private RequestObject<SimpleFooProxy> fooCreationRequest() {
    SimpleFooProxy originalFoo = req.create(SimpleFooProxy.class);
    final RequestObject<SimpleFooProxy> fooReq = req.simpleFooRequest().persistAndReturnSelf(
        originalFoo);
    originalFoo = fooReq.edit(originalFoo);
    originalFoo.setUserName("GWT User");
    return fooReq;
  }
}
