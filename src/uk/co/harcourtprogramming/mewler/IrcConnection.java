package uk.co.harcourtprogramming.mewler;

import uk.co.harcourtprogramming.mewler.servermesasges.IrcMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.AbstractIrcMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import uk.co.harcourtprogramming.logging.LogDecorator;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPingMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPongMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcResponseCode;
import uk.co.harcourtprogramming.mewler.servermesasges.User;

/**
 *
 */
public class IrcConnection
{
	private final static LogDecorator LOG = LogDecorator.getLogger("Mewler");

	private final IrcOutgoingThread outputThread;
	private final IrcIncomingThread inputThread;
	private final IrcPingThread pingThead;
	private String nick = null;

	public IrcConnection(final InputStream input, final OutputStream output, final ThreadGroup tg)
	{
		if (input == null) throw new IllegalArgumentException("InputStream must be a valid, active stream");
		try
		{
			input.available();
		}
		catch (IOException ex)
		{
			throw new IllegalArgumentException("InputStream must be a valid, active stream");
		}

		if (input == null) throw new IllegalArgumentException("InputStream must be a valid, active stream");
		try
		{
			input.available();
		}
		catch (IOException ex)
		{
			throw new IllegalArgumentException("InputStream must be a valid, active stream");
		}

		outputThread = new IrcOutgoingThread(output, tg);
		inputThread = new IrcIncomingThread(new BufferedReader(new InputStreamReader(input)), tg, this);
		pingThead = new IrcPingThread(tg, this, outputThread);
	}

	public synchronized void connect(final String nick, final String password, final String realName) throws IOException
	{
		if (inputThread.isAlive() && !inputThread.isDead())
			return; // TODO: Do we want to throw an exception here

		if (nick == null || !nick.matches("[\\w<\\-\\[\\]\\^{}]+"))
		{
			throw new IllegalArgumentException("Supplied nick is null or not a valid nickname");
		}

		if (password != null && password.length() != 0)
		{
			String passCommand = IrcCommands.createCommandString(
				IrcCommands.PASS, password);
			outputThread.send(passCommand);
		}

		String commandString;
		int nicksTried = 0;

		BufferedReader inputFromIrc = inputThread.inputStream;

		// Try and register a nick name
		commandString = IrcCommands.createCommandString(IrcCommands.NICK, nick);
		outputThread.send(commandString);
		String currNick = nick;

		// Send the user data
		commandString = IrcCommands.createCommandString(IrcCommands.USER,
			nick, 0, "*", realName == null ? "Mewler-Bot" : realName);
		outputThread.send(commandString);

		while(true)
		{
			String line = inputFromIrc.readLine();

			if (line == null)
			{
				dispose();
				throw new IOException("Error whilst trying to connect: null message");
			}

			AbstractIrcMessage mess = AbstractIrcMessage.parse(line, currNick);

			if (mess instanceof IrcPingMessage)
			{
				LOG.finer(">> {0}", mess.toString());
				outputThread.send(((IrcPingMessage)mess).reply());
			}
			else if (mess instanceof IrcMessage)
			{
				final IrcMessage message = (IrcMessage)mess;
				LOG.finer(">> {0}", mess.toString());
				if (message.getMessageType().equals("ERROR"))
				{
					dispose();
					throw new IOException("Error whilst trying to connect: " + message.getPayload());
				}
			}
			else if (mess instanceof IrcResponseCode)
			{
				final IrcResponseCode message = (IrcResponseCode)mess;

				switch (message.getCode())
				{
					case ERR_NICKNAMEINUSE:
					case ERR_NICKCOLLISION:
						LOG.fine("Nick in use, trying again");
						currNick = nick + "-" + nicksTried;
						commandString = IrcCommands.createCommandString(IrcCommands.NICK, currNick);
						outputThread.send(commandString);
						break;
					case RPL_MOTDSTART:
					case RPL_MOTD:
						LOG.fine("Sucessfully connected");
						this.nick = currNick;
						inputThread.start();
						outputThread.start();
						pingThead.start();
						return;

					default:
						LOG.finer(">> {0}", mess.toString());
						// TODO: do something with messages of other codes
				}
			}
			else
			{
				LOG.finer(">> {0}", mess.toString());
			}
		}
	}

	public void message(final String target, final String message)
	{
		StringBuilder rawMessage = new StringBuilder(100 + message.length());

		for (String line : message.split("[\r\n]+"))
		{
			rawMessage.append(IrcCommands.createCommandString(IrcCommands.MESS,
				target, line));
		}
		outputThread.queue(rawMessage.toString());
	}

	public void act(final String target, final String message)
	{
		String mess = IrcCommands.createCommandString(IrcCommands.ACTION,
			target, message.split("[\r\n]+")[0]);

		outputThread.queue(mess);
	}

	public void join(String channel)
	{
		String command = IrcCommands.createCommandString(IrcCommands.JOIN, channel);
		outputThread.queue(command);
	}

	public void part(String channel)
	{
		String command = IrcCommands.createCommandString(IrcCommands.PART, channel);
		outputThread.queue(command);
	}

	public void quit()
	{
		String command = IrcCommands.createCommandString(IrcCommands.QUIT, "Mewler");
		outputThread.queue(command);
	}

	@Override
	protected void finalize() throws Throwable
	{
		dispose();
		super.finalize();
	}

	public void dispose()
	{
		outputThread.interrupt();
		inputThread.interrupt();
		pingThead.dispose();
	}

	public String getNick()
	{
		return nick;
	}

	protected void onMessage(String nick, User sender, String channel, String message)
	{
		// Nothing to see here. Move along, citizen!
	}

	protected void onAction(String nick, User sender, String channel, String action)
	{
		// Nothing to see here. Move along, citizen!
	}

	protected void onDisconnect()
	{
		// Nothing to see here. Move along, citizen!
	}

	protected void onPing(IrcPingMessage ping)
	{
		try
		{
			outputThread.send(ping.reply());
		}
		catch (IOException ex)
		{
			LOG.severe("Exception when replying to PING", ex);
		}
	}

	protected void onPong(IrcPongMessage pong)
	{
		pingThead.onPong(pong);
	}

	protected boolean isAlive()
	{
		return (inputThread.isAlive() || outputThread.isAlive());
	}
}
