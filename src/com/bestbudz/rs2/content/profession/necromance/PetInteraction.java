package com.bestbudz.rs2.content.profession.necromance;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class PetInteraction
{

	public static final int BURYING_ANIMATION = 827;

	// Pet training using bones/items on pets
	public static boolean bury(Stoner stoner, int id, int amount) {
		Bones bones = Bones.forId(id);
		if (bones == null) {
			return false;
		}

		if (stoner.getActivePets().isEmpty()) {
			stoner.send(new SendMessage("You need an active pet to train with this item."));
			return false;
		}

		if (stoner.getProfession().locked()) {
			return true;
		}

		stoner.getProfession().lock(4);
		stoner.getCombat().reset();
		stoner.getUpdateFlags().sendAnimation(2780, 0);

		// Train the first active pet
		String petName = stoner.getActivePets().get(0).getDefinition().getName();
		stoner.send(new SendMessage("You train " + petName + " with " + Item.getDefinition(bones.id).getName() + "."));
		stoner.getBox().remove(bones.getId(), 1);

		// Award pet training experience
		stoner.getProfession().addExperience(5, bones.experience);

		return true;
	}

	// Keep enum for pet training items
	public enum Bones {
		// Training items for pets (reuse bone IDs)
		NORMAL_BONES(526, 15.0D),     // Basic training treat
		WOLF_BONES(2859, 15.0D),      // Basic training treat
		BAT_BONES(530, 15.0D),        // Basic training treat
		BIG_BONES(532, 35.0D),        // Good training treat
		BABYDRAGON_BONES(534, 50.0D), // Great training treat
		DRAGON_BONES(536, 100.0D),    // Excellent training treat
		DAGG_BONES(6729, 125.0D),     // Premium training treat
		OURG_BONES(4834, 150.0D),     // Elite training treat
		LONG_BONE(10976, 200.0D),     // Master training treat
		FISH_BONES(6904, 175.0D),     // Special training treat
		SKELETAL_WYVERN_BONES(6812, 185.0D), // Rare training treat
		LAVA_DRAGON_BONES(11943, 250.0D);    // Legendary training treat

		private final int id;
		private final double experience;

		Bones(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public static Bones forId(int id) {
			for (Bones b : values()) {
				if (b.getId() == id) {
					return b;
				}
			}
			return null;
		}

		public int getId() {
			return id;
		}

		public double getExperience() {
			return experience;
		}
	}
}