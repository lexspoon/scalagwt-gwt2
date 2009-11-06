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
package com.google.gwt.uibinder.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.impl.JavaResourceBase;
import com.google.gwt.dev.javac.impl.MockResourceOracle;
import com.google.gwt.uibinder.parsers.NullInterpreter;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests XMLElement.
 */
public class XMLElementTest extends TestCase {
  private static final String STRING_WITH_DOUBLEQUOTE = "I have a \" quote in me";
  private static final W3cDomHelper docHelper = new W3cDomHelper(TreeLogger.NULL);
  private Document doc;
  private Element item;
  private XMLElement elm;
  private XMLElementProvider elemProvider;
  private TypeOracle oracle;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    MockResourceOracle resourceOracle = new MockResourceOracle(
        JavaResourceBase.getStandardResources());
    CompilationState state = new CompilationState(TreeLogger.NULL,
        resourceOracle);
    oracle = state.getTypeOracle();

    elemProvider = new XMLElementProviderImpl(new AttributeParsers(), null,
        oracle, MortalLogger.NULL);

    init("<doc><elm attr1=\"attr1Value\" attr2=\"attr2Value\"/></doc>");
  }

  public void testConsumeBoolean() throws SAXException, IOException,
      UnableToCompleteException {
    init("<doc><elm yes='true' no='false' "
        + "fnord='fnord' ref='{foo.bar.baz}'/></doc>");

    assertEquals("", elm.consumeBooleanAttribute("foo"));

    assertEquals("true", elm.consumeBooleanAttribute("yes"));
    assertEquals("", elm.consumeBooleanAttribute("yes"));

    assertEquals("false", elm.consumeBooleanAttribute("no"));
    assertEquals("", elm.consumeBooleanAttribute("no"));

    assertEquals("foo.bar().baz()", elm.consumeBooleanAttribute("ref"));

    try {
      elm.consumeBooleanAttribute("fnord");
      fail("Should throw UnableToCompleteException on misparse");
    } catch (UnableToCompleteException c) {
      /* pass */
    }
  }

  public void testConsumeBooleanConstant() throws SAXException, IOException,
      UnableToCompleteException {
    init("<doc><elm yes='true' no='false' "
        + "fnord='fnord' ref='{foo.bar.baz}'/></doc>");

    assertNull(elm.consumeBooleanConstantAttribute("foo"));

    assertTrue(elm.consumeBooleanConstantAttribute("yes"));
    assertNull(elm.consumeBooleanConstantAttribute("yes"));

    assertFalse(elm.consumeBooleanConstantAttribute("no"));
    assertNull(elm.consumeBooleanConstantAttribute("no"));

    try {
      elm.consumeBooleanConstantAttribute("ref");
      fail("Should throw UnableToCompleteException on field ref");
    } catch (UnableToCompleteException c) {
      /* pass */
    }

    try {
      elm.consumeBooleanConstantAttribute("fnord");
      fail("Should throw UnableToCompleteException on misparse");
    } catch (UnableToCompleteException c) {
      /* pass */
    }
  }

  public void testConsumeDouble() throws UnableToCompleteException,
      SAXException, IOException {
    init("<doc><elm minus='-123.45' plus='123.45' minus-one='-1' "
        + "plus-one='1' fnord='fnord' ref='{foo.bar.baz}'/></doc>");
    assertEquals("1", elm.consumeDoubleAttribute("plus-one"));
    assertEquals("-1", elm.consumeDoubleAttribute("minus-one"));
    assertEquals("123.45", elm.consumeDoubleAttribute("plus"));
    assertEquals("-123.45", elm.consumeDoubleAttribute("minus"));
    assertEquals("foo.bar().baz()", elm.consumeBooleanAttribute("ref"));

    try {
      elm.consumeBooleanAttribute("fnord");
      fail("Should throw UnableToCompleteException on misparse");
    } catch (UnableToCompleteException c) {
      /* pass */
    }
  }

  public void testConsumeInnerTextEscapedAsHtmlStringLiteral()
      throws UnableToCompleteException {
    appendText(STRING_WITH_DOUBLEQUOTE);
    assertEquals(
        UiBinderWriter.escapeTextForJavaStringLiteral(STRING_WITH_DOUBLEQUOTE),
        elm.consumeInnerTextEscapedAsHtmlStringLiteral(new NullInterpreter<String>()));
  }

  public void testConsumeInnerTextEscapedAsHtmlStringLiteralEmpty()
      throws UnableToCompleteException {
    assertEquals(
        "",
        elm.consumeInnerTextEscapedAsHtmlStringLiteral(new NullInterpreter<String>()));
  }

  public void testConsumeRawAttribute() {
    assertEquals("attr1Value", elm.consumeRawAttribute("attr1"));
    assertEquals("", elm.consumeRawAttribute("attr1"));
  }

  public void testConsumeRawAttributeWithDefault() {
    assertEquals("attr1Value", elm.consumeRawAttribute("attr1", "default"));
    assertEquals("default", elm.consumeRawAttribute("attr1", "default"));
    assertEquals("otherDefault", elm.consumeRawAttribute("unsetthing",
        "otherDefault"));
  }

  public void testConsumeRequired() throws UnableToCompleteException {
    assertEquals("attr1Value", elm.consumeRequiredRawAttribute("attr1"));
    try {
      elm.consumeRequiredRawAttribute("unsetthing");
      fail("Should have thrown UnableToCompleteException");
    } catch (UnableToCompleteException e) {
      /* pass */
    }
  }

  public void testConsumeSingleChildElementEmpty() throws SAXException,
      IOException, UnableToCompleteException {
    try {
      elm.consumeSingleChildElement();
      fail("Should throw on single child element");
    } catch (UnableToCompleteException e) {
      /* pass */
    }

    init("<doc><elm><child>Hi.</child></elm></doc>");
    assertEquals("Hi.",
        elm.consumeSingleChildElement().consumeUnescapedInnerText());

    init("<doc><elm id='elm'><child>Hi.</child><child>Ho.</child></elm></doc>");
    try {
      elm.consumeSingleChildElement();
      fail("Should throw on too many children");
    } catch (UnableToCompleteException e) {
      /* pass */
    }
  }

  public void testConsumeUnescapedInnerText() throws UnableToCompleteException {
    appendText(STRING_WITH_DOUBLEQUOTE);
    assertEquals(STRING_WITH_DOUBLEQUOTE, elm.consumeUnescapedInnerText());
  }

  public void testConsumeUnescapedInnerTextEmpty()
      throws UnableToCompleteException {
    assertEquals("", elm.consumeUnescapedInnerText());
  }

  public void testEmptyStringOnMissingAttribute() {
    assertEquals("", elm.consumeRawAttribute("fnord"));
  }

  public void testIterator() {
    String[] expecteds = {"attr1", "attr2"};
    Set<String> seen = new HashSet<String>();
    for (int i = elm.getAttributeCount() - 1; i >= 0; i--) {
      XMLAttribute attr = elm.getAttribute(i);
      String expected = expecteds[i];
      assertEquals(expected, attr.getLocalName());
      assertFalse(attr.isConsumed());
      assertEquals(expected + "Value", attr.consumeRawValue());
      assertTrue(attr.isConsumed());
      seen.add(expected);
    }
    assertEquals(2, seen.size());
  }

  public void testNoEndTags() throws Exception {
    doc = docHelper.documentFor("<doc><br/></doc>");
    Element documentElement = doc.getDocumentElement();
    Element item = (Element) documentElement.getElementsByTagName("br").item(0);
    XMLElement elm = elemProvider.get(item);
    assertEquals("br", item.getTagName());
    assertEquals("", elm.getClosingTag());
  }

  private void appendText(final String text) {
    Text t = doc.createTextNode(text);
    item.appendChild(t);
  }

  private void init(final String domString) throws SAXException, IOException {
    doc = docHelper.documentFor(domString);
    item = (Element) doc.getDocumentElement().getElementsByTagName("elm").item(
        0);
    elm = elemProvider.get(item);
  }
}
