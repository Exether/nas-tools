package com.exether.nas.tools;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileInfoTest {

  @Test
  public void fileCreation() throws ToolException {
    String file = FileInfoTest.class.getClassLoader().getResource("testFile1.txt").getFile();
    FileInfo info1 = new FileInfo(file.replace("%20", " "));
    file = FileInfoTest.class.getClassLoader().getResource("testFile2.txt").getFile();
    FileInfo info2 = new FileInfo(file.replace("%20", " "));
    assertThat(info1.getCheckSum()).isNotEqualTo(info2.getCheckSum());
  }

  @Test
  public void sameFileCreation() throws ToolException {
    String file = FileInfoTest.class.getClassLoader().getResource("testFile1.txt").getFile();
    FileInfo info1 = new FileInfo(file.replace("%20", " "));
    file = FileInfoTest.class.getClassLoader().getResource("testFile1Again.txt").getFile();
    FileInfo info2 = new FileInfo(file.replace("%20", " "));
    assertThat(info1.getCheckSum()).isEqualTo(info2.getCheckSum());
  }

}
