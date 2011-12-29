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
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;
import sun.awt.windows.ThemeReader;

/**
 * <p>The main class for InternetRelayCats</p>
 */
public class RelayCat implements Runnable, IRelayCat
{

	/**
	 * <p>Class that represents a message or action sent to the instance, or a
	 * channel that this instance is in</p>
	 */
	@SuppressWarnings("PublicInnerClass")
	public class Message implements IRelayCat
	{
		/**
		 * <p>The message that was sent</p>
		 */
		private final String message;
		/**
		 * <p>The nick of the sender</p>
		 */
		private final String nick;
		/**
		 * <p>The nick of the {@link RelayCat}</p>
		 */
		private final String me = RelayCat.this.getNick();
		/**
		 * <p>The channel that the message arrived in.</p>
		 * <p>This is null if the message arrived directly.</p>
		 */
		private final String channel;
		/**
		 * <p>Whether this input was an action (true) or a message (false)</p>
		 */
		private final boolean action;
		/**
		 * <p>Whether a service has marked this message for disposal</p>
		 */
		private boolean dispose = false;

		/**
		 * Creates a new message object
		 * @param message the input data
		 * @param nick the source nick
		 * @param channel the source channel (or null if sent directly)
		 * @param action whether this was an action or a message
		 */
		private Message(String message, String nick, String channel, boolean action)
		{
			this.message = Colors.removeFormattingAndColors(message);
			this.nick = nick;
			this.channel = channel;
			this.action = action;
		}

		/**
		 * @return if this input is an action (otherwise, it is a message)
		 */
		public boolean isAction()
		{
			return action;
		}

		/**
		 * @return
		 */
		public String getMessage()
		{
			return message;
		}

		/**
		 * @return the channel this input came via, or null if it was sent directly
		 */
		public String getChannel()
		{
			return channel;
		}

		/**
		 * @return the nick of the user that sent this message
		 */
		public String getSender()
		{
			return nick;
		}

		@Override
		public String getNick()
		{
			return me;
		}

		/**
		 * Convenience method for messaging the sender directly
		 * @param message the message text
		 * @see #message(java.lang.String, java.lang.String) message
		 */
		public synchronized void reply(String message)
		{
			message(nick, message);
		}

		/**
		 * <p>Convenience method for sending action to the same scope as this
		 * message arrived</p>
		 * @param action the action text to send
		 * @see #act(java.lang.String, java.lang.String) act()
		 * @see #replyToAll(java.lang.String) replyToAll()
		 */
		public synchronized void act(String action)
		{
			final String target = (this.channel == null ? this.nick : this.channel);
			act(target, action);
		}

		/**
		 * <p>Convenience method for messaging the user or channel this message
		 * was received from</p>
		 * @param message the message text
		 * @see #message(java.lang.String, java.lang.String) message()
		 */
		public synchronized void replyToAll(String message)
		{
			if (channel == null)
			{
				message(nick, message);
			}
			else
			{
				message(channel, message);
			}
		}

		/**
		 * <p>Marks this message as handled</p>
		 * <p>It will be passed to no more Services,
		 * and the messaging and channel functions of this class will perform
		 * no actions.</p>
		 */
		public void dispose()
		{
			dispose = true;
		}

		@Override
		public void message(String target, String message)
		{
			if (dispose) return;
			RelayCat.this.message(target, message);
		}

		@Override
		public void act(String target, String message)
		{
			if (dispose) return;
			RelayCat.this.act(target, nick);
		}

		@Override
		public void join(String channel)
		{
			if (dispose) return;
			RelayCat.this.join(channel);
		}

		@Override
		public void leave(String channel)
		{
			if (dispose) return;
			RelayCat.this.leave(channel);
		}

		@Override
		public User[] names(String channel)
		{
			return RelayCat.this.names(channel);
		}

		@Override
		public String[] channels()
		{
			return RelayCat.this.channels();
		}
	}

	/**
	 * <p>Internal wrapper for {@link PircBot} that allows us to hide much of
	 * the 'functionality'</p>
	 */
	@SuppressWarnings("ProtectedInnerClass")
	protected class CatBot extends PircBot
	{
		/**
		 * Create a new bot with a given name
		 * @param name the name of the bot
		 */
		protected CatBot(String name)
		{
			this.setName(name);
		}

		/**
		 * Unified function for handling input data
		 * @param action whether the input is an action (or a message)
		 * @param sender the nick which sent the input
		 * @param channel the channel the input was received in (or null)
		 * @param data the text of the message
		 */
		@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
		public void onInput(boolean action, String sender, String channel, String data)
		{
			log.log(Level.FINE, "Input recieved from {0} (channel {1})",
			    new Object[] {sender, channel});

			final Message m = new Message(data, sender, channel, action);

			synchronized(srvs)
			{
				for (MessageService s : msrvs)
				{
					log.log(Level.FINE, "Input dispatched to {0}", s.toString());
					try
					{
						s.handle(m);
					}
					catch (Throwable ex)
					{
						log.log(Level.SEVERE, "Error whilst passing input to " + s.toString(), ex);
					}
					if (m.dispose) break;
				}
			}
		}

		@Override
		public void onMessage(String channel, String sender, String login, String hostname, String message)
		{
			onInput(false, sender, channel, message);
		}

		@Override
		public void onPrivateMessage(String sender, String login, String hostname, String message)
		{
			onInput(false, sender, null, message);
		}

		@Override
		public void onAction(String sender, String login, String hostname, String target, String action)
		{
			onInput(true, sender, (target.equals(getNick()) ? null : target), action);
		}
	}

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
	private final CatBot bot;

	/**
	 * <p>Creates a RelayCat instance</p>
	 * <p>The instance is initialised, and services can be added, but does not
	 * connect to the server specified in host until it is run, either by
	 * calling the {@link #run() run} method directly, or executing it in a new
	 * {@link Thread} with:
	 * <pre>    new Thread(RelayCat).start();</pre>
	 * </p>
	 * <p>A list of channels can be supplied to the constructor so that they
	 * are joined when the server connection is made. Other channels can be
	 * joined later with {@link IRelayCat#join(java.lang.String)}
	 * @todo Make a defensive copy of the channel list to prevent a concurrent
	 * modification error if a new channel is added whilst they're being
	 * processed in run
	 * @param name the name for the bot
	 * @param host the host to connect to
	 * @param channels a list of channels to connect to as soon as a connection
	 * is established
	 * @throws IllegalArgumentException if the name or host are not supplied
	 */
	public RelayCat(final String name, final String host, final List<String> channels)
	{
		if (name==null || name.length()==0) throw new IllegalArgumentException("Name must be a non-empty String");
		if (host==null) throw new IllegalArgumentException("Host must be supplied");

		bot = new CatBot(name);

		this.host = host;

		if (channels == null)
		{
			this.channels = new ArrayList<String>(0);
		}
		else
		{
			this.channels = channels;
		}
		bot.setVerbose(false);
	}

	/**
	 * <p>Adds a service to the RelayCat</p>
	 * <p>{@link MessageService Message Services} will be forwarded inputs</p>
	 * <p>{@link ExternalService External Services} will be correctly
	 * initialised, and their threads started</p>
	 * <p>Note that {@link Service Services} that do not fall into one of the
	 * two above categories will have to be supplied with access to the {@link
	 * IRelayCat interface} through external code; this behaviour is not
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
	 * <pre>    new Thread(RelayCat).start();</pre></p>
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
		}

		// Shutdown procedure :)
		synchronized(srvs)
		{
			dispose = true;
			for (Service s : srvs) s.shutdown();
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
}

