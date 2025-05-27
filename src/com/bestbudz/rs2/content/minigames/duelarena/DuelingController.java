package com.bestbudz.rs2.content.minigames.duelarena;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerOption;

public class DuelingController extends Controller {
  @Override
  public boolean allowMultiSpells() {
    return false;
  }

  @Override
  public boolean allowPvPCombat() {
    return true;
  }

  @Override
  public boolean canAssaultNPC() {
    return false;
  }

  @Override
  public boolean canAssaultStoner(Stoner p, Stoner p2) {
    if (p.getDueling().getInteracting() == null) {
      return false;
    }
    if (!p.getDueling().getInteracting().equals(p2)) {
      p.getClient().queueOutgoingPacket(new SendMessage("You are not dueling this stoner!"));
      return false;
    }
    if (!p.getDueling().canAssault()) {
      p.getClient().queueOutgoingPacket(new SendMessage("The duel hasn't started yet!"));
      return false;
    }

    return p.getDueling().canUseWeapon();
  }

  @Override
  public boolean canClick() {
    return true;
  }

  @Override
  public boolean canDrink(Stoner p) {
    if (p.getDueling().getRuleToggle() == null) {
      return true;
    }
    if (p.getDueling().getRuleToggle()[5]) {
      p.getClient()
          .queueOutgoingPacket(new SendMessage("You cannot use potions during this duel!"));
      return false;
    }
    return true;
  }

  @Override
  public boolean canEat(Stoner p) {
    if (p.getDueling().getRuleToggle() == null) {
      return true;
    }
    if (p.getDueling().getRuleToggle()[6]) {
      p.getClient().queueOutgoingPacket(new SendMessage("You cannot use food during this duel!"));
      return false;
    }
    return true;
  }

  @Override
  public boolean canEquip(Stoner p, int id, int slot) {
    if (p.getDueling().getToRemove() == null) {
      return true;
    }
    if ((p.getDueling().getToRemove()[slot])
        || ((slot == 3)
            && (p.getDueling().getToRemove()[5])
            && (Item.getWeaponDefinition(id).isTwoHanded()))) {
      p.getClient().queueOutgoingPacket(new SendMessage("You can't wear this during the duel!"));
      return false;
    }
    return true;
  }

  @Override
  public boolean canUnequip(Stoner stoner) {
    return true;
  }

  @Override
  public boolean canDrop(Stoner stoner) {
    return false;
  }

  @Override
  public boolean canLogOut() {
    return false;
  }

  @Override
  public boolean canMove(Stoner p) {
    if (p.getDueling().getRuleToggle() == null) {
      return true;
    }
    if (p.getDueling().getRuleToggle()[1]) {
      p.getClient().queueOutgoingPacket(new SendMessage("You cannot move during this duel."));
      return false;
    }
    return true;
  }

  @Override
  public boolean canSave() {
    return false;
  }

  @Override
  public boolean canTalk() {
    return true;
  }

  @Override
  public boolean canTeleport() {
    return false;
  }

  @Override
  public boolean canTrade() {
    return false;
  }

  @Override
  public boolean canUseCombatType(Stoner p, CombatTypes type) {
    if (p.getDueling().getRuleToggle() == null) {
      return true;
    }
    switch (type) {
      case MAGE:
        if (p.getDueling().getRuleToggle()[4]) {
          p.getClient()
              .queueOutgoingPacket(new SendMessage("You can't use Mage during this duel!"));
          return false;
        }
        break;
      case MELEE:
        if (p.getDueling().getRuleToggle()[3]) {
          p.getClient()
              .queueOutgoingPacket(new SendMessage("You can't use Melee during this duel!"));
          return false;
        }
        break;
      case SAGITTARIUS:
        if (p.getDueling().getRuleToggle()[2]) {
          p.getClient()
              .queueOutgoingPacket(new SendMessage("You can't use Sagittarius during this duel!"));
          return false;
        }
        break;
      case NONE:
        break;
    }

    return true;
  }

  @Override
  public boolean canUseNecromance(Stoner p, int id) {
    if (p.getDueling().getRuleToggle() == null) {
      return true;
    }
    if (p.getDueling().getRuleToggle()[7]) {
      p.getClient()
          .queueOutgoingPacket(new SendMessage("You cannot use necromance during this duel!"));
      return false;
    }
    return true;
  }

  @Override
  public boolean canUseSpecialAssault(Stoner p) {
    if (p.getDueling().getRuleToggle() == null) {
      return true;
    }
    if (p.getDueling().getRuleToggle()[10]) {
      p.getClient()
          .queueOutgoingPacket(
              new SendMessage("You cannot use special assaults during this duel!"));
      return false;
    }
    return true;
  }

  @Override
  public Location getRespawnLocation(Stoner stoner) {
    Location p =
        DuelingConstants.RESPAWN_LOCATIONS[
            com.bestbudz.core.util.Utility.randomNumber(DuelingConstants.RESPAWN_LOCATIONS.length)];
    int[] dir =
        com.bestbudz.rs2.GameConstants.DIR[
            com.bestbudz.core.util.Utility.randomNumber(com.bestbudz.rs2.GameConstants.DIR.length)];
    return new Location(p.getX() + dir[0], p.getY() + dir[1]);
  }

  @Override
  public boolean isSafe(Stoner stoner) {
    return true;
  }

  @Override
  public void onControllerInit(Stoner stoner) {
    stoner.getClient().queueOutgoingPacket(new SendStonerOption("Assault", 3));
  }

  @Override
  public void onDeath(Stoner p) {
    p.getDueling().onDuelEnd(false, false);
  }

  @Override
  public void onKill(Stoner stoner, Entity killed) {}

  @Override
  public void onDisconnect(Stoner p) {}

  @Override
  public void onTeleport(Stoner p) {}

  @Override
  public void tick(Stoner stoner) {}

  @Override
  public String toString() {
    return null;
  }

  @Override
  public boolean transitionOnWalk(Stoner p) {
    return false;
  }
}
