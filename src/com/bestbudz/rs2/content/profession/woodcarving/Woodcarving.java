package com.bestbudz.rs2.content.profession.woodcarving;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.Fletchable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.FletchableItem;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendItemOnInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;

public enum Woodcarving {
	SINGLETON;

	public static final String FLETCHABLE_KEY = "FLETCHABLE_KEY";

	private final HashMap<Integer, Fletchable> FLETCHABLES = new HashMap<>();

	public boolean itemOnItem(Stoner stoner, Item use, Item with) {
	if (stoner.getProfession().locked()) {
		return false;
	}

	final Fletchable fletchable = getFletchable(use.getId(), with.getId());

	if (fletchable == null || use.getId() == 590 || with.getId() == 590) {
		return false;
	}
	if (!stoner.getEquipment().isWearingItem(6575)) {
				DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
		return false;
	}

	String prefix = fletchable.getWith().getDefinition().getName().split(" ")[0];

	switch (fletchable.getFletchableItems().length) {
	case 1:
		stoner.getAttributes().set(FLETCHABLE_KEY, fletchable);
		stoner.send(new SendString("\\n \\n \\n \\n \\n" + fletchable.getFletchableItems()[0].getProduct().getDefinition().getName(), 2799));
		stoner.send(new SendItemOnInterface(1746, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
		stoner.send(new SendChatBoxInterface(4429));
		return true;
	case 2:
		stoner.getAttributes().set(FLETCHABLE_KEY, fletchable);
		stoner.send(new SendItemOnInterface(8869, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
		stoner.send(new SendItemOnInterface(8870, 170, fletchable.getFletchableItems()[1].getProduct().getId()));

		stoner.send(new SendString("\\n \\n \\n \\n \\n ".concat(prefix + " Short Bow"), 8874));
		stoner.send(new SendString("\\n \\n \\n \\n \\n ".concat(prefix + " Long Bow"), 8878));

		stoner.send(new SendChatBoxInterface(8866));
		return true;
	case 3:
		stoner.getAttributes().set(FLETCHABLE_KEY, fletchable);
		stoner.send(new SendItemOnInterface(8883, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
		stoner.send(new SendItemOnInterface(8884, 170, fletchable.getFletchableItems()[1].getProduct().getId()));
		stoner.send(new SendItemOnInterface(8885, 170, fletchable.getFletchableItems()[2].getProduct().getId()));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat(prefix + " Short Bow"), 8889));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat(prefix + " Long Bow"), 8893));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Crossbow Stock"), 8897));
		stoner.send(new SendChatBoxInterface(8880));
		return true;
	case 4:
		stoner.getAttributes().set(FLETCHABLE_KEY, fletchable);
		stoner.send(new SendItemOnInterface(8902, 170, fletchable.getFletchableItems()[0].getProduct().getId()));
		stoner.send(new SendItemOnInterface(8903, 170, fletchable.getFletchableItems()[1].getProduct().getId()));
		stoner.send(new SendItemOnInterface(8904, 170, fletchable.getFletchableItems()[2].getProduct().getId()));
		stoner.send(new SendItemOnInterface(8905, 170, fletchable.getFletchableItems()[3].getProduct().getId()));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("5 Headless Arrow"), 8909));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Short Bow"), 8913));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Long Bow"), 8917));
		stoner.send(new SendString("\\n \\n \\n \\n \\n".concat("Crossbow Stock"), 8921));
		stoner.send(new SendChatBoxInterface(8899));
		return true;
	default:
		return false;
	}
	}

	public boolean fletch(Stoner stoner, int index, int amount) {
	if (stoner.getProfession().locked()) {
		return false;
	}

	Fletchable fletchable = (Fletchable) stoner.getAttributes().get(FLETCHABLE_KEY);

	if (fletchable == null) {
		return false;
	}

		return start(stoner, fletchable, index, amount);
	}

	public void addFletchable(Fletchable fletchable) {
	if (FLETCHABLES.put(fletchable.getWith().getId(), fletchable) != null) {
		System.out.println("[Woodcarving] Conflicting item values: " + fletchable.getWith().getId() + " Type: " + fletchable.getClass().getSimpleName());
	}
	}

	public Fletchable getFletchable(int use, int with) {
	return FLETCHABLES.get(use) == null ? FLETCHABLES.get(with) : FLETCHABLES.get(use);
	}

	public boolean clickButton(Stoner stoner, int button) {
	if (stoner.getAttributes().get(FLETCHABLE_KEY) == null) {
		return false;
	}

	Fletchable fletchable = (Fletchable) stoner.getAttributes().get(FLETCHABLE_KEY);

	switch (button) {
	case 6211:
		start(stoner, fletchable, 0, stoner.getBox().getItemAmount(fletchable.getWith().getId()));
		return true;
	case 34205:
	case 34185:
	case 34170:
	case 10239:
		start(stoner, fletchable, 0, 1);
		return true;
	case 34204:
	case 34184:
	case 34169:
	case 10238:
		start(stoner, fletchable, 0, 5);
		return true;
	case 34203:
	case 34183:
	case 34168:
		start(stoner, fletchable, 0, 10);
		return true;
	case 34202:
	case 34182:
	case 34167:
	case 6212:
		start(stoner, fletchable, 0, 2500);
		return true;
	case 34209:
	case 34189:
	case 34174:
		start(stoner, fletchable, 1, 1);
		return true;
	case 34208:
	case 34188:
	case 34173:
		start(stoner, fletchable, 1, 5);
		return true;
	case 34207:
	case 34187:
	case 34172:
		start(stoner, fletchable, 1, 10);
		return true;
	case 34206:
	case 34186:
	case 34171:
		start(stoner, fletchable, 1, 2500);
		return true;
	case 34213:
	case 34193:
		start(stoner, fletchable, 2, 1);
		return true;
	case 34212:
	case 34192:
		start(stoner, fletchable, 2, 5);
		return true;
	case 34211:
	case 34191:
		start(stoner, fletchable, 2, 10);
		return true;
	case 34210:
	case 34190:
		start(stoner, fletchable, 2, 2500);
		return true;
	case 34217:
		start(stoner, fletchable, 3, 1);
		return true;
	case 34216:
		start(stoner, fletchable, 3, 5);
		return true;
	case 34215:
		start(stoner, fletchable, 3, 10);
		return true;
	case 34214:
		start(stoner, fletchable, 3, 2500);
		return true;

	default:
		return false;
	}
	}

	public boolean start(Stoner stoner, Fletchable fletchable, int index, int amount) {
	if (fletchable == null) {
		return false;
	}

	stoner.getAttributes().remove(FLETCHABLE_KEY);

	FletchableItem item = fletchable.getFletchableItems()[index];

	stoner.send(new SendRemoveInterfaces());

	if (stoner.getGrades()[Professions.WOODCARVING] < item.getGrade()) {
		DialogueManager.sendStatement(stoner, "<col=369>You need a Woodcarving grade of " + item.getGrade() + " to do that.");
		return true;
	}
	if (!stoner.getEquipment().isWearingItem(6575)) {
				DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
		return false;
	}

	if (!(stoner.getBox().hasAllItems(fletchable.getIngediants()))) {
		String firstName = fletchable.getUse().getDefinition().getName().toLowerCase();
		String secondName = fletchable.getWith().getDefinition().getName().toLowerCase();

		if (fletchable.getUse().getAmount() > 1 && !firstName.endsWith("s")) {
			firstName = firstName.concat("s");
		}

		if (fletchable.getWith().getAmount() > 1 && !secondName.endsWith("s")) {
			secondName = secondName.concat("s");
		}

		if (fletchable.getUse().getAmount() == 1 && firstName.endsWith("s")) {
			firstName = firstName.substring(0, firstName.length() - 1);
		}

		if (fletchable.getWith().getAmount() == 1 && secondName.endsWith("s")) {
			secondName = secondName.substring(0, secondName.length() - 1);
		}

		final String firstAmount;

		if (fletchable.getUse().getAmount() == 1) {
			firstAmount = Utility.getAOrAn(fletchable.getUse().getDefinition().getName());
		} else {
			firstAmount = String.valueOf(fletchable.getUse().getAmount());
		}

		final String secondAmount;

		if (fletchable.getWith().getAmount() == 1) {
			secondAmount = Utility.getAOrAn(fletchable.getWith().getDefinition().getName());
		} else {
			secondAmount = String.valueOf(fletchable.getWith().getAmount());
		}

		String firstRequirement = firstAmount + " " + firstName;
		String secondRequirement = secondAmount + " " + secondName;
		stoner.send(new SendMessage("You need " + firstRequirement + " and " + secondRequirement + " to do that."));
		return true;
	}

	TaskQueue.queue(new Task(stoner, 2, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.PROFESSION_CREATING) {
		private int iterations = 0;

		@Override
		public void execute() {
		stoner.getProfession().lock(2);

		stoner.getUpdateFlags().sendAnimation(new Animation(fletchable.getAnimation()));
		stoner.getProfession().addExperience(Professions.WOODCARVING, item.getExperience());
		stoner.getBox().remove(fletchable.getIngediants(), true);
		stoner.getBox().add(item.getProduct());

		if (fletchable.getProductionMessage() != null) {
			stoner.send(new SendMessage(fletchable.getProductionMessage()));
		}

		if (++iterations == amount) {
			stop();
			return;
		}

		if (!(stoner.getBox().hasAllItems(fletchable.getIngediants()))) {
			stop();
			DialogueManager.sendStatement(stoner, "<col=369>You have run out of materials.");
		}
		}

		@Override
		public void onStop() {
		stoner.getUpdateFlags().sendAnimation(new Animation(65535));
		}
	});

	return true;
	}
}