package com.bestbudz.rs2.entity.mob.ai;

import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.content.minigames.barrows.Barrows.Brother;
import com.bestbudz.rs2.content.minigames.warriorsguild.ArmourAnimator;

public class MobStateManager {
	private final Mob mob;
	private boolean visible = true;
	private boolean shouldRespawn = true;
	private boolean canAssault = true;
	private boolean movedLastCycle = false;
	private boolean placement = false;

	public MobStateManager(Mob mob) {
		this.mob = mob;
	}

	public void validateOwnership() {
		Stoner owner = mob.getOwner();
		if (owner != null && (!owner.isActive() || !owner.withinRegion(mob.getLocation()))) {
			if (!owner.inZulrah() && !mob.isDead()) {
				mob.remove();
				if (mob.getId() == 1778) {
					owner.getAttributes().set("KILL_AGENT", Boolean.FALSE);
				}
			}
		}
	}

	public boolean shouldRemove() {
		return !mob.isActive();
	}

	public void checkForDeath() {
		if (mob.getGrades()[3] <= 0) {
			com.bestbudz.core.task.TaskQueue.queue(new com.bestbudz.core.task.impl.MobDeathTask(mob));
			mob.getCombatHandler().clearCombatants();
		}
	}

	public void remove() {
		if (Brother.isBarrowsBrother(mob) && mob.getOwner() != null) {
			mob.getOwner().getCombat().resetCombatTimer();
		}

		if (ArmourAnimator.isAnimatedArmour(mob.getId()) && mob.getOwner() != null) {
			mob.getOwner().getAttributes().remove("warriorGuildAnimator");
		}

		visible = false;
		mob.setActive(false);
		World.unregister(mob);
		Walking.setNpcOnTile(mob, false);

		if (mob.getVirtualRegion() != null) {
			mob.setVirtualRegion(null);
		}

		MobConstants.GODWARS_BOSSES.remove(mob);
	}

	public void reset() {
		movedLastCycle = (mob.getMovementHandler().getPrimaryDirection() != -1);
		mob.getMovementHandler().resetMoveDirections();
		mob.getFollowing().updateWaypoint();
		mob.getUpdateFlags().reset();
		placement = false;
	}

	public void onDeath() {
		// Override in subclasses for custom death behavior
	}

	// Getters and setters
	public boolean isVisible() { return visible; }
	public void setVisible(boolean visible) { this.visible = visible; }
	public boolean shouldRespawn() { return shouldRespawn; }
	public void setRespawnable(boolean shouldRespawn) { this.shouldRespawn = shouldRespawn; }
	public boolean isCanAssault() { return canAssault; }
	public void setCanAssault(boolean canAssault) { this.canAssault = canAssault; }
	public boolean isMovedLastCycle() { return movedLastCycle; }
	public void setMovedLastCycle(boolean movedLastCycle) { this.movedLastCycle = movedLastCycle; }
	public boolean isPlacement() { return placement; }
	public void setPlacement(boolean placement) { this.placement = placement; }
}