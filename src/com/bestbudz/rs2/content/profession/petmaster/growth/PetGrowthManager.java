package com.bestbudz.rs2.content.profession.petmaster.growth;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.content.profession.petmaster.bond.PetBondManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PetGrowthManager {

	private static final Logger logger = Logger.getLogger(PetGrowthManager.class.getName());

	private final Stoner stoner;
	private final PetBondManager bondManager;

	public PetGrowthManager(Stoner stoner, PetBondManager bondManager) {
		this.stoner = stoner;
		this.bondManager = bondManager;
	}

	public void checkAndExecuteGrowth(Pet pet, PetBond bond) {
		try {
			if (pet == null || pet.getData() == null) return;

			PetData nextStage = PetGrowth.getNextGrowth(pet.getData());
			if (nextStage == null) return;

			GrowthRequirement req = PetGrowth.getGrowthRequirement(pet.getData());
			if (req == null) return;

			if (getStonerGrade() >= req.getStonerGrade() &&
				bond.getBondGrade() >= req.getBondGrade() &&
				bond.getActiveTime() >= req.getMinActiveTime()) {

				performGrowth(pet, nextStage, bond);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error checking growth for " + stoner.getUsername() +
				" with pet: " + (pet != null && pet.getData() != null ? pet.getData().name() : "null"), e);
		}
	}

	private void performGrowth(Pet oldPet, PetData newPetData, PetBond bond) {
		try {
			if (oldPet == null || newPetData == null) return;

			PetData oldPetData = oldPet.getData();

			logger.info("Starting INSTANT pet growth for " + stoner.getUsername() +
				" from " + oldPetData.name() + " to " + newPetData.name());

			Location savedLocation = new Location(
				oldPet.getPetStoner().getLocation().getX(),
				oldPet.getPetStoner().getLocation().getY(),
				oldPet.getPetStoner().getLocation().getZ()
			);

			int direction = oldPet.getPetStoner().getFaceDirection();

			if (oldPet.getPetStoner() != null && oldPet.getPetStoner().getUpdateFlags() != null) {
				oldPet.getPetStoner().getUpdateFlags().sendForceMessage("*evolving*");
			}

			stoner.send(new SendMessage("Your " + oldPetData.name() + " is evolving!"));

			stoner.getActivePets().remove(oldPet);

			oldPet.remove();

			Pet newPet = new Pet(stoner, newPetData);

			newPet.getPetStoner().getAttributes().set("PET_OWNER", stoner);
			newPet.getPetStoner().getAttributes().set("PET_DATA", newPetData);

			newPet.getPetStoner().getLocation().setAs(savedLocation);
			newPet.getPetStoner().faceDirection(direction);

			stoner.getActivePets().add(newPet);

			newPet.getPetStoner().setAppearanceUpdateRequired(true);
			newPet.getPetStoner().getUpdateFlags().setUpdateRequired(true);
			newPet.getPetStoner().setNeedsPlacement(true);

			newPet.getPetStoner().setVisible(false);

			newPet.getPetStoner().setVisible(true);

			bondManager.transferBond(oldPetData, newPetData, bond);

			PetData originalData = (PetData) oldPet.getPetStoner().getAttributes().get("ORIGINAL_PET_DATA");
			if (originalData == null) {
				originalData = oldPetData;
			}
			newPet.getPetStoner().getAttributes().set("ORIGINAL_PET_DATA", originalData);

			stoner.send(new SendMessage("Evolution complete! " + oldPetData.name() +
				" evolved into " + newPetData.name() + "!"));

			if (newPet.getPetStoner() != null && newPet.getPetStoner().getUpdateFlags() != null) {
				newPet.getPetStoner().getUpdateFlags().sendForceMessage("*feels more powerful*");
			}

			logger.info("INSTANT pet growth completed for " + stoner.getUsername() +
				" from " + oldPetData.name() + " to " + newPetData.name());

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error during INSTANT pet growth execution for " +
				stoner.getUsername(), e);
		}
	}

	private int getStonerGrade() {
		try {
			if (stoner == null || stoner.getProfession() == null) return 1;
			return (int) stoner.getProfession().getGrades()[17];
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error getting stoner grade for " +
				(stoner != null ? stoner.getUsername() : "null"), e);
			return 1;
		}
	}
}
