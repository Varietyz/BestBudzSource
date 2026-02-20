package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.bankstanding.BankStanding;
import com.bestbudz.rs2.content.profession.fisher.Fisher;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.content.profession.melee.Melee;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.content.profession.petmaster.PetMaster; // ADD THIS
import com.bestbudz.rs2.content.profession.resonance.Resonance;
import com.bestbudz.rs2.content.profession.sagittarius.SagittariusProfession;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Manages all profession/skill-related functionality
 */
public class StonerProfessions {
	private final Stoner stoner;

	// All professions
	private final Profession profession;
	private final MageProfession mage;
	private final SagittariusProfession sagittarius;
	private final Melee melee;
	private final Fisher fisher;
	private final Mercenary mercenary;
	private final Resonance resonance;
	private final BankStanding bankStanding;

	private PetMaster petMaster;
	private boolean petMasterInitialized = false;

	public StonerProfessions(Stoner stoner) {
		this.stoner = stoner;
		this.profession = new Profession(stoner);
		this.mage = new MageProfession(stoner);
		this.sagittarius = new SagittariusProfession(stoner);
		this.melee = new Melee();
		this.fisher = new Fisher(stoner);
		this.mercenary = new Mercenary(stoner);
		this.resonance = new Resonance(stoner);
		this.bankStanding = new BankStanding(stoner);
	}

	/**
	 * Initialize PetMaster when stoner is fully loaded
	 */
	private void ensurePetMasterInitialized() {
		if (!petMasterInitialized && stoner != null &&
			stoner.getUsername() != null && !stoner.getUsername().trim().isEmpty()) {
			try {
				this.petMaster = new PetMaster(stoner);
				this.petMasterInitialized = true;
				System.out.println("PetMaster initialized for user: " + stoner.getUsername());
			} catch (Exception e) {
				System.err.println("Failed to initialize PetMaster for " + stoner.getUsername() + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return; // Pets don't need profession processing
		}

		resonance.drain();
		bankStanding.process();
		ensurePetMasterInitialized();
		if (petMaster != null) {
			petMaster.process();
		}
	}

	/**
	 * Save all profession data
	 */
	public void save() {
		if (petMaster != null) {
			petMaster.save();
		}
	}

	/**
	 * Displays the current resonance status to the player
	 */
	public void displayResonanceStatus() {
		String status = resonance.getResonanceStats();
		stoner.send(new SendMessage("@mag@" + status));
	}

	// Getters for all professions
	/**
	 * Get PetMaster with lazy initialization
	 */
	public PetMaster getPetMaster() {ensurePetMasterInitialized();return petMaster;}
	public Profession getProfession() { return profession; }
	public MageProfession getMage() { return mage; }
	public SagittariusProfession getSagittarius() { return sagittarius; }
	public Melee getMelee() { return melee; }
	public Fisher getFisher() { return fisher; }
	public Mercenary getMercenary() { return mercenary; }
	public Resonance getResonance() { return resonance; }
	public BankStanding getBankStanding() { return bankStanding; }

}
