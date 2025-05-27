package com.bestbudz.rs2.content.cluescroll;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.Objects;

public final class Clue {

	private final ClueType clueType;
	private final Object[] data;

	public Clue(ClueType clueType, Object... data) {
	this.clueType = clueType;
	this.data = Objects.requireNonNull(data);
	}

	public void display(Stoner stoner) {
	switch (clueType) {

	case MAP:
		stoner.send(new SendInterface(Integer.parseInt(String.valueOf(data[0]))));
		break;

	case COORDINATE:
	case CRYPTIC:
		stoner.send(new SendRemoveInterfaces());
		stoner.send(new SendInterface(6965));

		for (int i = 6968; i <= 6975; i++) {
			stoner.send(new SendString(String.valueOf(data[i - 6968]), i));
		}
		break;

	case EMOTE:
		stoner.send(new SendRemoveInterfaces());
		stoner.send(new SendInterface(6965));

		for (int i = 6968; i <= 6975; i++) {
			stoner.send(new SendString(String.valueOf(data[1 + i - 6968]), i));
		}
		break;

	}
	}

	public ClueType getClueType() {
	return clueType;
	}

	public enum ClueType {

		MAP,

		CRYPTIC,

		EMOTE,

		COORDINATE
	}

}