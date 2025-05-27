package com.mayhem;

import com.mayhem.core.cache.map.MapLoading;
import com.mayhem.core.cache.map.ObjectDef;
import com.mayhem.core.cache.map.RSInterface;
import com.mayhem.core.cache.map.Region;
import com.mayhem.core.util.FileHandler;
import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.rs2.content.Emotes;
import com.mayhem.rs2.content.FountainOfRune;
import com.mayhem.rs2.content.GlobalMessages;
import com.mayhem.rs2.content.cluescroll.ClueScrollManager;
import com.mayhem.rs2.content.combat.impl.PoisonWeapons;
import com.mayhem.rs2.content.combat.special.SpecialAttackHandler;
import com.mayhem.rs2.content.dialogue.OneLineDialogue;
import com.mayhem.rs2.content.houses.HouseManager;
import com.mayhem.rs2.content.minigames.duelarena.DuelingConstants;
import com.mayhem.rs2.content.minigames.godwars.GodWarsData;
import com.mayhem.rs2.content.minigames.plunder.PlunderConstants;
import com.mayhem.rs2.content.minigames.plunder.PyramidPlunder;
import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.content.skill.agility.Agility;
import com.mayhem.rs2.content.skill.cooking.CookingData;
import com.mayhem.rs2.content.skill.crafting.Craftable;
import com.mayhem.rs2.content.skill.crafting.Glass;
import com.mayhem.rs2.content.skill.crafting.HideTanData;
import com.mayhem.rs2.content.skill.crafting.Jewelry;
import com.mayhem.rs2.content.skill.crafting.Spinnable;
import com.mayhem.rs2.content.skill.craftingnew.craftable.impl.Hide;
import com.mayhem.rs2.content.skill.farming.Farming;
import com.mayhem.rs2.content.skill.firemaking.LogData;
import com.mayhem.rs2.content.skill.fishing.FishableData;
import com.mayhem.rs2.content.skill.fishing.Fishing;
import com.mayhem.rs2.content.skill.fishing.ToolData;
import com.mayhem.rs2.content.skill.fletching.fletchable.impl.Arrow;
import com.mayhem.rs2.content.skill.fletching.fletchable.impl.Bolt;
import com.mayhem.rs2.content.skill.fletching.fletchable.impl.Carvable;
import com.mayhem.rs2.content.skill.fletching.fletchable.impl.Crossbow;
import com.mayhem.rs2.content.skill.fletching.fletchable.impl.Featherable;
import com.mayhem.rs2.content.skill.fletching.fletchable.impl.Stringable;
import com.mayhem.rs2.content.skill.herblore.FinishedPotionData;
import com.mayhem.rs2.content.skill.herblore.GrimyHerbData;
import com.mayhem.rs2.content.skill.herblore.GrindingData;
import com.mayhem.rs2.content.skill.herblore.UnfinishedPotionData;
import com.mayhem.rs2.content.skill.magic.MagicConstants;
import com.mayhem.rs2.content.skill.magic.MagicEffects;
import com.mayhem.rs2.content.skill.mining.Mining;
import com.mayhem.rs2.content.skill.prayer.BoneBurying;
import com.mayhem.rs2.content.skill.ranged.AmmoData;
import com.mayhem.rs2.content.skill.runecrafting.RunecraftingData;
import com.mayhem.rs2.content.skill.slayer.SlayerMonsters;
import com.mayhem.rs2.content.skill.smithing.SmeltingData;
import com.mayhem.rs2.content.skill.thieving.ThievingNpcData;
import com.mayhem.rs2.content.skill.thieving.ThievingStallData;
import com.mayhem.rs2.content.skill.woodcutting.WoodcuttingAxeData;
import com.mayhem.rs2.content.mayhembot.*;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.EquipmentConstants;
import com.mayhem.rs2.entity.item.impl.GlobalItemHandler;
import com.mayhem.rs2.entity.mob.Mob;
import com.mayhem.rs2.entity.mob.MobAbilities;
import com.mayhem.rs2.entity.mob.MobConstants;
import com.mayhem.rs2.entity.object.ObjectConstants;
import com.mayhem.rs2.entity.object.ObjectManager;
import com.mayhem.rs2.entity.player.net.in.PacketHandler;


/**
 * Loads all of the neccessary game data
 * 
 * @author Michael Sasse
 * 
 */
public class GameDataLoader {

	/**
	 * The stage of the game server
	 */
	private static int stage = 0;

	/**
	 * Loads all of the game data
	 */
	public static void load() {
		try {
			GameDefinitionLoader.declare();
			GlobalMessages.declare();
			MayhemBot.declare();
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
						GlobalMessages.initialize();
						HouseManager.declare();
						MayhemBot.initialize();
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
			GameDefinitionLoader.loadSpecialAttackDefinitions();
			GameDefinitionLoader.loadRangedStrengthDefinitions();
			GameDefinitionLoader.loadSpecialAttackDefinitions();
			GameDefinitionLoader.loadCombatSpellDefinitions();
			GameDefinitionLoader.loadFoodDefinitions();
			GameDefinitionLoader.loadPotionDefinitions();
			GameDefinitionLoader.loadRangedWeaponDefinitions();
			GameDefinitionLoader.loadNpcCombatDefinitions();
			//NpcDropSystem.get().loadDrops();
			//NpcDropSystem.get().loadRareDrops();
			GameDefinitionLoader.loadNpcDropDefinitions();
			GameDefinitionLoader.loadItemBonusDefinitions();			
			GodWarsData.declare();			
			Mining.declare();			
			PyramidPlunder.declare();
			PlunderConstants.UrnBitPosition.declare();
			PlunderConstants.DoorBitPosition.declare();		
			ClueScrollManager.declare();
			FountainOfRune.declare();
			Agility.declare();			
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
			com.mayhem.rs2.content.skill.craftingnew.craftable.impl.Gem.declare();
			Hide.declare();		
			Farming.declare();
			Shop.declare();
			MagicConstants.declare();
			SlayerMonsters.declare();
			DuelingConstants.declare();
			MobConstants.declare();
			Emotes.declare();
			PoisonWeapons.declare();
			SpecialAttackHandler.declare();
			CookingData.declare();
			Glass.declare();
			LogData.declare();
			FishableData.Fishable.declare();
			Fishing.FishingSpots.declare();
			ToolData.Tools.declare();
			FinishedPotionData.declare();
			GrimyHerbData.declare();
			GrindingData.declare();
			UnfinishedPotionData.declare();
			MagicEffects.declare();
			BoneBurying.Bones.declare();
			AmmoData.Ammo.declare();
			RunecraftingData.declare();
			Skills.declare();
			ThievingNpcData.declare();
			ThievingStallData.declare();
			WoodcuttingAxeData.declare();
			EquipmentConstants.declare();
			PacketHandler.declare();
			MobConstants.MobDissapearDelay.declare();
			MobAbilities.declare();
			SmeltingData.declare();
			OneLineDialogue.declare();
			FileHandler.load();
			World.getEventManager().appendTimer();
			stage += 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets if the server has been successfully loaded
	 * 
	 * @return
	 */
	public static boolean loaded() {
		return stage == 2;
	}
}
