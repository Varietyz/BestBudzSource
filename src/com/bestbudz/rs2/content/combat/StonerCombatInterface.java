package com.bestbudz.rs2.content.combat;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.StonerDeathTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.formula.MageFormulas;
import com.bestbudz.rs2.content.combat.formula.MeleeFormulas;
import com.bestbudz.rs2.content.combat.formula.RangeFormulas;
import com.bestbudz.rs2.content.combat.impl.PoisonWeapons;
import com.bestbudz.rs2.content.combat.impl.RingOfRecoil;
import com.bestbudz.rs2.content.combat.special.SpecialAssaultHandler;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlGame;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.mage.MageEffects;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.BarrowsSpecials;
import com.bestbudz.rs2.content.profession.mercenary.MercenaryMonsters;
import com.bestbudz.rs2.content.profession.sagittarius.BoltSpecials;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.sounds.StonerSounds;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.pets.PetCombatHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class StonerCombatInterface implements CombatInterface {

  private final Stoner stoner;

  public StonerCombatInterface(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public void afterCombatProcess(Entity entity) {
    if (stoner.getSpecialAssault().isInitialized()
        && stoner.getCombat().getCombatType() != CombatTypes.MAGE) {
      SpecialAssaultHandler.executeSpecialEffect(stoner, entity);
      stoner.getSpecialAssault().afterSpecial();
    }

    if (!stoner.getMage().isDFireShieldEffect()) {
      stoner.getMage().getSpellCasting().resetOnAssault();
    }

    stoner.getMelee().afterCombat();

    stoner.updateCombatType();
  }

  @Override
  public boolean canAssault() {
	  if (stoner.isPetStoner()) {
		  Entity target = stoner.getCombat().getAssaulting();
		  return target != null && !target.isDead() && target.isActive() &&
			  Boolean.TRUE.equals(stoner.getAttributes().get("PET_COMBAT_READY"));
	  }

    if (!stoner.getController().canUseCombatType(stoner, stoner.getCombat().getCombatType())) {
      return false;
    }

    Entity assaulting = stoner.getCombat().getAssaulting();
    CombatTypes type = stoner.getCombat().getCombatType();

    if (assaulting.getIndex() <= -1) {
      return false;
    }

    if (assaulting.isNpc()) {
      Mob mob = World.getNpcs()[assaulting.getIndex()];
      if (mob == null) {
        return false;
      }
      if (MobConstants.MERCENARY_REQUIREMENTS.get(mob.getId()) != null) {
        int requirement = MobConstants.MERCENARY_REQUIREMENTS.get(mob.getId());
        if (stoner.getProfession().getGrades()[Professions.MERCENARY] < requirement) {
          stoner.send(
              new SendMessage(
                  "You need a mercenary grade of " + requirement + " to assault this monster!"));
          return false;
        }
      }
    }

    if (stoner.getSpecialAssault().isInitialized()
        && stoner.getCombat().getCombatType() != CombatTypes.MAGE
        && (!stoner.getController().canUseSpecialAssault(stoner)
            || !SpecialAssaultHandler.hasSpecialAmount(stoner))) {
      stoner.getSpecialAssault().toggleSpecial();
      stoner.getCombat().setAssaultTimer(2);
      return false;
    }

    if (TridentOfTheSeas.hasTrident(stoner)) {
      if (stoner.getSeasTrident().getCharges() == 0) {
        stoner.send(new SendMessage("You have no charges to use this!"));
      } else {
        stoner.getMage().getSpellCasting().enableAutocast(9999);
      }
    }

    if (TridentOfTheSwamp.hasTrident(stoner)) {
      if (stoner.getSwampTrident().getCharges() == 0) {
        stoner.send(new SendMessage("You have no charges to use this!"));
      } else {
        stoner.getMage().getSpellCasting().enableAutocast(9998);
      }
    }

    if (type == CombatTypes.MAGE && !stoner.getMage().getSpellCasting().canCast()) return false;
    if (type == CombatTypes.SAGITTARIUS && !stoner.getSagittarius().canUseSagittarius()) {
      return false;
    }

    if (type == CombatTypes.MELEE && assaulting.isNpc()) {
      Mob mob = World.getNpcs()[assaulting.getIndex()];
      if (mob != null) {
        for (int i : MobConstants.FLYING_MOBS) {
          if (mob.getId() == i) {
            stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot reach this npc!"));
            return false;
          }
        }
      }
    }

    if (!assaulting.isNpc()) {
      Stoner other = World.getStoners()[assaulting.getIndex()];

      return other == null || stoner.getController().canAssaultStoner(stoner, other);
    } else {
      Mob mob = World.getNpcs()[assaulting.getIndex()];

      if (mob != null) {

        if (!stoner.getController().canAssaultNPC()) {
          stoner.getClient().queueOutgoingPacket(new SendMessage("You can't assault NPCs here."));
          return false;
        }
        if (!MercenaryMonsters.canAssaultMob(stoner, mob)) return false;

        if (!mob.getDefinition().isAssaultable()
            || (mob.getOwner() != null && !mob.getOwner().equals(stoner))) {
          stoner.getClient().queueOutgoingPacket(new SendMessage("You can't assault this NPC."));
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public void checkForDeath() {
    if (stoner.getGrades()[3] <= 0 && !stoner.isDead()) {

      TaskQueue.queue(new StonerDeathTask(stoner));
    }
  }

  @Override
  public int getCorrectedDamage(int damage) {
    Item weapon = stoner.getEquipment().getItems()[3];
    Item ammo = stoner.getEquipment().getItems()[13];

    if (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS) {
      if (stoner.getSpecialAssault().isInitialized()) {
        if ((weapon != null) && (weapon.getId() == 11235)) {

          if (ammo != null) {
            if (ammo.getId() == 11212
                || ammo.getId() == 11227
                || ammo.getId() == 0
                || ammo.getId() == 11228) {
              if (damage < 8) {
                return 8;
              }
            } else if (damage < 5) {
              return 5;
            }
          }
        }
      }
    } else if (stoner.getCombat().getCombatType() == CombatTypes.MAGE
        && ItemCheck.hasDFireShield(stoner)
        && stoner.getMage().isDFireShieldEffect()
        && damage < 15) {
      return 15;
    }

    return damage;
  }

  @Override
  public int getMaxHit(CombatTypes type) {
	  if (stoner.isPetStoner()) {
		  return PetCombatHandler.getPetMaxHit(stoner, type);
	  }

    switch (type) {
      case MAGE:
        return (MageFormulas.mageMaxHit(stoner));
      case MELEE:
        return (int) (MeleeFormulas.calculateBaseDamage(stoner));
      case SAGITTARIUS:
        return (RangeFormulas.getSagittariusMaxHit(stoner));
      case NONE:
        return 0;
    }
    return (int) (MeleeFormulas.calculateBaseDamage(stoner));
  }

  @Override
  public void hit(Hit hit) {
    if (!stoner.canTakeDamage()
        || stoner.isImmuneToHit()
        || stoner.getMage().isTeleporting() && !stoner.getController().isSafe(stoner)) {
      return;
    }

    if (stoner.isStunned()) {
      return;
    }

    if (stoner.isDead()) {
      hit.setDamage(0);
    }

    if (hit.getAssaulter() != null) {
      if (hit.getAssaulter().isNpc()) {
        Mob mob = World.getNpcs()[hit.getAssaulter().getIndex()];
        if (MobConstants.isDragon(mob)) {
          if (ItemCheck.isWearingAntiDFireShield(stoner) && (hit.getType() == Hit.HitTypes.MAGE)) {
            if (ItemCheck.hasDFireShield(stoner)) {
              stoner.getMage().incrDragonFireShieldCharges(mob);
            }
            if (stoner.hasFireImmunity()) {
              stoner
                  .getClient()
                  .queueOutgoingPacket(new SendMessage("You resist all of the dragonfire."));
              hit.setDamage(0);
            } else {
              stoner
                  .getClient()
                  .queueOutgoingPacket(
                      new SendMessage("You manage to resist some of the dragonfire."));
              hit.setDamage((int) (hit.getDamage() * 0.3));
            }
          } else if (hit.getType() == Hit.HitTypes.MAGE && stoner.hasSuperFireImmunity()) {
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("You reset all of the dragonfire."));
            hit.setDamage(0);
          } else if (hit.getType() == Hit.HitTypes.MAGE && stoner.hasFireImmunity()) {
            stoner
                .getClient()
                .queueOutgoingPacket(
                    new SendMessage("You manage to resist some of the dragonfire."));
            hit.setDamage((int) (hit.getDamage() * 0.5));
          } else if ((hit.getType() == Hit.HitTypes.MAGE)) {
            stoner
                .getClient()
                .queueOutgoingPacket(new SendMessage("You are horribly burned by the dragonfire."));
          }
        }
      } else {
        Stoner p = World.getStoners()[hit.getAssaulter().getIndex()];

        if (p != null && !stoner.getController().canAssaultStoner(p, stoner)
            || !stoner.getController().canAssaultStoner(stoner, p)) {
          return;
        }
      }
    }

    hit.setDamage(stoner.getEquipment().getEffectedDamage(hit.getDamage()));

    if (hit.getDamage() > stoner.getGrades()[3]) {
      hit.setDamage(stoner.getGrades()[3]);
    }

    if (hit.getType() != Hit.HitTypes.POISON && hit.getType() != Hit.HitTypes.NONE) {
      stoner.getDegrading().degradeEquipment(stoner);
    }

    stoner.getGrades()[3] = (short) (stoner.getGrades()[3] - hit.getDamage());

	  if (stoner.isPetStoner() && hit.getDamage() > 0) {
		  PetCombatHandler.onPetTakeDamage(stoner, hit.getDamage());
	  }

    if (!stoner.getUpdateFlags().isHitUpdate()) {
      stoner.getUpdateFlags().sendHit(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
    } else {
      stoner.getUpdateFlags().sendHit2(hit.getDamage(), hit.getHitType(), hit.getCombatHitType());
    }

    if (hit.getType() != Hit.HitTypes.POISON) {
      if (stoner.getTrade().trading()) {
        stoner.getTrade().end(false);
      } else {
        if (stoner.getInterfaceManager().hasInterfaceOpen()) {
          stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
        }
      }
    }

    checkForDeath();

    if (stoner.getGrades()[3] > 0 && !stoner.isDead()) {
      if (stoner.getEquipment().isWearingItem(11090)) {
        if (stoner.getProfession().getGrades()[3] > 0
            && stoner.getProfession().getGrades()[3] <= stoner.getMaxGrades()[3] * 0.20) {
          stoner.send(
              new SendMessage(
                  "The Phoenix necklace of life saves you but was destroyed in the process"));
          stoner.getEquipment().remove(new Item(11090, 1));
          stoner.getGrades()[3] += (stoner.getMaxGrades()[3] * 0.30);
        }
      }

      if (stoner.getEquipment().isWearingItem(2570) && !stoner.inDuelArena()) {
        if (stoner.getProfession().getGrades()[3] > 0
            && stoner.getProfession().getGrades()[3] <= stoner.getMaxGrades()[3] * 0.10) {
          stoner.getEquipment().remove(new Item(2570, 1));
          stoner.getMage().teleport(3085, 3491, 0, TeleportTypes.SPELL_BOOK);
          stoner.send(
              new SendMessage("The Ring of life has saved you; but was destroyed in the process."));
          hit.getAssaulter().getCombat().reset();
        }
      }
    }

    if (hit.getAssaulter() != null) {

      RingOfRecoil.doRecoil(stoner, hit.getAssaulter(), hit.getDamage());

      if (stoner.getEquipment().getItems()[3] != null
          && stoner.getEquipment().getItems()[3].getId() == 12006) {
        if (Utility.random(100) < 25) {
          if (hit.getAssaulter() != null) {
            hit.getAssaulter().poison(4);
          }
        }
      }

		if (stoner.isRetaliate()
			&& stoner.getCombat().getAssaulting() == null
			&& !stoner.getMovementHandler().moving()) {
			stoner.getCombat().setAssault(hit.getAssaulter());
		}

      stoner.getCombat().getDamageTracker().addDamage(hit.getAssaulter(), hit.getDamage());

      hit.getAssaulter().onHit(stoner, hit);
    }

    stoner.getProfession().update(3);
  }

  @Override
  public boolean isIgnoreHitSuccess() {
    if (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS
        && stoner.getSpecialAssault().isInitialized()) {
      Item weapon = stoner.getEquipment().getItems()[3];

      return weapon != null && weapon.getId() == 11235;
    }

    return false;
  }

  @Override
  public void onAssault(Entity assault, long hit, CombatTypes type, boolean success) {
    if (success || type == CombatTypes.MAGE) {
      if (assault.getGrades()[3] < hit) {
        hit = assault.getGrades()[3];
      }

      stoner.getProfession().addCombatExperience(type, hit);
    }

    if (!assault.isNpc()) {
      Stoner p = World.getStoners()[assault.getIndex()];

    }

    switch (type) {
      case MAGE:
        if (success) {
          MageEffects.doMageEffects(
              stoner, assault, stoner.getMage().getSpellCasting().getCurrentSpellId());
        }
        stoner.getMage().getSpellCasting();
        break;
      case MELEE:
        break;
      case SAGITTARIUS:
        stoner.getSagittarius().removeArrowsOnAssault();

        if (stoner.getSagittarius().isOnyxEffectActive()) {
          stoner.getSagittarius().doOnyxEffect(hit);
        }
        break;
      case NONE:
        break;
    }
  }

  @Override
  public void onCombatProcess(Entity entity) {
	  if (stoner.isPetStoner()) {
		  return;
	  }
    if (stoner.getSpecialAssault().isInitialized()
        && stoner.getCombat().getCombatType() != CombatTypes.MAGE) {
      SpecialAssaultHandler.handleSpecialAssault(stoner);
      if (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS) {
        stoner.getSagittarius().doActionsForDarkBow(entity);
      }
    } else if (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS) {
      BarrowsSpecials.checkForBarrowsSpecial(stoner);
      stoner.getSagittarius().doActionsForDarkBow(entity);
    } else {
      if (stoner.getMage().isDFireShieldEffect()) {
        stoner.getMage().decrDragonFireShieldCharges();
      }

      BarrowsSpecials.checkForBarrowsSpecial(stoner);
    }

    if (stoner.getCombat().getCombatType() != CombatTypes.MAGE) {
      StonerSounds.sendSoundForId(
          stoner,
          stoner.getSpecialAssault().isInitialized(),
          stoner.getEquipment().getItems()[3] != null
              ? stoner.getEquipment().getItems()[3].getId()
              : 0);
    }

    if (stoner.getCombat().getCombatType() == CombatTypes.MAGE) {
      stoner.getMage().getSpellCasting().appendMultiSpell(stoner);
    }

    PoisonWeapons.checkForPoison(stoner, entity);

    stoner.getDegrading().degradeWeapon(stoner);

    if (ToxicBlowpipe.hasBlowpipe(stoner)) {
      ToxicBlowpipe.degrade(stoner);
    }

    if (TridentOfTheSeas.hasTrident(stoner)) {
      TridentOfTheSeas.degrade(stoner);
    }

    if (TridentOfTheSwamp.hasTrident(stoner)) {
      TridentOfTheSwamp.degrade(stoner);
    }
  }

  @Override
  public void onHit(Entity entity, Hit hit) {
    if (stoner.getAttributes().get(PestControlGame.PEST_GAME_KEY) != null) {
      stoner
          .getAttributes()
          .set(
              PestControlGame.PEST_DAMAGE_KEY,
              stoner.getAttributes().get(PestControlGame.PEST_DAMAGE_KEY) != null
                  ? stoner.getAttributes().getInt(PestControlGame.PEST_DAMAGE_KEY) + hit.getDamage()
                  : hit.getDamage());
    }

	  if (stoner.isPetStoner() && hit.getDamage() > 0) {
		  PetCombatHandler.onPetDealDamage(stoner, entity, hit.getDamage());
	  }

    if (stoner.getCombat().getCombatType() == CombatTypes.SAGITTARIUS) {
      stoner.getSagittarius().dropArrowAfterHit();
      stoner.getSagittarius().doActionsForChinchompa(entity);
      if (hit.getDamage() != 0 && !ToxicBlowpipe.hasBlowpipe(stoner)) {
        BoltSpecials.checkForBoltSpecial(stoner, entity, hit);
      }
    }

    if (stoner.getMelee().isGuthanEffectActive())
      BarrowsSpecials.doGuthanEffect(stoner, entity, hit);
    else if (stoner.getMelee().isToragEffectActive()) BarrowsSpecials.doToragEffect(stoner, entity);
    else if (stoner.getSagittarius().isKarilEffectActive())
      BarrowsSpecials.doKarilEffect(stoner, entity);
    else if (stoner.getMage().isAhrimEffectActive()) {
      BarrowsSpecials.doAhrimEffect(stoner, entity, hit.getDamage());
    }

    if (entity.isNpc() && entity.isDead()) {
      stoner.getCombat().setAssaultTimer(0);
      stoner.getCombat().resetCombatTimer();
    }

    if (stoner.getMage().isDFireShieldEffect()) {
      stoner.getMage().reset();
      stoner.getMage().getSpellCasting().updateMageAssault();
      stoner.updateCombatType();
    }
  }

  @Override
  public void updateCombatType() {
    CombatTypes type;
    if (stoner.getMage().getSpellCasting().isCastingSpell()) {
      type = CombatTypes.MAGE;
    } else {
      type = EquipmentConstants.getCombatTypeForWeapon(stoner);
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
}
