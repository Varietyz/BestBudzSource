package com.bestbudz.rs2.content.moderation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.gambling.Gambling;

public class TicketSystem {

  public static void openTicket() {}

  public static void sendTicket() {}

  public static void saveGambling(
      String stoner, String title, String paragraph1, String paragraph2) {
    try {
      File file = new File("./data/tickets/" + stoner + ".txt");
      BufferedWriter out = new BufferedWriter(new FileWriter(file));

      out.newLine();
      out.write("Date: " + Utility.getCurrentServerTime());
      out.newLine();
      out.write("Stoner: " + stoner);
      out.newLine();
      out.write("Title: " + title);
      out.newLine();
      out.write("Info: " + paragraph1);
      out.newLine();
      out.write(paragraph2);
      out.newLine();
      out.write("--------------------------");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void loadGambling(String stoner, String title) {
    try {
      File file =
          new File("./data/tickets/" + stoner + " - " + Utility.getCurrentServerTime() + ".txt");
      if (!file.exists()) {
        return;
      }
      file.delete();
      BufferedReader in = new BufferedReader(new FileReader(file));

      in.lines();

      long money = Long.parseLong(in.readLine());
      Gambling.MONEY_TRACKER = money;

      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
