package uk.co.harcourtprogramming.mewler;

import java.io.Serializable;

/**
 *
 */
class OutQueueMessage implements Serializable, Comparable<OutQueueMessage>
{
	private final static long serialVersionUID = 1L;

	public final int priority;
	public final long timestamp;
	public final String message;

	public OutQueueMessage(final int priority, final String message)
	{
		this.priority = priority;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public int compareTo(final OutQueueMessage o)
	{
		if (this == o) return 0;
		if (priority == o.priority)
		{
			if (timestamp == o.timestamp)
				return message.compareTo(o.message);

			return timestamp > o.timestamp ? 1 : -1;
		}
		return priority > o.priority ? -1 : 1;
	}

}
