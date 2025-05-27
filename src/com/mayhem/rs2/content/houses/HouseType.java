package com.mayhem.rs2.content.houses;

import com.mayhem.rs2.entity.Area;
import com.mayhem.rs2.entity.Location;

public enum HouseType {
	SMALL(/*Iban's Throne Room*/"Small House", 1000000, new Location(2139, 4647, 1), new Area(2131, 4640, 2145, 4655, (byte) 1)),
	MEDIUM(/*Ancient Temple*/"Medium House", 5000000, new Location(3233, 9316, 0), new Area(3227, 9310, 3239, 9323, (byte) 0)),
	LARGE(/*Thammaron's Throne Room*/"Large House", 30000000, new Location(2720, 4884, 2), new Area(2709, 4879, 2731, 4919, (byte) 2));
	
	private String name;
	private int Price;
	private Location spawnLocation;
	private Area buildArea;

	HouseType(String name, int Price, Location spawnLocation, Area buildArea) {
		this.name = name;
		this.Price = Price;
		this.spawnLocation = spawnLocation;
		this.buildArea = buildArea;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return Price;
	}

	public Area getBuildArea() {
		return buildArea;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}
}
