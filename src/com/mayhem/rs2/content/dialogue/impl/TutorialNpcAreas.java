package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public class TutorialNpcAreas extends Dialogue {

	public static final int GUIDE = 306;



	public TutorialNpcAreas(Player player) {
		this.player = player;
	}

	@Override
	public boolean clickButton(int id) {
		switch (id) {
		case 9157:
			if (option == 1) {
				next = 3;
				execute();
			}
			return true;
		case 9158:
			if (option == 1) {
				next = 2;
				execute();
			}
			return true;
		}
		return false;
	}


	@Override
	public void execute() {
		
		switch (next) {

		case 0:
			DialogueManager.sendNpcChat(player, GUIDE, Emotion.HAPPY_TALK, "Hello @blu@" + player.getUsername() + "</col>, I'm the server guide!", "Would you like a tour of some areas around FalconExpress?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, new String[] { "Yes.", "No." });
			option = 1;
			break;
		case 2:
			pChat(new String[] {"No Thanks."});
			end();
			break;
		case 3:
			nChat(new String[] {"Okay @blu@" + player.getUsername() + "</col>.", "Let's get started with the tour!"});
			break;
		case 4:
			nChat(new String[] { "Clicking on the @blu@World Map</col> will allow you to teleport", "to various different locations.", "Including minigames, Monsters, Bosses, etc..." });
			break;
		case 5:
			nChat(new String[] { "We also have some commands such as @blu@::Home</col> & @mag@::Shops</col>.", "You can view some other commands by typing @blu@::Commands.</col>" });
			break;
		case 6:
			tele(1780, 3690);
			nChat(new String[] { "First of all, I'd like to show you our @blu@Shopping area</col>.", "You can buy all the supplies you need such as", "Armor, weapons and food. Even some outfits for you skillers!" });
			break;
		case 7:
			nChat(new String[] { "Also, you can spend your",  "@blu@Skill points, Monster points & Boss points</col> here. ", "You'll learn more about point systems as you progress." });
			break;
		case 8:
			tele(1838, 3666);
			nChat(new String[] { " @blu@Sand crabs</col> are located in the training section.", "These are a great place to start training", "and collect @blu@Crystal keys</col> to use on the chest at home!" });
			break;
		case 9:
			tele(2976, 3941);
			nChat(new String[] { "Here is one of the many @blu@Crystal monsters</col>.", "You can receive lots of gems and rare loots such as", "@blu@Vesta</col> & @blu@Statius</col>!" });
			break;
		case 10:
			tele(3565, 3309);
			nChat(new String[] { "Here is one of the many minigames on FalconExpress.", "This minigame is a great way to make", "some starter money & get barrows armor!" });
			break;
		case 11:
			tele(3087, 3515);
			nChat(new String[] { "This is the wilderness ditch. here you can", "kill other players for epic rewards such as", "@blu@Bounty points</col>, @blu@Blood money</col> and @blu@rare armor</col>!" });
			break;
		case 12:
			tele(3086, 3493);
			nChat(new String[] { "Type @blu@::teleports</col>, click a teleport in the mages book,", "or click the minimap icon in the right hand", "corner of your screen for more teleports!" });
			break;
		case 13:
			nChat(new String[] { "If you have any more questions please", "speak to a <img=1>Moderator for help or visit our forums at", "@blu@http://falcon-express.net/</col> and post a suggestion." });
			end();
			break;
		case 19:
			end();
			break;
		}

	}

	public void nChat(String[] chat) {
		DialogueManager.sendNpcChat(player, GUIDE, Emotion.HAPPY_TALK, chat);
		next += 1;
	}

	public void pChat(String[] chat) {
		DialogueManager.sendPlayerChat(player, Emotion.HAPPY, chat);
		next += 1;
	}

	public void tele(int x, int y) {
		player.teleport(new Location(x, y, 0));
	}

	public void tele(int x, int y, int z) {
		player.teleport(new Location(x, y, z));
	}
}
