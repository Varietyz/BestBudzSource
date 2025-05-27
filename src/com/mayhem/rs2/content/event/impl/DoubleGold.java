package com.mayhem.rs2.content.event.impl;

import com.mayhem.rs2.content.event.Event;

/**
 * @author Andy || ReverendDread Jul 1, 2017
 */
public class DoubleGold extends Event {
	
	private int duration;

	@Override
	public boolean start() {
		sendMessage("DoubleGold is now active. All NPC's now have double gold drops.");
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
		if (duration >= 3000)
			return -1;
		return 1;
	}

	@Override
	public void stop() {
		sendMessage("DoubleGold is now over!");
	}

}
