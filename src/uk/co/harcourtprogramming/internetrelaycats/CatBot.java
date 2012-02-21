package uk.co.harcourtprogramming.internetrelaycats;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import uk.co.harcourtprogramming.mewler.Mewler;
import uk.co.harcourtprogramming.mewler.servermesasges.User;

/**
 * <p>Internal wrapper for {@link PircBot} that allows us to hide much of
 * the 'functionality'</p>
 */
public class CatBot extends Mewler
{

	private final BasicRelayCat inst;

	/**
	 * Shared logger with {@link BasicRelayCat}
	 */
	private final static Logger log = Logger.getLogger("IntertnetRelayCat");

	public static CatBot create(BasicRelayCat inst, String host, int port, boolean ssl) throws UnknownHostException, IOException
	{
		Socket ircSocket = ssl ? SSLSocketFactory.getDefault().createSocket(host,port) : new Socket(host, port);
		return new CatBot(
			inst,
			ircSocket,
			new ThreadGroup("Mewler")
		);
	}

	protected CatBot(BasicRelayCat inst, InputStream i, OutputStream o, ThreadGroup threadGroup)
	{
		super(i, o, threadGroup);
		this.inst = inst;
	}

	protected CatBot(BasicRelayCat inst, Socket sock, ThreadGroup threadGroup) throws IOException
	{
		this(inst, sock.getInputStream(), sock.getOutputStream(), threadGroup);
	}

	/**
	 * Unified function for handling input data
	 * @param action whether the input is an action (or a message)
	 * @param sender the nick which sent the input
	 * @param channel the channel the input was received in (or null)
	 * @param data the text of the message
	 */
	public void onInput(boolean action, String sender, String channel,
		String data)
	{
		onInput(new Message(data, sender, channel, action, inst));
	}

	public void onInput(Message m)
	{
		log.log(Level.FINE, "Input recieved: {0}", m);
		if (inst == null) return;
		synchronized (inst.getSrvs())
		{
			for (MessageService s : inst.getMsrvs())
			{
				log.log(Level.FINE, "Input dispatched to {0}",
					s.toString());
				try
				{
					s.handle(m);
				}
				catch (Throwable ex)
				{
					log.log(Level.SEVERE,
						"Error whilst passing input to " + s.toString(), ex);
				}
				if (m.isDisposed()) break;
			}
		}
	}

	@Override
	protected void onMessage(String nick, User sender, String channel, String message)
	{
		onInput(true, nick, channel, message);
	}

	@Override
	protected void onAction(String nick, User sender, String channel, String action)
	{
		onInput(true, nick, channel, action);
	}

}
