package com.mayhem.rs2.content.skill.hunter;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.item.impl.GroundItemHandler;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.impl.Huntable;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author Jack
 */
public class HunterTrap {
    
    private final TrapData data;
    
    private final long setupTime;
    
    private final Player owner;
    
    private final Location location;
    
    private boolean smoked;
    
    private boolean baited;
    
    private boolean attracting;
    
    private boolean caught;
    
    private boolean fallen;
    
    private int loops = 1;
    
    private final double successDistributionMean;
    
    private final double successDistributionDeviation;
    
    private final NormalDistribution successDistribution;
    
    private int face;
    
    public HunterTrap(TrapData data, Player owner, Location loc) {
        this.data = data;
        this.owner = owner;
        this.location = new Location(loc);
        this.setupTime = System.currentTimeMillis();
        
        successDistributionDeviation = 40 * Math.pow((Math.log(owner.getLevels()[Skills.HUNTER] - data.getLevel())), -1); 
        successDistributionMean =  20 * (data.getLevel() / 99);
        successDistribution = new NormalDistribution(successDistributionMean, successDistributionDeviation);
    
    }
    
    public HunterTrap(TrapData data, Player owner) {
        this(data, owner, owner.getLocation());
    }

    public TrapData getData() {
        return data;
    }

    public boolean isSmoked() {
        return smoked;
    }

    public boolean isBaited() {
        return baited;
    }

    public long getSetupTime() {
        return setupTime;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isCaught() {
        return caught;
    }

    public boolean isFallen() {
        return fallen;
    }

    public Location getLocation() {
        return location;
    }
    
    
    public void setSmoked(boolean smoked) {
        this.smoked = smoked;
    }

    public void setBaited(boolean baited) {
        this.baited = baited;
    }

    public void setCaught(boolean caught) {
        this.caught = caught;
    }

    public void setFallen(boolean fallen) {
        this.fallen = fallen;
    }

    public void setAttracting(boolean attracting) {
        this.attracting = attracting;
    }

    public void setFace(int face) {
        this.face = face;
    }
    
    private HunterTrap getSelf() {
        return this;
    }
    
    private boolean success() {
        try {
            int probablity = (int) (Math.abs(successDistribution.inverseCumulativeProbability((double) loops / 300)) + Math.pow(successDistributionMean, 2));
            return ThreadLocalRandom.current().nextInt(probablity+1) == 0;
        } catch(Exception ex) {
            return false;
        }
    }
    
    public void process() {
        if(owner == null) {
            destruct();
            return;
        }
        
        if(!attracting && !caught && success()) {
            List<Mob> huntables = owner.getClient().getNpcs().stream()
                    .filter(npc -> npc instanceof Huntable)
                    .filter(npc -> npc.getLocation().withinDistance(location, 8))
                    .filter(npc -> npc.getId() == data.getAttractMob())
                    .filter(npc -> !npc.getCombat().inCombat())
                    .filter(npc -> ((Huntable) npc).getWalkingTo() == null)
                    .collect(Collectors.toList());
            
            Collections.shuffle(huntables);
            
            if(huntables.size() > 0) {
                this.attracting = true;
                this.loops = 1;
            }
            
            for(Mob npc : huntables) {
                ((Huntable) npc).startWalk(this); 
                return;
            }
        } else {
            if(owner.getClient().getNpcs().stream()
                    .filter(npc -> npc instanceof Huntable)
                    .filter(npc -> npc.getLocation().withinDistance(location, 8))
                    .filter(npc -> npc.getId() == data.getAttractMob())
                    .filter(npc -> !npc.getCombat().inCombat())
                    .filter(npc -> ((Huntable) npc).getWalkingTo() == getSelf())
                    .count() == 0) {
                attracting = false;
            }
        }
        
        loops++;
    }
    
    public void stopNpcs() {
        owner.getClient().getNpcs().stream()
                    .filter(npc -> npc instanceof Huntable)
                    .filter(npc -> npc.getLocation().withinDistance(location, 8))
                    .filter(npc -> npc.getId() == data.getAttractMob())
                    .filter(npc -> !npc.getCombat().inCombat())
                    .filter(npc -> ((Huntable) npc).getWalkingTo() == getSelf())
                    .forEach(npc -> ((Huntable) npc).setWalkingTo(null));
    }
    
    public void create() {
        ObjectManager.spawnWithObject(data.getSetupObject(), location, 10, face);
        return;
    }
    
    
    public void trigger() {
        if(!caught && TrapManager.trapExists(location) && owner != null) {
            ObjectManager.spawnWithObject(data.getActiveObject(), location, 10, face);
            owner.send(new SendMessage("Your trap has caught an animal."));
            caught = true;
        }
    }
    
    public void destruct() {
        ObjectManager.spawnWithObject(data.getReturnObject(), location, 10, face);
        ObjectManager.deleteWithObject(location.getX(), location.getY(), location.getZ());
        stopNpcs();
    }
    
    public void fallDown() {
        loops = 1;
        owner.send(new SendMessage("Your trap has fallen down."));
        GroundItemHandler.add(new Item(data.getItemId()), location, owner);
        destruct();
    }
    
    public boolean takeTrap() {
        if(owner.getInventory().hasSpaceFor(data.getItemId())) {
            owner.getInventory().add(data.getItemId());
            owner.send(new SendMessage("You dismantle your trap."));
            destruct();
            return true;
        } else {
            return false;
        }
    }
    
    public void pickup() {
        if(caught) {
            if(owner.getInventory().hasSpaceFor(data.getItemId()) && 
                    owner.getInventory().hasSpaceFor(data.getRewards().toArray())) {
                
                owner.getSkill().addExperience(Skills.HUNTER, data.getExperience());
                owner.getInventory().add(data.getItemId().getId(), 1);
                data.getRewards().stream().forEach(reward -> owner.getInventory().addItems(reward));
                TrapManager.deregisterTrap(this);
                destruct();
            } else {
                owner.send(new SendMessage("Your inventory is too full."));
            }
        }
    }
    /**
	 * Manages the spawning of chinchompas upon server startup
	 * 
	 * @param chinchompa
	 *            the chinchompa being created
	 *            TODO: add teleporting
	 */
	public static void appendChinchompa() {
		for (int i = 0; i < 20; i++) {
			int implings[] = { 2911 };
			int location[][] = { {3106, 3495}, {3109, 3493} };
			int npc = Utility.randomNumber(implings.length - 1);
			int random_location = Utility.randomNumber(location.length - 1);
			new Mob(implings[npc], true, new Location(location[random_location][0], location[random_location][1]));
		}
	}
}
