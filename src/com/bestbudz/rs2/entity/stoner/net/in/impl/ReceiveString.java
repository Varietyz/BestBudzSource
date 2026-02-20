package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class ReceiveString extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    String text = in.readString();
    int index = text.indexOf(",");
    int id = Integer.parseInt(text.substring(0, index));
    String string = text.substring(index + 1);
    switch (id) {
		case 99998: // Dock deposit attributes
			try {
				String[] parts = string.split(",");
				if (parts.length == 2) {
					int itemId = Integer.parseInt(parts[0]);
					int amount = Integer.parseInt(parts[1]);
					stoner.getAttributes().set("dock_deposit_itemid", itemId);
					stoner.getAttributes().set("dock_deposit_amount", amount);
					System.out.println("Set deposit attributes: itemId=" + itemId + ", amount=" + amount);
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid dock deposit attributes: " + string);
			}
			break;

		case 99999: // Dock withdraw attributes
			try {
				String[] parts = string.split(",");
				if (parts.length == 2) {
					int itemId = Integer.parseInt(parts[0]);
					int amount = Integer.parseInt(parts[1]);
					stoner.getAttributes().set("dock_withdraw_itemid", itemId);
					stoner.getAttributes().set("dock_withdraw_amount", amount);
					System.out.println("Set withdraw attributes: itemId=" + itemId + ", amount=" + amount);
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid dock withdraw attributes: " + string);
			}
			break;
      default:
        System.out.println("Received string: identifier=" + id + ", string=" + string);
        break;
    }
  }
}
