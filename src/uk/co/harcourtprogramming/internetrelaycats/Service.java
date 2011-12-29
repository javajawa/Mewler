package uk.co.harcourtprogramming.internetrelaycats;

import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.Level;

public abstract class Service
{
	private static int lastId = 0;

	private static synchronized int id()
	{
		return ++lastId;
	}

	private final int id = id();
	private final Logger log = Logger.getLogger("InternetRelayCats.Service." + id);

	public Service()
	{
		final Handler h = new ConsoleHandler();
		h.setFormatter(new Formatter()
		{
			@Override
			public String format(LogRecord l)
			{
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(l.getMillis());

				return String.format("[%2$tR %3$s] %1$s >> %4$s\n",
				    Service.this, c, l.getLevel().toString(), formatMessage(l));
			}
		});
		log.addHandler(h);
		log.setUseParentHandlers(false);
	}

	public void log(Level lvl, String msg, Throwable ex)
	{
		log.log(lvl, msg, ex);
	}

	public void log(Level lvl, String msg)
	{
		log.log(lvl, msg);
	}

	public void log(Level lvl, String msg, Object[] params)
	{
		log.log(lvl, msg, params);
	}

	public void log(Level lvl, Throwable ex)
	{
		log.log(lvl, null, ex);
	}

	protected final int getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + '@' + this.getId();
	}

	public abstract void shutdown();
}
