package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.internetrelaycats.MessageTokeniser;

public class User
{
	public final String nick;
	public final String user;
	public final String host;

	public User(String input)
	{
		MessageTokeniser tokeniser = new MessageTokeniser(input);
		nick = tokeniser.nextToken('!');
		if (tokeniser.startsWith("~"))
		{
			tokeniser.consume("~");
			user = tokeniser.nextToken('@');
		}
		else
		{
			user = null;
		}
		if (!tokeniser.isEmpty())
		{
			host = tokeniser.toString();
		}
		else
		{
			host = null;
		}
	}

	public String getNick()
	{
		return nick;
	}

	public String getUser()
	{
		return user;
	}

	public String getHost()
	{
		return host;
	}

}
