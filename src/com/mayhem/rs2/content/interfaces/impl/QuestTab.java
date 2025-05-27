package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.Constants;
import com.mayhem.rs2.content.DropTable;
import com.mayhem.rs2.content.PlayerProfiler;
import com.mayhem.rs2.content.achievements.AchievementHandler.AchievementDifficulty;
import com.mayhem.rs2.content.achievements.AchievementInterface;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.content.interfaces.impl.Quest.InformationTab;
import com.mayhem.rs2.content.interfaces.impl.Quest.LinkTab;
import com.mayhem.rs2.content.interfaces.impl.Quest.PanelTab;
import com.mayhem.rs2.content.interfaces.impl.Quest.StatisticTab;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendConfig;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendScrollInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;
//import com.mayhem.rs2.content.interfaces.impl.Quest.ActualQuestTab;
import com.mayhem.rs2.entity.player.net.out.impl.SendSidebarInterface;

public class QuestTab {

	public enum TabType {
		INFORMATION,
		STATISTICS,
		PANELS,
		LINKS;
	}

	public static void startUp(Player player) {
		player.setTabType(TabType.INFORMATION);
		InterfaceHandler.writeText(new InformationTab(player));
		player.send(new SendConfig(540, 1));
	}

	public static boolean handle(Player player, int button) {
		switch (button) {

		case 114218:
			player.send(new SendString("Information", 29407));
			InterfaceHandler.writeText(new InformationTab(player));
			player.send(new SendScrollInterface(29450, 0));
			player.setTabType(TabType.INFORMATION);
			return true;

		case 114219:
			player.send(new SendString("Statistics", 29407));
			InterfaceHandler.writeText(new StatisticTab(player));
			player.send(new SendScrollInterface(29450, 0));
			player.setTabType(TabType.STATISTICS);
			return true;

		case 114220:
			player.send(new SendString("Panels", 29407));
			InterfaceHandler.writeText(new PanelTab(player));
			player.send(new SendScrollInterface(29450, 0));
			player.setTabType(TabType.PANELS);
			return true;

		case 114221:
			player.send(new SendString("Links", 29407));
			InterfaceHandler.writeText(new LinkTab(player));
			player.send(new SendScrollInterface(29450, 0));
			player.setTabType(TabType.LINKS);
			return true;

		case 115011:
			if (player.getTabType() == TabType.PANELS) {
				PlayerProfiler.myProfile(player);
			} else if (player.getTabType() == TabType.LINKS) {
				player.send(new SendString(Constants.VOTE_LINK, 12000));
			} else if (player.getTabType() == TabType.INFORMATION) {
				//ActualQuestTab.startUp(player);
				player.send(new SendSidebarInterface(2, 639));
			}
			break;

		case 115012:
			if (player.getTabType() == TabType.PANELS) {
				//DropViewer.open(player);
				DropTable.open(player);
				//player.send(new SendMessage("Drop viewer will be coming soon!"));
			} else if (player.getTabType() == TabType.LINKS) {
				//player.send(new SendURL("http://falcon-express.net/"));
				player.send(new SendString(Constants.STORE_LINK, 12000));
			}
			break;

		case 115013:
			if (player.getTabType() == TabType.PANELS) {
				InterfaceHandler.writeText(new AchievementInterface(player, AchievementDifficulty.EASY));
				//InterfaceWriter.write(new AchievementInterface(player, AchievementDifficulty.EASY));
				AchievementInterface.sendInterfaceForAchievement(player, AchievementList.ANSWER_15_TRIVIABOTS_CORRECTLY);
				//player.attr().put(PlayerAttributes.ACHIEVEMENT_PAGE, AchievementDifficulty.EASY);
				player.setAchievement(AchievementDifficulty.EASY);

				player.send(new SendInterface(35_000));
			} else if (player.getTabType() == TabType.LINKS) {
				//player.send(new SendURL("http://falcon-express.net/"));
				player.send(new SendString(Constants.FORUM_LINK, 12000));
			}
			break;

		case 115014:
			if (player.getTabType() == TabType.PANELS) {
				player.send(new SendString("FalconExpress Command List", 8144));
				InterfaceHandler.writeText(new CommandInterface(player));
				player.send(new SendInterface(8134));
			} else if (player.getTabType() == TabType.LINKS) {
				//player.send(new SendURL("www.itemdb.biz"));
				player.send(new SendString(Constants.HISCORE_LINK, 12000));
			}
			break;

		case 115015:
			if (player.getTabType() == TabType.PANELS) {
				player.send(new SendString("Kill Tracker", 8144));
				InterfaceHandler.writeText(new KillTracker(player));
				player.send(new SendInterface(8134));
				//KillTracker.open(player);
			} else if (player.getTabType() == TabType.LINKS) {
				//player.send(new SendURL("http://falcon-express.net/"));
				player.send(new SendString(Constants.UPDATE_LINK, 12000));
			}
			break;

		case 115016:
			if (player.getTabType() == TabType.PANELS) {
				//PremadeGear.open(player);
			} else if (player.getTabType() == TabType.LINKS) {
				player.send(new SendString(Constants.RS_ADV, 12000));
			}
			break;

		case 115017:
			if (player.getTabType() == TabType.PANELS) {
				//player.getColorChanger().open();
				player.send(new SendInterface(37500));
				player.send(new SendString("Color chosen: @or2@-", 37506));
			}
			break;

		case 115018:
			if (player.getTabType() == TabType.PANELS) {
				//PlayerTitleManager.open(player);
				player.send(new SendInterface(55000));
			}
			break;
			
		case 115019:
			if (player.getTabType() == TabType.PANELS) {
				//int line = 37111;
				/*for (String strings : Constants.LAST_VOTER) {
					line++;			
					player.send(new SendString(line % 2 == 0 ? strings : "<col=800000>" + strings, line));
				}*/
				//player.send(new SendString("<col=800000>FalconExpress-OS Recent Updates", 37103));
				//player.send(new SendInterface(37100));
			}
			break;
			
		case 115028:
			if (player.getTabType() == TabType.STATISTICS) {
				player.send(new SendString("Skill Points", 8144));
				//InterfaceHandler.writeText(new PointsInterface(player));
				//player.send(new SendInterface(8134));
			}
			break;
		}

		return false;
	}
	
}
