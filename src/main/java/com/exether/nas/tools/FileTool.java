package com.exether.nas.tools;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileTool {

  public static void main(String[] argv) throws ToolException {
    // TODO -cleanUpDB (remove non existing files)
    // TODO -deleteEmptyDirs (find . -type d -empty -print -delete)
    // TODO -normalizeFileNames (replace white spaces and diacritics)
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

  public static String deleteDuplicates(String[] argv) throws ToolException {
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
    StringBuilder commands = new StringBuilder();
    commands.append(fdb.getStatisticsAsComments());
    List<File> sortedEntries = filesToDelete.keySet().stream().sorted(Comparator.comparing(File::getAbsolutePath)).collect(Collectors.toList());
    for(File key: sortedEntries) {
      commands.append("# ").append(filesToDelete.get(key)).append("\n");
      commands.append("rm \"").append(key.getAbsolutePath()).append("\"\n");
    }
    return commands.toString();
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
