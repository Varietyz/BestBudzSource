package com.bestbudz.rs2.content;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerProfilerIndex;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Handles the Stoner Profiler
 * 
 * @author Jaybane
 *
 */
public class StonerProfiler {

	/**
	 * Searches for a stoner
	 * 
	 * @param stoner
	 * @param string
	 */
	public static void search(Stoner stoner, String string) {

	stoner.send(new SendMessage("@dre@Searching '" + Utility.capitalizeFirstLetter(string) + "' for stoner..."));

	Stoner viewing = World.getStonerByName(string);

	stoner.viewing = string;

	if (stoner == viewing && !StonerConstants.isOwner(stoner)) {
		stoner.send(new SendMessage("@dre@Please click on the 'My Profile' button."));
		return;
	}

	if (viewing == null) {
		stoner.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(string) + " either does not exist or is not present!"));
		return;
	}

	TaskQueue.queue(new Task(2) {
		@Override
		public void execute() {

		int deltaX = viewing.getLocation().getX() - (stoner.getCurrentRegion().getRegionX() << 3);
		int deltaY = viewing.getLocation().getY() - (stoner.getCurrentRegion().getRegionY() << 3);

		if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88)) {
			stoner.send(new SendMessage("@dre@You can only view stoners that are in your region."));
			stop();
			return;
		}

		if (viewing.getProfilePrivacy()) {
			stoner.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(viewing.getUsername()) + " has disabled Bongbase."));
			stop();
			return;
		}

		if (stoner.getProfilePrivacy()) {
			stoner.send(new SendMessage("@dre@You cannot view Bongbase while you are in the bushes!"));
			stop();
			return;
		}

		displayProfile(stoner, viewing);
		stop();
		}

		@Override
		public void onStop() {
		}
	});

	}

	/**
	 * If a stoner is found, display the profile
	 * 
	 * @param stoner
	 * @param viewing
	 */
	public static void displayProfile(Stoner stoner, Stoner viewing) {

	viewing.send(new SendMessage("@dre@" + Utility.capitalizeFirstLetter(stoner.getUsername()) + " is viewing you!"));

	viewing.setProfileViews(+1);

	stoner.send(new SendStonerProfilerIndex(viewing.getIndex()));

	stoner.send(new SendString("", 51802));

	for (int i = 0; i < 20; i++) {
		stoner.send(new SendString(Utility.capitalizeFirstLetter(Professions.PROFESSION_NAMES[i]) + " lv: " + viewing.getProfession().getGrades()[i] + "/" + viewing.getProfession().getGradeForExperience(i, viewing.getProfession().getExperience()[i]) + "\\nAdvance lv: " + viewing.getProfessionAdvances()[i], 51832 + i));
	}

	stoner.send(new SendString("</col>Name: @gre@" + Utility.capitalizeFirstLetter(viewing.getUsername()), 51807));
	stoner.send(new SendString("</col>Rank: @gre@" + viewing.deterquarryIcon(viewing) + " " + viewing.deterquarryRank(viewing), 51808));
	stoner.send(new SendString("</col>Combat: @gre@" + viewing.getProfession().getCombatGrade(), 51809));

	stoner.send(new SendString("Stoner information", 51881));
	stoner.send(new SendString("__________________", 51882));
	stoner.send(new SendString("</col>Views @whi@" + viewing.getProfileViews(), 51883));
	stoner.send(new SendString("</col>Money Spent: $@whi@" + viewing.getMoneySpent(), 51884));
	stoner.send(new SendString("</col>CannaCredits: @whi@" + viewing.getCredits(), 51885));
	stoner.send(new SendString("</col>Kills: @whi@" + viewing.getKills(), 51886));
	stoner.send(new SendString("</col>Deaths: @whi@" + viewing.getDeaths(), 51887));
	stoner.send(new SendString("</col>KDR: @whi@" + "Nan", 51888));
	stoner.send(new SendString("</col>Task: @whi@" + viewing.getMercenary().getTask() + "</col>( @whi@" + viewing.getMercenary().getAmount() + "</col> )", 51889));
	stoner.send(new SendString("</col>Mercenary Points: @whi@" + viewing.getMercenaryPoints(), 51890));
	stoner.send(new SendString("</col>PC Points: @whi@" + viewing.getPestPoints(), 51891));
	stoner.send(new SendString("", 51892));

	stoner.send(new SendInterface(51800));
	}

	/**
	 * Display stoner's own profile
	 * 
	 * @param stoner
	 */
	public static void myProfile(Stoner stoner) {

	stoner.send(new SendMessage("@dre@You are now viewing your own profile."));

	stoner.send(new SendString("My Profile", 51602));

	for (int i = 0; i < 20; i++) {
		stoner.send(new SendString(Utility.capitalizeFirstLetter(Professions.PROFESSION_NAMES[i]) + " lv: " + stoner.getProfession().getGrades()[i] + "/" + stoner.getProfession().getGradeForExperience(i, stoner.getProfession().getExperience()[i]) + "\\nAdvance lv: " + stoner.getProfessionAdvances()[i], 51632 + i));
	}

	stoner.send(new SendString("</col>Name: @gre@" + Utility.capitalizeFirstLetter(stoner.getUsername()), 51607));
	stoner.send(new SendString("</col>Rank: @gre@" + stoner.deterquarryIcon(stoner) + " " + stoner.deterquarryRank(stoner), 51608));
	stoner.send(new SendString("</col>Combat: @gre@" + stoner.getProfession().getCombatGrade(), 51609));

	stoner.send(new SendString("", 51681));
	stoner.send(new SendString("Stoner information", 51682));
	stoner.send(new SendString("__________________", 51683));
	stoner.send(new SendString("</col>Views @whi@" + stoner.getProfileViews(), 51684));
	stoner.send(new SendString("</col>Money Spent: $@whi@" + stoner.getMoneySpent(), 51685));
	stoner.send(new SendString("</col>CannaCredits: @whi@" + stoner.getCredits(), 51686));
	stoner.send(new SendString("</col>Kills: @whi@" + stoner.getKills(), 51687));
	stoner.send(new SendString("</col>Deaths: @whi@" + stoner.getDeaths(), 51688));
	stoner.send(new SendString("</col>KDR: @whi@" + "Nan", 51689));
	stoner.send(new SendString("</col>Task: @whi@" + stoner.getMercenary().getTask() + "</col>(" + stoner.getMercenary().getAmount() + "</col>)", 51690));
	stoner.send(new SendString("</col>Mercenary Points: @whi@" + stoner.getMercenaryPoints(), 51691));
	stoner.send(new SendString("</col>PC Points: @whi@" + stoner.getPestPoints(), 51692));
	stoner.send(new SendString("", 51693));

	stoner.send(new SendInterface(51600));
	}

}
