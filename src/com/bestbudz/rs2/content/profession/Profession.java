package com.bestbudz.rs2.content.profession;

import static com.bestbudz.rs2.content.profession.Professions.DUNGEONEERING;

//import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.Advance;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
//import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendExpCounter;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterfaceConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendProfession;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class Profession {

	public static final int[] EXP_FOR_GRADE = { 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431 };

	private double[] experience = new double[Professions.PROFESSION_COUNT];
	private int combatGrade = 0;
	private int totalGrade = 0;
	private final Stoner stoner;

	private boolean expLock = false;

	private long lock = 0L;

	/**
	 * Melee experience
	 */
	private int meleeExp = 300;

	/**
	 * Range experience
	 */
	private int sagittariusExp = 210;

	/**
	 * Mage experience
	 */
	private int mageExp = 120;

	/**
	 * Constructs a new profession instance
	 * 
	 * @param stoner
	 *                   The stoner creating the profession instance
	 */
	public Profession(Stoner stoner) {
	this.stoner = stoner;
	for (int i = 0; i < Professions.PROFESSION_COUNT; i++)
		if (i == 3) {
			getGrades()[i] = 3;
			experience[i] = 174.0D;
		} else {
			getGrades()[i] = 1;
			experience[i] = 0.0D;
		}
	}

	/**
	 * Adds combat experience after dealing damage
	 * 
	 * @param type
	 *                 The type of combat the stoner dealt damage with
	 * @param hit
	 *                 The amount of damage dealt
	 */
	public void addCombatExperience(CombatTypes type, int hit) {
	if ((expLock) || (stoner.getMage().isDFireShieldEffect())) {
		return;
	}

	if (type == CombatTypes.MAGE) {
		stoner.getMage().getSpellCasting().addSpellExperience();
	}

	if (hit <= 0) {
		return;
	}

	double exp = hit * 4.0D;
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
			addExperience(4, exp * sagittariusExp);
			break;
		case AGGRESSIVE:
			addExperience(4, exp * sagittariusExp);
			break;
		case CONTROLLED:
			addExperience(4, exp * sagittariusExp);
			break;
		case DEFENSIVE:
			addExperience(4, exp / 2.0D * sagittariusExp);
			addExperience(1, exp / 2.0D * sagittariusExp);
			break;
		}
		break;
	}

	addExperience(3, hit * meleeExp * 1.33D);
	}

	/**
	 * Adds experience to the experience array
	 * 
	 * @param id
	 *                       The id of the profession to add experience too
	 * @param experience
	 *                       The amount of experience too add
	 * @return
	 */
	public double addExperience(int id, double experience) {
	if ((expLock) && (id <= 6)) {
		return 0;
	}

	experience = experience * Professions.EXPERIENCE_RATES[id] * 1.0D;

	this.experience[id] += experience;

	if (stoner.getMaxGrades()[id] == 99 && id != DUNGEONEERING || stoner.getMaxGrades()[id] == 120 && id == DUNGEONEERING) {
		if (this.experience[id] < 420_000_000) {
			stoner.send(new SendExpCounter(id, (int) experience));
		}

		if (this.experience[id] >= 420000000) {
			this.experience[id] = 420000000;
		}
		update(id);
		return experience;
	}

	int newGrade = getGradeForExperience(id, this.experience[id]);

	if (newGrade > 99 && id != DUNGEONEERING) {
		newGrade = 99;
	}

	if (newGrade > 120 && id == DUNGEONEERING) {
		newGrade = 120;
	}

	if (stoner.getMaxGrades()[id] < newGrade) {
		getGrades()[id] = ((short) (newGrade - (stoner.getMaxGrades()[id] - getGrades()[id])));
		stoner.getMaxGrades()[id] = ((short) (newGrade));

		updateTotalGrade();

		onUpgrade(newGrade, id);

		stoner.setAppearanceUpdateRequired(true);

		if (id == DUNGEONEERING ? newGrade == 120 : newGrade == 99) {
			World.sendGlobalMessage("<col=855907><img=12> " + stoner.getUsername() + " has achieved grade " + 99 + " in " + Professions.PROFESSION_NAMES[id] + "! Advance grade: " + stoner.getProfessionAdvances()[id]);
			stoner.getUpdateFlags().sendForceMessage("SMOKE SESH!");
			stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
			stoner.getUpdateFlags().sendAnimation(new Animation(884));
		}
	}

	if (this.experience[id] >= 420000000) {
		this.experience[id] = 420000000;
	} else {
		stoner.send(new SendExpCounter(id, (int) experience));
	}

	update(id);
	return experience;
	}

	/**
	 * Deducts an amount from a profession
	 * 
	 * @param id
	 *                   The id of the profession
	 * @param amount
	 *                   The amount to remove from the profession
	 */
	public void deductFromGrade(int id, int amount) {
	getGrades()[id] = ((short) (getGrades()[id] - amount));

	if (getGrades()[id] < 0) {
		getGrades()[id] = 0;
	}

	update(id);
	}

	/**
	 * Gets the stoners combat grades
	 * 
	 * @return
	 */
	public int getCombatGrade() {
	return combatGrade;
	}

	/**
	 * Gets the stoners combat grades
	 * 
	 * @return
	 */
	public int calcCombatGrade() {
	int magLvl = stoner.getMaxGrades()[Professions.MAGE];
	int ranLvl = stoner.getMaxGrades()[Professions.SAGITTARIUS];
	int attLvl = stoner.getMaxGrades()[Professions.ASSAULT];
	int strLvl = stoner.getMaxGrades()[Professions.VIGOUR];
	int defLvl = stoner.getMaxGrades()[Professions.AEGIS];
	int hitLvl = stoner.getMaxGrades()[Professions.LIFE];
	int prayLvl = stoner.getMaxGrades()[Professions.NECROMANCE];
	int slayLvl = stoner.getMaxGrades()[Professions.MERCENARY];
	int totalAdv = stoner.getTotalAdvances();
	double mag = magLvl * 1.5;
	double ran = ranLvl * 1.5;
	double attstr = attLvl + strLvl;

	combatGrade = 0;

	if (ran > attstr && ran > mag) { // stoner is sagittarius class
		combatGrade = (int) (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((totalAdv) * 0.50) + ((slayLvl) * 0.30) + ((prayLvl / 2) * 0.25) + ((ranLvl) * 0.4875));
	} else if (mag > attstr) { // stoner is mage class
		combatGrade = (int) (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((totalAdv) * 0.50) + ((slayLvl) * 0.30) + ((prayLvl / 2) * 0.25) + ((magLvl) * 0.4875));
	} else {
		combatGrade = (int) (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((totalAdv) * 0.50) + ((slayLvl) * 0.30) + ((prayLvl / 2) * 0.25) + ((attLvl) * 0.325) + ((strLvl) * 0.325));
	}

	return combatGrade;
	}

	/**
	 * Gets the stoners current experience
	 * 
	 * @return
	 */
	public double[] getExperience() {
	return experience;
	}

	/**
	 * Gets a grade based on the amount of experience provided
	 * 
	 * @param id
	 *                       The profession to check the grade for
	 * @param experience
	 *                       The amount of experience to check for a grade
	 * @return The grade based on the provided experience
	 */
	public byte getGradeForExperience(final int id, double experience) {
	if (experience >= EXP_FOR_GRADE[98]) {
		return 99;
	}
	return binarySearch(experience, 0, 98);
	}

	private static byte binarySearch(double experience, int min, int max) {
	int mid = (min + max) / 2;
	double value = EXP_FOR_GRADE[mid];

	if (value > experience) {
		return binarySearch(experience, min, mid - 1);
	} else if (value == experience || EXP_FOR_GRADE[mid + 1] > experience) {
		return (byte) (mid + 1);
	}

	return binarySearch(experience, mid + 1, max);
	}

	/**
	 * Gets the stoners grades
	 * 
	 * @return
	 */
	public short[] getGrades() {
	return stoner.getGrades();
	}

	/**
	 * Gets the total amount of combat experience
	 * 
	 * @return The total amount of combat experience
	 */
	public long getTotalCombatExperience() {
	long total = 0L;

	for (int i = 0; i <= 6; i++) {
		total = (long) (total + experience[i]);
	}

	return total;
	}

	/**
	 * Gets the stoners total experience
	 * 
	 * @return The stoners total experience
	 */
	public long getTotalExperience() {
	long total = 0L;

	for (int ii = 0; ii < experience.length; ii++) {
		if (ii == Professions.SUMMONING || ii == Professions.CONSTRUCTION || ii == Professions.DUNGEONEERING) {
			continue;
		}

		double i = experience[ii];
		total = (long) (total + (i > 420_000_000.0D ? 420_000_000.0D : i));
	}

	return total;
	}

	/**
	 * Gets the stoners total grade
	 * 
	 * @return
	 */
	public int getTotalGrade() {
	return totalGrade;
	}

	/**
	 * Gets the amount of experience for a grade
	 * 
	 * @param professionId
	 *                         The id of the profession
	 * @param grade
	 *                         The grade the stoner is getting the experience for
	 * @return The amount of experience for a grade
	 */
	public int getXPForGrade(int professionId, int grade) {
	int points = 0;
	int output = 0;

	for (int lvl = 1; lvl <= grade; lvl++) {
		points = (int) (points + Math.floor(lvl + 300.0D * Math.pow(2.0D, lvl / 7.0D)));
		if ((lvl >= grade) || (lvl == 99) && professionId != DUNGEONEERING || (lvl == 120) && professionId == DUNGEONEERING)
			return output;
		output = (int) Math.floor(points / 4);
	}
	return 0;
	}

	/**
	 * Gets if the stoner has a combat grades
	 * 
	 * @return
	 */
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

	/**
	 * Gets if the stoner has at least 2 99 professions
	 * 
	 * @return The stoner has at least 2 99's
	 */
	public boolean hasTwo99s() {
	byte c = 0;
	int index = 0;

	for (int i : stoner.getMaxGrades()) {
		if ((i == 99) && index != Professions.DUNGEONEERING || i == 120 && index == Professions.DUNGEONEERING) {
			if (++c == 2) {
				return true;
			}
		}

		index++;
	}

	return false;
	}

	/**
	 * Gets if the stoner has at least advanced 5 times
	 * 
	 * @return The stoner has at least advanced 5 times
	 */
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

	/**
	 * Gets if the experience is locked
	 * 
	 * @return The experience is locked
	 */
	public boolean isExpLocked() {
	return expLock;
	}

	/**
	 * Locks the stoners profession from performing another profession for a certain
	 * delay
	 * 
	 * @param delay
	 *                  The delay to lock the professions
	 */
	public void lock(int delay) {
	lock = (World.getCycles() + delay);
	}

	/**
	 * Gets if the stoners profession is locked
	 * 
	 * @return The stoners professions are locked
	 */
	public boolean locked() {
	return lock > World.getCycles();
	}

	/**
	 * Actions that take place on up-grade
	 * 
	 * @param lvl
	 *                       The grade the stoner has just achieved
	 * @param profession
	 *                       The profession the stoner achieved the grade in
	 */
	public void onUpgrade(int lvl, int profession) {
	stoner.getUpdateFlags().sendGraphic(Professions.UPGRADE_GRAPHIC);
	String line1 = "Well done bud! You have advanced " + Professions.PROFESSION_NAMES[profession] + " to grade " + lvl + "!";
	String line2 = "You have reached grade " + lvl + "!";

	stoner.getClient().queueOutgoingPacket(new SendMessage(line1));

	// stoner.getClient().queueOutgoingPacket(new
	// SendChatBoxInterface(Professions.CHAT_INTERFACES[profession][1]));

	if (profession == Professions.CULTIVATION) {
		stoner.send(new SendInterfaceConfig(4888, 200, 5340));
	}

	if ((profession != 4) && (profession != 14) && (profession != 17) && (profession != 19)) {
		stoner.getClient().queueOutgoingPacket(new SendString("<col=369>" + line1, Professions.CHAT_INTERFACES[profession][1] + 1));
		stoner.getClient().queueOutgoingPacket(new SendString(line2, Professions.CHAT_INTERFACES[profession][1] + 2));
	} else {
		stoner.getClient().queueOutgoingPacket(new SendString("<col=369>" + line1, Professions.CHAT_INTERFACES[profession][2]));
		stoner.getClient().queueOutgoingPacket(new SendString(line2, Professions.CHAT_INTERFACES[profession][3]));
	}

	stoner.getUpdateFlags().setUpdateRequired(true);
	}

	/**
	 * Updates the professions on login
	 */
	public void onLogin() {
	updateGradesForExperience();
	updateTotalGrade();

	for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
		update(i);
	}
	}

	/**
	 * Resets the stoners profession back to default
	 * 
	 * @param id
	 *               The id of the profession to reset
	 */
	public void reset(int id) {
	if (id == 3) {
		getGrades()[id] = 10;
		experience[id] = 1154.0D;
		stoner.getMaxGrades()[id] = 10;
	} else {
		getGrades()[id] = 1;
		stoner.getMaxGrades()[id] = 1;
		experience[id] = 0.0D;
	}

	update(id);
	}

	/**
	 * Resets the stoners combat stats, assault through mage
	 */
	public void resetCombatStats() {
	for (int i = 0; i <= 7; i++) {
		getGrades()[i] = stoner.getMaxGrades()[i];
		update(i);
	}
	}

	/**
	 * Restores the stoners grades back to normal
	 */
	public void restore() {
	for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
		getGrades()[i] = stoner.getMaxGrades()[i];
		update(i);
	}
	}

	/**
	 * Sets the stoners current experience
	 * 
	 * @param experience
	 */
	public void setExperience(double[] experience) {
	this.experience = experience;
	}

	/**
	 * Sets the experience locked or unlocked
	 * 
	 * @param locked
	 *                   If he experience is locked or unlocked
	 */
	public void setExpLock(boolean locked) {
	expLock = locked;
	}

	/**
	 * Sets a grade by the id
	 * 
	 * @param id
	 *                  The id of the profession
	 * @param grade
	 *                  The grade to set the profession too
	 */
	public void setGrade(int id, int grade) {
	getGrades()[id] = ((byte) grade);
	update(id);
	}

	/**
	 * Updates all of the stoners professions
	 */
	public void update() {
	for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
		update(i);
	}
	}

	/**
	 * Updates a profession by the id
	 * 
	 * @param id
	 *               The id of the profession being updated
	 */
	public void update(int id) {
	stoner.send(new SendProfession(id, getGrades()[id], (int) experience[id]));
	if (stoner.isAdvanceColors()) {
		stoner.send(new SendColor(Professions.REFRESH_DATA[id][1], Advance.professionTierColor(stoner, id)));
		stoner.send(new SendColor(Professions.REFRESH_DATA[id][0], Advance.professionTierColor(stoner, id)));
	} else {
		stoner.send(new SendColor(Professions.REFRESH_DATA[id][1], 0x070707));
		stoner.send(new SendColor(Professions.REFRESH_DATA[id][0], 0x070707));
	}
	}

	/**
	 * Resets the advance colors
	 */
	public void resetColors() {
	for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
		if (stoner.isAdvanceColors()) {
			stoner.send(new SendColor(Professions.REFRESH_DATA[i][1], Advance.professionTierColor(stoner, i)));
			stoner.send(new SendColor(Professions.REFRESH_DATA[i][0], Advance.professionTierColor(stoner, i)));
		} else {
			stoner.send(new SendColor(Professions.REFRESH_DATA[i][1], 0x070707));
			stoner.send(new SendColor(Professions.REFRESH_DATA[i][0], 0x070707));
		}
	}
	}

	/**
	 * Updates all of the grades for the stoners experience
	 */
	public void updateGradesForExperience() {
	for (int i = 0; i < Professions.PROFESSION_COUNT; i++)
		stoner.getMaxGrades()[i] = getGradeForExperience(i, experience[i]);
	}

	/**
	 * Updates the total grade
	 */
	public void updateTotalGrade() {
	totalGrade = 0;

	for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
		if (i == Professions.CONSTRUCTION || i == Professions.SUMMONING || i == Professions.DUNGEONEERING) {
			continue;
		}
		totalGrade += stoner.getMaxGrades()[i];
	}

	}
}
