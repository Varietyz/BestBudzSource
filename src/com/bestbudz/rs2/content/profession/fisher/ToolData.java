package com.bestbudz.rs2.content.profession.fisher;

import java.util.HashMap;
import java.util.Map;

public class ToolData {

	public static enum Tools {
		SMALL_NET(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		BIG_NET(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		CRAYFISH_CAGE(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		FISHER_ROD(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		FLYFISHER_ROD(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		KARAMBWAN_POT(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		HARPOON(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 }),
		LOBSTER_POT(6577, 1768, new short[] { 317, 3150, 321, 5004, 7994, 353, 341, 363, 327, 345, 349, 3379, 5001, 2148, 335, 331, 3142, 359, 371, 377, 534, 11212, 1779 });

		public static Tools forId(int id) {
		return tools.get(Integer.valueOf(id));
		}

		private int toolId;
		private int animation;
		private short[] outcomes;

		private static Map<Integer, Tools> tools = new HashMap<Integer, Tools>();

		public static final void declare() {
		for (Tools tool : values())
			tools.put(Integer.valueOf(tool.getToolId()), tool);
		}

		private Tools(int toolId, int animation, short[] outcomes) {
		this.toolId = toolId;
		this.outcomes = outcomes;
		this.animation = animation;
		}

		public int getAnimationId() {
		return animation;
		}

		public short[] getOutcomes() {
		return outcomes;
		}

		public int getToolId() {
		return toolId;
		}
	}
}
