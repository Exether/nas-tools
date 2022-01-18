package com.exether.nas.tools;

import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class FileToolTest {

  // FileUtils.writeStringToFile(new File(FileToolTest.class.getClassLoader().getResource("result1.txt").getFile().replace("%20", " ")), result, Charset.defaultCharset(), false);

  @Test
  public void deleteDuplicates() throws ToolException, IOException {
    String file = FileToolTest.class.getClassLoader().getResource("testFile1.txt").getFile();
    String folder = file.substring(0, file.lastIndexOf("/")).replace("%20", " ");
    String[] argv = new String[]{"-deleteDuplicates", folder + "/from", folder + "/other1", folder + "/other2"};
    String result = FileTool.deleteDuplicates(argv);
    String expectedResult = Files.contentOf(new File(FileToolTest.class.getClassLoader().getResource("result1.txt").getFile().replace("%20", " ")),
        StandardCharsets.UTF_8);
    assertThat(result).isEqualTo(expectedResult);
  }


}
