package uk.co.harcourtprogramming.mewler;

public enum IrcCommands
{
	PASS("PASS %s\r\n"),
	NICK("NICK %s\r\n"),
	/**
	 * Command:	USER
	 * Parameters: &gt;username&lt; &gt;hostname&lt; &gt;servername&lt; &gt;realname&lt;
	 */
	USER("USER %s %s %s :%s\r\n"),
	PONG("PONG :%s\r\n"),
	JOIN("JOIN %s\r\n"),
	JOIN_PASS("JOIN %s %s\r\n"),
	PART("PART %s\r\n"),
	MESS("PRIVMSG %s :%s\r\n"),
	ACTION("PRIVMSG %s :\u0001ACTION %s\u0001\r\n"),
	QUIT("QUIT :%s\r\n");

	private final String format;

	private IrcCommands(String format)
	{
		this.format = format;
	}

	public String getFormat()
	{
		return format;
	}

	public static String createCommandString(IrcCommands command, Object... params)
	{
		return String.format(command.getFormat(), params);
	}

	public static String createCommandString(String nick, IrcCommands command, Object... params)
	{
		return ':' + nick + ' ' + String.format(command.getFormat(), params);
	}

}
