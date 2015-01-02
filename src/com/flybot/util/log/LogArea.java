package com.flybot.util.log;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.TextArea;

import com.flybot.util.log.LogOutput;

public class LogArea extends TextArea
{
  private boolean mUseTimestamps = false;
  private static DateTimeFormatter mFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private static final ZoneId zone;
  private static final String NEWLINE = "\n";
  
  static
  {
    String tzone = System.getProperty("user.timezone");
    if(tzone == null)
    {
      tzone = "America/New_York";
    }
    zone = ZoneId.of(tzone);
  }

  public LogArea()
  {
    super();
    setEditable(false);
    setWrapText(true);
  }

  public void setUseTimestamps(boolean pUseTimestamps)
  {
    mUseTimestamps = pUseTimestamps;
  }
  
  public void log(LogOutput pInput)
  {
    log(pInput.text);
  }

  public void log(String pInput)
  {
    if(mUseTimestamps)
    {
      LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), zone);
      appendText("[");
      appendText(ldt.format(mFormat));
      appendText("]  ");
    }
    appendText(pInput);
    appendText(NEWLINE);
  }
}
