package uk.co.harcourtprogramming.internetrelaycats;

/**
 * <p>Class for easily dealing with irc messages</p>
 */
public class MessageTokeniser implements CharSequence
{
	private final String original;
	private final int originalLength;
	private boolean consumeWhitespace;
	private int offset = 0;

	public MessageTokeniser(String original)
	{
		if (original == null) throw new IllegalArgumentException("Input string much not be null");
		this.original = original;
		this.originalLength = original.length();
	}

	@Override
	public synchronized int length()
	{
		return originalLength - offset;
	}

	@Override
	public synchronized char charAt(int index)
	{
		return original.charAt(index+offset);
	}

	@Override
	public synchronized CharSequence subSequence(int start, int end)
	{
		return original.subSequence(start+offset, end+offset);
	}

	public synchronized void consumeWhitespace()
	{
		while (offset < originalLength && (original.charAt(offset) == ' ' || original.charAt(offset) == '\t')) ++offset;
	}

	public synchronized String nextToken(char delim)
	{
		if (offset >= originalLength) return null;

		int newOffset = original.indexOf(delim, offset);
		String token;
		if (newOffset == -1)
		{
			token = original.substring(offset, originalLength);
			offset = originalLength;
		}
		else
		{
			token = original.substring(offset, newOffset);
			offset = newOffset + 1;
		}
		if (consumeWhitespace) consumeWhitespace();

		return token;
	}

	public synchronized void consume(String token)
	{
		if (original.startsWith(token, offset))
		{
			offset += token.length();
			if (consumeWhitespace) consumeWhitespace();
		}
		else
		{
			// TODO: What should be do here?
		}
	}

	public synchronized boolean getConsumeWhitespace()
	{
		return consumeWhitespace;
	}

	public synchronized void setConsumeWhitespace(boolean consumeWhitespace)
	{
		this.consumeWhitespace = consumeWhitespace;
	}

	public synchronized boolean startsWith(String token)
	{
		return original.startsWith(token, offset);
	}

	@Override
	public synchronized String toString()
	{
		return original.substring(offset);
	}
}
