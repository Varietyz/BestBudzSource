package com.bestbudz.rs2.content.interfaces;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public abstract class InterfaceHandler {

  protected Stoner stoner;

  public InterfaceHandler(Stoner stoner) {
    this.stoner = stoner;
  }

	public static void writeText(InterfaceHandler handler) {
		int frame = handler.startingLine();
		for (String line : handler.text()) {
			handler.stoner.send(new SendString(line, frame++));
			//System.out.println("Sending text to frame " + frame + ": " + line);
		}
	}

	protected abstract String[] text();

  protected abstract int startingLine();
}
