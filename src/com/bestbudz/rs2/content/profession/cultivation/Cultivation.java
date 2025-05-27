package com.bestbudz.rs2.content.profession.cultivation;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Cultivation {
	/**
	 * Special plant one constructor & getter
	 */
	private SpecialPlantOne specialPlantOne;

	public SpecialPlantOne getSpecialPlantOne() {
	return specialPlantOne;
	}

	public void setSpecialPlantOne(SpecialPlantOne specialPlantOne) {
	this.specialPlantOne = specialPlantOne;
	}

	/**
	 * Special plant one constructor & getter
	 */
	private SpecialPlantTwo specialPlantTwo;

	public SpecialPlantTwo getSpecialPlantTwo() {
	return specialPlantTwo;
	}

	public void setSpecialPlantTwo(SpecialPlantTwo specialPlantTwo) {
	this.specialPlantTwo = specialPlantTwo;
	}

	/**
	 * Compost constructor & getter
	 */
	private Compost compost;

	public Compost getCompost() {
	return compost;
	}

	public void setCompost(Compost compost) {
	this.compost = compost;
	}

	/**
	 * Allotoments constructor & getter
	 */
	private Allotments allotment;

	public Allotments getAllotment() {
	return allotment;
	}

	public void setAllotment(Allotments allotment) {
	this.allotment = allotment;
	}

	/**
	 * Followers constructor & getter
	 */
	private Flowers flower;

	public Flowers getFlowers() {
	return flower;
	}

	public void setFlower(Flowers flower) {
	this.flower = flower;
	}

	/**
	 * Weeds constructor & getter
	 */
	private Weeds weed;

	public Weeds getWeeds() {
	return weed;
	}

	public void setWeed(Weeds weed) {
	this.weed = weed;
	}

	/**
	 * Hops constructor & getter
	 */
	private Hops hops;

	public Hops getHops() {
	return hops;
	}

	public void setHops(Hops hops) {
	this.hops = hops;
	}

	/**
	 * Bushes constructor & getter
	 */
	private Bushes bushes;

	public Bushes getBushes() {
	return bushes;
	}

	public void setBushes(Bushes bushes) {
	this.bushes = bushes;
	}

	/**
	 * Seedling constructor & getter
	 */
	private Seedling seedling;

	public Seedling getSeedling() {
	return seedling;
	}

	public void setSeedling(Seedling seedling) {
	this.seedling = seedling;
	}

	/**
	 * Wood trees constructor & getter
	 */
	private WoodTrees trees;

	public WoodTrees getTrees() {
	return trees;
	}

	public void setTrees(WoodTrees trees) {
	this.trees = trees;
	}

	/**
	 * Fruit tree constructor & getter
	 */
	private FruitTree fruitTrees;

	public FruitTree getFruitTrees() {
	return fruitTrees;
	}

	public void setFruitTrees(FruitTree fruitTrees) {
	this.fruitTrees = fruitTrees;
	}

	private long cultivationTimer = 0;

	public long getCultivationTimer() {
	return cultivationTimer;
	}

	public void setCultivationTimer(long cultivationTimer) {
	this.cultivationTimer = cultivationTimer;
	}

	public Cultivation(Stoner stoner) {
	allotment = new Allotments(stoner);
	fruitTrees = new FruitTree(stoner);
	trees = new WoodTrees(stoner);
	seedling = new Seedling(stoner);
	bushes = new Bushes(stoner);
	hops = new Hops(stoner);
	weed = new Weeds(stoner);
	flower = new Flowers(stoner);
	compost = new Compost(stoner);
	specialPlantOne = new SpecialPlantOne(stoner);
	specialPlantTwo = new SpecialPlantTwo(stoner);
	}

	public void doCalculations() {
	allotment.doCalculations();
	fruitTrees.doCalculations();
	trees.doCalculations();
	bushes.doCalculations();
	hops.doCalculations();
	weed.doCalculations();
	flower.doCalculations();
	specialPlantOne.doCalculations();
	specialPlantTwo.doCalculations();
	}

	public static boolean prepareCrop(Stoner stoner, int item, int id, int x, int y) {
	// plant pot
	if (stoner.getCultivation().getSeedling().fillPotWithSoil(item, x, y)) {
		return true;
	}
	// allotments
	if (stoner.getCultivation().getAllotment().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getAllotment().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getAllotment().clearPatch(x, y, item)) {
		return true;
	}
	if (item >= 3422 && item <= 3428 && id == 4090) {
		stoner.getBox().remove(item, 1);
		stoner.getBox().add(item + 8, 1);
		stoner.getUpdateFlags().sendAnimation(new Animation(832));
		stoner.send(new SendMessage("You put the olive oil on the fire, and turn it into sacred oil."));
		return true;
	}
	if (item <= 5340 && item > 5332) {
		if (stoner.getCultivation().getAllotment().waterPatch(x, y, item)) {
			return true;
		}
	}
	if (stoner.getCultivation().getAllotment().plantSeed(x, y, item)) {
		return true;
	}
	// flowers
	if (stoner.getCultivation().getFlowers().plantScareCrow(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getFlowers().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getFlowers().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getFlowers().clearPatch(x, y, item)) {
		return true;
	}
	if (item <= 5340 && item > 5332) {
		if (stoner.getCultivation().getFlowers().waterPatch(x, y, item)) {
			return true;
		}
	}
	if (stoner.getCultivation().getFlowers().plantSeed(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getCompost().handleItemOnObject(item, id, x, y)) {
		return true;
	}
	// weeds
	if (stoner.getCultivation().getWeeds().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getWeeds().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getWeeds().clearPatch(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getWeeds().plantSeed(x, y, item)) {
		return true;
	}
	// hops
	if (stoner.getCultivation().getHops().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getHops().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getHops().clearPatch(x, y, item)) {
		return true;
	}
	if (item <= 5340 && item > 5332)
		if (stoner.getCultivation().getHops().waterPatch(x, y, item)) {
			return true;
		}
	if (stoner.getCultivation().getHops().plantSeed(x, y, item)) {
		return true;
	}
	// bushes
	if (stoner.getCultivation().getBushes().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getBushes().putCompost(x, y, item)) {
		return true;
	}

	if (stoner.getCultivation().getBushes().clearPatch(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getBushes().plantSeed(x, y, item)) {
		return true;
	}
	// trees
	if (stoner.getCultivation().getTrees().pruneArea(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getTrees().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getTrees().plantSapling(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getTrees().clearPatch(x, y, item)) {
		return true;
	}
	// fruit trees
	if (stoner.getCultivation().getFruitTrees().pruneArea(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getFruitTrees().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getFruitTrees().clearPatch(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getFruitTrees().plantSapling(x, y, item)) {
		return true;
	}
	// special plant one
	if (stoner.getCultivation().getSpecialPlantOne().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getSpecialPlantOne().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getSpecialPlantOne().clearPatch(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getSpecialPlantOne().plantSapling(x, y, item)) {
		return true;
	}
	// Special plant two
	if (stoner.getCultivation().getSpecialPlantTwo().curePlant(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getSpecialPlantTwo().putCompost(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getSpecialPlantTwo().clearPatch(x, y, item)) {
		return true;
	}
	if (stoner.getCultivation().getSpecialPlantTwo().plantSeeds(x, y, item)) {
		return true;
	}
	// stoner.sendMessage("Cultivation disabled - coming soon");
	return false;
	}

	public static boolean inspectObject(Stoner stoner, int x, int y) {
	// allotments
	if (stoner.getCultivation().getAllotment().inspect(x, y)) {
		return true;
	} // flowers
	if (stoner.getCultivation().getFlowers().inspect(x, y)) {
		return true;
	}
	// weeds
	if (stoner.getCultivation().getWeeds().inspect(x, y)) {
		return true;
	}
	// hops
	if (stoner.getCultivation().getHops().inspect(x, y)) {
		return true;
	}
	// bushes
	if (stoner.getCultivation().getBushes().inspect(x, y)) {
		return true;
	}
	// trees
	if (stoner.getCultivation().getTrees().inspect(x, y)) {
		return true;
	}
	// fruit trees
	if (stoner.getCultivation().getFruitTrees().inspect(x, y)) {
		return true;
	}
	// special plant one
	if (stoner.getCultivation().getSpecialPlantOne().inspect(x, y)) {
		return true;
	}
	// special plant two
	if (stoner.getCultivation().getSpecialPlantTwo().inspect(x, y)) {
		return true;
	}
	return false;
	}

	public static boolean harvest(Stoner stoner, int x, int y) {
	// allotments

	if (stoner.getCultivation().getAllotment().harvest(x, y)) {
		return true;
	}
	// flowers
	if (stoner.getCultivation().getFlowers().harvest(x, y)) {
		return true;
	}
	// weeds
	if (stoner.getCultivation().getWeeds().harvest(x, y)) {
		return true;
	}
	// hops
	if (stoner.getCultivation().getHops().harvest(x, y)) {
		return true;
	}
	// bushes
	if (stoner.getCultivation().getBushes().harvestOrCheckHealth(x, y)) {
		return true;
	}
	// trees
	if (stoner.getCultivation().getTrees().checkHealth(x, y)) {
		return true;
	}
	if (stoner.getCultivation().getTrees().cut(x, y)) {
		return true;
	}
	// fruit trees
	if (stoner.getCultivation().getFruitTrees().harvestOrCheckHealth(x, y)) {
		return true;
	}
	// special plant one
	if (stoner.getCultivation().getSpecialPlantOne().harvestOrCheckHealth(x, y)) {
		return true;
	}
	// special plant two
	if (stoner.getCultivation().getSpecialPlantTwo().harvestOrCheckHealth(x, y)) {
		return true;
	}
	return false;
	}

	public static void declare() {
	TaskQueue.queue(new Task(100, true) {
		@Override
		public void execute() {
		for (Stoner stoner : World.getStoners()) {
			if (stoner == null || stoner.getCultivation() == null || !stoner.isActive()) {
				continue;
			}

			stoner.getCultivation().cultivationTimer++;
			stoner.getCultivation().doCalculations();
		}
		}

		@Override
		public void onStop() {
		}
	});
	}

	public static long getMinutesCounter(Stoner stoner) {
	return stoner.getCultivation().cultivationTimer;
	}
}