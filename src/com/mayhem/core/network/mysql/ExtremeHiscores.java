package com.mayhem.core.network.mysql;

import java.sql.PreparedStatement;

import com.mayhem.rs2.entity.player.Player;
import com.mayhem.core.util.Utility;
 
public class ExtremeHiscores implements Runnable {

	private Player player;
	
	public ExtremeHiscores(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		try {
			Database db = new Database("valius.net", "valiu104_admin", "ihateblacks", "valiu104_hiscores");

			String name = Utility.formatString(player.getUsername());
			
			if (!db.init()) {
				System.err.println("[Extreme]: Failed to update "+name+" highscores. Database could not connect.");
				return;
			}
			
			if (player.getRights() == 2 || player.getRights() == 3 || player.getRights() == 4) {
				System.err.println("Didn't save hiscores for [EXTREME]" + player.getUsername());
				return;
			}
			
			PreparedStatement stmt1 = db.prepare("DELETE FROM hs_users_extreme WHERE username=?");
			stmt1.setString(1, name);
			stmt1.execute();
				
			PreparedStatement stmt2 = db.prepare(generateQuery());
			stmt2.setString(1, name);
			stmt2.setInt(2, player.getRights());
			stmt2.setLong(3, player.getSkill().getTotalXp());
			
			for (int i = 0; i < 25; i++) {
				stmt2.setInt(4 + i, (int)player.getSkill().getExperience()[i]);
			}
			
			stmt2.setInt(23, 0);
			stmt2.setInt(24, 0);
			stmt2.setInt(25, 0);
			//i want the last 3 to always be 0 you also removed a few question marks from the end idk if that changes anythingjahsldkjahslkdjhasl it did l0l0l0l
			stmt2.execute();
			
			db.destroyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String generateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO hs_users_extreme (");
		sb.append("username, ");
		sb.append("rights, ");
		sb.append("overall_xp, ");
		sb.append("attack_xp, ");
		sb.append("defence_xp, ");
		sb.append("strength_xp, ");
		sb.append("constitution_xp, ");
		sb.append("ranged_xp, ");
		sb.append("prayer_xp, ");
		sb.append("magic_xp, ");
		sb.append("cooking_xp, ");
		sb.append("woodcutting_xp, ");
		sb.append("fletching_xp, ");
		sb.append("fishing_xp, ");
		sb.append("firemaking_xp, ");
		sb.append("crafting_xp, ");
		sb.append("smithing_xp, ");
		sb.append("mining_xp, ");
		sb.append("herblore_xp, ");
		sb.append("agility_xp, ");
		sb.append("thieving_xp, ");
		sb.append("slayer_xp, ");
		sb.append("farming_xp, ");
		sb.append("runecrafting_xp, ");
		sb.append("hunter_xp, ");
		sb.append("construction_xp, ");
		sb.append("summoning_xp, ");
		sb.append("dungeoneering_xp) ");
		sb.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		return sb.toString();
	}
	
}