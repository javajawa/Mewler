package uk.co.harcourtprogramming.internetrelaycats;

/**
 *
 * @author Benedict
 */
public interface IRelayCat
{
	public void message(String target, String message);
	public void act(String target, String message);
	public void join(String channel);
	public void leave(String channel);
}
