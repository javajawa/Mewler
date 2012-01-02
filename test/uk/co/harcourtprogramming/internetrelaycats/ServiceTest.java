package uk.co.harcourtprogramming.internetrelaycats;

import java.io.IOException;
import java.net.UnknownHostException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Benedict
 */
public class ServiceTest
{
	public ServiceTest()
	{
	}

	/**
	 * Test of getId method, of class Service.
	 */
	@Test
	public void testGetId()
	{
		Service instance = new ServiceImpl();

		@SuppressWarnings("deprecation") // Testing only function
		int expResult = Service.lastId();

		assertEquals(expResult, instance.getId());

		instance = new ServiceImpl();
		assertEquals(expResult+1, instance.getId());

	}

	/**
	 * Test of toString method, of class Service.
	 */
	@Test
	public void testToString()
	{
		Service instance = new ServiceImpl();
		String expResult = "ServiceImpl@" + instance.getId();
		String result = instance.toString();

		assertEquals(expResult, result);
	}

	/**
	 * Test of shutdown method, of class Service.
	 */
	@Test
	public void testShutdown() throws UnknownHostException, IOException
	{
		Service instance = new ServiceImpl();

		TestingRelayCat inst = new TestingRelayCat();
		inst.addService(new ShutdownTestSerivce());
		try
		{
			inst.shutdown();
		}
		catch (RuntimeException ex)
		{
			if (ex.getCause() instanceof ShutdownWasCalledException) return;
			throw ex;
		}
		fail("Shutdown was not called, or ShutdownWasCalledException was not propigated");
	}

	/**
	 * Blank implemetation of the Service class
	 */
	private class ServiceImpl extends Service
	{
		@Override
		public void shutdown()
		{
		}
	}
}
