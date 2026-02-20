package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetManager;
import java.util.ArrayList;
import java.util.List;

public class StonerPets {
	private final Stoner stoner;

	private boolean pet = false;
	private List<Pet> activePetsNew = new ArrayList<>();

	public StonerPets(Stoner stoner) {
		this.stoner = stoner;
	}

	public boolean isPet() {
		return pet;
	}

	public boolean isPetStoner() {
		return isPet() && PetManager.isPetUsername(stoner.getUsername());
	}

	public void setPet(boolean pet) {
		this.pet = pet;
		if (pet) {

			stoner.getGrades()[3] = 420000;
			stoner.getMaxGrades()[3] = 420000;
		}
	}

	public List<Pet> getActivePets() {
		return activePetsNew;
	}
}
