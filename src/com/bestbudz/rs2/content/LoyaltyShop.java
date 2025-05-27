package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class LoyaltyShop {

	public static enum TitleButton {

		/**
		 * Main Titles
		 */
		AMNESIA(214231, StonerTitle.create("Amnesia", 0xB0720E, false), 10),
		HAZE(214239, StonerTitle.create("Haze", 0xB0720E, false), 10),
		KUSH(214247, StonerTitle.create("Kush", 0xB0720E, false), 10),
		CHEESE(214255, StonerTitle.create("Cheese", 0xB0720E, false), 10),
		DIESEL(215007, StonerTitle.create("Diesel", 0xB0720E, false), 10),
		Jack_THE_RIPPER(215015, StonerTitle.create("Jack The Ripper", 0xB0720E, false), 20),
		DURBAN(215023, StonerTitle.create("Durban", 0xB0720E, false), 20),
		MASTER_KUSH(215031, StonerTitle.create("Master Kush", 0xB0720E, false), 20),
		SUPER_SILVER(215039, StonerTitle.create("Super Silver", 0xB0720E, false), 20),
		OG_KUSH(215047, StonerTitle.create("OG Kush", 0xB0720E, false), 20),
		JACK_HERER(215055, StonerTitle.create("Jack Herer", 0xB0720E, false), 20),
		BANANA_KUSH(215063, StonerTitle.create("Banana Kush", 0xB0720E, false), 20),
		PURPLE_HAZE(215071, StonerTitle.create("Purple Haze", 0xB0720E, false), 20),
		CHOCOLOPE(215079, StonerTitle.create("Chocolope", 0xB0720E, false), 20),
		POWERPLANT(215087, StonerTitle.create("PowerPlant", 0xB0720E, false), 20),
		BUBBLE_GUM(215095, StonerTitle.create("Bubble Gum", 0xB0720E, false), 20),
		BUBBA_KUSH(215103, StonerTitle.create("Bubba Kush", 0xB0720E, false), 20),
		CHEESE_HAZE(215111, StonerTitle.create("Cheese Haze", 0xB0720E, false), 20),
		ZKITTLEZ(215119, StonerTitle.create("Zkittlez", 0xB0720E, false), 20),
		GORILLAGLUE(215127, StonerTitle.create("Gorilla Glue", 0xB0720E, false), 20),
		BEST_BUD(215135, StonerTitle.create("Best Bud", 0xB0720E, false), 420),
		WALDO(215143, StonerTitle.create("Waldo", 0xB0720E, false), 420),
		FOURTWENTY(215151, StonerTitle.create("420", 0xB0720E, false), 420), 

		/**
		 * Achievement Titles
		 */
		SKELETAL(215164, StonerTitle.create("Skeletal", 0xB0720E, false), AchievementList.KILL_250_SKELETAL_WYVERNS),
		MULTI_TASK(215180, StonerTitle.create("Multi-Task", 0xB0720E, false), AchievementList.COMPLETE_100_MERCENARY_TASKS),
		PET(215188, StonerTitle.create("Pet", 0xB0720E, false), AchievementList.OBTAIN_10_BOSS_PET),
		TZTOK(215196, StonerTitle.create("TzTok", 0xB0720E, false), AchievementList.OBTAIN_50_FIRECAPES),
		BIG_BEAR(215212, StonerTitle.create("Big Bear", 0xB0720E, false), AchievementList.KILL_100_CALLISTO),

		/**
		 * Colors
		 */
		RED(215225, StonerTitle.create("null", 0xC22323, false), 500),
		GREEN(215233, StonerTitle.create("null", 0x4DFF00, false), 500),
		BLUE(215241, StonerTitle.create("null", 0x2AA4C9, false), 500),
		YELLOW(215249, StonerTitle.create("null", 0xC9BC28, false), 500),
		ORANGE(216001, StonerTitle.create("null", 0xB0720E, false), 500),
		PURPLE(216009, StonerTitle.create("null", 0xC931E8, false), 500),
		PINK(216017, StonerTitle.create("null", 0xF52CD7, false), 500),
		WHITE(216025, StonerTitle.create("null", 0xFFFFFF, false), 500);

		private int button;
		private StonerTitle title;
		private Object price;

		private TitleButton(int button, StonerTitle title, Object price) {
		this.button = button;
		this.title = title;
		this.price = price;
		}

		public int getButton() {
		return button;
		}

		public Object getPrice() {
		return price;
		}

		public StonerTitle getTitle() {
		return title;
		}

		public static TitleButton forButton(int button) {
		for (TitleButton titleButton : values()) {
			if (titleButton.getButton() == button) {
				return titleButton;
			}
		}
		return null;
		}
	}

	public static void load(Stoner stoner) {
	for (TitleButton titleButton : TitleButton.values()) {
		if (stoner.unlockedTitles.contains(titleButton.getTitle())) {
			stoner.send(new SendConfig(1040 + titleButton.ordinal(), 1));
		} else {
			stoner.send(new SendConfig(1040 + titleButton.ordinal(), 0));
		}
	}
	}

	public static boolean handleButtons(Stoner stoner, int buttonId) {
	TitleButton button = TitleButton.forButton(buttonId);

	if (button == null) {
		return false;
	}

	if (stoner.getStonerTitle() != null && stoner.getStonerTitle().equals(button.getTitle())) {
		stoner.send(new SendMessage("@dre@You already have this set as your title."));
		return true;
	}

	if (!stoner.unlockedTitles.contains(button.getTitle())) {
		if (button.getPrice() instanceof Integer) {
			if (stoner.getCredits() < Integer.parseInt(String.valueOf(button.getPrice()))) {
				stoner.send(new SendMessage("<col=128>You do not have enough CannaCredits to buy this."));
				return true;
			}
		}

		if (button.getPrice() instanceof AchievementList) {
			AchievementList achievement = ((AchievementList) button.getPrice());
			if (stoner.getStonerAchievements().get(achievement) != achievement.getCompleteAmount()) {
				stoner.send(new SendMessage("<col=128>Completion of the achievement '" + achievement.getName() + "' is required."));
				return true;
			}
		}

		if (button.ordinal() >= TitleButton.RED.ordinal() && button.ordinal() <= TitleButton.WHITE.ordinal()) {
			if (button.getTitle().getColor() == stoner.getStonerTitle().getColor()) {
				stoner.send(new SendMessage("<col=128>This is already your title color."));
				return true;
			}
			if (!stoner.getBox().hasItemAmount(995, Integer.parseInt(String.valueOf(button.getPrice())))) {
				stoner.send(new SendMessage("<col=128>You need more BestBucks to buy this color."));
				return true;
			}
			stoner.setStonerTitle(StonerTitle.create(stoner.getStonerTitle().getTitle(), button.getTitle().getColor(), stoner.getStonerTitle().isSuffix()));
			stoner.setAppearanceUpdateRequired(true);
			stoner.getBox().remove(995, Integer.parseInt(String.valueOf(button.getPrice())));
			stoner.send(new SendMessage("<col=128>You have changed your title color to '<col=" + Integer.toHexString(button.getTitle().getColor()) + ">" + button.name().toLowerCase().replaceAll("_", " ") + "</col>'!"));
		} else {
			stoner.setStonerTitle(button.getTitle());
			stoner.setAppearanceUpdateRequired(true);
			if (button.getPrice() instanceof Integer) {
				stoner.setCredits(stoner.getCredits() - Integer.parseInt(String.valueOf(button.getPrice())));
			}
			stoner.unlockedTitles.add(button.getTitle());
			stoner.send(new SendConfig(1040 + button.ordinal(), 1));
			stoner.send(new SendMessage("<col=128>You have unlocked the title '<col=" + Integer.toHexString(button.getTitle().getColor()) + ">" + button.getTitle().getTitle() + "</col>'!"));
		}
		return true;
	} else {
		stoner.setStonerTitle(button.getTitle());
		stoner.setAppearanceUpdateRequired(true);
		stoner.getBox().remove(995, Integer.parseInt(String.valueOf(button.getPrice())));
		stoner.send(new SendMessage("<col=128>You have changed your title to '<col=" + Integer.toHexString(button.getTitle().getColor()) + ">" + button.getTitle().getTitle() + "</col>'."));
		return true;
	}
	}
}