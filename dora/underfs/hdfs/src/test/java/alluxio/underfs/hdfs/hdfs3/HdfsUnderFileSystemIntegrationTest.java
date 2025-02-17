/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.underfs.hdfs.hdfs3;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HdfsUnderFileSystemIntegrationTest extends HdfsUnderFileSystemIntegrationTestBase {
  @Test
  public void testWriteEmptyFile() throws Exception {
    writeEmptyFileTest();
  }

  @Test
  public void testWriteMultiBlockFileTest() throws Exception {
    writeMultiBlockFileTest("/test_file");
  }

  @Test(expected = IOException.class)
  public void testException() throws Exception {
    hdfsDownDuringUploadTest();
  }

  @Test
  public void testSetAndGetXAttribute() throws Exception {
    // create empty file
    String testFilePath = "/empty_file";
    OutputStream os = mUfs.create(testFilePath, getCreateOption());
    os.close();
    assertEquals(0, mUfs.getStatus(testFilePath).asUfsFileStatus().getContentLength());

    try {
      // Set attribute with a normal pair of key and value
      String attrKey = "key1";
      String attrValue = "value1";
      mUfs.setAttribute(testFilePath, attrKey, attrValue.getBytes());

      // Set attribute with an empty value
      String attrKey2 = "key2";
      String attrEmptyValue = "";
      mUfs.setAttribute(testFilePath, attrKey2, attrEmptyValue.getBytes());

      // Set attribute with an empty key
      String attrEmptyKey = "";
      mUfs.setAttribute(testFilePath, attrEmptyKey, attrValue.getBytes());

      Map<String, String> attrMap = mUfs.getAttributes(testFilePath);
      assertEquals(attrMap.size(), 2);
      assertEquals(attrMap.get(attrKey), attrValue);
      assertEquals(attrMap.get(attrKey2), attrEmptyValue);
      assertFalse(attrMap.containsKey(attrEmptyKey));
    } finally {
      mUfs.deleteFile(testFilePath);
    }
  }

  @Test
  public void testSetDuplicatedKeyToXAttr() throws Exception {
    // create empty file
    String testFilePath = "/dup_xattr_file";
    OutputStream os = mUfs.create(testFilePath, getCreateOption());
    os.close();
    assertEquals(0, mUfs.getStatus(testFilePath).asUfsFileStatus().getContentLength());

    try {
      // Set attribute with a same key twice to overwrite it
      String attrKey = "key1";
      String attrValue1 = "value1";
      String attrValue2 = "value2";
      mUfs.setAttribute(testFilePath, attrKey, attrValue1.getBytes());
      mUfs.setAttribute(testFilePath, attrKey, attrValue2.getBytes());
      Map<String, String> attrMap = mUfs.getAttributes(testFilePath);
      assertEquals(attrMap.size(), 1);
      assertEquals(attrMap.get(attrKey), attrValue2);
    } finally {
      mUfs.deleteFile(testFilePath);
    }
  }
}
