package uk.co.harcourtprogramming.mewler.servermesasges;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeTrue;

public class IrcResponseCodeEnumTest
{
	/**
	 * Test of getByCode method, of class IrcResponseCodeEnum, with code
	 * 401 (ERR_NOSUCHNICK).
	 */
	@Test
	public void testGetByCode()
	{
		int code = 401;
		IrcResponseCodeEnum expResult = IrcResponseCodeEnum.ERR_NOSUCHNICK;
		
		assumeTrue( IrcResponseCodeEnum.ERR_NOSUCHNICK.number == code );
		
		IrcResponseCodeEnum result = IrcResponseCodeEnum.getByCode( code );

		assertEquals( expResult, result );
	}

	/**
	 * Test of getByCode method, of class IrcResponseCodeEnum, with code
	 * 401 (ERR_NOSUCHNICK).
	 */
	@Test
	public void testGetByInvalidCode()
	{
		int code = -2;

		IrcResponseCodeEnum expResult = IrcResponseCodeEnum.UNKNOWN;
		IrcResponseCodeEnum result = IrcResponseCodeEnum.getByCode( code );

		assertNotEquals( expResult.number, code );
		assertEquals( expResult, result );
	}
	
}
