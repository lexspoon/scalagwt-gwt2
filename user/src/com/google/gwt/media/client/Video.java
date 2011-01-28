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
package com.google.gwt.media.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * <p>
 * A widget representing a &lt;video&gt; element.
 * 
 * <p>
 * <span style="color:red">Experimental API: This API is still under development
 * and is subject to change.
 * </span>
 * </p>
 * 
 * This widget may not be supported on all browsers.
 */
public class Video extends FocusWidget {
  private static VideoElementSupportDetector detector;

  /**
   * Return a new {@link Video} if supported,  and null otherwise.
   * 
   * @return a new {@link Video} if supported, and null otherwise
   */
  public static Video createIfSupported() {
    if (detector == null) {
      detector = GWT.create(VideoElementSupportDetector.class);
    }
    if (!detector.isSupportedCompileTime()) {
      return null;
    }
    VideoElement element = Document.get().createVideoElement();
    if (!detector.isSupportedRunTime(element)) {
      return null;
    }
    return new Video(element);
  }

  /**
   * Runtime check for whether the video element is supported in this browser.
   * 
   * @return whether the video element is supported
   */
  public static boolean isSupported() {
    if (detector == null) {
      detector = GWT.create(VideoElementSupportDetector.class);
    }
    if (!detector.isSupportedCompileTime()) {
      return false;
    }
    VideoElement element = Document.get().createVideoElement();
    if (!detector.isSupportedRunTime(element)) {
      return false;
    }
    return true;
  }

  /**
   * Protected constructor. Use {@link #createIfSupported()} to create a Video.
   */
  private Video(VideoElement element) {
    setElement(element);
  }

  /**
   * Creates a Video widget.
   */
  public Video() {
    setElement(Document.get().createVideoElement());
  }
  
  /**
   * Creates a Video widget with a given source URL.
   * 
   * @param src a String URL
   */
  public Video(String src) {
    setElement(Document.get().createVideoElement());
    getVideoElement().setSrc(src);
  }

  /**
   * Returns the attached VideoElement.
   * 
   * @return the VideoElement
   */
  public VideoElement getVideoElement() {
    return this.getElement().cast();
  }

  /**
   * Detector for browser support of {@link VideoElement}.
   */
  private static class VideoElementSupportDetector {
    /**
     * Using a run-time check, return true if the {@link VideoElement} is 
     * supported.
     * 
     * @return true if supported, false otherwise.
     */
    static native boolean isSupportedRunTime(VideoElement element) /*-{
      return !!element.getContext;
    }-*/;

    /**
     * Using a compile-time check, return true if {@link VideoElement} might 
     * be supported.
     * 
     * @return true if might be supported, false otherwise.
     */
    boolean isSupportedCompileTime() {
      // will be true in VideoElementSupportDetectedMaybe
      // will be false in VideoElementSupportDetectedNo
      return false;
    }
  }

  /**
   * Detector for permutations that might support {@link VideoElement}.
   */
  @SuppressWarnings("unused")
  private static class VideoElementSupportDetectedMaybe
      extends VideoElementSupportDetector {
    /**
     * Using a compile-time check, return true if {@link VideoElement} might be
     * supported.
     *
     * @return true if might be supported, false otherwise.
     */
    @Override
    boolean isSupportedCompileTime() {
      return true;
    }
  }

  /**
   * Detector for permutations that do not support {@link VideoElement}.
   */
  @SuppressWarnings("unused")
  private static class VideoElementSupportDetectedNo
      extends VideoElementSupportDetector {
    /**
     * Using a compile-time check, return true if {@link VideoElement} might be
     * supported.
     *
     * @return true if might be supported, false otherwise.
     */
    @Override
    boolean isSupportedCompileTime() {
      return false;
    } 
  }
}
