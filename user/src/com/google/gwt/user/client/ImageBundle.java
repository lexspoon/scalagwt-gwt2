/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.user.client;

/**
 * A tag interface that is used in the generation of image bundles. An image
 * bundle is a composition of multiple images into a single large image, along
 * with an interface for accessing a specific image's
 * {@link com.google.gwt.user.client.ui.AbstractImagePrototype prototype} from
 * within the composition. Obtain an image bundle instance by calling
 * <code>GWT.create(<i>T</i>)</code>, where <code>T</code> is an
 * interface that directly or indirectly extends <code>ImageBundle</code>.
 * 
 * <p>
 * To create and use an image bundle, extend the <code>ImageBundle</code>
 * interface, and add a method declaration for each image that is to be part of
 * the bundle. Each method must take no parameters and must have a return type
 * of
 * {@link com.google.gwt.user.client.ui.AbstractImagePrototype AbstractImagePrototype}.
 * The image name can optionally be specified using the
 * <code>gwt.resource</code> metadata tag. Valid image name extensions are
 * <code>png</code>, <code>gif</code>, or <code>jpg</code>. If the
 * image name contains '/' characters, it is assumed to be the name of a
 * resource on the classpath, formatted as would be expected by
 * <code>ClassLoader.getResource(String)</code>. Otherwise, the image must be
 * located in the same package as the user-defined image bundle.
 * </p>
 * 
 * <p>
 * The easiest way to create image bundle is to omit the
 * <code>gwt.resource</code> metadata tag, and name the method the same as the
 * image name, excluding the extension. When the image name is inferred in this
 * manner, the image name's extension is assumed to be either <code>png</code>,
 * <code>gif</code>, or <code>jpg</code>, and the image location must be
 * in the same package as the user-defined image bundle. In the event that there
 * are multiple image files that have the same name with different extensions,
 * the order of extension precedence is <code>png</code>, <code>gif</code>,
 * <code>jpg</code>.
 * 
 * <h3>Example</h3>
 * 
 * <pre class="code">
 * public interface MyImageBundle extends ImageBundle {
 *
 *   /**
 *    * Notice that the gwt.resource metadata tag is not present, 
 *    * so the method name itself is assumed to match the associated 
 *    * image filename.
 *    *
 *    * One of btn_submit_icon.png, btn_submit_icon.gif, or 
 *    * btn_submit_icon.jpg must be located in the same package 
 *    * as MyImageBundle.
 *    *&#47; 
 *   public AbstractImagePrototype btn_submit_icon();
 *
 *   // No doc comment is required if you want the default 
 *   // name-matching behavior.
 *   public AbstractImagePrototype cancelButtonIcon();
 * }
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * An image bundle that uses the <code>gwt.resource</code> metadata tag to
 * specify image names might look something like this:
 * 
 * <pre class="code">
 * public interface MyImageBundle extends ImageBundle {
 *
 *   /**
 *    * The metadata tag contains no '/' characters, so 
 *    * btn_submit_icon.gif must be located in the same 
 *    * package as MyImageBundle.
 *    *
 *    * @gwt.resource btn_submit_icon.gif
 *    *&#47;
 *   public AbstractImagePrototype submitButtonIcon();
 *
 *   /**
 *    * btn_cancel_icon.png must be located in the package 
 *    * com.mycompany.myapp.icons (which must be on the classpath).
 *    *
 *    * @gwt.resource com/mycompany/myapp/icons/btn_cancel_icon.png
 *    *&#47;
 *   public AbstractImagePrototype cancelButtonIcon();
 * }
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Here is how MyImageBundle might be used in an application:
 * 
 * <pre class="code">
 *  ...
 *
 *  // Create a new instance of MyImageBundle using GWT.create.
 *  // This only needs to be done once - a reference to myImageBundle can
 *  // be kept for use by other parts of the application.
 *  MyImageBundle myImageBundle = (MyImageBundle) GWT.create(MyImageBundle.class);
 *
 *  // Retrieve the image prototypes from myImageBundle.
 *  AbstractImagePrototype submitButtonImgPrototype = myImageBundle.btn_submit_icon();
 *  AbstractImagePrototype cancelButtonImgPrototype = myImageBundle.cancelButtonIcon();
 *
 *  // Add the images that are created based on the prototypes to the panel.
 *  panel.add(submitButtonImgPrototype.createImage());
 *  panel.add(cancelButtonImgPrototype.createImage());
 *
 * ...
 * </pre>
 * 
 * </p>
 * 
 * <h3>For More Information</h3>
 * See the GWT Developer Guide for an introduction to image bundles.
 * @see com.google.gwt.user.client.ui.AbstractImagePrototype
 * @see com.google.gwt.user.client.ui.Image#Image(String, int, int, int, int)
 * @see com.google.gwt.user.client.ui.Image#setVisibleRect(int, int, int, int)
 * @see com.google.gwt.user.client.ui.Image#setUrlAndVisibleRect(String, int,
 *      int, int, int)
 */
public interface ImageBundle {
}
