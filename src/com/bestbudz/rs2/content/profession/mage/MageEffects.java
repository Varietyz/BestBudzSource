package com.bestbudz.rs2.content.profession.mage;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.profession.mage.effects.BindEffect;
import com.bestbudz.rs2.content.profession.mage.effects.BloodBarrageEffect;
import com.bestbudz.rs2.content.profession.mage.effects.BloodBlitzEffect;
import com.bestbudz.rs2.content.profession.mage.effects.BloodBurstEffect;
import com.bestbudz.rs2.content.profession.mage.effects.BloodRushEffect;
import com.bestbudz.rs2.content.profession.mage.effects.ClawsOfGuthixEffect;
import com.bestbudz.rs2.content.profession.mage.effects.EntangleEffect;
import com.bestbudz.rs2.content.profession.mage.effects.FlamesOfZamorakEffect;
import com.bestbudz.rs2.content.profession.mage.effects.IceBarrageEffect;
import com.bestbudz.rs2.content.profession.mage.effects.IceBlitzEffect;
import com.bestbudz.rs2.content.profession.mage.effects.IceBurstEffect;
import com.bestbudz.rs2.content.profession.mage.effects.IceRushEffect;
import com.bestbudz.rs2.content.profession.mage.effects.SaradominStrikeEffect;
import com.bestbudz.rs2.content.profession.mage.effects.ShadowBarrageEffect;
import com.bestbudz.rs2.content.profession.mage.effects.ShadowBlitzEffect;
import com.bestbudz.rs2.content.profession.mage.effects.ShadowBurstEffect;
import com.bestbudz.rs2.content.profession.mage.effects.ShadowRushEffect;
import com.bestbudz.rs2.content.profession.mage.effects.SmokeBarrageEffect;
import com.bestbudz.rs2.content.profession.mage.effects.SmokeBlitzEffect;
import com.bestbudz.rs2.content.profession.mage.effects.SmokeBurstEffect;
import com.bestbudz.rs2.content.profession.mage.effects.SmokeRushEffect;
import com.bestbudz.rs2.content.profession.mage.effects.SnareEffect;
import com.bestbudz.rs2.content.profession.mage.effects.TeleBlockEffect;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;
import java.util.Map;

public class MageEffects {
  private static final Map<Integer, CombatEffect> effects = new HashMap<Integer, CombatEffect>();

  public static final void declare() {
    effects.put(Integer.valueOf(12939), new SmokeRushEffect());
    effects.put(Integer.valueOf(12987), new ShadowRushEffect());
    effects.put(Integer.valueOf(12901), new BloodRushEffect());
    effects.put(Integer.valueOf(12861), new IceRushEffect());
    effects.put(Integer.valueOf(12963), new SmokeBurstEffect());
    effects.put(Integer.valueOf(13011), new ShadowBurstEffect());
    effects.put(Integer.valueOf(12919), new BloodBurstEffect());
    effects.put(Integer.valueOf(12881), new IceBurstEffect());
    effects.put(Integer.valueOf(12951), new SmokeBlitzEffect());
    effects.put(Integer.valueOf(12999), new ShadowBlitzEffect());
    effects.put(Integer.valueOf(12911), new BloodBlitzEffect());
    effects.put(Integer.valueOf(12871), new IceBlitzEffect());
    effects.put(Integer.valueOf(12975), new SmokeBarrageEffect());
    effects.put(Integer.valueOf(13023), new ShadowBarrageEffect());
    effects.put(Integer.valueOf(12929), new BloodBarrageEffect());
    effects.put(Integer.valueOf(12891), new IceBarrageEffect());

    effects.put(Integer.valueOf(1190), new SaradominStrikeEffect());
    effects.put(Integer.valueOf(1191), new ClawsOfGuthixEffect());
    effects.put(Integer.valueOf(1192), new FlamesOfZamorakEffect());

    effects.put(Integer.valueOf(1572), new BindEffect());
    effects.put(Integer.valueOf(1582), new SnareEffect());
    effects.put(Integer.valueOf(1592), new EntangleEffect());

    effects.put(Integer.valueOf(12445), new TeleBlockEffect());
  }

  public static void doMageEffects(Stoner assaulter, Entity assaulted, int spellId) {
    CombatEffect effect = effects.get(Integer.valueOf(spellId));

    if (effect == null) {
      return;
    }

    effect.execute(assaulter, assaulted);
  }
}
