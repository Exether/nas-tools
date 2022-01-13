package com.exether.nas.tools;


public class ToolException extends Throwable {
  public ToolException(String s) {
    super(s);
  }

  public ToolException(String s, Exception e) {
    super(s + " ("+ e.getMessage() +")");
  }
}
