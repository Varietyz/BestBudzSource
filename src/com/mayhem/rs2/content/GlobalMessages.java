package com.mayhem.rs2.content;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.mayhem.core.task.Task;
import com.mayhem.core.task.TaskQueue;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.World;

/**
 * Handles the global messages
 * @author Daniel
 *
 */
public class GlobalMessages {
	
	/**
	 * The logger for the class
	 */
	private static Logger logger = Logger.getLogger(GlobalMessages.class.getSimpleName());

	/**
	 * The news color text
	 */
	private static String newsColor = "<col=013B4F>";

	/**
	 * The news icon
	 */
	private static String iconNumber = "<img=8>";
	
	/**
	 * Holds all the announcements in a arraylist
	 */
	public final static ArrayList<String> announcements = new ArrayList<String>();

	/**
	 * The random messages that news will send
	 */
	public static final String[] ANNOUNCEMENTS = { 
			"You can get Pets, Outfits and other great rewards while skilling.",
			"Type ::Discord to automatically join our active DISCORD channel!",
			"3 NEW bosses have been added. check out the Ancient Warriors boss teleport.",
			"The new Ancient Warriors boss drop the Korasi!",
			"You are able to donate with OSRS and RS3 gold! Message Qwerty159063.",
			"You receive bloodmoney while doing most skills!",
			"Make sure to check out all the shops! you can check your currencies in the quests tab.",
			"Sand Crabs commonly drop CRYSTAL KEYS! Which is great for making MONEY!",
		"Do you love FalconExpress? Please show us some love by purchasing some FalconExpress Bucks!",
		"Type ::Commands to view a bunch of player commands.",
		"Want to help us grow? Vote every 12 hours, you will also get a reward!",
		"Considering membership? Members have access to many custom content!",
		"Members can change their title by speaking to Dunce at Memberzone.",
		"Check out our forums for the latest news & updates at ::forums!",
		"Do you have an interesting idea? Suggest it to us on forums!",
		"Found a bug? Report it on forums and we will fix it for you!",
		"Did you know you can talk to The Titler at home for a title?",
		"Check out our ACTIVE Community forums located at http://falcon-express.net/!",
		"To answer trivia questions type ::answer answerhere",
		"Did you know the bank table at home is used for player shops?",
		"Skilling is a great way to make money on FalconExpress.",
		"Everyone can change thier to custom colors by talking to the makeover mage.",
		"EVERY SINGLE boss has a pet! there are over 100 pets on FalconExpress!",
		"Kill Revenants for Vesta & Statius, Zuriels & Morrigans!",
		"Did you know? You can get really rare items from the crystal chest.",
		"Check out the shops tab by clicking the moneybag icon for easy access ANYWHERE!",
		"Check out the Bloodmoney shop to spend your bloodmoney on awesome rewards!",
		"Check out the Skill points shop to spend your Skill points.",
		"We have Trivia points & a Trivia points shop! Don't miss out on awesome rewards.",
		"Here on FalconExpress, we have GAMBLING. Check out the online donation shop to buy a dice bag!",
		"We also have an in-game FalconExpress Bucks shop for players to use their in-game bucks.",
		"Demonic gorillas drop pieces to make the Light & Heavy Ballista as well as Dragon Javelins.",
		"Demonic Gorillas now drop the Dragon hunter Crossbow & Dragon throwing axes!!!",
		"Go kill Demonic Gorillas now for a chance at the pet Demonic Gorilla!",
		"You can kill rock crabs for a chance at a pet rock crab. Easy for new players!",
		"The Abyssal demon now has a rare chance of dropping the Abyssal Orphan pet!!!",
		"All RFD Bosses have their own pets. Think you can get one?",
		"Open the client settings tab to customize your game experience!",
		"Cave Horrors and Demonic Gorillas drop black masks used to make Slayer helms.",
		"Slayer helmets have a dmg bonus. Slayer helmets (i) also have a Slayer exp bonus boost!",
		"Grace sells all kinds of Graceful outfit colors.",
		"There are plenty of outfits to choose from in the outfits shop.",
		"Skotizo can be accessed by receiving a Dark totem from the Superior slayer dungeon!",
		"All your shops are located in the moneybag icon, to the bottom of your inventory.",
		"Need help? Let a staff member know.",
		"Type ::Discord to join our active discord chat! We have daily GIVEAWAYS!"
	};
	
	/**
	 * Declares all the announcements
	 */
	public static void declare() {
		for (int i = 0; i < ANNOUNCEMENTS.length; i++) {
			announcements.add(ANNOUNCEMENTS[i]);
		}
		logger.info(Utility.format(announcements.size()) + " Announcements have been loaded successfully.");
	}

	/**
	 * Initializes the task
	 */
	public static void initialize() {
		TaskQueue.queue(new Task(500, false) {
			@Override
			public void execute() {
				final String announcement = Utility.randomElement(announcements);
				World.sendGlobalMessage(iconNumber + newsColor + " " + announcement);
			}

			@Override
			public void onStop() {
			}
		});
	}
	
}
