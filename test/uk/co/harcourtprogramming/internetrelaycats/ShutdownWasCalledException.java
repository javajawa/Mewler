package uk.co.harcourtprogramming.internetrelaycats;

/**
 * Thrown in {@link ShutdownTestSerivce} when the shutdown method is called.
 * Due to the lack of return type, etc, the Exceptional programming pattern
 * is use to check that the method is invoked
 */
public class ShutdownWasCalledException extends Exception
{
	private static final long serialVersionUID = 1L;

	public ShutdownWasCalledException()
	{
	}

}
