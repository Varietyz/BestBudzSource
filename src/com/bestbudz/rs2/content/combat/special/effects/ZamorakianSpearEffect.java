package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ZamorakianSpearEffect implements CombatEffect {

	private void walk(Entity e) {
	if (!Region.getRegion(e.getLocation().getX(), e.getLocation().getY()).blockedWest(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ())) {
		e.getMovementHandler().walkTo(-2, 0);
	} else if (!Region.getRegion(e.getLocation().getX(), e.getLocation().getY()).blockedEast(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ())) {
		e.getMovementHandler().walkTo(2, 0);
	} else if (!Region.getRegion(e.getLocation().getX(), e.getLocation().getY()).blockedNorth(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ())) {
		e.getMovementHandler().walkTo(0, 2);
	} else if (!Region.getRegion(e.getLocation().getX(), e.getLocation().getY()).blockedSouth(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ())) {
		e.getMovementHandler().walkTo(0, -2);
	}
	}

	@Override
	public void execute(Stoner p, Entity e) {
	if (!e.isStunned()) {
		e.stun(3);
	}

	walk(e);

	p.send(new SendMessage("You stun your opponent."));
	}
}
