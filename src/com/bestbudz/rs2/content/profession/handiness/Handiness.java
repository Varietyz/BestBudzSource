package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterXInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class Handiness {

	private static int[][] LEATHER_ARMOR_IDS = { { 33187, 1, 1129 }, { 33186, 5, 1129 }, { 33185, 10, 1129 }, { 33190, 1, 1059 }, { 33189, 5, 1059 }, { 33188, 10, 1059 }, { 33193, 1, 1061 }, { 33192, 5, 1061 }, { 33193, 10, 1061 }, { 33194, 1, 1063 }, { 33195, 5, 1063 }, { 33196, 10, 1063 }, { 33197, 1, 1095 }, { 33198, 5, 1095 }, { 33199, 10, 1095 }, { 33200, 1, 1169 }, { 33201, 5, 1169 }, { 33202, 10, 1169 }, { 33203, 1, 1167 }, { 33204, 5, 1167 }, { 33205, 10, 1167 } };

	public static boolean handleHandinessByButtons(Stoner stoner, int buttonId) {
	switch (buttonId) {
	case 6211:
		if (stoner.getAttributes().get("handinessType") != null) {
			switch (((HandinessType) stoner.getAttributes().get("handinessType"))) {
			case WHEEL_SPINNING:
				if (stoner.getAttributes().get("spinnable") != null) {
					TaskQueue.queue(new WheelSpinning(stoner, (short) 28, (Spinnable) stoner.getAttributes().get("spinnable")));
					stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
					stoner.getAttributes().remove("spinnable");
					stoner.getAttributes().remove("handinessType");
				}
				break;
			default:
				return true;
			}

			stoner.getAttributes().remove("handinessType");
		}
		break;
	case 10238:
		if (stoner.getAttributes().get("handinessType") != null) {
			switch (((HandinessType) stoner.getAttributes().get("handinessType"))) {
			case WHEEL_SPINNING:
				TaskQueue.queue(new WheelSpinning(stoner, (short) 5, (Spinnable) stoner.getAttributes().get("spinnable")));
				stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
				stoner.getAttributes().remove("spinnable");
				break;
			default:
				return true;
			}

			stoner.getAttributes().remove("handinessType");
		}
		break;
	case 10239:
		if (stoner.getAttributes().get("handinessType") != null) {
			switch (((HandinessType) stoner.getAttributes().get("handinessType"))) {
			case WHEEL_SPINNING:
				TaskQueue.queue(new WheelSpinning(stoner, (short) 1, (Spinnable) stoner.getAttributes().get("spinnable")));
				stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
				stoner.getAttributes().remove("spinnable");
				stoner.getAttributes().remove("handinessType");
				return true;
			default:
				stoner.getAttributes().remove("handinessType");
				return true;
			}
		} else {
			return false;
		}
	case 6212:
		stoner.getClient().queueOutgoingPacket(new SendEnterXInterface(8886, 0));
		break;
	case 34186:
		stoner.getClient().queueOutgoingPacket(new SendEnterXInterface(8890, 0));
		break;
	case 34190:
		stoner.getClient().queueOutgoingPacket(new SendEnterXInterface(8894, 0));
		return true;
	}

	for (int[] i : LEATHER_ARMOR_IDS) {
		if (i[0] == buttonId) {
			stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			TaskQueue.queue(new ArmourCreation(stoner, (short) i[1], Craftable.forReward(i[2])));
			stoner.getAttributes().remove("handinessHide");
			stoner.getAttributes().remove("handinessType");
			return true;
		}
	}
	return false;
	}
}
