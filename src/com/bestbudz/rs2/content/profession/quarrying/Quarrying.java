package com.bestbudz.rs2.content.profession.quarrying;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Quarrying {

  private static final Set<Location> DEAD_ORES = new HashSet<>();

  public static void declare() {
    Pickaxe.declare();
    Ore.declare();
  }

	/**
	 * CRITICAL FIXES for Quarrying.java - Discord Bot Integration
	 * Replace the existing clickRock method with this implementation
	 */

	public static boolean clickRock(Stoner stoner, RSObject object) {
		if (stoner.getProfession().locked() || object == null) {
			return false;
		}

		Ore ore = Ore.get(object.getId());
		if (ore == null) {
			//System.out.println("DEBUG: No ore found for object ID: " + object.getId());
			return false;
		}

		// FIXED: Proper Discord bot detection
		boolean isDiscordBot = isDiscordBot(stoner);
		//System.out.println("DEBUG: Processing quarrying for " + stoner.getUsername() + ", isDiscordBot: " + isDiscordBot);

		Pickaxe pickaxe = Pickaxe.TOOL_RING;

		if (stoner.getCombat().inCombat() || stoner.getCombat().getAssaulting() != null) {
			stoner.send(new SendMessage("You can't do that right now!"));
			//System.out.println("DEBUG: Player in combat, cannot mine");
			return false;
		}

		// FIXED: Proper inventory check for both players and bots
		if (!isDiscordBot && stoner.getBox().getTakenSlots() == 28) {
			DialogueManager.sendStatement(stoner, "Your box is full!");
			//System.out.println("DEBUG: Normal player inventory full");
			return false;
		}
		// Discord bot doesn't need inventory check due to auto-banking

		if (!isDiscordBot) {
			stoner.send(new SendMessage("You generated a pickaxe and started swinging at the rock."));
		} else {
			//System.out.println("DEBUG: Discord bot starting to mine " + ore.getName());
		}

		int ticks = ore.immunity == -1 ? 2 : ore.getImmunity() -
			(int) ((stoner.getGrades()[Professions.QUARRYING] - ore.getGrade()) * 2 / (double) pickaxe.getWeight());
		int gemTick = ore.getImmunity();

		if (ticks < 1) {
			ticks = 1;
		}

		int time = ore.getName().equalsIgnoreCase("gem rock") ? gemTick : ticks;

		//System.out.println("DEBUG: Mining will take " + time + " ticks for " + ore.getName());

		Pickaxe finalPickaxe = pickaxe;
		TaskQueue.queue(new Task(stoner, 1, false, StackType.STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION) {
			int ticks = 0;

			@Override
			public void execute() {
				// FIXED: Proper inventory check during execution
				if (!isDiscordBot && stoner.getBox().getFreeSlots() == 0) {
					DialogueManager.sendStatement(stoner, "Your box is full!");
					//System.out.println("DEBUG: Stopping mining - inventory full during execution");
					stop();
					return;
				}

				if (ticks++ == time || DEAD_ORES.contains(new Location(object.getX(), object.getY(), object.getZ()))) {
					// FIXED: Single reward per completion
					//System.out.println("DEBUG: Mining completed for " + ore.getName() + " after " + ticks + " ticks");
					addQuarryReward(stoner, ore, isDiscordBot);
					stoner.getProfession().addExperience(Professions.QUARRYING, ore.getExp());
					AchievementHandler.activateAchievement(stoner, AchievementList.QUARRY_12500_ROCKS, 1);

					ticks = 0;

					// FIXED: Proper completion check
					if (!isDiscordBot && stoner.getBox().getFreeSlots() == 0) {
						DialogueManager.sendStatement(stoner, "Your box is full!");
						stop();
					}
					return;
				}

				// Show mining animation
				stoner.getUpdateFlags().sendAnimation(finalPickaxe.getAnimation());
			}

			@Override
			public void onStop() {
				stoner.getUpdateFlags().sendAnimation(new Animation(65535));
				//System.out.println("DEBUG: Mining task stopped for " + stoner.getUsername());

				// Handle rock replacement
				if (ore.getReplacement() > 0) {
					ObjectManager.spawnWithObject(ore.getReplacement(), object.getX(), object.getY(),
						object.getZ(), object.getType(), object.getFace());
					DEAD_ORES.add(new Location(object.getX(), object.getY(), object.getZ()));

					TaskQueue.queue(new Task(stoner, ore.getRespawn(), false, StackType.STACK,
						BreakType.NEVER, TaskIdentifier.QUARRYING_ROCK) {
						@Override
						public void execute() {
							stop();
						}

						@Override
						public void onStop() {
							DEAD_ORES.remove(new Location(object.getX(), object.getY(), object.getZ()));
							ObjectManager.spawnWithObject(object.getId(), object.getX(), object.getY(),
								object.getZ(), object.getType(), object.getFace());
						}
					});
				}
			}
		});

		//System.out.println("DEBUG: Successfully queued mining task for " + stoner.getUsername());
		return true;
	}

	/**
	 * FIXED: Simple Discord bot check
	 */
	private static boolean isDiscordBot(Stoner stoner) {
		return stoner instanceof com.bestbudz.core.discord.stonerbot.DiscordBotStoner ||
			(stoner.getUsername() != null &&
				stoner.getUsername().equals(DiscordBotDefaults.DEFAULT_USERNAME));
	}

	/**
	 * FIXED: Single reward system - no duplicates
	 */
	private static void addQuarryReward(Stoner stoner, Ore ore, boolean isDiscordBot) {
		int[] rewards = ore.getOre();
		int selectedReward = rewards[com.bestbudz.core.util.Utility.random(rewards.length - 1)];

		//System.out.println("DEBUG: Adding reward " + selectedReward + " to " + stoner.getUsername() + " (isBot: " + isDiscordBot + ")");

		if (isDiscordBot && stoner instanceof com.bestbudz.core.discord.stonerbot.DiscordBotStoner) {
			com.bestbudz.core.discord.stonerbot.DiscordBotStoner bot =
				(com.bestbudz.core.discord.stonerbot.DiscordBotStoner) stoner;

			// FIXED: Use the existing auto-banking system
			bot.addItemDirectlyToBank(selectedReward, 1);
			//System.out.println("DEBUG: Added item " + selectedReward + " directly to Discord bot's bank");
		} else {
			// Normal player - add to inventory
			stoner.getBox().add(selectedReward, 1);
			//System.out.println("DEBUG: Added item " + selectedReward + " to normal player's inventory");
		}
	}


  public static void main(String[] args) {
    int pickaxe = Pickaxe.TOOL_RING.getWeight();
    int ore_req = 1;
    int immunity = 4;

    System.out.println("Immunity: " + immunity + " [" + (int) (immunity * 5 / 3.0) + "s]");

    for (int i = ore_req; i < 100; i++) {

      int result = immunity - (int) ((i - ore_req) * 2 / (double) pickaxe);

      if (result <= 2) {
        System.out.println("Grade: " + i + " = " + result + " [" + (int) (result * 5 / 3.0) + "s]");
        break;
      }

      System.out.println("Grade: " + i + " = " + result + " [" + (int) (result * 5 / 3.0) + "s]");
    }
  }

  public enum Pickaxe {
    DRAGON_PICKAXE_OR(12797, 91, 1, new Animation(335)),
    TOOL_RING(6575, 1, 2, new Animation(6758));

    private static final HashMap<Integer, Pickaxe> PICKAXES = new HashMap<>();
    private final int item;
    private final int grade;
    private final int weight;
    private final Animation animation;

    Pickaxe(int item, int grade, int weight, Animation animation) {
      this.item = item;
      this.grade = grade;
      this.animation = animation;
      this.weight = weight;
    }

    public static void declare() {
      for (Pickaxe pickaxe : values()) {
        PICKAXES.put(pickaxe.item, pickaxe);
      }
    }

    public static Pickaxe get(Stoner stoner) {
      Pickaxe highest = null;

      Queue<Pickaxe> picks =
          new PriorityQueue<>((first, second) -> second.getGrade() - first.getGrade());

      if (stoner.getEquipment().getItems()[EquipmentConstants.RING_SLOT] != null) {
        highest =
            PICKAXES.get(stoner.getEquipment().getItems()[EquipmentConstants.RING_SLOT].getId());

        if (highest != null) {
          picks.add(highest);
          highest = null;
        }
      }

      for (Item item : stoner.getEquipment().getItems()) {
        if (item == null) {
          continue;
        }

        Pickaxe pick = PICKAXES.get(item.getId());

        if (pick == null) {
          continue;
        }

        picks.add(pick);
      }

      Pickaxe pick = picks.poll();

      if (pick == null) {
        return null;
      }

      while (stoner.getGrades()[Professions.QUARRYING] < pick.getGrade()) {
        if (highest == null) {
          highest = pick;
        }

        pick = picks.poll();
      }

      return pick;
    }

    public int getItem() {
      return item;
    }

    public int getGrade() {
      return grade;
    }

    public Animation getAnimation() {
      return animation;
    }

    public int getWeight() {
      return weight;
    }
  }

  public enum Ore {
    COPPER("Copper Ore", new int[] {13708, 13709}, 1, 125, new int[] {436}, -1, -1, -1),
    TIN("Tin Ore", new int[] {13712, 13713}, 1, 125, new int[] {438}, -1, -1, -1),
    IRON("Iron Ore", new int[] {13710, 13711}, 1, 125, new int[] {440}, -1, -1, -1),
    SILVER_ORE("Silver Ore", new int[] {13716, 13717}, 1, 125, new int[] {442}, -1, -1, -1),
    COAL_ORE("Coal Ore", new int[] {13706, 13714}, 1, 125, new int[] {453}, -1, -1, -1),
    GOLD_ORE("Gold Ore", new int[] {13707, 13715}, 1, 125, new int[] {444}, -1, -1, -1),
    MITHRIL_ORE("Mithril Ore", new int[] {13718, 13719}, 1, 125, new int[] {447}, -1, -1, 1),
    ADAMANTITE_ORE("Adamantite Ore", new int[] {13720, 14168}, 1, 125, new int[] {449}, -1, -1, -1),
    RUNITE_ORE("Runite Ore", new int[] {14175}, 1, 125, new int[] {451}, -1, -1, -1),
    ESSENCE("Essence", new int[] {14912, 2491}, 1, 125, new int[] {1436}, -1, -1, -1),
    GEM_ROCK(
        "Gem Rock",
        new int[] {14856, 14855, 14854},
        1,
        125,
        new int[] {1625, 1627, 1629, 1623, 1621, 1619, 1617, 1631, 6571},
        -1,
        -1,
        1);

    private static final HashMap<Integer, Ore> ORES = new HashMap<>();
    private final String name;
    private final int[] objects;
    private final int grade;
    private final double exp;
    private final int[] ore;
    private final int replacement;
    private final int respawn;
    private final int immunity;

    Ore(
        String name,
        int[] objects,
        int grade,
        double exp,
        int[] ore,
        int replacement,
        int respawn,
        int immunity) {
      this.name = name;
      this.objects = objects;
      this.grade = grade;
      this.exp = exp;
      this.ore = ore;
      this.replacement = replacement;
      this.respawn = respawn;
      this.immunity = immunity;
    }

    public static void declare() {
      for (Ore ore : values()) {
        for (int object : ore.objects) {
          ORES.put(object, ore);
        }
      }
    }

    public static Ore get(int id) {
      return ORES.get(id);
    }

    public String getName() {
      return name;
    }

    public int getGrade() {
      return grade;
    }

    public double getExp() {
      return exp;
    }

    public int[] getOre() {
      return ore;
    }

    public int getReplacement() {
      return replacement;
    }

    public int getRespawn() {
      return respawn;
    }

    public int getImmunity() {
      return immunity;
    }
  }
}
