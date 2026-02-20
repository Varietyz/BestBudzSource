package com.bestbudz.rs2.content.profession;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Professions {

  public static final int GRADE_99_SONG = 420;
  public static final int[] GRADE_UP_SOUNDS = {112, 107, 104, 10, 21, 24};
  public static final short PROFESSION_COUNT = 21;

  public static final Graphic UPGRADE_GRAPHIC = Graphic.lowGraphic(1154, 0);
  public static final Animation UPGRADE_ANIM = new Animation(3170);

  public static final int[] EXPERIENCE_RATES = new int[PROFESSION_COUNT];
  public static final int MAX_EXPERIENCE = 420000000;
  public static final String[] PROFESSION_NAMES = {
    "assault",
    "aegis",
    "vigour",
    "life",
    "sagittarius",
    "resonance",
    "mage",
    "foodie",
    "lumbering",
    "woodcarving",
    "fisher",
    "pyromaniac",
    "handiness",
    "forging",
    "quarrying",
    "thc-hempistry",
    "weedsmoking",
    "petmaster",
    "mercenary",
    "bankstanding",
    "consumer",
  };
  public static final int ASSAULT = 0;
  public static final int AEGIS = 1;
  public static final int VIGOUR = 2;
  public static final int LIFE = 3;
  public static final int SAGITTARIUS = 4;
  public static final int RESONANCE = 5;
  public static final int MAGE = 6;
  public static final int FOODIE = 7;
  public static final int LUMBERING = 8;
  public static final int WOODCARVING = 9;
  public static final int FISHER = 10;
  public static final int PYROMANIAC = 11;
  public static final int HANDINESS = 12;
  public static final int FORGING = 13;
  public static final int QUARRYING = 14;
  public static final int THCHEMPISTRY = 15;
  public static final int WEEDSMOKING = 16;
  public static final int PET_MASTER = 17;
  public static final int MERCENARY = 18;
  public static final int BANKSTANDING = 19;
  public static final int CONSUMER = 20;
  public static final int[][] CHAT_INTERFACES = {
    {0, 6247},
    {1, 6253},
    {2, 6206},
    {3, 6216},
    {4, 4443, 5453, 6114},
    {5, 6242},
    {6, 6211},
    {7, 6226},
    {8, 4272},
    {9, 6231},
    {10, 6258},
    {11, 4282},
    {12, 6263},
    {13, 6221},
    {14, 4416, 4417, 4438},
    {15, 6237},
    {16, 4277},
    {17, 4261, 4263, 4264},
    {18, 12122},
    {19, 4887, 4890, 4891},
    {20, 4267},
    {21, 4268}
  };
  public static final int[][] REFRESH_DATA = {
    {4004, 4005},
    {4008, 4009},
    {4006, 4007},
    {4016, 4017},
    {4010, 4011},
    {4012, 4013},
    {4014, 4015},
    {4034, 4035},
    {4038, 4039},
    {4026, 4027},
    {4032, 4033},
    {4036, 4037},
    {4024, 4025},
    {4030, 4031},
    {4028, 4029},
    {4020, 4021},
    {4018, 4019},
    {4022, 4023},
    {12166, 12167},
    {13926, 13927},
    {4152, 4153},
    {24134, 24135},
  };
  public static final int CURRENT_HEALTH_UPDATE_ID = 4016;
  public static final int MAX_HEALTH_UPDATE_ID = 4017;
  public static final int CURRENT_RESONANCE_UPDATE_ID = 4012;
  public static final int MAX_RESONANCE_UPDATE_ID = 4013;

	public static void declare() {
    EXPERIENCE_RATES[ASSAULT] = 1;
    EXPERIENCE_RATES[AEGIS] = 1;
    EXPERIENCE_RATES[VIGOUR] = 1;
    EXPERIENCE_RATES[LIFE] = 1;
    EXPERIENCE_RATES[SAGITTARIUS] = 1;
    EXPERIENCE_RATES[RESONANCE] = 20;
    EXPERIENCE_RATES[MAGE] = 1;
    EXPERIENCE_RATES[FOODIE] = 10;
    EXPERIENCE_RATES[LUMBERING] = 4;
    EXPERIENCE_RATES[WOODCARVING] = 10;
    EXPERIENCE_RATES[FISHER] = 10;
    EXPERIENCE_RATES[PYROMANIAC] = 5;
    EXPERIENCE_RATES[HANDINESS] = 10;
    EXPERIENCE_RATES[FORGING] = 10;
    EXPERIENCE_RATES[QUARRYING] = 10;
    EXPERIENCE_RATES[THCHEMPISTRY] = 10;
    EXPERIENCE_RATES[WEEDSMOKING] = 10;
    EXPERIENCE_RATES[PET_MASTER] = 1;
    EXPERIENCE_RATES[MERCENARY] = 10;
    EXPERIENCE_RATES[BANKSTANDING] = 10;
    EXPERIENCE_RATES[CONSUMER] = 1;
  }

  public static final boolean isSuccess(long profession, int gradeRequired) {
    double grade = profession;
    double req = gradeRequired;
    double successChance = Math.ceil((grade * 50.0D - req * 15.0D) / req / 3.0D * 4.0D);
    int roll = Utility.randomNumber(99);

    return successChance >= roll;
  }

  public static final boolean isSuccess(Stoner p, int professionId, int gradeRequired) {
    double grade = p.getMaxGrades()[professionId];
    double req = gradeRequired;
    double successChance = Math.ceil((grade * 50.0D - req * 15.0D) / req / 3.0D * 4.0D);
    int roll = Utility.randomNumber(99);

    return successChance >= roll;
  }

  public static final boolean isSuccess(
      Stoner p, int professionId, int gradeRequired, int toolGradeRequired) {
    double grade = (p.getMaxGrades()[professionId] + toolGradeRequired) / 2.0D;
    double req = gradeRequired;
    double successChance = Math.ceil((grade * 50.0D - req * 15.0D) / req / 3.0D * 4.0D);
    int roll = Utility.randomNumber(99);

    return successChance >= roll;
  }
}
