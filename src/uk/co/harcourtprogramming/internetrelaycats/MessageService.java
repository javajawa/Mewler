package uk.co.harcourtprogramming.internetrelaycats;

public abstract class MessageService extends Service
{
	abstract protected void handle(RelayCat.Message m);
}

