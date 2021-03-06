package com.flybot.util.log;


public interface ILog {
	public void debug(Object... pOutputs);
	public void info(Object... pOutputs);
	public void warn(Object... pOutputs);
	public void error(Object... pOutputs);
	public void exception(Throwable pException);
}
