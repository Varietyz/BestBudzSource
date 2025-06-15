package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.util.ItemNames;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.logger.StonerLogger;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class StonerDrops {

  public static final void dropItemsOnDeath(Stoner stoner) {

    final Item[] FUN_PK_REWARDS = {
      new Item(ItemNames.SEERS_RING_ENCHANTED),
      new Item(ItemNames.ARCHER_RING_ENCHANTED),
      new Item(ItemNames.WARRIOR_RING_ENCHANTED),
      new Item(ItemNames.BERSERKER_RING_ENCHANTED),
      new Item(ItemNames.KRAKEN_TENTACLE),
      new Item(ItemNames.OCCULT_NECKLACE),
      new Item(ItemNames.BANDOS_BOOTS_WORK),
      new Item(ItemNames.DRACONIC_VISAGE),
      new Item(ItemNames.DCLAWS),
      new Item(ItemNames.SARA_SWORD),
      new Item(ItemNames.ZAMORAK_PLATEBODY),
      new Item(ItemNames.ADAMANT_PLATEBODY),
      new Item(ItemNames.ADAMANT_PLATELEGS),
      new Item(ItemNames.ADAMANT_FULL_HELM),
      new Item(ItemNames.ZAMORAK_PLATELEGS),
      new Item(ItemNames.SARADOMIN_PLATEBODY),
      new Item(ItemNames.SARADOMIN_PLATELEGS),
      new Item(ItemNames.GUTHIX_PLATEBODY),
      new Item(ItemNames.GUTHIX_PLATELEGS),
      new Item(ItemNames.ZAMORAK_FULL_HELM),
      new Item(ItemNames.GUTHIX_FULL_HELM),
      new Item(ItemNames.SARADOMIN_FULL_HELM),
      new Item(ItemNames.DRAGON_MED_HELM),
      new Item(ItemNames.DRAGON_PLATESKIRT),
      new Item(ItemNames.DRAGON_SQ_SHIELD),
      new Item(ItemNames.HELM_OF_NEITIZNOT),
      new Item(ItemNames.WARRIOR_HELM),
      new Item(ItemNames.ARCHER_HELM),
      new Item(ItemNames.FARSEER_HELM),
      new Item(ItemNames.RUNE_SCIMITAR),
      new Item(ItemNames.RUNE_2H_SWORD),
      new Item(ItemNames.DRAGON_DAGGER),
      new Item(ItemNames.DRAGON_SCIMITAR),
      new Item(ItemNames.DRAGON_MACE),
      new Item(ItemNames.DRAGON_BATTLEAXE),
      new Item(ItemNames.DRAGON_LONGSWORD),
      new Item(ItemNames.TZHAARKETEM),
      new Item(ItemNames.TZHAARKETOM),
      new Item(ItemNames.ADAMANT_SCIMITAR),
      new Item(ItemNames.ADAMANT_2H_SWORD),
      new Item(ItemNames.RUNE_PICKAXE),
      new Item(ItemNames.DRAGON_2H_SWORD),
      new Item(ItemNames.RUNE_FULL_HELM),
      new Item(ItemNames.RUNE_PLATEBODY),
      new Item(ItemNames.RUNE_KITESHIELD),
      new Item(ItemNames.RUNE_PLATELEGS),
      new Item(ItemNames.RUNE_PLATESKIRT),
      new Item(ItemNames.RUNE_BOOTS),
      new Item(ItemNames.RUNE_CHAINBODY),
      new Item(ItemNames.RUNE_CROSSBOW),
      new Item(ItemNames.RING_OF_RECOIL),
      new Item(ItemNames.RAW_SWORDFISH),
      new Item(ItemNames.RUNE_BAR),
      new Item(ItemNames.RUNITE_ORE),
      new Item(ItemNames.UNTRIMMED_GORILLA_GLUE),
      new Item(ItemNames.UNTRIMMED_AMNESIA),
      new Item(ItemNames.UNTRIMMED_GIRL_SCOUT_COOKIES),
      new Item(ItemNames.UNTRIMMED_KUSH),
      new Item(ItemNames.UNTRIMMED_POWERPLANT),
      new Item(ItemNames.UNTRIMMED_CHOCOLOPE),
      new Item(ItemNames.UNCUT_DIAMOND),
      new Item(ItemNames.UNCUT_EMERALD),
      new Item(ItemNames.UNCUT_RUBY),
      new Item(ItemNames.UNCUT_SAPPHIRE),
      new Item(ItemNames.BONES),
      new Item(ItemNames.SPINACH_ROLL),
      new Item(ItemNames.TAN_CAVALIER),
      new Item(ItemNames.DARK_CAVALIER),
      new Item(ItemNames.BLACK_CAVALIER),
      new Item(ItemNames.BLACK_BERET),
      new Item(ItemNames.RED_HEADBAND),
      new Item(ItemNames.PIRATES_HAT),
      new Item(ItemNames.BROWN_HEADBAND),
      new Item(ItemNames.SHARK),
      new Item(ItemNames.MONKEY_NUTS),
      new Item(ItemNames.EYE_PATCH),
      new Item(ItemNames.AHRIMS_HOOD),
      new Item(ItemNames.AHRIMS_STAFF),
      new Item(ItemNames.AHRIMS_ROBETOP),
      new Item(ItemNames.AHRIMS_ROBESKIRT),
      new Item(ItemNames.DHAROKS_HELM),
      new Item(ItemNames.DHAROKS_GREATAXE),
      new Item(ItemNames.DHAROKS_PLATEBODY),
      new Item(ItemNames.DHAROKS_PLATELEGS),
      new Item(ItemNames.GUTHANS_HELM),
      new Item(ItemNames.GUTHANS_WARSPEAR),
      new Item(ItemNames.GUTHANS_PLATEBODY),
      new Item(ItemNames.GUTHANS_CHAINSKIRT),
      new Item(ItemNames.KARILS_COIF),
      new Item(ItemNames.KARILS_CROSSBOW),
      new Item(ItemNames.KARILS_TOP),
      new Item(ItemNames.KARILS_SKIRT),
      new Item(ItemNames.BOLT_RACK),
      new Item(ItemNames.TORAGS_HELM),
      new Item(ItemNames.TORAGS_HAMMERS),
      new Item(ItemNames.TORAGS_PLATEBODY),
      new Item(ItemNames.TORAGS_PLATELEGS),
      new Item(ItemNames.VERACS_HELM),
      new Item(ItemNames.VERACS_FLAIL),
      new Item(ItemNames.VERACS_BRASSARD),
      new Item(ItemNames.VERACS_PLATESKIRT)
    };

    Entity killer = stoner.getCombat().getDamageTracker().getKiller();

    if (killer != null && !killer.isNpc()) {
      Item weapon = killer.getStoner().getEquipment().getItems()[3];
      if (weapon == null) {
        weapon = new Item(0);
      }
      Utility.formatStonerName(stoner.getStoner().getUsername());
      killer.getStoner().setRogueKills(killer.getStoner().getRogueKills() + 1);

        AchievementHandler.activateAchievement(killer.getStoner(), AchievementList.WIN_20_DUELS, 1);

        if (killer.getStoner().targetName.equals(stoner.getUsername())) {}

        killer.getStoner().setHunterKills(killer.getStoner().getHunterKills() + 1);
        if (killer.getStoner().getHunterKills() > killer.getStoner().getHunterRecord()) {
          killer.getStoner().setHunterRecord(killer.getStoner().getHunterKills());
        }
        killer.getStoner().send(new SendMessage("You smoked the stoner target."));
        killer.getStoner().getBox().add(new Item(Utility.randomElement(FUN_PK_REWARDS)));
        killer.getStoner().getBox().add(new Item(Utility.randomElement(FUN_PK_REWARDS)));
        killer.getStoner().getBox().update();
      }
    }
  }

