package uk.co.harcourtprogramming.internetrelaycats;

/**
 * <p>Interface that marks a {@link Service} as designed to process messages</p>
 * <p>The service will get passed all inputs (messages, private messages, actions)
 * and associated metadata that are sent to the bot or channels that the bot is
 * connected to.</p>
 */
public interface MessageService
{
	/**
	 * <p>Handle an input (message, action, etc.)</p>
	 * @param m The message to handle
	 */
	public void handle(RelayCat.Message m);
}

