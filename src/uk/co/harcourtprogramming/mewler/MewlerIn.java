package uk.co.harcourtprogramming.mewler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.co.harcourtprogramming.mewler.servermesasges.AbstractIrcMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPingMessage;
import uk.co.harcourtprogramming.mewler.servermesasges.IrcPrivmsg;

class MewlerIn extends Thread
{
	private final static Logger LOG = Logger.getLogger("InternetRelatCats.Mewler");
	private volatile boolean died = false;

	protected final Mewler outer;
	protected final BufferedReader inputStream;

	protected MewlerIn(BufferedReader inputStream, Mewler outer)
	{
		this(inputStream, null, outer);
	}

	protected MewlerIn(BufferedReader inputStream, ThreadGroup tg, Mewler outer)
	{
		super(tg, "InternetRelayCats.Mewler-Input-Thread");
		this.outer = outer;
		this.inputStream = inputStream;
	}

	protected final boolean isDead()
	{
		return died;
	}

	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				String s = inputStream.readLine();
				if (s == null) break;
				AbstractIrcMessage mess = AbstractIrcMessage.parse(s, outer.getNick());
				LOG.log(Level.FINER, ">> {0}", mess.toString());

				if (mess instanceof IrcPingMessage)
				{
					outer.onPing((IrcPingMessage)mess);
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
		catch (IOException ex)
		{
			LOG.log(Level.SEVERE, null, ex);
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ex)
			{
				LOG.log(Level.SEVERE, "Error whilst closing input stream", ex);
			}
			finally
			{
				died = true;
				outer.dispose();
			}
		}
	}
}
