package com.bestbudz.rs2.content.cluescroll;

import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.chance.Chance;
import com.bestbudz.rs2.content.cluescroll.Clue.ClueType;
import com.bestbudz.rs2.content.cluescroll.scroll.EmoteScroll;
import com.bestbudz.rs2.content.cluescroll.scroll.MapScroll;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.item.ItemContainer.ContainerTypes;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.impl.clickbuttons.ButtonAssignment;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public enum ClueScrollManager {
  SINGLETON;

  public static final int CASKET_EASY = 2714;

  public static final int CASKET_MEDIUM = 2802;

  public static final int CASKET_HARD = 2724;
  public static final Chance<Item> CROSS_TRAILS = new Chance<Item>();
  public static final Chance<Item> EASY = new Chance<Item>();
  public static final Chance<Item> MEDIUM = new Chance<Item>();
  public static final Chance<Item> HARD = new Chance<Item>();
  private static final HashMap<Integer, ClueScroll> CLUE_SCROLLS = new HashMap<>();

  public static void declare() {
    CROSS_TRAILS.add(100, new Item(995, 500));
    CROSS_TRAILS.add(100, new Item(995, 750));
    CROSS_TRAILS.add(100, new Item(995, 1000));
    CROSS_TRAILS.add(100, new Item(995, 5000));
    CROSS_TRAILS.add(100, new Item(995, 5500));
    CROSS_TRAILS.add(100, new Item(1692, 1));
    CROSS_TRAILS.add(100, new Item(1694, 1));
    CROSS_TRAILS.add(100, new Item(1696, 1));
    CROSS_TRAILS.add(100, new Item(1698, 1));
    CROSS_TRAILS.add(100, new Item(1700, 1));
    CROSS_TRAILS.add(75, new Item(1702, 1));
    CROSS_TRAILS.add(75, new Item(10280, 1));
    CROSS_TRAILS.add(75, new Item(10282, 1));
    CROSS_TRAILS.add(75, new Item(10284, 1));
    CROSS_TRAILS.add(100, new Item(554, 885));
    CROSS_TRAILS.add(100, new Item(555, 553));
    CROSS_TRAILS.add(100, new Item(556, 533));
    CROSS_TRAILS.add(100, new Item(557, 492));
    CROSS_TRAILS.add(100, new Item(558, 228));
    CROSS_TRAILS.add(100, new Item(559, 97));
    CROSS_TRAILS.add(100, new Item(560, 612));
    CROSS_TRAILS.add(100, new Item(561, 17));
    CROSS_TRAILS.add(100, new Item(562, 4));
    CROSS_TRAILS.add(100, new Item(563, 745));
    CROSS_TRAILS.add(100, new Item(564, 973));
    CROSS_TRAILS.add(100, new Item(565, 895));
    CROSS_TRAILS.add(25, new Item(3827, 1));
    CROSS_TRAILS.add(25, new Item(3828, 1));
    CROSS_TRAILS.add(25, new Item(3829, 1));
    CROSS_TRAILS.add(25, new Item(3830, 1));
    CROSS_TRAILS.add(25, new Item(3831, 1));
    CROSS_TRAILS.add(25, new Item(3832, 1));
    CROSS_TRAILS.add(25, new Item(3833, 1));
    CROSS_TRAILS.add(25, new Item(3834, 1));
    CROSS_TRAILS.add(25, new Item(3835, 1));
    CROSS_TRAILS.add(25, new Item(3836, 1));
    CROSS_TRAILS.add(25, new Item(3837, 1));
    CROSS_TRAILS.add(25, new Item(3838, 1));

    EASY.add(200, new Item(1077, 1));
    EASY.add(200, new Item(1089, 1));
    EASY.add(200, new Item(1107, 1));
    EASY.add(200, new Item(1125, 1));
    EASY.add(200, new Item(1151, 1));
    EASY.add(200, new Item(1165, 1));
    EASY.add(200, new Item(1179, 1));
    EASY.add(200, new Item(1195, 1));
    EASY.add(200, new Item(1217, 1));
    EASY.add(200, new Item(1283, 1));
    EASY.add(200, new Item(1297, 1));
    EASY.add(200, new Item(1313, 1));
    EASY.add(200, new Item(1327, 1));
    EASY.add(200, new Item(1341, 1));
    EASY.add(200, new Item(1361, 1));
    EASY.add(200, new Item(1367, 1));
    EASY.add(200, new Item(1426, 1));
    EASY.add(200, new Item(8778, 1));
    EASY.add(200, new Item(849, 1));
    EASY.add(200, new Item(1169, 1));
    EASY.add(200, new Item(1095, 1));
    EASY.add(200, new Item(1129, 1));
    EASY.add(200, new Item(1131, 1));
    EASY.add(200, new Item(1063, 1));
    EASY.add(200, new Item(1061, 1));
    EASY.add(200, new Item(1059, 1));
    EASY.add(200, new Item(1167, 1));
    EASY.add(200, new Item(329, 1));
    EASY.add(200, new Item(333, 1));
    EASY.add(200, new Item(1438, 1));
    EASY.add(200, new Item(1440, 1));
    EASY.add(200, new Item(1442, 1));
    EASY.add(200, new Item(1444, 1));
    EASY.add(200, new Item(1446, 1));
    EASY.add(200, new Item(1448, 1));
    EASY.add(200, new Item(1269, 1));
    EASY.add(200, new Item(1452, 1));
    EASY.add(200, new Item(1454, 1));
    EASY.add(200, new Item(1456, 1));
    EASY.add(200, new Item(1458, 1));
    EASY.add(200, new Item(1462, 1));
    EASY.add(15, new Item(12205, 1));
    EASY.add(15, new Item(12207, 1));
    EASY.add(15, new Item(12209, 1));
    EASY.add(15, new Item(12211, 1));
    EASY.add(15, new Item(12213, 1));
    EASY.add(15, new Item(12215, 1));
    EASY.add(15, new Item(12217, 1));
    EASY.add(15, new Item(12219, 1));
    EASY.add(15, new Item(12221, 1));
    EASY.add(15, new Item(12223, 1));
    EASY.add(15, new Item(12225, 1));
    EASY.add(15, new Item(12227, 1));
    EASY.add(15, new Item(12229, 1));
    EASY.add(15, new Item(12231, 1));
    EASY.add(15, new Item(12233, 1));
    EASY.add(15, new Item(12235, 1));
    EASY.add(15, new Item(12237, 1));
    EASY.add(15, new Item(12239, 1));
    EASY.add(15, new Item(12241, 1));
    EASY.add(15, new Item(12243, 1));
    EASY.add(15, new Item(2583, 1));
    EASY.add(15, new Item(2585, 1));
    EASY.add(15, new Item(2587, 1));
    EASY.add(15, new Item(2589, 1));
    EASY.add(15, new Item(2591, 1));
    EASY.add(15, new Item(2593, 1));
    EASY.add(15, new Item(2595, 1));
    EASY.add(15, new Item(2597, 1));
    EASY.add(15, new Item(3472, 1));
    EASY.add(15, new Item(3473, 1));
    EASY.add(45, new Item(2635, 1));
    EASY.add(45, new Item(2637, 1));
    EASY.add(45, new Item(12247, 1));
    EASY.add(45, new Item(2633, 1));
    EASY.add(200, new Item(2631, 1));
    EASY.add(25, new Item(12245, 1));
    EASY.add(15, new Item(7386, 1));
    EASY.add(15, new Item(7390, 1));
    EASY.add(15, new Item(7394, 1));
    EASY.add(15, new Item(7396, 1));
    EASY.add(15, new Item(7388, 1));
    EASY.add(15, new Item(7392, 1));
    EASY.add(15, new Item(12449, 1));
    EASY.add(15, new Item(12453, 1));
    EASY.add(15, new Item(12445, 1));
    EASY.add(15, new Item(12447, 1));
    EASY.add(15, new Item(12451, 1));
    EASY.add(15, new Item(12455, 1));
    EASY.add(15, new Item(7364, 1));
    EASY.add(15, new Item(7368, 1));
    EASY.add(15, new Item(7362, 1));
    EASY.add(15, new Item(7366, 1));
    EASY.add(175, new Item(7332, 1));
    EASY.add(175, new Item(7338, 1));
    EASY.add(175, new Item(7344, 1));
    EASY.add(175, new Item(7350, 1));
    EASY.add(175, new Item(7356, 1));
    EASY.add(175, new Item(10306, 1));
    EASY.add(175, new Item(10308, 1));
    EASY.add(175, new Item(10310, 1));
    EASY.add(175, new Item(10312, 1));
    EASY.add(175, new Item(10314, 1));
    EASY.add(25, new Item(10404, 1));
    EASY.add(25, new Item(10406, 1));
    EASY.add(25, new Item(10424, 1));
    EASY.add(25, new Item(10426, 1));
    EASY.add(25, new Item(10412, 1));
    EASY.add(25, new Item(10414, 1));
    EASY.add(25, new Item(10432, 1));
    EASY.add(25, new Item(10434, 1));
    EASY.add(25, new Item(10408, 1));
    EASY.add(25, new Item(10410, 1));
    EASY.add(25, new Item(10428, 1));
    EASY.add(25, new Item(10430, 1));
    EASY.add(25, new Item(10316, 1));
    EASY.add(25, new Item(10318, 1));
    EASY.add(25, new Item(10320, 1));
    EASY.add(25, new Item(10322, 1));
    EASY.add(25, new Item(10324, 1));
    EASY.add(27, new Item(10392, 1));
    EASY.add(27, new Item(10394, 1));
    EASY.add(27, new Item(10396, 1));
    EASY.add(27, new Item(10398, 1));
    EASY.add(17, new Item(10366, 1));
    EASY.add(10, new Item(12375, 1));
    EASY.add(10, new Item(12297, 1));
    EASY.add(20, new Item(10458, 1));
    EASY.add(20, new Item(10464, 1));
    EASY.add(20, new Item(10462, 1));
    EASY.add(20, new Item(10466, 1));
    EASY.add(20, new Item(10460, 1));
    EASY.add(20, new Item(10468, 1));
    EASY.add(20, new Item(12193, 1));
    EASY.add(20, new Item(12195, 1));
    EASY.add(20, new Item(12253, 1));
    EASY.add(20, new Item(12255, 1));
    EASY.add(20, new Item(12265, 1));
    EASY.add(20, new Item(12267, 1));

    MEDIUM.add(200, new Item(1073, 1));
    MEDIUM.add(200, new Item(1091, 1));
    MEDIUM.add(200, new Item(1111, 1));
    MEDIUM.add(200, new Item(1123, 1));
    MEDIUM.add(200, new Item(1145, 1));
    MEDIUM.add(200, new Item(1161, 1));
    MEDIUM.add(200, new Item(1183, 1));
    MEDIUM.add(200, new Item(1199, 1));
    MEDIUM.add(200, new Item(1211, 1));
    MEDIUM.add(200, new Item(1287, 1));
    MEDIUM.add(200, new Item(1301, 1));
    MEDIUM.add(200, new Item(1317, 1));
    MEDIUM.add(200, new Item(1331, 1));
    MEDIUM.add(200, new Item(1345, 1));
    MEDIUM.add(200, new Item(1357, 1));
    MEDIUM.add(200, new Item(1371, 1));
    MEDIUM.add(200, new Item(1430, 1));
    MEDIUM.add(200, new Item(1271, 1));
    MEDIUM.add(200, new Item(9183, 1));
    MEDIUM.add(200, new Item(4823, 15));
    MEDIUM.add(200, new Item(1393, 1));
    MEDIUM.add(200, new Item(857, 1));
    MEDIUM.add(200, new Item(8780, 1));
    MEDIUM.add(200, new Item(373, 1));
    MEDIUM.add(200, new Item(379, 1));
    MEDIUM.add(200, new Item(1099, 1));
    MEDIUM.add(200, new Item(1135, 1));
    MEDIUM.add(15, new Item(12293, 1));
    MEDIUM.add(15, new Item(12287, 1));
    MEDIUM.add(15, new Item(12289, 1));
    MEDIUM.add(15, new Item(12291, 1));
    MEDIUM.add(15, new Item(12295, 1));
    MEDIUM.add(15, new Item(12283, 1));
    MEDIUM.add(15, new Item(12277, 1));
    MEDIUM.add(15, new Item(12285, 1));
    MEDIUM.add(15, new Item(12279, 1));
    MEDIUM.add(15, new Item(12281, 1));
    MEDIUM.add(15, new Item(2605, 1));
    MEDIUM.add(15, new Item(3474, 1));
    MEDIUM.add(15, new Item(2603, 1));
    MEDIUM.add(15, new Item(2599, 1));
    MEDIUM.add(15, new Item(2601, 1));
    MEDIUM.add(15, new Item(2607, 1));
    MEDIUM.add(15, new Item(2609, 1));
    MEDIUM.add(15, new Item(2611, 1));
    MEDIUM.add(15, new Item(2613, 1));
    MEDIUM.add(15, new Item(3475, 1));
    MEDIUM.add(10, new Item(2577, 1));
    MEDIUM.add(10, new Item(12598, 1));
    MEDIUM.add(25, new Item(2579, 1));
    MEDIUM.add(100, new Item(2647, 1));
    MEDIUM.add(100, new Item(2645, 1));
    MEDIUM.add(100, new Item(2649, 1));
    MEDIUM.add(100, new Item(12305, 1));
    MEDIUM.add(100, new Item(12307, 1));
    MEDIUM.add(100, new Item(12301, 1));
    MEDIUM.add(100, new Item(12299, 1));
    MEDIUM.add(100, new Item(12303, 1));
    MEDIUM.add(35, new Item(7319, 1));
    MEDIUM.add(35, new Item(7321, 1));
    MEDIUM.add(35, new Item(7323, 1));
    MEDIUM.add(35, new Item(7325, 1));
    MEDIUM.add(35, new Item(7327, 1));
    MEDIUM.add(35, new Item(12309, 1));
    MEDIUM.add(35, new Item(12311, 1));
    MEDIUM.add(35, new Item(12313, 1));
    MEDIUM.add(15, new Item(7380, 1));
    MEDIUM.add(15, new Item(7372, 1));
    MEDIUM.add(15, new Item(7370, 1));
    MEDIUM.add(15, new Item(7378, 1));
    MEDIUM.add(100, new Item(7334, 1));
    MEDIUM.add(100, new Item(7340, 1));
    MEDIUM.add(100, new Item(7346, 1));
    MEDIUM.add(100, new Item(7352, 1));
    MEDIUM.add(100, new Item(7358, 1));
    MEDIUM.add(100, new Item(10296, 1));
    MEDIUM.add(100, new Item(10298, 1));
    MEDIUM.add(100, new Item(10300, 1));
    MEDIUM.add(100, new Item(10302, 1));
    MEDIUM.add(100, new Item(10304, 1));
    MEDIUM.add(25, new Item(10400, 1));
    MEDIUM.add(25, new Item(10402, 1));
    MEDIUM.add(25, new Item(10416, 1));
    MEDIUM.add(25, new Item(10418, 1));
    MEDIUM.add(25, new Item(12315, 1));
    MEDIUM.add(25, new Item(12317, 1));
    MEDIUM.add(25, new Item(12339, 1));
    MEDIUM.add(25, new Item(12341, 1));
    MEDIUM.add(25, new Item(12343, 1));
    MEDIUM.add(25, new Item(12345, 1));
    MEDIUM.add(25, new Item(12347, 1));
    MEDIUM.add(25, new Item(12349, 1));
    MEDIUM.add(25, new Item(10436, 1));
    MEDIUM.add(25, new Item(10438, 1));
    MEDIUM.add(25, new Item(10420, 1));
    MEDIUM.add(25, new Item(10422, 1));
    MEDIUM.add(10, new Item(12377, 1));
    MEDIUM.add(35, new Item(10364, 1));
    MEDIUM.add(35, new Item(12361, 1));
    MEDIUM.add(35, new Item(12428, 1));
    MEDIUM.add(35, new Item(12359, 1));
    MEDIUM.add(35, new Item(12319, 1));
    MEDIUM.add(20, new Item(10446, 1));
    MEDIUM.add(20, new Item(10448, 1));
    MEDIUM.add(20, new Item(10450, 1));
    MEDIUM.add(20, new Item(12197, 1));
    MEDIUM.add(20, new Item(12261, 1));
    MEDIUM.add(20, new Item(12273, 1));
    MEDIUM.add(20, new Item(10452, 1));
    MEDIUM.add(20, new Item(10454, 1));
    MEDIUM.add(20, new Item(10456, 1));
    MEDIUM.add(20, new Item(12203, 1));
    MEDIUM.add(20, new Item(12259, 1));
    MEDIUM.add(20, new Item(12271, 1));

    HARD.add(200, new Item(1079, 1));
    HARD.add(200, new Item(1093, 1));
    HARD.add(200, new Item(1113, 1));
    HARD.add(200, new Item(1127, 1));
    HARD.add(200, new Item(1147, 1));
    HARD.add(200, new Item(1163, 1));
    HARD.add(200, new Item(1185, 1));
    HARD.add(200, new Item(1201, 1));
    HARD.add(200, new Item(1213, 1));
    HARD.add(200, new Item(1289, 1));
    HARD.add(200, new Item(1303, 1));
    HARD.add(200, new Item(1319, 1));
    HARD.add(200, new Item(1333, 1));
    HARD.add(200, new Item(1347, 1));
    HARD.add(200, new Item(1359, 1));
    HARD.add(200, new Item(1373, 1));
    HARD.add(200, new Item(1432, 1));
    HARD.add(200, new Item(859, 1));
    HARD.add(200, new Item(861, 1));
    HARD.add(200, new Item(2497, 1));
    HARD.add(200, new Item(2503, 1));
    HARD.add(200, new Item(2491, 1));
    HARD.add(200, new Item(385, 1));
    HARD.add(200, new Item(379, 1));
    HARD.add(35, new Item(12526, 1));
    HARD.add(35, new Item(12532, 1));
    HARD.add(35, new Item(12534, 1));
    HARD.add(35, new Item(12536, 1));
    HARD.add(35, new Item(12538, 1));
    HARD.add(35, new Item(12528, 1));
    HARD.add(35, new Item(12530, 1));
    HARD.add(15, new Item(12381, 1));
    HARD.add(15, new Item(12383, 1));
    HARD.add(15, new Item(12385, 1));
    HARD.add(15, new Item(12387, 1));
    HARD.add(15, new Item(2615, 1));
    HARD.add(15, new Item(2617, 1));
    HARD.add(15, new Item(2619, 1));
    HARD.add(15, new Item(2621, 1));
    HARD.add(15, new Item(2623, 1));
    HARD.add(15, new Item(2625, 1));
    HARD.add(15, new Item(2627, 1));
    HARD.add(15, new Item(2629, 1));
    HARD.add(15, new Item(3476, 1));
    HARD.add(15, new Item(3477, 1));
    HARD.add(65, new Item(2669, 1));
    HARD.add(65, new Item(2671, 1));
    HARD.add(65, new Item(2673, 1));
    HARD.add(65, new Item(2675, 1));
    HARD.add(65, new Item(3480, 1));
    HARD.add(65, new Item(2653, 1));
    HARD.add(65, new Item(2655, 1));
    HARD.add(65, new Item(2657, 1));
    HARD.add(65, new Item(2659, 1));
    HARD.add(65, new Item(3478, 1));
    HARD.add(65, new Item(2661, 1));
    HARD.add(65, new Item(2663, 1));
    HARD.add(65, new Item(2665, 1));
    HARD.add(65, new Item(2667, 1));
    HARD.add(65, new Item(3479, 1));
    HARD.add(25, new Item(3481, 1));
    HARD.add(25, new Item(3483, 1));
    HARD.add(25, new Item(3485, 1));
    HARD.add(25, new Item(3486, 1));
    HARD.add(25, new Item(3488, 1));
    HARD.add(25, new Item(12389, 1));
    HARD.add(25, new Item(12391, 1));
    HARD.add(100, new Item(7336, 1));
    HARD.add(100, new Item(7342, 1));
    HARD.add(100, new Item(7348, 1));
    HARD.add(100, new Item(7354, 1));
    HARD.add(100, new Item(7360, 1));
    HARD.add(100, new Item(10286, 1));
    HARD.add(100, new Item(10288, 1));
    HARD.add(100, new Item(10290, 1));
    HARD.add(100, new Item(10292, 1));
    HARD.add(100, new Item(10294, 1));
    HARD.add(65, new Item(7374, 1));
    HARD.add(65, new Item(7376, 1));
    HARD.add(65, new Item(7382, 1));
    HARD.add(65, new Item(7384, 1));
    HARD.add(65, new Item(7398, 1));
    HARD.add(65, new Item(7399, 1));
    HARD.add(65, new Item(7400, 1));
    HARD.add(10, new Item(2581, 1));
    HARD.add(35, new Item(2639, 1));
    HARD.add(35, new Item(2641, 1));
    HARD.add(35, new Item(2643, 1));
    HARD.add(35, new Item(12321, 1));
    HARD.add(35, new Item(12323, 1));
    HARD.add(35, new Item(12325, 1));
    HARD.add(45, new Item(2651, 1));
    HARD.add(5, new Item(10346, 1));
    HARD.add(5, new Item(10348, 1));
    HARD.add(5, new Item(10350, 1));
    HARD.add(5, new Item(10352, 1));
    HARD.add(5, new Item(10330, 1));
    HARD.add(5, new Item(10332, 1));
    HARD.add(5, new Item(10334, 1));
    HARD.add(5, new Item(10336, 1));
    HARD.add(5, new Item(10338, 1));
    HARD.add(5, new Item(10340, 1));
    HARD.add(5, new Item(10342, 1));
    HARD.add(5, new Item(10344, 1));
    HARD.add(5, new Item(12422, 1));
    HARD.add(5, new Item(12424, 1));
    HARD.add(5, new Item(12426, 1));
    HARD.add(5, new Item(12437, 1));
    HARD.add(25, new Item(10382, 1));
    HARD.add(25, new Item(10380, 1));
    HARD.add(25, new Item(10378, 1));
    HARD.add(25, new Item(10376, 1));
    HARD.add(25, new Item(10390, 1));
    HARD.add(25, new Item(10388, 1));
    HARD.add(25, new Item(10386, 1));
    HARD.add(25, new Item(10384, 1));
    HARD.add(25, new Item(10374, 1));
    HARD.add(25, new Item(10372, 1));
    HARD.add(25, new Item(10370, 1));
    HARD.add(25, new Item(10368, 1));
    HARD.add(25, new Item(12512, 1));
    HARD.add(25, new Item(12510, 1));
    HARD.add(25, new Item(12508, 1));
    HARD.add(25, new Item(12506, 1));
    HARD.add(25, new Item(12496, 1));
    HARD.add(25, new Item(12494, 1));
    HARD.add(25, new Item(12492, 1));
    HARD.add(25, new Item(12490, 1));
    HARD.add(25, new Item(12504, 1));
    HARD.add(25, new Item(12502, 1));
    HARD.add(25, new Item(12500, 1));
    HARD.add(25, new Item(12498, 1));
    HARD.add(47, new Item(12518, 1));
    HARD.add(47, new Item(12520, 1));
    HARD.add(47, new Item(12522, 1));
    HARD.add(47, new Item(12524, 1));
    HARD.add(47, new Item(12516, 1));
    HARD.add(20, new Item(12269, 1));
    HARD.add(20, new Item(12257, 1));
    HARD.add(20, new Item(12201, 1));
    HARD.add(20, new Item(10474, 1));
    HARD.add(20, new Item(10472, 1));
    HARD.add(20, new Item(10470, 1));
    HARD.add(20, new Item(12275, 1));
    HARD.add(20, new Item(12263, 1));
    HARD.add(20, new Item(12199, 1));
    HARD.add(20, new Item(10444, 1));
    HARD.add(20, new Item(10442, 1));
    HARD.add(20, new Item(10440, 1));
    HARD.add(10, new Item(12379, 1));

    CROSS_TRAILS.sort();
    EASY.sort();
    MEDIUM.sort();
    HARD.sort();
    CLUE_SCROLLS.put(
        2682, new MapScroll(2682, ClueDifficulty.EASY, new Location(3290, 3372, 0), 7045));
    CLUE_SCROLLS.put(
        2683, new MapScroll(2683, ClueDifficulty.EASY, new Location(3092, 3226, 0), 7113));
    CLUE_SCROLLS.put(
        2684, new MapScroll(2684, ClueDifficulty.EASY, new Location(2702, 3428, 0), 7162));
    CLUE_SCROLLS.put(
        2685, new MapScroll(2685, ClueDifficulty.EASY, new Location(2970, 3414, 0), 17537));
    CLUE_SCROLLS.put(
        2803, new MapScroll(2803, ClueDifficulty.MEDIUM, new Location(3043, 3399, 0), 7271));
    CLUE_SCROLLS.put(
        2805, new MapScroll(2805, ClueDifficulty.MEDIUM, new Location(2616, 3077, 0), 9043));
    CLUE_SCROLLS.put(
        2807, new MapScroll(2807, ClueDifficulty.MEDIUM, new Location(3109, 3151, 0), 9275));
    CLUE_SCROLLS.put(
        2809, new MapScroll(2809, ClueDifficulty.MEDIUM, new Location(2722, 3339, 0), 17634));
    CLUE_SCROLLS.put(
        2677,
        new EmoteScroll(
            2677,
            ClueDifficulty.EASY,
            new Item[] {
              null,
              null,
              null,
              null,
              new Item(1133),
              null,
              null,
              new Item(1075),
              null,
              null,
              null,
              null,
              null,
              null
            },
            new Location(2598, 3280, 0),
            1,
            2110,
            "",
            "",
            "Blow a raspberry at the monkey",
            "cage in Ardougne Zoo.",
            "Equip a studded body and bronze",
            "platelegs.",
            "",
            "",
            ""));
    CLUE_SCROLLS.put(
        2678,
        new EmoteScroll(
            2678,
            ClueDifficulty.EASY,
            new Item[] {
              null,
              null,
              new Item(1654),
              new Item(1237),
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              new Item(1635),
              null
            },
            new Location(2977, 3240, 0),
            11,
            2113,
            "",
            "",
            "Shrug in the quarry near Rimmington,",
            "quip a gold necklace, a gold ring",
            "and a bronze spear.",
            "",
            "",
            ""));
    CLUE_SCROLLS.put(
        2679,
        new EmoteScroll(
            2679,
            ClueDifficulty.EASY,
            new Item[] {
              null,
              null,
              new Item(1696),
              new Item(845),
              null,
              null,
              null,
              new Item(1067),
              null,
              null,
              null,
              null,
              null,
              null
            },
            new Location(2728, 3348, 0),
            1,
            858,
            "",
            "",
            "Bow outside the entrance to the",
            "Legends' Guild. Equip iron platelegs,",
            "an emerald amulet and an oak",
            "longbow.",
            "",
            ""));
    CLUE_SCROLLS.put(
        2680,
        new EmoteScroll(
            2680,
            ClueDifficulty.EASY,
            new Item[] {
              new Item(740),
              null,
              null,
              new Item(1307),
              null,
              null,
              null,
              null,
              null,
              null,
              new Item(1061),
              null,
              null,
              null
            },
            new Location(2926, 3484, 0),
            5,
            862,
            "",
            "",
            "Cheer at the Druids' Circle. Equip",
            "a blue wizard hat, a bronze two-",
            "handed sword and leather boots.",
            "",
            "",
            "",
            ""));
    CLUE_SCROLLS.put(
        2681,
        new EmoteScroll(
            2681,
            ClueDifficulty.EASY,
            new Item[] {
              null,
              null,
              new Item(1694),
              null,
              new Item(1103),
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              new Item(1639),
              null
            },
            new Location(2611, 3393, 0),
            2,
            2106,
            "",
            "",
            "Dance a jig by the entrance to the",
            "Fisher Guild, equip a sapphire",
            "amulet, an emerald ring and a",
            "bronze chainbody",
            "",
            "",
            ""));
    CLUE_SCROLLS.put(
        2801,
        new EmoteScroll(
            2801,
            ClueDifficulty.MEDIUM,
            new Item[] {
              new Item(658),
              null,
              null,
              new Item(1267),
              null,
              null,
              null,
              null,
              null,
              null,
              new Item(6328),
              null,
              null,
              null
            },
            new Location(3370, 3428, 0),
            1,
            859,
            "",
            "",
            "Beckon in the Digsite, near the eastern",
            "winch. Equip a green hat, snakeskin",
            "boots and an iron pickaxe.",
            "",
            "",
            "",
            ""));
    CLUE_SCROLLS.put(
        2722,
        new EmoteScroll(
            2722,
            ClueDifficulty.HARD,
            new Item[] {
              null,
              null,
              null,
              new Item(1347),
              null,
              new Item(2890),
              null,
              new Item(2493),
              null,
              null,
              null,
              null,
              null,
              null
            },
            new Location(2587, 3420, 0),
            2,
            2110,
            "",
            "",
            "Blow a raspberry in the Fisher Guild",
            "bank. Beware of double agents!",
            "Equip an elemental shield, blue",
            "dragonhide chaps and a rune",
            "warhammer",
            "",
            ""));
    CLUE_SCROLLS.put(
        2723,
        new EmoteScroll(
            2723,
            ClueDifficulty.HARD,
            new Item[] {
              null,
              null,
              new Item(1731),
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              null,
              new Item(1643),
              null
            },
            new Location(2920, 3163, 0),
            10,
            2112,
            "",
            "Salute in the banana plantation.",
            "Beware of double agents!",
            "Equip a diamond ring, amulet",
            "of power and nothing on",
            "your chest and legs.",
            "",
            "",
            ""));
  }

  public static Item getRandomClue(Stoner stoner, ClueDifficulty difficulty) {
    List<ClueScroll> scrolls =
        CLUE_SCROLLS.values().stream()
            .filter(scroll -> checkScroll(stoner, scroll, difficulty))
            .collect(Collectors.toList());

    if (scrolls.isEmpty()) {
      return null;
    }

    return new Item(Utility.randomElement(scrolls).getScrollId());
  }

  private static final boolean checkScroll(
      Stoner stoner, ClueScroll scroll, ClueDifficulty difficulty) {
    int scrollId = scroll.getScrollId();

    List<ClueScroll> difficultyScrolls =
        CLUE_SCROLLS.values().stream()
            .filter(s -> s.getDifficulty() == difficulty)
            .collect(Collectors.toList());

    for (ClueScroll s : difficultyScrolls) {
      if (stoner.getBank().hasItemId(s.getScrollId())
          || stoner.getBox().hasItemId(s.getScrollId())) {
        return false;
      }
    }

    return scroll.getDifficulty().equals(difficulty)
        && !stoner.getBank().hasItemId(scrollId)
        && !stoner.getBox().hasItemId(scrollId);
  }

  public static void reward(Stoner stoner, int item, ClueDifficulty difficulty) {
    if (stoner.getBox().getFreeSlots() < 6) {
      stoner.send(new SendMessage("You need at least 6 free slots to open this casket."));
      return;
    }

    stoner.getBox().remove(item, 1);
    stoner.send(new SendRemoveInterfaces());

    ItemContainer items =
        new ItemContainer(9, ContainerTypes.ALWAYS_STACK, true, true) {
          @Override
          public boolean allowZero(int paramInt) {
            return false;
          }

          @Override
          public void onAdd(Item paramItem) {}

          @Override
          public void onFillContainer() {}

          @Override
          public void onMaxStack() {}

          @Override
          public void onRemove(Item paramItem) {}

          @Override
          public void update() {
            stoner.send(new SendUpdateItems(6963, items));
          }
        };

    int length = 3 + Utility.random(3);

    for (int i = 0; i < length; i++) {
      Item reward;

      do {
        reward = difficulty.getRewards().getReward();
      } while (items.hasItemId(reward.getId()));

      items.add(reward, false);

      int amount = reward.getAmount();

      if (amount > 1) {
        amount = Utility.randomNumber(amount) + 1;
      }

      stoner.getBox().add(reward.getId(), amount, false);
    }

    items.update();
    stoner.getBox().update();
    stoner.send(new SendInterface(6960));

    for (int i = 0; i < items.items.length; i++) {
      if (items.items[i] == null) {
        continue;
      }
      if (items.items[i].getDefinition().getGeneralPrice() >= 500_000
          || items.items[i].getDefinition().getName().contains("ornament")
          || items.items[i].getDefinition().getName().contains("Gilded")
          || items.items[i].getDefinition().getName().contains("Sagittarius")
          || items.items[i].getDefinition().getName().contains("Robin")
          || items.items[i].getDefinition().getName().contains("3rd")) {
        World.sendGlobalMessage(
            "<img=8> <col=C42BAD>"
                + stoner.deterquarryIcon(stoner)
                + Utility.formatStonerName(stoner.getUsername())
                + " has recieved "
                + Utility.deterquarryIndefiniteArticle(items.items[i].getDefinition().getName())
                + " "
                + items.items[i].getDefinition().getName()
                + " from a "
                + Utility.capitalize(difficulty.name().toLowerCase())
                + " clue scroll.");
      }
    }
  }

  public static ClueScroll getClue(int id) {
    return CLUE_SCROLLS.get(id);
  }

  public static boolean stonerHasScroll(Stoner stoner) {
    for (int i = 0; i < stoner.getBox().getItems().length; i++) {
      if (stoner.getBox().getItems()[i] != null
          && getClue(stoner.getBox().getItems()[i].getId()) != null) {
        return true;
      }
    }
    return false;
  }

  public boolean clickItem(Stoner stoner, int item) {
    ClueScroll scroll = CLUE_SCROLLS.get(item);

    if (scroll == null) {
      switch (item) {
        case CASKET_EASY:
          reward(stoner, item, ClueDifficulty.EASY);
          return true;
        case CASKET_MEDIUM:
          reward(stoner, item, ClueDifficulty.MEDIUM);
          return true;
        case CASKET_HARD:
          reward(stoner, item, ClueDifficulty.HARD);
          return true;
      }
      return false;
    }

    scroll.displayClue(stoner);

    return true;
  }

  public boolean dig(Stoner stoner) {
    List<ClueScroll> emoteScrolls =
        CLUE_SCROLLS.values().stream()
            .filter(
                scroll ->
                    scroll.getClueType() == ClueType.MAP
                        || scroll.getClueType() == ClueType.COORDINATE)
            .collect(Collectors.toList());

    if (emoteScrolls.isEmpty()) {
      return false;
    }

    for (ClueScroll scroll : emoteScrolls) {
      if (scroll.meetsRequirements(stoner)) {
        return scroll.execute(stoner);
      }
    }

    return false;
  }

  public void handleEmote(Stoner stoner, ButtonAssignment.Emote emote) {
    List<ClueScroll> emoteScrolls =
        CLUE_SCROLLS.values().stream()
            .filter(scroll -> scroll.getClueType() == ClueType.EMOTE)
            .collect(Collectors.toList());

    if (emoteScrolls.isEmpty()) {
      return;
    }

    for (ClueScroll scroll : emoteScrolls) {
      if (scroll.meetsRequirements(stoner)) {
        if (emote.animID == ((EmoteScroll) scroll).getAnimationId()) {
          scroll.execute(stoner);
        }
        break;
      }
    }
  }

  public interface ClueReward {

    Item getReward();
  }
}
