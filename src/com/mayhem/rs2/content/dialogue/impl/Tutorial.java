package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.rs2.content.StarterKit;
import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.dialogue.Emotion;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.controllers.DefaultController;
import com.mayhem.rs2.entity.player.net.out.impl.SendInterface;
import com.mayhem.rs2.entity.player.net.out.impl.SendSidebarInterface;

public class Tutorial extends Dialogue {

	public static class TutorialController extends DefaultController {

		@Override
		public boolean canAttackNPC() {
			return false;
		}

		@Override
		public boolean canClick() {
			return false;
		}

		@Override
		public boolean canMove(Player p) {
			return false;
		}

		@Override
		public boolean canTeleport() {
			return false;
		}

		@Override
		public boolean canTrade() {
			return false;
		}

		@Override
		public void onDisconnect(Player p) {
		}

		@Override
		public boolean transitionOnWalk(Player p) {
			return false;
		}
	}

	public static final TutorialController TUTORIAL_CONTROLLER = new TutorialController();

	public static final int GUIDE = 306;

	public Tutorial(Player player) {
		this.player = player;
		player.setController(TUTORIAL_CONTROLLER);
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

	public static final int[] SIDEBAR_INTERFACE_IDS = { -1, -1, -1, 3213, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	@Override
	public void execute() {

		for (int i = 0; i < SIDEBAR_INTERFACE_IDS.length; i++) {
			player.send(new SendSidebarInterface(i, SIDEBAR_INTERFACE_IDS[i]));
		}
		
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
			end();
			StarterKit.handle(player, 202051);
			player.send(new SendInterface(51750));
			break;
		case 3:
			nChat(new String[] {"Okay @blu@" + player.getUsername() + "</col>.", "Let's get started with the tutorial!"});
			break;
		case 4:
			nChat(new String[] { "Clicking on the @blu@World Map</col> will allow you to teleport", "to various different locations.", "Including minigames, Monsters, Bosses, etc..." });
			break;
		case 5:
			nChat(new String[] { "Shops can be accessed by clicking the MoneyBag Icon", "to the bottom right of your inventory.", "You are able to access shops anywhere!" });
			break;
		case 6:
			nChat(new String[] { "Skilling is a great place to start on FalconExpress.", "You can recieve many Experience boosting items", "RANDOMLY while skilling!" });
			break;
		case 7:
			nChat(new String[] { "If you're playing as a regular player, your starter kit", "Contains several Experience boosting items already.", "Just equip them and it'l explain." });
			break;
		case 8:
			nChat(new String[] { "If you're looking to get some RAID items", "Try the Event bosses that spawn every 2 hours!", "Look for the announcements." });
			break;
		case 9:
			nChat(new String[] { "The Crystal Chest is PACKED with hundreds of items", "Ranging from cosmetics to Bandos.", "Collect Crystal keys commonly dropped by monsters." });
			break;
		case 10:
			nChat(new String[] { "Don't forget to check out your @red@Quest Tab</col>", "For lots of useful information", "Such as your PVM, Boss and Skill points!" });
			break;
		case 11:
			tele(3086, 3493);
			nChat(new String[] { "If you have any more questions please speak to a", "<img=0>@blu@ Moderator</col> or any other staff member.", "Or you can always come back to me!" });
			break;
		case 12:
			nChat(new String[] { "Check out our forums (@red@::forums</col>)", "Make sure to vote to keep the server active." });
			break;
		case 13:
			nChat(new String[] { "There is tons of content to explore.", "So what are you waiting for?!?" });
			break;
		case 14:
			end();
			StarterKit.handle(player, 202051);
			player.send(new SendInterface(51750));
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
