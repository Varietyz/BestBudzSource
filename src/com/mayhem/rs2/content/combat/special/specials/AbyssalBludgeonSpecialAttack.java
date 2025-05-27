package com.mayhem.rs2.content.combat.special.specials;

import com.mayhem.rs2.content.combat.formula.MeleeFormulas;
import com.mayhem.rs2.content.combat.special.Special;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.player.Player;

/**
 *@author Andrew
 */
public class AbyssalBludgeonSpecialAttack implements Special {
	MeleeFormulas meleeformula = new MeleeFormulas();
	
	@Override
	public boolean checkRequirements(Player player) {
		return true;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 50;
	}

	@Override
	public void handleAttack(Player player) {
		player.getCombat().getMelee().setAnimation(new Animation(7010, 0));
		player.getCombat().getAttacking().getUpdateFlags().sendGraphic(Graphic.highGraphic(1284, 0));
		
		MeleeFormulas.getSpecialStr(player);
	}
	
}

