package com.bestbudz.rs2.content.profession.petmaster.growth;

import com.bestbudz.rs2.entity.pets.PetData;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple growth chains
 */
public class PetGrowth
{

	private static final Map<PetData, PetData> GROWTHS = new HashMap<>();
	private static final Map<PetData, GrowthRequirement> REQUIREMENTS = new HashMap<>();

	static {
		// JAD growth chain
		GROWTHS.put(PetData.TOY_JAD, PetData.INFANT_JAD);
		GROWTHS.put(PetData.INFANT_JAD, PetData.HATCHLING_JAD);
		GROWTHS.put(PetData.HATCHLING_JAD, PetData.CUB_JAD);
		GROWTHS.put(PetData.CUB_JAD, PetData.YOUTH_JAD);
		GROWTHS.put(PetData.YOUTH_JAD, PetData.TEEN_JAD);
		GROWTHS.put(PetData.TEEN_JAD, PetData.YOUNG_ADULT_JAD);
		GROWTHS.put(PetData.YOUNG_ADULT_JAD, PetData.ADULT_JAD);
		GROWTHS.put(PetData.ADULT_JAD, PetData.PRIME_JAD);
		GROWTHS.put(PetData.PRIME_JAD, PetData.JAD);

		// CORP growth chain
		GROWTHS.put(PetData.TOY_CORP, PetData.INFANT_CORP);
		GROWTHS.put(PetData.INFANT_CORP, PetData.HATCHLING_CORP);
		GROWTHS.put(PetData.HATCHLING_CORP, PetData.CUB_CORP);
		GROWTHS.put(PetData.CUB_CORP, PetData.YOUTH_CORP);
		GROWTHS.put(PetData.YOUTH_CORP, PetData.TEEN_CORP);
		GROWTHS.put(PetData.TEEN_CORP, PetData.YOUNG_ADULT_CORP);
		GROWTHS.put(PetData.YOUNG_ADULT_CORP, PetData.ADULT_CORP);
		GROWTHS.put(PetData.ADULT_CORP, PetData.PRIME_CORP);
		GROWTHS.put(PetData.PRIME_CORP, PetData.CORP);

		// Requirements
		REQUIREMENTS.put(PetData.TOY_JAD, new GrowthRequirement(15, 10, 30 * 60000)); // 30 min
		REQUIREMENTS.put(PetData.INFANT_JAD, new GrowthRequirement(50, 25, 60 * 60000)); // 1 hour
		REQUIREMENTS.put(PetData.HATCHLING_JAD, new GrowthRequirement(90, 45, 2 * 60 * 60000)); // 2 hours
		REQUIREMENTS.put(PetData.CUB_JAD, new GrowthRequirement(130, 65, 4 * 60 * 60000)); // 4 hours
		REQUIREMENTS.put(PetData.YOUTH_JAD, new GrowthRequirement(180, 85, 8 * 60 * 60000)); // 8 hours
		REQUIREMENTS.put(PetData.TEEN_JAD, new GrowthRequirement(230, 105, 12 * 60 * 60000)); // 12 hours
		REQUIREMENTS.put(PetData.YOUNG_ADULT_JAD, new GrowthRequirement(290, 130, 24 * 60 * 60000)); // 24 hours
		REQUIREMENTS.put(PetData.ADULT_JAD, new GrowthRequirement(350, 160, 48 * 60 * 60000)); // 48 hours
		REQUIREMENTS.put(PetData.PRIME_JAD, new GrowthRequirement(420, 195, 72 * 60 * 60000)); // 72 hours

		// Similar for CORP...
		REQUIREMENTS.put(PetData.TOY_CORP, new GrowthRequirement(8, 2, 40 * 60000));
		REQUIREMENTS.put(PetData.INFANT_CORP, new GrowthRequirement(16, 3, 80 * 60000));
		// ... etc
	}

	public static PetData getNextGrowth(PetData current) {
		return GROWTHS.get(current);
	}

	public static GrowthRequirement getGrowthRequirement(PetData current) {
		return REQUIREMENTS.get(current);
	}
}