package com.mayhem.rs2.content.combat.special;

import java.util.HashMap;
import java.util.Map;

import com.mayhem.rs2.content.combat.impl.CombatEffect;
import com.mayhem.rs2.content.combat.special.effects.AbyssalTentacleEffect;
import com.mayhem.rs2.content.combat.special.effects.AbyssalWhipEffect;
import com.mayhem.rs2.content.combat.special.effects.BandosGodswordEffect;
import com.mayhem.rs2.content.combat.special.effects.BarrelchestAnchorEffect;
import com.mayhem.rs2.content.combat.special.effects.DragonScimitarEffect;
import com.mayhem.rs2.content.combat.special.effects.DragonSpearEffect;
import com.mayhem.rs2.content.combat.special.effects.DragonWarhammerEffect;
import com.mayhem.rs2.content.combat.special.effects.SaradominGodswordEffect;
import com.mayhem.rs2.content.combat.special.effects.ToxicBlowpipeEffect;
import com.mayhem.rs2.content.combat.special.effects.ZamorakianHastaEffect;
import com.mayhem.rs2.content.combat.special.effects.ZamorakianSpearEffect;
import com.mayhem.rs2.content.combat.special.specials.AbyssalDaggerSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.AbyssalTentacleSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.AbyssalWhipSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.AnchorSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.ArmadylCrossbowSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.ArmadylGodswordSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.BandosGodswordSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.CrystalHalberdSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DarkBowSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonClawsSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonDaggerSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonHalberdSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonLongswordSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonMaceSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonScimitarSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonSpearSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.DragonWarhammerSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.GraniteMaulSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.MagicShortbowInfusedSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.MagicShortbowSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.SaradominGodswordSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.SaradominSwordSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.ToxicBlowpipeSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.ZamorakGodswordSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.ZamorakianHastaSpecialAttack;
import com.mayhem.rs2.content.combat.special.specials.ZamorakianSpearSpecialAttack;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.PlayerConstants;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
import com.mayhem.rs2.entity.player.net.out.impl.SendUpdateSpecialBar;

public class SpecialAttackHandler {
	
	private static Map<Integer, Special> specials = new HashMap<Integer, Special>();

	private static Map<Integer, CombatEffect> effects = new HashMap<Integer, CombatEffect>();

	private static void add(int weaponId, CombatEffect effect) {
		effects.put(Integer.valueOf(weaponId), effect);
	}

	private static void add(int weaponId, Special special) {
		specials.put(Integer.valueOf(weaponId), special);
	}

	public static void declare() {
		//add(20074, new StaffOfDeadSpecialAttack());
		//add(20076, new StaffOfDeadSpecialAttack());
	
		/* Magic Shortbow */
		add(12788, new MagicShortbowInfusedSpecialAttack());

		/* Zamorakian Hasta */
		add(11889, new ZamorakianHastaSpecialAttack());
		add(11889, new ZamorakianHastaEffect());
	
		/* Zamorakian Spear */
		add(11824, new ZamorakianSpearSpecialAttack());
		add(11824, new ZamorakianSpearEffect());
		
		/* Armadyl Crossbow */
		add(11785, new ArmadylCrossbowSpecialAttack());
		
		/*Crystal halberd */
		add(13092, new CrystalHalberdSpecialAttack());
		
		/* Blowpipe */
		add(12926, new ToxicBlowpipeSpecialAttack());
		add(12926, new ToxicBlowpipeEffect());

		/* Saradomin Sword */
		add(11838, new SaradominSwordSpecialAttack());
		add(12809, new SaradominSwordSpecialAttack());
		
		/* Korasi */
		add(5012, new SaradominSwordSpecialAttack());
		add(5012, new ZamorakianSpearEffect());
		
		/* Armadyl Godsword */
		add(11802, new ArmadylGodswordSpecialAttack());
		
		/* Bandos Godsword */
		add(11804, new BandosGodswordSpecialAttack());
		add(11804, new BandosGodswordEffect());
		
		/* Saradomin Godsword */
		add(11806, new SaradominGodswordSpecialAttack());
		add(11806, new SaradominGodswordEffect());
		
		/* Zamorak Godsword */
		add(11808, new ZamorakGodswordSpecialAttack());
		
		/* Dark Bow */
		add(11235, new DarkBowSpecialAttack());
		add(12765, new DarkBowSpecialAttack());
		add(12766, new DarkBowSpecialAttack());
		add(12767, new DarkBowSpecialAttack());
		add(12768, new DarkBowSpecialAttack());
		
		/* Barrelchest Anchor */
		add(10887, new BarrelchestAnchorEffect());
		add(10887, new AnchorSpecialAttack());

		/* Dragon Claws */
		add(13188, new DragonClawsSpecialAttack());
		add(20784, new DragonClawsSpecialAttack());

		
		/* Abyssal Dagger */
		add(13271, new AbyssalDaggerSpecialAttack());
		
		/* Dragon Warhammer */
		add(13576, new DragonWarhammerSpecialAttack());
		add(13576, new DragonWarhammerEffect());
		
		/* Dragon Spear */
		add(1249, new DragonSpearSpecialAttack());
		add(1249, new DragonSpearEffect());
		
		/* Dragon Dagger */
		add(1215, new DragonDaggerSpecialAttack());
		add(1231, new DragonDaggerSpecialAttack());
		add(5680, new DragonDaggerSpecialAttack());
		add(5698, new DragonDaggerSpecialAttack());
		
		/* Dragon Scimitar */
		add(4587, new DragonScimitarSpecialAttack());
		add(4587, new DragonScimitarEffect());
		
		/* Dragon Longsword */
		add(1305, new DragonLongswordSpecialAttack());
		
		/* Dragon Mace */
		add(1434, new DragonMaceSpecialAttack());

		/* Dragon Halbard */
		add(3204, new DragonHalberdSpecialAttack());
		
		/* Magic Shortbow */
		add(861, new MagicShortbowSpecialAttack());
		add(859, new MagicShortbowSpecialAttack());
		
		
		/*twisted bow*/
		/**
		add(20997, new MagicShortbowSpecialAttack());
		*/
		
		/* Granite Maul */
		add(4153, new GraniteMaulSpecialAttack());
		
		/* Abyssal Whip */
		add(4151, new AbyssalWhipSpecialAttack());
		add(4151, new AbyssalWhipEffect());
		add(4178, new AbyssalWhipSpecialAttack());
		add(4178, new AbyssalWhipEffect());
		add(12773, new AbyssalWhipSpecialAttack());
		add(12773, new AbyssalWhipEffect());
		add(12774, new AbyssalWhipSpecialAttack());
		add(12774, new AbyssalWhipEffect());
		
		/* Tentacle Whip */
		add(12006, new AbyssalTentacleSpecialAttack());
		add(12006, new AbyssalTentacleEffect());
		
	}

	public static void executeSpecialEffect(Player player, Entity attacked) {
		Item weapon = player.getEquipment().getItems()[3];

		if (weapon == null) {
			return;
		}

		CombatEffect effect = effects.get(Integer.valueOf(weapon.getId()));

		if (effect == null) {
			return;
		}
		effect.execute(player, attacked);
	}

	public static void handleSpecialAttack(Player player) {
		Item weapon = player.getEquipment().getItems()[3];

		if (weapon == null) {
			return;
		}

		Special special = specials.get(Integer.valueOf(weapon.getId()));

		if (special == null) {
			return;
		}

		if (special.checkRequirements(player)) {
			special.handleAttack(player);
			if (!PlayerConstants.isOwner(player))
				player.getSpecialAttack().deduct(special.getSpecialAmountRequired());
		}
	}

	public static boolean hasSpecialAmount(Player player) {
		Item weapon = player.getEquipment().getItems()[3];

		if (weapon == null) {
			return true;
		}

		Special special = specials.get(Integer.valueOf(weapon.getId()));

		if (special == null) {
			return true;
		}

		if (player.getSpecialAttack().getAmount() < special.getSpecialAmountRequired()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough special attack to do that."));
			return false;
		}
		return true;
	}

	public static void updateSpecialAmount(Player p, int id, int amount) {
		int specialCheck = 100;
		for (int i = 0; i < 10; i++) {
			id--;
			p.getClient().queueOutgoingPacket(new SendUpdateSpecialBar(amount >= specialCheck ? 500 : 0, id));
			specialCheck -= 10;
		}
	}

	public static void updateSpecialBarText(Player p, int id, int amount, boolean init) {
		if (init)
			p.getClient().queueOutgoingPacket(new SendString("@yel@Special Attack - "+p.getSpecialAttack().getAmount()+"%", id));
		else
			p.getClient().queueOutgoingPacket(new SendString("@bla@Special Attack - "+p.getSpecialAttack().getAmount()+"%", id));
	}
}
