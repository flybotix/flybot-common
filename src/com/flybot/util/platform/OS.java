package com.flybot.util.platform;

import java.util.Properties;

import com.flybot.util.log.LogUtils;
import com.flybot.util.log.ILog;

public class OS
{
	
	private static final ILog mLog = LogUtils.createLog(OS.class);
	private static ESupportedPlatform mCurrentPlatform = ESupportedPlatform.UNSUPPORTED;

	public static void printEnvironment()
	{
		for(String key : System.getenv().keySet())
		{
			mLog.debug(key + "= " + System.getenv(key));
		}
	}

	public static void printProperties()
	{

		Properties p = System.getProperties();
		for(Object o : p.keySet())
		{
			mLog.debug(o.toString() + "=" + p.get(o));
		}
	}
	
	private static final IPlatform sPLATFORM_INSTANCE;

  static
  {

    if(System.getProperty("os.name").contains("Windows"))
    {
      mCurrentPlatform = ESupportedPlatform.WINDOWS;
    }
    else
    {
      mCurrentPlatform = ESupportedPlatform.LINUX;
    }
    switch(mCurrentPlatform)
    {
      case WINDOWS:
        sPLATFORM_INSTANCE = new WindowsUtils();
        break;
      case LINUX:
      default:
        sPLATFORM_INSTANCE = new LinuxUtils();
        break;
    }

  }
	
	public static final IPlatform platform()
	{
	  return sPLATFORM_INSTANCE;
	}

	public static void main(String[] pArgs) {
		printEnvironment();
		printProperties();
	}
	
}
