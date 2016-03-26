package uk.co.harcourtprogramming.mewler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocalBufferedSocketTest
{

	public LocalBufferedSocketTest()
	{
	}

	@Test
	public void CreationTest()
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();

		assertNotNull(sock);
		assertNotNull(sock.in);
		assertNotNull(sock.out);
	}

	@Test
	public void WriteByteTest() throws IOException
	{
		final byte value = 97;

		LocalBufferedSocket sock = new LocalBufferedSocket();
		sock.out.write(value);

		assertEquals("Value was not written to buffer", 1, sock.size());
		assertEquals("Incrrect value was not written to buffer", value, sock.peek());
	}

	@Test
	public void WriteStringTest() throws IOException
	{
		final String s = "Hello";

		LocalBufferedSocket sock = new LocalBufferedSocket();

		sock.out.write(s.getBytes());

		assertEquals("String not written correctly?", s.length(), sock.size());
	}

	@Test
	public void ReadByte() throws IOException
	{
		final byte b = 97;

		LocalBufferedSocket sock = new LocalBufferedSocket();

		sock.out.write(b);

		assertEquals(b, sock.in.read());
	}

	@Test
	public void ReadString() throws IOException
	{
		final String s = "hello";

		LocalBufferedSocket sock = new LocalBufferedSocket();

		sock.out.write(s.getBytes());

		byte[] buf = new byte[5];

		sock.in.read(buf);
		assertEquals(s, new String(buf));
	}

	@Test
	public void BufferedWriterTestNoFlush() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.out));

		final String s = "Hello";

		out.write(s);
		assertEquals(0, sock.size());
	}

	@Test
	public void BufferedWriterTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.out));

		final String s = "Hello";

		out.write(s);
		out.flush();
		// Flushing the socket causes a -1 value to be added, causing the length to be increased by 1
		assertEquals(s.length() + 1, sock.size());
	}

	@Test
	public void LineReaderTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();

		final String s = "Hello";

		sock.out.write(s.getBytes());
		sock.out.write('\n');

		String r = sock.in.readLine();

		assertEquals(s, r);
	}

	@Test
	public void writeLineTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();

		final String s = "Hello";

		sock.out.writeLine(s);

		String r = sock.in.readLine();

		assertEquals(s, r);
	}

	@Test
	public void writeTwoLineTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();

		final String s = "Hello";
		final String t = "Bob";

		sock.out.writeLine(s);
		sock.out.writeLine(t);

		String r = sock.in.readLine();
		String q = sock.in.readLine();

		assertEquals(s, r);
		assertEquals(t, q);
	}

	@Test
	public void writeTwoInterspercedLineTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();

		final String s = "Hello";
		final String t = "Bob";

		sock.out.writeLine(s);
		String r = sock.in.readLine();

		sock.out.writeLine(t);
		String q = sock.in.readLine();

		assertEquals(s, r);
		assertEquals(t, q);
	}

	@Test
	public void BufferedReaderTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.in));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.out));

		final String s = "Hello";

		out.write(s);
		out.newLine();
		out.flush();
		out.close();

		String r = in.readLine();

		assertEquals(s, r);
	}


	@Test
	public void BufferedReaderTest2() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.in));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.out));

		final String s = "Hello";

		out.write(s);
		out.newLine();
		out.flush();
		sock.out.close();

		out.write(s);
		out.newLine();
		out.flush();
		sock.out.close();

		String r = in.readLine();
		assertEquals(s, r);

		r = in.readLine();
		assertEquals(s, r);
	}

	@Test
	public void BufferedReaderLineWriterTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.in));

		final String s1 = "Hello";
		final String s2 = "Bob";

		sock.out.writeLine(s1);
		sock.out.writeLine(s2);

		String r = in.readLine();
		assertEquals(s1, r);

		r = in.readLine();
		assertEquals(s2, r);
	}

	@Test
	public void BufferedReaderLineWriterInterspersedTest() throws IOException
	{
		LocalBufferedSocket sock = new LocalBufferedSocket();
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.in));

		final String s1 = "Hello";
		final String s2 = "Bob";

		sock.out.writeLine(s1);
		String r = in.readLine();
		assertEquals(s1, r);

		sock.out.writeLine(s2);
		r = in.readLine();
		assertEquals(s2, r);
	}
}
