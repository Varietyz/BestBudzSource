package com.bestbudz.rs2.content.profession.mage;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.combat.impl.Assault;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.mage.spells.Charge;
import com.bestbudz.rs2.content.profession.mage.spells.HighAlchemy;
import com.bestbudz.rs2.content.profession.mage.spells.LowAlchemy;
import com.bestbudz.rs2.content.profession.mage.spells.SuperHeat;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.Projectile;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.pets.PetManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendOpenTab;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MageProfession {

  public static final String MAGE_ITEM_KEY = "mageitem";
  public static final int[][] AUTOCAST_BUTTONS = {
    {84242, 21746},
    {50091, 12891},
    {50129, 12929},
    {50223, 13023},
    {50175, 12975},
    {84241, 21745},
    {50071, 12871},
    {50111, 12911},
    {50199, 12999},
    {50151, 12951},
    {86152, 22168},
    {50081, 12881},
    {50119, 12919},
    {50221, 13011},
    {50163, 12963},
    {84220, 21744},
    {50061, 12861},
    {50101, 12901},
    {50187, 12987},
    {50139, 12939},
    {6056, 1592},
    {4165, 1189},
    {4164, 1188},
    {4161, 1185},
    {4159, 1183},
    {4168, 1192},
    {4167, 1191},
    {4166, 1190},
    {4157, 1181},
    {4153, 1177},
    {6046, 1582},
    {4151, 1175},
    {4148, 1172},
    {4145, 1169},
    {4142, 1166},
    {4139, 1163},
    {6036, 1572},
    {4136, 1160},
    {4134, 1158},
    {4132, 1156},
    {4130, 1154},
    {4128, 1152}
  };
  private final Stoner stoner;
  private final SpellCasting spellCasting;
  private boolean teleporting = false;
  private boolean ahrimEffectActive = false;
  private byte dragonFireShieldCharges = 0;
  private int mageBook = 0;
  private SpellBookTypes spellBookType = SpellBookTypes.MODERN;
  private boolean dFireShieldEffect = false;
  private long dFireShieldTime = 0L;

  public MageProfession(Stoner stoner) {
    this.stoner = stoner;
    spellCasting = new SpellCasting(stoner);
  }

  public boolean canTeleport(TeleportTypes type) {
    if (stoner.isJailed()) {
      stoner.send(new SendMessage("You are jailed and can not do this!"));
      return false;
    }
    if (stoner.getController().equals(ControllerManager.FIGHT_PITS_CONTROLLER)
        || stoner.getController().equals(ControllerManager.FIGHT_PITS_WAITING_CONTROLLER)) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You can't teleport from here."));
      return false;
    }
    if (stoner.getController().equals(ControllerManager.PEST_WAITING_ROOM_CONTROLLER)) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("Please Exit the boat via the ladder."));
      return false;
    } else if (stoner.getController().equals(ControllerManager.PEST_CONTROLLER)) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You can't teleport whilst in pest control."));
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("If you wish to leave speak with the squire back at the boat."));
      return false;
    }
    if (stoner.inDuelArena() && stoner.getDueling().isDueling()) {
      return false;
    }
    if (stoner.isBusyNoInterfaceCheck()) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You can't teleport right now."));
      return false;
    }
    if (stoner.isTeleblocked()) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("A mage force blocks you from teleporting."));
      return false;
    } else if (teleporting) {
      return false;
    } else if (!stoner.getController().canTeleport()) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You can't teleport right now."));
      return false;
    }
    return true;
  }

  public boolean clickMageButtons(int buttonId) {
    if (buttonId == 26010) {
      spellCasting.disableAutocast();
      Autocast.resetAutoCastInterface(stoner);
      stoner.updateCombatType();
      return true;
    }

    for (int i = 0; i < AUTOCAST_BUTTONS.length; i++) {
      if (buttonId == AUTOCAST_BUTTONS[i][0]) {
        Autocast.setAutocast(stoner, AUTOCAST_BUTTONS[i][1]);
        return true;
      }
    }
    switch (buttonId) {
      case 75008:
        stoner
            .getClient()
            .queueOutgoingPacket(new SendMessage("You don't have a house to teleport to!"));
        return true;
      case 4169:
        stoner.getMage().getSpellCasting().cast(new Charge());
        return true;
    }
    return false;
  }

  public boolean clickMageItems(int id) {
    switch (id) {
      case 8007:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(3212, 3424, 0, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8008:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(3222, 3218, 0, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8009:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(2964, 3378, 0, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8010:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(2757, 3477, 0, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8011:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(2662, 3305, 0, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8012:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(2549, 3112, 0, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8013:
        if (canTeleport(TeleportTypes.TABLET)) {
          stoner.getBox().remove(id, 1);
          teleport(MageConstants.Teleports.HOME, TeleportTypes.TABLET);
          return true;
        }
        break;
      case 8014:
      case 8015:
        int[] bones = {526, 528, 530};

        int bone = 0;

        for (int index = 0; index < bones.length; index++) {
          if (stoner.getBox().hasItemId(bones[index])) {
            bone = bones[index];
            continue;
          }
        }

        int amount = stoner.getBox().getItemAmount(bone);

        if (amount == 0) {
          stoner.send(new SendMessage("You have no bones to do this!"));
          return false;
        }

        stoner.getBox().remove(id, 1);
        stoner.getBox().remove(bone, amount);
        stoner.getBox().add(id == 8014 ? 1963 : 6883, amount);
        stoner.getProfession().addExperience(Professions.MAGE, id == 8014 ? 25 : 35.5);

        stoner.send(
            new SendMessage(
                "You have converted "
                    + amount
                    + " bones to "
                    + (id == 8014 ? "bananas" : "peaches")
                    + " ."));

        return true;
    }
    return false;
  }

  public void decrDragonFireShieldCharges() {
    dragonFireShieldCharges = ((byte) (dragonFireShieldCharges - 1));

    if (dragonFireShieldCharges == 0)
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("Your Dragonfire shield is now empty."));
  }

  public void doWildernessTeleport(
      final int x, final int y, final int z, final TeleportTypes type) {
    teleporting = true;
    stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    stoner.getController().onTeleport(stoner);
    int delay = 3;
    switch (type) {
      case OBELISK:
        stoner.getUpdateFlags().sendGraphic(Graphic.highGraphic(342, 0));
        stoner.getUpdateFlags().sendAnimation(new Animation(1816));
        TaskQueue.queue(
            new Task(
                stoner,
                1,
                false,
                Task.StackType.STACK,
                Task.BreakType.NEVER,
                TaskIdentifier.CURRENT_ACTION) {
              @Override
              public void execute() {
                stop();
              }

              @Override
              public void onStop() {
                stoner
                    .getClient()
                    .queueOutgoingPacket(
                        new SendMessage("Ancient mage teleports you somewhere in the wilderness"));
              }
            });
        break;
      case TABLET:
        stoner.getUpdateFlags().sendAnimation(MageConstants.TABLET_BREAK_ANIMATION);
        TaskQueue.queue(
            new Task(
                stoner,
                1,
                false,
                Task.StackType.STACK,
                Task.BreakType.NEVER,
                TaskIdentifier.CURRENT_ACTION) {
              @Override
              public void execute() {
                stoner.getUpdateFlags().sendAnimation(MageConstants.TABLET_TELEPORT_ANIMATION);
                stoner.getUpdateFlags().sendGraphic(MageConstants.TABLET_TELEPORT_GRAPHIC);
                stop();
              }

              @Override
              public void onStop() {}
            });
        break;
      case TELE_OTHER:
        stoner.getUpdateFlags().sendAnimation(1816, 0);
        stoner.getUpdateFlags().sendGraphic(new Graphic(342, 0, false));
        break;
      case FOUNTAIN_OF_RUNE:
        stoner.getUpdateFlags().sendAnimation(1816, 0);
        stoner.getUpdateFlags().sendGraphic(new Graphic(342, 0, false));
        break;
      default:
        switch (spellBookType) {
          case ANCIENT:
            stoner.getUpdateFlags().sendAnimation(MageConstants.ANCIENT_TELEPORT_ANIMATION);
            stoner.getUpdateFlags().sendGraphic(MageConstants.ANCIENT_TELEPORT_GRAPHIC);
            delay = 4;
            break;
          default:
            stoner.getClient().queueOutgoingPacket(new SendSound(202, 1, 0));
            stoner.getUpdateFlags().sendAnimation(MageConstants.MODERN_TELEPORT_ANIMATION);
            stoner.getUpdateFlags().sendGraphic(MageConstants.MODERN_TELEPORT_GRAPHIC);
            delay = 4;
        }

        break;
    }

    TaskQueue.queue(
        new Task(
            stoner,
            delay,
            false,
            Task.StackType.STACK,
            Task.BreakType.NEVER,
            TaskIdentifier.CURRENT_ACTION) {
          @Override
          public void execute() {
            if (!stoner.getController().canTeleport()) {
              stoner.setTakeDamage(true);
              teleporting = false;
              return;
            }

            TaskQueue.onMovement(stoner);

            stoner.teleport(new Location(x, y, z));
            stoner.setTakeDamage(true);
            teleporting = false;

            switch (type) {
              case SPELL_BOOK:
                stoner.getUpdateFlags().sendAnimation(MageConstants.MODERN_TELEPORT_END_ANIMATION);
                switch (spellBookType) {
                  case MODERN:
                    stoner.getUpdateFlags().sendGraphic(MageConstants.MODERN_TELEPORT_END_GRAPHIC);
                    break;
                  default:
                    break;
                }
                break;
              case TABLET:
                stoner.getUpdateFlags().sendAnimation(MageConstants.TABLET_TELEPORT_END_ANIMATION);
                break;
              default:
                break;
            }

            stop();
          }

          @Override
          public void onStop() {}
        });
  }

  public byte getDragonFireShieldCharges() {
    return dragonFireShieldCharges;
  }

  public void setDragonFireShieldCharges(int dragonFireShieldCharges) {
    this.dragonFireShieldCharges = ((byte) dragonFireShieldCharges);
  }

  public int getMageBook() {
    return mageBook;
  }

  public void setMageBook(int mageBook) {
    this.mageBook = mageBook;
    stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(6, mageBook));

    if (stoner.isActive()) {
      spellCasting.disableAutocast();
      Autocast.resetAutoCastInterface(stoner);
      stoner.updateCombatType();
    }

    switch (mageBook) {
      case 1151:
        stoner.getMage().setSpellBookType(SpellBookTypes.MODERN);
        break;
      case 12855:
        stoner.getMage().setSpellBookType(SpellBookTypes.ANCIENT);
        break;
    }
  }

  public SpellBookTypes getSpellBookType() {
    return spellBookType;
  }

  public void setSpellBookType(SpellBookTypes spellBookType) {
    this.spellBookType = spellBookType;
  }

  public SpellCasting getSpellCasting() {
    return spellCasting;
  }

  public void incrDragonFireShieldCharges(Mob mob) {
    if (dragonFireShieldCharges == 50 || stoner.isDead() || stoner.getMage().isTeleporting()) {
      return;
    } else if (dragonFireShieldCharges > 50) {
      dragonFireShieldCharges = 50;
      return;
    }
    stoner.face(mob);
    stoner.getUpdateFlags().sendGraphic(new Graphic(1164));
    stoner.getUpdateFlags().sendAnimation(new Animation(6695));
    dragonFireShieldCharges = ((byte) (dragonFireShieldCharges + 1));
  }

  private void initTeleport(final int x, final int y, final int z, final TeleportTypes type) {

    teleporting = true;
    stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    stoner.getController().onTeleport(stoner);
    int delay = 3;
    switch (type) {
      case TABLET:
        stoner.getUpdateFlags().sendAnimation(MageConstants.TABLET_BREAK_ANIMATION);
        TaskQueue.queue(
            new Task(
                stoner,
                1,
                false,
                Task.StackType.STACK,
                Task.BreakType.NEVER,
                TaskIdentifier.CURRENT_ACTION) {
              @Override
              public void execute() {
                stoner.getUpdateFlags().sendAnimation(MageConstants.TABLET_TELEPORT_ANIMATION);
                stoner.getUpdateFlags().sendGraphic(MageConstants.TABLET_TELEPORT_GRAPHIC);
                stop();
              }

              @Override
              public void onStop() {}
            });
        break;
      case TELE_OTHER:
        stoner.getUpdateFlags().sendAnimation(1816, 0);
        stoner.getUpdateFlags().sendGraphic(new Graphic(342, 0, false));
        break;
      case FOUNTAIN_OF_RUNE:
        stoner.getUpdateFlags().sendAnimation(1816, 0);
        stoner.getUpdateFlags().sendGraphic(new Graphic(283, 0, false));
        break;
      default:
        switch (spellBookType) {
          case ANCIENT:
            stoner.getUpdateFlags().sendAnimation(MageConstants.ANCIENT_TELEPORT_ANIMATION);
            stoner.getUpdateFlags().sendGraphic(MageConstants.ANCIENT_TELEPORT_GRAPHIC);
            delay = 4;
            break;

          default:
            stoner.getClient().queueOutgoingPacket(new SendSound(202, 1, 0));
            stoner.getUpdateFlags().sendAnimation(MageConstants.MODERN_TELEPORT_ANIMATION);
            stoner.getUpdateFlags().sendGraphic(MageConstants.MODERN_TELEPORT_GRAPHIC);
            delay = 4;
        }

        break;
    }

	  TaskQueue.queue(
		  new Task(
			  stoner,
			  delay,
			  false,
			  Task.StackType.STACK,
			  Task.BreakType.NEVER,
			  TaskIdentifier.CURRENT_ACTION) {
			  @Override
			  public void execute() {
				  if (!stoner.getController().canTeleport()) {
					  stoner.setTakeDamage(true);
					  teleporting = false;
					  return;
				  }

				  TaskQueue.onMovement(stoner);

				  stoner.teleport(new Location(x, y, z));
				  stoner.setTakeDamage(true);
				  teleporting = false;

				  switch (type) {
					  case SPELL_BOOK:
						  stoner.getUpdateFlags().sendAnimation(MageConstants.MODERN_TELEPORT_END_ANIMATION);
						  switch (spellBookType) {
							  case MODERN:
								  stoner.getUpdateFlags().sendGraphic(MageConstants.MODERN_TELEPORT_END_GRAPHIC);
								  break;
							  default:
								  break;
						  }
						  break;
					  case TABLET:
						  stoner.getUpdateFlags().sendAnimation(MageConstants.TABLET_TELEPORT_END_ANIMATION);
						  break;
					  default:
						  break;
				  }

				  stop();
			  }

			  @Override
			  public void onStop() {
				  // Use the new Stoner pet system instead of old Mob pets
				  if (stoner.getBossID() > 0) {
					  // Find the PetData for this boss ID
					  PetData petData = PetData.forNPC(stoner.getBossID());
					  if (petData != null) {
						  // Remove any existing pets first (handles teleportation cleanup)
						  List<Pet> existingPets = new ArrayList<>(stoner.getActivePets());
						  for (Pet pet : existingPets) {
							  pet.remove();
							  stoner.getActivePets().remove(pet);
						  }

						  // Spawn new pet at teleport destination
						  Pet newPet = new Pet(stoner, petData);
						  stoner.getActivePets().add(newPet);
					  }
				  }
			  }
		  });
  }

  public boolean isAhrimEffectActive() {
    return ahrimEffectActive;
  }

  public void setAhrimEffectActive(boolean ahrimEffectActive) {
    this.ahrimEffectActive = ahrimEffectActive;
  }

  public boolean isDFireShieldEffect() {
    return dFireShieldEffect;
  }

  public void setDFireShieldEffect(boolean dFireShieldEffect) {
    this.dFireShieldEffect = dFireShieldEffect;
  }

  public boolean isTeleporting() {
    return teleporting;
  }

  public void setTeleporting(boolean teleporting) {
    this.teleporting = teleporting;
  }

  public void onLogin() {}

  public void onOperateDragonFireShield() {
    if (dragonFireShieldCharges == 0) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You do not have any charges on your shield."));
      return;
    }
    if ((!StonerConstants.isOwner(stoner))
        && (System.currentTimeMillis() - dFireShieldTime < 300000L)) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You must let your shield cool down before using it again."));
      return;
    }
    dFireShieldEffect = (!dFireShieldEffect);

    if (dFireShieldEffect) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("Your Dragonfire shield assault is now active."));

      Projectile p = new Projectile(1166);

      p.setStartHeight(25);
      p.setEndHeight(25);

      p.setDelay(40);
      p.setCurve(0);

      stoner.getCombat().getMage().setpDelay((byte) 1);

      if (stoner.getCombat().getAssaulting() == null) {
        stoner.getUpdateFlags().sendAnimation(6695, 0);
      }

      stoner
          .getCombat()
          .getMage()
          .setAssault(
              new Assault(5, 5),
              new Animation(6696),
              new Graphic(1167, 45, true),
              new Graphic(1167, 0, true),
              p);
    }

    stoner.updateCombatType();
  }

  public void reset() {
    dFireShieldEffect = false;
    dFireShieldTime = System.currentTimeMillis();
  }

  public void teleport(int x, int y, int z, TeleportTypes type) {
    if (!canTeleport(type) || stoner.isDead()) {
      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
      return;
    }

    initTeleport(x, y, z, type);
  }

  public void teleport(Location location, TeleportTypes teleportType) {
    teleport(location.getX(), location.getY(), location.getZ(), teleportType);
  }

  public void teleport(MageConstants.Teleports teleport, TeleportTypes teleportType) {
    teleport(
        teleport.getLocation().getX(),
        teleport.getLocation().getY(),
        teleport.getLocation().getZ(),
        teleportType);
  }

  public void teleportNoWildernessRequirement(int x, int y, int z, TeleportTypes type) {
    if (teleporting) return;
    if (stoner.isTeleblocked()) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("A mage force blocks you from teleporting."));
      return;
    }
    if (!stoner.getController().canTeleport()) {
      stoner.getClient().queueOutgoingPacket(new SendMessage("You can't teleport right now."));
      return;
    }

    if (stoner.isDead()) {
      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
      return;
    }

    initTeleport(x, y, z, type);
  }

  private boolean hasRunes(int spellID) {
    EnchantSpell ens = EnchantSpell.forId(spellID);
    if (ens.getReq3() == 0) {
      return stoner.getBox().hasItemAmount(ens.getReq1(), ens.getReqAmt1())
          && stoner.getBox().hasItemAmount(ens.getReq2(), ens.getReqAmt2())
          && stoner.getBox().hasItemAmount(ens.getReq3(), ens.getReqAmt3());
    } else {
      return stoner.getBox().hasItemAmount(ens.getReq1(), ens.getReqAmt1())
          && stoner.getBox().hasItemAmount(ens.getReq2(), ens.getReqAmt2());
    }
  }

  private int getEnchantmentGrade(int spellID) {
    switch (spellID) {
      case 1155:
        return 1;
      case 1165:
        return 2;
      case 1176:
        return 3;
      case 1180:
        return 4;
      case 1187:
        return 5;
      case 6003:
        return 6;
    }
    return 0;
  }

  public void enchantItem(int itemID, int spellID) {
    Enchant enc = Enchant.forId(itemID);
    EnchantSpell ens = EnchantSpell.forId(spellID);
    if (enc == null || ens == null) {
      return;
    }
    if (stoner.getProfession().getGrades()[Professions.MAGE] >= enc.getGradeReq()) {
      if (stoner.getBox().hasItemAmount(enc.getUnenchanted(), 1)) {
        if (hasRunes(spellID)) {
          if (getEnchantmentGrade(spellID) == enc.getEGrade()) {
            stoner.getBox().remove(enc.getUnenchanted(), 1);
            stoner.getBox().add(enc.getEnchanted(), 1);
            stoner.getProfession().addExperience(Professions.MAGE, enc.getXp());
            stoner.getBox().remove(ens.getReq1(), ens.getReqAmt1());
            stoner.getBox().remove(ens.getReq2(), ens.getReqAmt2());
            stoner.getUpdateFlags().sendAnimation(new Animation(enc.getAnim()));
            stoner.getUpdateFlags().sendGraphic(new Graphic(enc.getGFX(), true));
            if (ens.getReq3() != -1) {
              stoner.getBox().remove(ens.getReq3(), ens.getReqAmt3());
            }
            stoner.send(new SendOpenTab(6));
          } else {
            stoner.send(
                new SendMessage(
                    "You can only enchant this jewelry using a grade-"
                        + enc.getEGrade()
                        + " enchantment spell!"));
          }
        } else {
          stoner.send(new SendMessage("You do not have enough runes to cast this spell."));
        }
      }
    } else {
      stoner.send(
          new SendMessage(
              "You need a mage grade of at least " + enc.getGradeReq() + " to cast this spell."));
    }
  }

  public void useMageOnItem(int itemId, int spellId) {
    switch (spellId) {
      case 1162:
        spellCasting.cast(new LowAlchemy());
        break;
      case 1178:
        spellCasting.cast(new HighAlchemy());
        break;
      case 1155:
      case 1165:
      case 1176:
      case 1180:
      case 1187:
      case 6003:
        enchantItem(itemId, spellId);
        break;
      case 1173:
        spellCasting.cast(new SuperHeat());
        break;
    }
  }

  public enum SpellBookTypes {
    MODERN,
    ANCIENT
  }

  public enum TeleportTypes {
    SPELL_BOOK,
    TABLET,
    TELE_OTHER,
    FOUNTAIN_OF_RUNE,
    OBELISK
  }

  public enum Enchant {
    SAPPHIRERING(1637, 2550, 7, 18, 719, 114, 1),
    SAPPHIREAMULET(1694, 1727, 7, 18, 719, 114, 1),
    SAPPHIRENECKLACE(1656, 3853, 7, 18, 719, 114, 1),

    EMERALDRING(1639, 2552, 27, 37, 719, 114, 2),
    EMERALDAMULET(1696, 1729, 27, 37, 719, 114, 2),
    EMERALDNECKLACE(1658, 5521, 27, 37, 719, 114, 2),

    RUBYRING(1641, 2568, 47, 59, 720, 115, 3),
    RUBYAMULET(1698, 1725, 47, 59, 720, 115, 3),
    RUBYNECKLACE(1660, 11194, 47, 59, 720, 115, 3),

    DIAMONDRING(1643, 2570, 57, 67, 720, 115, 4),
    DIAMONDAMULET(1700, 1731, 57, 67, 720, 115, 4),
    DIAMONDNECKLACE(1662, 11090, 57, 67, 720, 115, 4),

    DRAGONSTONERING(1645, 2572, 68, 78, 721, 116, 5),
    DRAGONSTONEAMULET(1702, 1712, 68, 78, 721, 116, 5),
    DRAGONSTONENECKLACE(1664, 11105, 68, 78, 721, 116, 5),

    ONYXRING(6575, 6583, 87, 97, 721, 452, 6),
    ONYXAMULET(6581, 6585, 87, 97, 721, 452, 6),
    ONYXNECKLACE(6577, 11128, 87, 97, 721, 452, 6);
    private static final Map<Integer, Enchant> enc = new HashMap<Integer, Enchant>();

    static {
      for (Enchant en : Enchant.values()) {
        enc.put(en.getUnenchanted(), en);
      }
    }

    int unenchanted, enchanted, gradeReq, xpGiven, anim, gfx, reqEnchantmentGrade;

    Enchant(
        int unenchanted,
        int enchanted,
        int gradeReq,
        int xpGiven,
        int anim,
        int gfx,
        int reqEnchantmentGrade) {
      this.unenchanted = unenchanted;
      this.enchanted = enchanted;
      this.gradeReq = gradeReq;
      this.xpGiven = xpGiven;
      this.anim = anim;
      this.gfx = gfx;
      this.reqEnchantmentGrade = reqEnchantmentGrade;
    }

    public static Enchant forId(int itemID) {
      return enc.get(itemID);
    }

    public int getUnenchanted() {
      return unenchanted;
    }

    public int getEnchanted() {
      return enchanted;
    }

    public int getGradeReq() {
      return gradeReq;
    }

    public int getXp() {
      return xpGiven;
    }

    public int getAnim() {
      return anim;
    }

    public int getGFX() {
      return gfx;
    }

    public int getEGrade() {
      return reqEnchantmentGrade;
    }
  }

  private enum EnchantSpell {
    SAPPHIRE(1155, 555, 1, 564, 1, -1, 0),
    EMERALD(1165, 556, 3, 564, 1, -1, 0),
    RUBY(1176, 554, 5, 564, 1, -1, 0),
    DIAMOND(1180, 557, 10, 564, 1, -1, 0),
    DRAGONSTONE(1187, 555, 15, 557, 15, 564, 1),
    ONYX(6003, 557, 20, 554, 20, 564, 1);
    public static final Map<Integer, EnchantSpell> ens = new HashMap<Integer, EnchantSpell>();

    static {
      for (EnchantSpell en : EnchantSpell.values()) {
        ens.put(en.getSpell(), en);
      }
    }

    int spell, reqRune1, reqAmtRune1, reqRune2, reqAmtRune2, reqRune3, reqAmtRune3;

    EnchantSpell(
        int spell,
        int reqRune1,
        int reqAmtRune1,
        int reqRune2,
        int reqAmtRune2,
        int reqRune3,
        int reqAmtRune3) {
      this.spell = spell;
      this.reqRune1 = reqRune1;
      this.reqAmtRune1 = reqAmtRune1;
      this.reqRune2 = reqRune2;
      this.reqAmtRune2 = reqAmtRune2;
      this.reqRune3 = reqRune3;
      this.reqAmtRune3 = reqAmtRune3;
    }

    public static EnchantSpell forId(int id) {
      return ens.get(id);
    }

    public int getSpell() {
      return spell;
    }

    public int getReq1() {
      return reqRune1;
    }

    public int getReqAmt1() {
      return reqAmtRune1;
    }

    public int getReq2() {
      return reqRune2;
    }

    public int getReqAmt2() {
      return reqAmtRune2;
    }

    public int getReq3() {
      return reqRune3;
    }

    public int getReqAmt3() {
      return reqAmtRune3;
    }
  }
}
