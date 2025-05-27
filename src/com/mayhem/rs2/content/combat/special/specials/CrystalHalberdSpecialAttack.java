package com.mayhem.rs2.content.combat.special.specials;

import com.mayhem.rs2.content.combat.special.Special;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.player.Player;

public class CrystalHalberdSpecialAttack implements Special {
	
	@Override
	public boolean checkRequirements(Player player) {
		return false;
	}

	@Override
	public int getSpecialAmountRequired() {
		return 55;
	}

	@Override
	public void handleAttack(Player player) {
		player.getCombat().getMelee().setAnimation(new Animation(1203, 0));
		player.getUpdateFlags().sendGraphic(Graphic.lowGraphic(1232, 0));
		player.getCombat().getAttacking().getUpdateFlags().sendGraphic(Graphic.lowGraphic(1232, 0));
	}
}
