package com.bestbudz.rs2.content.consumables;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SpecialConsumables {
	public static void specialFood(Stoner stoner, Item item) {
	switch (item.getId()) {
	case 3146:
		stoner.getClient().queueOutgoingPacket(new SendMessage("You eat the poisoned karambwan..."));
		stoner.getClient().queueOutgoingPacket(new SendMessage("...and it damages you!"));
		stoner.getProfession().addExperience(20, 800);
		stoner.hit(new Hit(5, Hit.HitTypes.NONE));
		break;
	case 712:
		stoner.getUpdateFlags().sendForceMessage("Aaah, nothing like a nice cuppa tea!");

		break;
	case 3801:
		stoner.getUpdateFlags().sendAnimation(new Animation(1329));
		stoner.getClient().queueOutgoingPacket(new SendMessage("You chug the keg. You feel reinvigortated..."));
		stoner.getClient().queueOutgoingPacket(new SendMessage("...but extremely drunk too"));
		stoner.getProfession().addExperience(20, 1000);
		stoner.getProfession().deductFromGrade(0, 10);
		break;
	}
	}
}
