package com.mayhem.rs2.content.combat.special.specials;

import com.mayhem.rs2.content.combat.impl.Ranged;
import com.mayhem.rs2.content.combat.special.Special;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.Projectile;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;

/**
 * Dark Bow Special 
 * @author Daniel
 *
 */
public class DarkBowSpecialAttack implements Special {
	
	@Override
	public boolean checkRequirements(Player player) {
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 60;
	}

	@Override
	public void handleAttack(Player player) {
		Ranged r = player.getCombat().getRanged();
		Item ammo = player.getEquipment().getItems()[13];

		if (ammo != null) {
			if ((ammo.getId() == 11212) || (ammo.getId() == 11227) || (ammo.getId() == 0) || (ammo.getId() == 11228)) {
				r.setProjectile(new Projectile(1099));
				r.setEnd(new Graphic(1100, 0, true));
			} else {
				r.setProjectile(new Projectile(1101));
				r.setEnd(new Graphic(1103, 0, true));
			}

		}

		r.setProjectileOffset(1);
	}
}
