package com.mayhem.rs2.entity.mob.impl;

import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.MobWalkTask;
import com.mayhem.rs2.content.skill.hunter.HunterTrap;
import com.mayhem.rs2.content.skill.hunter.NPCWalkTask;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;

/**
 *
 * @author Jack
 */
public class Huntable extends Mob {

    private HunterTrap walkingTo = null;
    
    public Huntable(int id, Location loc) {
        super(id, true, loc);
        this.walkingTo =null;
        this.setWalks(true);
        this.setVisible(true);
        this.setForceWalking(false);
        this.setAttackable(true);
        this.setDropsItems(false);
    }

    public HunterTrap getWalkingTo() {
        return walkingTo;
    }

    public void setWalkingTo(HunterTrap walkingTo) {
        this.walkingTo = walkingTo;
        
        if(walkingTo == null) {
            this.setWalks(true);
        }
    }
    
    public void startWalk(HunterTrap trap) {
        setWalkingTo(trap);
        TaskQueue.queue(new MobWalkTask(this, trap.getLocation(), false));
        TaskQueue.queue(NPCWalkTask.getTask(this, trap));
    }
    
}
