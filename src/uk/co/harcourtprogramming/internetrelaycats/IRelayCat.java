package uk.co.harcourtprogramming.internetrelaycats;
import org.jibble.pircbot.User;
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
	public String getNick();
	public User[] names(String channel);
	public String[] channels();
}
