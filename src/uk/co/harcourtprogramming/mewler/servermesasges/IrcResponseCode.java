package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.internetrelaycats.MessageTokeniser;

public class IrcResponseCode extends AbstractIrcMessage
{
	private final IrcResponseCodeEnum code;
	private final int numericCode;
	private final String payload;


	IrcResponseCode(String origin, int replyCode, MessageTokeniser payload)
	{
		super(origin);
		this.numericCode = replyCode;
		this.code = IrcResponseCodeEnum.getByCode(replyCode);
		this.payload = payload.toString();
	}

	@Override
	public String toString()
	{
		if (getCode() == IrcResponseCodeEnum.UNKNOWN)
			return "UNKNOWN(" + getNumericCode() + "): " + getPayload();

		return getCode().toString() + ": " + getPayload();
	}

	/**
	 * @return the code
	 */
	public IrcResponseCodeEnum getCode()
	{
		return code;
	}

	/**
	 * @return the numericCode
	 */
	public int getNumericCode()
	{
		return numericCode;
	}

	/**
	 * @return the payload
	 */
	public String getPayload()
	{
		return payload;
	}
}
