package com.bestbudz.rs2.content.profession.foodie;

import com.bestbudz.core.cache.map.ObjectDef;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterXInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendItemOnInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class FoodieTask extends Task {

	public static final String FOODIE_OBJECT_KEY = "foodieobject";
	public static final String FOODIE_ITEM_KEY = "foodieitem";
	public static final int FOODIE_GAUNTLETS = 775;

	public static void attemptFoodie(Stoner stoner, int cook, int object, int amount) {
	FoodieData data = FoodieData.forId(cook);
	if (data == null) {
		return;
	}
	
	if (!meetsRequirements(stoner, data, cook, object)) {
		return;
	}

	TaskQueue.queue(new FoodieTask(stoner, data, cook, object, amount));
	stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
	}

	public static boolean handleFoodieByAmount(Stoner stoner, int buttonId) {
	int amount = 0;
	for (int i = 0; i < BUTTON_IDS.length; i++) {
		if (BUTTON_IDS[i][0] == buttonId) {
			amount = BUTTON_IDS[i][1];
			break;
		}
	}

	if (amount == 0) {
		return false;
	}
	if (amount != 100)
		attemptFoodie(stoner, ((Integer) stoner.getAttributes().get("foodieitem")).intValue(), ((Integer) stoner.getAttributes().get("foodieobject")).intValue(), amount);
	else {
		stoner.getClient().queueOutgoingPacket(new SendEnterXInterface(1743, ((Integer) stoner.getAttributes().get("foodieitem")).intValue()));
	}
	return true;
	}

	public static boolean isCookableObject(GameObject object) {
	if (object == null) {
		return false;
	}

	ObjectDef def = ObjectDef.getObjectDef(object.getId());

	if (def == null || def.name == null) {
		return false;
	}

	String name = def.name.toLowerCase();

	return name.equals("range") || name.equals("fire") || name.contains("oven") || name.contains("stove") || name.contains("foodie range") || name.contains("fireplace");
	}

	private static boolean meetsRequirements(Stoner stoner, FoodieData data, int used, int usedOn) {
	int foodieGrade = stoner.getProfession().getGrades()[7];
	if (foodieGrade < data.getGradeRequired()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You need a foodie grade of " + data.getGradeRequired() + " to cook " + Item.getDefinition(used).getName() + "."));
		stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		return false;
	}
	if (!stoner.getBox().hasItemId(used)) {
		return false;
	}
	if (!stoner.getEquipment().isWearingItem(6575)) {
		DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
return false;
}
	return true;
	}

	public static void showInterface(Stoner stoner, GameObject usedOn, Item used) {
	if (used == null || FoodieData.forId(used.getId()) == null) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("I knew you were an odd one!"));
		return;
	}

	stoner.getClient().queueOutgoingPacket(new SendChatBoxInterface(1743));
	stoner.getClient().queueOutgoingPacket(new SendItemOnInterface(13716, 250, used.getId()));
	stoner.getClient().queueOutgoingPacket(new SendString("\\n\\n\\n\\n\\n" + used.getDefinition().getName(), 13717));

	stoner.getAttributes().set("foodieobject", Integer.valueOf(usedOn.getId()));
	stoner.getAttributes().set("foodieitem", Integer.valueOf(used.getId()));
	}

	private FoodieData foodieData;

	private Stoner stoner;

	private int used;

	private int usedOn;

	private int amountToCook;

	private static final int[][] BUTTON_IDS = { { 53152, 1 }, { 53151, 5 }, { 53149, 28 }, { 53150, 100 } };

	public FoodieTask(Stoner stoner, FoodieData data, int used, int usedOn, int amount) {
	super(stoner, 3, false, Task.StackType.NEVER_STACK, Task.BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
	this.stoner = stoner;
	foodieData = data;
	this.used = used;
	this.usedOn = usedOn;
	amountToCook = amount;
	}

	private void burnFood() {
	stoner.getBox().add(new Item(foodieData.getBurnt(), 1));
	stoner.getClient().queueOutgoingPacket(new SendMessage("The " + Item.getDefinition(used).getName() + " has something shiny in it."));
	stoner.getClient().queueOutgoingPacket(new SendMessage("You have messed the fish up and got a BestBucks."));
	}

	private void cookFood() {
	stoner.getBox().add(new Item(foodieData.getReplacement(), 1));
	double experience = foodieData.getExperience();
	stoner.getProfession().addExperience(7, experience);
	AchievementHandler.activateAchievement(stoner, AchievementList.COOK_250_FOODS, 1);
	AchievementHandler.activateAchievement(stoner, AchievementList.COOK_10000_FOODS, 1);
	}

	@Override
	public void execute() {
	if (!meetsRequirements(stoner, foodieData, used, usedOn)) {
		stop();
		return;
	}

	stoner.getUpdateFlags().sendAnimation(883, 0);
	stoner.getBox().remove(used);

	if (successfulAttempt())
		cookFood();
	else {
		burnFood();
	}
	amountToCook -= 1;
	if (amountToCook == 0)
		stop();
	}

	private int getFoodieGradeBoost() {
	Item gloves = stoner.getEquipment().getItems()[9];

	if ((gloves != null) && (gloves.getId() == 775)) {
		return 3;
	}

	return 0;
	}

	@Override
	public void onStop() {
	}

	private boolean successfulAttempt() {
	if (stoner.getProfession().getGrades()[7] > foodieData.getNoBurnGrade()) {
		return true;
	}

	return Professions.isSuccess(stoner.getMaxGrades()[7] + getFoodieGradeBoost(), foodieData.getGradeRequired() / 2 == 0 ? 1 : foodieData.getGradeRequired() / 2);
	}
}
