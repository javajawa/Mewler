package uk.co.harcourtprogramming.netcat;

import java.util.logging.Level;

/**
 * <p>An external service is a {@link NetCat} {@link Service} that runs in a
 * separate thread from the IRC interface.</p>
 * <p>Currently, it is unable to receive messages (although, they can be
 * forwarded with a {@link MessageService}</p>
 * <p>By default, the service runs in a daemon thread, with a default
 * {@link Thread.UncaughtExceptionHandler UncaughtExceptionHandler}</p>
 */
public abstract class ExternalService extends Service implements Runnable
{
	/**
	 * The thread that this Service will run in
	 */
	private final Thread t = new Thread(this);
	/**
	 * <p>Reference to the {@link NetCat} instance that is using this
	 * service.</p>
	 * <p>This only will be null until the instance starts the thread; this must
	 * always be tested for.</p>
	 */
	private NetCat inst = null;

	/**
	 * <p>Create the external service</p>
	 * <p>The {@link #t thread} is created at this time, and will be {@link
	 * Thread#start() started} when the {@link NetCat} {@link #inst
	 * instance} is initialised.</p>
	 */
	public ExternalService()
	{
		super();
		t.setDaemon(true);
		t.setName(this.getClass().getSimpleName() + '@' + this.getId());
		t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable thrwbl)
			{
				ExternalService.this.log(Level.SEVERE, "Uncaught Exception", thrwbl);
			}
		});
	}

	/**
	 * @return reference to the Thread object this service will {@link #run()
	 * run} in.
	 */
	protected final Thread getThread()
	{
		return t;
	}

	/**
	 * Send a message
	 * @param target The user or channel to send the message to, e.g. 'bob',
	 * '#kittens'
	 * @param message The message to send. Multi-line messages are broken into
	 * single lines, but will not lose ordering or be interspaced with other
	 * lines from this service.
	 */
	protected synchronized final void message(String target, String message)
	{
		log(Level.FINE, " -> {0}: {1}", new Object[]{target, message});
		if (inst == null) return;
		if (message == null) return;
		for (String line : message.split("\n"))
		{
			inst.sendMessage(target, line);
		}
	}

	/**
	 * Assigns a {@link NetCat} instance to this service, allowing it
	 * to interface with IRC.
	 * @param i The instance to assign
	 */
	final void setInstance(NetCat i)
	{
		// TODO: check we're not disconnecting another instance
		inst = i;
	}
}

