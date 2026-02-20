package com.bestbudz.rs2.entity;

import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.rs2.content.dwarfcannon.DwarfCannon;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobUpdateFlags;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNPCUpdate;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerUpdate;
import java.util.List;

public class WorldUpdateManager {

	public void processEntities(Stoner[] stoners, Mob[] mobs, List<DwarfCannon> cannons,
								StonerUpdateFlags[] pFlags, MobUpdateFlags[] nFlags) {

		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null) continue;

			try {
				if (!stoner.isActive()) {
					if (stoner.getClient().getStage() == Client.Stages.LOGGED_IN) {
						stoner.setActive(true);
						stoner.start();
						stoner.getClient().resetLastPacketReceived();
					} else if (World.getCycles() - stoner.getClient().getLastPacketTime() > 30) {
						stoner.logout(true);
						continue;
					}
				}

				if (isDiscordBot(stoner)) {
					processDiscordBotEfficiently(stoner);

				} else if (isPet(stoner)) {
					processPetEfficiently(stoner);

				} else {

					stoner.getClient().processIncomingPackets();
					stoner.process();
					stoner.getClient().reset();

					for (var cannon : cannons) {
						if (cannon.getLoc().isViewableFrom(stoner.getLocation())) {
							cannon.rotate(stoner);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		for (int i = 0; i < mobs.length; i++) {
			var mob = mobs[i];
			if (mob == null) continue;

			try {
				mob.process();
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}

		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null || !stoner.isActive()) continue;

			try {

				stoner.getMovementHandler().process();

				pFlags[i] = new StonerUpdateFlags(stoner);

			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		for (var mob : mobs) {
			if (mob == null) continue;
			try {
				mob.processMovement();
				nFlags[mob.getIndex()] = new MobUpdateFlags(mob);
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}

		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null || !stoner.isActive() || pFlags[i] == null) continue;

			try {

				if (!isPet(stoner) && !isDiscordBot(stoner)) {
					stoner.getClient().queueOutgoingPacket(new SendStonerUpdate(pFlags));
					stoner.getClient().queueOutgoingPacket(new SendNPCUpdate(nFlags, pFlags[i]));
				}
			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null || !stoner.isActive()) continue;

			try {

				if (isDiscordBot(stoner)) {
					resetDiscordBotProperly(stoner);
				} else if (isPet(stoner)) {
					resetPetProperly(stoner);
				} else {
					stoner.reset();
				}
			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		for (var mob : mobs) {
			if (mob == null) continue;
			try {
				mob.reset();
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}
	}

	private void processPetEfficiently(Stoner pet) {
		try {
			pet.getClient().resetLastPacketReceived();
			pet.getClient().processIncomingPackets();
			pet.process();
			pet.getClient().reset();

		} catch (Exception e) {
			System.err.println("Pet full combat processing error for " + pet.getUsername() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void resetPetProperly(Stoner pet) {
		try {

			pet.reset();
		} catch (Exception e) {
			System.err.println("Pet reset error for " + pet.getUsername() + ": " + e.getMessage());
		}
	}

	private void processDiscordBotEfficiently(Stoner bot) {
		try {
			bot.getClient().resetLastPacketReceived();
			bot.getClient().processIncomingPackets();
			bot.process();
			bot.getClient().reset();

		} catch (Exception e) {
			System.err.println("Discord bot processing error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void resetDiscordBotProperly(Stoner bot) {
		try {

			bot.reset();
		} catch (Exception e) {
			System.err.println("Discord bot reset error: " + e.getMessage());
		}
	}

	private boolean isDiscordBot(Stoner stoner) {
		return stoner != null && DEFAULT_USERNAME.equals(stoner.getUsername());
	}

	private boolean isPet(Stoner stoner) {
		return stoner != null && stoner.isPetStoner();
	}
}
