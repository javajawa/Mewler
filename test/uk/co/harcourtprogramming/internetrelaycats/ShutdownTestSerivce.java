package uk.co.harcourtprogramming.internetrelaycats;

/**
 * Simple Service that throws a {@link ShutdownWasCalledException}, wrapped in
 * a Run when
 * shutdown is called on it.
 */
public class ShutdownTestSerivce extends Service
{

	@Override
	public void shutdown()
	{
		throw new RuntimeException(new ShutdownWasCalledException());
	}

}
