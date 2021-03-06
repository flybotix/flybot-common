package com.flybot.util.lang;

import com.flybot.util.log.ILog;
import com.flybot.util.log.LogUtils;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Notifyer implements INotify {
	private CopyOnWriteArrayList<INotify> mListeners = new CopyOnWriteArrayList<INotify>();
	private ILog mLog = LogUtils.createLog(Notifyer.class);
	
	private Executor mExecutor = null;
	
	public Notifyer(boolean pUseSeparateThread)
	{
	  if(pUseSeparateThread)
	  {
	    mExecutor = Executors.newFixedThreadPool(1);
	  }
	}
	
	public void addNotificationListener(INotify pNotifyee)
	{
	  mListeners.add(pNotifyee);
	}
	
	public void removedNotificationListener(INotify pNotifyee)
	{
	  mListeners.remove(pNotifyee);
	}
	
	@Override
	public void notifyOfUpdate() {
	  if(mExecutor != null)
	  {
	    mExecutor.execute(new Runnable()
      {
        
        @Override
        public void run()
        {
          notifyImpl();
        }
      });
	  }
	  else
	  {
	    notifyImpl();
	  }
		
	}

	private void notifyImpl()
	{
	  for(INotify n : mListeners)
	  {
	    try
	    {
	      n.notifyOfUpdate();
	    }
	    catch (Exception e)
	    {
	      mLog.exception(e);
	    }
	  }
	}
}
