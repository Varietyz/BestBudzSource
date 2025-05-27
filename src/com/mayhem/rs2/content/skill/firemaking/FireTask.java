package com.mayhem.rs2.content.skill.firemaking;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;

public class FireTask extends Task {
	private final GameObject object;
	private final Player p;

	public FireTask(Player p, int cycles, GameObject object) {
		super(cycles, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
		this.object = object;
		ObjectManager.registerObject(object);
		ObjectManager.setClipToZero(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ());
		this.p = p;
	}

	@Override
	public void execute() {
		ObjectManager.remove(object);
		ObjectManager.setClipToZero(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ());
		GroundItemHandler.add(new Item(592, 1), object.getLocation(), p, p.ironPlayer() ? p : null);
		stop();
	}

	@Override
	public void onStop() {
	}
}
