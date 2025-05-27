
/** Real Object Manger */ 

package com.mayhem.rs2.entity.object;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.mayhem.core.cache.map.MapLoading;
import com.mayhem.core.cache.map.RSObject;
import com.mayhem.core.cache.map.Region;
import com.mayhem.rs2.content.skill.firemaking.FireColor;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.object.GameObject;

@SuppressWarnings("all")
public class ObjectManager {

	private static final List<GameObject> active = new LinkedList<GameObject>();
	private static final Deque<GameObject> register = new LinkedList<GameObject>();
	private static final Queue<GameObject> send = new ConcurrentLinkedQueue<GameObject>();
	public static final int BLANK_OBJECT_ID = 2376;

	private static Logger logger = Logger.getLogger(MapLoading.class.getSimpleName());

	public static void declare() {

		active.clear();
		
		/**  Wildy Church */ 
		spawnWithObject(13176, 2958, 3821, 0, 0, 1);
		spawnWithObject(13176, 2957, 3821, 0, 0, 1);
		spawnWithObject(13176, 2958, 3820, 0, 0, 1);
		spawnWithObject(13176, 2957, 3820, 0, 0, 1);
		setClipToZero(2958, 3821, 0);
		setClipToZero(2957, 3821, 0);
		setClipToZero(2958, 3820, 0);
		setClipToZero(2957, 3820, 0);
		
		
		/** Thermonuclear Wildy */ 
		spawnWithObject(788, 3265, 3877, 0, 10, 3);
		
		
		/** Home Area */
		//Edgeville Home (old) 
		deleteWithObject(3092, 3508, 0);
		deleteWithObject(3092, 3512, 0);
		setClipped(3100, 3509, 0);
		setClipped(3100, 3510, 0);
		setClipped(3100, 3511, 0);
		setClipped(3101, 3509, 0);
		setClipped(3101, 3510, 0);
		setClipped(3101, 3511, 0);
		setClipped(3101, 3512, 0);
		spawnWithObject(412, 3100, 3508, 0, 10, 3); // Chaos Alter has all 3 magic spell books 
		
		
		// New Raid Boss Vespula (nex gear boss) 
		
		spawnWithObject(13279, 2382, 4716, 0, 10, 2); // Hag Statue Tip 4716 is hor (e/w) 2382 is up and down (n/s)
		spawnWithObject(18804, 2383, 4716, 0, 10, 2); // treasure chest large 
		spawnWithObject(13279, 2385, 4716, 0, 10, 2); // Hag Statue
		
		// End New Raid Boss Vespula (nex gear boss)
		
		
		//Start Brand New Snow Home 2865 9950 
		deleteAndRemoveClip(2865, 9949, 0);
		spawnWithObject(1954, 2867, 9932, 0, 10, 2);
		spawnWithObject(1954, 2866, 9932, 0, 10, 2);
		spawnWithObject(1954, 2868, 9932, 0, 10, 2);
		deleteAndRemoveClip(2868, 9954, 0);
		deleteAndRemoveClip(2869, 9953, 0);
		deleteAndRemoveClip(2862, 9953, 0); 
		deleteAndRemoveClip(2865, 9946, 0);
		deleteAndRemoveClip(2865, 9947, 0);
		deleteAndRemoveClip(2863, 9949, 0);
		deleteAndRemoveClip(2863, 9947, 0);
		deleteAndRemoveClip(2871, 9948, 0);
		deleteAndRemoveClip(2873, 9946, 0);
		deleteAndRemoveClip(2870, 9945, 0);
		deleteAndRemoveClip(2864, 9948, 0);
		deleteAndRemoveClip(2864, 9960, 0);
		deleteAndRemoveClip(2867, 9960, 0);
		deleteAndRemoveClip(2858, 9953, 0);
		deleteAndRemoveClip(2857, 9953, 0);
		deleteAndRemoveClip(2857, 9955, 0);
		deleteAndRemoveClip(2858, 9957, 0);
		deleteAndRemoveClip(2856, 9956, 0);
		deleteAndRemoveClip(2855, 9955, 0);
		deleteAndRemoveClip(2856, 9957, 0);
		deleteAndRemoveClip(2858, 9951, 0);
		deleteAndRemoveClip(2875, 9958, 0);
		deleteAndRemoveClip(2875, 9957, 0);
		deleteAndRemoveClip(2875, 9956, 0);
		deleteAndRemoveClip(2875, 9954, 0);
		deleteAndRemoveClip(2875, 9953, 0);
		deleteAndRemoveClip(2873, 9959, 0);
		deleteAndRemoveClip(2873, 9957, 0);
		deleteAndRemoveClip(2876, 9957, 0);
		deleteAndRemoveClip(2876, 9953, 0);
		deleteAndRemoveClip(2877, 9954, 0);
		deleteAndRemoveClip(2875, 9944, 0);
		deleteAndRemoveClip(2875, 9943, 0);
		deleteAndRemoveClip(2875, 9942, 0);
		deleteAndRemoveClip(2874, 9943, 0);
		deleteAndRemoveClip(2873, 9941, 0);
		deleteAndRemoveClip(2873, 9940, 0);
		deleteAndRemoveClip(2873, 9938, 0);
		deleteAndRemoveClip(2874, 9940, 0);
		deleteAndRemoveClip(2872, 9940, 0);
		deleteAndRemoveClip(2856, 9954, 0);
		deleteAndRemoveClip(2864, 9942, 0);
		deleteAndRemoveClip(2864, 9941, 0);
		deleteAndRemoveClip(2864, 9940, 0);
		deleteAndRemoveClip(2865, 9941, 0);
		deleteAndRemoveClip(2863, 9941, 0);
		deleteAndRemoveClip(2860, 9940, 0);
		deleteAndRemoveClip(2861, 9938, 0);
		deleteAndRemoveClip(2861, 9939, 0);
		deleteAndRemoveClip(2860, 9939, 0);
		deleteAndRemoveClip(2862, 9937, 0);
		deleteAndRemoveClip(2862, 9936, 0);
		deleteAndRemoveClip(2862, 9935, 0);
		deleteAndRemoveClip(2863, 9936, 0);
		deleteAndRemoveClip(2866, 9939, 0);
		deleteAndRemoveClip(2865, 9935, 0);
		deleteAndRemoveClip(2869, 9936, 0);
		deleteAndRemoveClip(2866, 9938, 0);
	
		
		
		//End Snow Home 2865 9950 
		
		
		//Korend Castle Home Un-used  
		
		//spawnWithObject(3327, 1650, 3671, 0, 10, 3);
		//spawnWithObject(1190, 1634, 3671, 0, 10, 3); // Sunflower 
		//spawnWithObject(1190, 1634, 3671, 0, 10, 3);
		
		spawnWithObject(585, 1632, 3685, 0, 10, 0); // Sara statue 
		spawnWithObject(587, 1631, 3685, 0, 10, 0); // Zammy statue 
		spawnWithObject(585, 1630, 3685, 0, 10, 0); // Sara statue
		spawnWithObject(2987, 1635, 3675, 0, 0, 0);
		spawnWithObject(2987, 1636, 3675, 0, 0, 0);
		spawnWithObject(566, 1607, 3674, 0, 10, 0);
		spawnWithObject(13277, 1607, 3673, 0, 0, 0);
		spawnWithObject(566, 1607, 3672, 0, 10, 4);
		
		deleteAndRemoveClip(1639, 3677, 0);
		deleteAndRemoveClip(1638, 3676, 0);
		deleteAndRemoveClip(1636, 3676, 0);
		deleteAndRemoveClip(1641, 3671, 0);
		deleteAndRemoveClip(1639, 3673, 0);
		
		
		
		deleteAndRemoveClip(1646, 3670, 0);
		deleteAndRemoveClip(1646, 3671, 0);
		deleteAndRemoveClip(1645, 3670, 0);
		deleteAndRemoveClip(1645, 3671, 0);
		deleteAndRemoveClip(1645, 3670, 0);
		deleteAndRemoveClip(1645, 3669, 0);
		deleteAndRemoveClip(1646, 3669, 0);
		deleteAndRemoveClip(1647, 3669, 0);
		deleteAndRemoveClip(1636, 3674, 0);
		deleteAndRemoveClip(1642, 3682, 0);
		deleteAndRemoveClip(1637, 3675, 0);
		deleteAndRemoveClip(1638, 3674, 0);
		deleteAndRemoveClip(1638, 3674, 0);
		deleteAndRemoveClip(1638, 3673, 0);
		deleteAndRemoveClip(1638, 3672, 0);
		deleteAndRemoveClip(1637, 3671, 0);
		deleteAndRemoveClip(1636, 3671, 0);
		deleteAndRemoveClip(1635, 3671, 0);
		deleteAndRemoveClip(1634, 3672, 0);
		deleteAndRemoveClip(1634, 3673, 0);
		deleteAndRemoveClip(1634, 3674, 0);
		deleteAndRemoveClip(1643, 3683, 0);
		deleteAndRemoveClip(1636, 3673, 0);
		deleteAndRemoveClip(1639, 3685, 0);
		deleteAndRemoveClip(1635, 3685, 0);
		deleteAndRemoveClip(1635, 3661, 0);
		deleteAndRemoveClip(1639, 3661, 0);
		deleteAndRemoveClip(1659, 3670, 0);
		deleteAndRemoveClip(1660, 3670, 0);
		deleteAndRemoveClip(1660, 3669, 0);
		deleteAndRemoveClip(1660, 3668, 0);
		deleteAndRemoveClip(1659, 3669, 0);
		deleteAndRemoveClip(1659, 3668, 0);
		deleteAndRemoveClip(1661, 3677, 0);
		deleteAndRemoveClip(1660, 3677, 0);
		deleteAndRemoveClip(1659, 3677, 0);
		deleteAndRemoveClip(1661, 3678, 0);
		deleteAndRemoveClip(1660, 3678, 0);
		deleteAndRemoveClip(1659, 3678, 0);
		
		deleteAndRemoveClip(1625, 3676, 0);
		deleteAndRemoveClip(1625, 3668, 0);
		deleteAndRemoveClip(1635, 3662, 0);
		deleteAndRemoveClip(1631, 3661, 0);
		deleteAndRemoveClip(1633, 3662, 0);
		deleteAndRemoveClip(1633, 3661, 0);
		deleteAndRemoveClip(1634, 3661, 0);
		deleteAndRemoveClip(1638, 3661, 0);
		deleteAndRemoveClip(1640, 3661, 0);
		deleteAndRemoveClip(1641, 3661, 0);
		deleteAndRemoveClip(1641, 3662, 0);
		deleteAndRemoveClip(1639, 3662, 0);
		deleteAndRemoveClip(1626, 3664, 0);
		deleteAndRemoveClip(1626, 3663, 0);
		deleteAndRemoveClip(1626, 3681, 0);
		deleteAndRemoveClip(1625, 3683, 0);
		deleteAndRemoveClip(1626, 3684, 0);
		deleteAndRemoveClip(1634, 3683, 0);
		deleteAndRemoveClip(1637, 3683, 0);
		deleteAndRemoveClip(1637, 3684, 0);
		deleteAndRemoveClip(1638, 3683, 0);
		deleteAndRemoveClip(1640, 3685, 0);
		deleteAndRemoveClip(1648, 3683, 0);
		deleteAndRemoveClip(1642, 3662, 0);
		
		
		
		/**
		spawnWithObject(410, 3092, 3508, 0, 10, 0);//Lunar Altar
		spawnWithObject(6552, 3092, 3512, 0, 10, 0);//Ancient Altar
		*/ 
		spawnWithObject(409, 3100, 3511, 0, 10, 3);//prayer Altar	
		spawnWithObject(4875, 3092, 3487, 0, 10, 5);//Food stall
		spawnWithObject(4876, 3093, 3487, 0, 10, 5);//General stall
		spawnWithObject(4874, 3094, 3487, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 3095, 3487, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 3096, 3487, 0, 10, 5);//Scimitar stall
		spawnWithObject(2191, 3090, 3492, 0, 10, 1);//Crystal chest
		
		deleteWithObject(3084, 3497, 0);
		//spawnWithObject(29152, 3084, 3497, 0, 10, 3);//Player owned shops
		spawnWithObject(6084, 1642, 3632, 0, 10, 1);//bank booth
		spawnWithObject(6084, 1642, 3631, 0, 10, 1);//bank booth
		spawnWithObject(6084, 1642, 3630, 0, 10, 1);//bank booth
		spawnWithObject(13465, 3169, 3471, 0, 10, 1);//banner
		spawnWithObject(13465, 3159, 3471, 0, 10, 1);//banner
		spawnWithObject(26492, 3092, 3488, 0, 10, 3);//vote booth
		spawnWithObject(26761, 3090, 3475, 0, 10, 1); // wall lever 
		deleteWithObject(3090, 3475, 0);//max cape object 29173
		deleteWithObject(3090, 3476, 0);//max cape object 29174
		deleteWithObject(3090, 3477, 0);//max cape object 29172
		deleteWithObject(3090, 3478, 0);//max cape object 29175 
		deleteWithObject(3090, 3479, 0);//max cape object 29171
		deleteWithObject(3090, 3480, 0);//max cape object 29170
		deleteWithObject(3084, 3497, 0); // player auction object 29152 
		deleteAndRemoveClip(3095, 3477, 0);//standing torch 
		
		/**
		spawnWithObject(29171, 3090, 3476, 0, 10, 1);//max cape
		spawnWithObject(29175, 3090, 3477, 0, 10, 1);//max cape
		spawnWithObject(29172, 3090, 3478, 0, 10, 1);//max cape
		spawnWithObject(29174, 3090, 3479, 0, 10, 1);//max cape
		spawnWithObject(29173, 3090, 3480, 0, 10, 1);//max cape
		*/ 
		
		spawnWithObject(14503, 1627, 3658, 0, 10, 0);
		spawnWithObject(14503, 1630, 3658, 0, 10, 0);
		spawnWithObject(27282, 3084, 3488, 0, 10, 3);
		setClipToZero(1636, 3638, 0);
		setClipToZero(1636, 3639, 0);
		deleteWithObject(3161, 3499, 0);
		

		
		
		//mini raids
		spawnWithObject(7324, 2796, 5167, 0, 10, 0);
		spawnWithObject(7322, 3087, 3483, 0, 10, 0);
		spawnWithObject(14846, 3086, 3483, 0, 10, 0);
		
		
		//elite donator zone
		spawnWithObject(1762, 2126, 4907, 0, 10, 2);//Magic tree
		spawnWithObject(1762, 2123, 4906, 0, 10, 2);//Magic tree
		spawnWithObject(409, 2133, 4905, 0, 10, 2);//prayer Altar	
		spawnWithObject(18772, 2129, 4914, 0, 10, 1);//MysteryBox chest
		spawnWithObject(6282, 2133, 4914, 0, 10, 0);//members portal
		spawnWithObject(6162, 2121, 4914, 0, 10, 1); //members stall
		spawnWithObject(14175, 2128, 4903, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2129, 4903, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2130, 4903, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2131, 4903, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2132, 4903, 0, 10, 1);//Rune Ore		
		spawnWithObject(13720, 2133, 4903, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2134, 4903, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2135, 4903, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2136, 4903, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2137, 4903, 0, 10, 1);//Adamant Ore		
		
		
		
		//edge tables
		deleteWithObject(3091, 3495, 0);
		deleteWithObject(3092, 3496, 0);
		deleteWithObject(3090, 3496, 0);
		deleteWithObject(3090, 3494, 0);
		setClipToZero(3091, 3495, 0);
		spawnWithObject(26518, 2885, 5333, 2, 10, 0);//zammy bridge
		
		/* motherlode */
		deleteWithObject(3731, 5683, 0);
		setClipToZero(3731, 5683, 0);
		deleteWithObject(3733, 5680, 0);
		setClipToZero(3733, 5680, 0);
		setClipToZero(3727, 5681, 0);
		setClipToZero(3727, 5682, 0);
		setClipToZero(3727, 5683, 0);
		deleteWithObject(3727, 5683, 0);
		
		
		/* Membership Area */
		deleteWithObject(2822, 3356, 0);
		deleteWithObject(2822, 3355, 0);
		deleteWithObject(2822, 3351, 0);
		deleteWithObject(2822, 3350, 0);
		deleteWithObject(2818, 3351, 0);
		deleteWithObject(2818, 3355, 0);
		deleteWithObject(2817, 3355, 0);
		deleteWithObject(2816, 3354, 0);
		deleteWithObject(2818, 3356, 0);
		deleteWithObject(2816, 3352, 0);
		deleteWithObject(2817, 3353, 0);
		deleteWithObject(2816, 3351, 0);
		deleteWithObject(2821, 3357, 0);
		deleteWithObject(2822, 3360, 0);
		deleteWithObject(2822, 3361, 0);
		deleteWithObject(2821, 3360, 0);
		deleteWithObject(2821, 3361, 0);
		deleteWithObject(2820, 3360, 0);
		deleteWithObject(2820, 3361, 0);	
		deleteWithObject(2819, 3360, 0);
		deleteWithObject(2819, 3361, 0);
		deleteWithObject(2818, 3360, 0);
		deleteWithObject(2818, 3361, 0);
		deleteWithObject(2817, 3360, 0);
		deleteWithObject(2817, 3361, 0);
		deleteWithObject(2817, 3359, 0);
		deleteWithObject(2817, 3358, 0);
		deleteWithObject(2817, 3357, 0);
		deleteWithObject(2857, 3338, 0);
		deleteWithObject(2859, 3338, 0);
		deleteWithObject(2860, 3338, 0);
		deleteWithObject(2862, 3338, 0);
		deleteWithObject(2861, 3335, 0);
		deleteWithObject(2862, 3335, 0);	
		deleteWithObject(2844, 3333, 0);
		deleteWithObject(2845, 3337, 0);
		deleteWithObject(2844, 3337, 0);
		deleteWithObject(2845, 3338, 0);
		deleteWithObject(2844, 3338, 0);
		deleteWithObject(2853, 3355, 0);
		deleteWithObject(2853, 3353, 0);
		deleteWithObject(2849, 3353, 0);
		deleteWithObject(2849, 3354, 0);
		deleteWithObject(2849, 3355, 0);
		deleteWithObject(2851, 3353, 0);
		deleteWithObject(2809, 3341, 0);
		deleteWithObject(2812, 3341, 0);
		deleteWithObject(2812, 3343, 0);
		deleteWithObject(2810, 3342, 0);
		deleteWithObject(2808, 3343, 0);
		deleteWithObject(2808, 3346, 0);
		deleteWithObject(2809, 3346, 0);
		deleteWithObject(2810, 3346, 0);
		deleteWithObject(2812, 3346, 0);
		deleteWithObject(2807, 3354, 0);
		deleteWithObject(2807, 3355, 0);
		deleteWithObject(2806, 3355, 0);
		deleteWithObject(2806, 3356, 0);
		deleteWithObject(2807, 3356, 0);
		deleteWithObject(2808, 3356, 0);				
		deleteWithObject(2830, 3350, 0);
		deleteWithObject(2831, 3348, 0);
		deleteWithObject(2830, 3349, 0);
		deleteWithObject(2816, 3361, 0);	
		deleteWithObject(2812, 3364, 0);
		deleteWithObject(2814, 3364, 0);
		deleteWithObject(2816, 3363, 0);
		deleteWithObject(2818, 3363, 0);
		deleteWithObject(2819, 3362, 0);	
		deleteWithObject(2815, 3358, 0);
		deleteWithObject(2814, 3357, 0);	
		deleteWithObject(2835, 3355, 0);	
		deleteWithObject(2822, 3354, 0);
		deleteWithObject(2820, 3356, 0);
		setClipToZero(2104, 3910, 0);
		spawnWithObject(6282, 2820, 3350, 0, 10, 0);//members portal
		spawnWithObject(27254, 2816, 3358, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3357, 0, 10, 3);//Banks	
		spawnWithObject(27254, 2810, 3343, 0, 10, 3);//Banks
		spawnWithObject(27254, 2874, 3339, 0, 10, 3);//Banks
		spawnWithObject(27254, 2874, 3340, 0, 10, 3);//Banks
		spawnWithObject(27254, 2829, 3351, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3356, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3355, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3354, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3353, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3352, 0, 10, 3);//Banks
		spawnWithObject(27254, 2816, 3351, 0, 10, 3);//Banks		
		spawnWithObject(27254, 2809, 3347, 0, 10, 0);//Banks				
		spawnWithObject(27254, 2827, 3355, 0, 10, 0);//Banks		
		spawnWithObject(9472, 2818, 3351, 0, 10, 5);//Shop Exchange
		spawnWithObject(27254, 2857, 3338, 0, 10, 0);//Banks
		spawnWithObject(4875, 2863, 3338, 0, 10, 5);//Food stall
		spawnWithObject(4876, 2862, 3338, 0, 10, 5);//General stall
		spawnWithObject(4874, 2861, 3338, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 2860, 3338, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 2859, 3338, 0, 10, 5);//Scmitar stall
		spawnWithObject(26181, 2874, 3333, 0, 10, 0);//Range
		spawnWithObject(4309, 2847, 3333, 0, 10, 2);//Spinning wheel
		spawnWithObject(11601, 2845, 3333, 0, 10, 3);//Pottery
		spawnWithObject(22472, 2844, 3338, 0, 10, 2);//Tab creation
		spawnWithObject(13618, 2850, 3355, 0, 10, 0);//Wyvern teleport
		spawnWithObject(13619, 2853, 3353, 0, 10, 1);//Fountain of rune teleport
		spawnWithObject(2191, 2818, 3356, 0, 10, 4);//Crystal chest
		spawnWithObject(18772, 2821, 3358, 0, 10, 1);//MysteryBox chest
		spawnWithObject(2097, 2830, 3349, 0, 10, 1);//Anvil
		spawnWithObject(11764, 2811, 3361, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2810, 3359, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2815, 3361, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2815, 3359, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2812, 3364, 0, 10, 1);//Magic Tree
		spawnWithObject(11764, 2814, 3364, 0, 10, 1);//Magic Tree
		spawnWithObject(11758, 2809, 3356, 0, 10, 1);//Yew Tree
		spawnWithObject(11758, 2809, 3353, 0, 10, 1);//Yew Tree
		spawnWithObject(11758, 2809, 3350, 0, 10, 1);//Yew Tree
		spawnWithObject(11762, 2804, 3344, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2804, 3346, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2806, 3348, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2806, 3351, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2805, 3353, 0, 10, 1);//Maple Tree
		spawnWithObject(14175, 2824, 3359, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2824, 3358, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2824, 3357, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2826, 3359, 0, 10, 1);//Rune Ore
		spawnWithObject(14175, 2825, 3356, 0, 10, 1);//Rune Ore		
		spawnWithObject(13720, 2828, 3358, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2829, 3358, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2830, 3358, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2831, 3357, 0, 10, 1);//Adamant Ore
		spawnWithObject(13720, 2831, 3356, 0, 10, 1);//Adamant Ore		
		spawnWithObject(13707, 2830, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13707, 2831, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13707, 2832, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13707, 2833, 3354, 0, 10, 1);//Gold Ore
		spawnWithObject(13706, 2833, 3356, 0, 10, 1);//Coal
		spawnWithObject(13706, 2834, 3356, 0, 10, 1);//Coal
		spawnWithObject(13706, 2835, 3356, 0, 10, 1);//Coal
		spawnWithObject(13706, 2835, 3354, 0, 10, 1);//Coal
		
		spawnWithObject(6943, 2088, 3904, 0, 10, 0);//Banks
		spawnWithObject(6943, 2089, 3920, 0, 10, 1);//Banks
		spawnWithObject(6943, 2107, 3911, 0, 10, 0);//Banks
		spawnWithObject(6943, 2079, 3927, 0, 10, 0);//Banks
		spawnWithObject(6943, 2080, 3927, 0, 10, 0);//Banks
		spawnWithObject(6943, 2091, 3927, 0, 10, 0);//Banks
		spawnWithObject(6943, 2093, 3927, 0, 10, 0);//Banks
		spawnWithObject(6943, 2098, 3927, 0, 10, 0);//Banks
		spawnWithObject(6943, 2106, 3900, 0, 10, 1);//Banks
		spawnWithObject(409, 2069, 3911, 0, 10, 3);//prayer altar
		spawnWithObject(26820, 2104, 3918, 0, 10, 2);//vote booth
		spawnWithObject(6943, 2857, 3338, 0, 10, 0);//Banks
		spawnWithObject(6162, 2100, 3910, 0, 10, 1); //members stall
		spawnWithObject(4875, 2105, 3916, 0, 10, 5);//Food stall
		spawnWithObject(4876, 2104, 3916, 0, 10, 5);//General stall
		spawnWithObject(4874, 2103, 3916, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 2102, 3916, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 2101, 3916, 0, 10, 5);//Scimtar stall
		spawnWithObject(26181, 2092, 3920, 0, 10, 2);//cooking Range
		spawnWithObject(4309, 2090, 3907, 0, 10, 1);//Spinning wheel
		spawnWithObject(11601, 2086, 3907, 0, 10, 0);//Pottery
		spawnWithObject(22472, 2097, 3917, 0, 10, 0);//Tab creation
		spawnWithObject(13618, 2094, 3918, 0, 10, 0);//Wyvern teleport
		spawnWithObject(13619, 2092, 3909, 0, 10, 0);//Fountain of rune teleport
		spawnWithObject(2191, 2098, 3916, 0, 10, 2);//Crystal chest
		spawnWithObject(18772, 2095, 3905, 0, 10, 2);//MysteryBox chest
		spawnWithObject(2097, 2102, 3908, 0, 10, 0);//Anvil
		spawnWithObject(2097, 2107, 3908, 0, 10, 2);//Anvil
		spawnWithObject(2030, 2103, 3905, 0, 10, 3);//furnace
		spawnWithObject(1761, 2102, 3929, 0, 10, 0);//Magic Tree
		spawnWithObject(1761, 2099, 3930, 0, 10, 1);//Magic Tree
		spawnWithObject(1761, 2096, 3932, 0, 10, 1);//Magic Tree
		spawnWithObject(1761, 2093, 3932, 0, 10, 1);//Magic Tree
		spawnWithObject(1761, 2090, 3932, 0, 10, 1);//Magic Tree
		spawnWithObject(1761, 2087, 3932, 0, 10, 1);//Magic Tree
		spawnWithObject(1753, 2084, 3932, 0, 10, 1);//Yew Tree
		spawnWithObject(1753, 2081, 3932, 0, 10, 1);//Yew Tree
		spawnWithObject(1753, 2078, 3932, 0, 10, 1);//Yew Tree
		spawnWithObject(11762, 2075, 3932, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2074, 3929, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2077, 3928, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2081, 3928, 0, 10, 1);//Maple Tree
		spawnWithObject(11762, 2084, 3928, 0, 10, 1);//Maple Tree
		spawnWithObject(7461, 2103, 3903, 0, 10, 1);//Rune Ore
		spawnWithObject(7461, 2104, 3903, 0, 10, 0);//Rune Ore
		spawnWithObject(7461, 2105, 3903, 0, 10, 1);//Rune Ore
		spawnWithObject(7461, 2106, 3903, 0, 10, 2);//Rune Ore
		spawnWithObject(7461, 2110, 3902, 0, 10, 1);//Rune Ore		
		spawnWithObject(7460, 2103, 3900, 0, 10, 0);//Adamant Ore
		spawnWithObject(7460, 2103, 3899, 0, 10, 1);//Adamant Ore
		spawnWithObject(7460, 2103, 3898, 0, 10, 0);//Adamant Ore
		spawnWithObject(7460, 2103, 3897, 0, 10, 1);//Adamant Ore
		spawnWithObject(7460, 2103, 3896, 0, 10, 0);//Adamant Ore		
		spawnWithObject(7491, 2109, 3900, 0, 10, 1);//Gold Ore
		spawnWithObject(7491, 2108, 3898, 0, 10, 0);//Gold Ore
		spawnWithObject(7492, 2107, 3897, 0, 10, 1);//mith Ore
		spawnWithObject(7492, 2106, 3896, 0, 10, 0);//mith Ore
		spawnWithObject(7456, 2102, 3900, 0, 10, 1);//Coal
		spawnWithObject(7456, 2101, 3900, 0, 10, 0);//Coal
		spawnWithObject(7456, 2100, 3900, 0, 10, 1);//Coal
		spawnWithObject(7456, 2102, 3903, 0, 10, 0);//Coal
		spawnWithObject(4090, 2095, 3924, 0, 10, 0);//blood Altar
		//spawnWithObject(16604, 2086, 3913, 0, 10, 0);//Dream tree
		spawnWithObject(6282, 2108, 3925, 0, 10, 0);//boss portals
		deleteWithObject(2072, 3911, 0);
		deleteWithObject(2078, 3910, 0);
		deleteWithObject(2104, 3908, 0);
		deleteWithObject(2089, 3919, 0);
		deleteWithObject(2105, 3907, 0);
		deleteWithObject(2104, 3905, 0);
		deleteWithObject(2106, 3905, 0);
		deleteWithObject(2107, 3906, 0);
		deleteWithObject(2107, 3907, 0);
		deleteWithObject(2107, 3909, 0);
		deleteWithObject(2107, 3910, 0);
		deleteWithObject(2102, 3904, 0);
		deleteWithObject(2102, 3905, 0);
		deleteWithObject(2102, 3906, 0);
		deleteWithObject(2102, 3907, 0);
		deleteWithObject(2098, 3906, 0);
		deleteWithObject(2097, 3920, 0);
		deleteWithObject(2094, 3907, 0);
		deleteWithObject(2094, 3908, 0);
		deleteWithObject(2092, 3921, 0);
		deleteWithObject(2088, 3906, 0);
		deleteWithObject(2086, 3908, 0);
		deleteWithObject(2090, 3904, 0);
		deleteWithObject(2090, 3906, 0);
		deleteWithObject(2090, 3908, 0);
		deleteWithObject(2090, 3909, 0);
		deleteWithObject(2096, 3906, 0);
		deleteWithObject(2097, 3905, 0);
		deleteWithObject(2096, 3909, 0);
		deleteWithObject(2096, 3910, 0);
		deleteWithObject(2118, 3911, 0);
		deleteWithObject(2115, 3882, 0);
		deleteWithObject(2115, 3880, 0);
		deleteWithObject(2116, 3879, 0);
		deleteWithObject(2118, 3879, 0);
		deleteWithObject(2117, 3923, 0);
		deleteWithObject(2120, 3923, 0);
		deleteWithObject(2116, 3925, 0);
		deleteWithObject(2119, 3925, 0);
		deleteWithObject(2118, 3926, 0);
		deleteWithObject(2122, 3926, 0);
		deleteWithObject(2117, 3927, 0);
		deleteWithObject(2120, 3928, 0);
		deleteWithObject(2116, 3929, 0);
		deleteWithObject(2115, 3930, 0);
		deleteWithObject(2114, 3930, 0);
		setClipToZero(2543, 3171, 0);
		setClipToZero(2543, 3170, 0);
		setClipToZero(2543, 3169, 0);
		setClipToZero(2543, 3168, 0);
		setClipToZero(2542, 3171, 0);
		setClipToZero(2542, 3170, 0);
		setClipToZero(2542, 3169, 0);
		setClipToZero(2542, 3168, 0);
		setClipToZero(2541, 3171, 0);
		setClipToZero(2541, 3170, 0);
		setClipToZero(2541, 3169, 0);
		setClipToZero(2541, 3168, 0);
		setClipToZero(2540, 3171, 0);
		setClipToZero(2540, 3170, 0);
		setClipToZero(2540, 3169, 0);
		setClipToZero(2540, 3168, 0);
		setClipToZero(2528, 3163, 0);
		setClipToZero(3092, 3480, 0);
		setClipToZero(3092, 3480, 0);
		setClipToZero(3080, 3506, 0);
		setClipToZero(3080, 3507, 0);
		
		
		//wilderness gates
		setClipToZero(3225, 3903, 0);
		setClipToZero(3225, 3904, 0);
		setClipToZero(2947, 3904, 0);
		setClipToZero(2948, 3904, 0);
		
		
		/*
		 * godwars rocks to sara
		 */
		//spawnWithObject(26372, 2917, 3500, 0, 10, 2);
		//spawnWithObject(0, 2914, 3500, 0, 10, 2);
		
		
		/* Wilderness Resource Arena */
		spawnWithObject(14175, 3195, 3942, 0, 10, 3);
		spawnWithObject(14175, 3194, 3943, 0, 10, 3);
		spawnWithObject(14175, 3175, 3937, 0, 10, 3);
		spawnWithObject(14175, 3175, 3943, 0, 10, 3);
		
		/* Blood crafting */
		spawnWithObject(4090, 2792, 3322, 0, 10, 0);//Altar
	
		/* Crafting */
		spawnWithObject(4309, 2751, 3446, 0, 10, 3);//Spinning wheel
		spawnWithObject(11601, 2751, 3449, 0, 10, 2);//Pottery
		
		//Puro Puro
		
		
		/** ores at dwarven mining */
		spawnWithObject(7494, 3051, 9765, 0, 10, 3); // Runite rocks 
		spawnWithObject(7494, 3052, 9766, 0, 10, 3);
		spawnWithObject(7487, 3048, 9787, 0, 10, 0);
		spawnWithObject(7487, 3049, 9786, 0, 10, 0);
		spawnWithObject(7487, 3047, 9788, 0, 10, 0);
		spawnWithObject(7487, 3047, 9790, 0, 10, 0);
		spawnWithObject(7495, 3048, 9783, 0, 10, 0);
		spawnWithObject(7495, 3048, 9782, 0, 10, 0);
		spawnWithObject(7495, 3049, 9781, 0, 10, 0);
		spawnWithObject(6084, 3044, 9776, 0, 10, 0);//bank booth
		spawnWithObject(6084, 3046, 9776, 0, 10, 0);//bank booth
		spawnWithObject(10, 1668, 10091, 0, 10, 0);//ladder blocking way to skotizo
		spawnWithObject(10, 1689, 10091, 0, 10, 0);//ladder blocking way to skotizo
		
		/** Smelting furnace */
		spawnWithObject(2030, 3191, 3425, 0, 10, 0);
		spawnWithObject(11833, 2437, 5166, 0, 10, 2);//jad cave entrance
		spawnWithObject(20843, 3002, 3932, 0, 10, 0);//agility portal home
		//spawnWithObject(20843, 3243, 10145, 0, 10, 0);//rev portal home
		setClipToZero(3202, 3856, 0);
		setClipToZero(3202, 3855, 0);
		setClipToZero(3202, 3854, 0);
		setClipToZero(3201, 3856, 0);
		setClipToZero(3201, 3855, 0);
		setClipToZero(3202, 3854, 0);
		setClipToZero(2923, 9803, 0);
		setClipToZero(2924, 9803, 0);
		setClipToZero(2611, 3393, 0);
		setClipToZero(2611, 3394, 0);
		setClipToZero(2611, 3399, 0);
		setClipToZero(2611, 3398, 0);
		
		//barrows
		spawnWithObject(20672, 3578, 9703, 3, 10, 0);//veracs
		spawnWithObject(20772, 3556, 3297, 0, 10, 0);
		spawnWithObject(20668, 3557, 9718, 3, 10, 0);//dh
		spawnWithObject(20720, 3575, 3297, 0, 10, 0);
		spawnWithObject(20722, 3576, 3281, 0, 10, 0);//guthans
		spawnWithObject(20699, 3534, 9705, 3, 10, 0);
		spawnWithObject(20770, 3564, 3288, 0, 10, 0);//ahrims
		spawnWithObject(20667, 3558, 9703, 3, 10, 0);
		spawnWithObject(20721, 3553, 3281, 0, 10, 0);//torags
		spawnWithObject(20671, 3565, 9683, 3, 10, 0);
		spawnWithObject(20771, 3565, 3273, 0, 10, 0);//karils
		spawnWithObject(20670, 3546, 9685, 3, 10, 0);
		spawnWithObject(20723, 3551, 9695, 0, 10, 2);//chest
		
		
		
		/** Weapon Game **/
		deleteWithObject(1863, 5328, 0);
		deleteWithObject(1863, 5326, 0);
		deleteWithObject(1863, 5323, 0);
		deleteWithObject(1862, 5327, 0);
		deleteWithObject(1862, 5326, 0);
		deleteWithObject(1862, 5325, 0);
		deleteWithObject(1865, 5325, 0);
		deleteWithObject(1863, 5321, 0);
		deleteWithObject(1865, 5321, 0);
		deleteWithObject(1865, 5323, 0);
		deleteWithObject(1863, 5319, 0);
		deleteWithObject(1862, 5319, 0);
		deleteWithObject(1863, 5317, 0);
		deleteWithObject(1865, 5319, 0);
		deleteWithObject(1862, 5321, 0);
		deleteWithObject(1862, 5323, 0);
		spawnWithObject(1, 1866, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(1, 1865, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(11005, 1864, 5323, 0, 10, 1);//Barrier	
		spawnWithObject(11005, 1863, 5323, 0, 10, 1);//Barrier	
		spawnWithObject(1, 1862, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(1, 1861, 5323, 0, 10, 0);//Barrier	
		spawnWithObject(27254, 1861, 5330, 0, 10, 0);//Barrier	
		spawnWithObject(27254, 1862, 5330, 0, 10, 0);//Barrier	
		spawnWithObject(27254, 1863, 5330, 0, 10, 0);//Barrier	
		spawnWithObject(27254, 1864, 5330, 0, 10, 0);//Barrier
		spawnWithObject(27254, 1865, 5330, 0, 10, 0);//Barrier
		spawnWithObject(27254, 1866, 5330, 0, 10, 0);//Barrier
		// 29749 barrier at home 
		
		/** Duel Arena */
		spawnWithObject(409, 3366, 3271, 0, 10, 10);//Altar	
		spawnWithObject(6552, 3370, 3271, 0, 10, 10);//Ancient Altar
		
		/* Crafting Area */
		spawnWithObject(27254, 2748, 3451, 0, 10, 0);// Banks
		
		/** Farming Areas */
		spawnWithObject(27254, 2804, 3463, 0, 10, 1);// Catherby Banks
		spawnWithObject(27254, 3599, 3522, 0, 10, 0);// Banks
		spawnWithObject(27254, 3056, 3311, 0, 10, 0);// Banks
		spawnWithObject(27254, 2662, 3375, 0, 10, 0);// Banks
		
				//woodcutting
				spawnWithObject(6084, 1591, 3475, 0, 10, 0);//bank booth
				spawnWithObject(29668, 1572, 3492, 2, 10, 3);//redwood
				spawnWithObject(29670, 1572, 3493, 2, 10, 3);//redwood
				
				//fishing
				spawnWithObject(6084, 1841, 3788, 0, 10, 0);//bank booth
				spawnWithObject(6084, 1842, 3788, 0, 10, 0);//bank booth
				spawnWithObject(6084, 1843, 3788, 0, 10, 0);//bank booth
				
				/* Fishing tele range */
		        spawnWithObject(26181, 1837, 3783, 0, 10, 0);//Range

		/** Mining banks */
		spawnWithObject(27254, 3047, 9765, 0, 10, 0);
		spawnWithObject(27254, 3045, 9765, 0, 10, 0);
		spawnWithObject(27254, 3044, 9776, 0, 10, 0);
		spawnWithObject(27254, 3045, 9776, 0, 10, 0);
		spawnWithObject(27254, 3046, 9776, 0, 10, 0);
		spawnWithObject(27254, 2930, 4821, 0, 10, 0);// Essences
		
		/** Shilo gem mining **/
		deleteWithObject(2825, 3001, 0);
		deleteWithObject(2825, 3003, 0);
		deleteWithObject(2823, 3002, 0);
		deleteWithObject(2823, 2999, 0);
		deleteWithObject(2821, 3000, 0);
		deleteWithObject(2821, 2998, 0);
		deleteWithObject(2820, 2998, 0);
		spawnWithObject(27886, 2826, 3001, 0, 10, 0);
		spawnWithObject(27886, 2823, 2997, 0, 10, 0);
		spawnWithObject(27886, 2820, 2995, 0, 10, 0);
		
		/** Deleting Objects */
		deleteWithObject(3079, 3501, 0);//Home gate
		deleteWithObject(3080, 3501, 0);//Home gate
		deleteWithObject(3445, 3554, 2);//Slayer tower door
		spawnWithObject(14897, 2843, 4833, 0, 10, 1);//air altar
		
		/** Motherlode */		
		deleteWithObject(3755, 5674, 0);
		spawnWithObject(26661, 3742, 5687, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3735, 5687, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3734, 5687, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3733, 5687, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3749, 5685, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3753, 5686, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3754, 5686, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3762, 5685, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3763, 5685, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3719, 5654, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3720, 5654, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3715, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3716, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3721, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3722, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3718, 5633, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3719, 5633, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3727, 5638, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3734, 5637, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3735, 5637, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3744, 5635, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3750, 5636, 0, 0, 1);// Ore vein
		spawnWithObject(26663, 3751, 5636, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3752, 5636, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3762, 5639, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3767, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26663, 3768, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3769, 5634, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3771, 5635, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3772, 5635, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3767, 5657, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3763, 5656, 0, 0, 1);// Ore vein
		spawnWithObject(26663, 3764, 5656, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3765, 5656, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3759, 5676, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3761, 5675, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3762, 5675, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3764, 5677, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3765, 5677, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3759, 5682, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3761, 5681, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3748, 5682, 0, 0, 1);// Ore vein
		spawnWithObject(26663, 3749, 5682, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3750, 5682, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3745, 5684, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3746, 5684, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3741, 5683, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3742, 5683, 0, 0, 1);// Ore vein
		spawnWithObject(26664, 3737, 5682, 0, 0, 1);// Ore vein
		spawnWithObject(26662, 3738, 5682, 0, 0, 1);// Ore vein
		spawnWithObject(26661, 3735, 5684, 0, 0, 1);// Ore vein
		
		spawnWithObject(26662, 3735, 5692, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3736, 5692, 0, 0, 3);// Ore vein
		spawnWithObject(26661, 3740, 5692, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3742, 5692, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3743, 5692, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3748, 5691, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3749, 5691, 0, 0, 3);// Ore vein
		spawnWithObject(26661, 3759, 5691, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3763, 5691, 0, 0, 3);// Ore vein
		spawnWithObject(26663, 3764, 5691, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3765, 5691, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3716, 5659, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3717, 5659, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3719, 5639, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3720, 5639, 0, 0, 3);// Ore vein
		spawnWithObject(26661, 3730, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3741, 5641, 0, 0, 3);// Ore vein
		spawnWithObject(26663, 3742, 5641, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3743, 5641, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3749, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26663, 3750, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3751, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26661, 3761, 5644, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3770, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26663, 3771, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3772, 5643, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3759, 5674, 0, 0, 3);// Ore vein
		spawnWithObject(26663, 3760, 5674, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3761, 5674, 0, 0, 3);// Ore vein
		spawnWithObject(26661, 3755, 5677, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3760, 5680, 0, 0, 3);// Ore vein
		spawnWithObject(26663, 3761, 5680, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3762, 5680, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3756, 5684, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3757, 5684, 0, 0, 3);// Ore vein
		spawnWithObject(26662, 3752, 5684, 0, 0, 3);// Ore vein
		spawnWithObject(26664, 3753, 5684, 0, 0, 3);// Ore vein
		
		spawnWithObject(26662, 3770, 5686, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3770, 5685, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3770, 5682, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3773, 5677, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3773, 5676, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3769, 5672, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3769, 5671, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3771, 5667, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3773, 5664, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3771, 5661, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3771, 5660, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3773, 5656, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3770, 5647, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3770, 5646, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3774, 5639, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3774, 5638, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3774, 5637, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3718, 5641, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3718, 5640, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3718, 5653, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3718, 5652, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3718, 5651, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3723, 5659, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3722, 5664, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3722, 5663, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3722, 5662, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3723, 5671, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3724, 5676, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3724, 5675, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3724, 5674, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3728, 5659, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3728, 5658, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3728, 5664, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3728, 5663, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3727, 5672, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3727, 5671, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3727, 5670, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3727, 5668, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3727, 5667, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3727, 5666, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3728, 5675, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3728, 5674, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3730, 5679, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3730, 5678, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3730, 5677, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3763, 5663, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3763, 5662, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3764, 5665, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3762, 5673, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3762, 5672, 0, 0, 0);// Ore vein
		spawnWithObject(26663, 3762, 5671, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3762, 5670, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3762, 5683, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3762, 5682, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3743, 5684, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3754, 5683, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3754, 5682, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3753, 5680, 0, 0, 0);// Ore vein
		spawnWithObject(26661, 3754, 5678, 0, 0, 0);// Ore vein
		spawnWithObject(26662, 3758, 5681, 0, 0, 0);// Ore vein
		spawnWithObject(26664, 3758, 5680, 0, 0, 0);// Ore vein
		
		spawnWithObject(26664, 3765, 5682, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3765, 5681, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3765, 5680, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3767, 5677, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3767, 5676, 0, 0, 2);// Ore vein
		spawnWithObject(26661, 3764, 5672, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3765, 5667, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3765, 5668, 0, 0, 2);// Ore vein
		spawnWithObject(26661, 3764, 5660, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3765, 5636, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3765, 5637, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3765, 5638, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3721, 5640, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3721, 5641, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3721, 5642, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3713, 5638, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3713, 5639, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3713, 5640, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3715, 5645, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3715, 5646, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3715, 5647, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3715, 5651, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3715, 5652, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3715, 5653, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3721, 5652, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3721, 5653, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3714, 5655, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3714, 5656, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3714, 5657, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3718, 5660, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3718, 5661, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3718, 5662, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3717, 5664, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3717, 5665, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3717, 5666, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3717, 5670, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3717, 5671, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3719, 5681, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3719, 5682, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3720, 5685, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3720, 5686, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3720, 5687, 0, 0, 2);// Ore vein
		spawnWithObject(26661, 3726, 5681, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3725, 5674, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3725, 5675, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3724, 5669, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3724, 5670, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3723, 5662, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3723, 5663, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3723, 5664, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3723, 5666, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3723, 5667, 0, 0, 2);// Ore vein
		spawnWithObject(26661, 3724, 5660, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3762, 5657, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3762, 5658, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3763, 5681, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3763, 5682, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3763, 5683, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3755, 5681, 0, 0, 2);// Ore vein
		spawnWithObject(26663, 3755, 5682, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3755, 5683, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3756, 5678, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3756, 5679, 0, 0, 2);// Ore vein
		spawnWithObject(26662, 3751, 5680, 0, 0, 2);// Ore vein
		spawnWithObject(26664, 3751, 5681, 0, 0, 2);// Ore vein
		spawnWithObject(26661, 3747, 5683, 0, 0, 2);// Ore vein
		spawnWithObject(26661, 3734, 5685, 0, 0, 2);// Ore vein
		
		/** New Home */
		
		deleteAndRemoveClip(3080, 3506, 0);
		deleteAndRemoveClip(3080, 3507, 0);
		
		
		deleteWithObject(3284, 3510, 0);		
		deleteWithObject(3286, 3497, 0);
		deleteWithObject(3286, 3498, 0);
		deleteWithObject(3286, 3500, 0);
		deleteWithObject(3288, 3502, 0);		
		deleteWithObject(3288, 3497, 0);	
		deleteWithObject(3286, 3502, 0);
		deleteWithObject(3287, 3498, 0);
		deleteWithObject(3287, 3500, 0);
		deleteWithObject(3284, 3510, 0);
		deleteWithObject(3282, 3497, 0);
		deleteWithObject(3283, 3497, 0);
		deleteWithObject(3283, 3500, 0);
		deleteWithObject(3283, 3500, 0);
		deleteWithObject(3277, 3498, 0);
		deleteWithObject(3278, 3497, 0);
		deleteWithObject(3277, 3497, 0);
		deleteWithObject(3278, 3500, 0);
		deleteWithObject(3277, 3500, 0);
		deleteWithObject(3282, 3499, 0);
		deleteWithObject(3277, 3493, 0);
		deleteWithObject(3279, 3493, 0);
		deleteWithObject(3276, 3493, 0);
		deleteWithObject(3276, 3494, 0);
		deleteWithObject(3278, 3492, 0);
		deleteWithObject(3282, 3493, 0);
		deleteWithObject(3282, 3495, 0);
		deleteWithObject(3283, 3495, 0);
		deleteWithObject(3285, 3493, 0);
		deleteWithObject(3284, 3503, 0);
		deleteWithObject(3285, 3503, 0);
		deleteWithObject(3286, 3503, 0);
		deleteWithObject(3286, 3504, 0);
		deleteWithObject(3287, 3504, 0);
		deleteWithObject(3288, 3504, 0);
		deleteWithObject(3284, 3504, 0);
		deleteWithObject(3276, 3503, 0);
		deleteWithObject(3277, 3503, 0);
		deleteWithObject(3278, 3503, 0);		
		deleteWithObject(3282, 3498, 0);		
		deleteWithObject(3284, 3501, 0);	
		deleteWithObject(3284, 3496, 0);
		deleteWithObject(3282, 3500, 0);
		deleteWithObject(3277, 3496, 0);
		
		deleteWithObject(3278, 3504, 0);
		deleteWithObject(3276, 3504, 0);
		deleteWithObject(3275, 3496, 0);
		deleteWithObject(3275, 3497, 0);
		deleteWithObject(3277, 3501, 0);
		
		deleteAndRemoveClip(3286, 3508, 0);
		deleteAndRemoveClip(3286, 3509, 0);
		deleteAndRemoveClip(3286, 3510, 0);
		deleteAndRemoveClip(3275, 3509, 0);
		deleteAndRemoveClip(3275, 3510, 0);
		deleteAndRemoveClip(3276, 3510, 0);
		
		deleteWithObject(3287, 3502, 0);// Banks
		spawnWithObject(27254, 3287, 3501, 0, 10, 1);// Banks
		spawnWithObject(27254, 3287, 3500, 0, 10, 1);// Banks
		spawnWithObject(27254, 3287, 3499, 0, 10, 1);// Banks		
		spawnWithObject(27254, 3287, 3498, 0, 10, 1);// Banks
		spawnWithObject(27254, 3287, 3497, 0, 10, 1);// Banks		
		spawnWithObject(9472, 3286, 3495, 0, 10, 5);//Shop Exchange
		spawnWithObject(8720, 3286, 3494, 0, 10, 2);//Vote		
		deleteWithObject(3282, 3507, 0);//Food stall
		spawnWithObject(4876, 3283, 3507, 0, 10, 5);//General stall
		spawnWithObject(4874, 3284, 3507, 0, 10, 5);//Crafting stall
		spawnWithObject(4877, 3285, 3507, 0, 10, 5);//Magic stall
		spawnWithObject(4878, 3286, 3507, 0, 10, 5);//Scimitar stall
		
		
		spawnWithObject(409, 3275, 3508, 0, 10, 1);//Altar	
		
		spawnWithObject(412, 3277, 3507, 0, 10, 10);//Ancient Altar
		spawnWithObject(6084, 2523, 2914, 0, 10, 1);//bank booth
		
		
		/** Webs */
		deleteWithObject(3105, 3958, 0);
		deleteWithObject(3106, 3958, 0);
		deleteWithObject(3093, 3957, 0);
		deleteWithObject(3095, 3957, 0);
		deleteWithObject(3092, 3957, 0);
		deleteWithObject(3158, 3951, 0);
		deleteWithObject(2543, 4715, 0);
		spawnWithObject(734, 3105, 3958, 0, 10, 3);
		spawnWithObject(734, 3106, 3958, 0, 10, 3);
		spawnWithObject(734, 3158, 3951, 0, 10, 1);
		spawnWithObject(734, 3093, 3957, 0, 10, 0);
		spawnWithObject(734, 3095, 3957, 0, 10, 0);
		deleteWithObject(2543, 4715, 0);
		deleteWithObject(2855, 3546, 0);
		deleteWithObject(2854, 3546, 0);

		/** Clipping */
		setClipToZero(3445, 3554, 2);
		setClipToZero(3119, 9850, 0);
		setClipToZero(3002, 3961, 0);
		setClipToZero(3002, 3960, 0);
		setClipToZero(2539, 4716, 0);
		setClipToZero(3068, 10255, 0);
		setClipToZero(3068, 10256, 0);
		setClipToZero(3068, 10258, 0);
		setClipToZero(3067, 10255, 0);
		setClipToZero(3066, 10256, 0);
		setClipToZero(3426, 3555, 1);
		setClipToZero(3427, 3555, 1);
		setClipToZero(3005, 3953, 0);
		setClipToZero(3005, 3952, 0);
		setClipToZero(2551, 3554, 0);
		setClipToZero(2551, 3555, 0);
		setClipToZero(2833, 3352, 0);
		setClipToZero(2996, 3960, 0);
		
		//old home area flag poles + doorways
		setClipped(1639, 3629, 0);
		setClipped(1640, 3629, 0);
		setClipped(1639, 3630, 0);
		setClipped(1640, 3630, 0);
		setClipped(1639, 3632, 0);
		setClipped(1640, 3632, 0);
		setClipped(1639, 3633, 0);
		setClipped(1640, 3633, 0);
		setClipped(1636, 3639, 0);
		
		//edgeville clipping
		
		/**
		spawnWithObject(1, 3101, 3510, 0, 10, 0);
		spawnWithObject(1, 3101, 3509, 0, 10, 0);
		*/
		
		
		// alter area Edge
		spawnWithObject(1723, 3100, 3509, 0, 0, 3);
		
		
		
		
		deleteAndRemoveClip(3095, 3508, 0);//north of bank
		setClipToZero(3096, 3507, 0); // door way edgeville alter area
		setClipToZero(3095, 3507, 0);//door way edgeville alter area
		setClipToZero(3096, 3511, 0);
		setClipToZero(3094, 3509, 0);
		setClipToZero(3094, 3510, 0);
		setClipToZero(3093, 3510, 0);
		setClipToZero(3093, 3509, 0);
		setClipToZero(3093, 3508, 0);
		setClipToZero(3096, 3506, 0);
		setClipToZero(3095, 3506, 0);
		setClipToZero(3099, 3512, 0);
		setClipToZero(3094, 3508, 0);
		setClipToZero(3098, 3513, 0);
		setClipToZero(3097, 3513, 0);
		setClipToZero(3094, 3513, 0);
		setClipped(3091, 3507, 0);
		setClipped(3100, 3509, 0);
		setClipped(3100, 3510, 0);
		setClipped(3100, 3511, 0);
		setClipToZero(3095, 3498, 0);//bank
		setClipToZero(3096, 3498, 0);
		spawnWithObject(7168, 3095, 3499, 0, 10, 0);
		deleteWithObject(3095, 3498, 0);
		deleteWithObject(3096, 3498, 0);
		spawnWithObject(7168, 3090, 3494, 0, 10, 0);
		deleteWithObject(3080, 3493, 0);//west of bank
		deleteWithObject(3080, 3489, 0);
		
		
		
		
		// Bronze Dragon Area Edgeiville 
		setClipped(3076, 3501, 0);
		setClipped(3077, 3501, 0);
		setClipped(3078, 3501, 0);
		setClipped(3079, 3501, 0);
		setClipped(3080, 3501, 0);
		setClipped(3081, 3501, 0);
		setClipped(3082, 3501, 0);
		setClipped(3076, 3500, 0);
		setClipToZero(3077, 3500, 0);
		setClipToZero(3078, 3500, 0);
		setClipToZero(3079, 3500, 0);
		setClipToZero(3080, 3500, 0);
		setClipToZero(3081, 3500, 0);
		setClipped(3082, 3500, 0);
		setClipped(3076, 3499, 0);
		setClipToZero(3077, 3499, 0);
		setClipToZero(3078, 3499, 0);
		setClipToZero(3079, 3499, 0);
		setClipToZero(3080, 3499, 0);
		setClipToZero(3081, 3499, 0);
		setClipped(3082, 3499, 0);
		setClipped(3076, 3498, 0);
		setClipToZero(3077, 3498, 0);
		setClipToZero(3078, 3498, 0);
		setClipToZero(3079, 3498, 0);
		setClipToZero(3080, 3498, 0);
		setClipToZero(3081, 3498, 0);
		setClipped(3082, 3498, 0);
		setClipped(3076, 3497, 0);
		setClipToZero(3077, 3497, 0);
		setClipToZero(3078, 3497, 0);
		setClipToZero(3079, 3497, 0);
		setClipToZero(3080, 3497, 0);
		setClipToZero(3081, 3497, 0);
		setClipped(3082, 3497, 0);
		setClipped(3076, 3496, 0);
		setClipToZero(3077, 3496, 0);
		setClipToZero(3078, 3496, 0);
		setClipToZero(3079, 3496, 0);
		setClipToZero(3080, 3496, 0);
		setClipToZero(3081, 3496, 0);
		setClipped(3082, 3496, 0);
		setClipped(3076, 3495, 0);
		setClipToZero(3077, 3495, 0);
		setClipToZero(3078, 3495, 0);
		setClipToZero(3079, 3495, 0);
		setClipToZero(3080, 3495, 0);
		setClipToZero(3081, 3495, 0);
		setClipToZero(3082, 3495, 0);
		setClipped(3076, 3494, 0);
		setClipToZero(3077, 3494, 0);
		setClipToZero(3078, 3494, 0);
		setClipToZero(3079, 3494, 0);
		setClipToZero(3080, 3494, 0);
		setClipToZero(3081, 3494, 0);
		setClipToZero(3082, 3494, 0);
		setClipped(3076, 3493, 0);
		setClipToZero(3077, 3493, 0);
		setClipToZero(3078, 3493, 0);
		setClipToZero(3079, 3493, 0);
		setClipToZero(3080, 3493, 0);
		setClipToZero(3081, 3493, 0);
		setClipped(3082, 3493, 0);
		setClipped(3076, 3492, 0);
		setClipToZero(3077, 3492, 0);
		setClipToZero(3078, 3492, 0);
		setClipToZero(3079, 3492, 0);
		setClipToZero(3080, 3492, 0);
		setClipToZero(3081, 3492, 0);
		setClipped(3082, 3492, 0);
		setClipped(3076, 3491, 0);
		setClipToZero(3077, 3491, 0);
		setClipToZero(3078, 3491, 0);
		setClipToZero(3079, 3491, 0);
		setClipToZero(3080, 3491, 0);
		setClipToZero(3081, 3491, 0);
		setClipToZero(3082, 3491, 0);
		setClipped(3076, 3490, 0);
		setClipToZero(3077, 3490, 0);
		setClipToZero(3078, 3490, 0);
		setClipToZero(3079, 3490, 0);
		setClipToZero(3080, 3490, 0);
		setClipToZero(3081, 3490, 0);
		setClipToZero(3082, 3490, 0);
		setClipped(3076, 3489, 0);
		setClipToZero(3077, 3489, 0);
		setClipToZero(3078, 3489, 0);
		setClipToZero(3079, 3489, 0);
		setClipToZero(3080, 3489, 0);
		setClipToZero(3081, 3489, 0);
		setClipped(3082, 3489, 0);
		setClipped(3076, 3488, 0);
		setClipToZero(3077, 3488, 0);
		setClipToZero(3078, 3488, 0);
		setClipToZero(3079, 3488, 0);
		setClipToZero(3080, 3488, 0);
		setClipToZero(3081, 3488, 0);
		setClipped(3082, 3488, 0);
		setClipped(3076, 3487, 0);
		setClipped(3077, 3487, 0);
		setClipped(3078, 3487, 0);
		setClipped(3079, 3487, 0);
		setClipped(3080, 3487, 0);
		setClipped(3081, 3487, 0);
		setClipped(3082, 3487, 0);
		// Bronze Dragon Area End 
		
		
		
		spawnWithObject(1018, 3077, 3513, 0, 10, 1);//general store
		spawnWithObject(1018, 3078, 3513, 0, 10, 1);
		spawnWithObject(1017, 3079, 3513, 0, 10, 1);
		spawnWithObject(1018, 3080, 3513, 0, 10, 1);
		spawnWithObject(1015, 3081, 3513, 0, 10, 1);
		spawnWithObject(1016, 3082, 3513, 0, 10, 1);
		spawnWithObject(1018, 3083, 3513, 0, 10, 1);
		spawnWithObject(1018, 3084, 3512, 0, 10, 0);
		spawnWithObject(1018, 3084, 3509, 0, 10, 0);
		spawnWithObject(1018, 3076, 3512, 0, 10, 0);
		spawnWithObject(1018, 3076, 3511, 0, 10, 0);
		spawnWithObject(1018, 3076, 3510, 0, 10, 0);
		spawnWithObject(1018, 3076, 3509, 0, 10, 0);
		setClipToZero(3077, 3512, 0);//change to setClipToZero
		setClipToZero(3078, 3512, 0);
		setClipToZero(3079, 3512, 0);
		setClipToZero(3080, 3512, 0);
		setClipToZero(3081, 3512, 0);
		setClipToZero(3082, 3512, 0);
		setClipToZero(3083, 3512, 0);
		setClipToZero(3077, 3511, 0);
		setClipToZero(3078, 3511, 0);
		setClipToZero(3079, 3511, 0);
		setClipToZero(3080, 3511, 0);
		setClipToZero(3081, 3511, 0);
		setClipToZero(3082, 3511, 0);
		setClipToZero(3083, 3511, 0);
		setClipToZero(3077, 3510, 0);
		setClipToZero(3078, 3510, 0);
		setClipToZero(3079, 3510, 0);
		setClipToZero(3080, 3510, 0);
		setClipToZero(3081, 3510, 0);
		setClipToZero(3082, 3510, 0);
		setClipToZero(3083, 3510, 0);
		setClipToZero(3077, 3509, 0);
		setClipToZero(3078, 3509, 0);
		setClipToZero(3079, 3509, 0);
		setClipToZero(3080, 3509, 0);
		setClipToZero(3081, 3509, 0);
		setClipToZero(3082, 3509, 0);
		setClipToZero(3083, 3509, 0);
		setClipToZero(3077, 3508, 0);
		setClipToZero(3078, 3508, 0);
		setClipToZero(3079, 3508, 0);
		setClipToZero(3080, 3508, 0);
		setClipToZero(3081, 3508, 0);
		setClipToZero(3082, 3508, 0);
		setClipToZero(3083, 3508, 0);
		setClipToZero(3106, 3500, 0);//forge house
		setClipToZero(3107, 3500, 0);
		setClipToZero(3108, 3500, 0);
		setClipToZero(3106, 3499, 0);
		setClipToZero(3107, 3499, 0);
		setClipToZero(3108, 3499, 0);
		setClipToZero(3109, 3499, 0);
		setClipToZero(3106, 3498, 0);
		setClipToZero(3107, 3498, 0);
		setClipToZero(3108, 3498, 0);
		setClipToZero(3109, 3498, 0);
		setClipToZero(3106, 3497, 0);
		setClipToZero(3107, 3497, 0);
		setClipToZero(3108, 3497, 0);
		setClipToZero(3109, 3497, 0);
		setClipToZero(3107, 3502, 0);
		setClipToZero(3107, 3501, 0);
		setClipToZero(3111, 3515, 0);
		setClipToZero(3111, 3514, 0);
		setClipToZero(3112, 3515, 0);
		setClipToZero(3112, 3514, 0);
		setClipToZero(3091, 3504, 0);
		setClipToZero(3091, 3503, 0);
		setClipToZero(3090, 3504, 0);
		setClipToZero(3090, 3503, 0);
		//spawnWithObject(12351, 3083, 3495, 0, 10, 0);//outside west door
		spawnWithObject(29749, 3083, 3494, 0, 10, 0);
		//spawnWithObject(12351, 3083, 3491, 0, 10, 0);
		spawnWithObject(29749, 3083, 3490, 0, 10, 0);//outside west door end
		setClipped(3097, 3513, 0);
		setClipped(3098, 3513, 0);
		setClipToZero(3100, 3516, 0);
		setClipToZero(3101, 3516, 0);
		setClipToZero(3100, 3517, 0);
		setClipToZero(3101, 3517, 0);
		setClipped(3094, 3513, 0);
		setClipped(3091, 3512, 0);
		setClipped(3091, 3511, 0);
		setClipped(3091, 3510, 0);
		setClipped(3091, 3509, 0);
		setClipped(3091, 3508, 0);
		setClipToZero(3089, 3509, 0);
		setClipToZero(3089, 3510, 0);
		setClipToZero(3089, 3511, 0);
		setClipToZero(3089, 3512, 0);
		setClipToZero(3088, 3512, 0);
		setClipToZero(3088, 3511, 0);
		setClipToZero(3088, 3510, 0);
		setClipToZero(3088, 3509, 0);
		setClipToZero(3096, 3482, 0);
		setClipToZero(3095, 3482, 0);
		setClipToZero(3095, 3480, 0);
		setClipToZero(3096, 3480, 0);
		setClipToZero(3097, 3480, 0);
		setClipToZero(3095, 3479, 0);
		setClipToZero(3096, 3479, 0);
		setClipToZero(3097, 3479, 0);
		setClipToZero(3093, 3476, 0);
		setClipToZero(3092, 3476, 0);
		setClipToZero(3091, 3476, 0);
		setClipToZero(3092, 3477, 0);
		setClipToZero(3091, 3477, 0);
		setClipToZero(3091, 3476, 0);
		setClipToZero(3090, 3476, 0);
		setClipToZero(3089, 3476, 0);
		setClipToZero(3296, 3429, 0);
		setClipToZero(3296, 3428, 0);
		
		
		//great whatever, new home area?
				setClipToZero(1646, 3675, 0);
				setClipToZero(1646, 3676, 0);
				setClipToZero(1646, 3677, 0);
				setClipToZero(1646, 3678, 0);
				setClipToZero(1645, 3675, 0);
				setClipToZero(1645, 3676, 0);
				setClipToZero(1645, 3677, 0);
				setClipToZero(1645, 3678, 0);
				setClipToZero(1644, 3675, 0);
				setClipToZero(1644, 3676, 0);
				setClipToZero(1644, 3677, 0);
				setClipToZero(1644, 3678, 0);
				setClipToZero(1645, 3682, 0);
				setClipToZero(1646, 3682, 0);
				setClipToZero(1647, 3682, 0);
				setClipToZero(1648, 3682, 0);
				setClipToZero(1649, 3682, 0);
				setClipToZero(1650, 3682, 0);
				setClipToZero(1650, 3681, 0);
				setClipToZero(1646, 3683, 0);
				setClipToZero(1647, 3683, 0);
				setClipToZero(1650, 3683, 0);
				setClipToZero(1651, 3683, 0);
				setClipToZero(1651, 3683, 0);
				setClipToZero(1651, 3682, 0);
				setClipToZero(1647, 3683, 0);
				setClipToZero(1641, 3682, 0);
				setClipToZero(1641, 3683, 0);
				setClipToZero(1641, 3684, 0);
				setClipToZero(1640, 3682, 0);
				setClipToZero(1640, 3683, 0);
				setClipToZero(1640, 3684, 0);
				setClipToZero(1648, 3684, 0);
				setClipToZero(1639, 3682, 0);
				setClipToZero(1638, 3682, 0);
				setClipToZero(1637, 3681, 0);
				setClipToZero(1637, 3682, 0);
				setClipToZero(1636, 3681, 0);
				setClipToZero(1636, 3682, 0);
				setClipToZero(1636, 3684, 0);
				setClipToZero(1636, 3685, 0);
				setClipToZero(1635, 3680, 0);
				setClipToZero(1635, 3681, 0);
				setClipToZero(1637, 3685, 0);
				setClipToZero(1647, 3684, 0);
				setClipToZero(1646, 3684, 0);
				setClipToZero(1645, 3684, 0);
				setClipToZero(1651, 3681, 0);
				setClipToZero(1645, 3671, 0);
				setClipToZero(1631, 3681, 0);
				setClipToZero(1633, 3682, 0);
				setClipToZero(1632, 3682, 0);
				setClipToZero(1631, 3682, 0);
				setClipToZero(1633, 3683, 0);
				setClipToZero(1634, 3682, 0);
				setClipToZero(1634, 3681, 0);
				setClipToZero(1638, 3685, 0);
				setClipToZero(1634, 3680, 0);
				setClipToZero(1634, 3681, 0);
				setClipToZero(1634, 3682, 0);
				setClipToZero(1635, 3682, 0);
				setClipToZero(1630, 3681, 0);
				setClipToZero(1630, 3682, 0);
				setClipToZero(1630, 3683, 0);
				setClipToZero(1630, 3684, 0);
				setClipToZero(1633, 3684, 0);
				setClipToZero(1632, 3684, 0);
				setClipToZero(1632, 3683, 0);
				setClipToZero(1631, 3683, 0);
				setClipToZero(1631, 3684, 0);
				setClipToZero(1631, 3685, 0);
				setClipToZero(1631, 3686, 0);
				setClipToZero(1631, 3687, 0);
				setClipToZero(1631, 3688, 0);
				setClipToZero(1631, 3689, 0);
				setClipToZero(1631, 3690, 0);
				setClipToZero(1631, 3691, 0);
				setClipToZero(1630, 3676, 0);
				setClipToZero(1630, 3677, 0);
				setClipToZero(1630, 3668, 0);
				setClipToZero(1630, 3667, 0);
				setClipToZero(1630, 3666, 0);
				setClipToZero(1630, 3665, 0);
				setClipToZero(1630, 3664, 0);
				setClipToZero(1630, 3663, 0);
				setClipToZero(1631, 3668, 0);
				setClipToZero(1631, 3667, 0);
				setClipToZero(1631, 3666, 0);
				setClipToZero(1631, 3665, 0);
				setClipToZero(1631, 3664, 0);
				setClipToZero(1631, 3663, 0);
				setClipToZero(1632, 3668, 0);
				setClipToZero(1632, 3667, 0);
				setClipToZero(1632, 3666, 0);
				setClipToZero(1632, 3665, 0);
				setClipToZero(1632, 3664, 0);
				setClipToZero(1632, 3663, 0);
				setClipToZero(1633, 3668, 0);
				setClipToZero(1633, 3667, 0);
				setClipToZero(1633, 3666, 0);
				setClipToZero(1633, 3665, 0);
				setClipToZero(1633, 3664, 0);
				setClipToZero(1633, 3663, 0);
				setClipToZero(1634, 3664, 0);
				setClipToZero(1634, 3663, 0);
				setClipToZero(1635, 3666, 0);
				setClipToZero(1635, 3665, 0);
				setClipToZero(1635, 3664, 0);
				setClipToZero(1635, 3663, 0);
				setClipToZero(1636, 3666, 0);
				setClipToZero(1636, 3665, 0);
				setClipToZero(1636, 3664, 0);
				setClipToZero(1636, 3663, 0);
				setClipToZero(1637, 3664, 0);
				setClipToZero(1637, 3663, 0);
				setClipToZero(1637, 3662, 0);
				setClipToZero(1637, 3661, 0);
				setClipToZero(1636, 3661, 0);
				setClipToZero(1637, 3663, 0);
				setClipToZero(1637, 3662, 0);
				setClipToZero(1637, 3663, 0);
				setClipToZero(1637, 3662, 0);
				setClipToZero(1638, 3664, 0);
				setClipToZero(1638, 3663, 0);
				setClipToZero(1638, 3662, 0);
				setClipToZero(1637, 3664, 0);
				setClipToZero(1637, 3663, 0);
				setClipToZero(1639, 3664, 0);
				setClipToZero(1640, 3664, 0);
				setClipToZero(1641, 3664, 0);
				setClipToZero(1641, 3663, 0);
				setClipToZero(1644, 3668, 0);
				setClipToZero(1644, 3669, 0);
				setClipToZero(1644, 3670, 0);
				setClipToZero(1644, 3671, 0);
				setClipToZero(1645, 3668, 0);
				setClipToZero(1646, 3668, 0);
				setClipToZero(1650, 3665, 0);
				setClipToZero(1650, 3666, 0);
				setClipped(1650, 3667, 0);
				setClipped(1650, 3666, 0);
				setClipToZero(1651, 3665, 0);
				setClipToZero(1651, 3664, 0);
				setClipToZero(1651, 3663, 0);
				setClipToZero(1651, 3662, 0);
				setClipToZero(1650, 3664, 0);
				setClipToZero(1650, 3663, 0);
				setClipToZero(1650, 3662, 0);
				setClipToZero(1649, 3664, 0);
				setClipToZero(1649, 3663, 0);
				setClipToZero(1649, 3662, 0);
				setClipToZero(1648, 3664, 0);
				setClipToZero(1648, 3663, 0);
				setClipToZero(1648, 3662, 0);
				setClipToZero(1647, 3664, 0);
				setClipToZero(1647, 3663, 0);
				setClipToZero(1647, 3662, 0);
				setClipToZero(1646, 3664, 0);
				setClipToZero(1646, 3663, 0);
				setClipToZero(1646, 3662, 0);
				setClipToZero(1645, 3664, 0);
				setClipToZero(1645, 3663, 0);
				setClipToZero(1645, 3662, 0);
				setClipToZero(1633, 3663, 0);
				setClipToZero(1632, 3663, 0);
				setClipToZero(1631, 3663, 0);
				setClipToZero(1630, 3663, 0);
				setClipToZero(1629, 3663, 0);
				setClipToZero(1627, 3663, 0);
				setClipToZero(1628, 3663, 0);
				setClipToZero(1629, 3664, 0);
				setClipToZero(1628, 3664, 0);
				setClipToZero(1629, 3665, 0);
				setClipToZero(1628, 3665, 0);
				setClipToZero(1629, 3666, 0);
				setClipToZero(1628, 3666, 0);
				setClipToZero(1627, 3666, 0);
				setClipToZero(1626, 3666, 0);
				setClipToZero(1625, 3666, 0);
				setClipToZero(1625, 3665, 0);
				setClipToZero(1625, 3664, 0);
				setClipToZero(1625, 3663, 0);
				setClipToZero(1625, 3662, 0);
				setClipToZero(1626, 3662, 0);
				setClipToZero(1627, 3662, 0);
				setClipToZero(1628, 3662, 0);
				setClipToZero(1629, 3662, 0);
				setClipToZero(1630, 3662, 0);
				setClipToZero(1628, 3661, 0);
				setClipToZero(1628, 3660, 0);
				setClipToZero(1628, 3659, 0);
				setClipToZero(1628, 3658, 0);
				setClipToZero(1628, 3657, 0);
				setClipToZero(1628, 3656, 0);
				setClipToZero(1628, 3655, 0);
				setClipToZero(1629, 3661, 0);
				setClipToZero(1629, 3660, 0);
				setClipToZero(1629, 3659, 0);
				setClipToZero(1629, 3658, 0);
				setClipToZero(1629, 3657, 0);
				setClipToZero(1629, 3656, 0);
				setClipToZero(1629, 3655, 0);
				setClipToZero(1628, 3661, 0);
				setClipToZero(1628, 3660, 0);
				setClipToZero(1628, 3659, 0);
				setClipToZero(1628, 3658, 0);
				setClipToZero(1628, 3657, 0);
				setClipToZero(1628, 3656, 0);
				setClipToZero(1629, 3684, 0);
				setClipToZero(1629, 3683, 0);
				setClipToZero(1629, 3682, 0);
				setClipToZero(1629, 3681, 0);
				setClipToZero(1629, 3680, 0);
				setClipToZero(1629, 3679, 0);
				setClipToZero(1629, 3678, 0);
				setClipToZero(1629, 3677, 0);
				setClipToZero(1629, 3676, 0);
				setClipToZero(1629, 3675, 0);
				setClipToZero(1629, 3674, 0);
				setClipToZero(1629, 3673, 0);
				setClipToZero(1629, 3672, 0);
				setClipToZero(1629, 3671, 0);
				setClipToZero(1629, 3670, 0);
				setClipToZero(1629, 3669, 0);
				setClipToZero(1629, 3668, 0);
				setClipToZero(1629, 3667, 0);
				setClipToZero(1628, 3684, 0);
				setClipToZero(1628, 3683, 0);
				setClipToZero(1628, 3682, 0);
				setClipToZero(1628, 3681, 0);
				setClipToZero(1628, 3680, 0);
				setClipToZero(1628, 3679, 0);
				setClipToZero(1628, 3678, 0);
				setClipToZero(1628, 3677, 0);
				setClipToZero(1628, 3676, 0);
				setClipToZero(1628, 3675, 0);
				setClipToZero(1628, 3674, 0);
				setClipToZero(1628, 3673, 0);
				setClipToZero(1628, 3672, 0);
				setClipToZero(1628, 3671, 0);
				setClipToZero(1628, 3670, 0);
				setClipToZero(1628, 3669, 0);
				setClipToZero(1628, 3668, 0);
				setClipToZero(1628, 3667, 0);
				setClipToZero(1627, 3667, 0);
				setClipToZero(1626, 3667, 0);
				setClipToZero(1625, 3667, 0);
				setClipToZero(1627, 3671, 0);
				setClipToZero(1627, 3672, 0);
				setClipToZero(1626, 3672, 0);
				setClipToZero(1625, 3672, 0);
				setClipToZero(1624, 3672, 0);
				setClipToZero(1627, 3673, 0);
				setClipToZero(1626, 3673, 0);
				setClipToZero(1625, 3673, 0);
				setClipToZero(1624, 3673, 0);
				setClipToZero(1627, 3674, 0);
				setClipToZero(1626, 3674, 0);
				setClipToZero(1625, 3674, 0);
				setClipToZero(1624, 3674, 0);
				setClipToZero(1627, 3676, 0);
				setClipToZero(1627, 3679, 0);
				setClipToZero(1626, 3679, 0);
				setClipToZero(1625, 3679, 0);
				setClipToZero(1627, 3680, 0);
				setClipToZero(1626, 3680, 0);
				setClipToZero(1625, 3680, 0);
				setClipToZero(1627, 3683, 0);
				setClipToZero(1626, 3683, 0);
				setClipToZero(1632, 3677, 0);
				setClipToZero(1632, 3676, 0);
				setClipToZero(1632, 3675, 0);
				setClipToZero(1632, 3674, 0);
				setClipToZero(1632, 3673, 0);
				setClipToZero(1632, 3672, 0);
				setClipToZero(1632, 3671, 0);
				setClipToZero(1632, 3669, 0);
				setClipToZero(1632, 3668, 0);
				setClipToZero(1633, 3677, 0);
				setClipToZero(1633, 3676, 0);
				setClipToZero(1633, 3675, 0);
				setClipToZero(1633, 3674, 0);
				setClipToZero(1633, 3673, 0);
				setClipToZero(1633, 3672, 0);
				setClipToZero(1633, 3671, 0);
				setClipToZero(1633, 3669, 0);
				setClipToZero(1633, 3668, 0);
				setClipToZero(1639, 3677, 0);
				setClipToZero(1639, 3676, 0);
				setClipToZero(1639, 3675, 0);
				setClipToZero(1639, 3674, 0);
				setClipToZero(1639, 3673, 0);
				setClipToZero(1639, 3672, 0);
				setClipToZero(1639, 3671, 0);
				setClipToZero(1639, 3669, 0);
				setClipToZero(1639, 3668, 0);
				setClipToZero(1640, 3677, 0);
				setClipToZero(1640, 3676, 0);
				setClipToZero(1640, 3675, 0);
				setClipToZero(1640, 3674, 0);
				setClipToZero(1640, 3673, 0);
				setClipToZero(1640, 3672, 0);
				setClipToZero(1640, 3671, 0);
				setClipToZero(1640, 3669, 0);
				setClipToZero(1640, 3668, 0);
				setClipToZero(1638, 3677, 0);
				setClipToZero(1638, 3676, 0);
				setClipToZero(1638, 3675, 0);
				setClipToZero(1634, 3677, 0);
				setClipToZero(1634, 3676, 0);
				setClipToZero(1634, 3675, 0);
				setClipToZero(1635, 3677, 0);
				setClipToZero(1635, 3676, 0);
				setClipToZero(1636, 3677, 0);
				setClipToZero(1636, 3676, 0);
				setClipToZero(1637, 3677, 0);
				setClipToZero(1637, 3676, 0);
				setClipToZero(1634, 3669, 0);
				setClipToZero(1634, 3670, 0);
				setClipToZero(1634, 3669, 0);
				setClipToZero(1638, 3669, 0);
				setClipToZero(1638, 3670, 0);
				setClipToZero(1638, 3671, 0);
				setClipToZero(1637, 3669, 0);
				setClipToZero(1637, 3670, 0);
				setClipToZero(1636, 3669, 0);
				setClipToZero(1636, 3670, 0);
				setClipToZero(1635, 3669, 0);
				setClipToZero(1635, 3670, 0);
				setClipped(1619, 3689, 0);
				setClipped(1618, 3690, 0);
				setClipped(1617, 3692, 0);
				setClipped(1616, 3693, 0);
				setClipped(1615, 3694, 0);
				setClipped(1614, 3693, 0);
				setClipped(1613, 3692, 0);
				setClipToZero(1616, 3689, 0);
				setClipped(1616, 3688, 0);
				setClipToZero(1615, 3687, 0);
				setClipToZero(1615, 3686, 0);
				setClipToZero(1614, 3686, 0);
				setClipToZero(1613, 3685, 0);
				setClipToZero(1613, 3684, 0);
				setClipToZero(1613, 3684, 0);
				deleteWithObject(1615, 3684, 0);
				setClipped(1618, 3683, 0);
				setClipped(1617, 3683, 0);
				setClipped(1615, 3680, 0);
				setClipped(1616, 3680, 0);
				setClipped(1617, 3680, 0);
				setClipped(1615, 3681, 0);
				setClipped(1616, 3681, 0);
				setClipped(1617, 3681, 0);
				setClipped(1614, 3683, 0);
				setClipped(1615, 3683, 0);
				setClipped(1616, 3683, 0);
				setClipped(1615, 3682, 0);
				setClipped(1614, 3682, 0);
				setClipped(1617, 3690, 0);
				setClipped(1617, 3682, 0);
				setClipped(1617, 3679, 0);
				setClipToZero(1619, 3681, 0);
				setClipToZero(1619, 3680, 0);
				setClipToZero(1620, 3680, 0);
				setClipToZero(1619, 3679, 0);
				setClipToZero(1620, 3679, 0);
				setClipToZero(1621, 3679, 0);
				setClipToZero(1619, 3678, 0);
				setClipToZero(1620, 3678, 0);
				setClipToZero(1621, 3678, 0);
				setClipToZero(1619, 3677, 0);
				setClipToZero(1620, 3677, 0);
				setClipToZero(1619, 3676, 0);
				setClipToZero(1620, 3676, 0);
				setClipToZero(1621, 3676, 0);
				setClipped(1616, 3691, 0);
				setClipped(1615, 3691, 0);
				setClipped(1614, 3691, 0);
				setClipped(1616, 3690, 0);
				setClipped(1611, 3684, 0);
				setClipped(1610, 3684, 0);
				setClipped(1609, 3684, 0);
				setClipped(1608, 3684, 0);
				setClipped(1607, 3684, 0);
				setClipped(1606, 3684, 0);
				setClipped(1605, 3684, 0);
				setClipped(1604, 3684, 0);
				setClipped(1603, 3684, 0);
				setClipped(1602, 3684, 0);
				setClipped(1601, 3684, 0);
				setClipped(1600, 3684, 0);
				setClipped(1599, 3684, 0);
				setClipToZero(1607, 3683, 0);
				setClipToZero(1606, 3683, 0);
				setClipped(1610, 3683, 0);
				setClipToZero(1614, 3679, 0);
				setClipToZero(1614, 3678, 0);
				setClipToZero(1614, 3677, 0);
				setClipToZero(1614, 3676, 0);
				deleteWithObject(1615, 3670, 0);
				setClipToZero(1620, 3675, 0);
				setClipToZero(1620, 3674, 0);
				setClipToZero(1620, 3673, 0);
				setClipToZero(1620, 3672, 0);
				setClipToZero(1620, 3671, 0);
				setClipToZero(1620, 3670, 0);
				setClipToZero(1620, 3669, 0);
				setClipToZero(1620, 3668, 0);
				setClipToZero(1619, 3675, 0);
				setClipToZero(1619, 3674, 0);
				setClipToZero(1619, 3673, 0);
				setClipToZero(1619, 3672, 0);
				setClipToZero(1619, 3671, 0);
				setClipToZero(1619, 3670, 0);
				setClipToZero(1619, 3669, 0);
				setClipToZero(1619, 3668, 0);
				setClipToZero(1618, 3675, 0);
				setClipToZero(1618, 3674, 0);
				setClipToZero(1618, 3673, 0);
				setClipToZero(1618, 3672, 0);
				setClipToZero(1618, 3671, 0);
				setClipToZero(1618, 3670, 0);
				setClipToZero(1618, 3669, 0);
				setClipToZero(1618, 3668, 0);
				setClipToZero(1617, 3675, 0);
				setClipToZero(1617, 3674, 0);
				setClipToZero(1617, 3673, 0);
				setClipToZero(1617, 3672, 0);
				setClipToZero(1617, 3671, 0);
				setClipToZero(1617, 3670, 0);
				setClipToZero(1617, 3669, 0);
				setClipToZero(1617, 3668, 0);
				setClipToZero(1616, 3675, 0);
				setClipToZero(1616, 3674, 0);
				setClipToZero(1616, 3673, 0);
				setClipToZero(1616, 3672, 0);
				setClipToZero(1616, 3671, 0);
				setClipToZero(1616, 3670, 0);
				setClipToZero(1616, 3669, 0);
				setClipToZero(1616, 3668, 0);
				setClipToZero(1615, 3675, 0);
				setClipToZero(1615, 3674, 0);
				setClipToZero(1615, 3673, 0);
				setClipToZero(1615, 3672, 0);
				setClipToZero(1615, 3671, 0);
				setClipToZero(1615, 3670, 0);
				setClipToZero(1615, 3669, 0);
				setClipToZero(1615, 3668, 0);
				setClipToZero(1614, 3675, 0);
				setClipToZero(1614, 3674, 0);
				setClipToZero(1614, 3673, 0);
				setClipToZero(1614, 3672, 0);
				setClipToZero(1614, 3671, 0);
				setClipToZero(1614, 3670, 0);
				setClipToZero(1614, 3669, 0);
				setClipToZero(1614, 3668, 0);
				setClipToZero(1613, 3676, 0);
				setClipToZero(1613, 3677, 0);
				setClipToZero(1613, 3675, 0);
				setClipToZero(1613, 3674, 0);
				setClipToZero(1613, 3673, 0);
				setClipToZero(1613, 3672, 0);
				setClipToZero(1613, 3671, 0);
				setClipToZero(1613, 3670, 0);
				setClipToZero(1613, 3669, 0);
				setClipToZero(1613, 3668, 0);
				setClipToZero(1612, 3676, 0);
				setClipToZero(1612, 3677, 0);
				setClipToZero(1612, 3675, 0);
				setClipToZero(1612, 3674, 0);
				setClipToZero(1612, 3673, 0);
				setClipToZero(1612, 3672, 0);
				setClipToZero(1612, 3671, 0);
				setClipToZero(1612, 3670, 0);
				setClipToZero(1612, 3669, 0);
				setClipToZero(1612, 3668, 0);
				setClipToZero(1616, 3676, 0);
				setClipToZero(1615, 3676, 0);
				setClipped(1611, 3675, 0);
				setClipped(1611, 3677, 0);
				setClipToZero(1636, 3670, 0);
				setClipToZero(1639, 3663, 0);
				setClipToZero(1640, 3663, 0);
				setClipToZero(1638, 3661, 0);
				setClipToZero(1641, 3662, 0);
				setClipToZero(1641, 3661, 0);
				setClipToZero(1642, 3661, 0);
				setClipToZero(1643, 3661, 0);
				setClipToZero(1644, 3661, 0);
				setClipToZero(1636, 3662, 0);
				setClipToZero(1635, 3662, 0);
				setClipToZero(1634, 3662, 0);
				setClipToZero(1634, 3661, 0);
				setClipToZero(1633, 3662, 0);
				setClipToZero(1632, 3662, 0);
				setClipToZero(1631, 3662, 0);
				setClipToZero(1630, 3661, 0);
				setClipToZero(1631, 3661, 0);
				setClipped(1639, 3661, 0);
				setClipped(1640, 3661, 0);
				setClipToZero(1627, 3664, 0);
				setClipToZero(1627, 3665, 0);
				setClipToZero(1626, 3663, 0);
				setClipToZero(1626, 3664, 0);
				setClipToZero(1626, 3665, 0);
				setClipped(1639, 3675, 0);
				setClipped(1639, 3676, 0);
				setClipped(1638, 3675, 0);
				setClipped(1638, 3676, 0);
				setClipToZero(1627, 3684, 0);
				setClipToZero(1627, 3685, 0);
				setClipToZero(1628, 3685, 0);
				setClipToZero(1627, 3681, 0);
				setClipToZero(1627, 3682, 0);
				setClipToZero(1626, 3681, 0);
				setClipToZero(1625, 3684, 0);
				setClipToZero(1625, 3683, 0);
				setClipToZero(1625, 3681, 0);
				setClipToZero(1634, 3683, 0);
				setClipToZero(1634, 3684, 0);
				setClipToZero(1636, 3684, 0);
				setClipped(1636, 3685, 0);
				setClipped(1635, 3685, 0);
				setClipped(1637, 3685, 0);
				setClipped(1638, 3685, 0);
				setClipped(1639, 3685, 0);
				setClipToZero(1637, 3683, 0);
				setClipToZero(1637, 3684, 0);
				setClipToZero(1638, 3683, 0);
				setClipToZero(1638, 3684, 0);
				setClipToZero(1639, 3683, 0);
				setClipToZero(1639, 3684, 0);
				setClipToZero(1640, 3685, 0);
				setClipToZero(1641, 3685, 0);
				setClipToZero(1642, 3685, 0);
				setClipToZero(1643, 3685, 0);
				setClipToZero(1648, 3683, 0);
				setClipToZero(1649, 3683, 0);
				setClipToZero(1649, 3684, 0);
				setClipToZero(1650, 3684, 0);
				setClipToZero(1651, 3684, 0);
				setClipToZero(3008, 3849, 0);
				setClipToZero(3008, 3850, 0);
				setClipToZero(1621, 3674, 0);
				setClipToZero(1621, 3673, 0);
				setClipToZero(1621, 3672, 0);
				setClipToZero(1622, 3674, 0);
				setClipToZero(1622, 3673, 0);
				setClipToZero(1622, 3672, 0);
				setClipToZero(1623, 3674, 0);
				setClipToZero(1623, 3673, 0);
				setClipToZero(1623, 3672, 0);
				setClipToZero(3100, 3510, 0);
				setClipToZero(3100, 3509, 0);
				setClipToZero(2642, 2592, 0);//pc
				setClipToZero(2642, 2593, 0);
				setClipToZero(2657, 2584, 0);
				setClipToZero(2656, 2584, 0);
				setClipToZero(2671, 2593, 0);
				setClipToZero(2671, 2592, 0);
				setClipToZero(2630, 2593, 0);
				setClipToZero(2630, 2592, 0);
				setClipToZero(2630, 2591, 0);
				setClipToZero(2629, 2593, 0);
				setClipToZero(2629, 2592, 0);
				setClipToZero(2629, 2591, 0);
				setClipToZero(2628, 2593, 0);
				setClipToZero(2628, 2592, 0);
				setClipToZero(2628, 2591, 0);
				setClipToZero(2645, 2571, 0);
				setClipToZero(2646, 2571, 0);
				setClipToZero(2647, 2571, 0);
				setClipToZero(2645, 2570, 0);
				setClipToZero(2646, 2570, 0);
				setClipToZero(2647, 2570, 0);
				setClipToZero(2645, 2569, 0);
				setClipToZero(2646, 2569, 0);
				setClipToZero(2647, 2569, 0);
				setClipToZero(2669, 2572, 0);
				setClipToZero(2670, 2572, 0);
				setClipToZero(2671, 2572, 0);
				setClipToZero(2669, 2571, 0);
				setClipToZero(2670, 2571, 0);
				setClipToZero(2671, 2571, 0);
				setClipToZero(2669, 2570, 0);
				setClipToZero(2670, 2570, 0);
				setClipToZero(2671, 2570, 0);
				setClipToZero(2680, 2590, 0);
				setClipToZero(2681, 2590, 0);
				setClipToZero(2682, 2590, 0);
				setClipToZero(2680, 2589, 0);
				setClipToZero(2681, 2589, 0);
				setClipToZero(2682, 2589, 0);
				setClipToZero(2680, 2588, 0);
				setClipToZero(2681, 2588, 0);
				setClipToZero(2682, 2588, 0);
				setClipToZero(3032, 3314, 0);
				setClipToZero(3032, 3313, 0);
				setClipToZero(3031, 3313, 0);
				setClipToZero(3031, 3314, 0);
				
				
				/** AFKZone never completed  
				
				spawnWithObject(13291, 2740, 5079, 0, 10, 0);// AFK Chest
				*/
		
		for (GameObject i : active) {
			send(i);
		}

		logger.info("All object spawns have been loaded successfully.");
	}
	

	public static void process() {
	        for (Iterator<GameObject> iterator = register.iterator(); iterator.hasNext();) {
	            GameObject o = iterator.next();
	            if (o != null) {
	                active.remove(o);
	                active.add(o);
	                MapLoading.addObject(false, o.getId(), o.getLocation().getX(), o.getLocation().getY(),
	                        o.getLocation().getZ(), o.getType(), o.getFace());
	                send.add(o);
	                iterator.remove();
	            }
	        }
	    }

	public static final void deleteWithObject(int x, int y, int z) {
		GameObject object = Region.getObject(x, y, z);

		if (Region.getDoor(x, y, z) != null) {
			Region.removeDoor(x, y, z);
		}

		if (object == null) {
			active.add(getBlankObject(new Location(x, y, z), 10));
			return;
		}

		MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());
		active.add(getBlankObject(new Location(x, y, z), object.getType()));
	}

	private static final void deleteAndRemoveClip(int x, int y, int z) {
		deleteWithObject(x, y, z);
		Region region = Region.getRegion(x, y);
		if (region == null)
			return;
		region.setClipToZero(x, y, z);
	}
	
	private static final void removeActiveObject(int x, int y, int z) {
		active.remove(getGameObject( x, y, z));
	}

	public static final void deleteHouseObject(GameObject o) {
		if (o == null)
			return;
		GameObject object = getGameObject(o);

		active.remove(object);
		MapLoading.removeObject(object.getId(), o.getLocation().getX(), o.getLocation().getY(), o.getLocation().getZ(),
				object.getType(), object.getFace());
		send(o);
	}

	public static GameObject getGameObject(int x, int y, int z) {
		for (GameObject o : active) {
			if (o != null && o.getLocation().equals(new Location(x, y, z))) {
				return o;
			}
		}

		int index = active.indexOf(new GameObject(x, y, z));

		if (index == -1) {
			return null;
		}

		return active.get(index);
	}

	public static void registerObject(GameObject o) {
		register.add(o);
	}

	public static void removeActive(GameObject o) {
		active.remove(o);
	}

	public static void remove(GameObject o) {
		removeActive(o);
		send.add(getBlankObject(o.getLocation(), o.getType()));
	}

	public static final GameObject getBlankObject(Location p, int type) {
		return new GameObject(BLANK_OBJECT_ID, p.getX(), p.getY(), p.getZ(), type, 0, false);
	}

	/**
	 * Removed %4 from height checking.
	 */
	public static void send(GameObject o) {
		for (Player player : World.getPlayers())
			if ((player != null) && (player.isActive())) {
				if ((player.withinRegion(o.getLocation())) && player.getLocation().getZ() == o.getLocation().getZ()) {
					player.getObjects().add(o);
				}
			}
	}

	public static void setClipToZero(int x, int y, int z) {
		Region region = Region.getRegion(x, y);
		region.setClipToZero(x, y, z);
	}

	public static void setClipped(int x, int y, int z) {
		Region region = Region.getRegion(x, y);
		region.setClipping(x, y, z, 0x12801ff);
	}

	public static void setProjecileClipToInfinity(int x, int y, int z) { 
		Region region = Region.getRegion(x, y);
		region.setProjecileClipToInfinity(x, y, z);
	}

	private static final void spawnWithObject(int id, int x, int y, int z, int type, int face) {
		spawnWithObject(id, new Location(x, y, z), type, face);
	}

	public static final void spawnWithObject(int id, Location location, int type, int face) {
		spawnWithObject(new GameObject(id, location, type, face));
	}

	public static final void spawnWithObject(GameObject o) {
		if (getGameObject(o) != null)
			active.remove(o);
		active.add(o);
		MapLoading.addObject(false, o.getId(), o.getLocation().getX(), o.getLocation().getY(), o.getLocation().getZ(),
				o.getType(), o.getFace());
		send(o);
	}

	public static boolean objectExists(Location location) {
		for (GameObject object : active) {
			if (location.equals(object.getLocation())) {
				return true;
			}
		}
		return false;
	}

	public static GameObject getGameObject(int id, int x, int y, int z) {
		for (GameObject o : active) {
			if (o != null && o.getLocation().equals(new Location(x, y, z)) && o.getId() == id) {
				return o;
			}
		}

		int index = active.indexOf(new GameObject(id, x, y, z));

		if (index == -1) {
			return null;
		}

		return active.get(index);
	}

	public static GameObject getGameObject(GameObject obj) {
		for (GameObject o : active) {
			if (o != null && o.getLocation().getX() == obj.getLocation().getX()
					&& o.getLocation().getY() == obj.getLocation().getY()
					&& o.getLocation().getZ() == obj.getLocation().getZ() && o.getType() == obj.getType())
				return o;
		}
		return null;
	}

	public static List<GameObject> getActive() {
		return active;
	}

	public static Queue<GameObject> getSend() {
		return send;
	}
}
