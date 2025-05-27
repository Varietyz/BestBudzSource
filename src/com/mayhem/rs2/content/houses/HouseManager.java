package com.mayhem.rs2.content.houses;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.task.impl.TaskIdentifier;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.houses.HouseObject.ObjectType;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.net.out.impl.SendColor;
import com.mayhem.rs2.entity.player.net.out.impl.SendConfig;
import com.mayhem.rs2.entity.player.net.out.impl.SendEnterString;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendItemToContainer;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;
import com.mayhem.rs2.entity.player.net.out.impl.SendSidebarInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;




/**
 * 
 * @author Jesse/Sk8rdude461
 *
 */
public class HouseManager {

	private static final String FILE_DIRECTORY = "./data/characters/houses/";
	private static final Gson gson = new GsonBuilder().create();
	private static final HashMap<String, House> PLAYER_HOUSES = new HashMap<String, House>();
	private static final ArrayList<House> ACTIVE_HOUSES = new ArrayList<House>();
	private static Logger logger = Logger.getLogger(HouseManager.class.getSimpleName());
	private static final FileFilter FILE_EXTENSIONS = new FileFilter() {
		@Override
		public boolean accept(File arg0) {
			return arg0 != null && arg0.getName().toLowerCase().endsWith(".json");
		}

	};

	/**
	 * Handles all the button packets for the player
	 * 
	 * @param player
	 *            the player clicking a button
	 * @return true if the button is used in the house interfaces
	 */
	public static boolean clickButton(Player player, int buttonId) {
		if (player == null)
			return false;
		if (buttonId >= 207132 && buttonId <= 207231) {
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			Object[] objectList = HouseObject
					.getHouseObjects((HouseObject.ObjectType) player.getAttributes().get("house-filter"));
			for (int i = 0; i <= 99; i++) {
				HouseObject obj = (i >= objectList.length || objectList[i] == null) ? null
						: (HouseObject) objectList[i];
				if (obj != null && (207132 + i) == buttonId) {
					selectBuildObject(player, obj);
				}
			}
			return true;
		}
		switch (buttonId) {
		case 207010:
			enterBuildMode(player);
			return true;
		case 207011:
			changeHouseLock(player);
			return true;
		case 207113:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			int rotation = player.getAttributes().get("house-obj-rotation") == null ? 0
					: (Integer) player.getAttributes().get("house-obj-rotation");
			rotation = rotation - 1;
			if (rotation < 0)
				rotation += 4;
			player.getAttributes().set("house-obj-rotation", rotation);
			openBuildInterface(player);
			return true;
		case 207114:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			rotation = player.getAttributes().get("house-obj-rotation") == null ? 0
					: (Integer) player.getAttributes().get("house-obj-rotation");
			rotation = rotation + 1;
			if (rotation > 3)
				rotation -= 4;
			player.getAttributes().set("house-obj-rotation", rotation);
			openBuildInterface(player);
			return true;
		case 207115:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			HouseObject data = player.getAttributes().get("house-obj-selected") != null
					? (HouseObject) player.getAttributes().get("house-obj-selected") : null;
			if (data == null) {
				player.send(new SendMessage("Please select an object to build first."));
				return true;
			}
			rotation = player.getAttributes().get("house-obj-rotation") == null ? 0
					: (Integer) player.getAttributes().get("house-obj-rotation");
			addHouseObject(player, data, rotation);
			return true;
		case 207014:
			leaveHouse(player);
			return true;
		case 207017:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			Item[] equipment = { new Item(7409, 1), new Item(2347, 1) };
			if (player.getInventory().hasAllItems(equipment)) {
				player.send(new SendMessage("You already have the equipment, why grab more?"));
				return true;
			}
			if (!player.getInventory().hasAllItems(equipment) && !player.getInventory().hasSpaceFor(equipment)) {
				player.send(new SendMessage("You need 2 free slots to grab these items."));
				return true;
			}
			player.getInventory().add(equipment, true);
			player.send(new SendMessage(
					"You pull the hammer and secateurs out of... Wait.. Where did you grab them from?"));
			return true;
		case 207020:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			openBuildInterface(player);
			return true;
		case 207023:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-ban-name", true);
			player.send(new SendEnterString());
			return true;
		case 207026:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-unban-name", true);
			player.send(new SendEnterString());
			return true;
		case 207029:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-kick-name", true);
			player.send(new SendEnterString());
			return true;
		case 207032:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			kickAllPlayers(player);
			return true;
		case 207125:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			if (player.getAttributes().get("house-filter") != null)
				player.getAttributes().remove("house-filter");
			openBuildInterface(player);
			return true;
		case 207126:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-filter", ObjectType.ORES);
			openBuildInterface(player);
			return true;
		case 207127:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-filter", ObjectType.TREES);
			openBuildInterface(player);
			return true;
		case 207128:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-filter", ObjectType.ALTARS);
			openBuildInterface(player);
			return true;
		case 207129:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-filter", ObjectType.STALLS);
			openBuildInterface(player);
			return true;
		case 207130:
			if (!inOwnHouse(player)) {
				player.send(new SendMessage("You must be in your house to do this."));
				return true;
			}
			player.getAttributes().set("house-filter", ObjectType.MISC);
			openBuildInterface(player);
			return true;
		default:
			return false;
		}
	}

	private static void selectBuildObject(Player player, HouseObject obj) {
		if (player == null || obj == null)
			return;
		player.getAttributes().set("house-obj-selected", obj);
		player.getAttributes().set("house-obj-rotation", 0);
		openBuildInterface(player);
	}

	/**
	 * Attempts to add a player to either their own home or as a guest to
	 * another home. If they're guesting, it checks a ban list first, if not
	 * guesting, it verifies the player has no existing house instance before
	 * entering.
	 * 
	 * @param player
	 *            the player entering the house
	 * @param playerToEnter
	 *            the name of the player house to enter
	 */
	public static void enterHouse(Player player, String playerToEnter) {
		if (player == null || playerToEnter == null)
			return;
		player.send(new SendRemoveInterfaces());
		String formattedHomeName = formatName(playerToEnter);
		boolean isSelf = player.getUsername().equalsIgnoreCase(formattedHomeName);
		if (!ownsHouse(formattedHomeName) || (!isSelf && !houseIsActive(formattedHomeName))) {
			player.send(new SendMessage(
					isSelf ? "You do not own a house to enter." : "This user is offline or does not own a house."));
			return;
		}
		if (isSelf && houseIsActive(formattedHomeName)) {
			player.send(new SendMessage("You cannot create a second instance of your house."));
			return;
		}
		player.send(new SendMessage(isSelf ? "Now entering your home..."
				: "Attempting to join " + Utility.formatPlayerName(formattedHomeName) + "'s house."));
		House playerHouse = isSelf ? PLAYER_HOUSES.get(formattedHomeName) : getActiveHouse(formattedHomeName);
		if (playerHouse == null) {
			player.send(new SendMessage(isSelf ? "Error.. Failed to find your home. Report this to staff."
					: "Failed to find " + Utility.formatPlayerName(formattedHomeName) + "'s house."));
			return;
		}
		if (playerHouse.isBuildMode()) {
			player.send(new SendMessage(
					isSelf ? "Please exit your house or toggle build mode off before attempting to enter again."
							: "They seem to be editing their house right now. Best come back later."));
			return;
		}
		if (playerHouse.isHouseLocked()) {
			player.send(new SendMessage(
					isSelf ? "Please exit your house or disable the lock before attempting to enter again."
							: "No matter how hard you budge, the door to their house seems securely locked."));
			return;
		}
		if (playerIsBanned(playerHouse, player.getUsername())) {
			player.send(new SendMessage("You cannot enter this house as you've been banned from it."));
			return;
		}
		if (isSelf) {
			playerHouse.setHeightLevel((player.getIndex() * 4) + playerHouse.getType().getSpawnLocation().getZ());
			ACTIVE_HOUSES.add(playerHouse);
		}
		player.getAttributes().set("house-enter", true);
		final Location spawnLoc = new Location(playerHouse.getType().getSpawnLocation().getX(),
				playerHouse.getType().getSpawnLocation().getY(), playerHouse.getHeightLevel());
		player.teleport(spawnLoc);
		player.send(new SendString(
				"Now entering " + (isSelf ? "your" : (Utility.formatPlayerName(formattedHomeName) + "'s")) + " house.",
				12285));
		player.send(new SendInterface(12283));
		TaskQueue.queue(
				new Task(player, 3, true, Task.StackType.STACK, Task.BreakType.NEVER, TaskIdentifier.CURRENT_ACTION) {
					boolean ran = false;

					@Override
					public void execute() {
						if (ran) {
							stop();
							return;
						}
						if (isSelf)
							spawnHouseObjects(player, playerHouse.getObjectList(), playerHouse.getHeightLevel());
						else
							player.getObjects().onRegionChange();
						ran = true;
					}

					@Override
					public void onStop() {
						player.send(new SendConfig(691, 0));
						player.send(new SendConfig(690, 0));
						player.send(new SendSidebarInterface(13, 53000));
						player.send(new SendMessage("You've entered " + (isSelf ? "your house."
								: (Utility.formatPlayerName(formattedHomeName) + "'s house."))));
						player.send(new SendRemoveInterfaces());
						player.getAttributes().remove("house-enter");
					}
				});
	}

	/**
	 * Sets up the build object interface for a player then displays it
	 * 
	 * @param player
	 *            the player the interface is set for
	 */
	private static void openBuildInterface(Player player) {
		if (player == null)
			return;
		House house = getActiveHouse(player.getUsername());
		if (house == null || !house.isBuildMode()) {
			player.send(new SendMessage("You must be in build mode in your house to do this."));
			return;
		}
		Object[] objectList = HouseObject
				.getHouseObjects((HouseObject.ObjectType) player.getAttributes().get("house-filter"));
		for (int i = 0; i <= 99; i++) {
			HouseObject obj = (i >= objectList.length || objectList[i] == null) ? null : (HouseObject) objectList[i];
			player.send(new SendString((obj == null) ? "" : obj.toString(), 53124 + i));
		}
		player.send(new SendColor(53117, (player.getAttributes().get("house-filter") == null ? 0x00FF00 : 0xFF0000)));
		player.send(new SendColor(53118,
				((player.getAttributes().get("house-filter") != null
						&& ((HouseObject.ObjectType) player.getAttributes().get("house-filter") == ObjectType.ORES))
								? 0x00FF00 : 0xFF0000)));
		player.send(new SendColor(53119,
				((player.getAttributes().get("house-filter") != null
						&& ((HouseObject.ObjectType) player.getAttributes().get("house-filter") == ObjectType.TREES))
								? 0x00FF00 : 0xFF0000)));
		player.send(new SendColor(53120,
				((player.getAttributes().get("house-filter") != null
						&& ((HouseObject.ObjectType) player.getAttributes().get("house-filter") == ObjectType.ALTARS))
								? 0x00FF00 : 0xFF0000)));
		player.send(new SendColor(53121,
				((player.getAttributes().get("house-filter") != null
						&& ((HouseObject.ObjectType) player.getAttributes().get("house-filter") == ObjectType.STALLS))
								? 0x00FF00 : 0xFF0000)));
		player.send(new SendColor(53122,
				((player.getAttributes().get("house-filter") != null
						&& ((HouseObject.ObjectType) player.getAttributes().get("house-filter") == ObjectType.MISC))
								? 0x00FF00 : 0xFF0000)));

		HouseObject data = player.getAttributes().get("house-obj-selected") != null
				? (HouseObject) player.getAttributes().get("house-obj-selected") : null;
		player.send(new SendString(data == null ? "Select an Object" : data.toString(), 53115));
		player.send(new SendString((data == null || player.getAttributes().get("house-obj-rotation") == null) ? "0"
				: Integer.toString((Integer) player.getAttributes().get("house-obj-rotation")), 53116));
		for (int i = 0; i < (8 * 3); i++) {
			Item item = (data == null || data.getMaterials() == null || i >= data.getMaterials().length) ? null
					: data.getMaterials()[i];
			player.send(new SendItemToContainer(53111, item != null ? item.getId() : 0,
					item != null ? item.getAmount() : 0, i));
		}
		if (player.getInterfaceManager().getMain() != 53100)
			player.send(new SendInterface(53100));
	}

	/**
	 * Places all objects that are in a house onto the map.
	 * 
	 * @param player
	 *            the player needing the objects spawned.
	 * @param objectList
	 *            the list of objects to spawn onto the map
	 * @param houseHeight
	 *            the height level of the house
	 */
	private static void spawnHouseObjects(Player player, ArrayList<GameObject> objectList, int houseHeight) {
		if (player == null || objectList == null || objectList.isEmpty())
			return;
		for (GameObject object : objectList) {
			if (object != null) {
				ObjectManager.registerObject(new GameObject(object.getId(), object.getLocation().getX(),
						object.getLocation().getY(), houseHeight, object.getType(), object.getFace()));
			}
		}
	}

	/**
	 * Places an object into the house list and spawns it onto the map
	 * 
	 * @param player
	 *            the owner of the house
	 * @param objectId
	 *            the new object's ID
	 * @param objectLocation
	 *            the position for the object to be placed
	 * @param type
	 *            the object type for the new object (0-22)
	 * @param rotation
	 *            the rotation of the object (0-3)
	 * @return if the object was successfully added
	 */
	public static boolean addHouseObject(Player player, int objectId, Location objectLocation, int type, int rotation) {
		if (player == null || objectLocation == null)
			return false;
		player.send(new SendRemoveInterfaces());
		House playerHouse = getActiveHouse(player.getUsername());
		if (playerHouse == null
				|| !player.inArea(playerHouse.getType().getBuildArea(), playerHouse.getHeightLevel(), true)) {
			player.send(new SendMessage("You must be in your house to do this."));
			return false;
		}
		if (!playerHouse.isBuildMode()) {
			player.send(new SendMessage("You must enable build mode to modify objects."));
			return false;
		}
		GameObject existing = playerHouse.getObject(objectLocation);
		final Location newLocation = new Location(objectLocation.getX(), objectLocation.getY(),
				player.getLocation().getZ());
		GameObject newObject = objectId < 1 ? null : new GameObject(objectId, newLocation, type, rotation);
		if (existing != null) {
			ObjectManager.deleteHouseObject(
					new GameObject(existing.getId(), existing.getLocation().getX(), existing.getLocation().getY(),
							player.getLocation().getZ(), existing.getType(), existing.getFace()));
			playerHouse.getObjectList().remove(existing);
		}
		if (objectId > 0 && newObject != null) {
			playerHouse.getObjectList().add(newObject);
			ObjectManager
					.registerObject(new GameObject(newObject.getId(), newLocation, newObject.getType(), newObject.getFace()));
		} else {
			ObjectManager.send(new GameObject(2376, newLocation, type, 0));
		}
		player.getAttributes().remove("house-obj-selected");
		player.getAttributes().remove("house-obj-rotation");
		saveHouse(player.getUsername(), playerHouse);
		return true;
	}

	/**
	 * Adds a new object to the house if the player has the required materials
	 * 
	 * @param player
	 *            the home owner
	 * @param newObject
	 *            the object that will be added
	 * @param rotation
	 *            the rotation of the object.
	 */
	public static void addHouseObject(Player player, HouseObject newObject, int rotation) {
		if (player == null || newObject == null)
			return;
		if (newObject.getMaterials() != null && !player.getInventory().hasAllItems(newObject.getMaterials())) {
			player.send(new SendMessage("To build a " + newObject.toString() + " you need:"));
			Arrays.stream(newObject.getMaterials()).filter(Objects::nonNull).forEach(i -> {
				player.send(new SendMessage(i.getAmount() + " x " + i.getDefinition().getName()));
			});
			return;
		}
		if (newObject.getMaterials() != null) {
			player.getInventory().remove(newObject.getMaterials(), true);
		}
		final Location objectLocation = new Location(player.getLocation().getX(), player.getLocation().getY(), 0);
		if (addHouseObject(player, newObject.getObjectId(), objectLocation, newObject.getType(), rotation)) {
			player.send(new SendMessage("You have created the object " + newObject.toString() + "."));
		}
	}

	/**
	 * Removes an object from the player's house
	 * 
	 * @param player
	 *            the home owner
	 * @param objectId
	 *            the object to be removed
	 * @param objectLocation
	 *            the location of the object
	 */
	public static void deleteHouseObject(Player player, int objectId, Location objectLocation) {
		if (player == null || objectId < 1)
			return;
		House playerHouse = getActiveHouse(player.getUsername());
		if (playerHouse == null
				|| !player.inArea(playerHouse.getType().getBuildArea(), playerHouse.getHeightLevel(), true)) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (!playerHouse.isBuildMode()) {
			player.send(new SendMessage("You must enable build mode to modify objects."));
			return;
		}
		GameObject existingObject = playerHouse.getObject(objectLocation);
		if (existingObject == null || existingObject.getId() != objectId) {
			player.send(
					new SendMessage("This object does not exist. Be sure to use the sectures on objects you placed."));
			return;
		}

		if (addHouseObject(player, 0, objectLocation, existingObject.getType(), 0)) {
			player.send(new SendMessage("You have removed the object."));
			return;
		}
	}

	/**
	 * Removes an object from the player's house
	 * 
	 * @param player
	 *            the home owner
	 * @param objectId
	 *            the object to be removed
	 * @param objectLocation
	 *            the location of the object
	 */
	public static void rotateHouseObject(Player player, int objectId, Location objectLocation, boolean left) {
		if (player == null || objectId < 1)
			return;
		House playerHouse = getActiveHouse(player.getUsername());
		if (playerHouse == null
				|| !player.inArea(playerHouse.getType().getBuildArea(), playerHouse.getHeightLevel(), true)) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (!playerHouse.isBuildMode()) {
			player.send(new SendMessage("You must enable build mode to modify objects."));
			return;
		}
		GameObject existingObject = playerHouse.getObject(objectLocation);
		if (existingObject == null || existingObject.getId() != objectId) {
			player.send(
					new SendMessage("This object does not exist. Be sure to use the hammer on objects you placed."));
			return;
		}

		int newRotation = existingObject.getFace() + (left ? -1 : 1);
		if (newRotation > 3)
			newRotation -= 4;
		else if (newRotation < 0)
			newRotation += 4;

		if (addHouseObject(player, existingObject.getId(), objectLocation, existingObject.getType(), newRotation)) {
			player.send(new SendMessage("You have rotated the object to the " + (left ? "left" : "right") + "."));
			return;
		}
	}

	/**
	 * Enters or exits build mode for the player, if they're in their own
	 * (empty) house
	 * 
	 * @param player
	 *            the player that is setting the build mode
	 */
	private static void enterBuildMode(Player player) {
		if (!houseIsActive(player.getUsername())) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		House house = getActiveHouse(player.getUsername());
		if (house == null) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (playersInHouse(player) > 1) {
			player.send(new SendMessage("You cannot enter build mode with guests in your house."));
			return;
		}
		house.setBuildMode(!house.isBuildMode());
		player.send(new SendMessage(
				"You have <col=dd0000>" + (house.isBuildMode() ? "entered" : "exited") + "</col> build mode."));
		player.send(new SendConfig(690, house.isBuildMode() ? 1 : 0));
	}

	/**
	 * Enables or disables the lock for the player's house, if they're in their
	 * own (empty) house
	 * 
	 * @param player
	 *            the player that is setting the build mode
	 */
	private static void changeHouseLock(Player player) {
		if (!houseIsActive(player.getUsername())) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		House house = getActiveHouse(player.getUsername());
		if (house == null) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (playersInHouse(player) > 1) {
			player.send(new SendMessage("You cannot lock players inside your house. Please kick them first."));
			return;
		}
		house.setHouseLocked(!house.isHouseLocked());
		player.send(new SendMessage(
				"You have <col=dd0000>" + (house.isHouseLocked() ? "locked" : "unlocked") + "</col> your house."));
		player.send(new SendConfig(691, house.isHouseLocked() ? 1 : 0));
	}

	/**
	 * Kicks a specified player from the house. Does not permanently ban them
	 * from house.
	 * 
	 * @param player
	 *            the player who is kicking the other player
	 * @param toKick
	 *            the name of the player to kick.
	 */
	public static void kickPlayer(Player player, String toKick) {
		if (player == null || toKick == null)
			return;
		player.send(new SendRemoveInterfaces());
		House house = getActiveHouse(player.getUsername());
		if (house == null) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (playersInHouse(player) < 2) {
			player.send(new SendMessage("It would be silly to kick players when you're the only one in the house."));
			return;
		}
		Player kickedPlayer = World.getPlayerByName(formatName(toKick));
		if (kickedPlayer == null) {
			player.send(new SendMessage("That player appears to be offline."));
			return;
		}
		if (!kickedPlayer.inArea(house.getType().getBuildArea(),
				((player.getIndex() * 4) + house.getType().getSpawnLocation().getZ()), true)) {
			player.send(new SendMessage("You cannot kick players that are not in your house."));
			return;
		}
		kickedPlayer.send(new SendSidebarInterface(13, 52500));
		kickedPlayer.teleport(PlayerConstants.HOME);
		kickedPlayer.send(new SendMessage(
				"You have been booted from " + Utility.formatPlayerName(player.getUsername()) + "'s house."));
		player.send(new SendMessage(
				"You have booted " + Utility.formatPlayerName(kickedPlayer.getUsername()) + " from your house."));
	}

	/**
	 * Removes all players from a house except the house owner no players are
	 * banned.
	 * 
	 * @param player
	 *            the home owner
	 */
	private static void kickAllPlayers(Player player) {
		House house = getActiveHouse(player.getUsername());
		if (house == null) {
			player.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (playersInHouse(player) < 2) {
			player.send(new SendMessage("It would be silly to kick players when you're the only one in the house."));
			return;
		}
		Arrays.stream(World.getPlayers())
				.filter(p -> p != null && !p.getUsername().equalsIgnoreCase(player.getUsername())
						&& p.inArea(house.getType().getBuildArea(),
								((player.getIndex() * 4) + house.getType().getSpawnLocation().getZ()), true))
				.forEach(p -> {
					p.send(new SendSidebarInterface(13, 52500));
					p.teleport(PlayerConstants.HOME);
					p.send(new SendMessage("You have been booted from " + Utility.formatPlayerName(player.getUsername())
							+ "'s house."));
				});
		player.send(new SendMessage("You have booted all players from your house."));
	}

	/**
	 * @param player
	 *            the owner of the house
	 * @return how many players are in the house
	 */
	private static int playersInHouse(Player player) {
		if (!houseIsActive(player.getUsername()))
			return 0;
		House house = getActiveHouse(player.getUsername());
		if (house == null)
			return 0;
		int height = ((player.getIndex() * 4) + house.getType().getSpawnLocation().getZ());
		int count = 0;
		for (Player p : World.getPlayers()) {
			if (p != null && p.inArea(house.getType().getBuildArea(), height, true))
				count++;
		}
		return count;
	}

	/**
	 * Checks if a player is standing in ANY active house.
	 * 
	 * @param player
	 *            the player to check
	 * @return if they're in a house
	 */
	public static boolean inHouse(Player player) {
		if (player == null)
			return false;
		for (House h : ACTIVE_HOUSES) {
			if (h == null)
				continue;
			if (player.inArea(h.getType().getBuildArea(), h.getHeightLevel(), true))
				return true;
		}

		return false;
	}

	/**
	 * Checks if a player is standing in ANY active house.
	 * 
	 * @param player
	 *            the player to check
	 * @return if they're in a house
	 */
	public static boolean inOwnHouse(Player player) {
		if (player == null)
			return false;
		House house = getActiveHouse(player.getUsername());
		if (house == null) {
			return false;
		}

		return player.inArea(house.getType().getBuildArea(), house.getHeightLevel(), true);
	}

	/**
	 * Checks if a player is banned from the specified house.
	 * 
	 * @param house
	 *            The house containing the list of banned players
	 * @param playerName
	 *            the name to check if banned or not
	 * @return true if playerName is banned.
	 */
	private static boolean playerIsBanned(House house, String playerName) {
		if (house == null || house.getBannedUsers() == null || house.getBannedUsers().isEmpty() || playerName == null)
			return false;
		playerName = formatName(playerName);
		return house.getBannedUsers().contains(playerName);
	}

	/**
	 * Either punishes or removes the punishment on a player, depending on the
	 * boolean
	 * 
	 * @param homeOwner
	 *            the person punishing the player
	 * @param playerToPunish
	 *            the person being punished
	 * @param removePunishment
	 *            true if remove punishment, false if add
	 */
	public static void punishPlayer(Player homeOwner, String playerToPunish, boolean removePunishment) {
		if (homeOwner == null || playerToPunish == null)
			return;
		playerToPunish = formatName(playerToPunish);
		House house = getActiveHouse(homeOwner.getUsername());
		if (house == null || !houseIsActive(homeOwner.getUsername()) || !ownsHouse(homeOwner.getUsername(), house)
				|| !homeOwner.inArea(house.getType().getBuildArea(), house.getHeightLevel(), true)) {
			homeOwner.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (removePunishment)
			unbanPlayer(homeOwner, house, playerToPunish);
		else
			banPlayer(homeOwner, house, playerToPunish);
	}

	/**
	 * Bans a specified name from a house
	 * 
	 * @param homeOwner
	 *            the owner of the house
	 * @param house
	 *            the how to add the punishment to
	 * @param playerToBan
	 *            the player name being banned
	 */
	private static void banPlayer(Player homeOwner, House house, String playerToBan) {
		if (homeOwner == null || house == null || playerToBan == null)
			return;
		playerToBan = formatName(playerToBan);
		if (!houseIsActive(homeOwner.getUsername()) || !ownsHouse(homeOwner.getUsername(), house)
				|| !homeOwner.inArea(house.getType().getBuildArea(), house.getHeightLevel(), true)) {
			homeOwner.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (playerIsBanned(house, playerToBan)) {
			homeOwner.send(new SendMessage("This player is already banned. Please unban them before re-banning."));
			return;
		}
		Player bannedPlayer = World.getPlayerByName(playerToBan);

		if (bannedPlayer == null) {
			homeOwner.send(new SendMessage("Could not find player. They may be offline."));
			return;
		}
		if (!bannedPlayer.inArea(house.getType().getBuildArea(), house.getHeightLevel(), true)) {
			homeOwner.send(new SendMessage("Error. This player must be in your house to ban them."));
			return;
		}
		house.getBannedUsers().add(playerToBan);
		bannedPlayer.send(new SendSidebarInterface(13, 52500));
		bannedPlayer.teleport(PlayerConstants.HOME);
		bannedPlayer.send(new SendMessage(
				"You have been banned from " + Utility.formatPlayerName(homeOwner.getUsername()) + "'s house."));
		homeOwner.send(
				new SendMessage("You have banned " + Utility.formatPlayerName(playerToBan) + " from your house."));
		saveHouse(homeOwner.getUsername(), house);
	}

	/**
	 * Removes the ban from a player for a house
	 * 
	 * @param homeOwner
	 *            the owner of the house
	 * @param house
	 *            the how to remove the punishment from
	 * @param playerToBan
	 *            the player name being unbanned
	 */
	private static void unbanPlayer(Player homeOwner, House house, String playerToBan) {
		if (homeOwner == null || house == null || playerToBan == null)
			return;
		playerToBan = formatName(playerToBan);
		if (!houseIsActive(homeOwner.getUsername()) || !ownsHouse(homeOwner.getUsername(), house)
				|| !homeOwner.inArea(house.getType().getBuildArea(), house.getHeightLevel(), true)) {
			homeOwner.send(new SendMessage("You must be in your house to do this."));
			return;
		}
		if (!playerIsBanned(house, playerToBan)) {
			homeOwner.send(new SendMessage(
					"Could not find " + playerToBan + " on the ban list. Did you spell the name right?"));
			return;
		}
		Player bannedPlayer = World.getPlayerByName(playerToBan);

		if (bannedPlayer == null) {
			homeOwner.send(new SendMessage("Player offline. They will not be notified of being unbanned."));
		}
		house.getBannedUsers().remove(playerToBan);
		if (bannedPlayer != null) {
			bannedPlayer.send(new SendMessage(
					"You have been unbanned from " + Utility.formatPlayerName(homeOwner.getUsername()) + "'s house."));
		}
		homeOwner.send(
				new SendMessage("You have unbanned " + Utility.formatPlayerName(playerToBan) + " from your house."));
		saveHouse(homeOwner.getUsername(), house);
	}

	/**
	 * When a player leaves the house by normal means they execute this method.
	 * 
	 * @param player
	 */
	public static void leaveHouse(Player player) {
		if (player == null || !inHouse(player))
			return;
		if (inOwnHouse(player)) {
			exitHouse(player, true);
		} else {
			player.teleport(PlayerConstants.HOME);
			player.send(new SendMessage("You have left the P.O.H"));
		}
		player.send(new SendSidebarInterface(13, 52500));
		resetAttributes(player);
	}

	/**
	 * Removes all players from a house when the owner leaves.
	 * 
	 * @param player
	 *            the owner of the house
	 * @param moveOwner
	 *            whether or not the owner should be moved.
	 */
	public static void exitHouse(Player player, boolean moveOwner) {
		House house = getActiveHouse(player.getUsername());
		if (house == null || !houseIsActive(player.getUsername()) || !ownsHouse(player.getUsername(), house)
				|| !player.inArea(house.getType().getBuildArea(), house.getHeightLevel(), true)
				|| playersInHouse(player) < 1) {
			return;
		}
		if (player.getAttributes().get("house-enter") != null)
			return;
		int height = ((player.getIndex() * 4) + house.getType().getSpawnLocation().getZ());
		for (Player p : World.getPlayers()) {
			if (p != null && p.inArea(house.getType().getBuildArea(), height, true)) {
				if (!p.getUsername().equalsIgnoreCase(player.getUsername()) || moveOwner)
					p.teleport(PlayerConstants.HOME);
				p.send(new SendMessage(p.getUsername().equalsIgnoreCase(player.getUsername()) ? "You leave your house."
						: "The host has left their house therefore everyone has been removed."));
				p.send(new SendSidebarInterface(13, 52500));
			}
		}
		if (house.getObjectList() != null && house.getObjectList().size() > 0) {
			house.getObjectList().stream().filter(Objects::nonNull).forEach(o -> ObjectManager.removeActive(o));
		}
		if (house.isBuildMode() && player.getUsername().equalsIgnoreCase(house.getHomeOwner())) {
			house.setBuildMode(false);
			saveHouse(player.getUsername(), house);
		}
		ACTIVE_HOUSES.remove(house);
		resetAttributes(player);
	}

	/**
	 * Verifies if an object exists in any active house. This is used to
	 * override the region checking.
	 * 
	 * @param objectId
	 *            the id of the object
	 * @param x
	 *            the x position of the object
	 * @param y
	 *            the y position of the object
	 * @param height
	 *            the height level of the object
	 * @return if the object exists in any house
	 */
	public static boolean objectInHouse(int objectId, int x, int y, int height) {
		if (ACTIVE_HOUSES == null || ACTIVE_HOUSES.size() < 1)
			return false;
		for (House house : ACTIVE_HOUSES) {
			if (house == null)
				continue;
			Predicate<GameObject> pre = o -> o.getId() == objectId && o.getLocation().getX() == x
					&& o.getLocation().getY() == y && house.getHeightLevel() == height;
			if (house.getObjectList() != null && house.getObjectList().size() > 0)
				return house.getObjectList().stream().filter(Objects::nonNull).anyMatch(pre);
		}

		return false;
	}

	/**
	 * Verfies the player's house is active
	 * 
	 * @param playerName
	 *            the name of the owner of the house
	 * @return true if their house is active
	 */
	private static boolean houseIsActive(String playerName) {
		return getActiveHouse(playerName) != null;
	}

	/**
	 * Gets the Active House object if a player currently is in their house
	 * 
	 * @param playerName
	 *            the name of the owner of the house
	 * @return the active player house, or null if they don't have one open
	 *         currently
	 */
	private static House getActiveHouse(String playerName) {
		if (ACTIVE_HOUSES == null || ACTIVE_HOUSES.size() < 1 || playerName == null)
			return null;
		playerName = formatName(playerName);
		for (House house : ACTIVE_HOUSES) {
			if (house != null && house.getHomeOwner().equalsIgnoreCase(playerName))
				return house;
		}
		return null;
	}

	/**
	 * Check if a player has ANY existing house.
	 * 
	 * @param playerName
	 *            the player to check
	 * @return true if they own a house
	 */
	public static boolean ownsHouse(String playerName) {
		if (PLAYER_HOUSES == null || PLAYER_HOUSES.size() < 1 || playerName == null)
			return false;
		playerName = formatName(playerName);
		return PLAYER_HOUSES.get(playerName) != null;
	}

	/**
	 * Verifies a player owns a house in question
	 * 
	 * @param playerName
	 *            the player name in question
	 * @param house
	 *            the house to verify the player name
	 * @return true if they own the house, false if not
	 */
	private static boolean ownsHouse(String playerName, House house) {
		if (house == null || playerName == null)
			return false;
		playerName = formatName(playerName);
		return house.getHomeOwner().equalsIgnoreCase(playerName);
	}

	/**
	 * Creates a base house for a player
	 * 
	 * @param player
	 *            the player creating the house
	 * @param type
	 *            the type (or size) of the house
	 * @return if the house that was created saved successfully.
	 */
	public static boolean createHouse(Player player, HouseType type) {
		House playerHouse = new House(player.getUsername(), new ArrayList<GameObject>(), new ArrayList<String>(), 0,
				type);
		if (PLAYER_HOUSES != null)
			PLAYER_HOUSES.put(formatName(player.getUsername()), playerHouse);
		return saveHouse(player.getUsername(), playerHouse);
	}

	/**
	 * Creates a base house for a player
	 * 
	 * @param player
	 *            the player creating the house
	 * @param type
	 *            the type (or size) of the house
	 * @return if the house that was created saved successfully.
	 */
	public static boolean deleteHouse(Player player) {
		if (player == null)
			return false;
		String playerName = formatName(player.getUsername());
		player.send(new SendRemoveInterfaces());
		if (getActiveHouse(playerName) != null) {
			player.send(
					new SendMessage("You cannot delete your house while players are inside it. (Including yourself)"));
			return false;
		}
		House playerHouse = PLAYER_HOUSES.get(playerName);
		if (playerHouse != null) {
			PLAYER_HOUSES.remove(playerName);
			ACTIVE_HOUSES.remove(playerHouse);
		}
		try {
			File houseFile = new File(FILE_DIRECTORY + playerName + ".json");
			player.send(new SendMessage("Your house has been deleted. You're free to pick another."));
			return houseFile.delete();
		} catch (Exception er) {
			player.send(new SendMessage("Error deleting your house! Please report to staff!"));
			er.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves a house to the FILE_DIRECTORY based on a player's name.
	 * 
	 * @param playerName
	 *            the name of the player, and what will be the file name.
	 * @param playerHouse
	 *            the player's house and all it's data
	 * @return true if it saved, false if there was an error.
	 */
	private static boolean saveHouse(String playerName, House playerHouse) {
		if (playerName == null || playerHouse == null)
			return false;
		playerName = formatName(playerName);
		try (FileWriter writer = new FileWriter(FILE_DIRECTORY + playerName + ".json")) {
			boolean buildMode = playerHouse.isBuildMode();
			playerHouse.setBuildMode(false);
			String json = gson.toJson(playerHouse);
			playerHouse.setBuildMode(buildMode);
			writer.write(json);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Parses the raw json into a House object
	 * 
	 * @param houseFile
	 *            the json file to read
	 * @return a House object based on the json
	 */
	private static House loadHouse(File houseFile) {
		try (FileReader reader = new FileReader(houseFile)) {
			return gson.fromJson(reader, House.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Called on server start, loads all the existing houses from the folder.
	 */
	public static void declare() {
		if (PLAYER_HOUSES != null && PLAYER_HOUSES.size() > 0)
			PLAYER_HOUSES.clear();
		File dir = new File(FILE_DIRECTORY);
		if (!dir.exists()) {
			logger.info("No house folder found. attempting to create.");
			dir.mkdirs();
			return;
		}
		File[] houses = dir.listFiles(FILE_EXTENSIONS);
		if (houses == null || houses.length < 1) {
			logger.info("House folder empty. Nothing to load.");
			return;
		}
		for (File house : houses) {
			House playerHouse = loadHouse(house);
			if (playerHouse != null)
				PLAYER_HOUSES.put(house.getName().substring(0, house.getName().indexOf('.')).toLowerCase(),
						playerHouse);
		}
		logger.info("Loaded " + PLAYER_HOUSES.size() + " Player houses.");
	}

	/**
	 * Formats a given string to the standards of the house files.
	 * 
	 * @param unformattedName
	 *            The string that hasn't been processed
	 * @return the formatted name
	 */
	private static String formatName(String unformattedName) {
		if (unformattedName == null)
			return unformattedName;
		return unformattedName.toLowerCase().replaceAll("_", " ").trim();
	}

	public static void resetAttributes(Player player) {
		if (player == null || player.getAttributes() == null)
			return;
		player.getAttributes().remove("house-obj-rotation");
		player.getAttributes().remove("house-ban-name");
		player.getAttributes().remove("house-unban-name");
		player.getAttributes().remove("house-kick-name");
		player.getAttributes().remove("house-filter");
		player.getAttributes().remove("house-obj-selected");
		player.getAttributes().remove("house-enter");
		player.getAttributes().remove("house-friend-name");
	}
}
