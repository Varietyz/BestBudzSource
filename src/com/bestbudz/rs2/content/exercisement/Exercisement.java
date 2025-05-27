package com.bestbudz.rs2.content.exercisement;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.rs2.content.exercisement.obstacle.Obstacle;
import com.bestbudz.rs2.content.exercisement.obstacle.ObstacleType;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public enum Exercisement {

	SINGLETON;

	public static final int GNOME_FLAGS = 0b0111_1111;
	public static final int BARBARIAN_FLAGS = 0b0111_1111;
	public static final int WILDERNESS_FLAGS = 0b0001_1111;

	private static final HashMap<Location, Obstacle> obstacles = new HashMap<>();

	public static void declare() {
	try {
		Obstacle[] loaded = new Gson().fromJson(new BufferedReader(new FileReader("./data/def/professions/exercisementment.json")), Obstacle[].class);
		for (Obstacle obstacle : loaded) {
			obstacles.put(obstacle.getStart(), obstacle);
		}
	} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
		e.printStackTrace();
	}
	}

	public static void main(String[] args) {


	try (FileWriter writer = new FileWriter(new File("./data/def/professions/exercisement1.json"))) {
		Gson builder = new GsonBuilder().setPrettyPrinting().create();

		writer.write(builder.toJson(obstacles.values()).replaceAll("\\{\n      \"x\"", "\\{ \"x\"").replaceAll(",\n      \"y\"", ", \"y\"").replaceAll(",\n      \"z\"", ", \"z\"").replaceAll("\n    \\},", " \\},"));
	} catch (Exception e) {
	}
	}

	public boolean fireObjectClick(Stoner stoner, Location location, RSObject obj) {
	Obstacle obstacle = obstacles.get(stoner.getLocation());

	if (obstacle == null) {
		return false;
	}

	if (stoner.getAttributes().get("EXERCISEMENT_FLAGS") == null) {
		stoner.getAttributes().set("EXERCISEMENT_FLAGS", 0);
	}

	if (obstacle.getType() == ObstacleType.ROPE_SWING) {
		stoner.getAttributes().set("EXERCISEMENT_OBJ", obj);
	}

	obstacle.execute(stoner);


	return false;
	}
}