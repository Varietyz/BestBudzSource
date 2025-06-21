package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.mob.ai.MobCombatHandler;
import com.bestbudz.rs2.entity.mob.ai.MobMovementController;
import com.bestbudz.rs2.entity.mob.ai.MobStateManager;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.mob.bosses.*;
import com.bestbudz.rs2.entity.mob.bosses.wild.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * Simplified 317-style mob with communication
 */
public class Mob extends Entity {

	private static final Logger logger = Logger.getLogger(Mob.class.getSimpleName());

	// Core identity
	private short npcId;
	private final boolean assaultable;
	private final boolean face;
	private final boolean noFollow;
	private final boolean lockFollow;
	private final Stoner owner;
	private byte faceDir;
	private VirtualMobRegion virtualRegion = null;

	// Component handlers
	private final MobStateManager stateManager;
	private final MobDefinitionProvider definitionProvider;
	private final MobTransformationHandler transformationHandler;
	private final MobMovementController movementController;
	private final MobCombatHandler combatHandler;

	public Mob(int npcId, boolean walks, Location location) {
		this(npcId, walks, location, null, true, false, null);
	}

	public Mob(int npcId, boolean walks, Location location, Stoner owner, boolean shouldRespawn,
			   boolean lockFollow, VirtualMobRegion virtualRegion) {

		this.npcId = (short) npcId;
		this.owner = owner;
		this.lockFollow = lockFollow;
		this.virtualRegion = virtualRegion;

		// Initialize component handlers
		this.stateManager = new MobStateManager(this);
		this.definitionProvider = new MobDefinitionProvider(this);
		this.transformationHandler = new MobTransformationHandler(this, npcId);
		this.movementController = new MobMovementController(this, location, walks);
		this.combatHandler = new MobCombatHandler(this);

		// Set initial state
		this.face = MobConstants.face(npcId);
		this.noFollow = MobConstants.noFollow(this);
		this.assaultable = getDefinition().isAssaultable();

		getLocation().setAs(location);
		setSize(getDefinition().getSize());
		setNpc(true);
		updateCombatType();

		Walking.setNpcOnTile(this, true);
		World.register(this);
		getUpdateFlags().setUpdateRequired(true);
		setActive(true);
		stateManager.setRespawnable(shouldRespawn);

		// Initialize combat stats
		initializeCombatStats();

		// Special face directions
		if (npcId == 8725) faceDir = 4;
		else if ((npcId == 553) && (location.getX() == 3091) && (location.getY() == 3497)) faceDir = 4;
		else faceDir = -1;

		setRetaliate(assaultable);

		// Add to GodWars bosses if applicable
		if (GodWarsData.forId(npcId) != null && GodWarsData.bossNpc(GodWarsData.forId(npcId))) {
			MobConstants.GODWARS_BOSSES.add(this);
		}
	}

	// Constructors
	public Mob(int npcId, boolean walks, boolean respawn, Location location) {
		this(npcId, walks, location, null, false, false, null);
	}

	public Mob(int npcId, boolean walks, Location location, VirtualMobRegion r) {
		this(npcId, walks, location, null, true, false, r);
	}

	public Mob(Stoner owner, int npcId, boolean walks, boolean shouldRespawn,
			   boolean lockFollow, Location location) {
		this(npcId, walks, location, owner, shouldRespawn, lockFollow, null);
	}

	public Mob(Stoner owner, VirtualMobRegion region, int npcId, boolean walks,
			   boolean shouldRespawn, boolean lockFollow, Location location) {
		this(npcId, walks, location, owner, shouldRespawn, lockFollow, region);
	}

	public Mob(VirtualMobRegion virtualRegion, int npcId, boolean walks,
			   boolean shouldRespawn, Location location) {
		this(npcId, walks, location, null, shouldRespawn, false, virtualRegion);
	}

	private void initializeCombatStats() {
		if (assaultable && getCombatDefinition() != null) {
			setBonuses(getCombatDefinition().getBonuses().clone());
			NpcCombatDefinition.Profession[] professions = getCombatDefinition().getProfessions();
			if (professions != null) {
				long[] profession = new long[21];

				for (int i = 0; i < professions.length; i++) {
					profession[professions[i].getId()] = professions[i].getGrade();
				}

				setGrades(profession.clone());
				setMaxGrades(profession.clone());
			}
		}
	}

	// Static methods
	public static NpcDefinition getDefinition(int id) {
		return MobDefinitionProvider.getDefinition(id);
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

	/**
	 * Simple 317-style aggro check
	 */
	private void checkAggression() {
		// Only check aggression occasionally and if not already fighting
		if (getCombat().getAssaulting() != null || Utility.randomNumber(20) != 0) {
			return;
		}

		// Only aggressive mobs attack
		if (!MobConstants.isAggressive(getId())) {
			return;
		}

		// Find closest valid target
		Stoner target = findClosestValidTarget();
		if (target != null) {
			getCombat().setAssault(target);
		}
	}

	/**
	 * Simple target finding
	 */
	private Stoner findClosestValidTarget() {
		int aggroRange = getSize() + 4; // Simple aggro range
		Stoner closest = null;
		int closestDistance = Integer.MAX_VALUE;

		for (Stoner player : World.getStoners()) {
			if (player == null || !player.isActive()) continue;
			if (player.getLocation().getZ() != getLocation().getZ()) continue;
			if (!player.getController().canAssaultNPC()) continue;
			if (!MobConstants.isAgressiveFor(this, player)) continue;

			int distance = getLocation().distanceTo(player.getLocation());
			if (distance <= aggroRange && distance < closestDistance) {
				closest = player;
				closestDistance = distance;
			}
		}

		return closest;
	}

	/**
	 * Simple main processing - 317 style
	 */
	@Override
	public void process() throws Exception {
		// Validate ownership first
		stateManager.validateOwnership();
		if (stateManager.shouldRemove()) {
			return;
		}

		// Simple aggression check for assaultable mobs
		if (isAssaultable()) {
			checkAggression();
		}

		// Process combat
		combatHandler.process();

		// Process movement
		movementController.process();
	}

	@Override
	public void reset() {
		stateManager.reset();
	}

	// Combat-related overrides
	@Override
	public void afterCombatProcess(Entity assault) {
		combatHandler.afterCombatProcess(assault);
	}

	@Override
	public boolean canAssault() {
		Entity assaulting = getCombat().getAssaulting();

		if (!stateManager.isCanAssault()) {
			return false;
		}

		if (!assaulting.isNpc()) {
			Stoner p = World.getStoners()[assaulting.getIndex()];
			return (p == null) || (p.getController().canAssaultNPC());
		}

		return true;
	}

	@Override
	public void checkForDeath() {
		stateManager.checkForDeath();
	}

	@Override
	public int getCorrectedDamage(int damage) {
		return damage;
	}

	@Override
	public int getMaxHit(CombatTypes type) {
		return combatHandler.getMaxHit(type);
	}

	@Override
	public void hit(Hit hit) {
		combatHandler.handleHit(hit);
	}

	@Override
	public boolean isIgnoreHitSuccess() {
		return false;
	}

	@Override
	public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {}

	@Override
	public void onCombatProcess(Entity assault) {
		combatHandler.onCombatProcess(assault);
	}

	@Override
	public void onHit(Entity e, Hit hit) {
		combatHandler.onHit(e, hit);
	}

	@Override
	public void updateCombatType() {
		combatHandler.updateCombatType();
	}

	@Override
	public void resetGrades() {
		if (getCombatDefinition() != null) {
			setBonuses(getCombatDefinition().getBonuses().clone());
			NpcCombatDefinition.Profession[] professions = getCombatDefinition().getProfessions();
			if (professions != null) {
				long[] profession = new long[21];

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

	// Movement-related overrides
	@Override
	public Following getFollowing() {
		return movementController.getFollowing();
	}

	@Override
	public MovementHandler getMovementHandler() {
		return movementController.getMovementHandler();
	}

	// Delegation methods to components
	public void addCombatant(Stoner p) {
		combatHandler.addCombatant(p);
	}

	public long getAffectedDamage(Hit hit) {
		return hit.getDamage();
	}

	public List<Stoner> getCombatants() {
		return combatHandler.getCombatants();
	}

	public NpcCombatDefinition getCombatDefinition() {
		return definitionProvider.getCombatDefinition();
	}

	public NpcCombatDefinition getCombatDefinition(int id) {
		return MobDefinitionProvider.getCombatDefinition(id);
	}

	public int getCombatIndex() {
		return combatHandler.getCombatIndex();
	}

	public Animation getDeathAnimation() {
		return definitionProvider.getDeathAnimation();
	}

	public NpcDefinition getDefinition() {
		return definitionProvider.getDefinition();
	}

	public int getRespawnTime() {
		return definitionProvider.getRespawnTime();
	}

	public void doAliveMobProcessing() {
		// Override in subclasses
	}

	public void doPostHitProcessing(Hit hit) {
		// Override in subclasses
	}

	public void onDeath() {
		stateManager.onDeath();
	}

	public void processMovement() {
		movementController.processMovement();
	}

	public void remove() {
		stateManager.remove();
	}

	public void retreat() {
		movementController.retreat();
	}

	public void teleport(Location p) {
		movementController.teleport(p);
	}

	public void transform(int id) {
		transformationHandler.transform(id);
	}

	public void unTransform() {
		transformationHandler.unTransform();
	}

	public boolean withinMobWalkDistance(Entity e) {
		return movementController.withinMobWalkDistance(e);
	}

	// Getters and setters
	public boolean face() { return face; }
	public int getFaceDirection() { return faceDir; }
	public void setFaceDir(int face) { faceDir = (byte) face; }
	public int getId() { return npcId; }
	public void setNpcId(short npcId) { this.npcId = npcId; }
	public Location getNextSpawnLocation() { return movementController.getNextSpawnLocation(); }
	public Stoner getOwner() { return owner; }
	public Location getSpawnLocation() { return movementController.getSpawnLocation(); }
	public int getTransformId() { return transformationHandler.getTransformId(); }
	public void setTransformId(int transformId) { transformationHandler.setTransformId(transformId); }
	public VirtualMobRegion getVirtualRegion() { return virtualRegion; }
	public void setVirtualRegion(VirtualMobRegion virtualRegion) { this.virtualRegion = virtualRegion; }
	public boolean inVirtualRegion() { return virtualRegion != null; }
	public boolean isAssaultable() { return assaultable; }
	public boolean isLockFollow() { return (owner != null) && (lockFollow); }
	public boolean isMovedLastCycle() { return stateManager.isMovedLastCycle(); }
	public boolean isNoFollow() { return noFollow; }
	public boolean isPlacement() { return stateManager.isPlacement(); }
	public void setPlacement(boolean placement) { stateManager.setPlacement(placement); }
	public boolean isTransformUpdate() { return transformationHandler.isTransformUpdate(); }
	public void setTransformUpdate(boolean transformUpdate) { transformationHandler.setTransformUpdate(transformUpdate); }
	public boolean isVisible() { return stateManager.isVisible(); }
	public void setVisible(boolean isVisible) { stateManager.setVisible(isVisible); }
	public boolean isWalkToHome() { return movementController.isWalkToHome(); }
	public void setForceWalking(boolean walkingHome) { movementController.setForceWalking(walkingHome); }
	public void setRespawnable(boolean state) { stateManager.setRespawnable(state); }
	public boolean shouldRespawn() { return stateManager.shouldRespawn(); }
	public boolean isCanAssault() { return stateManager.isCanAssault(); }
	public void setCanAssault(boolean canAssault) { stateManager.setCanAssault(canAssault); }

	// Component getters for internal use
	public MobStateManager getStateManager() { return stateManager; }
	public MobDefinitionProvider getDefinitionProvider() { return definitionProvider; }
	public MobTransformationHandler getTransformationHandler() { return transformationHandler; }
	public MobMovementController getMovementController() { return movementController; }
	public MobCombatHandler getCombatHandler() { return combatHandler; }

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "Mob [spawnLocation=" + getSpawnLocation() + ", npcId=" + npcId +
			", assaultable=" + assaultable + ", owner=" + owner + "]";
	}
}