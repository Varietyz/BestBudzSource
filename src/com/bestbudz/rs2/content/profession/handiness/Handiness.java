package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;
import java.util.List;

public enum Handiness {
	SINGLETON;

	public boolean itemOnItem(Stoner stoner, Item use, Item with) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		if (isAmuletStringing(use, with)) {
			AmuletStringing.stringAmulet(stoner, use.getId(), with.getId());
			return true;
		}

		return autoCraftAllAvailableItems(stoner);
	}

	public boolean handleObjectClick(Stoner stoner, int objectId) {
		if (stoner.getProfession().locked()) {
			return false;
		}

		switch (objectId) {
			case 2644:
				return autoCraftAllSpinning(stoner);
			case 3998:
				return autoCraftAllGlass(stoner);
			case 14888:
				return autoCraftAllTanning(stoner);
			case 11666:
				return autoCraftAllJewelry(stoner);
			default:

				return autoCraftAllAvailableItems(stoner);
		}
	}

	private boolean autoCraftAllAvailableItems(Stoner stoner) {
		List<HandinessTask> availableTasks = new ArrayList<>();

		if (stoner.getBox().contains(new Item(1755))) {
			int[] gemIds = {1625, 1627, 1629, 1623, 1621, 1619, 1617, 1631, 6571};
			for (int gemId : gemIds) {
				if (stoner.getBox().contains(new Item(gemId))) {
					availableTasks.add(new HandinessTask(HandinessType.GEM_CUTTING, gemId, 0));
				}
			}
		}

		for (Spinnable spinnable : Spinnable.values()) {
			if (stoner.getBox().contains(spinnable.getItem())) {
				availableTasks.add(new HandinessTask(HandinessType.SPINNING, spinnable.getItem().getId(), 0));
			}
		}

		if (stoner.getBox().contains(new Item(1785))) {
			for (Glass glass : Glass.values()) {
				if (stoner.getBox().contains(new Item(glass.getMaterialId()))) {
					availableTasks.add(new HandinessTask(HandinessType.GLASS_BLOWING, glass.getRewardId(), 0));
				}
			}
		}

		for (Jewelry jewelry : Jewelry.values()) {
			boolean hasAllMaterials = true;
			for (int materialId : jewelry.getMaterialsRequired()) {
				if (!stoner.getBox().contains(new Item(materialId))) {
					hasAllMaterials = false;
					break;
				}
			}
			if (hasAllMaterials) {
				availableTasks.add(new HandinessTask(HandinessType.JEWELRY, jewelry.getReward().getId(), 0));
			}
		}

		if (stoner.getBox().contains(new Item(1733)) && stoner.getBox().contains(new Item(1734))) {
			for (Craftable craftable : Craftable.values()) {
				if (stoner.getBox().contains(new Item(craftable.getItemId()))) {
					availableTasks.add(new HandinessTask(HandinessType.LEATHER_CRAFTING, craftable.getOutcome(), 0));
				}
			}
		}

		if (availableTasks.isEmpty()) {
			stoner.send(new SendMessage("@red@No handiness materials found in inventory."));
			return true;
		}

		startMultiHandiness(stoner, availableTasks);
		return true;
	}

	private boolean autoCraftAllSpinning(Stoner stoner) {
		List<HandinessTask> availableSpinning = new ArrayList<>();

		for (Spinnable spinnable : Spinnable.values()) {
			if (stoner.getBox().contains(spinnable.getItem())) {
				availableSpinning.add(new HandinessTask(HandinessType.SPINNING, spinnable.getItem().getId(), 0));
			}
		}

		if (availableSpinning.isEmpty()) {
			stoner.send(new SendMessage("@red@No spinnable materials found in inventory."));
			return true;
		}

		startMultiHandiness(stoner, availableSpinning);
		return true;
	}

	private boolean autoCraftAllGlass(Stoner stoner) {
		List<HandinessTask> availableGlass = new ArrayList<>();

		if (stoner.getBox().contains(new Item(1783)) && stoner.getBox().contains(new Item(1781))) {
			availableGlass.add(new HandinessTask(HandinessType.GLASS_MELTING, 1781, 0));
		}

		if (stoner.getBox().contains(new Item(1785))) {
			for (Glass glass : Glass.values()) {
				if (stoner.getBox().contains(new Item(glass.getMaterialId()))) {
					availableGlass.add(new HandinessTask(HandinessType.GLASS_BLOWING, glass.getRewardId(), 0));
				}
			}
		}

		if (availableGlass.isEmpty()) {
			stoner.send(new SendMessage("@red@No glass materials found in inventory."));
			return true;
		}

		startMultiHandiness(stoner, availableGlass);
		return true;
	}

	private boolean autoCraftAllTanning(Stoner stoner) {
		List<HandinessTask> availableTanning = new ArrayList<>();

		for (HideTanData hide : HideTanData.values()) {
			if (stoner.getBox().contains(new Item(hide.getItemId()))) {
				availableTanning.add(new HandinessTask(HandinessType.HIDE_TANNING, hide.getItemId(), 0));
			}
		}

		if (availableTanning.isEmpty()) {
			stoner.send(new SendMessage("@red@No hides found in inventory."));
			return true;
		}

		startMultiHandiness(stoner, availableTanning);
		return true;
	}

	private boolean autoCraftAllJewelry(Stoner stoner) {
		List<HandinessTask> availableJewelry = new ArrayList<>();

		for (Jewelry jewelry : Jewelry.values()) {
			boolean hasAllMaterials = true;
			for (int materialId : jewelry.getMaterialsRequired()) {
				if (!stoner.getBox().contains(new Item(materialId))) {
					hasAllMaterials = false;
					break;
				}
			}
			if (hasAllMaterials) {
				availableJewelry.add(new HandinessTask(HandinessType.JEWELRY, jewelry.getReward().getId(), 0));
			}
		}

		if (availableJewelry.isEmpty()) {
			stoner.send(new SendMessage("@red@No jewelry materials found in inventory."));
			return true;
		}

		startMultiHandiness(stoner, availableJewelry);
		return true;
	}

	private static class HandinessTask {
		final HandinessType type;
		final int itemId;
		final int index;

		HandinessTask(HandinessType type, int itemId, int index) {
			this.type = type;
			this.itemId = itemId;
			this.index = index;
		}
	}

	private void startMultiHandiness(Stoner stoner, List<HandinessTask> handinessTasks) {
		stoner.send(new SendMessage("@gre@Auto-handiness started - found " + handinessTasks.size() + " craftable items!"));
		TaskQueue.queue(new MultiHandinessTask(stoner, handinessTasks));
	}

	private boolean isAmuletStringing(Item use, Item with) {
		return (use.getId() == 1759 || with.getId() == 1759);
	}

	private static class MultiHandinessTask extends Task {
		private final Stoner stoner;
		private final List<HandinessTask> handinessTasks;
		private int currentTaskIndex = 0;

		public MultiHandinessTask(Stoner stoner, List<HandinessTask> handinessTasks) {
			super(stoner, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.PROFESSION_CREATING);
			this.stoner = stoner;
			this.handinessTasks = handinessTasks;
		}

		@Override
		public void execute() {
			if (!stoner.getBox().hasSpaceFor(new Item(1, 1))) {
				stoner.send(new SendMessage("@gre@Auto-handiness stopped - inventory is full!"));
				stop();
				return;
			}

			HandinessTask nextTask = findNextCraftableItem();
			if (nextTask == null) {
				stoner.send(new SendMessage("@gre@Auto-handiness completed - no more materials available!"));
				stop();
				return;
			}

			executeTask(nextTask);
		}

		private void executeTask(HandinessTask task) {
			stoner.getProfession().lock(2);

			switch (task.type) {
				case GEM_CUTTING:
					executeGemCutting(task.itemId);
					break;
				case SPINNING:
					executeSpinning(task.itemId);
					break;
				case GLASS_BLOWING:
					executeGlassBlowing(task.itemId);
					break;
				case GLASS_MELTING:
					executeGlassMelting();
					break;
				case HIDE_TANNING:
					executeHideTanning(task.itemId);
					break;
				case JEWELRY:
					executeJewelry(task.itemId);
					break;
				case LEATHER_CRAFTING:
					executeLeatherCrafting(task.itemId);
					break;
			}
		}

		private void executeGemCutting(int gemId) {

			stoner.getUpdateFlags().sendAnimation(new Animation(885));
			stoner.getBox().remove(new Item(gemId), true);
			stoner.getBox().remove(new Item(1755), true);
			stoner.getBox().add(new Item(1755));

			int cutGemId = getCutGemId(gemId);
			stoner.getBox().add(new Item(cutGemId));

			stoner.getProfession().addExperience(Professions.HANDINESS, 50.0);
			stoner.send(new SendMessage("You cut the gem."));
			AchievementHandler.activateAchievement(stoner, AchievementList.CUT_2500_GEMS, 1);
		}

		private void executeSpinning(int itemId) {
			Spinnable spinnable = Spinnable.forId(itemId);
			if (spinnable != null && stoner.getBox().contains(spinnable.getItem())) {
				stoner.getUpdateFlags().sendAnimation(new Animation(896));
				stoner.getBox().remove(spinnable.getItem(), true);
				stoner.getBox().add(spinnable.getOutcome());
				stoner.getProfession().addExperience(Professions.HANDINESS, spinnable.getExperience());
				stoner.send(new SendMessage("You spin the " + spinnable.getItem().getDefinition().getName().toLowerCase() +
					" into " + spinnable.getOutcome().getDefinition().getName().toLowerCase() + "."));
			}
		}

		private void executeGlassBlowing(int glassId) {
			Glass glass = Glass.forReward(glassId);
			if (glass != null && stoner.getBox().contains(new Item(glass.getMaterialId()))) {
				stoner.getUpdateFlags().sendAnimation(new Animation(884));
				stoner.getBox().remove(new Item(glass.getMaterialId()), true);
				stoner.getBox().add(new Item(glass.getRewardId()));
				stoner.getProfession().addExperience(Professions.HANDINESS, glass.getExperience());
				stoner.send(new SendMessage("You blow the glass."));
			}
		}

		private void executeGlassMelting() {
			if (stoner.getBox().contains(new Item(1783)) && stoner.getBox().contains(new Item(1781))) {
				stoner.getUpdateFlags().sendAnimation(new Animation(899));
				stoner.getBox().remove(new Item(1783), true);
				stoner.getBox().remove(new Item(1781), true);
				stoner.getBox().add(new Item(1775));
				stoner.getProfession().addExperience(Professions.HANDINESS, 120.0);
				stoner.send(new SendMessage("You heat the sand and soda ash in the furnace to make glass."));
			}
		}

		private void executeHideTanning(int hideId) {
			HideTanData hide = HideTanData.forReward((short) hideId);
			if (hide != null && stoner.getBox().contains(new Item(hide.getItemId()))) {
				stoner.getUpdateFlags().sendAnimation(new Animation(1249));
				stoner.getBox().remove(new Item(hide.getItemId()), true);
				stoner.getBox().add(new Item(hide.getOutcome()));
				stoner.getProfession().addExperience(Professions.HANDINESS, 25.0);
				stoner.send(new SendMessage("You tan the hide."));
			}
		}

		private void executeJewelry(int jewelryId) {
			Jewelry jewelry = Jewelry.forReward(jewelryId);
			if (jewelry != null) {
				boolean hasAllMaterials = true;
				for (int materialId : jewelry.getMaterialsRequired()) {
					if (!stoner.getBox().contains(new Item(materialId))) {
						hasAllMaterials = false;
						break;
					}
				}

				if (hasAllMaterials) {
					stoner.getUpdateFlags().sendAnimation(new Animation(899));
					for (int materialId : jewelry.getMaterialsRequired()) {
						if (materialId != 0) {
							stoner.getBox().remove(new Item(materialId), true);
						}
					}
					stoner.getBox().add(jewelry.getReward());
					stoner.getProfession().addExperience(Professions.HANDINESS, jewelry.getExperience());
					stoner.send(new SendMessage("You have crafted " + Utility.getAOrAn(jewelry.getReward().getDefinition().getName()) +
						" " + jewelry.getReward().getDefinition().getName() + "."));
				}
			}
		}

		private void executeLeatherCrafting(int outcomeId) {
			Craftable craftable = Craftable.forReward(outcomeId);
			if (craftable != null && stoner.getBox().contains(new Item(craftable.getItemId())) &&
				stoner.getBox().contains(new Item(1733)) && stoner.getBox().contains(new Item(1734))) {
				stoner.getUpdateFlags().sendAnimation(new Animation(1249));
				stoner.getBox().remove(new Item(craftable.getItemId()), true);
				stoner.getBox().remove(new Item(1734), true);
				stoner.getBox().add(new Item(craftable.getOutcome()));
				stoner.getProfession().addExperience(Professions.HANDINESS, craftable.getExperience());

				String prefix = "a";
				String itemName = GameDefinitionLoader.getItemDef(craftable.getOutcome()).getName().toLowerCase();
				if ((itemName.contains("glove")) || (itemName.contains("boot")) ||
					(itemName.contains("vamb")) || (itemName.contains("chap"))) {
					prefix = "a pair of";
				} else if (itemName.endsWith("s")) {
					prefix = "some";
				} else if (Utility.startsWithVowel(itemName)) {
					prefix = "an";
				}
				stoner.send(new SendMessage("You make " + prefix + " " + itemName + "."));
			}
		}

		private int getCutGemId(int uncut) {
			switch (uncut) {
				case 1625: return 1609;
				case 1627: return 1611;
				case 1629: return 1613;
				case 1623: return 1607;
				case 1621: return 1605;
				case 1619: return 1603;
				case 1617: return 1601;
				case 1631: return 1615;
				case 6571: return 6573;
				default: return uncut;
			}
		}

		private HandinessTask findNextCraftableItem() {
			for (int i = 0; i < handinessTasks.size(); i++) {
				int taskIndex = (currentTaskIndex + i) % handinessTasks.size();
				HandinessTask task = handinessTasks.get(taskIndex);

				if (canExecuteTask(task)) {
					currentTaskIndex = (currentTaskIndex + i + 1) % handinessTasks.size();
					return task;
				}
			}
			return null;
		}

		private boolean canExecuteTask(HandinessTask task) {
			switch (task.type) {
				case GEM_CUTTING:
					return stoner.getBox().contains(new Item(task.itemId)) && stoner.getBox().contains(new Item(1755));
				case SPINNING:
					Spinnable spinnable = Spinnable.forId(task.itemId);
					return spinnable != null && stoner.getBox().contains(spinnable.getItem());
				case GLASS_BLOWING:
					Glass glass = Glass.forReward(task.itemId);
					return glass != null && stoner.getBox().contains(new Item(glass.getMaterialId())) &&
						stoner.getBox().contains(new Item(1785));
				case GLASS_MELTING:
					return stoner.getBox().contains(new Item(1783)) && stoner.getBox().contains(new Item(1781));
				case HIDE_TANNING:
					return stoner.getBox().contains(new Item(task.itemId));
				case JEWELRY:
					Jewelry jewelry = Jewelry.forReward(task.itemId);
					if (jewelry != null) {
						for (int materialId : jewelry.getMaterialsRequired()) {
							if (!stoner.getBox().contains(new Item(materialId))) {
								return false;
							}
						}
						return true;
					}
					return false;
				case LEATHER_CRAFTING:
					Craftable craftable = Craftable.forReward(task.itemId);
					return craftable != null && stoner.getBox().contains(new Item(craftable.getItemId())) &&
						stoner.getBox().contains(new Item(1733)) && stoner.getBox().contains(new Item(1734));
				default:
					return false;
			}
		}

		@Override
		public void onStop() {
			stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		}
	}

	public enum HandinessType {
		GEM_CUTTING,
		SPINNING,
		GLASS_BLOWING,
		GLASS_MELTING,
		HIDE_TANNING,
		JEWELRY,
		LEATHER_CRAFTING
	}

	public boolean craft(Stoner stoner, int index, int amount) {
		stoner.send(new SendMessage("@red@Manual crafting is disabled. Use item-on-item or click objects for auto-crafting."));
		return false;
	}

	public boolean clickButton(Stoner stoner, int button) {
		stoner.send(new SendMessage("@red@Interface crafting is disabled. Use item-on-item or click objects for auto-crafting."));
		return false;
	}
}
