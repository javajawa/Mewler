package uk.co.harcourtprogramming.mewler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import uk.co.harcourtprogramming.logging.LogDecorator;
import uk.co.harcourtprogramming.mewler.servermesasges.AbstractIrcMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPingMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPongMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPrivmsg;

class IrcIncomingThread extends Thread
{
	private final static LogDecorator LOG = LogDecorator.getLogger("Mewler");
	private volatile boolean died = false;

	protected final IrcConnection outer;
	protected final BufferedReader inputStream;
	protected final TimeoutThread timeout;

	private final class TimeoutThread extends Thread
	{
		private final long timeout;
		private TimeoutThread(ThreadGroup tg, long timeout)
		{
			super(tg, "InternetRelayCats.Mewler-Input-Timeout-Thread");
			this.setDaemon(true);
			this.timeout = timeout;
		}

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					Thread.sleep(timeout);
					IrcIncomingThread.this.interrupt();
				}
				catch (InterruptedException ex)
				{
					// Timer reset.
				}
			}
		}
	}

	protected IrcIncomingThread(BufferedReader inputStream, IrcConnection outer)
	{
		this(inputStream, null, outer);
	}

	protected IrcIncomingThread(BufferedReader inputStream, ThreadGroup tg, IrcConnection outer)
	{
		super(tg, "InternetRelayCats.Mewler-Input-Thread");
		this.outer = outer;
		this.inputStream = inputStream;
		this.timeout = new TimeoutThread(tg, 120000);
	}

	protected final boolean isDead()
	{
		return died || this.getState() == State.TERMINATED;
	}

	@Override
	public void run()
	{
		timeout.start();
		try
		{
			while (true)
			{
				String s = inputStream.readLine();
				if (s == null) break;
				AbstractIrcMessage mess = AbstractIrcMessage.parse(s, outer.getNick());
				LOG.finer(">> {0}", mess.toString());

				if (mess instanceof IrcPingMessage)
				{
					timeout.interrupt();
					outer.onPing((IrcPingMessage)mess);
				}
				else if (mess instanceof IrcPongMessage)
				{
					timeout.interrupt();
					outer.onPong((IrcPongMessage)mess);
				}
				else if (mess instanceof IrcPrivmsg)
				{
					final IrcPrivmsg privateMessage = (IrcPrivmsg)mess;
					if (privateMessage.action)
					{
						outer.onAction(privateMessage.origin.nick, privateMessage.origin, privateMessage.channel, privateMessage.message);
					}
					else
					{
						outer.onMessage(privateMessage.origin.nick, privateMessage.origin, privateMessage.channel, privateMessage.message);
					}
				}
			}
		}
		catch (SocketException ex)
		{
			LOG.severe(ex, "Socket Exception - Possible Remote Disconnection");
		}
		catch (IOException ex)
		{
			LOG.severe(ex, null);
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ex)
			{
				LOG.severe(ex, "Error whilst closing input stream");
			}
			finally
			{
				died = true;
				if (outer.isAlive())
				{
					outer.onDisconnect();
					outer.dispose();
				}
			}
		}
	}
}
