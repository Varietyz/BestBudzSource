package com.mayhem.rs2.content.houses;

import java.util.ArrayList;

import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.object.GameObject;

public class House {

	private String homeOwner;
	private ArrayList<GameObject> objectList;
	private ArrayList<String> bannedUsers;
	private int heightLevel;
	private HouseType type;
	private boolean buildMode;
	private boolean houseLocked;

	public House(String homeOwner, ArrayList<GameObject> objectList, ArrayList<String> bannedUsers, int heightLevel,
			HouseType type) {
		this.homeOwner = homeOwner;
		this.objectList = objectList;
		this.bannedUsers = bannedUsers;
		this.heightLevel = heightLevel;
		this.type = type;
		this.buildMode = false;
		this.houseLocked = false;
	}

	public GameObject getObject(Location objectLocation) {
		if (objectLocation == null || objectList == null || objectList.isEmpty())
			return null;
		for (GameObject object : objectList) {
			if (object != null && object.getLocation().getX() == objectLocation.getX()
					&& object.getLocation().getY() == objectLocation.getY()) {
				return object;
			}
		}
		return null;
	}

	public GameObject getObject(GameObject object) {
		if (object == null || objectList == null || objectList.isEmpty())
			return null;
		for (GameObject o : objectList) {
			if (o != null && o.getLocation().equals(object.getLocation()) && o.getId() == object.getId())
				return o;

		}
		return null;
	}

	public GameObject getObject(int objectId, Location objectLocation) {
		if (objectId < 1 || objectLocation == null || objectList == null || objectList.isEmpty())
			return null;
		for (GameObject object : objectList) {
			if (object != null && object.getLocation().equals(objectLocation) && object.getId() == objectId)
				return object;

		}
		return null;
	}

	public String getHomeOwner() {
		return homeOwner;
	}

	public void setHomeOwner(String homeOwner) {
		this.homeOwner = homeOwner;
	}

	public ArrayList<GameObject> getObjectList() {
		return objectList;
	}

	public void setObjectList(ArrayList<GameObject> objectList) {
		this.objectList = objectList;
	}

	public int getHeightLevel() {
		return heightLevel;
	}

	public void setHeightLevel(int heightLevel) {
		this.heightLevel = heightLevel;
	}

	public HouseType getType() {
		return type;
	}

	public void setType(HouseType type) {
		this.type = type;
	}

	public boolean isBuildMode() {
		return buildMode;
	}

	public void setBuildMode(boolean buildMode) {
		this.buildMode = buildMode;
	}

	public ArrayList<String> getBannedUsers() {
		return bannedUsers;
	}

	public void setBannedUsers(ArrayList<String> bannedUsers) {
		this.bannedUsers = bannedUsers;
	}

	public boolean isHouseLocked() {
		return houseLocked;
	}

	public void setHouseLocked(boolean houseLocked) {
		this.houseLocked = houseLocked;
	}
}
