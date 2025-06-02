package com.bestbudz;

import com.bestbudz.core.cache.map.MapLoading;
import com.bestbudz.core.cache.map.ObjectDef;
import com.bestbudz.core.cache.map.RSInterface;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.Emotes;
import com.bestbudz.rs2.content.FountainOfRune;
import com.bestbudz.rs2.content.Announcement;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager;
import com.bestbudz.rs2.content.combat.impl.PoisonWeapons;
import com.bestbudz.rs2.content.combat.special.SpecialAssaultHandler;
import com.bestbudz.rs2.content.dialogue.OneLineDialogue;
import com.bestbudz.rs2.content.exercisement.Exercisement;
import com.bestbudz.rs2.content.minigames.duelarena.DuelingConstants;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.content.minigames.plunder.PlunderConstants;
import com.bestbudz.rs2.content.minigames.plunder.PyramidPlunder;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.fisher.FishableData;
import com.bestbudz.rs2.content.profession.fisher.Fisher;
import com.bestbudz.rs2.content.profession.fisher.ToolData;
import com.bestbudz.rs2.content.profession.foodie.FoodieData;
import com.bestbudz.rs2.content.profession.forging.SmeltingData;
import com.bestbudz.rs2.content.profession.handiness.Craftable;
import com.bestbudz.rs2.content.profession.handiness.Glass;
import com.bestbudz.rs2.content.profession.handiness.HideTanData;
import com.bestbudz.rs2.content.profession.handiness.Jewelry;
import com.bestbudz.rs2.content.profession.handiness.Spinnable;
import com.bestbudz.rs2.content.profession.handinessnew.craftable.impl.Hide;
import com.bestbudz.rs2.content.profession.thchempistry.FinishedPotionData;
import com.bestbudz.rs2.content.profession.thchempistry.UntrimmedWeedData;
import com.bestbudz.rs2.content.profession.weedsmoking.Weedsmoker;
import com.bestbudz.rs2.content.profession.thchempistry.GrindingData;
import com.bestbudz.rs2.content.profession.thchempistry.UnfinishedPotionData;
import com.bestbudz.rs2.content.profession.lumbering.LumberingAxeData;
import com.bestbudz.rs2.content.profession.mage.MageConstants;
import com.bestbudz.rs2.content.profession.mage.MageEffects;
import com.bestbudz.rs2.content.profession.mercenary.MercenaryMonsters;
import com.bestbudz.rs2.content.profession.necromance.BoneBurying;
import com.bestbudz.rs2.content.profession.pyromaniac.Pyromaniac;
import com.bestbudz.rs2.content.profession.quarrying.Quarrying;
import com.bestbudz.rs2.content.profession.sagittarius.AmmoData;
import com.bestbudz.rs2.content.profession.bankstanding.BankStanding;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl.Arrow;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl.Bolt;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl.Carvable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl.Crossbow;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl.Featherable;
import com.bestbudz.rs2.content.profession.woodcarving.fletchable.impl.Stringable;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.impl.GlobalItemHandler;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobAbilities;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.object.ObjectConstants;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.net.in.PacketHandler;

public class GameDataLoader {

  private static int stage = 0;

  public static void load() {
    try {
      GameDefinitionLoader.declare();
      new Thread() {
        @Override
        public void run() {
          try {
            ObjectDef.loadConfig();
            ObjectConstants.declare();
            MapLoading.load();
            Region.sort();
            GameDefinitionLoader.loadAlternateIds();
            MapLoading.processDoors();
            GameDefinitionLoader.clearAlternates();
            ObjectManager.declare();
            GlobalItemHandler.spawnGroundItems();
            Mob.spawnBosses();
            GameDefinitionLoader.loadNpcSpawns();
          } catch (Exception e) {
            e.printStackTrace();
          }

          GameDataLoader.stage += 1;
        }
      }.start();

      RSInterface.unpack();
      GameDefinitionLoader.loadNpcDefinitions();
      GameDefinitionLoader.loadItemDefinitions();
      GameDefinitionLoader.loadRareDropChances();
      GameDefinitionLoader.loadEquipmentDefinitions();
      GameDefinitionLoader.loadShopDefinitions();
      GameDefinitionLoader.setRequirements();
      GameDefinitionLoader.loadWeaponDefinitions();
      GameDefinitionLoader.loadSpecialAssaultDefinitions();
      GameDefinitionLoader.loadSagittariusVigourDefinitions();
      GameDefinitionLoader.loadSpecialAssaultDefinitions();
      GameDefinitionLoader.loadCombatSpellDefinitions();
      GameDefinitionLoader.loadFoodDefinitions();
      GameDefinitionLoader.loadPotionDefinitions();
      GameDefinitionLoader.loadSagittariusWeaponDefinitions();
      GameDefinitionLoader.loadNpcCombatDefinitions();
      GameDefinitionLoader.loadNpcDropDefinitions();
      GameDefinitionLoader.loadItemBonusDefinitions();
      GodWarsData.declare();
      Quarrying.declare();
      PyramidPlunder.declare();
      PlunderConstants.UrnBitPosition.declare();
      PlunderConstants.DoorBitPosition.declare();
      ClueScrollManager.declare();
      FountainOfRune.declare();
      Exercisement.declare();
      Arrow.declare();
      Bolt.declare();
      Carvable.declare();
      Crossbow.declare();
      Featherable.declare();
      Stringable.declare();
      Craftable.declare();
      HideTanData.declare();
      Jewelry.declare();
      Spinnable.declare();
      com.bestbudz.rs2.content.profession.handinessnew.craftable.impl.Gem.declare();
      Hide.declare();
      BankStanding.declare();
      Shop.declare();
      MageConstants.declare();
      MercenaryMonsters.declare();
      DuelingConstants.declare();
      MobConstants.declare();
      Emotes.declare();
      PoisonWeapons.declare();
      SpecialAssaultHandler.declare();
      FoodieData.declare();
      Glass.declare();
      Weedsmoker.Weed.declare();
      Pyromaniac.Wood.declare();
      FishableData.Fishable.declare();
      Fisher.FisherSpots.declare();
      ToolData.Tools.declare();
      FinishedPotionData.declare();
      UntrimmedWeedData.declare();
      GrindingData.declare();
      UnfinishedPotionData.declare();
      MageEffects.declare();
      BoneBurying.Bones.declare();
      AmmoData.Ammo.declare();
      Professions.declare();
      LumberingAxeData.declare();
      EquipmentConstants.declare();
      PacketHandler.declare();
      MobConstants.MobDissapearDelay.declare();
      MobAbilities.declare();
      SmeltingData.declare();
      OneLineDialogue.declare();
      Announcement.sequence();
      stage += 1;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean loaded() {
    return stage == 2;
  }
}
