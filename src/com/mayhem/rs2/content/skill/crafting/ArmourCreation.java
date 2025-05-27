package com.mayhem.rs2.content.skill.crafting;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.impl.ProductionTask;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.Graphic;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class ArmourCreation extends ProductionTask {
	short creationAmount;
	Craftable craftable;

	public ArmourCreation(Player entity, short creationAmount, Craftable craft) {
		super(entity, 0, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
		this.creationAmount = creationAmount;
		craftable = craft;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return new Animation(1249);
	}

	@Override
	public Item[] getConsumedItems() {
		if (craftable.getItemId() == 1741) {
			if (player.getInventory().hasItemId(1741)) {
				return new Item[] { new Item(1734, 1), new Item(1741, 1) };
			}
			return new Item[] { new Item(1734, 1), new Item(craftable.getItemId(), 1) };
		}

		return new Item[] { new Item(1734, 1), new Item(craftable.getItemId(), 1) };
	}

	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public double getExperience() {
		return craftable.getExperience() * (getConsumedItems()[0].getId() == 1743 ? 2 : 1);
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public String getInsufficentLevelMessage() {
		return "You need a " + com.mayhem.rs2.content.skill.Skills.SKILL_NAMES[getSkill()] + " level of " + getRequiredLevel() + " to create " + GameDefinitionLoader.getItemDef(getRewards()[0].getId()).getName() + ".";
	}

	@Override
	public int getProductionCount() {
		return creationAmount;
	}

	@Override
	public int getRequiredLevel() {
		return craftable.getRequiredLevel();
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(craftable.getOutcome(), 1) };
	}

	@Override
	public int getSkill() {
		return 12;
	}

	@Override
	public String getSuccessfulProductionMessage() {
		player.skillPoints += 40;
		handleDesAmulet1(player);
		handleDesAmulet2(player);
		handleDesAmulet3(player);
		String prefix = "a";
		String itemName = GameDefinitionLoader.getItemDef(getRewards()[0].getId()).getName().toLowerCase();
		if ((itemName.contains("glove")) || (itemName.contains("boot")) || (itemName.contains("vamb")) || (itemName.contains("chap")))
			prefix = "a pair of";
		else if (itemName.endsWith("s"))
			prefix = "some";
		else if (Utility.startsWithVowel(itemName)) {
			prefix = "an";
		}
		return "You make " + prefix + " " + itemName + ". You now have @blu@" + player.skillPoints + "</col> Skill points.";
		
	}

	@Override
	public String noIngredients(Item item) {
		return "You don't have enough " + GameDefinitionLoader.getItemDef(item.getId()).getName() + " to craft this item.";
	}

	@Override
	public void onStop() {
	}
	public static void handleDesAmulet1(Player player) {

		int random = Utility.randomNumber(200);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13133, 1)) {
				player.getEquipment().remove(new Item(13133, 1));
				player.getEquipment().equip(new Item(13134), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Desert Amulet has been upgraded!")); }
			}
		}

	public static void handleDesAmulet2(Player player) {

		int random = Utility.randomNumber(300);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13134, 1)) {
				player.getEquipment().remove(new Item(13134, 1));
				player.getEquipment().equip(new Item(13135), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Desert Amulet has been upgraded!")); }
		}
	}
	public static void handleDesAmulet3(Player player) {

		int random = Utility.randomNumber(1000);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13135, 1)) {
				player.getEquipment().remove(new Item(13135, 1));
				player.getEquipment().equip(new Item(13136), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Desert Amulet has been upgraded!")); }
		}
	}
}
