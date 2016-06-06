package com.flybot.util.platform;

public class LinuxUtils implements IPlatform
{

  @Override
  public String getCurrentUserDesktopPath()
  {
    return "~/Desktop/";
  }

  @Override
  public String getCopyExecutable()
  {
    return "cp";
  }

}
