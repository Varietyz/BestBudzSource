package com.bestbudz.rs2.content.profession.cultivation;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class Seedling {

	private final Stoner stoner;

	public Seedling(Stoner stoner) {
	this.stoner = stoner;
	}

	public boolean waterSeedling(int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
	SeedlingData seedlingData = SeedlingData.getUnwatered(itemUsed);
	if (seedlingData == null)
		seedlingData = SeedlingData.getUnwatered(usedWith);
	if (seedlingData == null || (!GameDefinitionLoader.getItemDef(itemUsed).getName().toLowerCase().contains("watering") && !GameDefinitionLoader.getItemDef(itemUsed).getName().toLowerCase().contains("watering")))
		return false;

	stoner.send(new SendMessage("You water the " + GameDefinitionLoader.getItemDef(seedlingData.getSeedId()) + "."));
	stoner.getBox().remove(seedlingData.getUnwateredSeedlingId(), 1);
	stoner.getBox().add(seedlingData.getWateredSeedlingId(), 1);
	return true;

	}

	public boolean placeSeedInPot(int itemUsed, int usedWith, int itemUsedSlot, int usedWithSlot) {
	SeedlingData seedlingData = SeedlingData.getSeed(itemUsed);
	if (seedlingData == null)
		seedlingData = SeedlingData.getUnwatered(usedWith);
	if (seedlingData == null || (itemUsed != 5354 && usedWith != 5354))
		return false;
	stoner.getBox().remove(seedlingData.getSeedId(), 1);
	stoner.getBox().add(seedlingData.getUnwateredSeedlingId(), 1);
	stoner.send(new SendMessage("You sow some maple tree seeds in the plantpots."));
	stoner.send(new SendMessage("They need watering before they will grow."));
	return true;
	}

	public boolean fillPotWithSoil(int itemId, int objectX, int objectY) {
	if (itemId != 5350)
		return false;

	if (!stoner.getEquipment().isWearingItem(CultivationConstants.TROWEL)) {
		stoner.send(new SendMessage("You must be wearing a tool ring to fill this pot with soil."));
		return true;
	}
	stoner.getBox().remove(itemId, 1);
	stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.FILLING_POT_ANIM));
	stoner.send(new SendMessage("You fill the empty plant pot with soil."));
	stoner.getBox().add(5354, 1);
	return true;
	}

	public enum SeedlingData {
		OAK(5312, 5358, 5364, 5370),
		WILLOW(5313, 5359, 5365, 5371),
		MAPLE(5314, 5360, 5366, 5372),
		YEW(5315, 5361, 5367, 5373),
		MAGE(5316, 5362, 5368, 5374),
		SPIRIT(5317, 5363, 5369, 5375),
		APPLE(5283, 5480, 5488, 5496),
		BANANA(5284, 5481, 5489, 5497),
		ORANGE(5285, 5482, 5490, 5498),
		CURRY(5286, 5483, 5491, 5499),
		PINEAPPLE(5287, 5484, 5492, 5500),
		PAPAYA(5288, 5485, 5493, 5501),
		PALM(5289, 5486, 5494, 5502),
		CALQUAT(5290, 5487, 5495, 5503);

		private static final Map<Integer, SeedlingData> seeds = new HashMap<Integer, SeedlingData>();
		private static final Map<Integer, SeedlingData> unwatered = new HashMap<Integer, SeedlingData>();
		private static final Map<Integer, SeedlingData> watered = new HashMap<Integer, SeedlingData>();

		static {
			for (SeedlingData data : SeedlingData.values()) {
				seeds.put(data.seedId, data);
				unwatered.put(data.unwateredSeedlingId, data);
				watered.put(data.wateredSeedlingId, data);
			}
		}

		private final int seedId;
		private final int unwateredSeedlingId;
		private final int wateredSeedlingId;
		private final int saplingId;

		SeedlingData(int seedId, int unwateredSeedlingId, int wateredSeedlingId, int saplingId) {
		this.seedId = seedId;
		this.unwateredSeedlingId = unwateredSeedlingId;
		this.wateredSeedlingId = wateredSeedlingId;
		this.saplingId = saplingId;
		}

		public static SeedlingData getSeed(int seedId) {
		return seeds.get(seedId);
		}

		public static SeedlingData getUnwatered(int seedId) {
		return unwatered.get(seedId);
		}

		public static SeedlingData getWatered(int seedId) {
		return watered.get(seedId);
		}

		public int getSeedId() {
		return seedId;
		}

		public int getUnwateredSeedlingId() {
		return unwateredSeedlingId;
		}

		public int getWateredSeedlingId() {
		return wateredSeedlingId;
		}

		public int getSaplingId() {
		return saplingId;
		}
	}
}
