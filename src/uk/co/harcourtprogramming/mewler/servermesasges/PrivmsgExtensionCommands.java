package uk.co.harcourtprogramming.mewler.servermesasges;

public enum PrivmsgExtensionCommands
{
	/**
	 * Message was not a CTCP Command
	 */
	NONE,
	/**
	 * Message was formatted as a CTCP command, but the command name was
	 * not recognised
	 */
	INVALID,
	/**
	 * Message was an IRC action
	 */
	ACTION;
}
