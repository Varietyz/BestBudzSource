package com.bestbudz.rs2.content.minigames.warriorsguild;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class TokenTask extends Task {

	private Stoner stoner;

	public TokenTask(Stoner stoner, int delay) {
	super(stoner, delay, false, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	}

	public static final Location NO_TOKENS = new Location(2846, 3540, 2);

	@Override
	public void execute() {
	if (!stoner.inCyclops()) {
		stop();
		return;
	}

	stoner.getBox().remove(8851, 25);

	stoner.getAttributes().set("warrguildtokensused", stoner.getAttributes().getInt("warrguildtokensused") + 25);
	stoner.send(new SendMessage("@red@25 tokens were taken away from you."));
	CyclopsRoom.updateInterface(stoner);
	if (stoner.getBox().getItemAmount(8851) < 20) {
		stoner.teleport(NO_TOKENS);
		stoner.getAttributes().remove("cyclopsdefenderdrop");
		stoner.getAttributes().remove("warrguildtokentask");
		stoner.send(new SendMessage("@red@You have ran out of tokens!"));
		stop();
	}
	}

	@Override
	public void onStop() {
	stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
	}
}
