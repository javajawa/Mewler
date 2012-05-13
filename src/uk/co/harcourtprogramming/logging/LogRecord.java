package uk.co.harcourtprogramming.logging;

import java.util.logging.Level;

public class LogRecord extends java.util.logging.LogRecord
{
	private static final long serialVersionUID = 1L;

	private final Thread thread;

	public LogRecord(Level level, String msg)
	{
		super(level, msg);
		thread = Thread.currentThread();
	}

	public LogRecord(Thread thread, Level level, String msg)
	{
		super(level, msg);
		this.thread = thread;
	}

	@Override
	public int getThreadID()
	{
		return (int)thread.getId();
	}

	public Thread getThread()
	{
		return thread;
	}


}
