package com.mayhem.rs2.content.skill.hunter;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.MobDeathTask;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.impl.Huntable;

/**
 *
 * @author Jack
 */
public class NPCWalkTask {
    
    private static final int MAX_WALK_TIME = 7000;
    
    public static Task getTask(Mob npc, HunterTrap trap) {
        return new Task(npc, 1, false, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {

            @Override
            public void execute() {
                Location lastLoc = new Location(npc.getLocation());
                int locationSameCount = 0; 
                
                if(trap.getOwner() == null) {
                    stop();
                }
                
                if(trap.getLocation().equals(npc.getLocation())) {
                    if(locationSameCount > 1) {
                        stop();
                        trap.trigger();
                    } else {
                        locationSameCount++;
                    }
                } else if(npc.getMovementHandler().getPrimaryDirection() == -1 && trap.getLocation().withinDistance(npc.getLocation(), 1)) {
                    npc.teleport(trap.getLocation());
                    TaskQueue.queue(new MobDeathTask(npc));
                    trap.trigger();
                    TaskQueue.queue(getVanishTask(npc, 3));
                } else if(npc.getMovementHandler().getPrimaryDirection() == -1 && lastLoc.equals(npc.getLocation())) {
                    trap.setAttracting(false);
                    ((Huntable) npc).setWalkingTo(null);
                     stop();
                }  
            }

            @Override
            public void onStop() {
                npc.getMovementHandler().reset();
                npc.setForceWalking(false);
            }
        };
    }
    
    /**
	 * @return the maxWalkTime
	 */
	public static int getMaxWalkTime() {
		return MAX_WALK_TIME;
	}

	public static Task getVanishTask(Mob npc, int delay) {
        return new Task(npc, delay) {

            @Override
            public void execute() {
                npc.setVisible(false);
                stop();
            }

            @Override
            public void onStop() {
            }

        };
    }
}
