package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.Server;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;

public class QuestTab extends InterfaceHandler {

	private final String[] text = {
		"@whi@Time & Date: @yel@" + Utility.getCurrentServerTime(),

			 "@yel@" + Server.bestbudzDate(),

			  "@bla@_____________________",

			   "@whi@Employees Online: @gre@" + World.getStaff(),

				"@whi@Stoners </col>@gre@Online@whi@: @gre@" + World.getActiveStoners(),

				 "@bla@_____________________",

				  "@whi@Username: @yel@" + Utility.capitalizeFirstLetter(stoner.getUsername()),

				   "@whi@Rank: @gre@" + stoner.deterquarryIcon(stoner) + stoner.deterquarryRank(stoner),

		"@bla@_____________________",

					"@whi@[ </col>@yel@Check @whi@] My Points",

					"@whi@[ </col>@yel@Check @whi@] Tracked PvM Kills",

					 "@bla@_____________________",

					   "@bla@-@or2@FREQUENTLY USED@bla@-",
					 "@bla@_____________________",

						"@whi@[ </col>@cya@CHANGE @whi@] Type of Mage",

							  "@whi@[ </col>@cya@OPEN @whi@] Drop Table",

							"@whi@[ </col>@cya@ADVANCE @whi@] Professions",

						"@whi@[ </col>@gre@CHECK @whi@] Stoner shops",




					 "@bla@_____________________",
					   "@bla@-@or2@SHOPS@bla@-",
					 "@bla@_____________________",

						  "@whi@[ </col>@gre@OPEN @whi@] General Store",

						 "@whi@[ </col>@gre@OPEN @whi@] Pack store",

							 "@whi@[ </col>@gre@OPEN @whi@] Professioning shop",

							 "@whi@[ </col>@gre@OPEN @whi@] Cultivation shop",

							  "@whi@[ </col>@gre@OPEN @whi@] THC-hempistry shop",

						   "@whi@[ </col>@gre@OPEN @whi@] Close combat shop",

							"@whi@[ </col>@gre@OPEN @whi@] Sagittarius shop",

					 "@whi@[ </col>@gre@OPEN @whi@] Mages shop",

					  "@whi@[ </col>@gre@OPEN @whi@] Pure shop",

						  "@whi@[ </col>@gre@OPEN @whi@] Fashion shop",

					   "@whi@[ </col>@gre@BUY @whi@] Profession capes",

						"@whi@[ </col>@gre@BUY @whi@] Advancer capes",




					 "@bla@_____________________",
					 "@bla@-@or1@SHOPS2@bla@-",
					 "@bla@_____________________",
								"@whi@[ </col>@yel@OPEN @whi@] Chill points",

								 "@whi@[ </col>@yel@OPEN @whi@] Weed protect points",

								  "@whi@[ </col>@yel@BUY @whi@] Graceful marks",

								   "@whi@[ </col>@yel@OPEN @whi@] Achievement points",

									  "@whi@[ </col>@yel@OPEN @whi@] Advance points",

										"@whi@[ </col>@yel@OPEN @whi@] Mercenary points",

					"@whi@[ </col>@yel@OPEN @whi@] Bounty points",





					 "@bla@_____________________",

					 "@bla@-@or3@CRAFTER@bla@-",

					 "@bla@_____________________",

						  "@whi@[ </col>@mag@CREATE @whi@] Dragon fireshield",

						   "@whi@[ </col>@mag@CREATE @whi@] Jewlery",

								  "@whi@[ </col>@mag@TAN @whi@] Hides",

							   "@whi@[ </col>@mag@DECANT @whi@] Potions",

					 "@bla@_____________________",

					 "@bla@-@or2@MANAGE@bla@-",

					 "@bla@_____________________",

									 "@whi@[ </col>@cya@SET @whi@] Deluxe title management",

							 "@whi@[ </col>@cya@RESET @whi@] Combat stats",

								"@whi@[ </col>@cya@GET @whi@] Plastic Surgery",

					  "@whi@[ </col>@gre@RECHARGE @whi@] Necromance",

					   "@whi@[ </col>@red@SKULL @whi@] Self",


					 "@bla@_____________________",

					 "@bla@-@or1@TASKS@bla@-",

					 "@bla@_____________________",


								   "@whi@[ </col>@yel@TASK @whi@] Mercenary",

								   "@whi@[ </col>@yel@TASK @whi@] Boss Mercenary",

					 "@bla@_____________________",

					 "@bla@-@or3@EXTRAS@bla@-",

					 "@bla@_____________________",




									 "@whi@[ </col>@mag@TRAVEL @whi@] Traveling Agency",

					"@whi@[ </col>@mag@OPEN @whi@] Misery Box game",

									"@whi@[ </col>@mag@OBTAIN @whi@] Achievement cape",


					 "@bla@_____________________",

					 "@bla@-@or2@ALTARS@bla@-",

					 "@bla@_____________________",

						"@whi@[ </col>@gre@AIR @whi@] Altar",

						 "@whi@[ </col>@gre@MIND @whi@] Altar",

						 "@whi@[ </col>@gre@WATER @whi@] Altar",

						  "@whi@[ </col>@gre@EARTH @whi@] Altar",

						  "@whi@[ </col>@gre@FIRE @whi@] Altar",

						  "@whi@[ </col>@gre@BODY @whi@] Altar",

						  "@whi@[ </col>@gre@COSMIC @whi@] Altar",

						   "@whi@[ </col>@gre@CHAOS @whi@] Altar",

						   "@whi@[ </col>@gre@NATURE @whi@] Altar",

							"@whi@[ </col>@gre@LAW @whi@] Altar",

							"@whi@[ </col>@gre@DEATH @whi@] Altar",

							"@bla@_____________________", };

	public QuestTab(Stoner stoner) {
	super(stoner);
	color(16, 0xC71C1C);
	color(17, 0xC71C1C);
	}

	public void color(int id, int color) {
	stoner.send(new SendColor(startingLine() + id, color));
	}

	@Override
	protected String[] text() {
	return text;
	}

	@Override
	protected int startingLine() {
	return 29501;
	}

}
