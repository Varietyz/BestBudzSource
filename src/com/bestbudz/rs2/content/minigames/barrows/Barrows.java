package com.bestbudz.rs2.content.minigames.barrows;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Barrows {

  public static final Item[] BROKEN_ITEM = {
    new Item(4860),
    new Item(4866),
    new Item(4872),
    new Item(4878),
    new Item(4884),
    new Item(4890),
    new Item(4896),
    new Item(4902),
    new Item(4908),
    new Item(4914),
    new Item(4920),
    new Item(4926),
    new Item(4932),
    new Item(4938),
    new Item(4944),
    new Item(4950),
    new Item(4956),
    new Item(4962),
    new Item(4968),
    new Item(4974),
    new Item(4980),
    new Item(4986),
    new Item(4992),
    new Item(4998)
  };

  public static final int BASE_REPAIR_COST = 250000;
  public static final int[][] BROKEN_BARROWS = {
    {4751, 4974},
    {4708, 4860},
    {4710, 4866},
    {4712, 4872},
    {4714, 4878},
    {4716, 4884},
    {4720, 4896},
    {4718, 4890},
    {4720, 4896},
    {4722, 4902},
    {4732, 4932},
    {4734, 4938},
    {4736, 4944},
    {4738, 4950},
    {4724, 4908},
    {4726, 4914},
    {4728, 4920},
    {4730, 4926},
    {4745, 4956},
    {4747, 4962},
    {4749, 4968},
    {4751, 4794},
    {4753, 4980},
    {4755, 4986},
    {4757, 4992},
    {4759, 4998}
  };

  public static void main(String[] args) {}

  public static int replaceBrokenItem(int itemId) {
    switch (itemId) {
      case 4860:
        return 4708;
      case 4866:
        return 4710;
      case 4872:
        return 4712;
      case 4878:
        return 4714;
      case 4884:
        return 4716;
      case 4890:
        return 4718;
      case 4896:
        return 4720;
      case 4902:
        return 4722;
      case 4908:
        return 4724;
      case 4914:
        return 4726;
      case 4920:
        return 4728;
      case 4926:
        return 4730;
      case 4932:
        return 4732;
      case 4938:
        return 4734;
      case 4944:
        return 4736;
      case 4950:
        return 4738;
      case 4956:
        return 4745;
      case 4962:
        return 4747;
      case 4968:
        return 4749;
      case 4974:
        return 4751;
      case 4980:
        return 4753;
      case 4986:
        return 4755;
      case 4992:
        return 4757;
      case 4998:
        return 4759;
    }
    return -1;
  }

  public enum Brother {
    AHRIM(1672),
    GUTHAN(1674),
    VERAC(1677),
    KARIL(1675),
    DHAROK(1673),
    TORAG(1676);

    private final int npcId;

    Brother(int npcId) {
      this.npcId = npcId;
    }

    public static boolean isBarrowsBrother(Mob mob) {
      for (Brother brother : values()) {
        if (brother.getNpcId() == mob.getId()) {
          return true;
        }
      }
      return false;
    }

    public static Brother getBarrowsBrother(Mob mob) {
      for (Brother brother : values()) {
        if (brother.getNpcId() == mob.getId()) {
          return brother;
        }
      }
      return null;
    }

    public int getNpcId() {
      return npcId;
    }
  }

  public static void onBarrowsDeath(Stoner p, Mob mob) {
    Brother bro = Brother.getBarrowsBrother(mob);

    if (bro == null) {
      return;
    }

    p.getKillRecord()[bro.ordinal()] = true;
    p.setBarrowsKC(p.getBarrowsKC() + 1);
  }
}
