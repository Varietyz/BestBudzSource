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
  private static Logger logger = Logger.getLogger(MapLoading.class.getSimpleName());

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
		// Clear existing active objects
		for (GameObject obj : active) {
			send(getBlankObject(obj.getLocation()));
		}
		active.clear();

		deleteWithObject(3444, 2909, 0);
		deleteWithObject(3440, 2909, 0);
		deleteWithObject(3438, 2910, 0);
		deleteWithObject(3446, 2911, 0);
		deleteWithObject(3440, 2922, 0);
		deleteWithObject(3443, 2922, 0);
		deleteWithObject(3449, 2912, 0);
		deleteWithObject(3445, 2918, 0);
		deleteWithObject(3439, 2918, 0);

		spawnWithObject(13709, 3438, 2911, 0, 10, 0);
		spawnWithObject(13712, 3438, 2910, 0, 10, 0);
		spawnWithObject(13711, 3439, 2909, 0, 10, 0);
		spawnWithObject(13714, 3440, 2909, 0, 10, 0);
		spawnWithObject(13707, 3443, 2909, 0, 10, 0);
		spawnWithObject(13718, 3444, 2909, 0, 10, 0);
		spawnWithObject(14168, 3447, 2909, 0, 10, 0);
		spawnWithObject(14175, 3448, 2909, 0, 10, 0);

		spawnWithObject(14856, 3449, 2912, 0, 10, 0);
		spawnWithObject(14855, 3448, 2912, 0, 10, 0);
		spawnWithObject(14854, 3447, 2912, 0, 10, 0);

		spawnWithObject(2097, 3443, 2913, 0, 10, 1); // ANVIL
		spawnWithObject(2097, 3440, 2913, 0, 10, 1); // ANVIL
		spawnWithObject(2030, 3439, 2916, 0, 10, 1);

		spawnWithObject(26181, 3443, 2922, 0, 10, 1);
		spawnWithObject(4309, 3445, 2919, 0, 10, 0);
		spawnWithObject(11601, 3439, 2922, 0, 10, 1);
		spawnWithObject(11744, 3446, 2920, 0, 10, 3);
		spawnWithObject(4090, 3424, 2898, 0, 10, 3);

		spawnWithObject(11758, 3444, 2898, 0, 10, 1);
		spawnWithObject(11758, 3444, 2893, 0, 10, 1);
		spawnWithObject(11758, 3436, 2906, 0, 10, 1);

		spawnWithObject(1276, 3445, 2928, 0, 10, 1);
		spawnWithObject(1276, 3449, 2925, 0, 10, 1);
		spawnWithObject(1276, 3409, 2918, 0, 10, 1);
		spawnWithObject(1276, 3436, 2918, 0, 10, 1);
		spawnWithObject(1276, 3432, 2898, 0, 10, 1);

		// Decorative or map-specific objects (11764 group)
		int[][] coords11764 = {
			{3435, 2913}, {3432, 2917}, {3429, 2921},
			{3432, 2903}, {3432, 2895}, {3435, 2887}
		};
		for (int[] pos : coords11764) {
			spawnWithObject(11764, pos[0], pos[1], 0, 10, 1);
		}

		// Multi-spot repeated types
		spawnWithObject(11762, 3434, 2921, 0, 10, 1);
		spawnWithObject(11762, 3420, 2920, 0, 10, 1);
		spawnWithObject(11762, 3420, 2911, 0, 10, 1);
		spawnWithObject(11762, 3432, 2909, 0, 10, 1);

		spawnWithObject(11759, 3413, 2913, 0, 10, 3);
		spawnWithObject(11759, 3431, 2887, 0, 10, 1);
		spawnWithObject(11759, 3429, 2910, 0, 10, 1);
		spawnWithObject(11759, 3436, 2910, 0, 10, 1);

		spawnWithObject(11756, 3438, 2928, 0, 10, 1);
		spawnWithObject(11756, 3442, 2927, 0, 10, 1);
		spawnWithObject(11756, 3413, 2922, 0, 10, 1);
		spawnWithObject(11756, 3443, 2887, 0, 10, 1);

		// Portal-like objects
		spawnWithObject(7134, 3437, 2914, 0, 10, 1);
		spawnWithObject(7134, 3436, 2917, 0, 10, 1);
		spawnWithObject(7134, 3432, 2920, 0, 10, 1);
		spawnWithObject(7134, 3437, 2921, 0, 10, 1);
		spawnWithObject(7134, 3438, 2912, 0, 10, 1);

		// Custom areas - group 1
		spawnWithObject(11744, 3446, 2916, 0, 10, 3);
		spawnWithObject(11744, 3446, 2915, 0, 10, 3);
		spawnWithObject(14175, 3195, 3942, 0, 10, 3);
		spawnWithObject(14175, 3194, 3943, 0, 10, 3);
		spawnWithObject(14175, 3175, 3937, 0, 10, 3);
		spawnWithObject(14175, 3175, 3943, 0, 10, 3);

		// Cleanup area - group 2
		int[][] deleteCoords = {
			{1863, 5328}, {1863, 5326}, {1863, 5323},
			{1862, 5327}, {1862, 5326}, {1862, 5325},
			{1865, 5325}, {1863, 5321}, {1865, 5321},
			{1865, 5323}, {1863, 5319}, {1862, 5319},
			{1863, 5317}, {1865, 5319}, {1862, 5321},
			{1862, 5323}
		};
		for (int[] coord : deleteCoords) {
			deleteWithObject(coord[0], coord[1], 0);
		}

		// Replacements for group 2
		spawnWithObject(1, 1866, 5323, 0, 10, 0);
		spawnWithObject(1, 1865, 5323, 0, 10, 0);
		spawnWithObject(11005, 1864, 5323, 0, 10, 1);
		spawnWithObject(11005, 1863, 5323, 0, 10, 1);
		spawnWithObject(1, 1862, 5323, 0, 10, 0);
		spawnWithObject(1, 1861, 5323, 0, 10, 0);
		for (int x = 1861; x <= 1866; x++) {
			spawnWithObject(11744, x, 5330, 0, 10, 0);
		}

		// Other regions
		spawnWithObject(11744, 2804, 3463, 0, 10, 1);
		spawnWithObject(11744, 3599, 3522, 0, 10, 0);
		spawnWithObject(11744, 3056, 3311, 0, 10, 0);
		spawnWithObject(11744, 2662, 3375, 0, 10, 0);
		spawnWithObject(11744, 2930, 4821, 0, 10, 0);

		// Legacy cleanup - wilderness/legacy tiles
		delete(3079, 3501, 0);
		delete(3080, 3501, 0);
		delete(3445, 3554, 2);

		// Misc removals
		remove(3431, 2891, 0);
		remove(3431, 2892, 0);
		deleteWithObject(3429, 2898, 0);
		deleteWithObject(3428, 2898, 0);
		deleteWithObject(3427, 2898, 0);
		deleteWithObject(3424, 2897, 0);
		deleteWithObject(3423, 2897, 0);
		deleteWithObject(3423, 2899, 0);
		deleteWithObject(3424, 2899, 0);
		remove(3425, 2901, 0);
		remove(3440, 2892, 0);
		deleteWithObject(3438, 2902, 0);
		deleteWithObject(3444, 2901, 0);
		deleteWithObject(3444, 2904, 0);
		deleteWithObject(3444, 2905, 0);
	}


  private static final void delete(int x, int y, int z) {
    RSObject object = Region.getObject(x, y, z);

    if (Region.getDoor(x, y, z) != null) {
      Region.removeDoor(x, y, z);
    }

    if (object == null) {
      if (z > 0) active.add(new GameObject(2376, x, y, z, 10, 0));
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
    while (!register.isEmpty()) {
      var obj = register.pollFirst();
      if (obj == null) continue;

      active.remove(obj); // ensures no duplicates
      active.add(obj); // re-registers it
      send.add(obj); // queues for sending
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

  private static final void removeWithoutClip(int x, int y, int z, int type) {}

  public static void send(GameObject o) {
    for (Stoner stoner : World.getStoners())
      if ((stoner != null) && (stoner.isActive())) {
        if ((stoner.withinRegion(o.getLocation()))
            && (stoner.getLocation().getZ() % 4 == o.getLocation().getZ() % 4))
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
