package com.bestbudz.rs2.content.combat.special;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.special.effects.AbyssalTentacleEffect;
import com.bestbudz.rs2.content.combat.special.effects.AbyssalWhipEffect;
import com.bestbudz.rs2.content.combat.special.effects.ArmadylCrossbowEffect;
import com.bestbudz.rs2.content.combat.special.effects.ArmadylGodswordEffect;
import com.bestbudz.rs2.content.combat.special.effects.BandosGodswordEffect;
import com.bestbudz.rs2.content.combat.special.effects.BarrelchestAnchorEffect;
import com.bestbudz.rs2.content.combat.special.effects.DarkBowEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonBattleaxeEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonClawsEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonDaggerEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonHalberdEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonLongswordEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonMaceEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonScimitarEffect;
import com.bestbudz.rs2.content.combat.special.effects.DragonSpearEffect;
import com.bestbudz.rs2.content.combat.special.effects.GraniteMaulEffect;
import com.bestbudz.rs2.content.combat.special.effects.MagicShortbowEffect;
import com.bestbudz.rs2.content.combat.special.effects.SaradominGodswordEffect;
import com.bestbudz.rs2.content.combat.special.effects.SaradominSwordEffect;
import com.bestbudz.rs2.content.combat.special.effects.StaffOfDeadEffect;
import com.bestbudz.rs2.content.combat.special.effects.ToxicBlowpipeEffect;
import com.bestbudz.rs2.content.combat.special.effects.ZamorakGodswordEffect;
import com.bestbudz.rs2.content.combat.special.effects.ZamorakianHastaEffect;
import com.bestbudz.rs2.content.combat.special.effects.ZamorakianSpearEffect;
import com.bestbudz.rs2.content.combat.special.specials.AbyssalTentacleSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.AbyssalWhipSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.AnchorSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.ArmadylCrossbowSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.ArmadylGodswordSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.BandosGodswordSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DarkBowSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonBattleaxeSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonClawsSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonDaggerSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonHalberdSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonLongswordSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonMaceSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonScimitarSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.DragonSpearSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.GraniteMaulSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.MageShortbowInfusedSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.MageShortbowSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.SaradominGodswordSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.SaradominSwordSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.ToxicBlowpipeSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.ZamorakGodswordSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.ZamorakianHastaSpecialAssault;
import com.bestbudz.rs2.content.combat.special.specials.ZamorakianSpearSpecialAssault;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateSpecialBar;
import java.util.HashMap;
import java.util.Map;

public class SpecialAssaultHandler {

  private static final Map<Integer, Special> specials = new HashMap<Integer, Special>();

  private static final Map<Integer, CombatEffect> effects = new HashMap<Integer, CombatEffect>();

  private static void add(int weaponId, CombatEffect effect) {
    effects.put(Integer.valueOf(weaponId), effect);
  }

  private static void add(int weaponId, Special special) {
    specials.put(Integer.valueOf(weaponId), special);
  }

  public static void declare() {
    add(12788, new MageShortbowInfusedSpecialAssault());
    add(11889, new ZamorakianHastaSpecialAssault());
    add(11889, new ZamorakianHastaEffect());
    add(11824, new ZamorakianSpearSpecialAssault());
    add(11824, new ZamorakianSpearEffect());
    add(11785, new ArmadylCrossbowSpecialAssault());
    add(12926, new ToxicBlowpipeSpecialAssault());
    add(12926, new ToxicBlowpipeEffect());
    add(11838, new SaradominSwordSpecialAssault());
    add(12809, new SaradominSwordSpecialAssault());
    add(11802, new ArmadylGodswordSpecialAssault());
    add(11804, new BandosGodswordSpecialAssault());
    add(11804, new BandosGodswordEffect());
    add(11806, new SaradominGodswordSpecialAssault());
    add(11806, new SaradominGodswordEffect());
    add(11808, new ZamorakGodswordSpecialAssault());
    add(11235, new DarkBowSpecialAssault());
    add(12765, new DarkBowSpecialAssault());
    add(12766, new DarkBowSpecialAssault());
    add(12767, new DarkBowSpecialAssault());
    add(12768, new DarkBowSpecialAssault());
    add(10887, new BarrelchestAnchorEffect());
    add(10887, new AnchorSpecialAssault());
    add(13188, new DragonClawsSpecialAssault());
    add(1249, new DragonSpearSpecialAssault());
    add(1249, new DragonSpearEffect());
    add(1215, new DragonDaggerSpecialAssault());
    add(1231, new DragonDaggerSpecialAssault());
    add(5680, new DragonDaggerSpecialAssault());
    add(5698, new DragonDaggerSpecialAssault());
    add(4587, new DragonScimitarSpecialAssault());
    add(4587, new DragonScimitarEffect());
    add(1305, new DragonLongswordSpecialAssault());
    add(1434, new DragonMaceSpecialAssault());
    add(3204, new DragonHalberdSpecialAssault());
    add(861, new MageShortbowSpecialAssault());
    add(859, new MageShortbowSpecialAssault());
    add(4153, new GraniteMaulSpecialAssault());
    add(4151, new AbyssalWhipSpecialAssault());
    add(4151, new AbyssalWhipEffect());
    add(4178, new AbyssalWhipSpecialAssault());
    add(4178, new AbyssalWhipEffect());
    add(12773, new AbyssalWhipSpecialAssault());
    add(12773, new AbyssalWhipEffect());
    add(12774, new AbyssalWhipSpecialAssault());
    add(12774, new AbyssalWhipEffect());
    add(12006, new AbyssalTentacleSpecialAssault());
    add(12006, new AbyssalTentacleEffect());
	add(1377, new DragonBattleaxeSpecialAssault());
	  add(13188, new DragonClawsEffect());        // Dragon Claws
	  add(1215, new DragonDaggerEffect());        // Dragon Dagger
	  add(1231, new DragonDaggerEffect());        // Dragon Dagger variants
	  add(5680, new DragonDaggerEffect());
	  add(5698, new DragonDaggerEffect());
	  add(4153, new GraniteMaulEffect());         // Granite Maul
	  add(11235, new DarkBowEffect());            // Dark Bow
	  add(12765, new DarkBowEffect());            // Dark Bow variants
	  add(12766, new DarkBowEffect());
	  add(12767, new DarkBowEffect());
	  add(12768, new DarkBowEffect());
	  add(1434, new DragonMaceEffect());          // Dragon Mace
	  add(1305, new DragonLongswordEffect());     // Dragon Longsword
	  add(11802, new ArmadylGodswordEffect());    // Armadyl Godsword
	  add(11808, new ZamorakGodswordEffect());    // Zamorak Godsword
	  add(11785, new ArmadylCrossbowEffect());
	  add(3204, new DragonHalberdEffect());
	  add(861, new MagicShortbowEffect());
	  add(859, new MagicShortbowEffect());
	  add(11838, new SaradominSwordEffect());
	  add(12809, new SaradominSwordEffect());
	  add(20074, new StaffOfDeadEffect()); // Staff of the Dead
	  add(20076, new StaffOfDeadEffect());
	  add(1377, new DragonBattleaxeEffect());
  }

  public static void executeSpecialEffect(Stoner stoner, Entity assaulted) {
    Item weapon = stoner.getEquipment().getItems()[3];

    if (weapon == null) {
      return;
    }

    CombatEffect effect = effects.get(Integer.valueOf(weapon.getId()));

    if (effect == null) {
      return;
    }
    effect.execute(stoner, assaulted);
  }

  public static void handleSpecialAssault(Stoner stoner) {
    Item weapon = stoner.getEquipment().getItems()[3];

    if (weapon == null) {
      return;
    }

    Special special = specials.get(Integer.valueOf(weapon.getId()));

    if (special == null) {
      return;
    }

    if (special.checkRequirements(stoner)) {
      special.handleAssault(stoner);
      if (!StonerConstants.isOwner(stoner))
        stoner.getSpecialAssault().deduct(special.getSpecialAmountRequired());
    }
  }

  public static boolean hasSpecialAmount(Stoner stoner) {
    Item weapon = stoner.getEquipment().getItems()[3];

    if (weapon == null) {
      return true;
    }

    Special special = specials.get(Integer.valueOf(weapon.getId()));

    if (special == null) {
      return true;
    }

    if (stoner.getSpecialAssault().getAmount() < special.getSpecialAmountRequired()) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You do not have enough power to fuck em up yet, get gud."));
      return false;
    }
    return true;
  }

  public static void updateSpecialAmount(Stoner p, int id, int amount) {
    int specialCheck = 100;
    for (int i = 0; i < 10; i++) {
      id--;
      p.getClient()
          .queueOutgoingPacket(new SendUpdateSpecialBar(amount >= specialCheck ? 500 : 0, id));
      specialCheck -= 10;
    }
  }

  public static void updateSpecialBarText(Stoner p, int id, int amount, boolean init) {
    if (init)
      p.getClient()
          .queueOutgoingPacket(
              new SendString(
                  "@gre@" + p.getSpecialAssault().getAmount() + "LIGHT EM UP", id));
    else
      p.getClient()
          .queueOutgoingPacket(
              new SendString(
                  "@bla@" + /*p.getSpecialAssault().getAmount()*/ "LIGHT EM UP", id));
  }
}
