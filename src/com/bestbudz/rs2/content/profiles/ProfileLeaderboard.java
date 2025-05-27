package com.bestbudz.rs2.content.profiles;

import java.util.ArrayList;
import java.util.Collections;

import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class ProfileLeaderboard implements Comparable<Object> {

  public static String sort = "";
  String name = "";
  double score = 0;

  public ProfileLeaderboard(String name, double score) {
    this.name = name;
    this.score = score;
  }

  public static void open(Stoner stoner, String sort) {
    ArrayList<ProfileLeaderboard> leaderboard = new ArrayList<ProfileLeaderboard>();
    stoner.send(new SendInterface(47400));

    for (int i = 0; i < 25; i++) {
      stoner.send(new SendString("", 51551 + i));
    }
    if (sort.equalsIgnoreCase("Views")) {
      sort = "Views";
      for (Stoner stoners : World.getStoners()) {
        if (stoners == null) continue;
        leaderboard.add(new ProfileLeaderboard(stoners.getUsername(), stoners.getProfileViews()));
        updateString(stoner, "views");
      }
    }
    if (sort.equalsIgnoreCase("Likes")) {
      sort = "Likes";
      for (Stoner stoners : World.getStoners()) {
        if (stoners == null) continue;
        leaderboard.add(new ProfileLeaderboard(stoners.getUsername(), stoners.getLikes()));
        updateString(stoner, "likes");
      }
    }
    if (sort.equalsIgnoreCase("Dislikes")) {
      sort = "Dislikes";
      for (Stoner stoners : World.getStoners()) {
        if (stoners == null) continue;
        leaderboard.add(new ProfileLeaderboard(stoners.getUsername(), stoners.getDislikes()));
        updateString(stoner, "dislikes");
      }
    }
    if (sort.equalsIgnoreCase("Ratio")) {
      sort = "Ratio";
      for (Stoner stoners : World.getStoners()) {
        if (stoners == null) continue;
        double ratio =
            (stoner.getLikes()
                / (double) (stoner.getDislikes() + stoner.getLikes())
                * stoner.getLikes());
        leaderboard.add(new ProfileLeaderboard(stoners.getUsername(), ratio));
        updateString(stoner, "ratio");
      }
    }
    Collections.sort(leaderboard);
    for (int i = 1; i <= leaderboard.size(); i++) {
      stoner.send(
          new SendString(
              "@lre@" + i + ") " + leaderboard.get((leaderboard.size() - i)), 51550 + i));
    }
    leaderboard.clear();
  }

  public static void updateString(Stoner stoner, String type) {
    switch (type) {
      case "views":
        stoner.send(new SendString("@gre@Views", 47418));
        stoner.send(new SendString("</col>Likes", 47419));
        stoner.send(new SendString("</col>Dislikes", 47420));
        stoner.send(new SendString("</col>Ratio", 47421));
        break;
      case "likes":
        stoner.send(new SendString("</col>Views", 47418));
        stoner.send(new SendString("@gre@Likes", 47419));
        stoner.send(new SendString("</col>Dislikes", 47420));
        stoner.send(new SendString("</col>Ratio", 47421));
        break;
      case "dislikes":
        stoner.send(new SendString("</col>Views", 47418));
        stoner.send(new SendString("</col>Likes", 47419));
        stoner.send(new SendString("@gre@Dislikes", 47420));
        stoner.send(new SendString("</col>Ratio", 47421));
        break;
      case "ratio":
        stoner.send(new SendString("</col>Views", 47418));
        stoner.send(new SendString("</col>Likes", 47419));
        stoner.send(new SendString("</col>Dislikes", 47420));
        stoner.send(new SendString("@gre@Ratio", 47421));
        break;
    }
  }

  @Override
  public String toString() {
    return String.format("%s %s: @gre@%s", name, sort, score);
  }

  public int compareTo(Object o1) {
    if (this.score == ((ProfileLeaderboard) o1).score) {
      return 0;
    } else if ((this.score) > ((ProfileLeaderboard) o1).score) {
      return 1;
    } else {
      return -1;
    }
  }
}
