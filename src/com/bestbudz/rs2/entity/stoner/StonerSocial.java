package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.PrivateMessaging;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.ArrayList;
import java.util.List;

public class StonerSocial {
	private final Stoner stoner;
	private final PrivateMessaging privateMessaging;

	private StonerTitle stonerTitle;
	public List<StonerTitle> unlockedTitles = new ArrayList<>();
	private String yellTitle = "Stoner";

	public long lastReport = 0;
	public String lastReported = "";
	public String reportName = "";
	public int reportClicked = 0;

	public String targetName = "";
	public int targetIndex;
	public String viewing;

	public StonerSocial(Stoner stoner) {
		this.stoner = stoner;
		this.privateMessaging = new PrivateMessaging(stoner);
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return;
		}

	}

	public void clearClanChat() {
		stoner.send(new SendString("Chilling in: ", 18139));
		stoner.send(new SendString("Grower: ", 18140));
		for (int j = 18144; j < 18244; j++) {
			stoner.send(new SendString("", j));
		}
	}

	public String deterquarryRank(Stoner stoner) {
		switch (stoner.getRights()) {
			case 0:
				return "Stoner";
			case 1:
				return "<col=006699>Moderator</col>";
			case 2:
				return "<col=E6E600>Adminstrator</col>";
			case 3:
				return "<col=AB1818>Owner</col>";
			case 4:
				return "<col=CF1DCF>Developer</col>";
			case 5:
				return "<col=B20000>Babylonian</col>";
			case 6:
				return "<col=223ca9>Ganja Man</col>";
			case 7:
				return "<col=2EB8E6>Rasta</col>";
			case 8:
				return "<col=971FF2>Waldo</col>";
			case 9:
				return "<col=971FF2>Best Bud</col>";
			case 10:
				return "<col=971FF2>No-Life</col>";
			case 11:
				return "@gry@Dealer</col>";
			case 12:
				return "@gre@Grower</col>";
		}
		return "Unknown!";
	}

	public String deterquarryIcon(Stoner stoner) {
		switch (stoner.getRights()) {
			case 0:
				return "<img=11>";
			case 1:
				return "<img=0>";
			case 2:
				return "<img=1>";
			case 3:
				return "<img=2>";
			case 4:
				return "<img=3>";
			case 5:
				return "<img=4>";
			case 6:
				return "<img=5>";
			case 7:
				return "<img=6>";
			case 8:
				return "<img=7>";
			case 9:
				return "<img=8>";
			case 10:
				return "<img=10>";
			case 11:
				return "<img=11>";
			case 12:
				return "<img=11>";
		}
		return "";
	}

	public PrivateMessaging getPrivateMessaging() { return privateMessaging; }

	public StonerTitle getStonerTitle() { return stonerTitle; }
	public void setStonerTitle(StonerTitle stonerTitle) { this.stonerTitle = stonerTitle; }

	public List<StonerTitle> getUnlockedTitles() { return unlockedTitles; }
	public void setUnlockedTitles(List<StonerTitle> unlockedTitles) { this.unlockedTitles = unlockedTitles; }

	public String getYellTitle() { return yellTitle; }
	public void setYellTitle(String yellTitle) { this.yellTitle = yellTitle; }

	public String getTargetName() { return targetName; }
	public void setTargetName(String targetName) { this.targetName = targetName; }

	public int getTargetIndex() { return targetIndex; }
	public void setTargetIndex(int targetIndex) { this.targetIndex = targetIndex; }

	public String getViewing() { return viewing; }
	public void setViewing(String viewing) { this.viewing = viewing; }

	public String getLastClanChat() { return stoner.lastClanChat; }
	public void setLastClanChat(String lastClanChat) { stoner.lastClanChat = lastClanChat; }
}
