package uk.co.harcourtprogramming.mewler;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTokeniserTest
{
	public MessageTokeniserTest()
	{
		// Nothing to see here. Move along, citizen!
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructionNull()
	{
		MessageTokeniser instance = new MessageTokeniser(null);
	}

	@Test
	public void testStartsWithNull()
	{
		MessageTokeniser instance = new MessageTokeniser("hello");
		assertFalse(instance.startsWith(null));
	}

	@Test
	public void testStartsWithEmptyString()
	{
		MessageTokeniser instance = new MessageTokeniser("hello");
		assertTrue(instance.startsWith(""));
	}

	@Test
	public void testConsumeNull()
	{
		final String data = "bob";
		MessageTokeniser instance = new MessageTokeniser(data);
		assertTrue(instance.consume(null));
		assertEquals(data, instance.toString());
	}

	@Test
	public void testConsumeEmptyString()
	{
		final String data = "bob";
		MessageTokeniser instance = new MessageTokeniser(data);
		assertTrue(instance.consume(""));
		assertEquals(data, instance.toString());
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testAccessBeforeZero()
	{
		final String data = "bob";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.charAt(-1);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testAccessBeforeShiftedZero()
	{
		final String data = "bob ben";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.nextToken(' ');
		instance.charAt(-1);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testAccessAfterEnd()
	{
		final String data = "bob";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.charAt(4);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testAccessAfterShiftedEnd()
	{
		final String data = "bob ben";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.nextToken(' ');
		instance.charAt(4);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testSubsequenceBeforeZero()
	{
		final String data = "bob";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.subSequence(-1, 2);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testSubsequenceBeforeShiftedZero()
	{
		final String data = "bob ben";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.nextToken(' ');
		instance.subSequence(-1, 2);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testSubsequenceAfterEnd()
	{
		final String data = "bob";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.subSequence(1, 4);
	}

	@Test(expected=StringIndexOutOfBoundsException.class)
	public void testSubsequenceAfterShiftedEnd()
	{
		final String data = "bob ben";
		MessageTokeniser instance = new MessageTokeniser(data);
		instance.nextToken(' ');
		instance.subSequence(1, 4);
	}

	@Test
	public void testTokenWithDelimNull()
	{
		final String data = "bob ben";
		MessageTokeniser instance = new MessageTokeniser(data);
		assertNull(instance.nextTokenWithDelim(null));
	}

	@Test
	public void testTokenWithDelimEmptyString()
	{
		final String data = "bob ben";
		MessageTokeniser instance = new MessageTokeniser(data);
		assertEquals("", instance.nextTokenWithDelim(""));
	}

	@Test
	public void testTokenWithDelimFinalToken()
	{
		final String testStr = "Hello World";
		final String token1 = "Hello ";
		final String token2 = "World";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals(token1, instance.nextTokenWithDelim(" "));
		assertEquals(token2, instance.nextTokenWithDelim(" "));
		assertNull(instance.nextToken());
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

	@Test
	public void testNoConsumeWhitespace()
	{
		final String testStr = "bob ben";
		final String part1 = "bob";
		final String expResult2 = " ben";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertTrue(instance.consume(part1));
		assertEquals(expResult2, instance.toString());
	}

	/**
	 * Tests that get/set ConsumeWhitespace get/set the same variable, that it
	 * defaults to false, and that when it is set to true, whitespace is consumed
	 */
	@Test
	public void testGetSetConsumeWhitespace()
	{
		final String testStr = "bob ben";
		final String part1 = "bob";
		final String expResult1 = " ben";
		final String expResult2 = "ben";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertFalse("Test precondition not met: consumeWhitspace not fasle by default", instance.getConsumeWhitespace());

		assertTrue(instance.consume(part1));
		assertEquals("Test precondition not met.", expResult1, instance.toString());

		instance.setConsumeWhitespace(true);
		assertTrue(instance.getConsumeWhitespace());
		assertEquals(expResult2, instance.toString());
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
	 * Test of nextToken method, of class MessageTokeniser.
	 */
	@Test
	public void testNextTokenAutoDelimWsConsume()
	{
		final String testStr = "bob\t ben";
		final char delim = ' ';
		final String expResult1 = "bob";
		final String expResult2 = "ben";

		MessageTokeniser instance = new MessageTokeniser(testStr);
		instance.setConsumeWhitespace(true);

		assertEquals(expResult1, instance.nextToken());
		assertEquals(expResult2, instance.nextToken());
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
		assertTrue(instance.consume(token));

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
		assertTrue(instance.consume(token1));
		assertEquals(token2 + remainder, instance.toString());
		assertTrue(instance.consume(token2));
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
		assertTrue(instance.consume(token1));
		assertTrue(instance.startsWith(token2));
	}

	/**
	 * Test of startsWith method, of class MessageTokeniser.
	 */
	@Test
	public void testUnavailableConsume()
	{
		final String testStr = "Hello world";
		final String token   = "bob";

		MessageTokeniser instance = new MessageTokeniser(testStr);
		assertFalse(instance.startsWith(token));
		assertFalse(instance.consume(token));
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
		assertTrue(instance.consume(token));

		assertEquals(remainder, instance.toString());
	}

	@Test
	public void testTokenise()
	{
		final String testStr = "Hello World";
		final String token1 = "Hello";
		final String token2 = "World";

		MessageTokeniser instance = new MessageTokeniser(testStr);

		assertEquals(token1, instance.nextToken(' '));
		assertEquals(token2, instance.nextToken(' '));
		assertNull(instance.nextToken(' '));
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

	@Test
	public void testWhiteSpaceAutoDelimConsume()
	{
		final String testStr = "Hello   \t World";
		final String token1 = "Hello";
		final String token2 = "World";

		MessageTokeniser instance = new MessageTokeniser(testStr);
		instance.setConsumeWhitespace(true);

		assertEquals(token1, instance.nextToken());
		assertEquals(token2, instance.nextToken());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConsturctorOnNullString()
	{
		MessageTokeniser instance = new MessageTokeniser(null);
	}
}
