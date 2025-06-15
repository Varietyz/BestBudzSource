package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.entity.InterfaceManager;
import com.bestbudz.rs2.entity.object.LocalObjects;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerAssistant;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;

/**
 * Handles player interactions with the world, interfaces, objects, and controllers
 */
public class StonerInteraction {
	private final Stoner stoner;
	private final StonerAssistant stonerAssistant;
	private final LocalObjects objects;
	private final InterfaceManager interfaceManager;

	// Dialogue system
	private Dialogue dialogue = null;

	// Controller system
	private Controller controller = ControllerManager.DEFAULT_CONTROLLER;

	// Cracking state (for certain activities)
	public boolean isCracking;

	public StonerInteraction(Stoner stoner) {
		this.stoner = stoner;
		this.stonerAssistant = new StonerAssistant(stoner);
		this.objects = new LocalObjects(stoner);
		this.interfaceManager = new InterfaceManager();
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return; // Pets don't need interaction processing
		}
		// Any periodic interaction processing can go here
	}

	/**
	 * Starts a dialogue with the player
	 */
	public void start(Dialogue dialogue) {
		this.dialogue = dialogue;
		if (dialogue != null) {
			dialogue.setNext(0);
			dialogue.setStoner(stoner);
			dialogue.execute();
		} else if (stoner.getAttributes().get("pauserandom") != null) {
			stoner.getAttributes().remove("pauserandom");
		}
	}

	/**
	 * Sets the controller without initialization
	 */
	public boolean setControllerNoInit(Controller controller) {
		this.controller = controller;
		return true;
	}

	/**
	 * Sets the controller with initialization
	 */
	public boolean setController(Controller controller) {
		this.controller = controller;
		controller.onControllerInit(stoner);
		return true;
	}

	/**
	 * Called when controller finishes
	 */
	public void onControllerFinish() {
		controller = ControllerManager.DEFAULT_CONTROLLER;
	}

	/**
	 * Checks if the player can save (based on controller)
	 */
	public boolean canSave() {
		return controller.canSave();
	}

	// Getters and setters
	public StonerAssistant getAssistant() { return stonerAssistant; }
	public LocalObjects getObjects() { return objects; }
	public InterfaceManager getInterfaceManager() { return interfaceManager; }

	public Dialogue getDialogue() { return dialogue; }
	public void setDialogue(Dialogue dialogue) { this.dialogue = dialogue; }

	public Controller getController() {
		if (controller == null) {
			setController(ControllerManager.DEFAULT_CONTROLLER);
		}
		return controller;
	}

	public boolean isCracking() { return isCracking; }
	public void setCracking(boolean cracking) { this.isCracking = cracking; }
}