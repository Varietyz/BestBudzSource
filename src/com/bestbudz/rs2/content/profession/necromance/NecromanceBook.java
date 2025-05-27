package com.bestbudz.rs2.content.profession.necromance;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendOpenTab;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;

public class NecromanceBook {
  private static final Necromance[] OVER_HEAD_DISABLED =
      new Necromance[] {
        Necromance.PROTECT_FROM_MAGE,
        Necromance.PROTECT_FROM_RANGE,
        Necromance.PROTECT_FROM_MELEE,
        Necromance.RETRIBUTION,
        Necromance.REDEMPTION,
        Necromance.SMITE
      };
  private static final Necromance[] AEGIS_DISABLED =
      new Necromance[] {
        Necromance.THICK_SKIN,
        Necromance.ROCK_SKIN,
        Necromance.STEEL_SKIN,
        Necromance.CHIVALRY,
        Necromance.PIETY
      };
  private static final Necromance[] ASSAULT_DISABLED =
      new Necromance[] {
        Necromance.CLARITY_OF_THOUGHT,
        Necromance.IMPROVED_REFLEXES,
        Necromance.INCREDIBLE_REFLEXES,
        Necromance.SHARP_EYE,
        Necromance.HAWK_EYE,
        Necromance.EAGLE_EYE,
        Necromance.MYSTIC_WILL,
        Necromance.MYSTIC_LORE,
        Necromance.MYSTIC_MIGHT,
        Necromance.CHIVALRY,
        Necromance.PIETY
      };
  private static final Necromance[] VIGOUR_DISABLED =
      new Necromance[] {
        Necromance.BURST_OF_VIGOUR,
        Necromance.SUPERHUMAN_VIGOUR,
        Necromance.ULTIMATE_VIGOUR,
        Necromance.SHARP_EYE,
        Necromance.HAWK_EYE,
        Necromance.EAGLE_EYE,
        Necromance.MYSTIC_WILL,
        Necromance.MYSTIC_LORE,
        Necromance.MYSTIC_MIGHT,
        Necromance.CHIVALRY,
        Necromance.PIETY
      };
  private static final Necromance[] ATT_STR_DISABLED =
      new Necromance[] {
        Necromance.CLARITY_OF_THOUGHT,
        Necromance.IMPROVED_REFLEXES,
        Necromance.INCREDIBLE_REFLEXES,
        Necromance.BURST_OF_VIGOUR,
        Necromance.SUPERHUMAN_VIGOUR,
        Necromance.ULTIMATE_VIGOUR,
        Necromance.SHARP_EYE,
        Necromance.HAWK_EYE,
        Necromance.EAGLE_EYE,
        Necromance.MYSTIC_WILL,
        Necromance.MYSTIC_LORE,
        Necromance.MYSTIC_MIGHT,
        Necromance.CHIVALRY,
        Necromance.PIETY
      };
  private static final Necromance[] COMBAT_DISABLED =
      new Necromance[] {
        Necromance.CLARITY_OF_THOUGHT,
        Necromance.IMPROVED_REFLEXES,
        Necromance.INCREDIBLE_REFLEXES,
        Necromance.BURST_OF_VIGOUR,
        Necromance.SUPERHUMAN_VIGOUR,
        Necromance.ULTIMATE_VIGOUR,
        Necromance.THICK_SKIN,
        Necromance.ROCK_SKIN,
        Necromance.STEEL_SKIN,
        Necromance.SHARP_EYE,
        Necromance.HAWK_EYE,
        Necromance.EAGLE_EYE,
        Necromance.MYSTIC_WILL,
        Necromance.MYSTIC_LORE,
        Necromance.MYSTIC_MIGHT,
        Necromance.CHIVALRY,
        Necromance.PIETY
      };
  private final boolean[] activated;
  private final int[] drain;
  private final Stoner stoner;
  private boolean[] quickNecromances;
  private int headIcon = -1;

  public NecromanceBook(Stoner stoner) {
    this.stoner = stoner;
    activated = new boolean[Necromance.values().length];
    quickNecromances = new boolean[Necromance.values().length];
    drain = new int[Necromance.values().length];
  }

  private boolean canToggle(Necromance necromance) {
    if (stoner.getMaxGrades()[5] < necromance.getGrade()) {
      stoner.send(
          new SendMessage(
              "You need a Necromance grade of "
                  + necromance.getGrade()
                  + " to use "
                  + necromance.getName()
                  + "."));
      return false;
    } else if (necromance == Necromance.CHIVALRY && (stoner.getMaxGrades()[1] < 65)) {
      stoner.send(new SendMessage("You need a Aegis grade of 65 to use Chivalry."));
      return false;
    } else if (necromance == Necromance.PIETY && (stoner.getMaxGrades()[1] < 70)) {
      stoner.send(new SendMessage("You need a Aegis grade of 70 to use Piety."));
      return false;
    }

    return true;
  }

  public boolean toggle(Necromance necromance) {
    boolean isEnabled = canToggle(necromance);

    if (stoner.isDead()) {
      return false;
    } else if (stoner.getProfession().getGrades()[5] == 0) {
      stoner.send(
          new SendMessage(
              "Necromance powers are exhausted; you could recharge these from your info-tab."));
      forceToggle(necromance, false);
      return false;
    } else if (!stoner.getController().canUseNecromance(stoner, necromance.ordinal())) {
      return false;
    } else if (!isEnabled) {
      stoner.send(new SendConfig(necromance.getConfigId(), 0));
      return false;
    }

    forceToggle(necromance, !activated[necromance.ordinal()]);

    return true;
  }

  public boolean clickButton(int button) {
    if (button >= 67050 && button <= 67075) {
      int necromanceId = button - 67050;
      Necromance necromance = Necromance.values()[necromanceId];

      if (!quickNecromances[necromanceId]) {
        if (!canToggle(necromance)) {
          stoner.send(new SendConfig(630 + necromanceId, 0));
          return true;
        } else {
          quickNecromances[necromance.ordinal()] = true;
          stoner.send(new SendConfig(630 + necromance.ordinal(), 1));
          if (necromance.getDisabledNecromances() != null) {
            for (Necromance override : necromance.getDisabledNecromances()) {
              if (override != necromance) {
                quickNecromances[override.ordinal()] = false;
                stoner.send(new SendConfig(630 + override.ordinal(), 0));
              }
            }
          }
        }
      } else {
        quickNecromances[necromanceId] = false;
        stoner.send(new SendConfig(630 + necromanceId, 0));
      }

      return true;
    }

    switch (button) {
      case 19136:
        toggleQuickNecromances();
        break;
      case 19137:
        for (Necromance necromance : Necromance.values()) {
          stoner.send(
              new SendConfig(
                  630 + necromance.ordinal(), quickNecromances[necromance.ordinal()] ? 1 : 0));
        }
        stoner.send(new SendSidebarInterface(5, 17200));
        stoner.send(new SendOpenTab(5));
        break;
      case 67079:
        stoner.send(new SendMessage("Your quick necromances have been saved."));
        stoner.send(new SendSidebarInterface(5, 5608));
        break;

      case 67089:
        stoner.send(new SendSidebarInterface(5, 5608));
        return true;

      case 87082:
        stoner.send(new SendSidebarInterface(5, 25789));
        stoner.send(new SendOpenTab(5));
        return true;

      case 21233:
        toggle(Necromance.THICK_SKIN);
        return true;
      case 21234:
        toggle(Necromance.BURST_OF_VIGOUR);
        return true;
      case 21235:
        toggle(Necromance.CLARITY_OF_THOUGHT);
        return true;
      case 77100:
        toggle(Necromance.SHARP_EYE);
        return true;
      case 77102:
        toggle(Necromance.MYSTIC_WILL);
        return true;
      case 21236:
        toggle(Necromance.ROCK_SKIN);
        return true;
      case 21237:
        toggle(Necromance.SUPERHUMAN_VIGOUR);
        return true;
      case 21238:
        toggle(Necromance.IMPROVED_REFLEXES);
        return true;
      case 21239:
        toggle(Necromance.RAPID_RESTORE);
        return true;
      case 21240:
        toggle(Necromance.RAPID_HEAL);
        return true;
      case 21241:
        toggle(Necromance.PROTECT_ITEM);
        return true;
      case 77104:
        toggle(Necromance.HAWK_EYE);
        return true;
      case 77106:
        toggle(Necromance.MYSTIC_LORE);
        return true;
      case 21242:
        toggle(Necromance.STEEL_SKIN);
        return true;
      case 21243:
        toggle(Necromance.ULTIMATE_VIGOUR);
        return true;
      case 21244:
        toggle(Necromance.INCREDIBLE_REFLEXES);
        return true;
      case 21245:
        toggle(Necromance.PROTECT_FROM_MAGE);
        return true;
      case 21246:
        toggle(Necromance.PROTECT_FROM_RANGE);
        return true;
      case 21247:
        toggle(Necromance.PROTECT_FROM_MELEE);
        return true;
      case 77109:
        toggle(Necromance.EAGLE_EYE);
        return true;
      case 77111:
        toggle(Necromance.MYSTIC_MIGHT);
        return true;
      case 2171:
        toggle(Necromance.RETRIBUTION);
        return true;
      case 2172:
        toggle(Necromance.REDEMPTION);
        return true;
      case 2173:
        toggle(Necromance.SMITE);
        return true;
      case 77113:
        toggle(Necromance.CHIVALRY);
        return true;
      case 77115:
        toggle(Necromance.PIETY);
        return true;
    }

    return false;
  }

  public boolean active(Necromance necromance) {
    return activated[necromance.ordinal()];
  }

  public void toggleQuickNecromances() {
    for (Necromance necromance : Necromance.values()) {
      if (!quickNecromances[necromance.ordinal()]) {
        if (activated[necromance.ordinal()]) {
          forceToggle(necromance, false);
        }
      } else {
        if (!toggle(necromance)) {
          return;
        }
      }
    }
  }

  private int deterquarryHeadIcon(Necromance necromance) {
    switch (necromance) {
      case PROTECT_FROM_MAGE:
        return 2;
      case PROTECT_FROM_RANGE:
        return 1;
      case PROTECT_FROM_MELEE:
        return 0;
      case RETRIBUTION:
        return 3;
      case REDEMPTION:
        return 5;
      case SMITE:
        return 4;
      default:
        return -1;
    }
  }

  public void forceToggle(Necromance necromance, boolean isEnabled) {
    activated[necromance.ordinal()] = isEnabled;
    stoner.send(new SendConfig(necromance.getConfigId(), isEnabled ? 1 : 0));

    if (isEnabled) {
      if (necromance.getDisabledNecromances() != null) {
        for (Necromance override : necromance.getDisabledNecromances()) {
          if (override != necromance) {
            forceToggle(override, false);
          }
        }
      }

      int icon = deterquarryHeadIcon(necromance);

      if (icon != headIcon && icon != -1) {
        headIcon = icon;
        stoner.setAppearanceUpdateRequired(true);
      }
    } else if (necromance.getType() == NecromanceType.OVER_HEAD) {
      headIcon = -1;
      stoner.setAppearanceUpdateRequired(true);
    }
  }

  public double getAffectedDrainRate(Necromance necromance) {
    return necromance.getDrainRate()
        * (1 + 0.035 * stoner.getBonuses()[EquipmentConstants.NECROMANCE]);
  }

  public void doEffectOnHit(Entity assaulted, Hit hit) {
    if (active(Necromance.SMITE) && assaulted.getGrades()[5] > 0) {
      assaulted.getGrades()[5] = (byte) (assaulted.getGrades()[5] - hit.getDamage() * 0.25D);

      if (!assaulted.isNpc()) {
        Stoner target = World.getStoners()[assaulted.getIndex()];

        if (target != null) {
          target.getProfession().update(5);
        }
      }
    }
  }

  public long getDamage(Hit hit) {
    switch (hit.getType()) {
      case MELEE:
        if (active(Necromance.PROTECT_FROM_MELEE)) {
          Entity target = hit.getAssaulter();
          if (target != null) {
            if (target.isNpc()) {
              Mob mob = World.getNpcs()[target.getIndex()];

              if (mob == null) {
                return hit.getDamage() / 2;
              }

              int id = mob.getId();

              if (id == 10057) {
                return hit.getDamage() / 2;
              }

              if (id == 2043) {
                return hit.getDamage() / 4;
              }

              if (id == 8596) {
                return hit.getDamage();
              }

              if ((id != 1677) && (id != 8133)) return 0;
            } else {
              Stoner otherStoner = World.getStoners()[target.getIndex()];

              if (otherStoner == null || !otherStoner.getMelee().isVeracEffectActive()) {
                return hit.getDamage() / 2;
              }
            }
          }

          return hit.getDamage() / 2;
        }
        break;

      case MAGE:
        if (active(Necromance.PROTECT_FROM_MAGE)) {
          Entity target = hit.getAssaulter();
          if ((target != null) && (target.isNpc())) {
            Mob mob = World.getNpcs()[target.getIndex()];

            if (MobConstants.isDragon(mob)) {
              return hit.getDamage();
            }

            if (mob == null) {
              return hit.getDamage() / 2;
            }
            if (mob.getId() == 494 || mob.getId() == 319) {
              return hit.getDamage() / 2;
            }

            if (mob.getId() == 2044) {
              return hit.getDamage() / 4;
            }
            if (mob.getId() == 8133) {
              return (int) (hit.getDamage() * 0.8D);
            }
            return 0;
          }

          return hit.getDamage() / 2;
        }

        break;

      case SAGITTARIUS:
        if (active(Necromance.PROTECT_FROM_RANGE)) {
          Entity target = hit.getAssaulter();
          if (target != null && target.isNpc()) {
            Mob mob = World.getNpcs()[target.getIndex()];

            if (mob == null) {
              return hit.getDamage() / 2;
            }

            int id = mob.getId();

            if (id == 2042) {
              return hit.getDamage() / 4;
            }

            if (id == 8133) {
              return (int) (hit.getDamage() * 0.8D);
            }

            return 0;
          }

          return hit.getDamage() / 2;
        }
        break;

      default:
        return hit.getDamage();
    }

    return hit.getDamage();
  }

  public byte getHeadicon() {
    return (byte) headIcon;
  }

  public boolean[] getQuickNecromances() {
    return quickNecromances;
  }

  public void setQuickNecromances(boolean[] quickNecromances) {
    this.quickNecromances = quickNecromances;
  }

  public boolean isQuickNecromance(Necromance necromance) {
    return quickNecromances[necromance.ordinal()];
  }

  public void drain() {
    int amount = 0;
    for (Necromance necromance : Necromance.values()) {
      if (active(necromance)) {
        if (++drain[necromance.ordinal()] >= getAffectedDrainRate(necromance) / 0.6) {
          amount++;
          drain[necromance.ordinal()] = 0;
        }
      }
    }

    if (amount > 0) {
      drain(amount);
    }
  }

  public void drain(long drain) {
    long necromance = stoner.getProfession().getGrades()[5];
    if (drain >= necromance) {
      for (int i = 0; i < this.drain.length; i++) {
        this.drain[i] = 0;
      }

      disable();
      stoner.getProfession().setGrade(5, 0);
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You have run out of necromance points; you must recharge at an altar."));
    } else {
      stoner.getProfession().deductFromGrade(5, drain < 1 ? 1 : (int) Math.ceil(drain));

      if (stoner.getProfession().getGrades()[5] <= 0) {
        disable();
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You have run out of necromance points; you must recharge at an altar."));
      }
    }
  }

  public void disable() {
    for (Necromance necromance : Necromance.values()) {
      if (active(necromance)) {
        forceToggle(necromance, false);
      }
    }
  }

  public void disable(Necromance necromance) {
    forceToggle(necromance, false);
  }

  public enum NecromanceType {
    OVER_HEAD,
    AEGIS,
    ASSAULT,
    VIGOUR,
    MAGE_RANGE,
    COMBAT,
    DEFAULT
  }

  public enum Necromance {
    THICK_SKIN("Thick Skin", 1, 12.0, 83, NecromanceType.AEGIS),
    BURST_OF_VIGOUR("Burst of Vigour", 4, 12.0, 84, NecromanceType.VIGOUR),
    CLARITY_OF_THOUGHT("Clarity of Thought", 7, 12.0, 85, NecromanceType.ASSAULT),
    SHARP_EYE("Sharp Eye", 8, 12.0, 700, NecromanceType.MAGE_RANGE),
    MYSTIC_WILL("Mystic Will", 9, 12.0, 701, NecromanceType.MAGE_RANGE),
    ROCK_SKIN("Rock Skin", 10, 8.0, 86, NecromanceType.AEGIS),
    SUPERHUMAN_VIGOUR("Superhuman Vigour", 13, 8.0, 87, NecromanceType.VIGOUR),
    IMPROVED_REFLEXES("Improved Reflexes", 16, 8.0, 88, NecromanceType.ASSAULT),
    RAPID_RESTORE("Rapid Restore", 19, 60.0, 89, NecromanceType.DEFAULT),
    RAPID_HEAL("Rapid Heal", 22, 60.0, 90, NecromanceType.DEFAULT),
    PROTECT_ITEM("Protect Item", 25, 30.0, 91, NecromanceType.DEFAULT),
    HAWK_EYE("Hawk Eye", 26, 6.0, 702, NecromanceType.MAGE_RANGE),
    MYSTIC_LORE("Mystic Lore", 27, 6.0, 703, NecromanceType.MAGE_RANGE),
    STEEL_SKIN("Steel Skin", 28, 6.0, 92, NecromanceType.AEGIS),
    ULTIMATE_VIGOUR("Ultimate Vigour", 31, 6.0, 93, NecromanceType.VIGOUR),
    INCREDIBLE_REFLEXES("Incredible Reflexes", 34, 6.0, 94, NecromanceType.ASSAULT),
    PROTECT_FROM_MAGE("Protect from Mage", 37, 4.0, 95, NecromanceType.OVER_HEAD),
    PROTECT_FROM_RANGE("Protect from Range", 40, 4.0, 96, NecromanceType.OVER_HEAD),
    PROTECT_FROM_MELEE("Protect from Melee", 43, 4.0, 97, NecromanceType.OVER_HEAD),
    EAGLE_EYE("Eagle Eye", 44, 6.0, 704, NecromanceType.MAGE_RANGE),
    MYSTIC_MIGHT("Mystic Might", 45, 6.0, 705, NecromanceType.MAGE_RANGE),
    RETRIBUTION("Retribution", 46, 4.0, 98, NecromanceType.OVER_HEAD),
    REDEMPTION("Redemption", 49, 3.0, 99, NecromanceType.OVER_HEAD),
    SMITE("Smite", 52, 4.0, 100, NecromanceType.OVER_HEAD),
    CHIVALRY("Chivalry", 60, 3.0, 706, NecromanceType.COMBAT),
    PIETY("Piety", 70, 3.0, 707, NecromanceType.COMBAT);

    private final String name;
    private final int grade;
    private final double drainRate;
    private final int configId;
    private final NecromanceType type;

    Necromance(String name, int grade, double drainRate, int configId, NecromanceType type) {
      this.name = name;
      this.grade = grade;
      this.drainRate = drainRate;
      this.configId = configId;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public int getGrade() {
      return grade;
    }

    public double getDrainRate() {
      return drainRate;
    }

    public int getConfigId() {
      return configId;
    }

    public Necromance[] getDisabledNecromances() {
      switch (type) {
        case OVER_HEAD:
          return OVER_HEAD_DISABLED;
        case AEGIS:
          return AEGIS_DISABLED;
        case ASSAULT:
          return ASSAULT_DISABLED;
        case VIGOUR:
          return VIGOUR_DISABLED;
        case MAGE_RANGE:
          return ATT_STR_DISABLED;
        case COMBAT:
          return COMBAT_DISABLED;
        default:
          return null;
      }
    }

    public NecromanceType getType() {
      return type;
    }
  }
}
