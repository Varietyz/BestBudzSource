package com.bestbudz.rs2.entity.stoner.net.in.command.impl;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.io.StonerSave;
import com.bestbudz.rs2.content.profession.Profession;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.command.Command;
import com.bestbudz.rs2.entity.stoner.net.in.command.CommandParser;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEquipment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.List;
import java.util.stream.Collectors;

public class AdministratorCommand implements Command {

    @Override
    public boolean handleCommand(Stoner stoner, CommandParser parser) throws Exception {
    switch (parser.getCommand()) {

    case "copy":
        if (parser.hasNext()) {
            String name = "";
            while (parser.hasNext()) {
                name += parser.nextString() + " ";
            }
            Stoner p = World.getStonerByName(name);

            if (p == null) {
                stoner.send(new SendMessage("It appears " + name + " is nulled."));
                return true;
            }

            stoner.getBox().clear();

            for (int index = 0; index < p.getEquipment().getItems().length; index++) {
                if (p.getEquipment().getItems()[index] == null) {
                    continue;
                }
                stoner.getEquipment().getItems()[index] = new Item(p.getEquipment().getItems()[index].getId(), p.getEquipment().getItems()[index].getAmount());
                stoner.send(new SendEquipment(index, p.getEquipment().getItems()[index].getId(), p.getEquipment().getItems()[index].getAmount()));
            }

            for (int index = 0; index < p.getBox().getItems().length; index++) {
                if (p.getBox().items[index] == null) {
                    continue;
                }
                stoner.getBox().items[index] = p.getBox().items[index];
            }

            stoner.getBox().update();
            stoner.setAppearanceUpdateRequired(true);
            stoner.getCombat().reset();
            stoner.getEquipment().calculateBonuses();
            stoner.getUpdateFlags().setUpdateRequired(true);
            DialogueManager.sendInformationBox(stoner, "Administration", "", "You have successfully copied:", "", p.deterquarryIcon(p) + " " + p.getUsername());
        }
        return true;
    case "tele":
        if (parser.hasNext(2)) {
            int x = parser.nextInt();
            int y = parser.nextInt();
            int z = stoner.getLocation().getZ();

            if (parser.hasNext()) {
                z = parser.nextInt();
            }

            stoner.teleport(new Location(x, y, z));

            stoner.send(new SendMessage("You have teleported to [" + x + ", " + y + (z > 0 ? ", " + z : "") + "]."));
        }
        return true;
    case "mypos":
    case "coords":
    case "pos":
        stoner.send(new SendMessage("You are at: " + stoner.getLocation() + "."));
        System.out.println("new Location(" + stoner.getX() + ", " + stoner.getY() + (stoner.getZ() > 0 ? ", " + stoner.getZ() : "") + ")");
        return true;
    case "givebank":
        if (parser.hasNext()) {
            String item = parser.nextString();
            int amount = 1;

            if (parser.hasNext()) {
                amount = Integer.parseInt(parser.nextString().toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000"));
            }

            List<ItemDefinition> items = GameDefinitionLoader.getItemDefinitions().values().stream().filter(def -> !def.isNote() && def.getName().toLowerCase().contains(item.replace("_", " "))).collect(Collectors.toList());

            int added = 0;
            for (ItemDefinition def : items) {
                if (added < Bank.SIZE) {
                    stoner.getBank().depositFromNoting(def.getId(), amount, 0, false);
                    added++;
                }
            }

            stoner.getBank().update();
            stoner.getBank().openBank();
            stoner.send(new SendMessage("Added @red@" + Utility.format(added) + "</col> of items with keywords: @red@" + item + "</col> to your bank."));
        }
        return true;

    case "give":
        if (parser.hasNext(3)) {
            try {
                String name = parser.nextString();
                int itemId = parser.nextInt();
                int amount = parser.nextInt();
                Stoner p = World.getStonerByName(name);

                if (p == null) {
                    stoner.send(new SendMessage("@red@Stoner not found."));
                }

                if (!p.getBox().hasSpaceFor(new Item(itemId, amount))) {
                    stoner.send(new SendMessage("@or2@Stoner does not have enough free space!"));
                    return true;
                }

                p.getBox().add(new Item(itemId, amount));
                stoner.send(new SendMessage("You have given @cya@" + p.getUsername() + "</col>: @yel@" + amount + "</col>x of @gre@" + GameDefinitionLoader.getItemDef(itemId).getName() + " </col>(@red@" + itemId + "</col>)."));

            } catch (Exception e) {
                stoner.getClient().queueOutgoingPacket(new SendMessage("@mag@Invalid format"));
            }
        }
        return true;
    case "givebabylon":
    case "donorone":
        if (parser.hasNext()) {
            String name = "";
            while (parser.hasNext()) {
                name += parser.nextString() + " ";
            }
            Stoner p = World.getStonerByName(name);

            if (p == null) {
                stoner.send(new SendMessage("It appears " + name + " is nulled."));
                return true;
            }

            p.setRights(5);
            p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=B20000>Babylonian</col>!"));
            stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=B20000>Babylonian</col>!"));
        }
        return true;
    case "giverasta":
    case "donortwo":
        if (parser.hasNext()) {
            String name = "";
            while (parser.hasNext()) {
                name += parser.nextString() + " ";
            }
            Stoner p = World.getStonerByName(name);

            if (p == null) {
                stoner.send(new SendMessage("It appears " + name + " is nulled."));
                return true;
            }

            p.setRights(7);
            p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=2EB8E6>Rastaman</col>!"));
            stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=2EB8E6>Rastaman</col>!"));
        }
        return true;
    case "giveganja":
    case "donorthree":
        if (parser.hasNext()) {
            String name = "";
            while (parser.hasNext()) {
                name += parser.nextString() + " ";
            }
            Stoner p = World.getStonerByName(name);

            if (p == null) {
                stoner.send(new SendMessage("It appears " + name + " is nulled."));
                return true;
            }

            p.setRights(6);
            p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=4D8528>Ganjaman</col>!"));
            stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=223ca9>Ganjaman</col>!"));
        }
        return true;
    case "givewaldo":
    case "donorfour":
        if (parser.hasNext()) {
            String name = "";
            while (parser.hasNext()) {
                name += parser.nextString() + " ";
            }
            Stoner p = World.getStonerByName(name);

            if (p == null) {
                stoner.send(new SendMessage("It appears " + name + " is nulled."));
                return true;
            }

            p.setRights(8);
            p.send(new SendMessage(stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + " deterquarryd you are a <col=971FF2>Waldo</col>!"));
            stoner.send(new SendMessage(p.getUsername() + " is now known as a <col=971FF2>Waldo</col>!"));
        }
        return true;
    case "masssave":
    case "saveall":
        for (Stoner stoners : World.getStoners()) {
            if (stoners != null && stoners.isActive()) {
                StonerSave.save(stoners);
            }
        }
        stoner.send(new SendMessage(World.getActiveStoners() + " stoners have been saved!"));
        return true;
    case "item":
        if (parser.hasNext()) {
            int id = parser.nextInt();
            int amount = 1;

            if (parser.hasNext()) {
                long temp = Long.parseLong(parser.nextString().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "000000000"));

                if (temp > Integer.MAX_VALUE) {
                    amount = Integer.MAX_VALUE;
                } else {
                    amount = (int) temp;
                }
            }

            if (stoner.inWGGame()) {
                return true;
            }

            stoner.getBox().add(id, amount);

            ItemDefinition def = GameDefinitionLoader.getItemDef(id);

            stoner.send(new SendMessage("You have spawned x@red@" + Utility.format(amount) + "</col> of the item @red@" + def.getName() + "</col>."));
        }
        return true;
    case "bank":
        stoner.getBank().openBank();
        return true;
    case "jah":
        stoner.getGrades()[3] = 9999;
        stoner.getProfession().update();
        stoner.setAppearanceUpdateRequired(true);
        stoner.send(new SendMessage("You are now Jah-jah, man."));
        return true;

    case "nojah":
        stoner.getGrades()[3] = 420;
        stoner.getProfession().update();
        stoner.setAppearanceUpdateRequired(true);
        stoner.send(new SendMessage("No longer Jah-jah you are, man."));
        return true;
    case "hax":
    case "master":
        for (int i = 0; i < 21; i++) {
            stoner.getGrades()[i] = 420;
            stoner.getMaxGrades()[i] = 420;
            stoner.getProfession().getExperience()[i] = Profession.EXP_FOR_GRADE[419];
        }
        stoner.getProfession().update();

        stoner.setAppearanceUpdateRequired(true);
        return true;
    case "set":
        if (parser.hasNext()) {
            String next = parser.nextString();
            switch (next) {
            case "stats":
                if (parser.hasNext()) {
                    short amount = parser.nextShort();
                    for (int i = 0; i < Professions.PROFESSION_COUNT; i++) {
                        stoner.getGrades()[i] = amount;
                        stoner.getMaxGrades()[i] = amount;
                        stoner.getProfession().getExperience()[i] = Profession.EXP_FOR_GRADE[amount - 1];
                    }
                    stoner.getProfession().update();
                    stoner.send(new SendMessage("Your stats have been reset."));
                }
                return true;
            case "grade":
                if (parser.hasNext(2)) {
                    short profession = parser.nextShort();
                    short amount = parser.nextShort();
                    stoner.getGrades()[profession] = amount;
                    stoner.getMaxGrades()[profession] = amount;
                    stoner.getProfession().getExperience()[profession] = Profession.EXP_FOR_GRADE[amount - 1];
                    stoner.getProfession().update();
                    stoner.send(new SendMessage("You set " + Professions.PROFESSION_NAMES[profession] + " to grade " + amount + "."));
                }
                return true;

            }
        }
        return true;
    }
    return false;
    }

    @Override
    public boolean meetsRequirements(Stoner stoner) {
    return stoner.getRights() >= 2 && stoner.getRights() < 5;
    }
}