package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public class TutorialNpcMoney extends Dialogue {

	public static final int GUIDE = 306;



	public TutorialNpcMoney(Player player) {
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
			DialogueManager.sendNpcChat(player, GUIDE, Emotion.HAPPY_TALK, "Would you like to hear some moneymaking tips?");
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
			nChat(new String[] {"Okay @blu@" + player.getUsername() + "</col>.", "Let's get started."});
			break;
		case 4:
			tele(3086, 3493);
			nChat(new String[] { "You can thieve here and sell what you steal", "to the merchant here for gold." });
			break;
		case 5:
			nChat(new String[] { "You will also receive bloodmoney and other", "random rewards(such as pets) while thieving", "and doing other skills as well!" });
			break;
		case 6:
			nChat(new String[] { "Like every skill on FalconExpress,", "You will receive skill points to be spent", "in the skill points shop on useful supplies!" });
			break;
		case 7:
			tele(3086, 3493);
			nChat(new String[] { "There are MANY more ways to get rich on FalconExpress.", "You can farm clue scrolls from monsters.", "Loads of @blu@NEW items</col> have been added to clues." });
			break;
		case 8:
			nChat(new String[] { "Monsters around FalconExpress also drop Crystal Keys commonly", "and with the crystal chest now packed with 100s", "of new items, it's a great way to make BANK!" });
			break;
		case 9:
			nChat(new String[] { "Bloodmoney is also a second currency on FalconExpress,", "with many of its own shops", "to purchase items." });
			break;
		case 10:
			nChat(new String[] { "You receive blood money for doing many things", "around FalconExpress such as skills, killing monsters", "killing other players & random drops." });
			break;
		case 11:
			nChat(new String[] { "That's about everything i have to tell you for now.", "Come back later and i may have some", "more money making methods." });
			break;
		case 12:
			nChat(new String[] { "Remember, you can always dance for money! Or Sell Pies! :)" });
			end();
			break;
		case 13:
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
