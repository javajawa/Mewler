package uk.co.harcourtprogramming.internetrelaycats;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>An external service is a {@link InternetRelayCat} {@link Service} that runs in a
 * separate thread from the IRC interface.</p>
 * <p>Currently, it is unable to receive messages (although, they can be
 * forwarded with a {@link MessageService}</p>
 * <p>By default, the service runs in a daemon thread, with a default
 * {@link Thread.UncaughtExceptionHandler UncaughtExceptionHandler}</p>
 */
public abstract class ExternalService extends Service implements Runnable
{
	/**
	 * <p>The thread group for the primary external service threads</p>
	 */
	private final static ThreadGroup THREAD_GROUP =
		new ThreadGroup("ExternalServices")
		{
			@Override
			public void uncaughtException(Thread t, Throwable e)
			{
				InternetRelayCat.getLogger().log(
					Level.SEVERE,
					"Uncaught Exception in " + t.getName(),
					e
				);
			}
		};
	/**
	 * <p>The thread that this Service will run in</p>
	 */
	private final Thread t;
	/**
	 * <p>Reference to the {@link InternetRelayCat} instance that is using this
	 * service.</p>
	 * <p>This only will be null until the instance starts the thread; this must
	 * always be tested for.</p>
	 */
	private final RelayCat inst;

	/**
	 * <p>Create the external service</p>
	 * <p>The {@link #t thread} is created at this time, and will be {@link
	 * Thread#start() started} when the {@link InternetRelayCat} {@link #inst
	 * instance} is initialised.</p>
	 * <p>External services can only be attached to one {@link InternetRelayCat}
	 * instance; however, they still need to be added after creation with
	 * {@link InternetRelayCat#addService(uk.co.harcourtprogramming.internetrelaycats.Service)
	 * InternetRelayCat.addService}. Adding the service will cause the service's
	 * thread to be run.</p>
	 * @param inst the instance that this external service will work with
	 */
	public ExternalService(InternetRelayCat inst)
	{
		super();
		t = new Thread(THREAD_GROUP, this, super.toString());
		t.setDaemon(true);
		this.inst = inst;
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
	 * @return the instance that this service was created to serve
	 */
	protected final RelayCat getInstance()
	{
		return inst;
	}
}

