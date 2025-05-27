package com.bestbudz.rs2.content.cluescroll;

import java.util.Objects;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Handles the clue the stoner reads after clicking a clue scroll.
 * 
 * @author Jaybane
 *
 */
public final class Clue {

	/**
	 * A representation of the type of clue a scroll is.
	 * 
	 * @author Jaybane
	 *
	 */
	public static enum ClueType {

		/**
		 * A map clue.
		 */
		MAP,

		/**
		 * A cryptic clue.
		 */
		CRYPTIC,

		/**
		 * An emote clue.
		 */
		EMOTE,

		/**
		 * A coordinate clue.
		 */
		COORDINATE
	}

	/**
	 * The clue type for this clue.
	 */
	private final ClueType clueType;

	/**
	 * The data required to display the clue.
	 */
	private final Object[] data;

	/**
	 * Constructs a new Clue.
	 * 
	 * @param clueType
	 *                     - The type of clue.
	 * @param data
	 *                     - The data required to display the clue.
	 */
	public Clue(ClueType clueType, Object... data) {
	this.clueType = clueType;
	this.data = Objects.requireNonNull(data);
	}

	/**
	 * Displays the clue for a stoner.
	 * 
	 * @param stoner
	 *                   - The stoner to display the clue for.
	 */
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

}