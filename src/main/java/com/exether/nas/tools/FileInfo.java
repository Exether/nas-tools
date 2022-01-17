package com.exether.nas.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileInfo {
  private final String checkSum;
  private final String absoluteFilePath;
  private String registeredWithSet;

  public FileInfo(String filePath) throws ToolException {
    File f = new File(filePath);
    if (!f.exists()) {
      throw new ToolException("File " + filePath + " does not exist.");
    }
    if (!f.isFile()) {
      throw new ToolException("File " + filePath + " is expected to be a file.");
    }
    this.absoluteFilePath = f.getAbsolutePath();
    this.checkSum = calculateCheckSum(f);
  }

  public FileInfo(File f) throws ToolException {
    this(f.getAbsolutePath());
  }

  public FileInfo(String fileName, String checkSum) {
    this.absoluteFilePath = fileName;
    this.checkSum = checkSum;
  }

  private String calculateCheckSum(File f) throws ToolException {
    try {
      byte[] buffer= new byte[8192];
      int count;
      MessageDigest digest = MessageDigest.getInstance("SHA-512");
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
      while ((count = bis.read(buffer)) > 0) {
        digest.update(buffer, 0, count);
      }
      bis.close();
      byte[] hash = digest.digest();
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new ToolException("Error while reading and calculating hash for " + f.getAbsolutePath(), e);
    }
  }


  public String getCheckSum() {
    return checkSum;
  }

  public String getAbsoluteFilePath() {
    return absoluteFilePath;
  }

  public String getRegisteredWithSet() {
    return registeredWithSet;
  }

  public void setSet(File folder) {
    registeredWithSet = folder.getAbsolutePath();
  }
}
