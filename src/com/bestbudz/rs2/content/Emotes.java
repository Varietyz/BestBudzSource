package com.bestbudz.rs2.content;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class Emotes {

  private static final Map<Integer, ProfessionCapeEmote> capeEmotes =
      new HashMap<Integer, ProfessionCapeEmote>();

  public static boolean clickButton(Stoner stoner, int id) {
    if (id == 74108) {
      handleProfessionCapeEmote(stoner);
      return true;
    }

    if (stoner.getEquipment().isWearingItem(10394)) {
      if (id == 166) {
        stoner.getUpdateFlags().sendAnimation(new Animation(5316));
        return true;
      }
    }

    for (Emote i : Emote.values()) {
      if (i.buttonID == id) {
        if (i.animID != 1) {
          ClueScrollManager.SINGLETON.handleEmote(stoner, i);
          stoner.getUpdateFlags().sendAnimation(new Animation(i.animID));
        }
        if (i.gfxID != 1) stoner.getUpdateFlags().sendGraphic(Graphic.lowGraphic(i.gfxID, 0));
        return true;
      }
    }

    return false;
  }

  public static final void declare() {
    for (int i = 0; i < 20145; i++) {
      ItemDefinition def = GameDefinitionLoader.getItemDef(i);
      if ((def != null) && (def.getName() != null)) {
        String name = def.getName();

        if ((name.contains("Assault cape")) || (name.contains("Assault Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4959, 823));
        else if ((name.contains("Aegis cape")) || (name.contains("Aegis Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4961, 824));
        else if ((name.contains("Vigour cape")) || (name.contains("Vigour Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4981, 824));
        else if ((name.contains("Life cape")) || (name.contains("Life Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4971, 833));
        else if ((name.contains("Ranging cape")) || (name.contains("Sagittarius Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4973, 832));
        else if ((name.contains("Mage cape")) || (name.contains("Mage Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4939, 813));
        else if ((name.contains("Necromance cape")) || (name.contains("Necromance Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4979, 829));
        else if ((name.contains("Foodie cape")) || (name.contains("Foodie Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4955, 821));
        else if ((name.contains("Lumber. cape"))
            || (name.contains("Lumbering cape"))
            || (name.contains("Lumbering Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4957, 822));
        else if ((name.contains("Woodcarving cape")) || (name.contains("Woodcarving Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4937, 812));
        else if ((name.contains("Fisher cape")) || (name.contains("Fisher Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4951, 819));
        else if ((name.contains("Pyromaniac cape")) || (name.contains("Pyromaniac Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4975, 831));
        else if ((name.contains("Handiness cape")) || (name.contains("Handiness Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4949, 818));
        else if ((name.contains("Forging cape")) || (name.contains("Forging Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4943, 815));
        else if ((name.contains("Quarrying cape")) || (name.contains("Quarrying Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4941, 814));
        else if ((name.contains("THC-hempistry cape"))
            || (name.contains("THC-hempistry Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4969, 835));
        else if ((name.contains("Weedsmoking cape")) || (name.contains("Weedsmoking Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4977, 830));
        else if ((name.contains("Accomplisher cape"))
            || (name.contains("Accomplisher Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4965, 826));
        else if ((name.contains("Mercenary cape")) || (name.contains("Mercenary Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4967, 827));
        else if ((name.contains("BankStanding cape")) || (name.contains("BankStanding Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4963, 825));
        else if ((name.contains("Consumer cape")) || (name.contains("Consumer Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4947, 817));
        else if ((name.contains("Construct. cape")) || (name.contains("Construction Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4953, 820));
        else if ((name.contains("Summoning cape")) || (name.contains("Summoning Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(921, 281));
        else if ((name.contains("Hunter cape")) || (name.contains("Hunter Master Cape")))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(5158, 907));
        else if (name.contains("Quest point cape"))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(4945, 816));
        else if (name.contains("Achievement diary cape"))
          capeEmotes.put(Integer.valueOf(i), new ProfessionCapeEmote(2709, 323));
      }
    }
  }

  public static void handleProfessionCapeEmote(Stoner stoner) {
    Item cape = stoner.getEquipment().getItems()[1];

    if (stoner.getCombat().inCombat()) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You can't perform emotes whilst in combat."));
    }

    if (cape == null) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You need to be wearing a professioncape to do this."));
      return;
    }

    ProfessionCapeEmote emote = capeEmotes.get(Integer.valueOf(cape.getId()));

    if (emote != null) {
      stoner.getUpdateFlags().sendAnimation(emote.getAnim(), 0);
      stoner.getUpdateFlags().sendGraphic(Graphic.lowGraphic(emote.getGfx(), 0));
      AchievementHandler.activateAchievement(stoner, AchievementList.DO_A_PROFESSIONCAPE_EMOTE, 1);
    } else {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You must be wearing a professioncape to do this."));
    }
  }

  public static void onLogin(Stoner p) {
    for (int i = 744; i <= 760; i++) p.getClient().queueOutgoingPacket(new SendConfig(i, 1));
  }

  public enum Emote {
    Yes(168, 855, -1),
    No(169, 856, -1),
    Bow(164, 858, -1),
    Angry(167, 864, -1),
    Think(162, 857, -1),
    Wave(163, 863, -1),
    Shrug(52058, 2113, -1),
    Cheer(171, 862, -1),
    Beckon(165, 859, -1),
    Laugh(170, 861, -1),
    Jump_For_Joy(52054, 2109, -1),
    Yawn(52056, 2111, -1),
    Dance(166, 866, -1),
    Jig(52051, 2106, -1),
    Twirl(52052, 2107, -1),
    Headbang(52053, 2108, -1),
    Cry(161, 860, -1),
    Blow_Kiss(43092, 1368, 574),
    Panic(52050, 2105, -1),
    Rasberry(52055, 2110, -1),
    Clap(172, 865, -1),
    Salute(52057, 2112, -1),
    Goblin_Bow(52071, 2127, -1),
    Goblin_Salute(52072, 2128, -1),
    Glass_Box(2155, 1131, -1),
    Climb_Rope(25103, 1130, -1),
    Lean(25106, 1129, -1),
    Glass_Wall(2154, 1128, -1),
    Idea(88060, 4276, 712),
    Stomp(88061, 4278, -1),
    Flap(88062, 4280, -1),
    Slap_Head(88063, 4275, -1),
    Zombie_Walk(72032, 3544, -1),
    Zombie_Dance(72033, 3543, -1),
    Zombie_Hand(88065, 7272, 1244),
    Scared(59062, 2836, -1),
    Bunny_Hop(72254, 6111, -1),
    Professioncape(154, 1, 1),
    AIR_GUITAR(88059, 2414, 1537);

    public final int gfxID;
    public final int animID;
    public final int buttonID;

    Emote(int buttonId, int animId, int gfxId) {
      buttonID = buttonId;
      animID = animId;
      gfxID = gfxId;
    }
  }

  private static class ProfessionCapeEmote {
    private final int anim;
    private final int gfx;

    public ProfessionCapeEmote(int anim, int gfx) {
      this.anim = anim;
      this.gfx = gfx;
    }

    public int getAnim() {
      return anim;
    }

    public int getGfx() {
      return gfx;
    }
  }
}
