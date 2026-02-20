package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.auto.combat.AutoCombat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.CombatInterface;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.combat.StonerCombatInterface;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.content.combat.impl.SpecialAssault;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.mob.RareDropEP;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class StonerCombat {
	private final Stoner stoner;
	private final CombatInterface combatInterface;
	private final SpecialAssault specialAssault;
	private final RareDropEP rareDropEP;
	private final AutoCombat autoCombat;

	private long aggressionDelay = System.currentTimeMillis();
	private long currentStunDelay;
	private long setStunDelay;

	private boolean hitZulrah;

	public StonerCombat(Stoner stoner) {
		this.stoner = stoner;
		this.combatInterface = new StonerCombatInterface(stoner);
		this.specialAssault = new SpecialAssault(stoner);
		this.rareDropEP = new RareDropEP();
		this.autoCombat = new AutoCombat(stoner);
	}

	public void process() {
		stoner.getFollowing().process();
		stoner.getCombat().process();
		doAgressionCheck();
		autoCombat.process();
	}

	public void reset() {

	}

	public void afterCombatProcess(Entity assault) {
		combatInterface.afterCombatProcess(assault);
	}

	public boolean canAssault() {
		return combatInterface.canAssault();
	}

	public void checkForDeath() {
		combatInterface.checkForDeath();
	}

	public int getCorrectedDamage(int damage) {
		return combatInterface.getCorrectedDamage(damage);
	}

	public int getMaxHit(CombatTypes type) {
		return combatInterface.getMaxHit(type);
	}

	public void hit(Hit hit) {
		if (!stoner.canTakeDamage()) {
			return;
		}

		if (stoner.isDead()) {
			hit.setDamage(0);
		} else {
			hit.setDamage(getCorrectedDamage((int) hit.getDamage()));
		}

		if (hit.getDamage() > stoner.getGrades()[3]) {
			hit.setDamage(stoner.getGrades()[3]);
		}

		stoner.getGrades()[3] = Math.max(0, stoner.getGrades()[3] - hit.getDamage());

		if (!stoner.getUpdateFlags().isHitUpdate()) {
			stoner.getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		} else {
			stoner.getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
		}

		if (hit.getAssaulter() != null) {
			stoner.getCombat().getDamageTracker().addDamage(hit.getAssaulter(), hit.getDamage());

			if (stoner.isRetaliate() && stoner.getCombat().getAssaulting() == null) {
				stoner.getCombat().setAssault(hit.getAssaulter());
			}

		}

		if (!stoner.isDead()) {
			checkForDeath();
		}

		if (hit.getAssaulter() != null) {
			hit.getAssaulter().onHit(stoner, hit);
		}

	}

	public boolean isIgnoreHitSuccess() {
		return combatInterface.isIgnoreHitSuccess();
	}

	public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {
		combatInterface.onAssault(assault, hit, type, success);
	}

	public void onCombatProcess(Entity assault) {
		combatInterface.onCombatProcess(assault);
	}

	public void onHit(Entity e, Hit hit) {
		combatInterface.onHit(e, hit);

		if (e.isNpc()) {
			Mob m = World.getNpcs()[e.getIndex()];
			if (m != null) {
				rareDropEP.forHitOnMob(stoner, m, hit);
			}
		}
	}

	public void updateCombatType() {
		CombatTypes type;
		if (stoner.getMage().getSpellCasting().isCastingSpell()) {
			type = CombatTypes.MAGE;
		} else {
			type = EquipmentConstants.getCombatTypeForWeapon(stoner);
		}

		if (type != CombatTypes.MAGE) {
			stoner.send(new SendConfig(333, 0));
		}

		stoner.getCombat().setCombatType(type);

		switch (type) {
			case MELEE:
				stoner.getEquipment().updateMeleeDataForCombat();
				break;
			case SAGITTARIUS:
				stoner.getEquipment().updateSagittariusDataForCombat();
				break;
			default:
				break;
		}
	}

	public void retaliate(Entity assaulted) {
		if (assaulted != null) {

			if (!stoner.isPetStoner() && assaulted instanceof Stoner && ((Stoner) assaulted).isPetStoner()) {
				return;
			}

			if (stoner.isRetaliate() && stoner.getCombat().getAssaulting() == null && !stoner.getMovementHandler().moving()) {
				stoner.getCombat().setAssault(assaulted);
			}
		}
	}

	public void doAgressionCheck() {
		if (!stoner.getController().canAssaultNPC()) {
			return;
		}

		short[] override = new short[3];
		if ((stoner.getCombat().inCombat()) && (stoner.getCombat().getLastAssaultedBy().isNpc())) {
			Mob m = World.getNpcs()[stoner.getCombat().getLastAssaultedBy().getIndex()];
			if (m != null) {
				if (m.getId() == 2215) {
					override[0] = 2216; override[1] = 2217; override[2] = 2218;
				} else if (m.getId() == 3162) {
					override[0] = 3163; override[1] = 3164; override[2] = 3165;
				} else if (m.getId() == 2205) {
					override[0] = 2206; override[1] = 2207; override[2] = 2208;
				} else if (m.getId() == 3129) {
					override[0] = 3130; override[1] = 3131; override[2] = 3132;
				}
			}
		}

		java.util.List<Mob> npcsToCheck = new java.util.ArrayList<>();

		if (World.isDiscordBot(stoner) || World.isPet(stoner)) {

			Mob[] worldNpcs = World.getNpcs();
			if (worldNpcs != null) {
				for (Mob npc : worldNpcs) {
					if (npc != null && npc.isActive() && npc.getLocation().getZ() == stoner.getLocation().getZ()) {
						int distance = Math.max(
							Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
							Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
						);
						if (distance <= 25) {
							npcsToCheck.add(npc);
						}
					}
				}
			}
		} else {

			if (stoner.getClient().getNpcs() != null && !stoner.getClient().getNpcs().isEmpty()) {
				npcsToCheck.addAll(stoner.getClient().getNpcs());
			} else {
				Mob[] worldNpcs = World.getNpcs();
				if (worldNpcs != null) {
					for (Mob npc : worldNpcs) {
						if (npc != null && npc.isActive() && npc.getLocation().getZ() == stoner.getLocation().getZ()) {
							int distance = Math.max(
								Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
								Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
							);
							if (distance <= 25) {
								npcsToCheck.add(npc);
							}
						}
					}
				}
			}
		}

		for (Mob npc : npcsToCheck) {
			if ((npc.getCombat().getAssaulting() == null) && (npc.getCombatDefinition() != null)) {
				boolean overrideCheck = false;

				for (short overrideId : override) {
					if ((short) npc.getId() == overrideId) {
						overrideCheck = true;
						break;
					}
				}

				if (overrideCheck && npc.inWilderness()) {
					continue;
				}

				if (npc.getOwner() == stoner) {
					continue;
				}

				if (!MobConstants.isAggressive(npc.getId())) {
					continue;
				}

				if ((npc.getLocation().getZ() == stoner.getLocation().getZ()) && (!npc.isWalkToHome())) {

					if (stoner.getController().equals(ControllerManager.GOD_WARS_CONTROLLER)) {
						GodWarsNpc npcData = GodWarsData.forId(npc.getId());
						if (npcData != null) {
							if (!npc.getCombat().inCombat()) {
								int distance = Math.max(
									Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
									Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
								);
								if (distance <= 25) {
									npc.getCombat().setAssault(stoner);
									npc.getFollowing().setFollow(stoner, Following.FollowType.COMBAT);
								}
							}
						}
						continue;
					}

					if (!npc.getCombat().inCombat() || npc.inMultiArea()) {

						int distance = Math.max(
							Math.abs(stoner.getLocation().getX() - npc.getLocation().getX()),
							Math.abs(stoner.getLocation().getY() - npc.getLocation().getY())
						);

						int aggroRange = npc.getSize() * 2 + 2;

						if (overrideCheck || (distance <= aggroRange)) {

							npc.getCombat().setAssault(stoner);
						}
					}
				}
			}
		}
	}

	public void displayCombatStatus() {
		String status = FormulaData.getCombatStatus(stoner);
		double effectiveness = FormulaData.getCombatEffectiveness(stoner);

		stoner.getClient().queueOutgoingPacket(new SendMessage(
			"@gre@Combat Status: " + status + " (Effectiveness: " +
				String.format("%.1f", effectiveness * 100) + "%)"
		));
	}

	public void resetAggression() {
		aggressionDelay = System.currentTimeMillis();
	}

	public SpecialAssault getSpecialAssault() { return specialAssault; }
	public RareDropEP getRareDropEP() { return rareDropEP; }
	public AutoCombat getAutoCombat() { return autoCombat; }

	public long getAggressionDelay() { return aggressionDelay; }
	public void setAggressionDelay(long aggressionDelay) { this.aggressionDelay = aggressionDelay; }

	public long getCurrentStunDelay() { return currentStunDelay; }
	public void setCurrentStunDelay(long delay) { currentStunDelay = delay; }

	public long getSetStunDelay() { return setStunDelay; }
	public void setSetStunDelay(long delay) { setStunDelay = delay; }

	public boolean isHitZulrah() { return hitZulrah; }
	public void setHitZulrah(boolean hitZulrah) { this.hitZulrah = hitZulrah; }
}
