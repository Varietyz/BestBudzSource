package com.bestbudz.core.definitions;

public class PotionDefinition {

	public enum PotionTypes {

		NORMAL,
		RESTORE,
		ANTIFIRE,
		SUPER_ANTIFIRE
	}

	public class ProfessionData {

		private byte professionId;
		private byte add;
		private double modifier;

		public int getAdd() {
		return add;
		}

		public double getModifier() {
		return modifier;
		}

		public int getProfessionId() {
		return professionId;
		}
	}

	private short id;
	private String name;
	private short replaceId;

	private PotionTypes potionType;

	private ProfessionData[] professionData;

	public int getId() {
	return id;
	}

	public String getName() {
	return name;
	}

	public PotionTypes getPotionType() {
	return potionType;
	}

	public int getReplaceId() {
	return replaceId;
	}

	public ProfessionData[] getProfessionData() {
	return professionData;
	}

	/**
	 * @param name
	 *                 the name to set
	 */
	public void setName(String name) {
	this.name = name;
	}
}
