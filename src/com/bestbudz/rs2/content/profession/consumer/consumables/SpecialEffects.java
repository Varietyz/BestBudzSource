package com.bestbudz.rs2.content.profession.consumer.consumables;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.AntifireTask;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.consumer.ExperienceCalculator;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SpecialEffects {

	private final Stoner stoner;
	private final ExperienceCalculator expCalculator;

	public SpecialEffects(Stoner stoner) {
		this.stoner = stoner;
		this.expCalculator = new ExperienceCalculator(stoner);
	}

	public void applySpecialFoodEffects(Item item) {
		switch (item.getId()) {
			case 3146:
				stoner.getClient().queueOutgoingPacket(new SendMessage("You eat the poisoned karambwan..."));
				stoner.getClient().queueOutgoingPacket(new SendMessage("...and it damages you!"));
				expCalculator.addSpecialExperience(800);
				stoner.hit(new Hit(5, Hit.HitTypes.NONE));
				break;
			case 712:
				stoner.getUpdateFlags().sendForceMessage("Aaah, nothing like a nice cuppa tea!");
				break;
			case 3801:
				stoner.getUpdateFlags().sendAnimation(new Animation(1329));
				stoner.getClient().queueOutgoingPacket(new SendMessage("You chug the keg. You feel reinvigortated..."));
				stoner.getClient().queueOutgoingPacket(new SendMessage("...but extremely drunk too"));
				expCalculator.addSpecialExperience(1000);
				stoner.getProfession().deductFromGrade(0, 10);
				break;
		}
	}

	public boolean applySpecialPotionEffects(int id) {
		switch (id) {
			case 2452:
			case 2454:
			case 2456:
			case 2458:
			case 2488:
				TaskQueue.queue(new AntifireTask(stoner, false));
				break;
			case 15304:
			case 15305:
			case 15306:
			case 15307:
				TaskQueue.queue(new AntifireTask(stoner, true));
				break;
			case 3008:
			case 3010:
			case 3012:
			case 3014:
				stoner.getRunEnergy().add(20);
				return true;
			case 175:
			case 177:
			case 179:
			case 2446:
				stoner.curePoison(100);
				return true;
			case 12695:
			case 12697:
			case 12699:
			case 12701:
				applySuperCombatEffect();
				return true;
		}
		return false;
	}

	private void applySuperCombatEffect() {
		for (int i = 0; i < 7; i++) {
			stoner.getGrades()[i] = 150;
		}
		stoner.getRunEnergy().setEnergy(420);
		stoner.getGrades()[3] = 250;
		stoner.getGrades()[18] = 100;
		stoner.getProfession().update();
		expCalculator.addSpecialExperience(250000);
		stoner.setAppearanceUpdateRequired(true);
	}
}