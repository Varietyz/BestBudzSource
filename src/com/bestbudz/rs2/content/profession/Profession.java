package com.bestbudz.rs2.content.profession;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults;
import com.bestbudz.rs2.content.Advance;
import com.bestbudz.rs2.content.ProfessionsChat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.io.sqlite.SaveCache;
import com.bestbudz.rs2.content.io.sqlite.SaveWorker;
import com.bestbudz.rs2.content.profession.bankstanding.BankStanding;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.pets.PetUtils;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChatBridgeManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendExpCounter;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterfaceConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProfession;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class Profession {

	public static final int[] EXP_FOR_GRADE = {
		0, 0, 15053, 30322, 45812, 61525, 77465, 93635, 110038, 126678, 143558,
		160681, 178051, 195671, 213546, 231678, 250072, 268732, 287660, 306862, 326340,
		346099, 366144, 386477, 407104, 428028, 449254, 470786, 492629, 514786, 537264,
		560065, 583195, 606659, 630461, 654607, 679101, 703948, 729154, 754723, 780660,
		806972, 833664, 860740, 888207, 916070, 944335, 973007, 1002093, 1031599, 1061530,
		1091893, 1122694, 1153939, 1185634, 1217787, 1250404, 1283491, 1317055, 1351103, 1385642,
		1420680, 1456223, 1492278, 1528853, 1565956, 1603594, 1641775, 1680507, 1719797, 1759654,
		1800086, 1841101, 1882707, 1924914, 1967729, 2011162, 2055221, 2099916, 2145255, 2191248,
		2237905, 2285234, 2333246, 2381951, 2431358, 2481478, 2532320, 2583896, 2636216, 2689290,
		2743130, 2797746, 2853150, 2909353, 2966367, 3024203, 3082873, 3142389, 3202764, 3264009,
		3326138, 3389163, 3453097, 3517953, 3583744, 3650484, 3718187, 3786867, 3856537, 3927211,
		3998905, 4071634, 4145411, 4220252, 4296172, 4373188, 4451314, 4530567, 4610963, 4692519,
		4775251, 4859176, 4944312, 5030675, 5118285, 5207157, 5297312, 5388767, 5481540, 5575652,
		5671121, 5767968, 5866211, 5965871, 6066968, 6169523, 6273558, 6379093, 6486150, 6594751,
		6704919, 6816675, 6930044, 7045047, 7161709, 7280054, 7400105, 7521888, 7645428, 7770749,
		7897878, 8026840, 8157662, 8290371, 8424995, 8561559, 8700094, 8840626, 8983186, 9127801,
		9274503, 9423320, 9574283, 9727424, 9882774, 10040364, 10200227, 10362396, 10526903, 10693783,
		10863071, 11034799, 11209005, 11385723, 11564990, 11746842, 11931318, 12118454, 12308289,
		12500862,
		12696212, 12894380, 13095406, 13299331, 13506198, 13716048, 13928925, 14144872, 14363933,
		14586154,
		14811581, 15040258, 15272234, 15507555, 15746270, 15988429, 16234080, 16483274, 16736062,
		16992496,
		17252628, 17516512, 17784202, 18055754, 18331221, 18610662, 18894133, 19181692, 19473399,
		19769313,
		20069495, 20374007, 20682910, 20996269, 21314147, 21636610, 21963723, 22295555, 22632172,
		22973645,
		23320042, 23671436, 24027898, 24389500, 24756319, 25128427, 25505903, 25888823, 26277265,
		26671310,
		27071039, 27476532, 27887874, 28305149, 28728442, 29157840, 29593431, 30035304, 30483551,
		30938262,
		31399532, 31867455, 32342126, 32823644, 33312106, 33807614, 34310268, 34820171, 35337429,
		35862147,
		36394434, 36934397, 37482148, 38037799, 38601464, 39173259, 39753301, 40341709, 40938603,
		41544106,
		42158342, 42781437, 43413519, 44054717, 44705163, 45364991, 46034335, 46713333, 47402124,
		48100849,
		48809652, 49528678, 50258074, 50997990, 51748578, 52509991, 53282387, 54065922, 54860758,
		55667058,
		56484987, 57314713, 58156406, 59010238, 59876385, 60755025, 61646336, 62550504, 63467711,
		64398148,
		65342004, 66299473, 67270751, 68256039, 69255536, 70269450, 71297986, 72341358, 73399777,
		74473462,
		75562633, 76667512, 77788327, 78925307, 80078686, 81248700, 82435589, 83639596, 84860968,
		86099955,
		87356813, 88631798, 89925172, 91237200, 92568151, 93918298, 95287918, 96677292, 98086704,
		99516445,
		100966806, 102438085, 103930585, 105444610, 106980473, 108538486, 110118971, 111722250,
		113348654, 114998515,
		116672171, 118369966, 120092249, 121839371, 123611692, 125409575, 127233388, 129083506,
		130960308, 132864179,
		134795509, 136754694, 138742136, 140758243, 142803427, 144878109, 146982714, 149117673,
		151283424, 153480412,
		155709086, 157969904, 160263329, 162589832, 164949890, 167343987, 169772613, 172236267,
		174735454, 177270686,
		179842483, 182451373, 185097890, 187782578, 190505987, 193268674, 196071208, 198914162,
		201798120, 204723672,
		207691419, 210701969, 213755940, 216853958, 219996658, 223184685, 226418692, 229699342,
		233027309, 236403275,
		239827931, 243301981, 246826136, 250401120, 254027665, 257706515, 261438425, 265224159,
		269064494, 272960218,
		276912129, 280921037, 284987766, 289113148, 293298030, 297543270, 301849738, 306218318,
		310649905, 315145408,
		319705748, 324331862, 329024698, 333785217, 338614397, 343513227, 348482712, 353523871,
		358637738, 363825361,
		369087804, 374426147, 379841484, 385334925, 390907597, 396560643, 402295222, 408112509,
		414013699, 420000000
	};
	private final Stoner stoner;
	private final int meleeExp = 300;
	private final int sagittariusExp = 210;
	private final int mageExp = 120;
	private final int combatGrade = 0;
	private double[] experience = new double[Professions.PROFESSION_COUNT];
	private long totalGrade = 0;
	private boolean expLock = false;
	private long lock = 0L;

	public Profession(Stoner stoner) {
		this.stoner = stoner;
		for (int i = 0; i < Professions.PROFESSION_COUNT; i++)
			if (i == 3) {
				getGrades()[i] = 3;
				experience[i] = 30322.0D;
			} else {
				getGrades()[i] = 1;
				experience[i] = 0.0D;
			}
	}

	private static long binarySearch(double experience, int min, int max) {
		while (min <= max) {
			int mid = (min + max) / 2;
			double value = EXP_FOR_GRADE[mid];

			if (value > experience) {
				max = mid - 1;
			} else if (mid + 1 >= EXP_FOR_GRADE.length || EXP_FOR_GRADE[mid + 1] > experience) {
				return mid;
			} else {
				min = mid + 1;
			}
		}
		return 0;
	}


	public void addCombatExperience(CombatTypes type, long hit) {
		if ((expLock) || (stoner.getMage().isDFireShieldEffect())) {
			return;
		}

		if (type == CombatTypes.MAGE) {
			stoner.getMage().getSpellCasting().addSpellExperience();
		}

		if (hit <= 0) {
			return;
		}

		if (stoner.isPetStoner()) {
			handlePetOwnerExperience(type, hit);
			return; // Exit early - pets don't gain their own experience
		}

		double exp = hit * 3.0D;

		switch (type) {
			case NONE:
				break;
			case MELEE:
				switch (stoner.getEquipment().getAssaultStyle()) {
					case ACCURATE:
						addExperience(0, exp * meleeExp);
						break;
					case AGGRESSIVE:
						addExperience(2, exp * meleeExp);
						break;
					case CONTROLLED:
						addExperience(0, (exp / 3.0D) * meleeExp);
						addExperience(2, (exp / 3.0D) * meleeExp);
						addExperience(1, (exp / 3.0D) * meleeExp);
						break;
					case DEFENSIVE:
						addExperience(1, exp * meleeExp);
						break;
				}
				break;
			case MAGE:
				addExperience(6, exp * mageExp);
				break;
			case SAGITTARIUS:
				addExperience(4, exp * sagittariusExp);
				switch (stoner.getEquipment().getAssaultStyle()) {
					case ACCURATE:
					case AGGRESSIVE:
					case CONTROLLED:
						addExperience(4, exp * sagittariusExp);
						break;
					case DEFENSIVE:
						addExperience(4, (exp / 2.0D) * sagittariusExp);
						addExperience(1, (exp / 2.0D) * sagittariusExp);
						break;
				}
				break;
		}

		addExperience(3, exp * meleeExp * 1.33D);
	}

	/**
	 * Handle experience and messaging for pet owners
	 */
	private void handlePetOwnerExperience(CombatTypes type, long hit) {
		Stoner owner = (Stoner) stoner.getAttributes().get("PET_OWNER");
		if (owner == null || !owner.isActive()) {
			return;
		}

		// Apply advancement multiplier if owner has 2+ advances in Pet Master
		long experienceHit = hit;
		if (owner.getProfessionAdvances()[17] >= 2) {
			experienceHit = hit * owner.getProfessionAdvances()[17];
		}

		// Calculate base experience (same formula as regular combat)
		double exp = experienceHit;
		double totalExpGained = 0;

		// Award experience to owner based on combat type
		switch (type) {
			case MELEE:
				// Use owner's assault style for melee experience distribution
				switch (owner.getEquipment().getAssaultStyle()) {
					case ACCURATE:
						totalExpGained += owner.getProfession().addExperience(0, exp * meleeExp);
						break;
					case AGGRESSIVE:
						totalExpGained += owner.getProfession().addExperience(2, exp * meleeExp);
						break;
					case CONTROLLED:
						totalExpGained += owner.getProfession().addExperience(0, (exp / 3.0D) * meleeExp);
						totalExpGained += owner.getProfession().addExperience(2, (exp / 3.0D) * meleeExp);
						totalExpGained += owner.getProfession().addExperience(1, (exp / 3.0D) * meleeExp);
						break;
					case DEFENSIVE:
						totalExpGained += owner.getProfession().addExperience(1, exp * meleeExp);
						break;
				}
				break;
			case MAGE:
				totalExpGained += owner.getProfession().addExperience(6, exp * mageExp);
				break;
			case SAGITTARIUS:
				totalExpGained += owner.getProfession().addExperience(4, exp * sagittariusExp);
				switch (owner.getEquipment().getAssaultStyle()) {
					case ACCURATE:
					case AGGRESSIVE:
					case CONTROLLED:
						totalExpGained += owner.getProfession().addExperience(4, exp * sagittariusExp);
						break;
					case DEFENSIVE:
						totalExpGained += owner.getProfession().addExperience(4, (exp / 2.0D) * sagittariusExp);
						totalExpGained += owner.getProfession().addExperience(1, (exp / 2.0D) * sagittariusExp);
						break;
				}
				break;
		}

		// Always add hitpoints experience
		totalExpGained += owner.getProfession().addExperience(3, exp * meleeExp * 1.33D);

		// Send message to owner for significant hits
		if (hit > 50 && totalExpGained > 0) {
			String petName = PetUtils.formatPetDisplayName(
				(PetData) stoner.getAttributes().get("PET_DATA")
			);

			StringBuilder message = new StringBuilder();
			message.append("@gre@Pet ").append(petName).append(" dealt ").append(hit).append(" dmg");
			message.append(" (+").append((int)totalExpGained).append(" XP)");

			owner.send(new SendMessage(message.toString()));
		}
	}

	public double addExperience(int id, double experience) {
		if ((expLock) && (id <= 6)) {
			return 0;
		}

		// FIXED: Validate skill ID to prevent skill 20 XP gain
		if (id < 0 || id >= Professions.PROFESSION_COUNT) {
			System.err.println("Invalid skill ID: " + id + " (max: " + (Professions.PROFESSION_COUNT - 1) + ")");
			return 0;
		}

		experience = experience * Professions.EXPERIENCE_RATES[id] * 1.0D;

		// FIXED: Simple Discord bot check without creating additional systems
		boolean isDiscordBot = isDiscordBot(stoner);

		if (isDiscordBot) {
			// REMOVED: Auto-banking during XP gains to prevent conflicts
			// The quarrying system already handles banking separately
			System.out.println("Discord bot gained XP: " + (int)experience + " in skill " + id + " (" +
				Professions.PROFESSION_NAMES[id] + ")");
		}

		// Apply bank standing XP bonus to other skills (not to bank standing itself)
		if (id != BankStanding.BANKSTANDING_SKILL_ID && stoner.getBankStanding().isActive()) {
			int originalXP = (int) experience;
			int bonusXP = stoner.getBankStanding().applyXPBonus(id, originalXP);
			experience = bonusXP;
		}

		this.experience[id] += experience;

		long newGrade = getGradeForExperience(id, this.experience[id]);

		int cap = EXP_FOR_GRADE.length - 1;
		if (newGrade > cap) {
			newGrade = cap;
		}

		if (stoner.getMaxGrades()[id] < newGrade) {
			getGrades()[id] = ((newGrade - (stoner.getMaxGrades()[id] - getGrades()[id])));
			stoner.getMaxGrades()[id] = newGrade;

			updateTotalGrade();
			onUpgrade(newGrade, id);
			stoner.setAppearanceUpdateRequired(true);

			if (newGrade == cap) {
				String message =
					"<col=855907><img=12> " + stoner.getUsername() + " has achieved grade " + cap + " in " +
						Professions.PROFESSION_NAMES[id] + "! Advance grade: " + stoner.getProfessionAdvances()[id];

				World.sendGlobalMessage(message);
				stoner.getUpdateFlags().sendForceMessage("SMOKE SESH!");
				stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
				stoner.getUpdateFlags().sendAnimation(new Animation(884));
			}
		}

		stoner.send(new SendExpCounter(id, (int) experience));
		SaveCache.markDirty(stoner);
		update(id);

		return experience;
	}

	public void deductFromGrade(int id, int amount) {
		getGrades()[id] = ((getGrades()[id] - amount));

		if (getGrades()[id] < 0) {
			getGrades()[id] = 0;
		}

		update(id);
	}

	public int getCombatGrade() {
		return combatGrade;
	}

	public int calcCombatGrade() {
		long mag = stoner.getMaxGrades()[Professions.MAGE];
		long ran = stoner.getMaxGrades()[Professions.SAGITTARIUS];
		long att = stoner.getMaxGrades()[Professions.ASSAULT];
		long str = stoner.getMaxGrades()[Professions.VIGOUR];
		long def = stoner.getMaxGrades()[Professions.AEGIS];
		long hit = stoner.getMaxGrades()[Professions.LIFE];
		long pray = stoner.getMaxGrades()[Professions.RESONANCE];
		long slay = stoner.getMaxGrades()[Professions.MERCENARY];
		int adv = stoner.getTotalAdvances();

		// Core contributions
		double tankFactor = def * 0.30 + hit * 0.30 + pray * 0.20 + slay * 0.10;
		double hybridBias = (att + str + ran + mag) / 4.0;

		double offensiveMelee = att * 0.30 + str * 0.30 + slay * 0.15 + pray * 0.10;
		double offensiveRange = ran * 0.50 + slay * 0.15 + pray * 0.10;
		double offensiveMage = mag * 0.50 + slay * 0.15 + pray * 0.10;

		// Determine dominant style
		double offensiveFactor;
		if (ran >= att && ran >= mag) {
			offensiveFactor = offensiveRange;
		} else if (mag >= att && mag >= ran) {
			offensiveFactor = offensiveMage;
		} else {
			offensiveFactor = offensiveMelee;
		}

		// Composite score
		double totalScore = hybridBias * 0.10 + tankFactor + offensiveFactor + adv * 0.10;

		return (int) totalScore;
	}

	public double[] getExperience() {
		return experience;
	}

	public void setExperience(double[] experience) {
		this.experience = experience;
	}

	public long getGradeForExperience(final int id, double experience) {
		// Check for exact max grade first
		if (experience >= EXP_FOR_GRADE[EXP_FOR_GRADE.length - 1]) {
			return EXP_FOR_GRADE.length - 1;
		}

		// Binary search for the correct grade
		int low = 0;
		int high = EXP_FOR_GRADE.length - 1;
		int result = 0;

		while (low <= high) {
			int mid = (low + high) / 2;
			if (EXP_FOR_GRADE[mid] <= experience) {
				result = mid;
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}

		return result;
	}

	public long[] getGrades() {
		return stoner.getGrades();
	}

	public long getTotalCombatExperience() {
		long total = 0L;

		for (int i = 0; i <= 6; i++) {
			total = (long) (total + experience[i]);
		}

		return total;
	}

	public long getTotalExperience() {
		long total = 0L;

		for (int ii = 0; ii < experience.length; ii++) {

			double i = experience[ii];
			total = (long) (total + (i > 1_000_000_000.0D ? 1_000_000_000.0D : i));
		}

		return total;
	}

	public long getTotalGrade() {
		return totalGrade;
	}

	public int getXPForGrade(int professionId, int grade) {
		if (grade < 1) {
			return 0;
		}
		if (grade >= EXP_FOR_GRADE.length) {
			return EXP_FOR_GRADE[EXP_FOR_GRADE.length - 1];
		}
		return EXP_FOR_GRADE[grade];
	}

	public boolean hasCombatGrades() {
		for (int i = 0; i <= 6; i++) {
			if ((i == 3) && (stoner.getMaxGrades()[i] > 10)) {
				return true;
			}

			if (stoner.getMaxGrades()[i] > 1) {
				return true;
			}
		}

		return false;
	}

	public boolean hasTwo99s() {
		byte c = 0;

		for (long i : stoner.getMaxGrades()) {
			int cap = EXP_FOR_GRADE.length - 1;
			if ((i == cap)) {

				if (++c == 2) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isAdvanceFive() {
		byte c = 0;

		for (int i : stoner.getProfessionAdvances()) {
			if ((i == 5)) {
				if (++c == 1) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isExpLocked() {
		return expLock;
	}

	public void lock(int delay) {
		lock = (World.getCycles() + delay);
	}

	public boolean locked() {
		return lock > World.getCycles();
	}

	/**
	 * FIXED: Simple Discord bot check
	 */
	private boolean isDiscordBot(Stoner stoner) {
		return stoner instanceof com.bestbudz.core.discord.stonerbot.DiscordBotStoner ||
			(stoner.getUsername() != null &&
				stoner.getUsername().equals(DiscordBotDefaults.DEFAULT_USERNAME));
	}

	public void onUpgrade(long lvl, int profession) {
		stoner.getUpdateFlags().sendGraphic(Professions.UPGRADE_GRAPHIC);
		stoner.getUpdateFlags().sendAnimation(Professions.UPGRADE_ANIM);
		String line1 =
			"Well done bud! You have advanced "
				+ Professions.PROFESSION_NAMES[profession]
				+ " to grade "
				+ lvl
				+ "!";
		String line2 = "You have reached grade " + lvl + "!";
		ChatBridgeManager.notifyLevelUp(stoner.getUsername(), Professions.PROFESSION_NAMES[profession], (int) lvl);
		stoner.getClient().queueOutgoingPacket(new SendMessage(line1));
		SaveWorker.enqueueSave(stoner);

		// FIXED: Only trigger quickchat for Discord bot, not duplicate XP systems
		if (isDiscordBot(stoner)) {
			triggerProfessionQuickChat(profession);
		}

		if (profession == Professions.BANKSTANDING) {
			stoner.send(new SendInterfaceConfig(4888, 200, 5340));
		}

		if ((profession != 4) && (profession != 14) && (profession != 17) && (profession != 19)) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendString("<col=369>" + line1, Professions.CHAT_INTERFACES[profession][1] + 1));
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendString(line2, Professions.CHAT_INTERFACES[profession][1] + 2));
		} else {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendString("<col=369>" + line1, Professions.CHAT_INTERFACES[profession][2]));
			stoner
				.getClient()
				.queueOutgoingPacket(new SendString(line2, Professions.CHAT_INTERFACES[profession][3]));
		}
		stoner.getUpdateFlags().setUpdateRequired(true);
	}

	/**
	 * Trigger profession quickchat when leveling up
	 */
	private void triggerProfessionQuickChat(int profession) {
		try {
			// Find the corresponding chat button for this profession
			int buttonId = getProfessionChatButton(profession);
			if (buttonId != -1) {
				System.out.println("Bot triggered profession quickchat for " +
					Professions.PROFESSION_NAMES[profession] + " (button: " + buttonId + ")");

				// Use the existing profession chat system
				ProfessionsChat.handle(stoner, buttonId);
			}
		} catch (Exception e) {
			System.out.println("Error triggering profession quickchat: " + e.getMessage());
		}
	}

	/**
	 * Get the chat button ID for a profession
	 */
	private int getProfessionChatButton(int profession) {
		switch (profession) {
			case Professions.ASSAULT: return 94147;
			case Professions.VIGOUR: return 94150;
			case Professions.SAGITTARIUS: return 94156;
			case Professions.MAGE: return 94162;
			case Professions.AEGIS: return 94153;
			case Professions.LIFE: return 94148;
			case Professions.RESONANCE: return 94159;
			case Professions.WEEDSMOKING: return 94151;
			case Professions.THCHEMPISTRY: return 94154;
			case Professions.PET_MASTER: return 94157;
			case Professions.HANDINESS: return 94160;
			case Professions.CONSUMER: return 94165;
			case Professions.MERCENARY: return 94166;
			case Professions.BANKSTANDING: return 94167;
			case Professions.QUARRYING: return 94149;
			case Professions.FORGING: return 94152;
			case Professions.FISHER: return 94155;
			case Professions.FOODIE: return 94158;
			case Professions.PYROMANIAC: return 94161;
			case Professions.LUMBERING: return 94164;
			case Professions.WOODCARVING: return 94163;
			default: return -1;
		}
	}

	public void onLogin() {
		updateGradesForExperience();
		updateTotalGrade();

		for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
			update(i);
		}
	}

	public void reset(int id) {
		if (id == 3) {
			getGrades()[id] = 3;
			experience[id] = 30322.0D;
			stoner.getMaxGrades()[id] = 10;
		} else {
			getGrades()[id] = 1;
			stoner.getMaxGrades()[id] = 1;
			experience[id] = 0.0D;
		}

		update(id);
	}

	public void resetCombatStats() {
		for (int i = 0; i <= 7; i++) {
			getGrades()[i] = stoner.getMaxGrades()[i];
			update(i);
		}
	}

	public void restore() {
		for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
			getGrades()[i] = stoner.getMaxGrades()[i];
			update(i);
		}
	}

	public void setExpLock(boolean locked) {
		expLock = locked;
	}

	public void setGrade(int id, long grade) {
		getGrades()[id] = (grade);
		update(id);
	}

	public void update() {
		for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {

			update(i);
		}
	}

	public void update(int id) {
		stoner.send(new SendProfession(id, (int) getGrades()[id], (int) experience[id]));
		if (stoner.isAdvanceColors()) {
			stoner.send(
				new SendColor(Professions.REFRESH_DATA[id][1], Advance.professionTierColor(stoner, id)));
			stoner.send(
				new SendColor(Professions.REFRESH_DATA[id][0], Advance.professionTierColor(stoner, id)));
		} else {
			stoner.send(new SendColor(Professions.REFRESH_DATA[id][1], 0x070707));
			stoner.send(new SendColor(Professions.REFRESH_DATA[id][0], 0x070707));
		}

	}

	public void resetColors() {
		for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
			if (stoner.isAdvanceColors()) {
				stoner.send(
					new SendColor(Professions.REFRESH_DATA[i][1], Advance.professionTierColor(stoner, i)));
				stoner.send(
					new SendColor(Professions.REFRESH_DATA[i][0], Advance.professionTierColor(stoner, i)));
			} else {
				stoner.send(new SendColor(Professions.REFRESH_DATA[i][1], 0x070707));
				stoner.send(new SendColor(Professions.REFRESH_DATA[i][0], 0x070707));
			}
		}
	}

	public void updateGradesForExperience() {
		int capGrade = EXP_FOR_GRADE.length - 1;
		for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
			stoner.getMaxGrades()[i] = getGradeForExperience(i, experience[i]);
			// Ensure current grade matches max grade
			if (getGrades()[i] < stoner.getMaxGrades()[i]) {
				getGrades()[i] = stoner.getMaxGrades()[i];
			}
		}
	}


	public void updateTotalGrade() {
		totalGrade = 0;

		for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
			// Base grade from current max grade
			long baseGrade = stoner.getMaxGrades()[i];

			// Additional grades from advancements (1 advance = 420 grades)
			long advancementGrades = stoner.getProfessionAdvances()[i] * 420L;

			// Total effective grade for this profession
			long effectiveGrade = baseGrade + advancementGrades;

			totalGrade += effectiveGrade;
		}
	}
}