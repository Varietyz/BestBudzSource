package com.bestbudz.rs2.content.profession.mage;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class Autocast {

  public static boolean clickButton(Stoner stoner, int id) {
    switch (id) {
      case 1093:
      case 1094:
        if (stoner.getMage().getSpellCasting().isAutocasting()) {
          stoner.getMage().getSpellCasting().disableAutocast();
          resetAutoCastInterface(stoner);
        } else {
          Item weapon = stoner.getEquipment().getItems()[3];
          if (weapon == null) {
            return true;
          }

          sendSelectionInterface(stoner, weapon.getId());
        }
        return true;
      case 7038:
        setAutocast(stoner, 1152);
        return true;
      case 7039:
        setAutocast(stoner, 1154);
        return true;
      case 7040:
        setAutocast(stoner, 1156);
        return true;
      case 7041:
        setAutocast(stoner, 1158);
        return true;
      case 7042:
        setAutocast(stoner, 1160);
        return true;
      case 7043:
        setAutocast(stoner, 1163);
        return true;
      case 7044:
        setAutocast(stoner, 1166);
        return true;
      case 7045:
        setAutocast(stoner, 1169);
        return true;
      case 7046:
        setAutocast(stoner, 1172);
        return true;
      case 7047:
        setAutocast(stoner, 1175);
        return true;
      case 7048:
        setAutocast(stoner, 1177);
        return true;
      case 7049:
        setAutocast(stoner, 1181);
        return true;
      case 7050:
        setAutocast(stoner, 1183);
        return true;
      case 7051:
        setAutocast(stoner, 1185);
        return true;
      case 7052:
        setAutocast(stoner, 1188);
        return true;
      case 7053:
        setAutocast(stoner, 1189);
        return true;
      case 7212:
        stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(0, 328));
        return true;
      case 51133:
        setAutocast(stoner, 12939);
        return true;
      case 51185:
        setAutocast(stoner, 12987);
        return true;
      case 51091:
        setAutocast(stoner, 12901);
        return true;
      case 24018:
        setAutocast(stoner, 12861);
        return true;
      case 51159:
        setAutocast(stoner, 12963);
        return true;
      case 51211:
        setAutocast(stoner, 13011);
        return true;
      case 51111:
        setAutocast(stoner, 12919);
        return true;
      case 51069:
        setAutocast(stoner, 12881);
        return true;
      case 51146:
        setAutocast(stoner, 12951);
        return true;
      case 51198:
        setAutocast(stoner, 12999);
        return true;
      case 51102:
        setAutocast(stoner, 12911);
        return true;
      case 51058:
        setAutocast(stoner, 12871);
        return true;
      case 51172:
        setAutocast(stoner, 12975);
        return true;
      case 51224:
        setAutocast(stoner, 13023);
        return true;
      case 51122:
        setAutocast(stoner, 12929);
        return true;
      case 51080:
        setAutocast(stoner, 12891);
        return true;
      case 24017:
        stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(0, 328));
        return true;
    }

    return false;
  }

  public static void resetAutoCastInterface(Stoner stoner) {
    if (!stoner.getMage().getSpellCasting().isAutocasting()) {
      stoner.getClient().queueOutgoingPacket(new SendConfig(108, 0));
      stoner.getClient().queueOutgoingPacket(new SendString("Spell", 18584));
      stoner.getEquipment().updateAssaultStyle();
    } else {
      stoner.getClient().queueOutgoingPacket(new SendConfig(43, 3));
      stoner.getClient().queueOutgoingPacket(new SendConfig(108, 1));
    }
  }

  public static void sendSelectionInterface(Stoner stoner, int weaponId) {

    switch (stoner.getMage().getSpellBookType()) {
      case ANCIENT:
        stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(0, 1689));
        break;
      case MODERN:
        stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(0, 1829));
        break;
    }
  }

  public static void setAutocast(Stoner stoner, int id) {
    stoner.getMage().getSpellCasting().enableAutocast(id);
    stoner.getClient().queueOutgoingPacket(new SendSidebarInterface(0, 328));
    resetAutoCastInterface(stoner);
  }
}
