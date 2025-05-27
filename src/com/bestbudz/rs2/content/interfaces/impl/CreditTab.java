package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class CreditTab extends InterfaceHandler {

  private final String[] text = {
    "Open CannaCredits shops",
    "Inf. Special Assault (@gre@1M</col>)",
    "@gre@Tap Tycoon</col>",
    "Open Bank (@yel@30</col>)",
    "Unlock Free Teleports (@gre@350</col>)",
    "Unlock Disease Immunity (@gre@270</col>)",
    "Remove Teleblock (@cya@200</col>)",
    "Expensive Easy Shop (@gre@SOON</col>)",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
  };

  public CreditTab(Stoner stoner) {
    super(stoner);
    if (stoner.isCreditUnlocked(CreditPurchase.SPECIAL_ASSAULT)) {
      text[1] = "<str>Inf. Spec (@bla@1.000.000</col>)";
    }
    if (stoner.isCreditUnlocked(CreditPurchase.FREE_TELEPORTS)) {
      text[4] = "<str>Unlock Free Teleports (@bla@350</col>)";
    }
    if (stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
      text[5] = "<str>Unlock Disease Immunity (@bla@270</col>)";
    }
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 52531;
  }
}
