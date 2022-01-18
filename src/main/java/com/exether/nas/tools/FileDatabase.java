package com.exether.nas.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileDatabase {

  private static String[] EXCLUDED_FOLDERS = {"@eaDir", ".git"};

  // Hash, list of fileInfo with this hash
  private Map<String, List<FileInfo>> databaseByCheckSum;
  // filename, list of fileInfo with this filename
  private Map<String, List<FileInfo>> databaseByFile;
  // Set Id, list of fileInfo with this hash
  private Map<String, List<FileInfo>> databaseBySetId;

  private List<File> folders;

  public FileDatabase() {
    databaseByCheckSum = new HashMap<>();
    databaseByFile = new HashMap<>();
    databaseBySetId = new HashMap<>();
    folders = new ArrayList<>();
  }

  public void addFileSet(File folder) throws ToolException {
    folders.add(folder);
    List<File> front = new ArrayList<>();
    front.add(folder);
    while (!front.isEmpty()) {
      List<File> newFront = new ArrayList<>();
      for (File curFolder : front) {
        for (File sub : curFolder.listFiles()) {
          if (isEligibleFolder(sub)) {
            newFront.add(sub);
          } else if (sub.isFile()) {
            FileInfo info;
            if (databaseByFile.containsKey(sub.getAbsolutePath())) {
              info = databaseByFile.get(sub.getAbsolutePath()).get(0);
              info.setSet(folder);
            } else {
              info = new FileInfo(sub);
              info.setSet(folder);
              addFileInfo(databaseByFile, info.getAbsoluteFilePath(), info);
              addFileInfo(databaseByCheckSum, info.getCheckSum(), info);
            }
            addFileInfo(databaseBySetId, folder.getAbsolutePath(), info);
          }
        }
      }
      front = newFront;
    }
  }

  private void addFileInfo(Map<String, List<FileInfo>> map, String key, FileInfo info) {
    if (!map.containsKey(key))
      map.put(key, new ArrayList<>());
    map.get(key).add(info);
  }

  private boolean isEligibleFolder(File sub) {
    if (!sub.isDirectory())
      return false;
    for (String excl : EXCLUDED_FOLDERS) {
      if (excl.equals(sub.getName()))
        return false;
    }
    if (folders.contains(sub))
      return false;
    return true;
  }

  public void load() {
    if (!new File(getSaveFileName()).exists())
      return;
    try {
      FileReader reader = new FileReader(getSaveFileName());
      BufferedReader bufferedReader = new BufferedReader(reader);

      String line;

      while ((line = bufferedReader.readLine()) != null) {
        String[] split = line.split("\\|");
        FileInfo info = new FileInfo(split[0], split[1]);
        addFileInfo(databaseByCheckSum, info.getCheckSum(), info);
        addFileInfo(databaseByFile, info.getAbsoluteFilePath(), info);
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void save() throws ToolException {
    String fileSave = getSaveFileName();
    FileWriter writer = null;
    try {
      writer = new FileWriter(fileSave, false);
      // We don't save the sets which could change, we just keep the file -> checksums associations
      for (Map.Entry<String, List<FileInfo>> e : databaseByCheckSum.entrySet()) {
        for (FileInfo file : e.getValue()) {
          writer.write(file.getAbsoluteFilePath() + "|" + file.getCheckSum() + "\n");
        }
      }
      writer.close();
    } catch (IOException e) {
      throw new ToolException("Impossible to save file for DB " + fileSave, e);
    }
  }

  private String getSaveFileName() {
    return System.getProperty("user.home") + "/.fileDatabase.txt";
  }

  public Map<File, String> getFilesToDelete(File fromFolder) {
    List<FileInfo> files = databaseBySetId.get(fromFolder.getAbsolutePath());
    Map<File, String> result = new HashMap<>();
    String setId = fromFolder.getAbsolutePath();
    for (FileInfo info : files) {
      List<FileInfo> dups = databaseByCheckSum.get(info.getCheckSum());
      List<FileInfo> dupsFrom = dups.stream().filter(i -> setId.equals(i.getRegisteredWithSet()))
          .sorted(Comparator.comparingInt(i -> ((FileInfo)i).getAbsoluteFilePath().length())
              .thenComparing(i -> ((FileInfo)i).getAbsoluteFilePath()))
          .collect(Collectors.toList());
      List<FileInfo> dupsOther = dups.stream().filter(i -> !setId.equals(i.getRegisteredWithSet())).collect(Collectors.toList());
      // Cases (from,other), (1,0), (n,0), (1,n), (n,m)
      int deleteStartingFrom = 0;
      String comment;
      if (dupsOther.isEmpty()) {
        comment = "File is duplicate within the input folder (keeping " + dupsFrom.get(0).getAbsoluteFilePath() + ")";
        deleteStartingFrom = 1;
      } else {
        comment = "File is duplicate (keeping " + dupsOther.stream().map(FileInfo::getAbsoluteFilePath)
            .collect(Collectors.joining(",")) + ") " + (dupsOther.size() > 1 ? " WARNING" : "");
      }
      for (int i = deleteStartingFrom; i < dupsFrom.size(); i++) {
        result.put(new File(dupsFrom.get(i).getAbsoluteFilePath()), comment);
      }
    }
    return result;
  }

  public String getStatisticsAsComments() {
    StringBuilder result = new StringBuilder();
    result.append("# DB used ").append(folders.size()).append(" input folders.\n");
    result.append("# DB contains ").append(databaseByFile.size()).append(" different files.\n");
    result.append("# DB contains ").append(databaseByCheckSum.size()).append(" different checksums.\n");
    return result.toString();
  }
}
