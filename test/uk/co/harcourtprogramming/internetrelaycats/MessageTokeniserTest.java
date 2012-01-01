package uk.co.harcourtprogramming.internetrelaycats;

import static org.junit.Assert.*;
import org.junit.Test;

public class MessageTokeniserTest
{
	public MessageTokeniserTest()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Test
	public void testLengthBasic()
	{
		final String testStr = "Hello world";
		MessageTokeniser instance = new MessageTokeniser(testStr);
		int expResult = testStr.length();
		int result = instance.length();

		assertEquals(expResult, result);
	}

	@Test
	public void testCharAtBasic()
	{
		MessageTokeniser instance = new MessageTokeniser("a b");

		assertEquals('a', instance.charAt(0));
		assertEquals(' ', instance.charAt(1));
		assertEquals('b', instance.charAt(2));
	}

	/**
	 * Test of subSequence method, of class MessageTokeniser.
	 */
	@Test
	public void testSubSequenceBasic()
	{
		final String testStr = "Hello world";
		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals(testStr, instance.subSequence(0, instance.length()));
		assertEquals(testStr.subSequence(2, 5), instance.subSequence(2, 5));
	}

	/**
	 * Test of consumeWhitespace method, of class MessageTokeniser.
	 */
	@Test
	public void testConsumeWhitespace()
	{
		MessageTokeniser instance = new MessageTokeniser("   \t\t a");
		instance.consumeWhitespace();

		assertEquals(1, instance.length());
		assertEquals('a', instance.charAt(0));
	}

	/**
	 * Test of nextToken method, of class MessageTokeniser.
	 */
	@Test
	public void testNextToken()
	{
		final String testStr = "bob ben";
		final char delim = ' ';
		final String expResult1 = "bob";
		final String expResult2 = "ben";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals(expResult1, instance.nextToken(delim));
		assertEquals(expResult2, instance.nextToken(delim));
	}

	/**
	 * Test of consume method, of class MessageTokeniser.
	 */
	@Test
	public void testConsumeBasic()
	{
		final String testStr = "Hello world";
		final String token = testStr.substring(0, 4);
		final String remainder = testStr.substring(4);

		MessageTokeniser instance = new MessageTokeniser(testStr);
		instance.consume(token);

		assertEquals(remainder, instance.toString());
	}

	@Test
	public void testConsumeTwice()
	{
		final String testStr = "Hello world";
		final String token1 = testStr.substring(0, 4);
		final String token2 = testStr.substring(4).substring(0, 3);
		final String remainder = testStr.substring(4).substring(3);

		MessageTokeniser instance = new MessageTokeniser(testStr);
		instance.consume(token1);
		assertEquals(token2 + remainder, instance.toString());
		instance.consume(token2);
		assertEquals(remainder, instance.toString());
	}

	/**
	 * Test of startsWith method, of class MessageTokeniser.
	 */
	@Test
	public void testStartsWithConsume()
	{
		final String testStr = "Hello world";
		final String token1 = testStr.substring(0, 4);
		final String token2 = testStr.substring(4).substring(0, 3);

		MessageTokeniser instance = new MessageTokeniser(testStr);
		assertTrue(instance.startsWith(token1));
		instance.consume(token1);
		assertTrue(instance.startsWith(token2));
	}

	@Test
	public void testStartsWith()
	{
		final String testStr = "Hello world";
		final String token = testStr.substring(0, 4);

		MessageTokeniser instance = new MessageTokeniser(testStr);
		assertTrue(instance.startsWith(token));
	}
	/**
	 * Test of toString method, of class MessageTokeniser.
	 */
	@Test
	public void testToStringBasic()
	{
		final String testStr = "Hello world";
		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals(testStr, instance.toString());
	}

	@Test
	public void testToStringBasicConsume()
	{
		final String testStr = "Hello world";
		final String token = testStr.substring(0, 4);
		final String remainder = testStr.substring(4);

		MessageTokeniser instance = new MessageTokeniser(testStr);
		instance.consume(token);

		assertEquals(remainder, instance.toString());
	}

	@Test public void testTokenise()
	{
		final String testStr = "Hello World";
		final String token1 = "Hello";
		final String token2 = "World";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals(token1, instance.nextToken(' '));
		assertEquals(token2, instance.nextToken(' '));
	}


	@Test
	public void testTokeniseOnEmptyString()
	{
		final String testStr = "";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals((String)null, instance.nextToken(' '));
	}

	@Test
	public void testWhiteSpaceConsume()
	{
		final String testStr = "Hello   \t World";
		final String token1 = "Hello";
		final String token2 = "World";

		MessageTokeniser instance = new MessageTokeniser(testStr);
		instance.setConsumeWhitespace(true);

		assertEquals(token1, instance.nextToken(' '));
		assertEquals(token2, instance.nextToken(' '));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConsturctorOnNullString()
	{
		MessageTokeniser instance = new MessageTokeniser(null);
	}
}
