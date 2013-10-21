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
		StringBuilder buffer = new StringBuilder(128);

		buffer
			.append('[');

		synchronized (time)
		{
			time.setTimeInMillis(l.getMillis());
			buffer.append(String.format("%1$tD %1$tR", time));
		}

		buffer
			.append(' ')
			.append(l.getLoggerName())
			.append(' ')
			.append(l.getLevel().getLocalizedName())
			.append(']')
			.append(' ')
		;

		formatMessage(l, buffer);

		return buffer.toString();
	}

	@Override
	public String formatMessage(final LogRecord record)
	{
		StringBuilder buffer = new StringBuilder(128);

		formatMessage(record, buffer);
		return buffer.toString();
	}

	public void formatMessage(final LogRecord record, StringBuilder buffer)
	{
		if (record.getMessage() != null)
		{
			buffer.append(super.formatMessage(record));
			if (record.getThrown() == null)
			{
				buffer.append('\n');
				return;
			}
		}
		else
			buffer.append("Empty log message from");

		buffer
			.append(' ')
			.append(record.getSourceClassName())
			.append("::")
			.append(record.getSourceMethodName())
			.append(" <")
			.append(Thread.currentThread().getName())
			.append(':')
			.append(Thread.currentThread().getThreadGroup().getName())
			.append('>')
			.append('\n');

		if (record.getThrown() != null)
			formatThrowable(record.getThrown(), buffer);
	}

	private void formatThrowable(final Throwable thrown, StringBuilder buffer)
	{
		buffer.append(thrown.getClass().getSimpleName()).append('\n');
		if (thrown.getMessage() != null)
			buffer.append(": ").append(thrown.getLocalizedMessage());
		buffer.append('\n');

		StackTraceElement[] trace = thrown.getStackTrace();

		for (StackTraceElement frame : trace)
		{
			buffer.append('\t').append(frame).append('\n');
		}

		if (thrown.getCause() != null)
			formatThrowable(thrown.getCause(), buffer);
	}
}
