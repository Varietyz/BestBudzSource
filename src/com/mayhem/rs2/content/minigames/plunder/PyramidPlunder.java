package com.mayhem.rs2.content.minigames.plunder;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.Task.BreakType;
import com.mayhem.core.task.Task.StackType;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.minigames.plunder.PlunderConstants.DoorBitPosition;
import com.mayhem.rs2.content.minigames.plunder.PlunderConstants.UrnBitPosition;
import com.mayhem.rs2.content.minigames.plunder.tasks.LootUrnTask;
import com.mayhem.rs2.content.minigames.plunder.tasks.PicklockDoorTask;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.ControllerManager;
import com.mayhem.rs2.entity.player.net.out.impl.SendConfig;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;


public enum PyramidPlunder {
	SINGLETON;

	private static final DoorBitPosition[] EXITS = new DoorBitPosition[7];

	public static final void declare() {
		TaskQueue.queue(new Task(3000, true) {
			@Override
			public void execute() {
				for (int index = 0; index < EXITS.length; index++) {
					EXITS[index] = Utility.randomElement(DoorBitPosition.values());
				}
			}

			@Override
			public void onStop() {
			}
		});
	}

	public boolean isExitDoor(DoorBitPosition exit, int floor) {
		return EXITS[floor] == exit;
	}

	public void start(Player player) {
		player.getAttributes().set("PLUNDER_TASK", TaskQueue.queue(
				new Task(player, 10, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.PYRAMID_PLUNDER) {
					private int ticks = 0;

					@Override
					public void onStart() {
						player.send(new SendMessage(
								"You have @dre@5:00</col> minutes to plunder as much loot as possible."));
						changeFloor(player, 0);
						player.setController(ControllerManager.PLUNDER_CONTROLLER);
					}

					@Override
					public void execute() {
						switch (++ticks) {
						case 10:
							player.send(new SendMessage("You have @dre@4:00</col> minutes remaining!"));
							break;
						case 20:
							player.send(new SendMessage("You have @dre@3:00</col> minutes remaining!"));
							break;
						case 30:
							player.send(new SendMessage("You have @dre@2:00</col> minutes remaining!"));
							break;
						case 40:
							player.send(new SendMessage("You have @dre@1:00</col> minute remaining!"));
							break;
						case 45:
							player.send(new SendMessage("You have @dre@0:30</col> seconds remaining!"));
							break;
						case 50:
							player.send(new SendMessage("Your time is up!"));
							stop();
							break;
						}
					}

					@Override
					public void onStop() {
						player.teleport(new Location(3087, 3500, 0));
						player.setController(ControllerManager.DEFAULT_CONTROLLER);
					}
				}));
	}

	private void resetConfigs(Player player) {
		player.getAttributes().set(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY, 0);
		player.getAttributes().set(PlunderConstants.URNS_CONFIG_KEY, 0);

		player.send(new SendConfig(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG, 0));
		player.send(new SendConfig(PlunderConstants.URNS_CONFIG, 0));
	}

	public boolean changeFloor(Player player, int floor) {
		if (player.getLevels()[Skills.THIEVING] < 21 + floor * 10) {
			return false;
		}

		if (floor == 6) {
			return false;
		}

		player.getAttributes().set("PLUNDER_FLOOR", floor);

		resetConfigs(player);

		switch (floor) {
		case 0:
			player.teleport(PlunderConstants.FLOOR_1);
			break;
		case 1:
			player.teleport(PlunderConstants.FLOOR_2);
			break;
		case 2:
			player.teleport(PlunderConstants.FLOOR_3);
			break;
		case 3:
			player.teleport(PlunderConstants.FLOOR_4);
			break;
		case 4:
			player.teleport(PlunderConstants.FLOOR_5);
			break;
		case 5:
			player.teleport(PlunderConstants.FLOOR_6);
			break;
		case 6:
			player.teleport(PlunderConstants.FLOOR_7);
			break;
		case 7:
			// player.teleport(PlunderConstants.FLOOR_8);
			break;
		}
		return true;
	}

	public boolean openChest(Player player) {
		// GOLDEN_CHEST(26616, 2)
		return false;
	}

	public boolean openSarcophogus(Player player) {
		// SARCOPHOGUS(26626, 0),
		return false;
	}

	public boolean clickObject(Player player, GameObject object) {
		if (player.getSkill().locked()) {
			return false;
		}

		UrnBitPosition urn = UrnBitPosition.get(object.getId());

		if (urn != null) {
			TaskQueue.queue(new LootUrnTask(player, urn));
			return true;
		}

		DoorBitPosition door = DoorBitPosition.get(object.getId());

		if (door != null) {
			TaskQueue.queue(new PicklockDoorTask(player, object, door));
			return true;
		}

		if (openChest(player)) {
			return true;
		}

		if (openSarcophogus(player)) {
			return true;
		}

		return false;
	}
}