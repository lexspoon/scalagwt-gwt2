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
package com.google.gwt.uibinder.rebind.messages;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.uibinder.rebind.IndentedWriter;
import com.google.gwt.uibinder.rebind.UiBinderWriter;
import com.google.gwt.uibinder.rebind.XMLElement;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TemplateWriter for messages.
 */
public class MessagesWriter {

  public static final String ATTRIBUTE = "attribute";

  private static final String NAME = "name";
  private static final String[] EMPTY_ARRAY = {};

  private final String messagesNamespaceURI;
  private final String packageName;
  private final String messagesClassName;
  private final TreeLogger logger;
  private final List<MessageWriter> messages = new ArrayList<MessageWriter>();
  private final String generatedFrom;

  private String defaultLocale;
  private String messagesPrefix;
  private String generateKeys;
  private GenerateAnnotationWriter generate;

  private Map<XMLElement, Collection<AttributeMessage>> elemToAttributeMessages =
      new HashMap<XMLElement, Collection<AttributeMessage>>();

  public MessagesWriter(String nameSpaceUri, TreeLogger logger, String generatedFrom,
      String packageName, String uiBinderImplClassName) {
    this.messagesNamespaceURI = nameSpaceUri;
    this.generatedFrom = generatedFrom;
    this.packageName = packageName;

    // Localizable classes cannot have underscores in their names.
    this.messagesClassName = uiBinderImplClassName.replaceAll("_", "") + "GenMessages";

    this.logger = logger;
  }

  /**
   * Call {@link #consumeAttributeMessages}, but instead of returning the
   * results store them for retrieval by a later call to
   * {@link #retrieveMessageAttributesFor}.
   *
   * @throws UnableToCompleteException on error
   */
  public void consumeAndStoreMessageAttributesFor(XMLElement elem)
      throws UnableToCompleteException {
    Collection<AttributeMessage> attributeMessages =
        consumeAttributeMessages(elem);
    if (!attributeMessages.isEmpty()) {
      elemToAttributeMessages.put(elem, attributeMessages);
    }
  }

  /**
   * Examine the children of the given element. Consume those tagged m:attribute
   * and return a set of {@link AttributeMessage} instances. E.g.:
   * <p>
   *
   * <pre>
   * &lt;img src="blueSky.jpg" alt="A blue sky">
   *   &lt;ui:attribute name="alt" description="blue sky image alt text"/>
   * &lt;/img>
   * </pre>
   *
   * <p>
   */
  public Collection<AttributeMessage> consumeAttributeMessages(XMLElement elem)
      throws UnableToCompleteException {
    Collection<XMLElement> messageChildren = getAttributeMessageChildren(elem);
    if (messageChildren.isEmpty()) {
      return Collections.emptySet();
    }

    Set<AttributeMessage> attributeMessages = new HashSet<AttributeMessage>();
    for (XMLElement child : messageChildren) {
      String attributeName = consumeMessageElementAttribute(NAME, child);
      if (attributeName.length() == 0) {
        die(String.format("Missing name attribute in %s", child));
      }
      if (!elem.hasAttribute(attributeName)) {
        die(String.format("%s has no attribute matching %s", elem, child));
      }

      String defaultMessage =
          MessageWriter.escapeMessageFormat(elem.consumeAttribute(attributeName));
      defaultMessage =
          UiBinderWriter.escapeTextForJavaStringLiteral(defaultMessage);
      attributeMessages.add(new AttributeMessage(attributeName, declareMessage(
          child, defaultMessage)));
    }
    return attributeMessages;
  }

  /**
   * Consume an m:blah attribute on a non-message element, e.g.
   * {@code <span m:ph="fnord"/>}
   */
  public String consumeMessageAttribute(String attName, XMLElement elem) {
    String fullAttName = getMessagesPrefix() + ":" + attName;
    return elem.consumeAttribute(fullAttName);
  }

  /**
   * Declares a message created by a previous call to {@link #newMessage}, and
   * returns its invocation expression to be stitched into an innerHTML block.
   */
  public String declareMessage(MessageWriter newMessage) {
    messages.add(newMessage);
    return String.format("messages.%s", newMessage.getInvocation());
  }

  /**
   * Expected to be called with the root element, to allow configuration from
   * various messages related attributes.
   */
  public void findMessagesConfig(XMLElement elem) {
    String prefix = elem.lookupPrefix(getMessagesUri());
    if (prefix != null) {
      messagesPrefix = prefix;
      defaultLocale = consumeMessageAttribute("defaultLocale", elem);
      generateKeys = consumeMessageAttribute("generateKeys", elem);
      generate =
          new GenerateAnnotationWriter(getMessageAttributeStringArray(
              "generateFormat", elem), consumeMessageAttribute(
              "generateFilename", elem), getMessageAttributeStringArray(
              "generateLocales", elem));
    }
  }

  /**
   * @return the expression that will instantiate the Messages interface
   */
  public String getDeclaration() {
    return String.format(
        "static %1$s messages = (%1$s) GWT.create(%1$s.class);",
        getMessagesClassName());
  }

  public String getMessagesClassName() {
    return messagesClassName;
  }

  /**
   * @return The namespace prefix (not including :) declared by the template for
   *         message elements and attributes
   */
  public String getMessagesPrefix() {
    return messagesPrefix;
  }

  /**
   * Confirm existence of an m:blah attribute on a non-message element, e.g.
   * {@code <span m:ph="fnord"/>}
   */
  public boolean hasMessageAttribute(String attName, XMLElement elem) {
    String fullAttName = getMessagesPrefix() + ":" + attName;
    return elem.hasAttribute(fullAttName);
  }

  /**
   * @return true iff any messages have been declared
   */
  public boolean hasMessages() {
    return !messages.isEmpty();
  }

  public boolean isMessage(XMLElement elem) {
    return isMessagePrefixed(elem) && "msg".equals(elem.getLocalName());
  }

  /**
   * Creates a new MessageWriter instance with description, key and meaning
   * values consumed from the given XMLElement. Note that this message will not
   * be written in the generated code unless it is later declared via
   * {@link #declareMessage(MessageWriter)}
   */
  public MessageWriter newMessage(XMLElement elem) {
    MessageWriter newMessage =
        new MessageWriter(consumeMessageElementAttribute("description", elem),
            consumeMessageElementAttribute("key", elem),
            consumeMessageElementAttribute("meaning", elem),
            nextMessageName());
    return newMessage;
  }

  /**
   * @return The set of AttributeMessages that were found in elem and stored by
   *         a previous call to {@link #consumeAndStoreMessageAttributesFor}
   */
  public Collection<AttributeMessage> retrieveMessageAttributesFor(
      XMLElement elem) {
    return elemToAttributeMessages.get(elem);
  }

  public void write(PrintWriter printWriter) {
    IndentedWriter writer = new IndentedWriter(printWriter);

    // Package declaration.
    if (packageName.length() > 0) {
      writer.write("package %1$s;", packageName);
      writer.newline();
    }

    // Imports.
    writer.write("import com.google.gwt.i18n.client.Messages;");
    writer.write("import static com.google.gwt.i18n.client.LocalizableResource.*;");
    writer.newline();

    // Open interface.
    genInterfaceAnnotations(writer);
    writer.write("public interface %s extends Messages {", getMessagesClassName());
    writer.newline();
    writer.indent();

    // Write message methods
    for (MessageWriter m : messages) {
      m.writeDeclaration(writer);
    }

    // Close interface.
    writer.outdent();
    writer.write("}");
  }

  /**
   * Consume an attribute on a messages related element (as oppposed to a
   * messages attribute in some other kind of element), e.g. the description in
   * {@code <m:msg description="described!">}
   */
  String consumeMessageElementAttribute(String attName, XMLElement elem) {
    if (elem.hasAttribute(attName)) {
      return UiBinderWriter.escapeTextForJavaStringLiteral(elem.consumeAttribute(attName));
    }

    String fullAttName = getMessagesPrefix() + ":" + attName;
    if (elem.hasAttribute(fullAttName)) {
      String value = elem.consumeAttribute(fullAttName);
      logger.log(TreeLogger.WARN, String.format(
          "In %s, deprecated prefix \"%s:\" on \"%s\". Use \"%s\" instead.",
          elem, getMessagesPrefix(), fullAttName, attName));
      return value;
    }

    return "";
  }

  String consumeRequiredMessageElementAttribute(String attName,
      XMLElement elem) throws UnableToCompleteException {
    String value = consumeMessageElementAttribute(attName, elem);
    if ("".equals(value)) {
      die("%s does not have required attribute %s", elem, attName);
    }
    return value;
  }

  boolean isMessagePrefixed(XMLElement elem) {
    String uri = elem.getNamespaceUri();
    return uri != null && uri.startsWith(getMessagesUri());
  }

  private String declareMessage(XMLElement elem, String defaultMessage)
      throws UnableToCompleteException {
    List<PlaceholderWriter> emptyList = Collections.emptyList();
    MessageWriter newMessage = newMessage(elem);
    newMessage.setDefaultMessage(defaultMessage);
    for (PlaceholderWriter placeholder : emptyList) {
      newMessage.addPlaceholder(placeholder);
    }
    return declareMessage(newMessage);
  }

  private void die(String message) throws UnableToCompleteException {
    // TODO(rjrjr) copied from TemplateWriter. Move to common superclass or
    // something
    logger.log(TreeLogger.ERROR, message);
    throw new UnableToCompleteException();
  }

  /**
   * Post an error message and halt processing. This method always throws an
   * {@link UnableToCompleteException}
   */
  private void die(String message, Object... params)
      throws UnableToCompleteException {
    // TODO(rjrjr) copied from TemplateWriter. Move to common superclass or
    // something
    logger.log(TreeLogger.ERROR, String.format(message, params));
    throw new UnableToCompleteException();
  }

  private void genInterfaceAnnotations(IndentedWriter pw) {
    pw.write("@GeneratedFrom(\"%s\")", generatedFrom);
    if (defaultLocale.length() > 0) {
      pw.write("@DefaultLocale(\"%s\")", defaultLocale);
    }
    if (generateKeys.length() > 0) {
      pw.write("@GenerateKeys(\"%s\")", generateKeys);
    }
    generate.write(pw);
  }

  private Collection<XMLElement> getAttributeMessageChildren(
      final XMLElement elem) throws UnableToCompleteException {
    return elem.consumeChildElements(new XMLElement.Interpreter<Boolean>() {
      public Boolean interpretElement(XMLElement child)
          throws UnableToCompleteException {
        if (isAttributeMessage(child)) {
          if (child.hasChildNodes()) {
            die(String.format("Illegal body for %s in %s.", child, elem));
          }
          return true;
        }

        return false;
      }
    });
  }

  private String[] getMessageAttributeStringArray(String attName,
      XMLElement elem) {
    String value = consumeMessageAttribute(attName, elem);
    if (value == null) {
      return EMPTY_ARRAY;
    }
    return value.split("\\s*,\\s*");
  }

  private String getMessagesUri() {
    return messagesNamespaceURI;
  }

  private boolean isAttributeMessage(XMLElement elem) {
    return isMessagePrefixed(elem) && ATTRIBUTE.equals(elem.getLocalName());
  }

  private String nextMessageName() {
    return String.format("message%d", messages.size() + 1);
  }
}
