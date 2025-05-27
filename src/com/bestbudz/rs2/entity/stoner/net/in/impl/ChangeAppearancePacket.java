package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ChangeAppearancePacket extends IncomingPacket {

  private static final int[][] MALE_VALUES = {
    {0, 8}, {10, 17}, {18, 25}, {26, 31}, {33, 34}, {36, 40}, {42, 43},
  };

  private static final int[][] FEMALE_VALUES = {
    {45, 54}, {-1, -1}, {56, 60}, {61, 65}, {67, 68}, {70, 77}, {79, 80},
  };

  private static final int[][] IDK_COLORS = {
    {
      6798, 107, 10283, 16, 4797, 7744, 5799, 4634, -31839, 22433, 2983, -11343, 8, 5281, 10438,
      3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701, -14010, 1000, 7114, 6873, 6400,
      6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700,
      36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938,
      96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114,
      3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131
    },
    {
      8741, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341, 16578,
      -30533, 25239, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701,
      -14010, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075,
      2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860,
      35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652,
      926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821,
      23421, 9583, 123456, 28131
    },
    {
      25238, 8742, 12, -1506, -22374, 7735, 8404, 1701, -27106, 24094, 10153, -8915, 4783, 1341,
      16578, -30533, 8, 5281, 10438, 3650, -27322, -21845, 200, 571, 908, 21830, 28946, -15701,
      -14010, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375, 660, 21662, 5738, 675, 1075,
      2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831, 33, 17350, 38693, 8759, 13860,
      35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933, 689484, 50000, 61093, 5652,
      926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832, 33823, 271833, 869308091, 12821,
      23421, 9583, 123456, 28131
    },
    {
      4626, 11146, 6439, 12, 4758, 10270, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400, 325, 375,
      660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860, 86933, 27831,
      33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500, 54783, 58933,
      689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823, 689385, 67832,
      33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131
    },
    {
      4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574, 1000, 7114, 6873, 6400, 6837, 6850, 350, 400,
      325, 375, 660, 21662, 5738, 675, 1075, 2130, 1050, 8776, 7833, 3700, 36133, 4960, 19860,
      86933, 27831, 33, 17350, 38693, 8759, 13860, 35321, 43297, 167550, 5938, 96993, 49863, 49500,
      54783, 58933, 689484, 50000, 61093, 5652, 926, 79839683, 2219, 7114, 3982, 6073, 49823,
      689385, 67832, 33823, 271833, 869308091, 12821, 23421, 9583, 123456, 28131
    }
  };

  private static final int[][] ALLOWED_COLORS = {
    {0, IDK_COLORS[0].length},
    {0, IDK_COLORS[1].length},
    {0, IDK_COLORS[2].length},
    {0, IDK_COLORS[3].length},
    {0, IDK_COLORS[4].length}
  };

  public static void setToDefault(Stoner p) {
    p.setGender((byte) 0);
    p.getAppearance()[0] = 1;
    p.getAppearance()[1] = 19;
    p.getAppearance()[2] = 27;
    p.getAppearance()[3] = 34;
    p.getAppearance()[4] = 40;
    p.getAppearance()[5] = 43;
    p.getAppearance()[6] = 11;
  }

  public static boolean validate(int[] app, byte[] col, byte gender) {
    if (gender == 0) {
      for (int i = 0; i < app.length; i++)
        if ((app[i] < MALE_VALUES[i][0]) || (app[i] > MALE_VALUES[i][1])) return false;
    } else if (gender == 1) {
      for (int i = 0; i < app.length; i++)
        if ((app[i] < FEMALE_VALUES[i][0]) || (app[i] > FEMALE_VALUES[i][1])) return false;
    } else {
      return false;
    }

    for (int i = 0; i < col.length; i++) {
      if ((col[i] < ALLOWED_COLORS[i][0]) || (col[i] > ALLOWED_COLORS[i][1])) return false;
    }
    return true;
  }

  public static boolean validate(Stoner p) {
    int[] a = p.getAppearance();

    return validate(
        new int[] {a[0], a[6], a[1], a[2], a[3], a[4], a[5]}, p.getColors(), p.getGender());
  }

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    byte gender = (byte) in.readByte();
    int head = in.readByte();
    int jaw = in.readByte();
    int torso = in.readByte();
    int arms = in.readByte();
    int hands = in.readByte();
    int legs = in.readByte();
    int feet = in.readByte();

    byte[] col = {
      (byte) in.readByte(),
      (byte) in.readByte(),
      (byte) in.readByte(),
      (byte) in.readByte(),
      (byte) in.readByte()
    };

    int[] app = new int[stoner.getAppearance().length];

    app[0] = head;
    app[1] = torso;
    app[2] = arms;
    app[3] = hands;
    app[4] = legs;
    app[5] = feet;
    app[6] = jaw;

    if (!validate(new int[] {head, jaw, torso, arms, hands, legs, feet}, col, gender)) {
      stoner.send(new SendMessage("Your appearance could not be validated."));
      return;
    }

    stoner.setGender(gender);
    stoner.setAppearance(app);
    stoner.setColors(col);

    stoner.setAppearanceUpdateRequired(true);

    AchievementHandler.activateAchievement(stoner, AchievementList.CAME_WITH_STYLE, 1);
  }
}
