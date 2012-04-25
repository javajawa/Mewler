package uk.co.harcourtprogramming.mewler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import uk.co.harcourtprogramming.internetrelaycats.MessageTokeniser;
import uk.co.harcourtprogramming.logging.LogDecorator;
/**
 *
 * @author Benedict
 */
public class IrcOutgoingThread extends Thread
{
	private final static LogDecorator LOG = LogDecorator.getLogger("Mewler");

	private final OutputStream outputStream;
	private final BlockingQueue<OutQueueMessage> messageQueue =
		new PriorityBlockingQueue<OutQueueMessage>(50);
	private volatile long messageDelayMillis = 500;
	private volatile long lineDelayMillis = 100;

	public IrcOutgoingThread(OutputStream out)
	{
		this(out, null);
	}

	public IrcOutgoingThread(OutputStream out, ThreadGroup tg)
	{
		super(tg, "InternetRelayCats.Mewler-Output-Thread");
		this.outputStream = out;
	}

	public void queue(String message, int priority)
	{
		messageQueue.add(new OutQueueMessage(priority, message));
	}

	public void queue(String message)
	{
		queue(message, 0);
	}

	@SuppressWarnings("SleepWhileHoldingLock") // Make sure messages are kept together
	public synchronized void send(String message) throws IOException
	{
		if (message == null) return;
		MessageTokeniser splitter = new MessageTokeniser(message);

		while (true)
		{
			String line = splitter.nextTokenWithDelim("\r\n");
			LOG.finer("<< {0}", line.substring(0, line.length() - 2));
			outputStream.write(line.getBytes());
			outputStream.flush();
			if (splitter.isEmpty()) break;
			try
			{
				Thread.sleep(lineDelayMillis);
			}
			catch (InterruptedException ex)
			{
			}
		}
	}

	@Override
	public void run()
	{
		try
		{
			while(!interrupted())
			{
				Thread.sleep(messageDelayMillis);
				OutQueueMessage messageToSend = messageQueue.take();
				send(messageToSend.message);
			}
		}
		catch (InterruptedException ex)
		{
		}
		catch (IOException ex)
		{
			LOG.severe("Error in Thread" + getName(), ex);
		}
		finally
		{
			LOG.fine("{0} closing", getName());
			try
			{
				outputStream.close();
			}
			catch (IOException ex)
			{
				LOG.severe("Error in Thread" + getName(), ex);
			}
		}

	}

}
