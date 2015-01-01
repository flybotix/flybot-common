package com.flybot.util.log;

import com.flybot.util.lang.IUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogUtils implements ILog {
	public static void registerOutputListener(IUpdate<LogOutput> pListener)
	{
		LoggingControls.INST.addOutputListener(pListener);
	}
	
	public static void deregisterOutputListener(IUpdate<LogOutput> pListener)
	{
		LoggingControls.INST.removeOutputListener(pListener);
	}
	
	private LogUtils() {}

	@Override
	public void error(Object... pOutputs) {
		LoggingControls.INST.log(ELevel.ERROR, generateString(pOutputs));
	}

	@Override
	public void exception(Throwable pException) {
		LoggingControls.INST.logException(pException);
	}

	@Override
	public void debug(Object... pOutputs) {
		LoggingControls.INST.log(ELevel.DEBUG, generateString(pOutputs));
	}

	@Override
	public void info(Object... pOutputs) {
		LoggingControls.INST.log(ELevel.INFO, generateString(pOutputs));

	}

	@Override
	public void warn(Object... pOutputs) {
		LoggingControls.INST.log(ELevel.WARN, generateString(pOutputs));

	}

	public static ILog createLog(Class<?> pClass) {
		ILog result = sDEBUGS.get(pClass);
		if (result == null) {
			result = new LogUtils();
			sDEBUGS.put(pClass, result);
		}
		return result;
	}
	
	public static List<LogOutput> getRecentLogs()
	{
		return LoggingControls.INST.getHistory();
	}

	private static String generateString(Object... pOutputs) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pOutputs.length; i++) {
			sb.append(pOutputs[i]);
		}
		return sb.toString();
	}

	private static Map<Class<?>, ILog> sDEBUGS = new HashMap<Class<?>, ILog>();
}
