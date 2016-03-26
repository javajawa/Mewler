package uk.co.harcourtprogramming.mewler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic test of the connection transaction for Mewler
 *
 * @author Benedict
 */
public class MewlerTest
{

	/**
	 * Test of connect method, of class IrcConnection.
	 *
	 * @throws java.lang.Exception If an error occurs
	 */
	@Test(timeout=8000)
	public void testConnect() throws Exception
	{
		final String nick = "bob";
		final String password = "pass";
		final String realName = "Bobby";

		final LocalBufferedSocket inputSocket = new LocalBufferedSocket();
		final LocalBufferedSocket outputSocket = new LocalBufferedSocket();

		final LocalBufferedSocket.LineReader fromClient = outputSocket.in;
		final BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(inputSocket.out));

		final IrcConnection instance = new IrcConnection(inputSocket.in, outputSocket.out, null);
		final Thread main = Thread.currentThread();

		final Thread testHolder = new Thread()
		{
			private Exception e;

			@Override
			public void run()
			{
				try
				{
					instance.connect(nick, password, realName);
				}
				catch (IOException ex)
				{
					e = ex;
				}
				finally
				{
					synchronized (main)
					{
						main.notifyAll();
					}
				}
			}

			@Override
			public ClassLoader getContextClassLoader()
			{
				if (e != null)
				{
					throw new Error(e);
				}
				return super.getContextClassLoader();
			}
		};

		toClient.append(":localhost 375 :- <server> Message of the day - \r\n");
		toClient.flush();

		synchronized (main)
		{
			testHolder.start();
			main.wait();
		}

		testHolder.getContextClassLoader();
		String line;

		line = fromClient.readLine();
		assertNotNull(line);
		assertEquals(IrcCommands.createCommandString(IrcCommands.PASS, password), line + "\r\n");

		line = fromClient.readLine();
		assertNotNull(line);
		assertEquals(IrcCommands.createCommandString(IrcCommands.NICK, nick), line + "\r\n");

		line = fromClient.readLine();
		assertNotNull(line);
		assertEquals(IrcCommands.createCommandString(IrcCommands.USER, nick, 0, "*", realName), line + "\r\n");

		assertFalse(testHolder.isAlive());

		instance.dispose();
	}

}
