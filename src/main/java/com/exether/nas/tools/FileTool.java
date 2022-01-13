package com.exether.nas.tools;

import java.io.File;

public class FileTool {

  public static void main(String[] argv) {
    if ("-deleteDuplicates".equals(argv[0])) {
      if (argv.length < 3) {
        usage();
        return;
      }
      File fromFolder = new File(argv[1]);
      if (!fromFolder.exists()) {
        System.err.println(fromFolder + " does not exist.");
        return;
      }
      if (!fromFolder.isDirectory()) {
        System.err.println(fromFolder + " is not a directory.");
        return;
      }
      FileDatabase fdb = new FileDatabase();
    }
  }


  private static void usage() {
    System.out.println("tool -deleteDuplicates from to1 [to2 to3 ... ton]");
  }

}
