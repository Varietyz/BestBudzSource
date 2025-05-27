package com.mayhem.rs2.entity.item;

import java.util.ArrayList;

import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.Prestige;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.impl.MacDialogue;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles creating items
 * @author Daniel
 *
 */
public enum ItemCreating {
	
	//ITEM("", new int[] {  }, new int[] {  }, new int[][] { }),
	
	CAP_AND_GOGGLES("Cap and goggles", new int[] { 9946 }, new int[] { 9945, 9472 }, new int[][] { }),
	DARK_TOTEM("Dark totem", new int[] { 19685 }, new int[] { 19679, 19681, 19683 }, new int[][] { }),
	TOXIC_STAFF_OF_THE_DEAD("Toxic staff of the dead", new int[] { 12904 }, new int[] { 11791, 12932 }, new int[][] { { Skills.CRAFTING, 59 } }),
	TRIDENT_OF_THE_SWAMP("Trident of the swamp", new int[] { 12899 }, new int[] { 12932, 11907 }, new int[][] { { Skills.CRAFTING, 59 } }),
	DRAGON_PICKAXE("Dragon pickaxe", new int[] { 12797 }, new int[] { 11920, 12800 }, new int[][] { }),
	ODIUM_WARD("Odium ward", new int[] { 11926 }, new int[] { 11928, 11929, 11930 }, new int[][] { }),
	MALEDICTION_WARD("Malediction ward", new int[] { 11924 }, new int[] { 11931, 11932, 11933 }, new int[][] { }),	
	ODIUM_WARD_OR("Odium ward (or)", new int[] { 12807 }, new int[] { 11926, 12802 }, new int[][] { }),	
	MALEDICTION_WARD_OR("Malediction ward (or)", new int[] { 12806 }, new int[] { 11924, 12802 }, new int[][] { }),	
	MAGIC_SHORTBOW_INFUSED("Magic shortbow (i)", new int[] { 12788 }, new int[] { 12786, 861 }, new int[][] { }),
	FURY_AMULET_KIT("Amulet of fury (or)", new int[] { 12436 }, new int[] { 6585, 12526 }, new int[][] { }),
	ABYSSAL_TENTACLE("Abyssal tentacle", new int[] { 12006 }, new int[] { 12004, 4151 }, new int[][] { }),
	ARMADYL_GODSWORD("Armadyl godsword", new int[] { 11802 }, new int[] { 11798, 11810 }, new int[][] { }),
	BANDOS_GODSWORD("Bandos godsword", new int[] { 11804 }, new int[] { 11798, 11812 }, new int[][] { }),
	BLOWPIPE("Toxic blowpipe", new int[] { 12924 }, new int[] { 12922, 1755 }, new int[][] { }),	
	DARK_INFINITY("Dark infinity set", new int[] { 12458, 12457, 12459 }, new int[] { 12528, 6916, 6918, 6924 }, new int[][] { }),	
	GODSWORD_BLADE("Godsword blade", new int[] { 11798 }, new int[] { 11818, 11820, 11822 }, new int[][] { }),
	LIGHT_INFINITY("Light infinity set", new int[] { 12420, 12419, 12421 }, new int[] { 12530, 6916, 6918, 6924 }, new int[][] { }),
	MYSTIC_STEAM_BATTLESTAFF("Mystic steam battlestaff", new int[] { 12796 }, new int[] { 11789, 12798 }, new int[][] { }),
	STEAM_BATTLESTAFF("Steam battlestaff", new int[] { 12795 }, new int[] { 11787, 12798 }, new int[][] { }),
	SARADOMIN_BLESSED_SWORD("Saradomin blessed sword", new int[] { 12809 }, new int[] { 12804, 11838 }, new int[][] { }),
	SARADOMIN_GODSWORD("Saradomin godsword", new int[] { 11806 }, new int[] { 11798, 11814 }, new int[][] { }),
	ZAMORAK_GODSWORD("Zamorak godsword", new int[] { 11808 }, new int[] { 11798, 11816 }, new int[][] { }),
	SERPENTINE_VISAGE("Serpentine visage", new int[] { 12929 }, new int[] { 12927, 1755 }, new int[][] { { Skills.CRAFTING, 52 } }),
	DRAGONFIRE_SHIELD("Dragonfire shield", new int[] { 11283 }, new int[] { 1540, 11286 }, new int[][] { { Skills.SMITHING, 90 } }),	
	DRAGON_SHIELD_KIT("Dragon sq shield (g)", new int[] { 12418 }, new int[] { 12532, 1187 }, new int[][] { }),	
	DRAGON_CHAINBODY_KIT("Dragon chainbody (g)", new int[] { 12414 }, new int[] { 12534, 2513 }, new int[][] { }),		
	DRAGON_CHAINBODY_KIT_2("Dragon chainbody (g)", new int[] { 12414 }, new int[] { 12534, 3140 }, new int[][] { }),	
	DRAGON_HELM_KIT("Dragon full helm (g)", new int[] { 12417 }, new int[] { 12538, 11335 }, new int[][] { }),
	DRAGON_PLATELEGS_KIT("Dragon platelegs (g)", new int[] { 12415 }, new int[] { 12536, 4087 }, new int[][] { }),
	DRAGON_PLATESKIRT_KIT("Dragon plateskirt (g)", new int[] { 12416 }, new int[] { 12536, 4585 }, new int[][] { }),
	PRIMORDIAL_BOOTS("Primordial Boots", new int[] { 13239 }, new int[] { 13231, 11840 }, new int[][] { { Skills.RUNECRAFTING, 60} }),
	ETERNAL_BOOTS("Eternal Boots", new int[] { 13235 }, new int[] { 13227, 6920 }, new int[][] { { Skills.RUNECRAFTING, 60} }),
	PEGASIAN_BOOTS("Pegasian Boots", new int[] { 13237 }, new int[] { 13229, 2577 }, new int[][] { { Skills.RUNECRAFTING, 60} }),
	INFERNAL_PICKAXE("Infernal Pickaxe", new int[] { 13243 }, new int[] { 13233, 11920 }, new int[][] { }),
	INFERNAL_AXE("Infernal Axe", new int[] { 13241 }, new int[] { 13233,  6739 }, new int[][] { }),
	DRAGON_SCIMITAR_TRIMMED("Dragon scimitar (g)", new int[] { 20000 }, new int[] { 20002,  4587 }, new int[][] { }),
	DRAGON_DEFENDER_TRIMMED("Dragon defender (g)", new int[] { 19722 }, new int[] { 20143,  12954 }, new int[][] { }),
	AMULET_OF_TORTURE_OR("Amulet of torture (or)", new int[] { 20366 }, new int[] { 20062,  19553 }, new int[][] { }),
	ARMADYL_GODSWORD_OR("Armadyl godsword (or)", new int[] { 20368 }, new int[] { 20068,  11802 }, new int[][] { }),
	BANDOS_GODSWORD_OR("Bandos godsword (or)", new int[] { 20370 }, new int[] { 20071,  11804 }, new int[][] { }),
	SARADOMIN_GODSWORD_OR("Saradomin godsword (or)", new int[] { 20372 }, new int[] { 20074,  11806 }, new int[][] { }),
	ZAMORAK_GODSWORD_OR("Zamorak godsword (or)", new int[] { 20374 }, new int[] { 20077,  11808 }, new int[][] { }),
	VOLCANIC_WHIP("Volcanic whip", new int[] { 12773 }, new int[] { 4151,  12771 }, new int[][] { }),
	FROZEN_WHIP("Frozen whip", new int[] { 12774 }, new int[] { 4151,  12769 }, new int[][] { }),
	SLAYER_HELM_I("Slayer helmet (i)", new int[] { 11865 }, new int[] { 11864, 19668 }, new int[][] { }),
	SLAYER_HELM("Slayer helmet", new int[] { 11864 }, new int[] { 8921, 4166, 4551, 4155, 4168, 4164 }, new int[][] { }),
	RED_SLAYER_HELM_I("Red slayer helmet (i)", new int[] { 19649 }, new int[] { 19647, 19668 }, new int[][] { }),
	GREEN_SLAYER_HELM_I("Green slayer helmet (i)", new int[] { 19645 }, new int[] { 19643, 19668 }, new int[][] { }),
	BLACK_SLAYER_HELM_I("Black slayer helmet (i)", new int[] { 19641 }, new int[] { 19639, 19668 }, new int[][] { }),
	RING_OF_WEALTH_I("Ring of wealth (i)", new int[] { 12785 }, new int[] { 2572, 12783 }, new int[][] { }),
	BANDOS_CHESTPLATE_I("Bandos chestplate (i)", new int[] { 7812 }, new int[] { 19742, 11832 }, new int[][] { }),
	BANDOS_TASSETS_I("Bandos tassets (i)", new int[] { 7813 }, new int[] { 19742, 11834 }, new int[][] { }),
	BANDOS_BOOTS_I("Bandos boots (i)", new int[] { 7814 }, new int[] { 19742, 11836 }, new int[][] { }),
	ARMADYL_HELMET_I("Armadyl helmet (i)", new int[] { 7815 }, new int[] { 19744, 11826 }, new int[][] { }),
	ARMADYL_CHESTPLATE_I("Armadyl chestplate (i)", new int[] { 7816 }, new int[] { 19744, 11828 }, new int[][] { }),
	ARMADYL_PLATESKIRT_I("Armadyl plateskirt (i)", new int[] { 7817 }, new int[] { 19744, 11830 }, new int[][] { }),
	DARK_BOW_RECOLOR("Dark Bow", new int[] { 12765 }, new int[] { 11235, 12757 }, new int[][] { }),
	DARK_BOW_RECOLOR2("Dark Bow", new int[] { 12766 }, new int[] { 11235, 12758 }, new int[][] { }),
	DARK_BOW_RECOLOR3("Dark Bow", new int[] { 12767 }, new int[] { 11235, 12759 }, new int[][] { }),
	DARK_BOW_RECOLOR4("Dark Bow", new int[] { 12768 }, new int[] { 11235, 12760 }, new int[][] { }),
	LIGHT_BALLISTA("Light ballista", new int[] { 19478 }, new int[] { 19586, 19592, 19601 }, new int[][] { { Skills.FLETCHING, 85  } }),
	HEAVY_BALLISTA("Heavy ballista", new int[] { 19481 }, new int[] { 19589, 19592, 19601 }, new int[][] { { Skills.FLETCHING, 85  } }),
	DRAGON_SQ("Dragon sq shield", new int[] { 1187 }, new int[] { 2366,  2368 }, new int[][] { });
	
	private final String name;
	private final int[] product;
	private final int[] items;
	private final int[][] skills;
	private ItemCreating(String name, int[] product, int[] items, int[][] skills) {
		this.name = name;
		this.product = product;
		this.items = items;
		this.skills = skills;
	}
	
	public String getName() {
		return name;
	}
	
	public int[] getProduct() {
		return product;
	}
	
	public int[] getItems() {
		return items;
	}
	
	public int[][] getSkills() {
		return skills;
	}
	
	/**
	 * Gets the items 
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static ItemCreating getDataForItems(int item1, int item2) {
		for(ItemCreating data : ItemCreating.values()) {
			int found = 0;
			for (int it : data.items) {
				if (it == item1 || it == item2) {
					found++;
				}
			}
			if (found >= 2) {
				return data;				
			}
		}
		return null;
	}
	
	/**
	 * Handles creating the items
	 * @param player
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static boolean handle(Player player, int item1, int item2) {
		if (item1 == item2) {
			return false;			
		}
		
		ItemCreating data = ItemCreating.getDataForItems(item1, item2);
		
		MacDialogue.maxCapeCombining(player, item1, item2);
		
		if (data == null || !player.getInventory().hasItemId(item1) || !player.getInventory().hasItemId(item2)) {
			return false;			
		}
		
		for (int i = 0; i < data.getSkills().length; i++) {
			if (player.getSkill().getLevels()[data.getSkills()[i][0]] < data.getSkills()[i][1]) {
				DialogueManager.sendItem1(player, "You need a " + Prestige.getSkillName(data.getSkills()[i][0]) + " level of " + data.getSkills()[i][1] + " to do this!", data.getProduct()[0]);
				return true;
			}
		}
		
		ArrayList<String> required = new ArrayList<String>();
		
		for (int reqItem : data.items) {
			if (!player.getInventory().hasItemId(reqItem)) {
				player.send(new SendMessage("You do not have all the needed items to do this!"));
				required.add(GameDefinitionLoader.getItemDef(reqItem).getName());
				continue;
			}
		}
		
		if (!required.isEmpty()) {
			player.send(new SendMessage("@red@To make " + data.getName() + " you need: " + required + "."));
			required.clear();
			return false;
		}
		
		if (player.getInventory().getFreeSlots() < data.product.length) {
			player.send(new SendMessage("You need at least " + data.product.length + " free inventory space(s) to do this!"));
			return false;
		}
		
		for (int reqItem : data.items) {
			if (reqItem == 1755 || reqItem == 1595) {
				continue;				
			}
			player.getInventory().remove(reqItem);
		}
	
		for (int product : data.product) {
			player.getInventory().add(product, 1);
		}
		
		DialogueManager.sendItem1(player, "@dre@You have created " + Utility.getAOrAn(data.getName()) + " " + data.getName() + ".", data.getProduct()[0]);		
		return true;
	}

}
