package uk.co.harcourtprogramming.internetrelaycats;

/**
 * <p>Interface that allows a {@link Service} to act as an outbound message
 * filter</p>
 */
public interface FilterService
{
	/**
	 * <p>Filter an outbound message</p>
	 * @param m The pre-filtered message
	 * @return The filtered message, or null to cancel message sending
	 */
	public OutboundMessage filter(final OutboundMessage m);
}
