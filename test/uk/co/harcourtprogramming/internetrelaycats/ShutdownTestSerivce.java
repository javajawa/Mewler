package uk.co.harcourtprogramming.internetrelaycats;

/**
 * Simple Service that throws a {@link ShutdownWasCalledException}, wrapped in
 * a Run when
 * shutdown is called on it.
 */
public class ShutdownTestSerivce extends Service
{

	@Override
	protected void startup(RelayCat r)
	{
		// Nothing to see here. Move along, citizen!
	}

	@Override
	public void shutdown()
	{
		throw new RuntimeException(new ShutdownWasCalledException());
	}

}
