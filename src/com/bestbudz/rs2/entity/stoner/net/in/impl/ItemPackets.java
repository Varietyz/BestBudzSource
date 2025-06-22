package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.DigTask;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.ItemInteraction;
import com.bestbudz.rs2.content.ItemOpening;
import com.bestbudz.rs2.content.MysteryBox;
import com.bestbudz.rs2.content.bank.Bank.RearrangeTypes;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.consumables.ConsumableType;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.content.dwarfcannon.DwarfMultiCannon;
import com.bestbudz.rs2.content.minigames.weapongame.WeaponGameStore;
import com.bestbudz.rs2.content.profession.forging.ForgingTask;
import com.bestbudz.rs2.content.profession.handiness.AmuletStringing;
import com.bestbudz.rs2.content.profession.handiness.JewelryCreationTask;
import com.bestbudz.rs2.content.profession.handinessnew.Handiness;
import com.bestbudz.rs2.content.profession.hunter.Impling.ImplingRewards;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.content.profession.mage.TabCreation;
import com.bestbudz.rs2.content.profession.mage.spells.BoltEnchanting;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.SerpentineHelmet;
import com.bestbudz.rs2.content.profession.pyromaniac.Pyromaniac;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.content.profession.thchempistry.CleanWeedTask;
import com.bestbudz.rs2.content.profession.thchempistry.PotionDecanting;
import com.bestbudz.rs2.content.profession.thchempistry.SuperCombatPotion;
import com.bestbudz.rs2.content.profession.thchempistry.THChempistryMasterProcessor;
import com.bestbudz.rs2.content.profession.weedsmoking.Weedsmoker;
import com.bestbudz.rs2.content.profession.woodcarving.Woodcarving;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCreating;
import com.bestbudz.rs2.entity.mob.bosses.Zulrah;
import com.bestbudz.rs2.entity.pets.PetManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class ItemPackets extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 40;
  }

  @SuppressWarnings("unused")
  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    if (stoner.isStunned() || stoner.isDead() || !stoner.getController().canClick()) {
      return;
    }
    int x;
    int mageId;
    int z;

    switch (opcode) {
      case 145:
			  // Add detailed logging for packet parsing
			  System.out.println("=== PACKET 145 DEBUG START ===");
			  System.out.println("Packet size: " + length);


			  int interfaceId = in.readShort(StreamBuffer.ValueType.A);
			  System.out.println("Read interfaceId: " + interfaceId);

			  int slot = in.readShort(StreamBuffer.ValueType.A);
			  System.out.println("Read slot: " + slot);

			  int itemId = in.readShort(StreamBuffer.ValueType.A);
			  System.out.println("Read itemId: " + itemId);

			  System.out.println("Interface Manager Main: " + stoner.getInterfaceManager().getMain());

			  if (BestbudzConstants.DEV_MODE) {
				  stoner.send(
					  new SendMessage(
						  "InterfaceId: "
							  + interfaceId
							  + " | Interface Manager: "
							  + stoner.getInterfaceManager().getMain()));
			  }

			  if (stoner.getMage().isTeleporting()) {
				  System.out.println("Player is teleporting - blocking action");
				  return;
			  }

        switch (interfaceId) {
          case 56503:
            if (stoner.getInterfaceManager().main == 56500) {
              WeaponGameStore.select(stoner, itemId);
            }
            break;
          case 59813:
            if (stoner.getInterfaceManager().main == 59800) {
              DropTable.itemDetails(stoner, itemId);
            }
            break;
          case 4393:
            if (stoner.getInterfaceManager().main == 48500) {
              stoner.getPriceChecker().withdraw(itemId, slot, 1);
            } else if (stoner.getInterfaceManager().main == 26700) {
              TabCreation.handle(stoner, itemId);
            } else if (stoner.getInterfaceManager().main == 42750) {
              BoltEnchanting.handle(stoner, itemId);
            } else if (stoner.getInterfaceManager().main == 59750) {
              String aName =
                  Utility.getAOrAn(GameDefinitionLoader.getItemDef(itemId).getName())
                      + " "
                      + GameDefinitionLoader.getItemDef(itemId).getName();
              stoner
                  .getUpdateFlags()
                  .sendForceMessage(
                      Utility.randomElement(BestbudzConstants.ITEM_IDENTIFICATION_MESSAGES)
                          .replaceAll("/s/", aName));
            }
            break;

          case 1119:
          case 1120:
          case 1121:
          case 1122:
          case 1123:
            ForgingTask.start(stoner, itemId, 1, interfaceId, slot);
            break;

			case 1688:
				System.out.println("=== EQUIPMENT UNEQUIP DEBUG ===");
				System.out.println("Equipment interface detected");
				System.out.println("Checking slot " + slot + " for item presence");

				// Log current equipment state
				System.out.println("Current equipment array:");
				for (int i = 0; i < stoner.getEquipment().getItems().length; i++) {
					Item equipItem = stoner.getEquipment().getItems()[i];
					if (equipItem != null && equipItem.getId() > 0) {
						System.out.println("  Slot " + i + ": " + equipItem.getId() + " x" + equipItem.getAmount());
					}
				}

				if (!stoner.getEquipment().slotHasItem(slot)) {
					System.out.println("ERROR: No item in slot " + slot + " - cannot unequip");
					stoner.send(new SendMessage("No item to unequip in that slot."));
					return;
				}

				Item itemToUnequip = stoner.getEquipment().getItems()[slot];
				System.out.println("Item to unequip: " + (itemToUnequip != null ? itemToUnequip.getId() + " x" + itemToUnequip.getAmount() : "null"));


				System.out.println("Calling stoner.getEquipment().unequip(" + slot + ")");
				stoner.getEquipment().unequip(slot);
				System.out.println("Unequip call completed successfully");
				break;

          case 4233:
          case 4239:
          case 4245:
            JewelryCreationTask.start(stoner, itemId, 1);
            break;

          case 5064:
            if (!stoner.getBox().slotContainsItem(slot, itemId)) {
              return;
            }

            if (stoner.getInterfaceManager().getMain() == 48500) {
              stoner.getPriceChecker().store(itemId, 1);
              return;
            }

            if (stoner.getInterfaceManager().hasBankOpen()) {
              bankItem(stoner, slot, itemId, 1);
              return;
            }
            break;

          case 5382:
            withdrawBankItem(stoner, slot, itemId, 1);
            break;

          case 3322:
            if (stoner.getTrade().trading()) handleTradeOffer(stoner, slot, itemId, 1);
            else if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().offer(itemId, 1, slot);
            }
            break;

          case 3415:
            if (stoner.getTrade().trading()) {
              handleTradeRemove(stoner, slot, itemId, 1);
            }
            break;

          case 6669:
            if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().withdraw(slot, 1);
            }
            break;

          case 3900:
            stoner.getShopping().sendSellPrice(itemId);
            break;

          case 3823:
            stoner.getShopping().sendBuyPrice(itemId);
        }

        break;

      case 117:
        interfaceId = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        itemId = in.readShort(true, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        slot = in.readShort(true, StreamBuffer.ByteOrder.LITTLE);

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 117 | interface " + interfaceId));
        }

        if ((interfaceId != 1688 && interfaceId != 56503)
            && (!stoner.getInterfaceManager().verify(interfaceId))) return;

        if (ToxicBlowpipe.itemOption(stoner, 2, itemId)) {
          return;
        }

        if (TridentOfTheSeas.itemOption(stoner, 2, itemId)) {
          return;
        }

        if (TridentOfTheSwamp.itemOption(stoner, 2, itemId)) {
          return;
        }

        if (SerpentineHelmet.itemOption(stoner, 2, itemId)) {
          return;
        }

        switch (interfaceId) {
          case 56503:
            if (stoner.getInterfaceManager().main == 56500) {
              WeaponGameStore.purchase(stoner, itemId);
            }
            break;
          case 2700:
            break;
          case 1688:
            if (itemId == 1704) {
              stoner
                  .getClient()
                  .queueOutgoingPacket(
                      new SendMessage("<col=C60DDE>This amulet is all out of charges."));
              return;
            }

            if (itemId == 11283) {
              stoner.getMage().onOperateDragonFireShield();
              return;
            }
            break;
          case 1119:
          case 1120:
          case 1121:
          case 1122:
          case 1123:
            ForgingTask.start(stoner, itemId, 5, interfaceId, slot);
            break;
          case 4233:
          case 4239:
          case 4245:
            JewelryCreationTask.start(stoner, itemId, 5);
            break;
          case 5064:
            if (!stoner.getBox().slotContainsItem(slot, itemId)) {
              return;
            }

            if (stoner.getInterfaceManager().getMain() == 48500) {
              stoner.getPriceChecker().store(itemId, 5);
              return;
            }

            if (stoner.getInterfaceManager().hasBankOpen()) {
              bankItem(stoner, slot, itemId, 5);
            }
            break;

          case 4393:
            if (stoner.getInterfaceManager().main == 48500) {
              stoner.getPriceChecker().withdraw(itemId, slot, 5);
            } else if (stoner.getInterfaceManager().main == 26700) {
              TabCreation.getInfo(stoner, itemId);
            }
            break;

          case 5382:
            withdrawBankItem(stoner, slot, itemId, 5);
            break;
          case 3322:
            if (stoner.getTrade().trading()) handleTradeOffer(stoner, slot, itemId, 5);
            else if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().offer(itemId, 5, slot);
            }
            break;
          case 6669:
            if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().withdraw(slot, 5);
            }
            break;
          case 3415:
            if (stoner.getTrade().trading()) {
              handleTradeRemove(stoner, slot, itemId, 5);
            }
            break;
          case 3900:
            stoner.getShopping().buy(itemId, 1, slot);
            break;
          case 3823:
            stoner.getShopping().sell(itemId, 1, slot);
        }

        break;
      case 43:
        interfaceId = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        itemId = in.readShort(StreamBuffer.ValueType.A);
        slot = in.readShort(StreamBuffer.ValueType.A);

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 43 | interface " + interfaceId));
        }
        if (!stoner.getInterfaceManager().verify(interfaceId)) {
          return;
        }

        switch (interfaceId) {
          case 4393:
            stoner.getPriceChecker().withdraw(itemId, slot, 10);
            break;
          case 2700:
            break;
          case 1119:
          case 1120:
          case 1121:
          case 1122:
          case 1123:
            ForgingTask.start(stoner, itemId, 10, interfaceId, slot);
            break;
          case 4233:
          case 4239:
          case 4245:
            JewelryCreationTask.start(stoner, itemId, 10);
            break;
          case 5064:
            if (!stoner.getBox().slotContainsItem(slot, itemId)) {
              return;
            }

            if (stoner.getInterfaceManager().getMain() == 48500) {
              stoner.getPriceChecker().store(itemId, 10);
              return;
            }

            if (stoner.getInterfaceManager().hasBankOpen()) bankItem(stoner, slot, itemId, 10);

            break;
          case 5382:
            withdrawBankItem(stoner, slot, itemId, 10);
            break;
          case 3322:
            if (stoner.getTrade().trading()) handleTradeOffer(stoner, slot, itemId, 10);
            else if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().offer(itemId, 10, slot);
            }
            break;
          case 6669:
            if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().withdraw(slot, 10);
            }
            break;
          case 3415:
            if (stoner.getTrade().trading()) {
              handleTradeRemove(stoner, slot, itemId, 10);
            }
            break;
          case 3900:
            stoner.getShopping().buy(itemId, 5, slot);
            break;
          case 3823:
            stoner.getShopping().sell(itemId, 5, slot);
        }

        break;
      case 129:
        slot = in.readShort(StreamBuffer.ValueType.A);
        interfaceId = in.readShort();
        itemId = in.readShort(StreamBuffer.ValueType.A);

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 129 | interface " + interfaceId));
        }

        if (!stoner.getInterfaceManager().verify(interfaceId)) {
          return;
        }
        switch (interfaceId) {
          case 4393:
            stoner
                .getPriceChecker()
                .withdraw(itemId, slot, stoner.getPriceChecker().getItemAmount(itemId));
            break;
          case 2700:
            break;
          case 5064:
            if (!stoner.getBox().slotContainsItem(slot, itemId)) {
              return;
            }

            if (stoner.getInterfaceManager().getMain() == 48500) {
              stoner.getPriceChecker().store(itemId, stoner.getBox().getItemAmount(itemId));
              return;
            }

            if (stoner.getInterfaceManager().hasBankOpen())
              bankItem(stoner, slot, itemId, 2147483647);
            break;
          case 5382:
            withdrawBankItem(stoner, slot, itemId, 2147483647);
            break;
          case 3322:
            if (stoner.getTrade().trading()) handleTradeOffer(stoner, slot, itemId, 2147483647);
            else if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().offer(itemId, 2147483647, slot);
            }
            break;
          case 6669:
            if (stoner.getDueling().isStaking()) {
              stoner.getDueling().getContainer().withdraw(slot, 2147483647);
            }
            break;
          case 3415:
            if (stoner.getTrade().trading()) {
              handleTradeRemove(stoner, slot, itemId, 2147483647);
            }
            break;
          case 3900:
            stoner.getShopping().buy(itemId, 10, slot);
            break;
          case 3823:
            stoner.getShopping().sell(itemId, 10, slot);
        }

        break;
      case 41:
        itemId = in.readShort();
        slot = in.readShort(StreamBuffer.ValueType.A);
        in.readShort();

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 41"));
        }

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        if (ItemInteraction.clickPouch(stoner, itemId, 2)) {
          return;
        }

        switch (itemId) {
          case 4079:
            stoner.getUpdateFlags().sendAnimation(1458, 0);
            return;
        }

        stoner.getEquipment().equip(stoner.getBox().get(slot), slot);
        break;
      case 214:
        interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        int transfer = in.readByte(StreamBuffer.ValueType.C);
        int fromSlot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        int toSlot = in.readShort(StreamBuffer.ByteOrder.LITTLE);

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 214"));
        }

        switch (interfaceId) {
          case 5382:
            if (stoner.getTrade().trading()) {
              stoner.send(new SendMessage("You can not do that right now!"));
              return;
            }

            if (!stoner.getBank().isSearching()) {
              if (transfer == 2) {
                stoner.getBank().itemToTab(fromSlot, toSlot, true);
              } else {
                if (transfer == 1) {
                  int fromTab = stoner.getBank().getData(fromSlot, 0);
                  int toTab = stoner.getBank().getData(toSlot, 0);
                  stoner.getBank().changeTabAmount(toTab, 1, false);
                  stoner.getBank().changeTabAmount(fromTab, -1, true);
                  RearrangeTypes temp = stoner.getBank().rearrangeType;
                  stoner.getBank().rearrangeType = RearrangeTypes.INSERT;
                  stoner.getBank().swap(toSlot - (toTab > fromTab ? 1 : 0), fromSlot);
                  stoner.getBank().rearrangeType = temp;
                  stoner.getBank().update();
                } else {
                  RearrangeTypes temp = stoner.getBank().rearrangeType;
                  stoner.getBank().rearrangeType = RearrangeTypes.SWAP;
                  stoner.getBank().swap(toSlot, fromSlot);
                  stoner.getBank().rearrangeType = temp;
                }
              }
            }
            break;
          case 3214:
          case 5064:
            stoner.getBox().swap(toSlot, fromSlot, false);
            break;
        }

        break;
      case 87:
        itemId = in.readShort(StreamBuffer.ValueType.A);
        in.readShort();
        slot = in.readShort(StreamBuffer.ValueType.A);

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        if (stoner.getMage().isTeleporting() || !stoner.getController().canDrop(stoner)) {
          return;
        }

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 87"));
        }

        if (ToxicBlowpipe.itemOption(stoner, 4, itemId)) {
          return;
        }

        if (TridentOfTheSeas.itemOption(stoner, 4, itemId)) {
          return;
        }

        if (TridentOfTheSwamp.itemOption(stoner, 4, itemId)) {
          return;
        }

        if (SerpentineHelmet.itemOption(stoner, 4, itemId)) {
          return;
        }

        if (itemId == 4045) {
          stoner.getUpdateFlags().sendAnimation(new Animation(827));
          stoner.getBox().remove(new Item(4045, 1));
          stoner.hit(new Hit(15));
          stoner.getUpdateFlags().sendForceMessage("Ow! That really hurt my soul!");
          return;
        }

        if (PetManager.spawnPet(stoner, itemId, false)) {
          return;
        }

        for (int index = 0; index < BestbudzConstants.ITEM_DISMANTLE_DATA.length; index++) {
          if (itemId == BestbudzConstants.ITEM_DISMANTLE_DATA[index][0]) {
            stoner.getBox().remove(itemId, 1);
            stoner
                .getBox()
                .addOrCreateGroundItem(BestbudzConstants.ITEM_DISMANTLE_DATA[index][1], 1, true);
            stoner
                .getBox()
                .addOrCreateGroundItem(BestbudzConstants.ITEM_DISMANTLE_DATA[index][1], 1, true);
            stoner.send(
                new SendMessage(
                    "You have dismantled your "
                        + GameDefinitionLoader.getItemDef(itemId).getName()
                        + "."));
            stoner.send(new SendRemoveInterfaces());
            return;
          }
        }

        if (!Item.getDefinition(itemId).isTradable()
            || Item.getDefinition(itemId).getName().contains("Clue scroll")) {
          stoner.start(
              new OptionDialogue(
                  "</col>Drop and loose forever",
                  p -> {
                    stoner.getBox().remove(itemId, 1);
                    stoner.send(
                        new SendMessage(
                            "Your "
                                + GameDefinitionLoader.getItemDef(itemId).getName()
                                + " has been dropped and lost forever."));
                    stoner.send(new SendRemoveInterfaces());
                  },
                  "Keep " + GameDefinitionLoader.getItemDef(itemId).getName(),
                  p -> {
                    stoner.send(new SendRemoveInterfaces());
                  }));
          return;
        }

        stoner.getGroundItems().drop(itemId, slot);
        break;
      case 236:
        int y = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        itemId = in.readShort();
        x = in.readShort(StreamBuffer.ByteOrder.LITTLE);

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        stoner.getCombat().reset();

        stoner.getGroundItems().pickup(x, y, itemId);
        break;
      case 53:
        int firstSlot = in.readShort();
        int secondSlot = in.readShort(StreamBuffer.ValueType.A);

        if ((!stoner.getBox().slotHasItem(firstSlot))
            || (!stoner.getBox().slotHasItem(secondSlot))) {
          return;
        }

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        Item usedWith = stoner.getBox().get(firstSlot);
        Item itemUsed = stoner.getBox().get(secondSlot);

        if ((usedWith == null) || (itemUsed == null)) {
          return;
        }

        if ((usedWith.getId() == 985 && itemUsed.getId() == 987)
            || (usedWith.getId() == 987 && itemUsed.getId() == 985)) {
          stoner.getBox().remove(985, 1);
          stoner.getBox().remove(987, 1);
          stoner.getBox().add(989, 1);
          return;
        }

        if (Woodcarving.SINGLETON.itemOnItem(stoner, usedWith, itemUsed)) {
          return;
        }

        if (Handiness.SINGLETON.itemOnItem(stoner, usedWith, itemUsed)) {
          return;
        }

        if (ItemCreating.handle(stoner, itemUsed.getId(), usedWith.getId())) {
          return;
        }

        if (ToxicBlowpipe.itemOnItem(stoner, itemUsed, usedWith)) {
          return;
        }

        if (TridentOfTheSeas.itemOnItem(stoner, itemUsed, usedWith)) {
          return;
        }

        if (TridentOfTheSwamp.itemOnItem(stoner, itemUsed, usedWith)) {
          return;
        }

        if (SuperCombatPotion.itemOnItem(stoner, itemUsed, usedWith)) {
          return;
        }

        if (SerpentineHelmet.itemOnItem(stoner, itemUsed, usedWith)) {
          return;
        }

        if (itemUsed.getId() == 1759 || usedWith.getId() == 1759) {
          AmuletStringing.stringAmulet(stoner, itemUsed.getId(), usedWith.getId());
          return;
        }

// Replace the entire THC-hempistry block with this:
		  if (THChempistryMasterProcessor.SINGLETON.handleItemOnItem(stoner, itemUsed, usedWith)) {
			  return;
		  }

// Keep the potion decanting at the end:
		  if (PotionDecanting.decant(stoner, firstSlot, secondSlot)) {
			  return;
		  }

        break;
      case 25:
        in.readShort();
        int itemInInven = in.readShort(StreamBuffer.ValueType.A);
        int groundItem = in.readShort();
        y = in.readShort(StreamBuffer.ValueType.A);
        z = stoner.getLocation().getZ();
        in.readShort();
        x = in.readShort();

      case 237:
        slot = in.readShort();
        itemId = in.readShort(StreamBuffer.ValueType.A);
        interfaceId = in.readShort();
        mageId = in.readShort(StreamBuffer.ValueType.A);

        if (!stoner.getInterfaceManager().verify(interfaceId)) {
          return;
        }

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        stoner.getAttributes().set("mageitem", Integer.valueOf(itemId));
        stoner.getMage().useMageOnItem(itemId, mageId);
        break;
      case 181:
        y = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        itemId = in.readShort();
        x = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        mageId = in.readShort(StreamBuffer.ValueType.A);
        break;
      case 253:
        x = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        y = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        itemId = in.readShort(StreamBuffer.ValueType.A);
        z = stoner.getLocation().getZ();

        break;
      case 122:
        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 122"));
        }

        interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        slot = in.readShort(StreamBuffer.ValueType.A);
        itemId = in.readShort(StreamBuffer.ByteOrder.LITTLE);

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (ClueScrollManager.SINGLETON.clickItem(stoner, itemId)) {
          return;
        }

        if (ItemOpening.openSet(stoner, itemId)) {
          return;
        }

        if (ItemInteraction.clickPouch(stoner, itemId, 1)) {
          return;
        }

        if (itemId == 4079) {
          stoner.getUpdateFlags().sendAnimation(1457, 0);
          return;
        }

        if (DwarfMultiCannon.setCannonBase(stoner, itemId)) {
          return;
        }

        if (Pyromaniac.burnin(stoner, itemId, slot, interfaceId)) {
          return;
        }

        if (Weedsmoker.burnin(stoner, itemId, slot, interfaceId)) {
          return;
        }

        if ((stoner.getConsumables().consume(itemId, slot, ConsumableType.FOOD))
            || (stoner.getConsumables().consume(itemId, slot, ConsumableType.POTION))) {
          return;
        }

        if (stoner.getMage().clickMageItems(itemId)) {
          return;
        }
        switch (itemId) {
          case 6199:
            MysteryBox.open(stoner);
            break;

          case 12846:
            break;

          case 405:
            stoner.getBox().remove(itemId, 1);
            int random = Utility.random(10000) + Utility.random(2500) + Utility.random(666);
            stoner.getBox().add(995, random);
            stoner.send(
                new SendMessage("You have found " + random + " bestbucks inside the casket"));
            break;
          case 12938:
            stoner.getBox().remove(12938, 1);
            stoner.getMage().teleport(2268, 3070, stoner.getIndex() << 2, TeleportTypes.SPELL_BOOK);
            TaskQueue.queue(
                new Task(5) {
                  @Override
                  public void execute() {
                    Zulrah mob =
                        new Zulrah(stoner, new Location(2266, 3073, stoner.getIndex() << 2));
                    mob.face(stoner);
                    mob.getUpdateFlags().sendAnimation(new Animation(5071));
                    stoner.face(mob);
                    stoner.send(new SendMessage("Welcome to Zulrah's shrine."));
                    DialogueManager.sendStatement(stoner, "Welcome to Zulrah's shrine.");
                    stop();
                  }

                  @Override
                  public void onStop() {}
                });
            break;
          case 2528:
            stoner.send(new SendInterface(2808));
            break;
          case 952:
            TaskQueue.queue(new DigTask(stoner));
            return;
          case 4155:
            if (!stoner.getMercenary().hasTask()) {
              DialogueManager.sendStatement(stoner, "You currently do not have a task!");
              return;
            }
            DialogueManager.sendStatement(
                stoner,
                "You have been tasked to kill:",
                stoner.getMercenary().getAmount() + " " + stoner.getMercenary().getTask());
            return;

          case 13188:
            stoner.getEquipment().equip(stoner.getBox().get(slot), slot);
            break;
        }

        CleanWeedTask.attemptWeedCleaning(stoner, slot);
        break;
      case 16:
        itemId = in.readShort(StreamBuffer.ValueType.A);
        slot = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);

        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 16"));
        }

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (ItemInteraction.clickPouch(stoner, itemId, 3)) {
          return;
        }

        if (ImplingRewards.impReward.containsKey(itemId)) {
          ImplingRewards.lootImpling(stoner, itemId);
          return;
        }

        if (ToxicBlowpipe.itemOption(stoner, 1, itemId)) {
          return;
        }

        if (TridentOfTheSeas.itemOption(stoner, 1, itemId)) {
          return;
        }

        if (TridentOfTheSwamp.itemOption(stoner, 1, itemId)) {
          return;
        }

        if (SerpentineHelmet.itemOption(stoner, 1, itemId)) {
          return;
        }

        if (itemId == 4079) {
          stoner.getUpdateFlags().sendAnimation(1459, 0);
          return;
        }

        if (itemId == 11283) {
          stoner
              .getClient()
              .queueOutgoingPacket(
                  new SendMessage(
                      "Your shield has "
                          + stoner.getMage().getDragonFireShieldCharges()
                          + " charges."));
          return;
        }

        switch (itemId) {
          case 11802:
          case 11804:
          case 11806:
          case 11808:
            int[][] items = {{11802, 11810}, {11804, 11812}, {11806, 11814}, {11808, 11816}};
            if (stoner.getBox().getFreeSlots() < 1) {
              DialogueManager.sendItem1(
                  stoner, "You need at least one free slot to dismantle your godsword.", itemId);
              return;
            }
            for (int i = 0; i < items.length; i++) {
              if (itemId == items[i][0] && stoner.getBox().hasItemAmount(items[i][0], 1)) {
                stoner.getBox().remove(items[i][0], 1);
                stoner.getBox().add(items[i][1], 1);
                stoner.getBox().add(11798, 1);
                DialogueManager.sendItem2zoom(
                    stoner,
                    "You carefully attempt to dismantly your godsword...",
                    "You were successful!",
                    items[i][1],
                    11798);
                break;
              }
            }
            break;
        }

        break;
      case 75:
        if (BestbudzConstants.DEV_MODE) {
          stoner.send(new SendMessage("Item packet 75"));
        }

        interfaceId = in.readShort(StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
        slot = in.readShort(StreamBuffer.ByteOrder.LITTLE);
        itemId = in.readShort(true, StreamBuffer.ValueType.A);

        if (!stoner.getBox().slotContainsItem(slot, itemId)) {
          return;
        }

        if (stoner.getMage().isTeleporting()) {
          return;
        }

        if (ToxicBlowpipe.itemOption(stoner, 3, itemId)) {
          return;
        }

        if (TridentOfTheSeas.itemOption(stoner, 3, itemId)) {
          return;
        }

        if (TridentOfTheSwamp.itemOption(stoner, 3, itemId)) {
          return;
        }

        if (itemId == 1704) {
          stoner
              .getClient()
              .queueOutgoingPacket(
                  new SendMessage("<col=C60DDE>This amulet is all out of charges."));
          return;
        }

        if (itemId == 4079) {
          stoner.getUpdateFlags().sendAnimation(1460, 0);
          return;
        }

        if (itemId == 995) {
          stoner.getPouch().addPouch();
          return;
        }

        break;
    }
  }

  public void handleTradeOffer(Stoner stoner, int slot, int itemId, int amount) {
    stoner.getTrade().getContainer().offer(itemId, amount, slot);
  }

  public void handleTradeRemove(Stoner stoner, int slot, int itemId, int amount) {
    stoner.getTrade().getContainer().withdraw(slot, amount);
  }

  public void withdrawBankItem(Stoner stoner, int slot, int itemId, int amount) {
    stoner.getBank().withdraw(itemId, amount);
  }

  public void bankItem(Stoner stoner, int slot, int itemId, int amount) {
    stoner.getBank().deposit(itemId, amount, slot);
  }
}
