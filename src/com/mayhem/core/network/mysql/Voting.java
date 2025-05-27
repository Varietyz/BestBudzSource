package com.mayhem.core.network.mysql; // dont forget to change packaging ^-^

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;


public class Voting implements Runnable {

	public static final String HOST = "http://falcon-express.net/";
	public static final String USER = "";
	public static final String PASS = "";
	public static final String DATABASE = "";

	private Player player;
	private Connection conn;
	private Statement stmt;
	private ResultSet rs;

	public Voting(Player player) {
		this.player = player;
	}	

	@Override
	public void run() {
		try {
			if (!connect(HOST, DATABASE, USER, PASS)) {
				return;
			}

			String name = player.getUsername().replace(" ", "_");
			rs = executeQuery("SELECT * FROM fx_votes WHERE username='"+name+"' AND claimed=0 AND callback_date IS NOT NULL");

			while (rs.next()) {
				String timestamp = rs.getTimestamp("callback_date").toString();
				String ipAddress = rs.getString("ip_address");
				int siteId = rs.getInt("site_id");


				// -- ADD CODE HERE TO GIVE TOKENS OR WHATEVER
				player.getInventory().addItems(new Item(4067, 10));
				
				//send a message ingame so we can see load it up and just send a message lol? i mean like,
				player.send(new SendMessage("You claim your vote reward."));

				System.out.println("[FoxVote] Vote claimed by "+name+". (sid: "+siteId+", ip: "+ipAddress+", time: "+timestamp+")");

				rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
				rs.updateRow();
			}
			
	

			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("[Voting]: Failing connecting to database!");
			return false;
		}
	}

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


