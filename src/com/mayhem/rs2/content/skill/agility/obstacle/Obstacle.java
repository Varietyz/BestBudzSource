package com.mayhem.rs2.content.skill.agility.obstacle;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public final class Obstacle {
	
	private final ObstacleType type;
	private final Location start;
	private final Location end;
	private final int level;
	private final float experience;
	private final int ordinal;
	private final Obstacle next;
	
	private Obstacle(ObstacleBuilder builder) {
		type = builder.type;
		start = builder.start;
		end = builder.end;
		level = builder.level;
		experience = builder.experience;
		ordinal = builder.ordinal;
		next = builder.next;
	}
	
	public Location getStart() {
		return start;
	}
	
	public Location getEnd() {
		return end;
	}
	
	public Obstacle getNext() {
		return next;
	}
	
	public int getOrdinal() {
		return ordinal;
	}
	
	public ObstacleType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "OBSTACLE [Type: " + type + ", Start: " + start + ", End: " + end + ", Level: " + level + ", Experience: " + experience + ", Ordinal: " + ordinal + "]";
	}

	public void execute(Player player) {
		type.execute(player, next, start, end, level, experience, ordinal);
		int random = Utility.randomNumber(5000);
		if (random == 1) {
			if (player.getInventory().hasSpaceFor(new Item(20663))) {
				player.getInventory().add(new Item(20663));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Giant Squirrel!"));
			} else if (player.getBank().hasSpaceFor((new Item(20663)))) {
				player.getBank().add((new Item(20663)));
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@You receive a Giant Squirrel! It has been sent to your bank."));
			}
				World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved a Giant Squirrel while training Agility!");
			}
		
	}
	
	public static class ObstacleBuilder {
		// Required parameters
		private final ObstacleType type;
		private final Location start;
		private final Location end;
		
		// Optional parameters
		private int level;
		private float experience;
		private int ordinal;
		private Obstacle next;
		
		public ObstacleBuilder(ObstacleType type, Location start, Location end) {
			this.type = type;
			this.end = end;
			this.start = start;
			level = 1;
			experience = 0;
			ordinal = -1;
		}
		
		public ObstacleBuilder setLevel(int level) {
			this.level = level;
			return this;
		}

		public ObstacleBuilder setExperience(float experience) {
			this.experience = experience;
			return this;
		}

		public ObstacleBuilder setOrdinal(int ordinal) {
			this.ordinal = ordinal;
			return this;
		}
		
		public ObstacleBuilder setNext(Obstacle next) {
			this.next = next;
			return this;
		}

		public Obstacle build() {
			return new Obstacle(this);
		}
	}
}