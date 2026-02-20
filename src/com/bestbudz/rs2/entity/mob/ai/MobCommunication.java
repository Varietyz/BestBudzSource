package com.bestbudz.rs2.entity.mob.ai;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MobCommunication {
	private final Mob mob;

	public enum MessageType {
		ALERT_INTRUDER,
		ALERT_BACKUP_NEEDED,
		ALERT_MAGE_THREAT,
		ALERT_ARCHER_THREAT,
		ALERT_MELEE_THREAT,
		SWARM_CONVERGE,
		SWARM_SURROUND,
		SWARM_OVERWHELM,
		COMBAT_ENGAGE_GENERAL,
		COMBAT_ENGAGE_MAGE,
		COMBAT_ENGAGE_ARCHER,
		COMBAT_ENGAGE_MELEE,
		COMBAT_PAIN,
		RETREAT_FALLING_BACK,
		PATROL_ALL_CLEAR,
		PATROL_CONTINUING,
		PATROL_AREA_SECURE,
		RETREAT_RECOVERY
	}

	public MobCommunication(Mob mob) {
		this.mob = mob;
	}

	public void sendMessage(MessageType type) {

		if (Utility.randomNumber(10) < 9) {
			return;
		}

		String[] messages = getMessagesForType(type);
		if (messages.length > 0) {
			String message = messages[Utility.randomNumber(messages.length)];
			mob.getUpdateFlags().sendForceMessage(message);
		}
	}

	public void sendCombatMessage(Stoner player) {

		if (Utility.randomNumber(10) < 9) {
			return;
		}

		CombatTypes playerType = player.getCombat().getCombatType();
		MessageType messageType;

		switch (playerType) {
			case MAGE:
				messageType = MessageType.COMBAT_ENGAGE_MAGE;
				break;
			case SAGITTARIUS:
				messageType = MessageType.COMBAT_ENGAGE_ARCHER;
				break;
			case MELEE:
				messageType = MessageType.COMBAT_ENGAGE_MELEE;
				break;
			default:
				messageType = MessageType.COMBAT_ENGAGE_GENERAL;
				break;
		}

		String[] messages = getMessagesForType(messageType);
		if (messages.length > 0) {
			String message = messages[Utility.randomNumber(messages.length)];
			mob.getUpdateFlags().sendForceMessage(message);
		}
	}

	public void sendDamageMessage(long damage, long maxHealth) {
		if (damage > maxHealth * 0.3) {

			if (Utility.randomNumber(2) == 0) {
				String[] messages = getMessagesForType(MessageType.COMBAT_PAIN);
				if (messages.length > 0) {
					String message = messages[Utility.randomNumber(messages.length)];
					mob.getUpdateFlags().sendForceMessage(message);
				}
			}
		}
	}

	private String[] getMessagesForType(MessageType type) {
		switch (type) {

			case ALERT_INTRUDER:
				return new String[]{
					"Yo, someone's harshin' the mellow!",
					"Who invited this buzzkill?",
					"Intruder harshing my vibe!",
					"Security breach, bro!",
					"Hey man, stranger danger!",
					"Unwanted guest detected!",
					"Someone's sneaking in on my sesh!",
					"Dude, we got a lurker!",
					"Bogart spotted!",
					"This dude didn't bring any bud!",
					"Whoa, unauthorized presence, man!",
					"There's a sketchy dude over here!",
					"This ain't no smoke circle member!",
					"Hey man, they didn't pass the vibe check!",
					"Yo, trespasser trying to steal our stash!",
					"Vibe disturbance incoming!",
					"Hostile energy detected, bro!",
					"Someone's tryna raid the stash jar!",
					"Security breach! Hide the edibles!",
					"Who let this narc in?!"
				};

			case ALERT_BACKUP_NEEDED:
				return new String[]{
					"Yo I'm gonna need some bros here!",
					"Back me up, dudes!",
					"Help! They're killin' my high!",
					"Requesting backup — bring snacks too!",
					"Come on homies, I can't fight AND roll!",
					"Need assistance — my joint went out!",
					"Send the squad — we're in deep smoke!",
					"Under attack, call the crew!",
					"SOS — save our stash!",
					"I need backup, preferably baked!",
					"Bring the reinforcements, and the bong!",
					"Yo, I'm surrounded, send help!",
					"Someone pass the help blunt!",
					"Homies, this is getting sketchy!",
					"Call the high command!",
					"Brothers! I require assistance, dude!",
					"Help! My chill is compromised!",
					"Squad up! We got issues!",
					"Back me up, this is killin' my vibe!",
					"Need a hand before I whitey out!"
				};

			case ALERT_MAGE_THREAT:
				return new String[]{
					"Yo that wizard's got trippy vibes!",
					"Mage alert, stay spaced out — literally!",
					"Spellcaster detected, watch the head trips!",
					"Duuude magic guy incoming!",
					"Watch out! He's casting bad vibes!",
					"Magic dude detected — close the gap or we're toast!",
					"Wizard in the area, stay chill and spread!",
					"Yo, that's a sorcery bro!",
					"Some trippy magic user on deck!",
					"Keep distance, don't get hexed!",
					"Spell slinger spotted!",
					"Magic guy harshin' the scene!",
					"That dude's got more spells than strains!",
					"Wizard threat, wide circle bros!",
					"He's about to cast no-fun-icus!",
					"Heads up! Magic in the air!",
					"Arcane user trying to kill the vibe!",
					"Wizard energy detected — major buzzkill!",
					"Spell dude alert — everyone chill!",
					"That mage is about to spark chaos!"
				};

			case ALERT_ARCHER_THREAT:
				return new String[]{
					"Arrow dude on the loose!",
					"Whoa! Watch for flying sticks!",
					"Dude's sniping the vibe!",
					"Get cover — we're in the archer's sights!",
					"Yo, arrow slinger alert!",
					"Archer spotted! Duck your head!",
					"Bow guy is killin' our chill!",
					"Yo, arrows incoming, take cover!",
					"That ranged guy's trying to ruin our sesh!",
					"Heads up! Someone's shooting at us!",
					"Yo archer! Put the bow down, puff instead!",
					"Ranged hostile detected!",
					"Someone's playing Robin Hood up in here!",
					"Duck! Arrows messing up my zen!",
					"He's trying to pin me like a roach clip!",
					"Arrow dude harshing the harmony!",
					"Incoming pointy sticks!",
					"Archer threat — let's crowd him, bro!",
					"Yo! The dude's flinging splinters!",
					"Watch for arrows, protect the stash!"
				};

			case ALERT_MELEE_THREAT:
				return new String[]{
					"Melee fighter up in my grill!",
					"Yo this dude wants to scrap!",
					"Close combat guy incoming!",
					"Someone's swinging steel at my chill!",
					"Warrior detected — he ain't here to share!",
					"He's tryin' to harsh me with fists!",
					"Close combat alert — hold your bongs!",
					"Melee threat! Form up stoner squad!",
					"Yo! He's coming in hot!",
					"Steel swinger detected, bro!",
					"Dude's bringing fists to a smoke fight!",
					"Melee dude harshin' the vibe again!",
					"He's getting all up in my personal smoke cloud!",
					"Keep him at bong-length!",
					"Watch out! Swords and shit!",
					"Sword guy trying to kill the vibe!",
					"Warrior alert — someone pass me a blunt shield!",
					"He's swinging steel — that ain't chill!",
					"Yo! This guy wants to duel my chill!",
					"Close fighter, keep formation tight!"
				};

			case COMBAT_ENGAGE_GENERAL:
				return new String[]{
					"Time to spark up this fight!",
					"Yo, you wanna mess with my chill?",
					"Prepare to face the high guard!",
					"For the realm — and the stash!",
					"Stand down or get smoked!",
					"I'll defend this sesh with my life!",
					"Yo you just walked into the wrong circle!",
					"Your vibes are off, man!",
					"Face me, buzzkill!",
					"You're about to get roasted!",
					"No one touches our stash, bro!",
					"You dare challenge my high?",
					"I'll protect this circle, man!",
					"Halt, thief of vibes!",
					"You will not pass the blunt — or this line!",
					"Mess with the stoner, get the stone!",
					"Prepare for a beatdown, bro!",
					"You're killin' the vibe — now I'm mad!",
					"You're facing the baked avenger!",
					"Your energy's way off — gotta shut you down!"
				};

			case COMBAT_ENGAGE_MAGE:
				return new String[]{
					"Yo wizard, you tryna harsh my mellow?",
					"Puff puff pass that magic, bro!",
					"Your spells are like stems and seeds — useless!",
					"Yo, stop sparking that arcane kush!",
					"I'll light you up like my bong!",
					"Bro, your staff ain't even packed!",
					"Quit casting, hit this instead!",
					"Your magic's weak sauce, homie!",
					"Spell slinger, more like buzz killer!",
					"No room for sorcery at this sesh!",
					"Yo, you dropping spells like bad edibles!",
					"Can't smoke me, wizard wannabe!",
					"Your robe game weak, bro!",
					"Abraca-dab-ra, time to get smoked!",
					"That's some mid-tier magic, dude!",
					"Your fireball's just a weak torch!",
					"Bro, that's not how you light up!",
					"This ain't Hogwarts, it's Hotboxwarts!",
					"Yo mage, your aura's harshing my vibe!",
					"Put that wand down and grab a joint!"
				};

			case COMBAT_ENGAGE_ARCHER:
				return new String[]{
					"Yo Legolas, bring that weak sauce closer!",
					"Arrows? I thought we were sharing blunts!",
					"You can't hit what's already faded!",
					"Come at me bro, stop hiding behind trees!",
					"Archers shoot, stoners puff — know your lane!",
					"Quit firing arrows, pass the lighter!",
					"Your aim's as bad as my munchie choices!",
					"I'll dodge that like I dodge the cops!",
					"Yo, you tryna pop my high with that bow?",
					"Bro, we don't need pointy sticks at this sesh!",
					"Quit camping, join the circle!",
					"Yo, I'm too blazed to feel that shot!",
					"You got more arrows or just empty threats?",
					"Archers aim, stoners chill — get it right!",
					"Close range or no range, homie!",
					"Stop trying to snipe, pass the pipe!",
					"Arrows won't stop this cloud of smoke!",
					"You shooting arrows or trying to light my bowl?",
					"Your bow's like a broken vape — useless!",
					"Yo, stop trying to poke holes in my vibes!"
				};

			case COMBAT_ENGAGE_MELEE:
				return new String[]{
					"Steel meets reefer, homie!",
					"Let's hash it out with fists!",
					"Yo, sword bros, bring it in tight!",
					"I'll spark ya like my blunt tip!",
					"Swingin' blades? I'm swingin' joints!",
					"Steel clashin'? More like sesh clashin'!",
					"Your blade's duller than my grinder!",
					"Hit harder, bro, I barely felt that!",
					"Your swordplay's weak, grab a bong!",
					"You tryna slice my stash or what?",
					"Yo, warrior, take a hit, chill out!",
					"That's some mid-tier swordplay, bro!",
					"Your form's as shaky as my first dab!",
					"Swing again, maybe you'll hit my vibe!",
					"Yo, I parry with peace, not pieces!",
					"Bro, I'll outsmoke and outslash ya!",
					"Melee? More like mellow out!",
					"Your blade ain't sharper than my wit!",
					"Yo, let's spark up, not spark steel!",
					"Put down the sword, pick up the spliff!"
				};

			case COMBAT_PAIN:
				return new String[]{
					"Ow, bro, that's a vibe kill!",
					"You harshin' my buzz with that hit!",
					"Yo, that was a low blow, man!",
					"Damn, right in the smoke hole!",
					"Bro, that shot felt like a bad edible!",
					"Yo, not cool, my vibe's dented!",
					"You'll pay for poppin' my high!",
					"That hit was dry as an empty grinder!",
					"Bro, you scuffed my sesh!",
					"Is that all? I've had worse from a hot dab!",
					"Lucky shot, bro!",
					"Yo, that nicked my good kush pocket!",
					"Dude, that's like spilling my stash!",
					"You're gonna regret that buzz kill!",
					"Argh, that stung worse than no lighter!",
					"Bro, that hurt like stepping on a pipe!",
					"You just singed my chill!",
					"Yo, that's it? Weak hit!",
					"I'll spark you back for that one!",
					"Bro, don't mess the mellow, man!"
				};

			default:
				return new String[]{"..."};
		}
	}
}
