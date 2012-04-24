package uk.co.harcourtprogramming.logging;

import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * <p>Useful little decorator for the {@link Logger} class</p>
 * @author Benedict
 */
public class LogFormatter extends Formatter
{
	private final Calendar time = Calendar.getInstance();

	public static ConsoleHandler consoleHandler()
	{
		ConsoleHandler h = new ConsoleHandler();
		h.setFormatter(new LogFormatter());
		return h;
	}

	@Override
	public String format(LogRecord l)
	{
		String mess = formatMessage(l);
		synchronized (time)
		{
			time.setTimeInMillis(l.getMillis());
			return String.format("[%3$tD %3$tR %1$s %2$s] %4$s\n",
				l.getLoggerName(),
				l.getLevel().getLocalizedName(),
				time,
				mess
			);
		}
	}

	@Override
	public String formatMessage(LogRecord record)
	{
		if (record.getMessage() == null)
			return formatNoMessage(record);

		if (record.getThrown() == null)
			return super.formatMessage(record);

		Throwable thrown = record.getThrown();
		return String.format("%s <%s>%s::%s\n\t%s\n\t%s",
			thrown.getClass().getName(),
			Thread.currentThread().getName(),
			record.getSourceClassName(),
			record.getSourceMethodName(),
			super.formatMessage(record),
			thrown.getLocalizedMessage()
		);
	}

	private String formatNoMessage(LogRecord record)
	{
		Throwable thrown = record.getThrown();

		if (thrown == null)
		{
			return String.format("null log from <%3s>%1s::%2s",
				record.getSourceClassName(),
				record.getSourceMethodName(),
				Thread.currentThread().getName()
			);
		}
		else
		{
			return String.format("%s <%s>%s::%s\n\t%s",
				thrown.getClass().getName(),
				Thread.currentThread().getName(),
				record.getSourceClassName(),
				record.getSourceMethodName(),
				thrown.getLocalizedMessage()
			);
		}
	}
}
