package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import java.util.Arrays;

public class DockPanelPacket extends IncomingPacket {

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
		if (in == null || !in.readable()) {
			System.err.println("[DockPanelPacket] Empty or unreadable input buffer.");
			return;
		}

		String input = in.readString();
		if (input == null || input.isEmpty()) {
			System.err.println("[DockPanelPacket] Empty string from readString().");
			return;
		}

		String[] args = input.split(":");
		if (args.length < 2) {
			System.err.println("[DockPanelPacket] Malformed input: " + input);
			return;
		}

		switch (args[0]) {
			case "bank":
				handleBank(stoner, args);
				break;
			default:
				System.err.println("[DockPanelPacket] Unknown prefix: " + args[0]);
		}
	}

	private void handleBank(Stoner stoner, String[] args) {
		switch (args[1]) {
			case "open":
				stoner.getBank().openBank();
				break;
			case "withdraw":
				if (args.length >= 4) {
					try {
						int id = Integer.parseInt(args[2]);
						int amt = Integer.parseInt(args[3]);
						stoner.getBank().withdraw(id, amt);
					} catch (NumberFormatException e) {
						System.err.println("Invalid withdraw args: " + Arrays.toString(args));
					}
				}
				break;
			case "deposit":
				if (args.length >= 5) {
					try {
						int id = Integer.parseInt(args[2]);
						int amt = Integer.parseInt(args[3]);
						int slot = Integer.parseInt(args[4]);
						stoner.getBank().deposit(id, amt, slot);
					} catch (NumberFormatException e) {
						System.err.println("Invalid deposit args: " + Arrays.toString(args));
					}
				}
				break;
			case "search":
				if (args.length >= 3) {
					stoner.getBank().setSearching(Boolean.parseBoolean(args[2]));
				}
				break;
			case "rearrange":
				if (args.length >= 3) {
					String type = args[2];
					stoner.getBank().rearrangeType = type.equalsIgnoreCase("swap") ?
						Bank.RearrangeTypes.SWAP : Bank.RearrangeTypes.INSERT;
				}
				break;
			case "withdrawtype":
				if (args.length >= 3) {
					String type = args[2];
					stoner.getBank().withdrawType = type.equalsIgnoreCase("note") ?
						Bank.WithdrawTypes.NOTE : Bank.WithdrawTypes.ITEM;
				}
				break;
			case "update":
				stoner.getBank().update();
				break;
			default:
				System.err.println("[DockPanelPacket] Unknown bank operation: " + args[1]);
		}
	}

	@Override
	public int getMaxDuplicates() {
		return 10;
	}
}
