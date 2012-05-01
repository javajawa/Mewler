package uk.co.harcourtprogramming.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedict
 */
public class LogDecorator
{
	@SuppressWarnings("NonConstantLogger")
	private final Logger inner;

	public static LogDecorator getLogger(String name)
	{
		return new LogDecorator(Logger.getLogger(name));
	}

	public LogDecorator(Logger inner)
	{
		this.inner = inner;
	}

	public void severe(String messageFormat, Object ... params)
	{
		log(Level.SEVERE, null, messageFormat, params);
	}

	public void severe(Throwable ex, String messageFormat, Object ... params)
	{
		log(Level.SEVERE, ex, messageFormat, params);
	}

	public void warning(String messageFormat, Object ... params)
	{
		log(Level.WARNING, null, messageFormat, params);
	}

	public void warning(Throwable ex, String messageFormat, Object ... params)
	{
		log(Level.WARNING, ex, messageFormat, params);
	}

	public void info(String messageFormat, Object ... params)
	{
		log(Level.INFO, null, messageFormat, params);
	}

	public void info(Throwable ex, String messageFormat, Object ... params)
	{
		log(Level.INFO, ex, messageFormat, params);
	}

	public void fine(String messageFormat, Object ... params)
	{
		log(Level.FINE, null, messageFormat, params);
	}

	public void fine(Throwable ex, String messageFormat, Object ... params)
	{
		log(Level.FINE, ex, messageFormat, params);
	}

	public void finer(String messageFormat, Object ... params)
	{
		log(Level.FINE, null, messageFormat, params);
	}

	public void finer(Throwable ex, String messageFormat, Object ... params)
	{
		log(Level.FINE, ex, messageFormat, params);
	}

	public void log(Level lvl, Throwable ex, String messForm, Object...params)
	{
		LogRecord r = new LogRecord(lvl, messForm);
		r.setParameters(params);
		r.setThrown(ex);
		inner.log(r);
	}

	public void uncaught(Thread t, Throwable ex)
	{
		LogRecord r = new LogRecord(t, Level.SEVERE, "Uncaught excpetion in ''{0}''");
		r.setParameters(new Object[] {t.getName()});
		r.setThrown(ex);
		inner.log(r);
	}
}
