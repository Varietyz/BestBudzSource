package com.bestbudz.core.network;

import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;

public class ClientMap {

	public static boolean allow(Client client) {
	byte am = 0;

	for (Stoner p : World.getStoners()) {
		if (p != null && p.getClient().getHost() != null && p.getClient().getHost().equals(client.getHost())) {
			am++;
		}
	}

	return am < 9;
	}

	private ClientMap() {
	}

}
