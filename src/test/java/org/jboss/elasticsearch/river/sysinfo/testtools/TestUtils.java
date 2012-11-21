/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.testtools;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

/**
 * Helper methods for Unit tests.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public abstract class TestUtils {

  /**
   * Assert passed string is same as content of given file loaded from classpath.
   * 
   * @param expectedFilePath path to file inside classpath
   * @param actual content to assert
   * @throws IOException
   */
  public static void assertStringFromClasspathFile(String expectedFilePath, String actual) throws IOException {
    Assert.assertEquals(readStringFromClasspathFile(expectedFilePath), actual);
  }

  /**
   * Read file from classpath into String. UTF-8 encoding expected.
   * 
   * @param filePath in classpath to read data from.
   * @return file content.
   * @throws IOException
   */
  public static String readStringFromClasspathFile(String filePath) throws IOException {
    StringWriter stringWriter = new StringWriter();
    IOUtils.copy(TestUtils.class.getResourceAsStream(filePath), stringWriter, "UTF-8");
    return stringWriter.toString();
  }

}
