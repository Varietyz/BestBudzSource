package com.bestbudz.rs2.content.interfaces;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public abstract class InterfaceHandler {

  protected Stoner stoner;

  public InterfaceHandler(Stoner stoner) {
    this.stoner = stoner;
  }

  public static void writeText(InterfaceHandler interfacetext) {
    int line = interfacetext.startingLine();
    for (int i1 = 0; i1 < interfacetext.text().length; i1++) {
      interfacetext.stoner.send(new SendString(interfacetext.text()[i1], line++));
    }
  }

  protected abstract String[] text();

  protected abstract int startingLine();
}
