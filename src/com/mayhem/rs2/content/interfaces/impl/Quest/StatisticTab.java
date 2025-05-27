package com.mayhem.rs2.content.interfaces.impl.Quest;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the quest tab text
 * 
 * @author Daniel
 *
 */
public class StatisticTab extends InterfaceHandler {
	
	private double KDR = ((double) player.getKills()) / ((double) player.getDeaths());

	private final String[] text = {
		"<col=ff9040>Rank: " + player.determineIcon(player) + player.determineRank(player),
		//"<col=ff9040>FalconExpress Bucks: <col=75C934>" + Utility.format(player.getCredits()),
		"<col=ff9040>Money Spent: <col=75C934>$" + Utility.format(player.getMoneySpent()),
		"<col=ff9040>FalconExpress Bucks: <col=75C934>" + Utility.format(player.getCredits()),
		"",
		"<col=ff9040>Kills: <col=75C934>" + Utility.format(player.getKills()),
		"<col=ff9040>Deaths: <col=75C934>" + Utility.format(player.getDeaths()),
		"<col=ff9040>KDR: <col=75C934>" + (KDR),
		//"<col=ff9040>Killstreak: <col=75C934>",
		"<col=ff9040>ELO Rating: <col=75C934>" + Utility.format(player.eloRating),
		"",
		"<col=ff9040>Voting Points: <col=75C934>" + Utility.format(player.getVotePoints()),
		"<col=ff9040>Boss Points: <col=75C934>" + Utility.format(player.getbossPoints()),
		"<col=ff9040>PVM Points: <col=75C934>" + Utility.format(player.getpvmPoints()),
		"<col=ff9040>Trivia Points: <col=75C934>" + Utility.format(player.gettriviaPoints()),
		"<col=ff9040>Achievement Points: <col=75C934>" + Utility.format(player.getAchievementsPoints()),
		"<col=ff9040>Prestige Points: <col=75C934>" + Utility.format(player.getPrestigePoints()),
		"<col=ff9040>Slayer Points: <col=75C934>" + Utility.format(player.getSlayerPoints()),
		"<col=ff9040>Pest Control Points: <col=75C934>" + Utility.format(player.getPestPoints()),
		"<col=ff9040>Skill Points: <col=75C934>" + Utility.format(player.getskillPoints()),
		"",
		"",
		"",
		"",

	};

	public StatisticTab(Player player) {
		super(player);

	}

	@Override
	protected int startingLine() {
		return 29451;
	}

	@Override
	protected String[] text() {
		return text;
	}

}
