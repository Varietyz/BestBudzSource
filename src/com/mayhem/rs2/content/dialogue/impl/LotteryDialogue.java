package com.mayhem.rs2.content.dialogue.impl;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.dialogue.Dialogue;
import com.mayhem.rs2.content.dialogue.DialogueConstants;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.gambling.Lottery;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Handles Gambler dialogue
 * @author Daniel
 *
 */
public class LotteryDialogue extends Dialogue {
	
	public LotteryDialogue(Player player) {
		this.player = player;
	}

    @Override
    public boolean clickButton(int id) {
    
       switch (id) {
    	   
       case DialogueConstants.OPTIONS_4_1:
    	   DialogueManager.sendStatement(player, "Coming soon!");
    	   break;
    	   
       case DialogueConstants.OPTIONS_4_2:
    	   DialogueManager.sendInformationBox(player, "Lottery Information", "</col>Current Pot: @blu@" + Utility.format(Lottery.getPot()), "</col>Pot Limit: @blu@" + Utility.format(Lottery.getLimit()), "</col>Players: @blu@" + Lottery.getPlayers() , "</col>You are " + (Lottery.hasEntered(player) ? "@gre@entered</col>" : "@red@not entered</col>") + " in the lottery");
    	   break;
    	   
       case DialogueConstants.OPTIONS_4_3:
    	   Lottery.enterLotter(player);
    	   player.send(new SendRemoveInterfaces());
    	   break;
    	   
       case DialogueConstants.OPTIONS_4_4:
    	   player.start(new GamblerDialogue(player));
    	   break;
       
       }
       
        return false;
    }

    @Override
    public void execute() {
    
       switch (next) {
       
       case 0:
    	   DialogueManager.sendOption(player, "Lottery guide", "Current pot", "Enter lottery", "Nevermind"); 	
    	   break;
    	   
       
       }
       
    }

}