package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.Advance;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.EasterRing;
import com.bestbudz.rs2.content.Emotes;
import com.bestbudz.rs2.content.GenieLamp;
import com.bestbudz.rs2.content.GenieReset;
import com.bestbudz.rs2.content.LoyaltyShop;
import com.bestbudz.rs2.content.ProfessionsChat;
import com.bestbudz.rs2.content.StarterKit;
import com.bestbudz.rs2.content.StonersOnline;
import com.bestbudz.rs2.content.TeleportHandler;
import com.bestbudz.rs2.content.achievements.AchievementButtons;
import com.bestbudz.rs2.content.combat.formula.MageFormulas;
import com.bestbudz.rs2.content.combat.formula.MeleeFormulas;
import com.bestbudz.rs2.content.combat.formula.RangeFormulas;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.dialogue.impl.AchievementDialogue;
import com.bestbudz.rs2.content.dialogue.impl.DunceDialogue;
import com.bestbudz.rs2.content.dialogue.impl.GenieResetDialogue;
import com.bestbudz.rs2.content.dialogue.impl.NeiveDialogue;
import com.bestbudz.rs2.content.dialogue.impl.OziachDialogue;
import com.bestbudz.rs2.content.dialogue.impl.SailorDialogue;
import com.bestbudz.rs2.content.dialogue.impl.StaffTitleDialogue;
import com.bestbudz.rs2.content.dialogue.impl.Tutorial;
import com.bestbudz.rs2.content.dialogue.impl.VannakaDialogue;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.AchievementTab;
import com.bestbudz.rs2.content.interfaces.impl.BossInterface;
import com.bestbudz.rs2.content.interfaces.impl.MinigameInterface;
import com.bestbudz.rs2.content.interfaces.impl.OtherInterface;
import com.bestbudz.rs2.content.interfaces.impl.PointsInterface;
import com.bestbudz.rs2.content.interfaces.impl.ProfessioningInterface;
import com.bestbudz.rs2.content.interfaces.impl.PvPInterface;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.interfaces.impl.TrainingInterface;
import com.bestbudz.rs2.content.membership.CreditHandler;
import com.bestbudz.rs2.content.membership.MysteryBoxMinigame;
import com.bestbudz.rs2.content.minigames.duelarena.DuelingConstants;
import com.bestbudz.rs2.content.profession.ProfessionGoal;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.foodie.FoodieTask;
import com.bestbudz.rs2.content.profession.forging.ForgingConstants;
import com.bestbudz.rs2.content.profession.handiness.Handiness;
import com.bestbudz.rs2.content.profession.handiness.HideTanning;
import com.bestbudz.rs2.content.profession.handiness.JewelryCreationTask;
import com.bestbudz.rs2.content.profession.mage.Autocast;
import com.bestbudz.rs2.content.profession.mage.MageProfession.SpellBookTypes;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.content.profession.mage.spells.BoltEnchanting;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.profession.summoning.SummoningCreation;
import com.bestbudz.rs2.content.profession.thchempistry.PotionDecanting;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryFinishedPotionTask;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryUnfinishedPotionTask;
import com.bestbudz.rs2.content.profession.woodcarving.Woodcarving;
import com.bestbudz.rs2.content.profiles.ProfileLeaderboard;
import com.bestbudz.rs2.content.profiles.StonerProfiler;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.ReportHandler;
import com.bestbudz.rs2.entity.ReportHandler.ReportData;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendOpenTab;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClickButtonPacket extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 5;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    int buttonId = in.readShort();
    in.reset();
    buttonId = Utility.hexToInt(in.readBytes(2));

    if (stoner.isStunned()) {
      return;
    }

    if (stoner.getNecromance().clickButton(buttonId)) {
      return;
    }

    if (StonerConstants.isOwner(stoner)) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("@red@Developer - button: " + buttonId));
      System.out.println("button: " + buttonId);
    }

    if (stoner.getAttributes().get("DROPTABLE_SEARCH") != null) {
      @SuppressWarnings("unchecked")
      HashMap<Integer, Integer> searchButtons =
          (HashMap<Integer, Integer>) stoner.getAttributes().get("DROPTABLE_SEARCH");
      if (searchButtons.containsKey(buttonId)) {
        DropTable.displayNpc(stoner, searchButtons.get(buttonId));
      }
    }

    if (ReportData.get(buttonId) != null) {
      stoner.reportClicked = buttonId;
      return;
    }

    if ((stoner.getController().equals(Tutorial.TUTORIAL_CONTROLLER))
        && (stoner.getDialogue() != null)) {
      stoner.getDialogue().clickButton(buttonId);
      if (stoner.getInterfaceManager().getMain() != 51750) {
        return;
      }
    }

    if ((stoner.getController().equals(EasterRing.EASTER_RING_CONTROLLER)) && (buttonId == 23132)) {
      EasterRing.cancel(stoner);
      return;
    }

    if (LoyaltyShop.handleButtons(stoner, buttonId)) {
      return;
    }

    if (StonerConstants.isSettingAppearance(stoner)) {
      return;
    }

    if (StarterKit.handle(stoner, buttonId)) {
      return;
    }

    if ((stoner.isDead()) || (!stoner.getController().canClick())) {
      return;
    }

    if (TeleportHandler.selection(stoner, buttonId)) {
      return;
    }

    if (ProfessionGoal.handle(stoner, buttonId)) {
      return;
    }

    if (Advance.handleActionButtons(stoner, buttonId)) {
      return;
    }

    switch (buttonId) {
      case 114229:
        if (StonerConstants.isStaff(stoner)) {
          String accessibility = "";
          if (StonerConstants.isModerator(stoner)) {
            accessibility = "You have access to a few commands!";
          } else if (StonerConstants.isAdministrator(stoner)) {
            accessibility = "You have access to most commands!";
          } else if (StonerConstants.isOwner(stoner)) {
            accessibility = "You have access to all commands!";
          }
          stoner.send(new SendString(accessibility, 49704));
          stoner.send(
              new SendString(
                  "</col>Rank: "
                      + stoner.deterquarryIcon(stoner)
                      + " "
                      + stoner.deterquarryRank(stoner),
                  49705));
          stoner.send(new SendSidebarInterface(2, 49700));
          stoner.send(new SendOpenTab(2));
          stoner.send(new SendMessage("<col=25236>Consequences upon abuse."));
        }
        break;
      case 194042:
        if (StonerConstants.isStaff(stoner)) {
          InterfaceHandler.writeText(new QuestTab(stoner));
          stoner.send(new SendSidebarInterface(2, 29400));
          stoner.send(new SendOpenTab(2));
        }
        break;
      case 66108:
        MysteryBoxMinigame.play(stoner);
        break;
      case 233110:
        DropTable.displayNpc(stoner, stoner.monsterSelected);
        break;
      case 50235:
      case 4140:
        InterfaceHandler.writeText(new TrainingInterface(stoner));
        stoner.send(new SendInterface(61000));
        break;
      case 50245:
      case 4143:
        InterfaceHandler.writeText(new ProfessioningInterface(stoner));
        stoner.send(new SendInterface(62000));
        break;
      case 50253:
      case 4146:
        InterfaceHandler.writeText(new PvPInterface(stoner));
        stoner.send(new SendInterface(63000));
        break;
      case 51005:
      case 4150:
        InterfaceHandler.writeText(new BossInterface(stoner));
        stoner.send(new SendInterface(64000));
        break;
      case 51013:
      case 6004:
        InterfaceHandler.writeText(new MinigameInterface(stoner));
        stoner.send(new SendInterface(65000));
        break;
      case 51023:
      case 6005:
        InterfaceHandler.writeText(new OtherInterface(stoner));
        stoner.send(new SendInterface(61500));
        break;
      case 163046:
        ReportHandler.handleReport(stoner);
        break;
      case 2094:
        stoner.send(new SendRemoveInterfaces());
        stoner.reportClicked = 0;
        stoner.reportName = "";
        stoner.send(new SendInterface(41750));
        break;
      case 75007:
        BoltEnchanting.open(stoner);
        break;
      case 117048:
      case 75010:
      case 84237:
        if (stoner.getMage().isTeleporting()) {
          return;
        }
        if (stoner.inJailed()) {
          return;
        }
        stoner
            .getMage()
            .teleport(
                StonerConstants.HOME.getX(),
                StonerConstants.HOME.getY(),
                StonerConstants.HOME.getZ(),
                TeleportTypes.SPELL_BOOK);
        stoner.send(new SendMessage("Home weed home. " + stoner.deterquarryIcon(stoner)));
        break;
      case 55095:
        if (stoner.getAttributes().getInt("ASK_KEY") == 0) {
          ToxicBlowpipe.unload(stoner);
        } else if (stoner.getAttributes().getInt("ASK_KEY") == 1) {
          TridentOfTheSeas.unload(stoner);
          stoner.getBox().remove(11908, 1);
          stoner.getGroundItems().drop(new Item(11908), stoner.getLocation());
        }
        stoner.send(new SendRemoveInterfaces());
        return;
      case 2202:
        stoner.send(
            new SendMessage(
                "You have <col=255>"
                    + Utility.format(stoner.getMoneyPouch())
                    + " </col>BestBucks on your Debit Card."));
        break;
      case 2203:
        stoner.start(
            new OptionDialogue(
                "Pay by Cash",
                p -> {
                  stoner.setPouchPayment(false);
                  stoner.send(new SendRemoveInterfaces());
                  stoner.send(new SendMessage("You will now be paying with your Cash."));
                },
                "Pay by Debit",
                p -> {
                  stoner.setPouchPayment(true);
                  stoner.send(new SendRemoveInterfaces());
                  stoner.send(new SendMessage("You will now be paying with your Debit Card."));
                }));
        break;
      case 15062:
      case 55096:
      case 190116:
      case 184163:
        stoner.send(new SendRemoveInterfaces());
        break;
      case 59097:
        stoner.send(
            new SendString(
                "</col>Melee Max Hit: @gre@" + MeleeFormulas.calculateBaseDamage(stoner), 15116));
        stoner.send(
            new SendString(
                "</col>Range Max Hit: @gre@" + RangeFormulas.getSagittariusMaxHit(stoner) + ".0",
                15117));
        stoner.send(
            new SendString(
                "</col>Mage Max Hit: @gre@" + MageFormulas.mageMaxHit(stoner) + ".0", 15118));
        stoner.send(new SendInterface(15106));
        break;
      case 140186:
        stoner.send(new SendMessage(":updateSettings:"));
        stoner.send(new SendSidebarInterface(11, 28400));
        stoner.send(new SendOpenTab(11));
        break;
      case 110245:
        stoner.send(new SendMessage(":saveSettings:"));
        stoner.send(new SendSidebarInterface(11, 904));
        stoner.send(new SendOpenTab(11));
        stoner.send(new SendMessage("@gre@Your settings have been saved!"));
        break;
      case 110248:
        stoner.send(new SendMessage(":defaultSettings:"));
        stoner.send(new SendMessage("@yel@Your settings have been reset!"));
        break;
      case 140185:
        stoner.send(new SendInterface(28200));
        break;
      case 140189:
        stoner.send(new SendInterface(37500));
        stoner.send(new SendString("Color chosen: @or2@-", 37506));
        break;
      case 110046:
        stoner.send(new SendMessage(":transparentTab:"));
        break;
      case 110047:
        stoner.send(new SendMessage(":transparentChatbox:"));
        break;
      case 110048:
        stoner.send(new SendMessage(":sideStones:"));
        break;
      case 111024:
        if (stoner.getDelay().elapsed() < 3_000) {
          stoner.send(new SendMessage("YO CHILL! Wait before doing this again!"));
          return;
        }
        if (stoner.isAdvanceColors()) {
          stoner.setAdvanceColors(false);
          stoner.send(new SendMessage(":advanceColorsFalse:"));
          stoner.getProfession().resetColors();
          stoner.send(new SendMessage("Advance colors will not display in profession tab."));
        } else {
          stoner.setAdvanceColors(true);
          stoner.send(new SendMessage(":advanceColorsTrue:"));
          stoner.getProfession().resetColors();
          stoner.send(new SendMessage("Advance colors will now display in profession tab."));
        }
        stoner.getDelay().reset();
        break;
      case 201051:
      case 201053:
        stoner.send(new SendConfig(1032, 1));
        stoner.setProfilePrivacy(true);
        stoner.send(new SendMessage("@dre@You have hidden yourself in some bushes."));
        break;
      case 201052:
      case 201054:
        stoner.send(new SendConfig(1032, 2));
        stoner.setProfilePrivacy(false);
        stoner.send(new SendMessage("@dre@You jumped out of the bushes."));
        break;
      case 201055:
        StonerProfiler.myProfile(stoner);
        break;
      case 201059:
      case 185046:
        ProfileLeaderboard.open(stoner, "Look at man");
        break;
      case 185049:
        ProfileLeaderboard.open(stoner, "A good man");
        break;
      case 185052:
        ProfileLeaderboard.open(stoner, "A bad man");
        break;
      case 185055:
        ProfileLeaderboard.open(stoner, "What kind of man");
        break;
      case 59103:
        stoner.getPriceChecker().open();
        break;
      case 189121:
        stoner.getPriceChecker().depositeAll();
        break;
      case 189194:
        stoner.getPriceChecker().withdrawAll();
        break;
      case 189124:
        stoner.send(new SendMessage("Dont even ask.."));
        break;
      case 59206:
        stoner.start(
            new OptionDialogue(
                "Lock experience",
                p -> {
                  stoner.getProfession().setExpLock(true);
                  stoner.send(new SendMessage("You have @blu@locked</col> your experience."));
                  stoner.send(new SendRemoveInterfaces());
                },
                "Unlock experience",
                p -> {
                  stoner.getProfession().setExpLock(false);
                  stoner.send(new SendMessage("You have @blu@unlocked</col> your experience."));
                  stoner.send(new SendRemoveInterfaces());
                }));
        break;
      case 195087:
        stoner.send(new SendInterface(32500));
        break;
      case 127000:
        stoner.send(new SendInterface(5292));
        break;
      case 209002:
        stoner.start(
            new OptionDialogue(
                "Search name",
                p -> {
                  stoner.setEnterXInterfaceId(55777);
                  stoner.getClient().queueOutgoingPacket(new SendEnterString());
                },
                "Search item",
                p -> {
                  stoner.setEnterXInterfaceId(55778);
                  stoner.getClient().queueOutgoingPacket(new SendEnterString());
                }));
        break;
      case 114220:
        InterfaceHandler.writeText(new AchievementTab(stoner));
        stoner.send(new SendSidebarInterface(2, 31000));
        stoner.send(new SendMessage("@gre@Redirected to the Achievements Tab."));
        break;
      case 121028:
        InterfaceHandler.writeText(new QuestTab(stoner));
        stoner.send(new SendSidebarInterface(2, 29400));
        stoner.send(new SendMessage("@gre@Redirected to the BestBudz Tab."));
        break;
      case 114226:
        InterfaceHandler.writeText(new QuestTab(stoner));
        break;
      case 115070:
        stoner.send(new SendString("@gre@" + stoner.getUsername() + "'s tracked points.", 8144));
        InterfaceHandler.writeText(new PointsInterface(stoner));
        stoner.send(new SendInterface(8134));
        break;

      case 115071:
        int linePosition = 8145;
        HashMap<String, Integer> map = stoner.getProperties().getPropertyValues("MOB");

        List<String> alphabetical = new ArrayList<>();
        alphabetical.addAll(map.keySet());
        alphabetical.sort(String.CASE_INSENSITIVE_ORDER);

        for (String key : alphabetical) {
          String line =
              Utility.formatStonerName(key.toLowerCase().replaceAll("_", " "))
                  + ": @gre@"
                  + map.get(key);
          stoner.send(new SendString("@gre@PvM Tracker | " + alphabetical.size(), 8144));
          stoner.send(new SendString("</col>" + line, linePosition++));
        }

        map = stoner.getProperties().getPropertyValues("BARROWS");
        for (String key : map.keySet()) {
          String line =
              Utility.formatStonerName(key.toLowerCase().replaceAll("_", " "))
                  + ": @gre@"
                  + map.get(key);
          stoner.send(new SendString("</col>" + line, linePosition++));
        }

        while (linePosition < 8193) {
          stoner.send(new SendString("", linePosition++));
        }

        stoner.send(new SendInterface(8134));
        break;
      case 115075:
        stoner.start(
            new OptionDialogue(
                "Focused Mage",
                p -> {
                  stoner.getMage().setSpellBookType(SpellBookTypes.MODERN);
                  stoner.getMage().setMageBook(1151);
                  stoner.getUpdateFlags().sendAnimation(new Animation(6299));
                  stoner.getUpdateFlags().sendGraphic(new Graphic(1062));
                  stoner.send(new SendMessage("You are now a focused mage."));
                  stoner.send(new SendRemoveInterfaces());
                },
                "AoE Mage",
                p -> {
                  stoner.getMage().setSpellBookType(SpellBookTypes.ANCIENT);
                  stoner.getMage().setMageBook(12855);
                  stoner.getUpdateFlags().sendAnimation(new Animation(6299));
                  stoner.getUpdateFlags().sendGraphic(new Graphic(1062));
                  stoner.send(new SendMessage("You are now a AoE mage."));
                  stoner.send(new SendRemoveInterfaces());
                }));
        break;

      case 115076:
        DropTable.open(stoner);
        break;

      case 115077:
        Advance.update(stoner);
        stoner.send(new SendInterface(51000));
        break;

      case 115078:
      case 115082:
        stoner.send(new SendMessage("@gre@You have opened General shop."));
        stoner.getShopping().open(0);
        break;

      case 115083:
        stoner.send(new SendMessage("@gre@You have opened Packs shop."));
        stoner.getShopping().open(31);
        break;

      case 115084:
        stoner.send(new SendMessage("@gre@You have opened Professioning shop."));
        stoner.getShopping().open(17);
        break;

      case 115085:
        stoner.send(new SendMessage("@gre@You have opened Cultivation shop."));
        stoner.getShopping().open(32);
        break;

      case 115086:
        stoner.send(new SendMessage("@gre@You have opened THC-hempistry shop."));
        stoner.getShopping().open(33);
        break;

      case 115087:
        stoner.send(new SendMessage("@gre@You have opened Close combat shop."));
        stoner.getShopping().open(15);
        break;

      case 115088:
        stoner.send(new SendMessage("@gre@You have opened Sagittarius's shop."));
        stoner.getShopping().open(16);
        break;

      case 115089:
        stoner.send(new SendMessage("@gre@You have opened Mages shop."));
        stoner.getShopping().open(26);
        break;

      case 115090:
        stoner.send(new SendMessage("@gre@You have opened Pure shop."));
        stoner.getShopping().open(27);
        break;

      case 115091:
        stoner.start(
            new OptionDialogue(
                "Fashionscape",
                p -> {
                  stoner.getShopping().open(28);
                },
                "Fashionscaper.",
                p -> {
                  stoner.getShopping().open(40);
                }));
        break;

      case 115092:
        stoner.send(new SendMessage("@gre@You have opened Profession cape shop."));
        stoner.getShopping().open(20);
        break;

      case 115093:
        stoner.send(new SendMessage("@gre@You have opened Advance cape shop."));
        stoner.getShopping().open(45);
        break;
      case 115097:
        stoner.send(new SendMessage("@gre@You have opened Chill Point shop."));
        stoner.getShopping().open(92);
        break;

      case 115098:
        stoner.send(new SendMessage("@gre@You have opened Weed protect point shop."));
        stoner.getShopping().open(5);
        break;

      case 115099:
        stoner.send(new SendMessage("@gre@You have opened Graceful shop."));
        stoner.getShopping().open(3);
        break;

      case 115100:
        stoner.send(new SendMessage("@gre@You have opened Achievement shop."));
        stoner.getShopping().open(89);
        break;

      case 115101:
        stoner.send(new SendMessage("@gre@You have opened Advance shop."));
        stoner.getShopping().open(93);
        break;

      case 115102:
        stoner.send(new SendMessage("@gre@You have opened Mercenary shop."));
        stoner.getShopping().open(6);
        break;

      case 115103:
        stoner.send(new SendMessage("@gre@You have opened Bounty shop."));
        stoner.getShopping().open(7);
        break;
      case 115107:
        stoner.start(new OziachDialogue(stoner));
        break;

      case 115108:
        JewelryCreationTask.sendInterface(stoner);
        break;

      case 115109:
        HideTanning.sendTanningInterface(stoner);
        break;

      case 115110:
        PotionDecanting.decantAll(stoner);
        break;
      case 115114:
        if (stoner.inMemberZone()) {
          stoner.start(new DunceDialogue(stoner));
        } else {
          stoner.start(new StaffTitleDialogue(stoner));
        }
        break;

      case 115115:
        stoner.start(new GenieResetDialogue(stoner));
        break;

      case 115116:
        stoner.send(new SendInterface(3559));
        break;

      case 115117:
        if (stoner.getProfession().getGrades()[Professions.NECROMANCE]
            < stoner.getMaxGrades()[Professions.NECROMANCE]) {
          stoner
              .getProfession()
              .setGrade(Professions.NECROMANCE, stoner.getMaxGrades()[Professions.NECROMANCE]);
          stoner
              .getClient()
              .queueOutgoingPacket(new SendMessage("You recharge your necromance points."));
          stoner.getUpdateFlags().sendAnimation(new Animation(5864));
        } else {
          stoner
              .getClient()
              .queueOutgoingPacket(new SendMessage("Your necromance is already full."));
        }
        break;

      case 115118:
        if (stoner.getSkulling().isSkulled()) {
          DialogueManager.sendNpcChat(
              stoner, 315, Emotion.DEFAULT, "You already have a wilderness skull!");
          return;
        } else {
          stoner.getSkulling().skull(stoner, stoner);
          DialogueManager.sendNpcChat(stoner, 315, Emotion.DEFAULT, "You have been skulled.");
          stoner.getUpdateFlags().sendAnimation(new Animation(5315));
          stoner.getUpdateFlags().sendGraphic(new Graphic(1061));
        }
        break;
      case 115122:
        stoner.start(new VannakaDialogue(stoner));
        break;

      case 115123:
        stoner.start(new NeiveDialogue(stoner));
        break;
      case 115127:
        stoner.start(new SailorDialogue(stoner));
        break;

      case 115128:
        MysteryBoxMinigame.open(stoner);
        break;

      case 115129:
        stoner.start(new AchievementDialogue(stoner));
        break;
      case 115133:
        stoner.teleport(new Location(2843, 4832));
        break;

      case 115134:
        stoner.teleport(new Location(2787, 4839));
        break;

      case 115135:
        stoner.teleport(new Location(2718, 4837));
        break;

      case 115136:
        stoner.teleport(new Location(2660, 4840));
        break;

      case 115137:
        stoner.teleport(new Location(2583, 4839));
        break;

      case 115138:
        stoner.teleport(new Location(2524, 4842));
        break;

      case 115139:
        stoner.teleport(new Location(2144, 4833));
        break;

      case 115140:
        stoner.teleport(new Location(2273, 4842));
        break;

      case 115141:
        stoner.teleport(new Location(2400, 4839));
        break;

      case 115142:
        stoner.teleport(new Location(2464, 4830));
        break;

      case 115143:
        stoner.teleport(new Location(2205, 4834));
        break;
      case 115065:
      case 154052:
        StonersOnline.showStoners(
            stoner,
            p -> {
              return true;
            });
        break;
      case 29124:
      case 29049:
      case 29199:
      case 29138:
      case 48034:
      case 155:
      case 30108:
      case 29238:
        stoner.getSpecialAssault().clickSpecialButton(buttonId);
        break;
      case 29074:
        if (stoner.getSpecialAssault().getAmount() != 100) {
          stoner.send(new SendMessage("You lack a bit of spec man."));
          return;
        }
        stoner.getUpdateFlags().sendAnimation(new Animation(1056));
        stoner.getUpdateFlags().sendGraphic(new Graphic(246));
        stoner.getSpecialAssault().deduct(100);
        stoner.getSpecialAssault().update();
        stoner.getSpecialAssault().setInitialized(false);
        stoner.getGrades()[Professions.ASSAULT] =
            (short) (stoner.getMaxGrades()[Professions.ASSAULT] * 0.9);
        stoner.getGrades()[Professions.AEGIS] =
            (short) (stoner.getMaxGrades()[Professions.AEGIS] * 0.9);
        stoner.getGrades()[Professions.SAGITTARIUS] =
            (short) (stoner.getMaxGrades()[Professions.SAGITTARIUS] * 0.9);
        stoner.getGrades()[Professions.MAGE] =
            (short) (stoner.getMaxGrades()[Professions.MAGE] * 0.9);
        stoner.getGrades()[Professions.VIGOUR] =
            (short) (stoner.getMaxGrades()[Professions.VIGOUR] * 1.2);
        stoner.getProfession().update(Professions.ASSAULT);
        stoner.getProfession().update(Professions.AEGIS);
        stoner.getProfession().update(Professions.SAGITTARIUS);
        stoner.getProfession().update(Professions.MAGE);
        stoner.getProfession().update(Professions.VIGOUR);
        stoner.getUpdateFlags().sendForceMessage("Raarrrrrgggggghhhhhhh!");
        break;

      case 155026:
        stoner.getClient().queueOutgoingPacket(new SendInterface(38700));
        break;
      case 151045:
        stoner.getClient().queueOutgoingPacket(new SendInterface(39700));
        break;
      case 9118:
      case 83051:
        stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
        break;
      case 83093:
        stoner.getClient().queueOutgoingPacket(new SendInterface(15106));
        break;
      case 3162:
        stoner.setMusicVolume((byte) 4);
        stoner.getClient().queueOutgoingPacket(new SendConfig(168, 4));
        break;
      case 70209:
        stoner.setEnterXInterfaceId(6969);
        break;
      case 3163:
      case 3164:
      case 3165:
      case 3166:
        stoner.setMusicVolume((byte) (3166 - buttonId));
        stoner.getClient().queueOutgoingPacket(new SendConfig(168, stoner.getMusicVolume()));
        break;
      case 3173:
        stoner.setSoundVolume((byte) 4);
        stoner.getClient().queueOutgoingPacket(new SendConfig(169, 4));
        break;
      case 3174:
      case 3175:
      case 3176:
      case 3177:
        stoner.setSoundVolume((byte) (3177 - buttonId));
        stoner.getClient().queueOutgoingPacket(new SendConfig(169, stoner.getSoundVolume()));
        break;
      case 24125:
        stoner.getAttributes().remove("manual");
        break;
      case 24126:
        stoner.getAttributes().set("manual", Byte.valueOf((byte) 1));
        break;
      case 108005:
        stoner.getClient().queueOutgoingPacket(new SendInterface(19148));
        break;
      case 14067:
        stoner.setAppearanceUpdateRequired(true);
        stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
        break;
      case 9154:
        if (stoner.getCombat().inCombat()) {
          stoner
              .getClient()
              .queueOutgoingPacket(new SendMessage("Nope, we dont flight, we fight!"));
        } else {
          if (stoner.getClient().getStage() == Client.Stages.LOGGED_IN) {
            stoner.logout(false);
          }
        }
        break;

      case 74:
      case 152:
      case 33230:
      case 74214:
        stoner.getRunEnergy().setRunning(!stoner.getRunEnergy().isRunning());
        stoner
            .getClient()
            .queueOutgoingPacket(new SendConfig(173, stoner.getRunEnergy().isRunning() ? 1 : 0));
        break;
      case 211172:
        stoner.getRunEnergy().toggleResting();
        break;

      case 3138:
        stoner.setScreenBrightness((byte) 1);
        break;
      case 3140:
        stoner.setScreenBrightness((byte) 2);
        break;
      case 3142:
        stoner.setScreenBrightness((byte) 3);
        break;
      case 3144:
        stoner.setScreenBrightness((byte) 4);
        break;
      case 100228:
        stoner.setMultipleMouseButtons((byte) (stoner.getMultipleMouseButtons() == 0 ? 1 : 0));
        break;
      case 100231:
        stoner.setChatEffectsEnabled((byte) (stoner.getChatEffectsEnabled() == 0 ? 1 : 0));
        break;
      case 3189:
        stoner.setSplitPrivateChat((byte) (stoner.getSplitPrivateChat() == 0 ? 1 : 0));
        stoner.getClient().queueOutgoingPacket(new SendConfig(287, stoner.getSplitPrivateChat()));
        break;
      case 100237:
        stoner.setAcceptAid((byte) (stoner.getAcceptAid() == 0 ? 1 : 0));
        break;
      case 150:
      case 89061:
      case 93202:
      case 93209:
      case 93217:
      case 93225:
      case 94051:
        stoner.setRetaliate(!stoner.isRetaliate());
        break;
      case 59100:
        int kept = 3;

        if (stoner.getSkulling().isSkulled()) {
          kept = 0;
        }

        if (stoner.getNecromance().active(Necromance.PROTECT_ITEM)) {
          kept++;
        }

        Queue<Item> dropItems = new PriorityQueue<Item>(42);

        for (Item i : stoner.getBox().getItems()) {
          if (i != null) {
            dropItems.add(new Item(i.getId(), i.getAmount()));
          }
        }

        for (Item i : stoner.getEquipment().getItems()) {
          if (i != null) {
            dropItems.add(new Item(i.getId(), i.getAmount()));
          }
        }

        Item dropItem = null;
        Item[] toKeep = new Item[kept];
        int keepIndex = 0;

        for (int i = 0; i < kept; i++) {
          Item keep = dropItems.poll();

          if (keep != null) {
            if (keep.getAmount() == 1) {
              toKeep[keepIndex++] = keep;
            } else {
              keep.remove(1);
              toKeep[keepIndex++] = new Item(keep.getId(), 1);
            }
          }
        }

        Item[] toDrop = new Item[dropItems.size()];
        int dropIndex = 0;

        while ((dropItem = dropItems.poll()) != null) {
          if (dropItem.getDefinition().isTradable()
              || !dropItem.getDefinition().isTradable()
              || ItemCheck.isItemDyedWhip(dropItem)) {
            toDrop[dropIndex++] = dropItem;
          }
        }

        for (int i = 17109; i < 17131; i++) {
          stoner.send(new SendString("", i));
        }

        stoner.send(new SendString("Items you will keep on death:", 17104));
        stoner.send(new SendString("Items you will lose on death:", 17105));
        stoner.send(new SendString("Stoner Information", 17106));
        stoner.send(new SendString("Max items kept on death:", 17107));
        stoner.send(new SendString("~ " + kept + " ~", 17108));
        stoner.send(new SendString("The normal amount of", 17111));
        stoner.send(new SendString("items kept is three.", 17112));
        switch (kept) {
          case 0:
          default:
            stoner.send(new SendString("Items you will keep on death:", 17104));
            stoner.send(new SendString("Items you will lose on death:", 17105));
            stoner.send(new SendString("You're marked with a", 17111));
            stoner.send(new SendString("@red@skull. @lre@This reduces the", 17112));
            stoner.send(new SendString("items you keep from", 17113));
            stoner.send(new SendString("three to zero!", 17114));
            break;
          case 1:
            stoner.send(new SendString("Items you will keep on death:", 17104));
            stoner.send(new SendString("Items you will lose on death:", 17105));
            stoner.send(new SendString("You're marked with a", 17111));
            stoner.send(new SendString("@red@skull. @lre@This reduces the", 17112));
            stoner.send(new SendString("items you keep from", 17113));
            stoner.send(new SendString("three to zero!", 17114));
            stoner.send(new SendString("However, you also have", 17115));
            stoner.send(new SendString("the @red@Protect @lre@Items necromance", 17116));
            stoner.send(new SendString("active, which saves you", 17117));
            stoner.send(new SendString("one extra item!", 17118));
            break;
          case 3:
            stoner.send(new SendString("Items you will keep on death(if not skulled):", 17104));
            stoner.send(new SendString("Items you will lose on death(if not skulled):", 17105));
            stoner.send(new SendString("You have no factors", 17111));
            stoner.send(new SendString("affecting the items you", 17112));
            stoner.send(new SendString("keep.", 17113));
            break;
          case 4:
            stoner.send(new SendString("Items you will keep on death(if not skulled):", 17104));
            stoner.send(new SendString("Items you will lose on death(if not skulled):", 17105));
            stoner.send(new SendString("You have the @red@Protect", 17111));
            stoner.send(new SendString("@red@Item @lre@necromance active,", 17112));
            stoner.send(new SendString("which saves you one", 17113));
            stoner.send(new SendString("extra item!", 17114));
            break;
        }
        stoner.send(new SendString("Carried wealth:", 17121));
        BigInteger carrying =
            stoner.getBox().getContainerNet().add(stoner.getEquipment().getContainerNet());
        if (carrying.equals(BigInteger.ZERO)) {
          stoner.send(new SendString("@red@Nothing!", 17122));
        } else {
          stoner.send(
              new SendString(
                  "@red@"
                      + NumberFormat.getNumberInstance(Locale.US).format(carrying)
                      + "</col> bestbucks.",
                  17122));
        }

        BigInteger risked = BigInteger.ZERO;
        for (Item dropping : toDrop) {
          if (dropping == null || dropping.getDefinition() == null) {
            continue;
          }

          risked =
              risked.add(
                  new BigInteger(String.valueOf(dropping.getDefinition().getGeneralPrice()))
                      .multiply(new BigInteger(String.valueOf(dropping.getAmount()))));
        }

        stoner.send(new SendString("Risked wealth:", 17124));

        if (risked.equals(BigInteger.ZERO)) {
          stoner.send(new SendString("@red@Nothing!", 17125));
        } else {
          stoner.send(
              new SendString(
                  "@red@"
                      + NumberFormat.getNumberInstance(Locale.US).format(risked)
                      + "</col> bestbucks.",
                  17125));
        }

        stoner.send(new SendUpdateItems(10494, toKeep));
        stoner.send(new SendUpdateItems(10600, toDrop));
        stoner.send(new SendInterface(17100));
        break;
      case 238107:
      case 242083:
      case 246059:
      case 250035:
      case 254011:
      case 240095:
        TeleportHandler.teleport(stoner);
        break;

      case 5227:
      case 238077:
      case 242053:
      case 246029:
      case 253237:
      case 240065:
      case 250005:
        InterfaceHandler.writeText(new TrainingInterface(stoner));
        stoner.send(new SendInterface(61000));
        stoner.send(new SendString("Selected: @red@None", 61031));
        stoner.send(new SendString("Cost: @red@Free", 61032));
        stoner.send(new SendString("Requirement: @red@None", 61033));
        stoner.send(new SendString("Other: @red@None", 61034));
        break;
      case 238080:
      case 242056:
      case 246032:
      case 253240:
      case 240068:
      case 250008:
        InterfaceHandler.writeText(new ProfessioningInterface(stoner));
        stoner.send(new SendInterface(62000));
        stoner.send(new SendString("Selected: @red@None", 62031));
        stoner.send(new SendString("Cost: @red@Free", 62032));
        stoner.send(new SendString("Requirement: @red@None", 62033));
        stoner.send(new SendString("Other: @red@None", 62034));
        break;
      case 238083:
      case 242059:
      case 246035:
      case 253243:
      case 240071:
      case 250011:
        InterfaceHandler.writeText(new PvPInterface(stoner));
        stoner.send(new SendInterface(63000));
        stoner.send(new SendString("Selected: @red@None", 63031));
        stoner.send(new SendString("Cost: @red@Free", 63032));
        stoner.send(new SendString("Requirement: @red@None", 63033));
        stoner.send(new SendString("Other: @red@None", 63034));
        break;
      case 238086:
      case 246038:
      case 253246:
      case 240074:
      case 250014:
      case 242062:
        InterfaceHandler.writeText(new BossInterface(stoner));
        stoner.send(new SendInterface(64000));
        stoner.send(new SendString("Selected: @red@None", 64031));
        stoner.send(new SendString("Cost: @red@Free", 64032));
        stoner.send(new SendString("Requirement: @red@None", 64033));
        stoner.send(new SendString("Other: @red@None", 64034));
        break;
      case 238089:
      case 253249:
      case 246041:
      case 240077:
      case 250017:
      case 242065:
        Utility.writeBuffer(stoner.getUsername());
        InterfaceHandler.writeText(new MinigameInterface(stoner));
        stoner.send(new SendInterface(65000));
        stoner.send(new SendString("Selected: @red@None", 65031));
        stoner.send(new SendString("Cost: @red@Free", 65032));
        stoner.send(new SendString("Requirement: @red@None", 65033));
        stoner.send(new SendString("Other: @red@None", 65034));
        break;
      case 238092:
      case 253252:
      case 240080:
      case 250020:
      case 242068:
      case 246044:
        InterfaceHandler.writeText(new OtherInterface(stoner));
        stoner.send(new SendInterface(61500));
        stoner.send(new SendString("Selected: @red@None", 61531));
        stoner.send(new SendString("Cost: @red@Free", 61532));
        stoner.send(new SendString("Requirement: @red@None", 61533));
        stoner.send(new SendString("Other: @red@None", 61534));
        break;

      default:
        if (CreditHandler.handleClicking(stoner, buttonId)) {
          return;
        }
        if (GenieLamp.handle(stoner, buttonId)) {
          return;
        }
        if (GenieReset.handle(stoner, buttonId)) return;
        if (AchievementButtons.handleButtons(stoner, buttonId)) return;
        if (ProfessionsChat.handle(stoner, buttonId)) break;
        if (stoner.getSummoning().click(buttonId)) break;
        if (SummoningCreation.create(stoner, buttonId)) break;
        if (Woodcarving.SINGLETON.clickButton(stoner, buttonId)) break;
        if (com.bestbudz.rs2.content.profession.handinessnew.Handiness.SINGLETON.clickButton(
            stoner, buttonId)) break;
        if (Handiness.handleHandinessByButtons(stoner, buttonId)) break;
        if (HideTanning.clickButton(stoner, buttonId)) break;
        if (FoodieTask.handleFoodieByAmount(stoner, buttonId)) break;
        if ((stoner.getDialogue() != null) && (stoner.getDialogue().clickButton(buttonId))) break;
        if (Autocast.clickButton(stoner, buttonId)) break;
        if (Emotes.clickButton(stoner, buttonId)) break;
        if (DuelingConstants.clickDuelButton(stoner, buttonId)) break;
        if (stoner.getTrade().clickTradeButton(buttonId)) break;
        if (stoner.getBank().clickButton(buttonId)) break;
        if (stoner.getMage().clickMageButtons(buttonId)) break;
        if (EquipmentConstants.clickAssaultStyleButtons(stoner, buttonId)) break;
        if (ForgingConstants.clickSmeltSelection(stoner, buttonId)) break;
        if ((stoner.getAttributes().get("thchempistryitem1") != null)
            && ((((Item) stoner.getAttributes().get("thchempistryitem1")).getId() == 227)
                    || (((Item) stoner.getAttributes().get("thchempistryitem2")).getId() == 227)
                ? !THChempistryUnfinishedPotionTask.handleTHChempistryButtons(stoner, buttonId)
                : !THChempistryFinishedPotionTask.handleTHChempistryButtons(stoner, buttonId)))
          break;
        break;
    }
  }
}
