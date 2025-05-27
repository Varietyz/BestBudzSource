package com.bestbudz.rs2.content.profession.mage.spells;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.profession.mage.Spell;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendOpenTab;

public class LowAlchemy extends Spell {

	@Override
	public boolean execute(Stoner stoner) {
	if (stoner.getProfession().locked())
		return false;
	if (stoner.getAttributes().get("mageitem") == null) {
		return false;
	}

	int item = stoner.getAttributes().getInt("mageitem");
	if (item == 995) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot cast alchemy on this item."));
		return false;
	}
	Item bestbucks = new Item(995, GameDefinitionLoader.getLowAlchemyValue(item));

	if (!stoner.getBox().hasSpaceFor(bestbucks)) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough box space to cast High alchemy."));
		return false;
	}

	stoner.getBox().remove(item);
	stoner.getBox().add(bestbucks);

	stoner.getUpdateFlags().sendAnimation(712, 0);
	stoner.getUpdateFlags().sendGraphic(new Graphic(112, true));

	stoner.getClient().queueOutgoingPacket(new SendOpenTab(6));

	stoner.getProfession().lock(5);

	return true;
	}

	@Override
	public double getExperience() {
	return 850.5D;
	}

	@Override
	public int getGrade() {
	return 21;
	}

	@Override
	public String getName() {
	return "Low alchemy";
	}

	// @Override
	// public Item[] getRunes() {
	// return new Item[] { new Item(554, 3), new Item(561, 1) };
	// }
}
