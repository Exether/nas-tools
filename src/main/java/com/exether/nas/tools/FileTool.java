package com.exether.nas.tools;

import java.io.File;
import java.util.Map;

public class FileTool {

  public static void main(String[] argv) throws ToolException {
    if ("-deleteDuplicates".equals(argv[0])) {
      if (argv.length < 3) {
        usage();
        return;
      }
      System.out.println(deleteDuplicates(argv));
    } else {
      usage();
    }
  }

  private static String deleteDuplicates(String[] argv) throws ToolException {
    File fromFolder = getExistingFolder(argv[1]);
    if (fromFolder == null)
      return null;
    FileDatabase fdb = new FileDatabase();
    fdb.load();
    fdb.addFileSet(fromFolder);
    for(int i = 2; i < argv.length; i++) {
      File otherFolder = getExistingFolder(argv[i]);
      if(otherFolder == null)
        return null;
      fdb.addFileSet(otherFolder);
    }
    fdb.save();
    Map<File, String> filesToDelete = fdb.getFilesToDelete(fromFolder);
    return "";
  }

  private static File getExistingFolder(String filename) {
    File result = new File(filename);
    if (!result.exists()) {
      System.err.println(result + " does not exist.");
      return null;
    }
    if (!result.isDirectory()) {
      System.err.println(result + " is not a directory.");
      return null;
    }
    return result;
  }

  private static void usage() {
    System.out.println("tool -deleteDuplicates from to1 [to2 to3 ... ton]");
  }

}
