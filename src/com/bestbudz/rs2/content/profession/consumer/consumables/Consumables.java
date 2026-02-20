package com.bestbudz.rs2.content.profession.consumer.consumables;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.profession.consumer.consumables.food.FoodHandler;
import com.bestbudz.rs2.content.profession.consumer.consumables.potions.PotionHandler;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public final class Consumables {

	private final Stoner stoner;
	private final FoodHandler foodHandler;
	private final PotionHandler potionHandler;
	private boolean canEat = true;
	private boolean canDrink = true;

	public Consumables(Stoner stoner) {
		this.stoner = stoner;
		this.foodHandler = new FoodHandler(stoner, this);
		this.potionHandler = new PotionHandler(stoner, this);
	}

	public static boolean isPotion(Item i) {
		return i != null && GameDefinitionLoader.getPotionDefinition(i.getId()) != null;
	}

	public boolean consume(int id, int slot, ConsumableType type) {
		Item consumable = stoner.getBox().get(slot);

		if (consumable == null || stoner.getMage().isTeleporting() || stoner.isStunned()) {
			return false;
		}

		switch (type) {
			case FOOD:
				return foodHandler.consumeFood(id, slot, consumable);
			case POTION:
				return potionHandler.consumePotion(id, slot, consumable);
			default:
				System.out.print("[ERROR] - CONSUMABLES");
				return false;
		}
	}

	public boolean canEat() {
		return canEat;
	}

	public void setCanEat(boolean canEat) {
		this.canEat = canEat;
	}

	public boolean canDrink() {
		return canDrink;
	}

	public void setCanDrink(boolean canDrink) {
		this.canDrink = canDrink;
	}
}