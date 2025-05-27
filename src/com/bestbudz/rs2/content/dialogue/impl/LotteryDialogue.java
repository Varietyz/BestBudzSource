package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.gambling.Lottery;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Handles Gambler dialogue
 * 
 * @author Jaybane
 *
 */
public class LotteryDialogue extends Dialogue {

    public LotteryDialogue(Stoner stoner) {
    this.stoner = stoner;
    }

    @Override
    public boolean clickButton(int id) {

    switch (id) {

    case DialogueConstants.OPTIONS_4_1:
        DialogueManager.sendStatement(stoner, "Coming soon!");
        break;

    case DialogueConstants.OPTIONS_4_2:
        DialogueManager.sendInformationBox(stoner, "Lottery Information", "</col>Current Pot: @blu@" + Utility.format(Lottery.getPot()), "</col>Pot Limit: @blu@" + Utility.format(Lottery.getLimit()), "</col>Stoners: @blu@" + Lottery.getStoners(), "</col>You are " + (Lottery.hasEntered(stoner) ? "@gre@entered</col>" : "@red@not entered</col>") + " in the lottery");
        break;

    case DialogueConstants.OPTIONS_4_3:
        Lottery.enterLotter(stoner);
        stoner.send(new SendRemoveInterfaces());
        break;

    case DialogueConstants.OPTIONS_4_4:
        stoner.start(new GamblerDialogue(stoner));
        break;

    }

    return false;
    }

    @Override
    public void execute() {

    switch (next) {

    case 0:
        DialogueManager.sendOption(stoner, "Lottery guide", "Current pot", "Enter lottery", "Nevermind");
        break;

    }

    }

}