package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.definitions.NpcCombatDefinition.Mage;
import com.bestbudz.core.definitions.NpcCombatDefinition.Melee;
import com.bestbudz.core.definitions.NpcCombatDefinition.Sagittarius;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.MobDeathTask;
import com.bestbudz.core.task.impl.MobWalkTask;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.CombatConstants;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.minigames.barrows.Barrows.Brother;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.GodWarsNpc;
import com.bestbudz.rs2.content.minigames.warriorsguild.ArmourAnimator;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.content.sounds.MobSounds;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.following.MobFollowing;
import com.bestbudz.rs2.entity.mob.impl.CorporealBeast;
import com.bestbudz.rs2.entity.mob.impl.GiantMole;
import com.bestbudz.rs2.entity.mob.impl.KalphiteQueen;
import com.bestbudz.rs2.entity.mob.impl.Kraken;
import com.bestbudz.rs2.entity.mob.impl.Kreearra;
import com.bestbudz.rs2.entity.mob.impl.SeaTrollQueen;
import com.bestbudz.rs2.entity.mob.impl.Tentacles;
import com.bestbudz.rs2.entity.mob.impl.wild.Callisto;
import com.bestbudz.rs2.entity.mob.impl.wild.ChaosElemental;
import com.bestbudz.rs2.entity.mob.impl.wild.ChaosFanatic;
import com.bestbudz.rs2.entity.mob.impl.wild.CrazyArchaeologist;
import com.bestbudz.rs2.entity.mob.impl.wild.Scorpia;
import com.bestbudz.rs2.entity.mob.impl.wild.Vetion;
import com.bestbudz.rs2.entity.movement.MobMovementHandler;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Mob extends Entity {

	private static final Logger logger = Logger.getLogger(Mob.class.getSimpleName());
	private final MovementHandler movementHandler = new MobMovementHandler(this);
	private final MobFollowing following = new MobFollowing(this);
	private final Location spawnLocation;
	private final boolean walks;
	private final short originalNpcId;
	private final boolean assaultable;
	private final boolean face;
	private final boolean noFollow;
	private final boolean lockFollow;
	private final Stoner owner;
	private boolean pet;
	private short npcId;
	private short transformId = -1;
	private boolean visible = true;
	private boolean transformUpdate = false;
	private boolean forceWalking = false;
	private boolean placement = false;
	private byte combatIndex = 0;
	private boolean shouldRespawn = true;
	private byte faceDir;
	private VirtualMobRegion virtualRegion = null;
	private List<Stoner> combatants;
	private boolean assaulted = false;
	private boolean movedLastCycle = false;
	private boolean canAssault = true;
	public Mob(int npcId, boolean walks, Location location) {
	this(npcId, walks, location, null, true, false, null);
	}

	public Mob(int npcId, boolean walks, Location location, Stoner owner, boolean shouldRespawn, boolean lockFollow, VirtualMobRegion virtualRegion) {
	originalNpcId = ((short) npcId);
	this.npcId = ((short) npcId);
	this.walks = walks;
	this.virtualRegion = virtualRegion;
	this.owner = owner;
	this.lockFollow = lockFollow;
	this.shouldRespawn = shouldRespawn;
	face = MobConstants.face(npcId);
	noFollow = MobConstants.noFollow(this);

	getLocation().setAs(location);
	spawnLocation = new Location(location);

	setSize(getDefinition().getSize());

	movementHandler.resetMoveDirections();

	setNpc(true);

	updateCombatType();

	Walking.setNpcOnTile(this, true);
	World.register(this);

	getUpdateFlags().setUpdateRequired(true);

	assaultable = getDefinition().isAssaultable();

	setActive(true);

	if (assaultable) {
		if (getCombatDefinition() != null) {
			setBonuses(getCombatDefinition().getBonuses().clone());
			NpcCombatDefinition.Profession[] professions = getCombatDefinition().getProfessions();
			if (professions != null) {
				long[] profession = new long[22];

				for (int i = 0; i < professions.length; i++) {
					profession[professions[i].getId()] = professions[i].getGrade();
				}

				setGrades(profession.clone());
				setMaxGrades(profession.clone());
			}

		}

		if (inMultiArea())
			combatants = new ArrayList<Stoner>();
		else
			combatants = null;
	} else {
		combatants = null;
	}

	if (npcId == 8725)
		faceDir = 4;
	else if ((npcId == 553) && (location.getX() == 3091) && (location.getY() == 3497))
		faceDir = 4;
	else
		faceDir = -1;

	setRetaliate(assaultable);

	if (GodWarsData.forId(npcId) != null && GodWarsData.bossNpc(GodWarsData.forId(npcId))) {
		MobConstants.GODWARS_BOSSES.add(this);
	}
	}

	public Mob(int npcId, boolean walks, boolean respawn, Location location) {
	this(npcId, walks, location, null, false, false, null);
	}

	public Mob(int npcId, boolean walks, Location location, VirtualMobRegion r) {
	this(npcId, walks, location, null, true, false, r);
	}

	public Mob(Stoner owner, int npcId, boolean walks, boolean shouldRespawn, boolean lockFollow, Location location) {
	this(npcId, walks, location, owner, shouldRespawn, lockFollow, null);
	}

	public Mob(Stoner owner, VirtualMobRegion region, int npcId, boolean walks, boolean shouldRespawn, boolean lockFollow, Location location) {
	this(npcId, walks, location, owner, shouldRespawn, lockFollow, region);
	}

	public Mob(VirtualMobRegion virtualRegion, int npcId, boolean walks, boolean shouldRespawn, Location location) {
	this(npcId, walks, location, null, shouldRespawn, false, virtualRegion);
	}

	public static NpcDefinition getDefinition(int id) {
	return GameDefinitionLoader.getNpcDefinition(id);
	}

	public static void spawnBosses() {
	new CorporealBeast();
	new SeaTrollQueen();
	new Kreearra();
	new KalphiteQueen();
	new GiantMole();
	new ChaosElemental();
	new Callisto();
	new Scorpia();
	new Vetion();
	new ChaosFanatic();
	new CrazyArchaeologist();
	logger.info("All MOB bosses have been spawned.");
	}

	public boolean isPet() {
		return pet;
	}

	public void setPet(boolean pet) {
		this.pet = pet;
		if (pet) {
			this.getGrades()[3] = 420000;
			this.getMaxGrades()[3] = 420000;
		}
	}

	public void addCombatant(Stoner p) {
	if (combatants == null) {
		combatants = new ArrayList<Stoner>();
	}

	if (!combatants.contains(p))
		combatants.add(p);
	}

	@Override
	public void afterCombatProcess(Entity assault) {
	if (assault.isDead()) {
		getCombat().reset();
	} else {
		MobAbilities.executeAbility(npcId, this, assault);
	}
	}

	@Override
	public boolean canAssault() {
	Entity assaulting = getCombat().getAssaulting();
	if (isPet()) return false;

	if (!isCanAssault()) {
		return false;
	}

	if ((!inMultiArea()) || (!assaulting.inMultiArea())) {
		if ((getCombat().inCombat()) && (getCombat().getLastAssaultedBy() != getCombat().getAssaulting())) {
			return false;
		}

		if ((assaulting.getCombat().inCombat()) && (assaulting.getCombat().getLastAssaultedBy() != this)) {
			return false;
		}
	}

	if (!assaulting.isNpc()) {
		Stoner p = World.getStoners()[assaulting.getIndex()];

		return (p == null) || (p.getController().canAssaultNPC());

	}

	return true;
	}

	@Override
	public void checkForDeath() {
		if (getGrades()[3] <= 0) {
			if (isPet()) {
				getGrades()[3] = 1; // 💡 Keep them alive
				return;
			}
			TaskQueue.queue(new MobDeathTask(this));


			if (combatants != null)
			combatants.clear();
	}
	}

	@Override
	public int getCorrectedDamage(int damage) {
	return damage;
	}

	@Override
	public int getMaxHit(CombatTypes type) {
	if (getCombatDefinition() == null) {
		return 1;
	}

		switch (type) {
			case NONE:
				break;

			case MAGE:
				if (getCombatDefinition().getMage() != null) {
					int base = getCombatDefinition().getMage()[combatIndex].getMax();
					int bonus = (int) (base * getCombat().getHitChainBonus() / 100);

					return base + bonus;
				}
				break;

			case MELEE:
				if (getCombatDefinition().getMelee() != null) {
					int base = getCombatDefinition().getMelee()[combatIndex].getMax();

					if (npcId == 1673) {
						base = (int)(base * (1.0D + (2.0D - getGrades()[3] / getMaxGrades()[3])));
					}

					int bonus = (int) (base * getCombat().getHitChainBonus() / 100);
					return base + bonus;
				}
				break;

			case SAGITTARIUS:
				if (getCombatDefinition().getSagittarius() != null) {
					int base = getCombatDefinition().getSagittarius()[combatIndex].getMax();
					int bonus = (int) (base * getCombat().getHitChainBonus() / 100);
					return base + bonus;
				}
				break;
		}

		return 1;

	}

	@Override
	public void hit(Hit hit) {
	if (!canTakeDamage()) {
		return;
	}

	if (isDead())
		hit.setDamage(0);
	else {
		hit.setDamage(getAffectedDamage(hit));
	}

	if (npcId == 493 && getOwner().inKraken()) {
		if (hit.getDamage() != 0) {
			hit.setDamage(0);
			remove();
			new Tentacles(getOwner(), new Location(getX(), getY(), getOwner().getZ()));
			getOwner().whirlpoolsHit++;
		}
	}

	if (npcId == 496 && getOwner().inKraken()) {
		if (getOwner().whirlpoolsHit != 4) {
			getOwner().hit(new Hit(Utility.random(10)));
			getOwner().send(new SendMessage("@dre@You need to assault all 4 whirlpools before doing this! Remaining: " + (4 - getOwner().whirlpoolsHit)));
			return;
		}
		if (hit.getDamage() != 0) {
			hit.setDamage(0);
			remove();
			new Kraken(getOwner(), new Location(3695, 5811, getOwner().getZ()));
		}
	}

	if ((npcId == 2883) && ((hit.getType() == Hit.HitTypes.MELEE) || (hit.getType() == Hit.HitTypes.SAGITTARIUS))) {
		hit.setDamage(0);
	}

	if ((npcId == 2881) && ((hit.getType() == Hit.HitTypes.MAGE) || (hit.getType() == Hit.HitTypes.SAGITTARIUS))) {
		hit.setDamage(0);
	}

	if ((npcId == 2882) && ((hit.getType() == Hit.HitTypes.MAGE) || (hit.getType() == Hit.HitTypes.MELEE))) {
		hit.setDamage(hit.getDamage() / 10);
	}

		if (isPet() && getGrades()[3] - hit.getDamage() < 1) {
			hit.setDamage(getGrades()[3] - 1);
		}

		getGrades()[3] = ((short) (getGrades()[3] - hit.getDamage()));

	if (!getUpdateFlags().isHitUpdate())
		getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
	else {
		getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
	}

	if (hit.getAssaulter() != null) {
		getCombat().getDamageTracker().addDamage(hit.getAssaulter(), hit.getDamage());

		if (getCombat().getAssaulting() == null && isRetaliate() || !inMultiArea() && isRetaliate()) {
			getCombat().setAssault(hit.getAssaulter());
		}

		if (inMultiArea() && (assaultable) && isRetaliate()) {
			if (((assaulted) && (hit.getAssaulter() != getCombat().getAssaulting())) || ((!assaulted) && (!movedLastCycle) && (!getCombat().withinDistanceForAssault(getCombat().getCombatType(), true)))) {
				getCombat().setAssault(hit.getAssaulter());
				assaulted = false;
			}

			if (!hit.getAssaulter().isNpc()) {
				Stoner p = World.getStoners()[hit.getAssaulter().getIndex()];
				if (p != null) {
					MobSounds.sendBlockSound(p, npcId);

					addCombatant(p);
				}
			}
		} else if ((!isDead()) && (!hit.getAssaulter().isNpc())) {
			Stoner p = World.getStoners()[hit.getAssaulter().getIndex()];
			if (p != null) {
				MobSounds.sendBlockSound(p, npcId);
			}
		}

		doPostHitProcessing(hit);
	}

	if (!isDead()) {
		checkForDeath();
	}

	if (hit.getAssaulter() != null)
		hit.getAssaulter().onHit(this, hit);
	}

	@Override
	public boolean isIgnoreHitSuccess() {
	return false;
	}

	@Override
	public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {
	}

	@Override
	public void onCombatProcess(Entity assault) {
	if ((npcId == 8133) && (getCombat().getCombatType() == CombatTypes.MELEE) && (combatIndex == 0)) {
		getUpdateFlags().sendGraphic(new Graphic(1834, 0, false));
	}

	assaulted = true;

	if ((inMultiArea()) && (!assault.isNpc())) {
		Stoner p = World.getStoners()[assault.getIndex()];

		if (p != null) {
			MobSounds.sendBlockSound(p, npcId);
			addCombatant(p);
		}
	} else if (!assault.isNpc()) {
		Stoner p = World.getStoners()[assault.getIndex()];

		if (p != null)
			MobSounds.sendAssaultSound(p, npcId, getCombat().getCombatType(), getLastDamageDealt() > 0);
	}
	}

	@Override
	public void onHit(Entity e, Hit hit) {
	if ((e.isDead()) && (inMultiArea()) && (!e.isNpc())) {
		if (combatants == null) {
			combatants = new ArrayList<Stoner>();
		}

		combatants.remove(World.getStoners()[e.getIndex()]);
	}
	}

	@Override
	public void updateCombatType() {
	NpcCombatDefinition def = getCombatDefinition();

	if (def == null) {
		return;
	}

	CombatTypes combatType = CombatTypes.MELEE;

	switch (def.getCombatType()) {
	case MAGE:
		combatType = CombatTypes.MAGE;
		break;
	case MELEE_AND_MAGE:
		if (!getCombat().withinDistanceForAssault(CombatTypes.MELEE, true) || Utility.randomNumber(2) == 1) {
			combatType = CombatTypes.MAGE;
		} else {
			combatType = CombatTypes.MELEE;
		}
		break;
	case MELEE_AND_SAGITTARIUS:
		if (!getCombat().withinDistanceForAssault(CombatTypes.MELEE, true) || Utility.randomNumber(2) == 1) {
			combatType = CombatTypes.SAGITTARIUS;
		} else {
			combatType = CombatTypes.MELEE;
		}
		break;
	case SAGITTARIUS:
		combatType = CombatTypes.SAGITTARIUS;
		break;
	case SAGITTARIUS_AND_MAGE:
		if (!getCombat().withinDistanceForAssault(CombatTypes.SAGITTARIUS, true) || Utility.randomNumber(2) == 1) {
			combatType = CombatTypes.MAGE;
		} else {
			combatType = CombatTypes.SAGITTARIUS;
		}
		break;
	case ALL:
		if (!getCombat().withinDistanceForAssault(CombatTypes.MELEE, true)) {
			int roll = Utility.randomNumber(2);
			if (getCombat().withinDistanceForAssault(CombatTypes.SAGITTARIUS, true) && roll == 0)
				combatType = CombatTypes.SAGITTARIUS;
			else
				combatType = CombatTypes.MAGE;
			break;
		}

		int roll = Utility.randomNumber(3);

		if (roll == 0) {
			combatType = CombatTypes.MAGE;
		} else if (roll == 1) {
			combatType = CombatTypes.SAGITTARIUS;
		} else if (roll == 2) {
			combatType = CombatTypes.MELEE;
		}
		break;
	default:
		break;
	}

	getCombat().setCombatType(combatType);
	getCombat().setBlockAnimation(def.getBlock());

	switch (combatType) {
	case NONE:
		break;
	case MELEE:
		if (def.getMelee() == null || def.getMelee().length < 1) {
			remove();
			System.out.println("Null combat def error:melee for npc: " + npcId);
			return;
		}

		combatIndex = (byte) Utility.randomNumber(def.getMelee().length);
		Melee melee = def.getMelee()[combatIndex];
		getCombat().getMelee().setAssault(melee.getAssault(), melee.getAnimation());
		break;
	case MAGE:
		if (def.getMage() == null || def.getMage().length < 1) {
			remove();
			System.out.println("Null combat def error:mage for npc: " + npcId);
			return;
		}

		combatIndex = (byte) Utility.randomNumber(def.getMage().length);
		Mage mage = def.getMage()[combatIndex];
		getCombat().getMage().setAssault(mage.getAssault(), mage.getAnimation(), mage.getStart(), mage.getEnd(), mage.getProjectile());
		break;
	case SAGITTARIUS:
		if (def.getSagittarius() == null || def.getSagittarius().length < 1) {
			remove();
			System.out.println("Null combat def error:sagittarius for npc: " + npcId);
			return;
		}

		combatIndex = (byte) Utility.randomNumber(def.getSagittarius().length);
		Sagittarius sagittarius = def.getSagittarius()[combatIndex];
		getCombat().getSagittarius().setAssault(sagittarius.getAssault(), sagittarius.getAnimation(), sagittarius.getStart(), sagittarius.getEnd(), sagittarius.getProjectile());
		break;
	}
	}

	public void doAliveMobProcessing() {

	}

	public void doPostHitProcessing(Hit hit) {
	}

	@Override
	public boolean equals(Object o) {
	return super.equals(o);
	}

	@Override
	public Following getFollowing() {
	return following;
	}

	@Override
	public MovementHandler getMovementHandler() {
	return movementHandler;
	}

	@Override
	public void process() throws Exception {
	if ((owner != null) && (!owner.isActive() || !owner.withinRegion(getLocation()))) {
		if (!owner.inZulrah() && !isDead()) {
			remove();
			if (getId() == 1778) {
				owner.getAttributes().set("KILL_AGENT", Boolean.FALSE);
			}
			return;
		}
	}

	if ((assaultable) || ((this instanceof FamiliarMob))) {
		if (forceWalking)
			return;
		if (isDead()) {
			getCombat().reset();
			return;
		}

		if (inMultiArea()) {
			if (combatants == null) {
				combatants = new ArrayList<Stoner>();
			}

			if (combatants.size() > 0) {
				if (getCombat().getAssaulting() == null)
					combatants.clear();
				else {
					for (Iterator<Stoner> i = combatants.iterator(); i.hasNext();) {
						Stoner p = i.next();
						if ((!p.getLocation().isViewableFrom(getLocation())) || (!p.getCombat().inCombat()) || (p.isDead())) {
							i.remove();
						}
					}
				}
			}
		}

		doAliveMobProcessing();

		if ((getCombat().getAssaultTimer() <= 1) && (getCombat().getAssaulting() != null)) {
			updateCombatType();
		}

		if (isWalkToHome()) {
			getCombat().reset();
			getFollowing().reset();
			TaskQueue.queue(new MobWalkTask(this, spawnLocation, true));
		} else if ((!isDead()) && (getCombat().getAssaulting() == null) && (!getFollowing().isFollowing()) && (walks) && (!forceWalking)) {
			RandomMobChatting.handleRandomMobChatting(this);
			Walking.randomWalk(this);

			GodWarsNpc npc = GodWarsData.forId(getId());

			if (npc != null && !getCombat().inCombat()) {

				for (Mob i : World.getNpcs()) {

					if (i == null) {
						continue;
					}

					GodWarsNpc other = GodWarsData.forId(i.getId());

					if (npc == null || other == null) {
						continue;
					}

					if ((i.getCombat().getAssaulting() == null) && (i.getCombatDefinition() != null)) {

						if (npc.getAllegiance() != other.getAllegiance() && !i.getCombat().inCombat()) {

							if (Math.abs(getX() - i.getX()) + Math.abs(getY() - i.getY()) <= 4 + CombatConstants.getDistanceForCombatType(i.getCombat().getCombatType())) {
								i.getCombat().setAssault(this);
							}
						}
					}
				}
			}
		}
	}

	if ((!forceWalking) && (!isDead())) {
		following.process();
		getCombat().process();
	} else if ((!isDead()) && (!assaultable) && (walks) && (!following.isFollowing()) && (!forceWalking)) {
		Walking.randomWalk(this);
	} else if (!forceWalking) {
		following.process();
	}
	}

	@Override
	public void reset() {
	movedLastCycle = (getMovementHandler().getPrimaryDirection() != -1);
	getMovementHandler().resetMoveDirections();
	getFollowing().updateWaypoint();
	getUpdateFlags().reset();
	placement = false;
	}

	@Override
	public void resetGrades() {
	if (getCombatDefinition() != null) {
		setBonuses(getCombatDefinition().getBonuses().clone());
		NpcCombatDefinition.Profession[] professions = getCombatDefinition().getProfessions();
		if (professions != null) {
			long[] profession = new long[25];

			for (int i = 0; i < professions.length; i++) {
				profession[professions[i].getId()] = professions[i].getGrade();
			}

			setGrades(profession.clone());
			setMaxGrades(profession.clone());
		}

	}
	}

	@Override
	public void retaliate(Entity assaulted) {
	if (!getCombat().inCombat()) {
		getCombat().setAssault(assaulted);
	}
	}

	public boolean face() {
	return face;
	}

	public long getAffectedDamage(Hit hit) {
	return hit.getDamage();
	}

	public List<Stoner> getCombatants() {
	if (combatants == null) {
		combatants = new ArrayList<Stoner>();
	}

	return combatants;
	}

	public NpcCombatDefinition getCombatDefinition() {
	return GameDefinitionLoader.getNpcCombatDefinition(npcId);
	}

	public NpcCombatDefinition getCombatDefinition(int id) {
	return GameDefinitionLoader.getNpcCombatDefinition(id);
	}

	public int getCombatIndex() {
	return combatIndex;
	}

	public Animation getDeathAnimation() {
	return getCombatDefinition() != null ? getCombatDefinition().getDeath() : new Animation(0, 0);
	}

	public NpcDefinition getDefinition() {
	return GameDefinitionLoader.getNpcDefinition(npcId);
	}

	public int getFaceDirection() {
	return faceDir;
	}

	public int getId() {
	return npcId;
	}

	public Location getNextSpawnLocation() {
	return spawnLocation;
	}

	public Stoner getOwner() {
	return owner;
	}

	public int getRespawnTime() {
	if (getCombatDefinition() != null) {
		return getCombatDefinition().getRespawnTime();
	}
	return 50;
	}

	public Location getSpawnLocation() {
	return spawnLocation;
	}

	public int getTransformId() {
	return transformId;
	}

	public void setTransformId(int transformId) {
	this.transformId = ((short) transformId);
	}

	public VirtualMobRegion getVirtualRegion() {
	return virtualRegion;
	}

	public boolean inVirtualRegion() {
	return virtualRegion != null;
	}

	public boolean isLockFollow() {
	return (owner != null) && (lockFollow);
	}

	public boolean isMovedLastCycle() {
	return movedLastCycle;
	}

	public boolean isNoFollow() {
	return noFollow;
	}

	public boolean isPlacement() {
	return placement;
	}

	public void setPlacement(boolean placement) {
	this.placement = placement;
	}

	public boolean isTransformUpdate() {
	return transformUpdate;
	}

	public void setTransformUpdate(boolean transformUpdate) {
	this.transformUpdate = transformUpdate;
	}

	public boolean isVisible() {
	return visible;
	}

	public void setVisible(boolean isVisible) {
	visible = isVisible;
	}

	public boolean isWalkToHome() {
	if (GodWarsData.forId(npcId) != null && GodWarsData.bossNpc(GodWarsData.forId(npcId))) {
		return false;
	}

	if (inWilderness()) {
		return Math.abs(getLocation().getX() - spawnLocation.getX()) + Math.abs(getLocation().getY() - spawnLocation.getY()) > getSize() + 2;
	}

	if ((getFollowing().isIgnoreDistance()) || (owner != null)) {
		return false;
	}

	if (assaultable) {
		return Math.abs(getLocation().getX() - spawnLocation.getX()) + Math.abs(getLocation().getY() - spawnLocation.getY()) > getSize() * 2 + 6;
	}
	return Utility.getManhattanDistance(spawnLocation, getLocation()) > 2;
	}

	public void onDeath() {
	}

	public void processMovement() {
	}

	public void remove() {
	if ((Brother.isBarrowsBrother(this)) && (owner != null)) {
		owner.getCombat().resetCombatTimer();
	}

	if ((ArmourAnimator.isAnimatedArmour(npcId)) && (owner != null)) {
		owner.getAttributes().remove("warriorGuildAnimator");
	}

	visible = false;
	setActive(false);
	World.unregister(this);
	Walking.setNpcOnTile(this, false);

	if (virtualRegion != null) {
		virtualRegion = null;
	}

	MobConstants.GODWARS_BOSSES.remove(this);
	}

	public void retreat() {
	if (getCombat().getAssaulting() != null) {
		forceWalking = true;
		getCombat().reset();
		TaskQueue.queue(new MobWalkTask(this, new Location(getX() + 5, getY() + 5), false));
	}
	}

	public void setFaceDir(int face) {
	faceDir = ((byte) face);
	}

	public void setForceWalking(boolean walkingHome) {
	forceWalking = walkingHome;
	}

	public void setRespawnable(boolean state) {
	shouldRespawn = state;
	}

	public boolean shouldRespawn() {
	return shouldRespawn;
	}

	public void teleport(Location p) {
	Walking.setNpcOnTile(this, false);
	getMovementHandler().getLastLocation().setAs(new Location(p.getX(), p.getY() + 1));
	getLocation().setAs(p);
	Walking.setNpcOnTile(this, true);
	placement = true;
	getMovementHandler().resetMoveDirections();
	}

	public void transform(int id) {
	transformUpdate = true;
	transformId = ((short) id);
	npcId = ((short) id);
	updateCombatType();
	getUpdateFlags().setUpdateRequired(true);
	if (assaultable) {
		if (getCombatDefinition() != null) {
			setBonuses(getCombatDefinition().getBonuses().clone());
			NpcCombatDefinition.Profession[] professions = getCombatDefinition().getProfessions();
			if (professions != null) {
				long[] profession = new long[Professions.PROFESSION_COUNT];
				long[] professionMax = new long[Professions.PROFESSION_COUNT];

				for (int i = 0; i < professions.length; i++) {
					if (i == 3) {
						profession[3] = getGrades()[3];
						professionMax[3] = getMaxGrades()[3];
						continue;
					}
					profession[professions[i].getId()] = professions[i].getGrade();
					professionMax[professions[i].getId()] = professions[i].getGrade();
				}

				setGrades(profession.clone());
				setMaxGrades(professionMax.clone());
			}

		}
	}
	}

	public void unTransform() {
	if (originalNpcId != npcId)
		transform(npcId);
	}

	public boolean withinMobWalkDistance(Entity e) {
	if ((following.isIgnoreDistance()) || (owner != null)) {
		return true;
	}

	return Math.abs(e.getLocation().getX() - spawnLocation.getX()) + Math.abs(e.getLocation().getY() - spawnLocation.getY()) < getSize() * 2 + 6;
	}

	@Override
	public String toString() {
	return "Mob [spawnLocation=" + spawnLocation + ", npcId=" + npcId + ", assaultable=" + assaultable + ", owner=" + owner + "]";
	}

	public boolean isCanAssault() {
	return canAssault;
	}

	public void setCanAssault(boolean canAssault) {
	this.canAssault = canAssault;
	}

}