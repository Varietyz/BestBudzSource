package com.bestbudz.rs2.entity.object;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.bestbudz.core.cache.map.MapLoading;
import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;

@SuppressWarnings("all")
public class ObjectManager {

	public static final int BLANK_OBJECT_ID = 2376;

	private static final List<GameObject> active = new LinkedList<GameObject>();
	private static final Deque<GameObject> register = new LinkedList<GameObject>();

	private static final Queue<GameObject> send = new ConcurrentLinkedQueue<GameObject>();

	public static void add(GameObject o) {
	active.add(o);
	}

	public static void registerObject(GameObject o) {
	register.add(o);
	}

	public static void addClippedObject(GameObject o) {
	register.add(o);
	}

	public static void declare() {

	for (GameObject i : active) {
		send(getBlankObject(i.getLocation()));
	}

	active.clear();

	// PROFESSIONING AREA HOME
	deleteWithObject(3444, 2909, 0);
	deleteWithObject(3440, 2909, 0);
	deleteWithObject(3438, 2910, 0);
	deleteWithObject(3446, 2911, 0);
	deleteWithObject(3440, 2922, 0);
	deleteWithObject(3443, 2922, 0);
	deleteWithObject(3449, 2912, 0);
	deleteWithObject(3445, 2918, 0);
	deleteWithObject(3439, 2918, 0);
	spawnWithObject(13709, 3438, 2911, 0, 10, 0);// copper
	spawnWithObject(13712, 3438, 2910, 0, 10, 0);// tin
	spawnWithObject(13711, 3439, 2909, 0, 10, 0);// iron
	spawnWithObject(13714, 3440, 2909, 0, 10, 0);// coal
	spawnWithObject(13707, 3443, 2909, 0, 10, 0);// gold
	spawnWithObject(13718, 3444, 2909, 0, 10, 0);// mithril
	spawnWithObject(14168, 3447, 2909, 0, 10, 0);// adamantite
	spawnWithObject(14175, 3448, 2909, 0, 10, 0);// runite
	spawnWithObject(14856, 3449, 2912, 0, 10, 0);// gemrock
	spawnWithObject(14855, 3448, 2912, 0, 10, 0);// gemrock
	spawnWithObject(14854, 3447, 2912, 0, 10, 0);// gemrock
	spawnWithObject(2097, 3444, 2913, 0, 10, 1);// Anvil
	spawnWithObject(2097, 3442, 2913, 0, 10, 1);// Anvil
	spawnWithObject(2097, 3440, 2913, 0, 10, 1);// Anvil
	spawnWithObject(2030, 3439, 2916, 0, 10, 1);// furnace
	spawnWithObject(26181, 3443, 2922, 0, 10, 1);// Range
	spawnWithObject(4309, 3445, 2919, 0, 10, 0);// Spinning wheel
	spawnWithObject(11601, 3439, 2922, 0, 10, 1);// Pottery
	spawnWithObject(11744, 3446, 2920, 0, 10, 3);// Bank
	spawnWithObject(4090, 3424, 2898, 0, 10, 3);// Blood altar
	spawnWithObject(11764, 3435, 2913, 0, 10, 1);// Mage Tree
	spawnWithObject(11764, 3432, 2917, 0, 10, 1);// Mage Tree
	spawnWithObject(11764, 3429, 2921, 0, 10, 1);// Mage Tree
	spawnWithObject(11764, 3432, 2903, 0, 10, 1);// Mage Tree
	spawnWithObject(11764, 3432, 2895, 0, 10, 1);// Mage Tree
	spawnWithObject(11764, 3435, 2887, 0, 10, 1);// Mage Tree
	spawnWithObject(11758, 3444, 2898, 0, 10, 1);// Yew Tree
	spawnWithObject(11758, 3444, 2893, 0, 10, 1);// Yew Tree
	spawnWithObject(11758, 3436, 2906, 0, 10, 1);// Yew Tree
	spawnWithObject(11762, 3434, 2921, 0, 10, 1);// Maple Tree
	spawnWithObject(11762, 3420, 2920, 0, 10, 1);// Maple Tree
	spawnWithObject(11762, 3420, 2911, 0, 10, 1);// Maple Tree
	spawnWithObject(11762, 3432, 2909, 0, 10, 1);// Maple Tree
	spawnWithObject(11759, 3413, 2913, 0, 10, 3);// willow
	spawnWithObject(11759, 3431, 2887, 0, 10, 1);// willow
	spawnWithObject(11759, 3429, 2910, 0, 10, 1);// willow
	spawnWithObject(11759, 3436, 2910, 0, 10, 1);// willow
	spawnWithObject(11756, 3438, 2928, 0, 10, 1);// oak
	spawnWithObject(11756, 3442, 2927, 0, 10, 1);// oak
	spawnWithObject(11756, 3413, 2922, 0, 10, 1);// oak
	spawnWithObject(11756, 3443, 2887, 0, 10, 1);// oak
	spawnWithObject(1276, 3445, 2928, 0, 10, 1);// tree
	spawnWithObject(1276, 3449, 2925, 0, 10, 1);// tree
	spawnWithObject(1276, 3409, 2918, 0, 10, 1);// tree
	spawnWithObject(1276, 3436, 2918, 0, 10, 1);// tree
	spawnWithObject(1276, 3432, 2898, 0, 10, 1);// tree
	spawnWithObject(7134, 3437, 2914, 0, 10, 1);// FLAX
	spawnWithObject(7134, 3436, 2917, 0, 10, 1);// FLAX
	spawnWithObject(7134, 3432, 2920, 0, 10, 1);// FLAX
	spawnWithObject(7134, 3437, 2921, 0, 10, 1);// FLAX
	spawnWithObject(7134, 3438, 2912, 0, 10, 1);// FLAX

	/** Home Area */

	spawnWithObject(11744, 3446, 2916, 0, 10, 3);// BANK PROFESSION AREA
	spawnWithObject(11744, 3446, 2915, 0, 10, 3);// BANK PROFESSION AREA

	/* Wilderness Resource Arena */
	spawnWithObject(14175, 3195, 3942, 0, 10, 3);
	spawnWithObject(14175, 3194, 3943, 0, 10, 3);
	spawnWithObject(14175, 3175, 3937, 0, 10, 3);
	spawnWithObject(14175, 3175, 3943, 0, 10, 3);

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
	spawnWithObject(1, 1866, 5323, 0, 10, 0);// Barrier
	spawnWithObject(1, 1865, 5323, 0, 10, 0);// Barrier
	spawnWithObject(11005, 1864, 5323, 0, 10, 1);// Barrier
	spawnWithObject(11005, 1863, 5323, 0, 10, 1);// Barrier
	spawnWithObject(1, 1862, 5323, 0, 10, 0);// Barrier
	spawnWithObject(1, 1861, 5323, 0, 10, 0);// Barrier
	spawnWithObject(11744, 1861, 5330, 0, 10, 0);// Barrier
	spawnWithObject(11744, 1862, 5330, 0, 10, 0);// Barrier
	spawnWithObject(11744, 1863, 5330, 0, 10, 0);// Barrier
	spawnWithObject(11744, 1864, 5330, 0, 10, 0);// Barrier
	spawnWithObject(11744, 1865, 5330, 0, 10, 0);// Barrier
	spawnWithObject(11744, 1866, 5330, 0, 10, 0);// Barrier

	/** Cultivation Areas */
	spawnWithObject(11744, 2804, 3463, 0, 10, 1);// Catweedy Banks
	spawnWithObject(11744, 3599, 3522, 0, 10, 0);// Banks
	spawnWithObject(11744, 3056, 3311, 0, 10, 0);// Banks
	spawnWithObject(11744, 2662, 3375, 0, 10, 0);// Banks

	spawnWithObject(11744, 2930, 4821, 0, 10, 0);// Essences

	/** Deleting Objects */
	delete(3079, 3501, 0);// Home gate
	delete(3080, 3501, 0);// Home gate
	delete(3445, 3554, 2);// Mercenary tower door
	remove(3431, 2891, 0);// BANKDOORS
	remove(3431, 2892, 0);// BANKDOORS
	deleteWithObject(3429, 2898, 0);// SHOPAREA
	deleteWithObject(3428, 2898, 0);// SHOPAREA
	deleteWithObject(3427, 2898, 0);// SHOPAREA /
	deleteWithObject(3424, 2897, 0);// SHOPAREA /
	deleteWithObject(3423, 2897, 0);// SHOPAREA
	deleteWithObject(3423, 2899, 0);// SHOPAREA
	deleteWithObject(3424, 2899, 0);// SHOPAREA
	remove(3425, 2901, 0);// SHOPAREA LADDER
	remove(3440, 2892, 0);// SHOPAREA2 LADDER
	deleteWithObject(3438, 2902, 0);// TELE CHEST AREA
	deleteWithObject(3444, 2901, 0);// TELE CHEST AREA
	deleteWithObject(3444, 2904, 0);// TELE CHEST AREA
	deleteWithObject(3444, 2905, 0);// TELE CHEST AREA
	deleteWithObject(3441, 2905, 0);// TELE CHEST AREA
	deleteWithObject(3439, 2899, 0);// TELE CHEST AREA

	deleteWithObject(3430, 2894, 0);// BANK STONERSHOP /

	delete(3426, 2916, 0);// SHOME SQUARE FOUNTAIN

	/** New Home */
	spawnWithObject(11744, 3430, 2930, 0, 10, 1);// Banks HOME
	spawnWithObject(11744, 3430, 2929, 0, 10, 1);// Banks HOME
	spawnWithObject(11744, 3430, 2928, 0, 10, 1);// Banks HOME
	spawnWithObject(11744, 3430, 2927, 0, 10, 1);// Banks HOME
	spawnWithObject(22472, 3430, 2902, 0, 10, 2);// Create Tabs
	spawnWithObject(8720, 3286, 3494, 0, 10, 2);// Chill
	spawnWithObject(4875, 3424, 2927, 0, 10, 5);// Food stall
	spawnWithObject(4876, 3423, 2927, 0, 10, 5);// General stall
	spawnWithObject(4874, 3422, 2928, 0, 10, 5);// Handiness stall
	spawnWithObject(4877, 3422, 2929, 0, 10, 5);// Mage stall
	spawnWithObject(4878, 3422, 2930, 0, 10, 5);// Scimitar stall

	spawnWithObject(13618, 3438, 2899, 0, 10, 0);// Wyvern teleport
	spawnWithObject(13619, 3440, 2899, 0, 10, 0);// Fountain of rune teleport
	spawnWithObject(2191, 3439, 2902, 0, 10, 0);// Crystal chest

	spawnWithObject(409, 3437, 2891, 0, 10, 3);// Altar HOME
	spawnWithObject(5249, 3428, 2912, 0, 10, 3);// Fyah HOME

	/** Webs */
	delete(3105, 3958, 0);
	delete(3106, 3958, 0);
	delete(3093, 3957, 0);
	delete(3095, 3957, 0);
	delete(3092, 3957, 0);
	delete(3158, 3951, 0);
	deleteWithObject(2543, 4715, 0);
	spawnWithObject(734, 3105, 3958, 0, 10, 3);
	spawnWithObject(734, 3106, 3958, 0, 10, 3);
	spawnWithObject(734, 3158, 3951, 0, 10, 1);
	spawnWithObject(734, 3093, 3957, 0, 10, 0);
	spawnWithObject(734, 3095, 3957, 0, 10, 0);
	delete(2543, 4715, 0);
	delete(2855, 3546, 0);
	delete(2854, 3546, 0);

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
	setClipToZero(3431, 2891, 0);
	setClipToZero(3431, 2892, 0);
	setClipToZero(3427, 2923, 0);
	setClipToZero(3426, 2923, 0);

	for (GameObject i : active) {
		send(i);
	}

	logger.info("All object spawns have been loaded successfully.");
	}

	private static Logger logger = Logger.getLogger(MapLoading.class.getSimpleName());

	private static final void delete(int x, int y, int z) {
	RSObject object = Region.getObject(x, y, z);

	if (Region.getDoor(x, y, z) != null) {
		Region.removeDoor(x, y, z);
	}

	if (object == null) {
		if (z > 0)
			active.add(new GameObject(2376, x, y, z, 10, 0));
		return;
	}

	MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());

	if ((object.getType() != 10) || (z > 0))
		active.add(new GameObject(2376, x, y, z, object.getType(), 0));
	}

	private static final void deleteWithObject(int x, int y, int z) {
	RSObject object = Region.getObject(x, y, z);

	if (Region.getDoor(x, y, z) != null) {
		Region.removeDoor(x, y, z);
	}

	if (object == null) {
		active.add(new GameObject(2376, x, y, z, 10, 0));
		return;
	}

	MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());

	active.add(new GameObject(2376, x, y, z, object.getType(), 0));
	}

	private static final void remove(int x, int y, int z) {
	RSObject object = Region.getObject(x, y, z);

	if (Region.getDoor(x, y, z) != null) {
		Region.removeDoor(x, y, z);
	}

	if (object == null) {
		active.add(new GameObject(2376, x, y, z, 10, 0));
		return;
	}

	MapLoading.removeObject(object.getId(), x, y, z, object.getType(), object.getFace());

	active.add(new GameObject(2376, x, y, z, object.getType(), 0));
	Region region = Region.getRegion(x, y);

	region.setClipToZero(x, y, z);
	}

	private static final void deleteWithObject(int x, int y, int z, int type) {
	active.add(new GameObject(2376, x, y, z, type, 0));
	}

	public static List<GameObject> getActive() {
	return active;
	}

	public static final GameObject getBlankObject(Location p) {
	return new GameObject(2376, p.getX(), p.getY(), p.getZ(), 10, 0, false);
	}

	public static GameObject getBlankObject(Location p, int type) {
	return new GameObject(2376, p.getX(), p.getY(), p.getZ(), type, 0, false);
	}

	public static GameObject getGameObject(int x, int y, int z) {
	int index = active.indexOf(new GameObject(x, y, z));

	if (index == -1) {
		return null;
	}

	return active.get(index);
	}

	public static Queue<GameObject> getSend() {
	return send;
	}

	public static boolean objectExists(Location location) {
	for (GameObject object : active) {
		if (location.equals(object.getLocation())) {
			return true;
		}
	}
	return false;
	}

	public static void process() {
	for (Iterator<GameObject> i = register.iterator(); i.hasNext();) {
		GameObject reg = i.next();
		active.remove(reg);
		active.add(reg);
		send.add(reg);

		i.remove();
	}
	}

	public static void queueSend(GameObject o) {
	send.add(o);
	}

	public static void register(GameObject o) {
	register.add(o);
	}

	public static void remove(GameObject o) {
	removeFromList(o);
	send.add(getBlankObject(o.getLocation(), o.getType()));
	}

	public static void remove2(GameObject o) {
	send.add(getBlankObject(o.getLocation(), o.getType()));
	}

	public static void removeFromList(GameObject o) {
	active.remove(o);
	}

	private static final void removeWithoutClip(int x, int y, int z, int type) {
	}

	public static void send(GameObject o) {
	for (Stoner stoner : World.getStoners())
		if ((stoner != null) && (stoner.isActive())) {
			if ((stoner.withinRegion(o.getLocation())) && (stoner.getLocation().getZ() % 4 == o.getLocation().getZ() % 4))
				stoner.getObjects().add(o);
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

	private static final void spawn(int id, int x, int y, int z, int type, int face) {
	MapLoading.addObject(false, id, x, y, z, type, face);
	}

	public static final void spawnWithObject(int id, Location location, int type, int face) {
	active.add(new GameObject(id, location.getX(), location.getY(), location.getZ(), type, face));
	MapLoading.addObject(false, id, location.getX(), location.getY(), location.getZ(), type, face);

	send(new GameObject(id, location.getX(), location.getY(), location.getZ(), type, face));
	}

	public static final void spawnWithObject(int id, int x, int y, int z, int type, int face) {
	active.add(new GameObject(id, x, y, z, type, face));
	MapLoading.addObject(false, id, x, y, z, type, face);

	send(new GameObject(id, x, y, z, type, face));
	}
}
