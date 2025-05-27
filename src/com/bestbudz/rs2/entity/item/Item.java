package com.bestbudz.rs2.entity.item;

import com.bestbudz.core.definitions.EquipmentDefinition;
import com.bestbudz.core.definitions.FoodDefinition;
import com.bestbudz.core.definitions.ItemBonusDefinition;
import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.definitions.PotionDefinition;
import com.bestbudz.core.definitions.SagittariusVigourDefinition;
import com.bestbudz.core.definitions.SagittariusWeaponDefinition;
import com.bestbudz.core.definitions.SpecialAssaultDefinition;
import com.bestbudz.core.definitions.WeaponDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import java.util.Objects;

public class Item {

	private short id;
	private int amount;

	public Item() {
	}

	public Item(int id) {
	this.id = ((short) id);
	amount = 1;
	}

	public Item(int id, int amount) {
	this.id = ((short) id);
	this.amount = amount;
	}

	public Item(Item item) {
	id = ((short) item.getId());
	amount = item.getAmount();
	}

	public static ItemDefinition getDefinition(int id) {
	return GameDefinitionLoader.getItemDef(id);
	}

	public static EquipmentDefinition getEquipmentDefinition(int id) {
	return GameDefinitionLoader.getEquipmentDefinition(id);
	}

	public static FoodDefinition getFoodDefinition(int id) {
	if ((id < 0) || (id > 20144)) {
		return null;
	}

	return GameDefinitionLoader.getFoodDefinition(id);
	}

	public static short[] getItemBonuses(int id) {
	ItemBonusDefinition def = GameDefinitionLoader.getItemBonusDefinition(id);
	if (def != null) {
		return def.getBonuses();
	}
	return null;
	}

	public static PotionDefinition getPotionDefinition(int id) {
	if ((id < 0) || (id > 20144)) {
		return null;
	}

	return GameDefinitionLoader.getPotionDefinition(id);
	}

	public static SagittariusWeaponDefinition getSagittariusDefinition(int id) {
	return GameDefinitionLoader.getSagittariusWeaponDefinition(id);
	}

	public static int getSagittariusVigourBonus(int id) {
	SagittariusVigourDefinition def = GameDefinitionLoader.getSagittariusVigourDefinition(id);
	return def == null ? 0 : def.getBonus();
	}

	public static SpecialAssaultDefinition getSpecialDefinition(int id) {
	return GameDefinitionLoader.getSpecialDefinition(id);
	}

	public static WeaponDefinition getWeaponDefinition(int id) {
	if ((id < 0) || (id > 20144)) {
		return null;
	}

	return GameDefinitionLoader.getWeaponDefinition(id);
	}

	public void add(int amount) {
	this.amount += amount;
	}

	@Override
	public int hashCode() {
	return Objects.hash(id, amount);
	}

	@Override
	public boolean equals(Object obj) {
	if ((obj instanceof Item)) {
		Item item = (Item) obj;
		return item.getId() == id;
	}
	return false;
	}

	@Override
	public String toString() {
	return "Item [id=" + id + ", amount=" + amount + "]";
	}

	public int getAmount() {
	return amount;
	}

	public void setAmount(int amount) {
	this.amount = amount;
	}

	public ItemDefinition getDefinition() {
	return GameDefinitionLoader.getItemDef(id);
	}

	public EquipmentDefinition getEquipmentDefinition() {
	return GameDefinitionLoader.getEquipmentDefinition(id);
	}

	public FoodDefinition getFoodDefinition() {
	return GameDefinitionLoader.getFoodDefinition(id);
	}

	public int getId() {
	return id;
	}

	public void setId(int id) {
	this.id = ((short) id);
	}

	public short[] getItemBonuses() {
	ItemBonusDefinition def = GameDefinitionLoader.getItemBonusDefinition(id);
	if (def != null) {
		return def.getBonuses();
	}
	return null;
	}

	public byte[][] getItemRequirements() {
	return GameDefinitionLoader.getItemRequirements(id);
	}

	public PotionDefinition getPotionDefinition() {
	return GameDefinitionLoader.getPotionDefinition(id);
	}

	public SagittariusWeaponDefinition getSagittariusDefinition() {
	return GameDefinitionLoader.getSagittariusWeaponDefinition(id);
	}

	public int getSagittariusVigourBonus() {
	SagittariusVigourDefinition def = GameDefinitionLoader.getSagittariusVigourDefinition(id);
	return def == null ? 0 : def.getBonus();
	}

	public SpecialAssaultDefinition getSpecialDefinition() {
	return GameDefinitionLoader.getSpecialDefinition(id);
	}

	public WeaponDefinition getWeaponDefinition() {
	return GameDefinitionLoader.getWeaponDefinition(id);
	}

	public void note() {
	int noteId = getDefinition().getNoteId();

	if (noteId == -1) {
		return;
	}

	id = ((short) noteId);
	}

	public void remove(int amount) {
	this.amount -= amount;
	}

	public Item getSingle() {
	return new Item(id, 1);
	}

	public void unNote() {
	int noteId = getDefinition().getNoteId();

	if (noteId == -1) {
		return;
	}

	id = ((short) noteId);
	}
}
