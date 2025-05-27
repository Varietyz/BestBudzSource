package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;

public class TutorialNpc extends Dialogue {

	public static final int GUIDE = 306;



	public TutorialNpc(Player player) {
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
			DialogueManager.sendNpcChat(player, GUIDE, Emotion.HAPPY_TALK, "Hello @blu@" + player.getUsername() + "</col>, Welcome to FalconExpress!", "Would you like a quick tour of our home area?");
			next++;
			break;
		case 1:
			DialogueManager.sendOption(player, new String[] { "Yes.(Recommended!)", "No." });
			option = 1;
			break;
		case 2:
			pChat(new String[] {"No Thanks."});
			end();
			break;
		case 3:
			nChat(new String[] {"Okay @blu@" + player.getUsername() + "</col>.", "Let's get started with the tutorial!"});
			break;
		case 4:
			nChat(new String[] { "Clicking on the @blu@World Map</col> will allow you to teleport", "to various different locations.", "Including minigames, Monsters, Bosses, etc..." });
			break;
		case 5:
			nChat(new String[] { "Now i'll show you around our home area.", "You should probably take a look around on your own", "to get your own feel." });
			break;
		case 6:
			tele(3095, 3511);
			nChat(new String[] { "You can change your @blu@magic books</col> with the altars to your right.", "The altar to your left is for @blu@recharging prayer</col>.", "and using bones on for prayer experience." });
			break;
		case 7:
			tele(3093, 3480);
			nChat(new String[] { "This is Vannaka; he can give you a @blu@slayer task</col>.", "You can also talk to Nieve for @blu@Boss tasks</col>!" });
			break;
		case 8:
			tele(3084, 3495);
			nChat(new String[] { "These are our @blu@Player owned shops</col>.", "You may sell/buy items from other players here." });
			break;
		case 9:
			nChat(new String[] { "The book icon in the bottom right hand of your backpack", "Will give you a list of most shops you'll need to use!" });
			break;
		case 10:
			tele(3078, 3496);
			nChat(new String[] { "Here you have the Bloodmoney, Recolored items salesmen","and the Skill points shop.", "You can earn Bloodmoney for killing monsters & bosses,", "Skilling & random rewards." });
			break;
		case 11:
			nChat(new String[] { "The Recolored items salesman sells a variety of", "colorful cosmetics for Gold coins." });
			break;
		case 12:
			nChat(new String[] { "And the Skill points shop sells some useful items", "for those of you who like to skill.", "Skill points are aquired from ALL skills!" });
			break;
		case 13:
			nChat(new String[] { "And theres also Graceful.", "She will sell you Grace outfits", "for Grace points aquired while doing Agility." });
			break;
		case 14:
			tele(3098, 3510);
			nChat(new String[] { "This is the Emblem Trader.", "He will reward you for killing other players in PVP." });
			break;
		case 15:
			tele(3086, 3493);
			nChat(new String[] { "This is the home thieving area.", "This is a very easy method to make @blu@quick money</col>." });
			break;
		case 16:
			tele(3078, 3488);
			nChat(new String[] { "Patchy here is in charge of our Prestige system.", "After reaching 99 in a skill, you may prestige it." });
			break;
		case 17:
			nChat(new String[] { "You can check your achievements and tons of", "other options in the @blu@Quest tab</col>", "Once you receive so many achievement points,", "Talk to the Achievements shop for Rewards!" });
			break;
		case 18:
			nChat(new String[] { "And you also have Mac. He is in charge of", "giving you max capes once you hit 99", "in ALL skills. some capes have Perks!" });
			break;
		case 19:
			tele(3093, 3492);
			nChat(new String[] { "If you are ever curious about a NPC, talk to James.", "He will be able to help you out with drops and more!" });
			break;
		case 20:
			tele(3080, 3505);
			nChat(new String[] { "These three young fellows can be very useful for you.", "They can decant your potions, give you skillcapes,", "and reset your skills for a cost of course!" });
			break;
		case 21:
			nChat(new String[] { "Otto godblessed can make your Zamorakian spear",  "into a @blu@Hasta</col>!" });
			break;
		case 22:
			tele(3086, 3493);
			nChat(new String[] { "If you have any more questions please speak to a", "<img=0>@blu@ Moderator</col> or any other staff member.", "Or you can always come back to me!" });
			break;
		case 23:
			nChat(new String[] { "Check out our forums (@red@::forums</col>)", "Make sure to vote to keep the server active." });
			break;
		case 24:
			nChat(new String[] { "There is tons of content to explore.", "So what are you waiting for?!?" });
			end();
			break;
		case 25:
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
