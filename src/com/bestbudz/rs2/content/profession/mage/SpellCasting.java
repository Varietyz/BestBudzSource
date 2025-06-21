package com.bestbudz.rs2.content.profession.mage;

import com.bestbudz.core.definitions.CombatSpellDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SpellCasting {

  public static final Assault MAGE_ASSAULT = new Assault(4, 5);
  private final Stoner stoner;
  private int castId = -1;
  private int autocastId = -1;

  public SpellCasting(Stoner stoner) {
    this.stoner = stoner;
  }

  public void addSpellExperience() {
    if (getCurrentSpellId() != -1) {
      stoner
          .getProfession()
          .addExperience(
              6,
              GameDefinitionLoader.getCombatSpellDefinition(getCurrentSpellId())
                  .getBaseExperience());
    }
  }

  public void appendMultiSpell(Stoner p) {
    // Unconditionally allow AoE with larger radius
    byte affected = 0;

    Entity a = p.getCombat().getAssaulting();
    if (a == null) return;

    int x = a.getLocation().getX();
    int y = a.getLocation().getY();

    // Allow PvM AoE
    for (Mob i : stoner.getClient().getNpcs()) {
      if (!i.equals(a)) {
        int dx = Math.abs(x - i.getLocation().getX());
        int dy = Math.abs(y - i.getLocation().getY());
        if (dx <= 3 && dy <= 3) { // ← Tripled radius
          p.getCombat().getMage().finish(i);
          affected++;
          if (affected == 18) return; // Doubled affected
        }
      }
    }
  }

  public boolean canCast() {
    if (castId != -1) return canCastSpell(castId);
    if (isAutocasting()) {
      return canCastSpell(autocastId);
    }

    return stoner.getMage().isDFireShieldEffect();
  }

  private boolean canCastSpell(int id) {
    CombatSpellDefinition definition = getDefinition(id);

    if (definition == null) {
      return false;
    }

    if (definition.getId() == 9999) {
      if (stoner.getCombat().getAssaulting() != null
          && !stoner.getCombat().getAssaulting().isNpc()) {
        stoner.send(new SendMessage("@red@You cannot use this spell on stoners!"));
        return false;
      }
    }

    int[] wep = definition.getWeapons();
    boolean found = false;

    if ((wep != null) && (wep.length > 0)) {
      Item weapon = stoner.getEquipment().getItems()[3];

      if (weapon == null) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You need a "
                        + Item.getDefinition(wep[0]).getName()
                        + " to cast "
                        + definition.getName()
                        + "."));
        return false;
      }

      for (int i : wep) {
        if (weapon.getId() == i) {
          found = true;
        }
      }

      if (!found) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You need a "
                        + Item.getDefinition(wep[0]).getName()
                        + " to cast "
                        + definition.getName()
                        + "."));
        return false;
      }
    }

    return true;
  }

  public void cast(Spell spell) {

    if (!stoner.getController().canUseCombatType(stoner, CombatTypes.MAGE)) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot use mage right now."));
      return;
    }

    if (spell.execute(stoner)) {
      stoner.getProfession().addExperience(6, spell.getExperience());
    }
  }

	public void castCombatSpell(int castId, Entity other) {
		if (castId == 12445 && other.isTeleblocked()) {
			stoner
				.getClient()
				.queueOutgoingPacket(
					new SendMessage("This stoner is already affected by this spell."));
			return;
		}
		if (canCastSpell(castId)) {
			this.castId = castId;
			this.autocastId = castId;  // ← Add this line to enable autocast
			updateMageAssault();
			stoner.updateCombatType();
			//Autocast.resetAutoCastInterface(stoner);  // ← Add this to update the interface
			stoner.getFollowing().setFollow(other, Following.FollowType.COMBAT);
			stoner.getCombat().setAssaulting(other);
			stoner.face(other);
		} else {
			stoner.getCombat().reset();
		}
	}

  public Item[] createArray(Item[] items) {
    Item[] array = new Item[items.length];

    for (int i = 0; i < array.length; i++) {
      if (items[i] != null) {
        array[i] = new Item(items[i].getId(), items[i].getAmount());
      }
    }

    return array;
  }

  public void disableAutocast() {
    autocastId = -1;
    stoner.getMage().setDFireShieldEffect(false);
    Autocast.resetAutoCastInterface(stoner);
  }

  public void disableClickCast() {
    if (castId != -1) {
      castId = -1;
      stoner.updateCombatType();
    }
  }

  public void enableAutocast(int autocastId) {
    if (canCastSpell(autocastId)) {
      this.autocastId = autocastId;
      updateMageAssault();
      stoner.updateCombatType();
    }
  }

  public int getCurrentSpellId() {
    if (isClickcasting()) return castId;
    if (isAutocasting()) {
      return autocastId;
    }
    return -1;
  }

  public CombatSpellDefinition getDefinition(int id) {
    return GameDefinitionLoader.getCombatSpellDefinition(id);
  }

  public boolean isAutocasting() {
    return autocastId != -1;
  }

  public boolean isCastingSpell() {
    return (isAutocasting()) || (isClickcasting()) || (stoner.getMage().isDFireShieldEffect());
  }

  public boolean isClickcasting() {
    return castId != -1;
  }

  public void resetOnAssault() {
    if ((isClickcasting()) && (isAutocasting())) {
      castId = -1;
      stoner.getCombat().reset();
      updateMageAssault();
      stoner.updateCombatType();
    } else if (isClickcasting()) {
      castId = -1;
      stoner.getCombat().reset();
      stoner.updateCombatType();
    } else if (stoner.getMage().isDFireShieldEffect()) {
      stoner.getMage().reset();
      stoner.updateCombatType();
    }
  }

  public void updateMageAssault() {
    CombatSpellDefinition def = getDefinition(getCurrentSpellId());

    if (def == null) {
      stoner.getMage().reset();
      return;
    }

    stoner
        .getCombat()
        .getMage()
        .setAssault(
            MAGE_ASSAULT, def.getAnimation(), def.getStart(), def.getEnd(), def.getProjectile());

    stoner.getCombat().getMage().setMulti(def.getName().contains("barrage"));
  }
}
