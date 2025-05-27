package com.bestbudz.rs2.entity.stoner.net.in.command;

import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Command
 * 
 * @author BestBudz Team
 *
 */
public abstract interface Command {

	/**
	 * Handles the commands
	 * 
	 * @param stoner
	 * @param parser
	 */
	public abstract boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception;

	/**
	 * Checks if stoner meets requirement(s)
	 * 
	 * @param stoner
	 * @return
	 */
	public abstract boolean meetsRequirements(Stoner stoner);
}
