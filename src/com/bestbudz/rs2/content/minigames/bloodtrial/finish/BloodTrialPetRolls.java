package com.bestbudz.rs2.content.minigames.bloodtrial.finish;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialConfig;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.pets.PetManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BloodTrialPetRolls
{
	private static final StringBuilder messageBuilder = new StringBuilder(128);

	public static void handlePet(Stoner stoner) {
		if (stoner == null) {
			return;
		}

		PetData petDrop = PetData.forItem(BloodTrialConfig.PET_ID);
		if (petDrop == null) {
			return;
		}

		if (stoner.getActivePets().size() < BloodTrialConfig.MAX_ACTIVE_PETS) {
			spawnPet(stoner, petDrop);
		} else {
			bankPet(stoner, petDrop);
		}
	}

	private static void spawnPet(Stoner stoner, PetData petDrop) {
		PetManager.spawnPet(stoner, petDrop.getItem(), true);

		messageBuilder.setLength(0);
		messageBuilder.append("You feel a presence following you; ")
			.append(Utility.formatStonerName(
				GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName()))
			.append(" starts to follow you.");

		stoner.send(new SendMessage(messageBuilder.toString()));
	}

	private static void bankPet(Stoner stoner, PetData petDrop) {
		stoner.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
		stoner.send(new SendMessage("You feel a presence added to your bank."));
	}
}