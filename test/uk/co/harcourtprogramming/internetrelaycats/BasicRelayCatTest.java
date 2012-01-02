package uk.co.harcourtprogramming.internetrelaycats;

import java.net.UnknownHostException;
import org.junit.Test;
import static org.junit.Assert.*;

public class BasicRelayCatTest
{
	/**
	 * Blank public test constructor
	 */
	public BasicRelayCatTest()
	{
		// Nothing to see here. Move along, citizen!
	}

	/**
	 * Nickname to use when testing
	 */
	private final static String NICK = "InternetRelayCats-test";
	/**
	 * Host to connect to when testing
	 */
	private final static String HOST = "irc.esper.net";

	/**
	 * Test of shutdown method, of class BasicRelayCat.
	 *
	 * @throws Throwable
	 */
	@Test(expected=ShutdownWasCalledException.class)
	public void testShutdown() throws Throwable
	{
		BasicRelayCat instance = new BasicRelayCat(NICK, "localhost", null);
		instance.addService(new ShutdownTestSerivce());

		new Thread(instance).start();

		try
		{
			instance.shutdown();
		}
		catch (RuntimeException ex)
		{
			throw ex.getCause();
		}
	}

	/**
	 * Test of getNick method, of class BasicRelayCat.
	 */
	@Test
	public void testGetNick() throws UnknownHostException
	{
		BasicRelayCat instance = new BasicRelayCat(NICK, "localhost", null);
		// Until a connection is made, the Bot will return PircBot.
		// This test only checks correct forwarding
		// TODO: Update notes
		assertEquals(null, instance.getNick());
	}
}
