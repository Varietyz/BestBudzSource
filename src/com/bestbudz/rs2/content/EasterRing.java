package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;

public class EasterRing {

  public static final int EASTER_RING_ID = 7927;
  public static final int UNMORPH_INTERFACE_ID = 6014;
  public static final Controller EASTER_RING_CONTROLLER = new EasterRingController();
  public static final int[] EGG_IDS = {3689, 3690, 3691, 3692, 3693, 3694};

  public static void cancel(Stoner stoner) {
    stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
    stoner.setNpcAppearanceId((short) -1);
    stoner.setAppearanceUpdateRequired(true);

    int[] tabs = (int[]) stoner.getAttributes().get("tabs");

    for (int i = 0; i < tabs.length; i++) {
      stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(i, tabs[i]));
    }

    stoner.getClient().queueOutgoingPacket(new SendMessage("You morph back into a human."));
  }

  public static final boolean canEquip(Stoner stoner) {
    if (!stoner.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot do this here."));
      return false;
    }

    return true;
  }

  public static void init(Stoner stoner) {
    stoner.setNpcAppearanceId((short) EGG_IDS[Utility.randomNumber(EGG_IDS.length)]);
    stoner.setController(EASTER_RING_CONTROLLER);
    stoner.getMovementHandler().reset();

    int[] tabs = stoner.getInterfaceManager().getTabs().clone();

    stoner.getAttributes().set("tabs", tabs);

    for (int i = 0; i < tabs.length; i++) {
      stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(i, -1));
    }
    stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(3, 6014));
    stoner.getClient().queueOutgoingPacket(new SendMessage("You morph into an Easter egg."));
  }

  public static class EasterRingController extends Controller {
    @Override
    public boolean allowMultiSpells() {
      return false;
    }

    @Override
    public boolean allowPvPCombat() {
      return false;
    }

    @Override
    public boolean canAssaultNPC() {
      return false;
    }

    @Override
    public boolean canAssaultStoner(Stoner p, Stoner p2) {
      return false;
    }

    @Override
    public boolean canClick() {
      return false;
    }

    @Override
    public boolean canDrink(Stoner p) {
      return false;
    }

    @Override
    public boolean canEat(Stoner p) {
      return false;
    }

    @Override
    public boolean canEquip(Stoner p, int id, int slot) {
      return false;
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
      return false;
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
      return false;
    }

    @Override
    public boolean canUseResonance(Stoner p, int id) {
      return false;
    }

    @Override
    public boolean canUseSpecialAssault(Stoner p) {
      return false;
    }

    @Override
    public Location getRespawnLocation(Stoner stoner) {
      return StonerConstants.HOME;
    }

    @Override
    public boolean isSafe(Stoner stoner) {
      return false;
    }

    @Override
    public void onControllerInit(Stoner p) {}

    @Override
    public void onDeath(Stoner p) {}

    @Override
    public void onKill(Stoner stoner, Entity killed) {}

    @Override
    public void onDisconnect(Stoner p) {}

    @Override
    public void onTeleport(Stoner p) {}

    @Override
    public void tick(Stoner p) {}

    @Override
    public String toString() {
      return "Easter ring";
    }

    @Override
    public boolean transitionOnWalk(Stoner p) {
      return true;
    }
  }
}
