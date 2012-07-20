package uk.co.harcourtprogramming.internetrelaycats;

import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import uk.co.harcourtprogramming.logging.LogDecorator;
import uk.co.harcourtprogramming.logging.LogFormatter;

// TODO: Switch to LogDecorator

/**
 * <p>The base class for Services for {@link RelayCat}</p>
 * <p>Services form the main part of the user logic for a chat bot; they
 * can send and receive messages, etc.</p>
 */
public abstract class Service
{
	/**
	 * Stores the previously assigned
	 */
	private static int lastId = 0;

	/**
	 * @return The next sequential id for a service
	 * @throws Error thrown if every integer has been assigned to a service
	 */
	private static synchronized int id()
	{
		++lastId;
		if (lastId == 0) throw new Error("Out of available service ids");
		return lastId;
	}
	/**
	 * @deprecated for testing use <b>only</b>
	 * @return the last assigned id
	 */
	static synchronized int lastId()
	{
		return lastId;
	}

	/**
	 * Assign the id to this service
	 */
	private final int id = id();
	/**
	 * Logger for this service
	 */
	private final LogDecorator LOG;

	/**
	 * <p>Create the general implementation of a Service</p>
	 * <p>This initialises this services {@link #getId() id}, and the attached
	 * logger</p>
	 */
	public Service()
	{
		final Handler h = new ConsoleHandler();
		h.setFormatter(new LogFormatter()
		{
			@Override
			public String format(LogRecord l)
			{
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(l.getMillis());

				return String.format("[%3$tD %3$tR %2$s] %1$s >> %4$s\n",
					Service.this,
					l.getLevel().getLocalizedName(),
					c,
					formatMessage(l)
				);
			}
		});
		Logger log = Logger.getLogger("InternetRelayCats.Service." + id);
		log.addHandler(h);
		log.setUseParentHandlers(false);
		LOG = new LogDecorator(log);
	}

	/**
	 * <p>Log a message, with associated Throwable information.</p>
	 * <p>If the logger is currently enabled for the given message level
	 * then the given arguments are stored in a LogRecord which is forwarded
	 * to all registered output handlers.</p>
	 * <p>Note that the thrown argument is stored in the LogRecord thrown
	 * property, rather than the LogRecord parameters property.
	 * Thus is it processed specially by output Formatters and is not treated
	 * as a formatting parameter to the LogRecord message property.</p>
	 * @param lvl One of the message level identifiers, e.g. SEVERE
	 * @param msg The string message (or a key in the message catalog)
	 * @param ex Throwable associated with log message.
	 * @see Logger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
	 */
	protected final void log(Level lvl, String msg, Throwable ex)
	{
		LOG.log(lvl, ex, msg);
	}

	/**
	 * <p>Log a message, with an array of object arguments.</p>
	 * <p>If the logger is currently enabled for the given message level then
	 * a corresponding LogRecord is created and forwarded to all the registered
	 * output Handler objects.</p>
	 * @param lvl One of the message level identifiers, e.g. SEVERE
	 * @param msg The string message (or a key in the message catalog)
	 */
	protected final void log(Level lvl, String msg)
	{
		LOG.log(lvl, null, msg);
	}

	/**
	 * <p>Log a message, with an array of object arguments.</p>
	 * <p>If the logger is currently enabled for the given message level then
	 * a corresponding LogRecord is created and forwarded to all the registered
	 * output Handler objects.</p>
	 * @param lvl One of the message level identifiers, e.g. SEVERE
	 * @param msg The string message (or a key in the message catalog)
	 * @param params array of parameters to the message
	 */
	protected final void log(Level lvl, String msg, Object[] params)
	{
		LOG.log(lvl, null, msg, params);
	}

	/**
	 * <p>Log a message, with associated Throwable information.</p>
	 * <p>If the logger is currently enabled for the given message level
	 * then the given arguments are stored in a LogRecord which is forwarded
	 * to all registered output handlers.</p>
	 * <p>Note that the thrown argument is stored in the LogRecord thrown
	 * property, rather than the LogRecord parameters property.
	 * Thus is it processed specially by output Formatters and is not treated
	 * as a formatting parameter to the LogRecord message property.</p>
	 * @param lvl One of the message level identifiers, e.g. SEVERE
	 * @param ex Throwable associated with log message.
	 * @see Logger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
	 */
	protected final void log(Level lvl, Throwable ex)
	{
		LOG.log(lvl, ex, null);
	}

	/**
	 * <p>This Service's service id</p>
	 * <p>The service id is a unique, consecutively assigned integer used to
	 * identify the service</p>
	 * @return The service id
	 */
	protected final int getId()
	{
		return id;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + '@' + this.getId();
	}

	/**
	 * <p>Code that is to be run when the {@link RelayCat} instance is closed,
	 * or this service is otherwise being removed from the instance.</p>
	 * <p>This function should not be called by user code; to have external code
	 * remove a service, use {@link IRelayCat#unregister(Service)
	 * IRelayCat.unregister}</p>
	 */
	protected abstract void shutdown();

	/**
	 * <p>Code that is to be run when the service is added to a {@link RelayCat}
	 * instance.</p>
	 * <p>This function should not be called by user code; to have external code
	 * remove a service, use {@link IRelayCat#unregister(Service)
	 * IRelayCat.unregister}</p>
	 */
	protected abstract void startup(RelayCat r);
}
