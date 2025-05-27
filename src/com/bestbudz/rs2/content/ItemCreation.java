package com.bestbudz.rs2.content;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.impl.ConfirmDialogue;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import java.util.HashMap;

public enum ItemCreation {

	ABYSSAL_TENTACLE(new int[] { 12004, 4151 }, 12006, new CreationHandle() {
		@Override
		public void handle(Stoner stoner, ItemCreation data) {
		if (meetsRequirements(stoner, data)) {
			stoner.start(new ConfirmDialogue(stoner) {
				@Override
				public void onConfirm() {
				if (meetsRequirements(stoner, data)) {
					ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
					DialogueManager.sendItem1(stoner, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
					stoner.getBox().remove(new Item(data.getItem()[0]));
					stoner.getBox().remove(new Item(data.getItem()[1]));
					stoner.getBox().add(new Item(data.getNewItem(), 1));
				}
				}
			});
		}
		}
	}),
	BLOWPIPE(new int[] { 12922, 1755 }, 12924, new CreationHandle() {
		@Override
		public void handle(Stoner stoner, ItemCreation data) {
		if (meetsRequirements(stoner, data)) {
			stoner.start(new ConfirmDialogue(stoner) {
				@Override
				public void onConfirm() {
				if (meetsRequirements(stoner, data)) {
					if (!professionRequired(stoner, Professions.WOODCARVING, 33)) {
						return;
					}
					ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
					DialogueManager.sendItem1(stoner, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
					stoner.getBox().remove(new Item(data.getItem()[0]));
					stoner.getBox().add(new Item(data.getNewItem(), 1));
				}
				}
			});
		}
		}
	}),
	SARADOMIN_BLESSED_SWORD(new int[] { 12804, 11838 }, 12809, new CreationHandle() {
		@Override
		public void handle(Stoner stoner, ItemCreation data) {
		if (meetsRequirements(stoner, data)) {
			stoner.start(new ConfirmDialogue(stoner) {
				@Override
				public void onConfirm() {
				if (meetsRequirements(stoner, data)) {
					ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
					DialogueManager.sendItem1(stoner, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
					stoner.getBox().remove(new Item(data.getItem()[0]));
					stoner.getBox().remove(new Item(data.getItem()[1]));
					stoner.getBox().add(new Item(data.getNewItem(), 1));
				}
				}
			});
		}
		}
	}),
	DRAGONFIRE_SHIELD(new int[] { 1540, 11286 }, 11283, new CreationHandle() {
		@Override
		public void handle(Stoner stoner, ItemCreation data) {
		if (meetsRequirements(stoner, data)) {
			stoner.start(new ConfirmDialogue(stoner) {
				@Override
				public void onConfirm() {
				if (meetsRequirements(stoner, data)) {
					if (!professionRequired(stoner, Professions.FORGING, 90)) {
						return;
					}
					ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
					DialogueManager.sendItem1(stoner, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
					stoner.getBox().remove(new Item(data.getItem()[0]));
					stoner.getBox().remove(new Item(data.getItem()[1]));
					stoner.getBox().add(new Item(data.getNewItem(), 1));
				}
				}
			});
		}
		}
	}),;

	public static HashMap<Integer, ItemCreation> creation = new HashMap<>();

	static {
		for (final ItemCreation creation : ItemCreation.values()) {
			if (ItemCreation.creation.put(creation.itemID[0] << 16 | creation.itemID[1], creation) != null) {
				throw new AssertionError("Conflicting keys. Items: [" + creation.itemID[0] + ", " + creation.itemID[1] + "]");
			}

			if (ItemCreation.creation.put(creation.itemID[1] << 16 | creation.itemID[0], creation) != null) {
				throw new AssertionError("Conflicting keys. Items: [" + creation.itemID[0] + ", " + creation.itemID[1] + "]");
			}
		}
	}

	private final int[] itemID;
	private final CreationHandle handle;
	int newItem;

	ItemCreation(int[] itemId, int newItem, CreationHandle handle) {
	this.itemID = itemId;
	this.handle = handle;
	this.newItem = newItem;
	}

	public static boolean professionRequired(Stoner stoner, int profession, int grade) {
	if (stoner.getProfession().getGrades()[profession] < grade) {
		stoner.send(new SendRemoveInterfaces());
		DialogueManager.sendStatement(stoner, "You need a " + Advance.getProfessionName(profession) + " grade of " + grade + " to do this!");
		return false;
	}
	return true;
	}

	public static boolean meetsRequirements(Stoner stoner, ItemCreation data) {
	for (int item : data.getItem()) {
		if (!stoner.getBox().hasItemId(item)) {
			stoner.send(new SendMessage("Nothing interesting happens"));
			return false;
		}
	}
	return true;
	}

	public static boolean handle(Stoner stoner, int item1, int item2) {
	ItemCreation data = ItemCreation.creation.get(item1 << 16 | item2);

	if (data == null) {
		return handleGodsword(stoner, item1, item2);
	}

	data.getHandle().handle(stoner, data);
	return true;
	}

	public static boolean handleGodsword(Stoner stoner, int use, int with) {
	Item product = null;
	boolean forging = false;
	if (isUsedWith(use, with, 11820, 11818)) {
		product = new Item(11794);
	} else if (isUsedWith(use, with, 11822, 11818)) {
		product = new Item(11796);
	} else if (isUsedWith(use, with, 11822, 11820)) {
		product = new Item(11800);
	} else if (isUsedWith(use, with, 11794, 11822)) {
		forging = true;
		product = new Item(11798);
	} else if (isUsedWith(use, with, 11796, 11820)) {
		forging = true;
		product = new Item(11798);
	} else if (isUsedWith(use, with, 11800, 11818)) {
		forging = true;
		product = new Item(11798);
	} else if (isUsedWith(use, with, 11798, 11810)) {
		product = new Item(11802);
	} else if (isUsedWith(use, with, 11798, 11812)) {
		product = new Item(11804);
	} else if (isUsedWith(use, with, 11798, 11814)) {
		product = new Item(11806);
	} else if (isUsedWith(use, with, 11798, 11816)) {
		product = new Item(11808);
	}
	if (forging) {
		if (stoner.getGrades()[Professions.FORGING] < 80) {
			DialogueManager.sendStatement(stoner, "<col=369>You need a forging grade of @dre@80<col=369> to create godsword blades.");
			return true;
		}
		stoner.getProfession().addExperience(Professions.FORGING, 100.0);
	}
	if (product != null) {
		stoner.getBox().remove(use, 1);
		stoner.getBox().remove(with, 1);
		stoner.getBox().add(product);
		DialogueManager.sendItem1(stoner, "You have created " + Utility.getAOrAn(product.getDefinition().getName().toLowerCase()) + " " + product.getDefinition().getName() + ".", product.getId());
	}
	return product != null;
	}

	private static final boolean isUsedWith(int use, int with, int item1, int item2) {
	return (use == item1 && with == item2) || (use == item2 && with == item1);
	}

	public int[] getItem() {
	return itemID;
	}

	public int getNewItem() {
	return newItem;
	}

	public CreationHandle getHandle() {
	return handle;
	}

}