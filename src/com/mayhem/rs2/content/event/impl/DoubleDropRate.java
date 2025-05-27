package com.mayhem.rs2.content.event.impl;

import com.mayhem.rs2.content.event.Event;

/**
 * @author Travis | Divine
 */
public class DoubleDropRate extends Event {
	
	private int duration;

	@Override
	public boolean start() {
		sendMessage("Double Drop Rates are now active. All NPC's now have twice the chance of dropping rare loot!");
		return true;
	}

	@Override
	public boolean preStartupCheck() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int process() {
		duration++;
		if (duration >= 2000)
			return -1;
		return 1;
	}

	@Override
	public void stop() {
		sendMessage("Double Drop Rates is now over!");
	}

}
