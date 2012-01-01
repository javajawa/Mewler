package uk.co.harcourtprogramming.internetrelaycats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.io.IOException;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

/**
 * <p>The main class for InternetRelayCats</p>
 */
public class BasicRelayCat implements Runnable, RelayCat
{

	/**
	 * <p>The logger for the bot</p>
	 */
	private final static Logger log = Logger.getLogger("InternetRelayCat");
	static
	{
		Handler h = new ConsoleHandler();
		h.setFormatter(new Formatter()
		{
			@Override
			public String format(LogRecord l)
			{
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(l.getMillis());

				return String.format("[%2$tR %1$s] %3$s\n",
					l.getLevel().getLocalizedName(), time, formatMessage(l));
			}
		});
		log.addHandler(h);
		log.setUseParentHandlers(false);
	}

	/**
	 * <p>The host to which we shall connect to when the the thread is run</p>
	 */
	private final String host;
	/**
	 * <p>A list of channels to connect to when the thread is run</p>
	 */
	private final List<String> channels;
	/**
	 * <p>The list of currently activated {@link Service Services}</p>
	 */
	private final List<Service> srvs = new ArrayList<Service>();
	/**
	 * <p>The list of currently activated {@link MessageService MessageServices}</p>
	 */
	private final List<MessageService> msrvs = new ArrayList<MessageService>();
	/**
	 * <p>Flag to denote that the bot is currently exiting</p>
	 */
	private boolean dispose = false;

	/**
	 * <p>instance of the underlying bot interface</p>
	 */
	protected final CatBot bot;

	/**
	 * <p>Creates a BasicRelayCat instance</p>
	 * <p>The instance is initialised, and services can be added, but does not
	 * connect to the server specified in host until it is run, either by
	 * calling the {@link #run() run} method directly, or executing it in a new
	 * {@link Thread} with:
	 * <pre>    new Thread(BasicRelayCat).start();</pre>
	 * </p>
	 * <p>A list of channels can be supplied to the constructor so that they
	 * are joined when the server connection is made. Other channels can be
	 * joined later with {@link RelayCat#join(java.lang.String)}</p>
	 * @param name the name for the bot
	 * @param host the host to connect to
	 * @param channels a list of channels to connect to as soon as a connection
	 * is established
	 * @throws IllegalArgumentException if the name or host are not supplied
	 */
	public BasicRelayCat(final String name, final String host, final List<String> channels)
	{
		super();
		if (host==null) throw new IllegalArgumentException("Host must be supplied");
		if (name==null || name.length() == 0)  throw new IllegalArgumentException("Name must be supplied");

		this.host = host;
		bot = new CatBot(name);
		bot.setInst(this);
		bot.setVerbose(false);

		if (channels == null)
		{
			this.channels = new ArrayList<String>(0);
		}
		else
		{
			this.channels = new ArrayList<String>(channels);
		}
	}

	/**
	 * <p>Creates a BasicRelayCat instance</p>
	 * <p>This constructor is for sub classes to override and supply their own
	 * wrapper to {@link PircBot} which, for compatibility reasons, much extend
	 * {@link CatBot}. Note that none of CatBot's methods are private or final.
	 * </p>
	 * <p>The instance is initialised, and services can be added, but does not
	 * connect to the server specified in host until it is run, either by
	 * calling the {@link #run() run} method directly, or executing it in a new
	 * {@link Thread} with:
	 * <pre>    new Thread(BasicRelayCat).start();</pre>
	 * </p>
	 * <p>A list of channels can be supplied to the constructor so that they
	 * are joined when the server connection is made. Other channels can be
	 * joined later with {@link RelayCat#join(java.lang.String)}</p>
	 * @param bot the bot to be used
	 * @param host the host to connect to
	 * @param channels a list of channels to connect to as soon as a connection
	 * is established
	 */
	protected BasicRelayCat(CatBot bot, final String host, final List<String> channels)
	{
		super();
		if (host==null) throw new IllegalArgumentException("Host must be supplied");

		this.host = host;
		this.bot = bot;

		if (channels == null)
		{
			this.channels = new ArrayList<String>(0);
		}
		else
		{
			this.channels = new ArrayList<String>(channels);
		}
	}

	/**
	 * <p>Adds a service to the BasicRelayCat</p>
	 * <p>{@link MessageService Message Services} will be forwarded inputs</p>
	 * <p>{@link ExternalService External Services} will be correctly
	 * initialised, and their threads started</p>
	 * <p>Note that {@link Service Services} that do not fall into one of the
	 * two above categories will have to be supplied with access to the {@link
	 * RelayCat interface} through external code; this behaviour is not
	 * recommended</p>
	 * @param s the service to add
	 */
	public void addService(Service s)
	{
		synchronized(srvs)
		{
			if (dispose) return;
			log.log(Level.INFO, "Service Loaded: {0}@{1}",
			    new Object[]{s.getClass().getSimpleName(), s.getId()});
			if (s instanceof MessageService)
			{
				msrvs.add((MessageService)s);
				log.log(Level.INFO, "Service {0}@{1} loaded as MessageService.",
				    new Object[]{s.getClass().getSimpleName(), s.getId()});
			}
			if (s instanceof ExternalService)
			{
				final ExternalService es = (ExternalService)s;
				if (es.getInstance() != this)
					throw new IllegalArgumentException("Supplied External Service does not belong to this RelayCat instance");
				es.getThread().start();
			}
			srvs.add(s);
		}
	}

	/**
	 * <p>Runs the bot</p>
	 * <p>Not that this function will block until {@link #shutdown()} is called;
	 * thus is it recommend to run the bot in a new thread:
	 * <pre>    new Thread(BasicRelayCat).start();</pre></p>
	 */
	@Override
	public synchronized void run()
	{
		try
		{
			log.log(Level.INFO, "Connecting to ''{0}''", host);
			bot.connect(host);
			for (String channel : channels)
			{
				log.log(Level.INFO, "Joining ''{0}''", channel);
				bot.joinChannel(channel);
			}
			log.log(Level.INFO, "Operations Running!");
			wait();
		}
		catch (IOException ex)
		{
			log.log(Level.SEVERE, null, ex);
		}
		catch (IrcException ex)
		{
			log.log(Level.SEVERE, null, ex);
		}
		catch (InterruptedException ex)
		{
			shutdown();
		}
		bot.quitServer();
		bot.disconnect();
		bot.dispose();
	}

	/**
	 * <p>Unblocks a call to {@link #run() run}, causing the bot to exit</p>
	 */
	public synchronized void shutdown()
	{
		synchronized(srvs)
		{
			setDispose(true);
			for (Service s : srvs) s.shutdown();
		}
		notifyAll(); // run() waits to stop thread being killed; exits when notified
	}

	/**
	 * <p>Object lock to synchronise on when sending messages, such that
	 * multi-line messages can be sent as a contiguous block</p>
	 */
	private final Object transmissionLock = new Object();

	@Override
	public void message(String target, String message)
	{
		if (target == null || target.length() == 0) throw new IllegalArgumentException("Invalid target: null or empty string");
		if (message == null || message.length() == 0) return;
		synchronized (transmissionLock)
		{
			for (String line : message.split("\n"))
			{
				bot.sendMessage(target, line);
			}
		}
	}

	@Override
	public void act(String target, String action)
	{
		if (target == null || target.length() == 0) throw new IllegalArgumentException("Invalid target: null or empty string");
		if (action == null || action.length() == 0) return;
		synchronized (transmissionLock)
		{
			bot.sendAction(target, action);
		}

	}

	@Override
	public void join(String channel)
	{
		bot.joinChannel(channel);
	}

	@Override
	public void leave(String channel)
	{
		bot.partChannel(channel);
	}

	@Override
	public String getNick()
	{
		return bot.getNick();
	}

	@Override
	public User[] names(String channel)
	{
		return bot.getUsers(channel);
	}

	@Override
	public String[] channels()
	{
		return bot.getChannels();
	}

		/**
	 * <p>Flag to denote that the bot is currently exiting</p>
	 * @return the dispose
	 */
	protected boolean isDispose()
	{
		return dispose;
	}

	/**
	 * <p>Flag to denote that the bot is currently exiting</p>
	 * @param dispose the dispose to set
	 */
	protected void setDispose(boolean dispose)
	{
		this.dispose = dispose;
	}

	/**
	 * <p>The list of currently activated {@link Service Services}</p>
	 * @return the srvs
	 */
	protected List<Service> getSrvs()
	{
		return srvs;
	}

	/**
	 * <p>The list of currently activated {@link MessageService MessageServices}</p>
	 * @return the msrvs
	 */
	protected List<MessageService> getMsrvs()
	{
		return msrvs;
	}

}
