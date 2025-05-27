package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class MiscInterfaces {

	public static void startUp(Stoner stoner) {

	// Mage Tab - Normal
	stoner.send(new SendString("Home Teleport", 19220));
	stoner.send(new SendString("Teleport home", 19222));
	stoner.send(new SendString("Training Teleport", 19641));
	stoner.send(new SendString("Opens training interface", 19642));
	stoner.send(new SendString("Professioning Teleport", 19722));
	stoner.send(new SendString("Opens professioning interface", 19723));
	stoner.send(new SendString("SvS Teleport", 19803));
	stoner.send(new SendString("Opens SvS interface", 19804));
	stoner.send(new SendString("SvM Teleport", 19960));
	stoner.send(new SendString("Opens SvM interface", 19961));
	stoner.send(new SendString("Minigame Teleport", 20195));
	stoner.send(new SendString("Opens minigame interface", 20196));
	stoner.send(new SendString("Other Teleport", 20354));
	stoner.send(new SendString("Opens other interface", 20355));

	// Mage Tab - Ancients
	stoner.send(new SendString("Home Teleport", 21756));
	stoner.send(new SendString("Teleport home", 21757));
	stoner.send(new SendString("Training Teleport", 21833));
	stoner.send(new SendString("Opens training interface", 21834));
	stoner.send(new SendString("Professioning Teleport", 21933));
	stoner.send(new SendString("Opens professioning interface", 21934));
	stoner.send(new SendString("SvS Teleport", 22052));
	stoner.send(new SendString("Opens SvS interface", 22053));
	stoner.send(new SendString("SvM Teleport", 22123));
	stoner.send(new SendString("Opens SvM interface", 22124));
	stoner.send(new SendString("Minigame Teleport", 22232));
	stoner.send(new SendString("Opens minigame interface", 22233));
	stoner.send(new SendString("Other Teleport", 22307));
	stoner.send(new SendString("Opens other interface", 22308));

	}

}
