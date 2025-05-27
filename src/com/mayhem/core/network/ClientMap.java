package com.mayhem.core.network;

import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.Client;

public class ClientMap {

	public static boolean allow(Client client) {
		byte am = 0;

		for (Player p : World.getPlayers()) {
			if (p != null && p.getClient().getHost() != null
					&& p.getClient().getHost().equals(client.getHost())) {
				am++;
			}
		}

		return am < 3;
	}

	private ClientMap() {
	}

}
