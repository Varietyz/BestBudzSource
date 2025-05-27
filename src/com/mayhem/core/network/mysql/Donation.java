package com.mayhem.core.network.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.membership.RankHandler;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Using this class:
 * To call this class, it's best to make a new thread. You can do it below like so:
 * new Thread(new Donation(player)).start();
 */
public class Donation implements Runnable {

	public static final String HOST = "http://falcon-express.net/";
	public static final String USER = "";
	public static final String PASS = "";
	public static final String DATABASE = "";

	private Player player;
	private Connection conn;
	private Statement stmt;

	/**
	 * The constructor
	 * @param player
	 */
	public Donation(Player player) {
		this.player = player;
	}

	
	@Override
	public void run() {
		try {
			player.send(new SendMessage("Your donation is being processed....Please wait."));
			if (!connect(HOST, DATABASE, USER, PASS)) {
				player.send(new SendMessage("Donation claim failed, please contact an administrator."));
				return;
			}
			String name = player.getUsername().replace("_", " ");
			ResultSet rs = executeQuery("SELECT * FROM payments WHERE player_name='"+name+"' AND status='Completed' AND claimed=0");
			
			while (rs.next()) {
				int item_number = rs.getInt("item_number");
				double paid = rs.getDouble("amount");
				int quantity = rs.getInt("quantity"); 
				
				switch (item_number) {// add products according to their ID in the ACP

				case 18: // 100 FalconExpress bucks
					player.getInventory().add(13190, 1 * quantity); 
					//player.setMoneySpent(player.getMoneySpent() + (int)paid);
					//RankHandler.upgrade(player);
					//player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 19: // 300 FalconExpress bucks
					player.getInventory().add(13191, 1 * quantity); 
					//player.setMoneySpent(player.getMoneySpent() + (int)paid);
					//RankHandler.upgrade(player);
					//player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");

				break;
				case 20: // 500 FalconExpress bucks
					player.getInventory().add(13192, 1 * quantity); 
					//player.setMoneySpent(player.getMoneySpent() + (int)paid);
				//	RankHandler.upgrade(player);
				//	player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 21: // 1000 FalconExpress bucks
					player.getInventory().add(14000, 1 * quantity); 
				//	player.setMoneySpent(player.getMoneySpent() + (int)paid);
				//	RankHandler.upgrade(player);
				//	player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 22: // 2500 FalconExpress bucks
					player.getInventory().add(13195, 1 * quantity); 
				//	player.setMoneySpent(player.getMoneySpent() + (int)paid);
				//	RankHandler.upgrade(player);
				//	player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 23: // 5000 FalconExpress bucks
					player.getInventory().add(13196, 1 * quantity); 
			//		player.setMoneySpent(player.getMoneySpent() + (int)paid);
				//	RankHandler.upgrade(player);
				//	player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 24: // 10,000 FalconExpress bucks
					player.getInventory().add(5444, 1 * quantity); 
				//	player.setMoneySpent(player.getMoneySpent() + (int)paid);
				//	RankHandler.upgrade(player);
				//	player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 25: // 20,000 FalconExpress bucks
					player.getInventory().add(13198, 1 * quantity); 
				//	player.setMoneySpent(player.getMoneySpent() + (int)paid);
				//	RankHandler.upgrade(player);
				//	player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 29: // Bag of dice
					player.getInventory().add(13190, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 26: // 10x keys
					player.getInventory().add(990, 10 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 27: // 50x keys
					player.getInventory().add(990, 50 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 28: //100x keys
					player.getInventory().add(990, 100 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 44: // partyhat
					player.getInventory().add(1040, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 45: // partyhat
					player.getInventory().add(1042, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				case 46: // partyhat
					player.getInventory().add(1044, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 47: // partyhat
					player.getInventory().add(1046, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 48: // partyhat
					player.getInventory().add(1048, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 49: // mystery box
					player.getInventory().add(6199, 1 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 50: // mystery box x10
					if (player.getInventory().hasSpaceFor(new Item(6199, 10 * quantity))) {
					player.getInventory().add(6199, 10 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
					} else if (player.getBank().hasSpaceFor((new Item(6199, 10 * quantity)))) {
						player.getBank().add(6199, 10 * quantity); 
						player.setMoneySpent(player.getMoneySpent() + (int)paid);
						RankHandler.upgrade(player);
						player.setMember(true);
						player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress. Your donation has been sent to your bank."));
					}
				break;
				case 30: // 50,000 bloodmoney
					player.getInventory().add(13307, 50000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 31: // 100,000 bloodmoney
					player.getInventory().add(13307, 100000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 32: // 250,000 bloodmoney
					player.getInventory().add(13307, 250000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 33: // 500000 bloodmoney
					player.getInventory().add(13307, 500000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)paid);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 34: // 100x Mithril Seeds
					player.getInventory().add(299, 100 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)1);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 35: // 300x Mithril Seeds
					player.getInventory().add(299, 300 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)3);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 36: // 500x Mithril Seeds
					player.getInventory().add(299, 500 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)5);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 37: // 1000x Mithril Seeds
					player.getInventory().add(299, 1000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)10);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 38: // 5M
					player.getInventory().add(995, 5000000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)2);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 39: // 25M
					player.getInventory().add(995, 25000000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)10);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 40: // 50M
					player.getInventory().add(995, 50000000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)18);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				case 41: // 100M
					player.getInventory().add(995, 100000000 * quantity); 
					player.setMoneySpent(player.getMoneySpent() + (int)35);
					RankHandler.upgrade(player);
					player.setMember(true);
					player.getClient().queueOutgoingPacket(new SendMessage("Thank you for supporting FalconExpress."));
					World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				break;
				
				
								
				}
				player.send(new SendMessage("You've claimed your donation."));
				World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + " has just Donated to FalconExpress! Thank you for your contribution! ");
				rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
				rs.updateRow();
			}

			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param host the host ip address or url
	 * @param database the name of the database
	 * @param user the user attached to the database
	 * @param pass the users password
	 * @return true if connected
	 */
	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("[Donation]: Failed connecting to database!");
			return false;
		}
	}

	/**
	 * Disconnects from the MySQL server and destroy the connection
	 * and statement instances
	 */
	public void destroy() {
        try {
    		conn.close();
        	conn = null;
        	if (stmt != null) {
    			stmt.close();
        		stmt = null;
        	}
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Executes an update query on the database
	 * @param query
	 * @see {@link Statement#executeUpdate}
	 */
	public int executeUpdate(String query) {
        try {
        	this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

	/**
	 * Executres a query on the database
	 * @param query
	 * @see {@link Statement#executeQuery(String)}
	 * @return the results, never null
	 */
	public ResultSet executeQuery(String query) {
        try {
        	this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
