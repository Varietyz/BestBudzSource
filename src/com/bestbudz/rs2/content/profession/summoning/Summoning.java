package com.bestbudz.rs2.content.profession.summoning;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.dialogue.impl.ConfirmDialogue;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.impl.GroundItemHandler;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendModelAnimation;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNPCDialogueHead;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class Summoning {

	private final Stoner p;
	private FamiliarMob familiar = null;

	private int time = -1;

	private int special = 60;

	private Task removalTask = null;

	private BOBContainer container = null;

	private boolean assault = false;

	private long specialDelay = System.currentTimeMillis();

	public Summoning(Stoner p) {
	this.p = p;
	}

	public boolean click(int button) {
	switch (button) {
	case 70098:
	case 70103:
		doSpecial();
		return true;
	case 70105:
		if (familiar != null) {
			assault = (!assault);
			p.getClient().queueOutgoingPacket(new SendConfig(333, assault ? 1 : 0));
		} else {
			p.getClient().queueOutgoingPacket(new SendMessage("You do not have a familiar."));
		}
		return true;
	case 70112:
		if (familiar != null) {
			if (p.getBox().hasItemId(familiar.getData().pouch)) {
				p.getBox().remove(familiar.getData().pouch);
				time = 20;
				renew();
				familiar.getUpdateFlags().sendGraphic(new Graphic(familiar.getSize() == 1 ? 1314 : 1315, 0, false));
			} else {
				p.getClient().queueOutgoingPacket(new SendMessage("You need another pouch to renew this familiar."));
			}
		} else
			p.getClient().queueOutgoingPacket(new SendMessage("You do not have a familiar."));

		return true;
	case 70118:
		if (familiar == null) {
			p.getClient().queueOutgoingPacket(new SendMessage("You do not have a familiar."));
			return true;
		}

		p.start(new ConfirmDialogue(p, new String[] { "Are you sure you want to dismiss this familiar?", "Any BoB items will be dropped." }) {

			@Override
			public void onConfirm() {
			if (familiar != null) {
				familiar.remove();
				onFamiliarDeath();
			} else {
				p.getClient().queueOutgoingPacket(new SendMessage("You do not have a familiar."));
			}
			}

		});
		return true;
	case 70115:
		if (familiar != null) {
			if (Utility.getManhattanDistance(p.getLocation(), familiar.getLocation()) > 3) {
				if (!p.getProfession().locked())
					p.getProfession().lock(1);
				else {
					return true;
				}

				Location l = GameConstants.getClearAdjacentLocation(p.getLocation(), familiar.getSize());

				familiar.teleport(l != null ? l : p.getLocation());
			}
		} else
			p.getClient().queueOutgoingPacket(new SendMessage("You do not have a familiar."));

		return true;
	case 70109:
		if (familiar != null) {
			if (container != null)
				container.open();
			else
				p.getClient().queueOutgoingPacket(new SendMessage("Your current familiar cannot store items."));
		} else {
			p.getClient().queueOutgoingPacket(new SendMessage("You do not have a familiar."));
		}
		return true;
	}
	return false;
	}

	public void doSpecial() {
	if (familiar != null) {
		FamiliarSpecial spec = SummoningConstants.getSpecial(familiar.getData());

		if (spec != null) {
			int scroll = SummoningConstants.getScrollForFamiliar(familiar);

			if (scroll == -1) {
				p.getClient().queueOutgoingPacket(new SendMessage("This familiar's scroll could not be found, please report this error."));
				return;
			}

			if ((spec.getSpecialType() == FamiliarSpecial.SpecialType.COMBAT) && (familiar.getCombat().getAssaulting() == null)) {
				p.getClient().queueOutgoingPacket(new SendMessage("You must be assaulting something to use this special move!"));
				return;
			}

			if (System.currentTimeMillis() - specialDelay < 5000L) {
				p.getClient().queueOutgoingPacket(new SendMessage("You must let your familiar rest for a few seconds!"));
				return;
			}

			if (special < spec.getAmount()) {
				p.getClient().queueOutgoingPacket(new SendMessage("You do not have enough special points to perform that assault."));
				return;
			}

			if (!p.getBox().hasItemId(scroll)) {
				p.getClient().queueOutgoingPacket(new SendMessage("You do not have the scroll required for this special move."));
				return;
			}

			if (spec.execute(p, familiar)) {
				p.getUpdateFlags().sendAnimation(7660, 0);

				specialDelay = System.currentTimeMillis();
				special -= spec.getAmount();
				p.getProfession().addExperience(21, spec.getExperience());

				p.getBox().remove(scroll, 1);

				updateSpecialAmount();
			}
		} else {
			p.getClient().queueOutgoingPacket(new SendMessage("This special is not supported yet."));
		}
	}
	}

	public BOBContainer getContainer() {
	return container;
	}

	public Familiar getFamiliarData() {
	return familiar != null ? familiar.getData() : null;
	}

	public String getMinutes(int ticks) {
	if ((familiar != null) && (ticks > 0)) {
		int minutes = ticks / 100;
		int seconds = (int) (ticks % 100 * 0.6D);

		return minutes + "." + (seconds < 10 ? "0" + seconds : Integer.valueOf(seconds));
	}
	return "0.0";
	}

	public int getSpecialAmount() {
	return special;
	}

	public int getTime() {
	return time;
	}

	public void setTime(int time) {
	this.time = time;
	}

	public boolean hasFamiliar() {
	return familiar != null;
	}

	public boolean interact(Mob mob, int option) {
	if ((mob == null) || (mob.getOwner() == null) || !(mob instanceof FamiliarMob)) {
		return false;
	}

	if (((mob instanceof FamiliarMob)) && (!mob.getOwner().equals(p))) {
		p.getClient().queueOutgoingPacket(new SendMessage("This is not your familiar!"));
		return true;
	}

	if ((option == 2) && familiar.getData() != null && (familiar.getData().bobSlots > 0) && (container != null)) {
		container.open();
		return true;
	}

	return false;
	}

	public boolean isAssault() {
	return assault;
	}

	public void setAssault(boolean assault) {
	this.assault = assault;
	}

	public boolean isFamilarBOB() {
	return (familiar != null) && (container != null);
	}

	public boolean isFamiliar(Entity e) {
	return (e.equals(familiar));
	}

	public void onFamiliarDeath() {
	if (familiar.getData().bobSlots > 0) {
		if (p.getInterfaceManager().hasBOBBoxOpen()) {
			p.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
		}

		for (Item i : container.getItems()) {
			if (i != null) {
				GroundItemHandler.add(i, familiar.getLocation(), p);
			}
		}

		container.clear();
		container = null;
	}

	familiar = null;
	assault = false;
	refreshSidebar();
	}

	public void onLogin() {
	if (p.getAttributes().get("summoningfamsave") != null) {
		summonOnLogin(p.getAttributes().getInt("summoningfamsave"));

		if (p.getAttributes().get("summoningbobbox") != null) {
			container.setItems((Item[]) p.getAttributes().get("summoningbobbox"));
		}
	}

	TaskQueue.queue(new Task(p, 35) {
		@Override
		public void execute() {
		if (special < 60) {
			special += 5;

			if (special > 60) {
				special = 60;
			}

			updateSpecialAmount();
		}
		}

		@Override
		public void onStop() {
		}
	});
	refreshSidebar();
	}

	public void onUpdateBox() {
	if (familiar != null) {
		int scroll = SummoningConstants.getScrollForFamiliar(familiar);
		int am = p.getBox().getItemAmount(scroll);

		p.getClient().queueOutgoingPacket(new SendString("" + (scroll == -1 ? 0 : am), 18024));

		p.getClient().queueOutgoingPacket(new SendConfig(330, am > 0 ? 1 : 0));
	} else {
		p.getClient().queueOutgoingPacket(new SendString("0", 18024));
		p.getClient().queueOutgoingPacket(new SendConfig(330, 0));
	}
	}

	public void refreshSidebar() {
	if (familiar == null) {
		p.getClient().queueOutgoingPacket(new SendString(getMinutes(0), 18043));
		p.getClient().queueOutgoingPacket(new SendNPCDialogueHead(4000, 18021));
		p.getClient().queueOutgoingPacket(new SendString("None", 18028));
		p.getClient().queueOutgoingPacket(new SendConfig(333, 0));
	} else {
		p.getClient().queueOutgoingPacket(new SendConfig(333, assault ? 1 : 0));
	}

	updateSpecialAmount();
	onUpdateBox();
	}

	public void removeForLogout() {
	familiar.remove();
	}

	public void renew() {
	if (familiar != null) {
		if (removalTask != null) {
			removalTask.stop();
		}

		p.getClient().queueOutgoingPacket(new SendString(getMinutes(time * (familiar.getData().time / 20)), 18043));

		removalTask = new Task(familiar, familiar.getData().time / 20) {
			@Override
			public void execute() {
			if ((familiar == null) || (familiar.isDead())) {
				stop();
				return;
			}

			time -= 1;

			p.getClient().queueOutgoingPacket(new SendString(getMinutes(time * (familiar.getData().time / 20)), 18043));

			if (time == 1) {
				p.getClient().queueOutgoingPacket(new SendMessage("Your familiar will dissapear in " + (int) (familiar.getData().time / 20 * 0.6D) + " seconds."));
			}

			if (time == 0) {
				familiar.getUpdateFlags().sendGraphic(new Graphic(familiar.getSize() == 1 ? 1314 : 1315, 0, false));
				setTaskDelay(4);
				return;
			}
			if (time < 0) {
				if (familiar != null) {
					familiar.remove();
					onFamiliarDeath();
				}

				stop();
			}
			}

			@Override
			public void onStop() {
			}
		};
		TaskQueue.queue(removalTask);
	}
	}

	public void setSpecial(int special) {
	this.special = special;
	}

	public boolean summon(int id) {
	Familiar f = SummoningConstants.getFamiliarForPouch(id);

	if (f != null) {
		if ((!BestbudzConstants.DEV_MODE) && (!SummoningConstants.isAllowed(f))) {
			p.getClient().queueOutgoingPacket(new SendMessage("This familiar is not yet functional."));
			return true;
		}

		if (p.getMaxGrades()[21] < f.gradeRequired) {
			p.getClient().queueOutgoingPacket(new SendMessage("You need a Summoning grade of " + f.gradeRequired + " to summon this mob."));
			return true;
		}

		if (p.getGrades()[21] < f.pointsForSummon) {
			p.getClient().queueOutgoingPacket(new SendMessage("You must recharge your Summoning points to do this!"));
			return true;
		}

		Location l = GameConstants.getClearAdjacentLocation(p.getLocation(), GameDefinitionLoader.getNpcDefinition(f.mob).getSize());

		if (l == null) {
			p.getClient().queueOutgoingPacket(new SendMessage("You must make room for your familiar!"));
			return true;
		}
		long[] tmp243_238 = p.getGrades();
		tmp243_238[21] = ((short) (tmp243_238[21] - f.pointsForSummon));
		p.getProfession().update(21);

		p.getBox().remove(id, 1, true);

		familiar = new FamiliarMob(f, p, l);

		familiar.getUpdateFlags().sendGraphic(new Graphic(familiar.getSize() == 1 ? 1314 : 1315, 0, false));

		p.getClient().queueOutgoingPacket(new SendModelAnimation(18021, -1));
		p.getClient().queueOutgoingPacket(new SendNPCDialogueHead(f.mob, 18021));
		p.getClient().queueOutgoingPacket(new SendString(GameDefinitionLoader.getNpcDefinition(f.mob).getName(), 18028));

		time = 20;
		renew();

		onUpdateBox();

		if (f.bobSlots > 0)
			container = new BOBContainer(p, f.bobSlots);
		else {
			container = null;
		}

		assault = false;
		p.getClient().queueOutgoingPacket(new SendConfig(333, 0));

		return true;
	}

	return false;
	}

	public void summonOnLogin(int id) {
	Familiar f = Familiar.forMobId(id);

	if ((f == null) || (!SummoningConstants.isAllowed(f))) {
		return;
	}

	Location l = GameConstants.getClearAdjacentLocation(p.getLocation(), GameDefinitionLoader.getNpcDefinition(f.mob).getSize());

	familiar = new FamiliarMob(f, p, l == null ? p.getLocation() : l);

	familiar.getUpdateFlags().sendGraphic(new Graphic(familiar.getSize() == 1 ? 1314 : 1315, 0, false));

	p.getClient().queueOutgoingPacket(new SendModelAnimation(18021, -1));
	p.getClient().queueOutgoingPacket(new SendNPCDialogueHead(f.mob, 18021));
	p.getClient().queueOutgoingPacket(new SendString(GameDefinitionLoader.getNpcDefinition(f.mob).getName(), 18028));
	renew();

	onUpdateBox();

	if (f.bobSlots > 0)
		container = new BOBContainer(p, f.bobSlots);
	else
		container = null;
	}

	public void updateSpecialAmount() {
	for (int i = 0; i < 17; i++)
		p.getClient().queueOutgoingPacket(new SendConfig(334 + i, (special == 60) || (special > 3 * (i + 1)) ? 0 : 1));
	}
}
