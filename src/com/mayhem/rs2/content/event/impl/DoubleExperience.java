package com.mayhem.rs2.content.event.impl;

import com.mayhem.Constants;
import com.mayhem.rs2.content.event.Event;
import com.mayhem.rs2.entity.World;

/**
 * @author Andy || ReverendDread Apr 12, 2017
 */
/*public class DoubleExperience extends Event {
	
	private int ticks = 0;

	@Override
	public boolean start() {
		Constants.doubleExperience = true;
		World.sendGlobalMessage("<img=1><col=ff0000>[EVENT]: Double Experience is now active for 30 minutes.");
		return true;
	}

	@Override
	public boolean preStartupCheck() {
		return true;
	}

	@Override
	public int process() {	
		ticks++;
		if (ticks >= 500) 
			stop();
		return 1;
	}

	@Override
	public void stop() {
		Constants.doubleExperience = false;
		World.sendGlobalMessage("<img=1><col=ff0000>[EVENT]: Double Experience has now ended.");
		stop();
	}

}
*/