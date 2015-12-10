package uk.co.harcourtprogramming.mewler;

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
		if (index < 0) throw new StringIndexOutOfBoundsException(index);
		return original.charAt(index+offset);
	}

	@Override
	public synchronized CharSequence subSequence(int start, int end)
	{
		if (start < 0 || end < 0) throw new StringIndexOutOfBoundsException();
		return original.subSequence(start+offset, end+offset);
	}

	public synchronized void consumeWhitespace()
	{
		while (!isEmpty() &&(Character.isWhitespace(original.charAt(offset))))
			++offset;
	}

	public synchronized int indexOfWhitespace()
	{
		int myOffset = offset;
		while (myOffset < originalLength)
		{
			if (Character.isWhitespace(original.charAt(myOffset))) return myOffset;
			else ++myOffset;
		}
		return -1;
	}

	/**
	 * Returns next whitespace-delimited token and consumes remaining whitespace
	 * @return next token
	 */
	public synchronized String nextToken()
	{
		if (isEmpty()) return null;

		int newOffset = this.indexOfWhitespace();
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
		consumeWhitespace();

		return token;
	}

	public synchronized String nextToken(char delim)
	{
		if (isEmpty()) return null;

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

	public synchronized String nextTokenWithDelim(String delim)
	{
		if (isEmpty() || delim == null) return null;

		int newOffset = original.indexOf(delim, offset);
		String token;
		if (newOffset == -1)
		{
			token = original.substring(offset, originalLength);
			offset = originalLength;
		}
		else
		{
			newOffset += delim.length();
			token = original.substring(offset, newOffset);
			offset = newOffset;
		}
		if (consumeWhitespace) consumeWhitespace();

		return token;
	}

	/**
	 * <p>Attempt to consume a particular token</p>
	 * <p>If the remaining string starts with the supplied token, the position
	 * offset is moved over it. If {@link #consumeWhitespace} is set, any
	 * following whitespace is also consumed. If the token is not found at the
	 * start of the string, state is not changed and the function returns false.
	 * A null <code>token</code> value is treated as an empty string; both will
	 * return true without changing state.</p>
	 * @param token The token to consume
	 * @return Whether the token was found and consumed
	 */
	public synchronized boolean consume(String token)
	{
		if (token == null) return true;
		if (original.startsWith(token, offset))
		{
			offset += token.length();
			if (consumeWhitespace) consumeWhitespace();
			return true;
		}
		return false;
	}

	public synchronized boolean getConsumeWhitespace()
	{
		return consumeWhitespace;
	}

	public synchronized void setConsumeWhitespace(boolean consumeWhitespace)
	{
		this.consumeWhitespace = consumeWhitespace;
		if (consumeWhitespace) consumeWhitespace();
	}

	public synchronized boolean startsWith(String token)
	{
		if (token == null) return false;
		return original.startsWith(token, offset);
	}

	@Override
	public synchronized String toString()
	{
		return original.substring(offset);
	}

	public synchronized boolean isEmpty()
	{
		return (offset >= originalLength);
	}
}
