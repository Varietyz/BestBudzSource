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
	private final PetMaster petMaster; // ADD THIS

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
		this.petMaster = new PetMaster(stoner); // ADD THIS
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return; // Pets don't need profession processing
		}

		resonance.drain();
		bankStanding.process();
		petMaster.process(); // ADD THIS - Process PetMaster every cycle
	}

	/**
	 * Save all profession data
	 */
	public void save() {
		petMaster.save(); // ADD THIS
	}

	/**
	 * Displays the current resonance status to the player
	 */
	public void displayResonanceStatus() {
		String status = resonance.getResonanceStats();
		stoner.send(new SendMessage("@mag@" + status));
	}

	// Getters for all professions
	public Profession getProfession() { return profession; }
	public MageProfession getMage() { return mage; }
	public SagittariusProfession getSagittarius() { return sagittarius; }
	public Melee getMelee() { return melee; }
	public Fisher getFisher() { return fisher; }
	public Mercenary getMercenary() { return mercenary; }
	public Resonance getResonance() { return resonance; }
	public BankStanding getBankStanding() { return bankStanding; }
	public PetMaster getPetMaster() { return petMaster; } // ADD THIS
}
