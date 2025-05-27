package com.bestbudz.rs2.content.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.NameUtil;
import com.bestbudz.rs2.content.StonerProperties;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.bank.Bank;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.SerpentineHelmet;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.entity.Entity.AssaultType;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemDegrading;
import com.bestbudz.rs2.entity.stoner.Stoner;

public final class StonerSave {

  public static final class StonerCultivation {

    private final long cultivationTimer;
    private final long lastCultivationTimer;
    private final int[] compostBins;
    private final long[] compostBinsTimer;
    private final int[] organicItemAdded;
    private final int[] bushesStages;
    private final int[] bushesSeeds;
    private final int[] bushesState;
    private final long[] bushesTimer;
    private final double[] bushesDiseaseChance;
    private final boolean[] bushesHasFullyGrown;
    private final boolean[] bushesWatched;
    private final int[] allotmentStages;
    private final int[] allotmentSeeds;
    private final int[] allotmentHarvest;
    private final int[] allotmentState;
    private final long[] allotmentTimer;
    private final double[] allotmentDiseaseChance;
    private final boolean[] allotmentWatched;
    private final boolean[] allotmentHasFullyGrown;
    private final int[] flowerStages;
    private final int[] flowerSeeds;
    private final int[] flowerState;
    private final long[] flowerTimer;
    private final double[] flowerDiseaseChance;
    private final boolean[] flowerHasFullyGrown;
    private final int[] fruitTreeStages;
    private final int[] fruitTreeSaplings;
    private final int[] fruitTreeState;
    private final long[] fruitTreeTimer;
    private final double[] fruitDiseaseChance;
    private final boolean[] fruitHasFullyGrown;
    private final boolean[] fruitTreeWatched;
    private final int[] weedStages;
    private final int[] weedSeeds;
    private final int[] weedHarvest;
    private final int[] weedState;
    private final long[] weedTimer;
    private final double[] weedDiseaseChance;
    private final int[] hopsStages;
    private final int[] hopsSeeds;
    private final int[] hopsHarvest;
    private final int[] hopsState;
    private final long[] hopsTimer;
    private final double[] hopDiseaseChance;
    private final boolean[] hopHasFullyGrown;
    private final boolean[] hopsWatched;
    private final int[] specialPlantOneStages;
    private final int[] specialPlantOneSeeds;
    private final int[] specialPlantOneState;
    private final long[] specialPlantOneTimer;
    private final double[] specialPlantOneDiseaseChance;
    private final boolean[] specialPlantOneHasFullyGrown;
    private final int[] specialPlantTwoStages;
    private final int[] specialPlantTwoSeeds;
    private final int[] specialPlantTwoState;
    private final long[] specialPlantTwoTimer;
    private final double[] specialPlantTwoDiseaseChance;
    private final boolean[] specialPlantTwoHasFullyGrown;
    private final int[] treeStages;
    private final int[] treeSaplings;
    private final int[] treeHarvest;
    private final int[] treeState;
    private final long[] treeTimer;
    private final double[] treeDiseaseChance;
    private final boolean[] treeHasFullyGrown;
    private final boolean[] treeWatched;
    public StonerCultivation(Stoner stoner) {
      this.cultivationTimer = stoner.getCultivation().getCultivationTimer();
      this.lastCultivationTimer = System.currentTimeMillis();
      this.compostBins = stoner.getCultivation().getCompost().compostBins;
      this.compostBinsTimer = stoner.getCultivation().getCompost().compostBinsTimer;
      this.organicItemAdded = stoner.getCultivation().getCompost().organicItemAdded;
      this.bushesStages = stoner.getCultivation().getBushes().bushesStages;
      this.bushesSeeds = stoner.getCultivation().getBushes().bushesSeeds;
      this.bushesState = stoner.getCultivation().getBushes().bushesState;
      this.bushesTimer = stoner.getCultivation().getBushes().bushesTimer;
      this.bushesDiseaseChance = stoner.getCultivation().getBushes().diseaseChance;
      this.bushesHasFullyGrown = stoner.getCultivation().getBushes().hasFullyGrown;
      this.bushesWatched = stoner.getCultivation().getBushes().bushesWatched;
      this.allotmentStages = stoner.getCultivation().getAllotment().allotmentStages;
      this.allotmentSeeds = stoner.getCultivation().getAllotment().allotmentSeeds;
      this.allotmentHarvest = stoner.getCultivation().getAllotment().allotmentHarvest;
      this.allotmentState = stoner.getCultivation().getAllotment().allotmentState;
      this.allotmentTimer = stoner.getCultivation().getAllotment().allotmentTimer;
      this.allotmentDiseaseChance = stoner.getCultivation().getAllotment().diseaseChance;
      this.allotmentWatched = stoner.getCultivation().getAllotment().allotmentWatched;
      this.allotmentHasFullyGrown = stoner.getCultivation().getAllotment().hasFullyGrown;
      this.flowerStages = stoner.getCultivation().getFlowers().flowerStages;
      this.flowerSeeds = stoner.getCultivation().getFlowers().flowerSeeds;
      this.flowerState = stoner.getCultivation().getFlowers().flowerState;
      this.flowerTimer = stoner.getCultivation().getFlowers().flowerTimer;
      this.flowerDiseaseChance = stoner.getCultivation().getFlowers().diseaseChance;
      this.flowerHasFullyGrown = stoner.getCultivation().getFlowers().hasFullyGrown;
      this.fruitTreeStages = stoner.getCultivation().getFruitTrees().fruitTreeStages;
      this.fruitTreeSaplings = stoner.getCultivation().getFruitTrees().fruitTreeSaplings;
      this.fruitTreeState = stoner.getCultivation().getFruitTrees().fruitTreeState;
      this.fruitTreeTimer = stoner.getCultivation().getFruitTrees().fruitTreeTimer;
      this.fruitDiseaseChance = stoner.getCultivation().getFruitTrees().diseaseChance;
      this.fruitHasFullyGrown = stoner.getCultivation().getFruitTrees().hasFullyGrown;
      this.fruitTreeWatched = stoner.getCultivation().getFruitTrees().fruitTreeWatched;
      this.weedStages = stoner.getCultivation().getWeeds().weedStages;
      this.weedSeeds = stoner.getCultivation().getWeeds().weedSeeds;
      this.weedHarvest = stoner.getCultivation().getWeeds().weedHarvest;
      this.weedState = stoner.getCultivation().getWeeds().weedState;
      this.weedTimer = stoner.getCultivation().getWeeds().weedTimer;
      this.weedDiseaseChance = stoner.getCultivation().getWeeds().diseaseChance;
      this.hopsStages = stoner.getCultivation().getHops().hopsStages;
      this.hopsSeeds = stoner.getCultivation().getHops().hopsSeeds;
      this.hopsHarvest = stoner.getCultivation().getHops().hopsHarvest;
      this.hopsState = stoner.getCultivation().getHops().hopsState;
      this.hopsTimer = stoner.getCultivation().getHops().hopsTimer;
      this.hopDiseaseChance = stoner.getCultivation().getHops().diseaseChance;
      this.hopHasFullyGrown = stoner.getCultivation().getHops().hasFullyGrown;
      this.hopsWatched = stoner.getCultivation().getHops().hopsWatched;
      this.specialPlantOneStages = stoner.getCultivation().getSpecialPlantOne().specialPlantStages;
      this.specialPlantOneSeeds = stoner.getCultivation().getSpecialPlantOne().specialPlantSaplings;
      this.specialPlantOneState = stoner.getCultivation().getSpecialPlantOne().specialPlantState;
      this.specialPlantOneTimer = stoner.getCultivation().getSpecialPlantOne().specialPlantTimer;
      this.specialPlantOneDiseaseChance =
          stoner.getCultivation().getSpecialPlantOne().diseaseChance;
      this.specialPlantOneHasFullyGrown =
          stoner.getCultivation().getSpecialPlantOne().hasFullyGrown;
      this.specialPlantTwoStages = stoner.getCultivation().getSpecialPlantTwo().specialPlantStages;
      this.specialPlantTwoSeeds = stoner.getCultivation().getSpecialPlantTwo().specialPlantSeeds;
      this.specialPlantTwoState = stoner.getCultivation().getSpecialPlantTwo().specialPlantState;
      this.specialPlantTwoTimer = stoner.getCultivation().getSpecialPlantTwo().specialPlantTimer;
      this.specialPlantTwoDiseaseChance =
          stoner.getCultivation().getSpecialPlantTwo().diseaseChance;
      this.specialPlantTwoHasFullyGrown =
          stoner.getCultivation().getSpecialPlantTwo().hasFullyGrown;
      this.treeStages = stoner.getCultivation().getTrees().treeStages;
      this.treeSaplings = stoner.getCultivation().getTrees().treeSaplings;
      this.treeHarvest = stoner.getCultivation().getTrees().treeHarvest;
      this.treeState = stoner.getCultivation().getTrees().treeState;
      this.treeTimer = stoner.getCultivation().getTrees().treeTimer;
      this.treeDiseaseChance = stoner.getCultivation().getTrees().diseaseChance;
      this.treeHasFullyGrown = stoner.getCultivation().getTrees().hasFullyGrown;
      this.treeWatched = stoner.getCultivation().getTrees().treeWatched;
    }

    public static boolean loadDetails(Stoner stoner) throws Exception {
      File file = new File("./data/characters/cultivation/" + stoner.getUsername() + ".json");

      if (!file.exists()) {
        return false;
      }

      BufferedReader reader = new BufferedReader(new FileReader(file));
      try {
        StonerCultivation details = StonerSave.GSON.fromJson(reader, StonerCultivation.class);

        long millis = System.currentTimeMillis();

        if (details.lastCultivationTimer > 0) {
          stoner
              .getCultivation()
              .setCultivationTimer(
                  details.cultivationTimer
                      + TimeUnit.MILLISECONDS.toMinutes(millis - details.lastCultivationTimer));
        } else {
          stoner.getCultivation().setCultivationTimer(details.cultivationTimer);
        }

        stoner.getCultivation().getCompost().compostBins = details.compostBins;
        stoner.getCultivation().getCompost().compostBinsTimer = details.compostBinsTimer;
        stoner.getCultivation().getCompost().organicItemAdded = details.organicItemAdded;
        stoner.getCultivation().getBushes().bushesStages = details.bushesStages;
        stoner.getCultivation().getBushes().bushesSeeds = details.bushesSeeds;
        stoner.getCultivation().getBushes().bushesState = details.bushesState;
        stoner.getCultivation().getBushes().bushesTimer = details.bushesTimer;
        stoner.getCultivation().getBushes().diseaseChance = details.bushesDiseaseChance;
        stoner.getCultivation().getBushes().hasFullyGrown = details.bushesHasFullyGrown;
        stoner.getCultivation().getBushes().bushesWatched = details.bushesWatched;
        stoner.getCultivation().getAllotment().allotmentStages = details.allotmentStages;
        stoner.getCultivation().getAllotment().allotmentSeeds = details.allotmentSeeds;
        stoner.getCultivation().getAllotment().allotmentHarvest = details.allotmentHarvest;
        stoner.getCultivation().getAllotment().allotmentState = details.allotmentState;
        stoner.getCultivation().getAllotment().allotmentTimer = details.allotmentTimer;
        stoner.getCultivation().getAllotment().diseaseChance = details.allotmentDiseaseChance;
        stoner.getCultivation().getAllotment().allotmentWatched = details.allotmentWatched;
        stoner.getCultivation().getAllotment().hasFullyGrown = details.allotmentHasFullyGrown;
        stoner.getCultivation().getFlowers().flowerStages = details.flowerStages;
        stoner.getCultivation().getFlowers().flowerSeeds = details.flowerSeeds;
        stoner.getCultivation().getFlowers().flowerState = details.flowerState;
        stoner.getCultivation().getFlowers().flowerTimer = details.flowerTimer;
        stoner.getCultivation().getFlowers().diseaseChance = details.flowerDiseaseChance;
        stoner.getCultivation().getFlowers().hasFullyGrown = details.flowerHasFullyGrown;
        stoner.getCultivation().getFruitTrees().fruitTreeStages = details.fruitTreeStages;
        stoner.getCultivation().getFruitTrees().fruitTreeSaplings = details.fruitTreeSaplings;
        stoner.getCultivation().getFruitTrees().fruitTreeState = details.fruitTreeState;
        stoner.getCultivation().getFruitTrees().fruitTreeTimer = details.fruitTreeTimer;
        stoner.getCultivation().getFruitTrees().diseaseChance = details.fruitDiseaseChance;
        stoner.getCultivation().getFruitTrees().hasFullyGrown = details.fruitHasFullyGrown;
        stoner.getCultivation().getFruitTrees().fruitTreeWatched = details.fruitTreeWatched;
        stoner.getCultivation().getWeeds().weedStages = details.weedStages;
        stoner.getCultivation().getWeeds().weedSeeds = details.weedSeeds;
        stoner.getCultivation().getWeeds().weedHarvest = details.weedHarvest;
        stoner.getCultivation().getWeeds().weedState = details.weedState;
        stoner.getCultivation().getWeeds().weedTimer = details.weedTimer;
        stoner.getCultivation().getWeeds().diseaseChance = details.weedDiseaseChance;
        stoner.getCultivation().getHops().hopsStages = details.hopsStages;
        stoner.getCultivation().getHops().hopsSeeds = details.hopsSeeds;
        stoner.getCultivation().getHops().hopsHarvest = details.hopsHarvest;
        stoner.getCultivation().getHops().hopsState = details.hopsState;
        stoner.getCultivation().getHops().hopsTimer = details.hopsTimer;
        stoner.getCultivation().getHops().diseaseChance = details.hopDiseaseChance;
        stoner.getCultivation().getHops().hasFullyGrown = details.hopHasFullyGrown;
        stoner.getCultivation().getHops().hopsWatched = details.hopsWatched;
        stoner.getCultivation().getSpecialPlantOne().specialPlantStages =
            details.specialPlantOneStages;
        stoner.getCultivation().getSpecialPlantOne().specialPlantSaplings =
            details.specialPlantOneSeeds;
        stoner.getCultivation().getSpecialPlantOne().specialPlantState =
            details.specialPlantOneState;
        stoner.getCultivation().getSpecialPlantOne().specialPlantTimer =
            details.specialPlantOneTimer;
        stoner.getCultivation().getSpecialPlantOne().diseaseChance =
            details.specialPlantOneDiseaseChance;
        stoner.getCultivation().getSpecialPlantOne().hasFullyGrown =
            details.specialPlantOneHasFullyGrown;
        stoner.getCultivation().getSpecialPlantTwo().specialPlantStages =
            details.specialPlantTwoStages;
        stoner.getCultivation().getSpecialPlantTwo().specialPlantSeeds =
            details.specialPlantTwoSeeds;
        stoner.getCultivation().getSpecialPlantTwo().specialPlantState =
            details.specialPlantTwoState;
        stoner.getCultivation().getSpecialPlantTwo().specialPlantTimer =
            details.specialPlantTwoTimer;
        stoner.getCultivation().getSpecialPlantTwo().diseaseChance =
            details.specialPlantTwoDiseaseChance;
        stoner.getCultivation().getSpecialPlantTwo().hasFullyGrown =
            details.specialPlantTwoHasFullyGrown;
        stoner.getCultivation().getTrees().treeStages = details.treeStages;
        stoner.getCultivation().getTrees().treeSaplings = details.treeSaplings;
        stoner.getCultivation().getTrees().treeHarvest = details.treeHarvest;
        stoner.getCultivation().getTrees().treeState = details.treeState;
        stoner.getCultivation().getTrees().treeTimer = details.treeTimer;
        stoner.getCultivation().getTrees().diseaseChance = details.treeDiseaseChance;
        stoner.getCultivation().getTrees().hasFullyGrown = details.treeHasFullyGrown;
        stoner.getCultivation().getTrees().treeWatched = details.treeWatched;
      } finally {
        if (reader != null) {
          reader.close();
        }
      }

      return true;
    }

    public void parseDetails(Stoner stoner) throws IOException {
      BufferedWriter writer =
          new BufferedWriter(
              new FileWriter(
                  "./data/characters/cultivation/" + stoner.getUsername() + ".json", false));
      try {
        writer.write(StonerSave.GSON.toJson(this));
        writer.flush();
      } finally {
        writer.close();
      }
    }
  }

  public static final class StonerContainer {

    private final Item[] bank;
    private final int[] tabAmounts;
    private final Item[] box;
    private final Item[] equipment;
    private final Item[] bobBox;
    private final Item[] shopItems;
    private final int[] shopPrices;
    private final byte[] pouches;
    public StonerContainer(Stoner stoner) {
      bank = stoner.getBank().getItems();
      tabAmounts = stoner.getBank().getTabAmounts();
      box = stoner.getBox().getItems();
      equipment = stoner.getEquipment().getItems();
      bobBox =
          (stoner.getSummoning().isFamilarBOB()
              ? stoner.getSummoning().getContainer().getItems()
              : null);
      shopItems = stoner.getStonerShop().getItems();
      shopPrices = stoner.getStonerShop().getPrices();
      pouches = stoner.getPouches();
    }

    public static boolean loadDetails(Stoner stoner) throws Exception {
      File file = new File("./data/characters/containers/" + stoner.getUsername() + ".json");

      if (!file.exists()) {
        return false;
      }

      BufferedReader reader = new BufferedReader(new FileReader(file));
      try {
        StonerContainer details = StonerSave.GSON.fromJson(reader, StonerContainer.class);

        if (details.shopItems != null) {
          stoner.getStonerShop().setItems(details.shopItems);
        }

        if (details.shopPrices != null) {
          stoner.getStonerShop().setPrices(details.shopPrices);
        }

        if (details.tabAmounts != null) {
          stoner.getBank().setTabAmounts(details.tabAmounts);
        }

        if (details.bank != null) {
          int tabs = Arrays.stream(stoner.getBank().getTabAmounts()).sum();
          int total = 0;
          for (int i = 0, slot = 0; i < Bank.SIZE; i++) {
            if (i >= Bank.SIZE) {
              break;
            }
            if (i >= details.bank.length) {
              break;
            }
            Item check = ItemCheck.check(stoner, details.bank[i]);
            stoner.getBank().getItems()[slot++] = check;
            if (check != null) {
              total++;
            }
          }

          if (total != tabs) {
            stoner.getBank().setTabAmounts(new int[] {total, 0, 0, 0, 0, 0, 0, 0, 0, 0});
            DialogueManager.sendStatement(
                stoner,
                "@dre@There was an issue loading your bank tabs.",
                "@dre@Your bank tabs have been collapsed as a safety measure.");
          }
        }

        if (details.equipment != null) {
          for (int i = 0; i < details.equipment.length; i++) {
            stoner.getEquipment().getItems()[i] = ItemCheck.check(stoner, details.equipment[i]);
          }
        }

        if (details.box != null) {
          for (int i = 0; i < details.box.length; i++) {
            stoner.getBox().getItems()[i] = ItemCheck.check(stoner, details.box[i]);
          }
        }

        if (details.bobBox != null) {
          stoner.getAttributes().set("summoningbobbox", details.bobBox);
        }

        if (details.pouches != null) {
          stoner.setPouches(details.pouches);
        }

        stoner.setLastLoginDay(Utility.getDayOfYear());
        stoner.setLastLoginYear(Utility.getYear());

      } finally {
        if (reader != null) {
          reader.close();
        }
      }

      return true;
    }

    public void parseDetails(Stoner stoner) throws IOException {
      BufferedWriter writer =
          new BufferedWriter(
              new FileWriter(
                  "./data/characters/containers/" + stoner.getUsername() + ".json", false));
      try {
        writer.write(StonerSave.GSON.toJson(this));
        writer.flush();
      } finally {
        writer.close();
      }
    }
  }

  public static final class StonerDetails {

    private final String username;
    private final String password;
    private final int x;
    private final int y;
    private final int z;
    private final byte rights;
    private final String lastKnownUID;
    private final int[] cluesCompleted;
    private final String yellTitle;
    private final StonerTitle stonerTitle;
    private final List<StonerTitle> unlockedTitles;
    private final boolean banned;
    private final long banLength;
    private final long moneyPouch;
    private final long jailLength;
    private final long shopCollection;
    private final String shopMotto;
    private final String shopColor;
    private final String lastClanChat;
    private final boolean muted;
    private final boolean isMember;
    private final boolean jailed;
    private final long muteLength;
    private final int fightCavesWave;
    private final int mageBook;
    private final int necromanceBook;
    private final boolean retaliate;
    private final boolean expLock;
    private final short[] gwkc;
    private final boolean poisoned;
    private final int poisonDmg;
    private final String mercenaryTask;
    private final byte mercenaryAmount;
    private final Mercenary.MercenaryDifficulty mercenaryDifficulty;
    private final long[] professionsGrade;
    private final double[] experience;
    private final byte gender;
    private final int[] appearance;
    private final byte[] colours;
    private final long left;
    private final int skullIcon;
    private final byte bright;
    private final byte multipleMouse;
    private final byte chatEffects;
    private final byte splitPrivate;
    private final byte transparentPanel;
    private final byte transparentChatbox;
    private final byte sideStones;
    private final byte acceptAid;
    private final boolean pouchPayment;
    private final String[] friends;
    private final String[] ignores;
    private final int yearCreated;
    private final int dayCreated;
    private final int recoilStage;
    private final int spec;
    private final Equipment.AssaultStyles assaultStyle;
    private final AssaultType assaultType;
    private final double energy;
    private final int lastLoginDay;
    private final int lastLoginYear;
    private final String host;
    private final int chillPoints;
    private final ItemDegrading.DegradedItem[] degrading;
    private final byte dragonFireShieldCharges;
    private final int mercenaryPoints;
    private final int musicVolume;
    private final int soundVolume;
    private final Item[] savedArrows;
    private final int deaths;
    private final int kills;
    private final int rogueKills;
    private final int rogueRecord;
    private final int hunterKills;
    private final int hunterRecord;
    private final int cannacredits;
    private final int bountyPoints;
    private final long lastLike;
    private final byte likesGiven;
    private final int likes;
    private final int dislikes;
    private final int profileViews;
    private final String pins;
    private final boolean running;
    private final boolean logPackets;
    private final int weaponPoints;
    private final int summoningTime;
    private final int summoningSpecialAmount;
    private final int summoningFamiliar;
    private final boolean summoningAssault;
    private final int pestPoints;
    private final int arenaPoints;
    private final int teleblockTime;
    private final int blackMarks;
    private final double rareDropEP;
    private final int rareDropsReceived;
    private final int[][] professionGoals;
    private final ArrayList<String> lastKilledStoners;
    private final HashMap<AchievementList, Integer> stonerAchievements;
    private final int achievementsPoints;
    private final Set<CreditPurchase> unlockedCredits;
    private final boolean[] quickNecromances;
    private final HashMap<Object, Object> stonerProperties;
    private final double expCounter;
    private final int advancePoints;
    private final int[] advances;
    private final int totalAdvances;
    private final ToxicBlowpipe blowpipe;
    private final TridentOfTheSeas seasTrident;
    private final TridentOfTheSwamp swampTrident;
    private final SerpentineHelmet serpentineHelmet;
    public StonerDetails(Stoner stoner) {
      username = stoner.getUsername();
      password = stoner.getPassword();
      x = stoner.getLocation().getX();
      y = stoner.getLocation().getY();
      z = stoner.getLocation().getZ();
      rights = ((byte) stoner.getRights());
      lastKnownUID = stoner.getUid();
      pins = stoner.getPin();
      cannacredits = stoner.getCredits();
      host = stoner.getClient().getHost();
      cluesCompleted = stoner.getCluesCompleted();
      yellTitle = stoner.getYellTitle();
      stonerTitle = stoner.getStonerTitle();
      unlockedTitles = stoner.unlockedTitles;
      lastLike = stoner.getLastLike();
      likesGiven = stoner.getLikesGiven();
      likes = stoner.getLikes();
      dislikes = stoner.getDislikes();
      profileViews = stoner.getProfileViews();
      banned = stoner.isBanned();
      banLength = stoner.getBanLength();
      moneyPouch = stoner.getMoneyPouch();
      jailLength = stoner.getJailLength();
      shopCollection = stoner.getShopCollection();
      shopMotto = stoner.getShopMotto();
      shopColor = stoner.getShopColor();
      muted = stoner.isMuted();
      isMember = stoner.isMember();
      jailed = stoner.isJailed();
      muteLength = stoner.getMuteLength();
      weaponPoints = stoner.getWeaponPoints();
      fightCavesWave = stoner.getJadDetails().getStage();
      mageBook = stoner.getMage().getMageBook();
      necromanceBook = stoner.getNecromanceInterface();
      retaliate = stoner.isRetaliate();
      expLock = stoner.getProfession().isExpLocked();
      gwkc = stoner.getMinigames().getGWKC();
      lastClanChat = stoner.lastClanChat;

      quickNecromances = stoner.getNecromance().getQuickNecromances();

      rareDropEP = stoner.getRareDropEP().getEp();

      rareDropsReceived = stoner.getRareDropEP().getReceived();

      blackMarks = stoner.getBlackMarks();

      poisoned = stoner.isPoisoned();
      pouchPayment = stoner.isPouchPayment();
      poisonDmg = stoner.getPoisonDamage();
      mercenaryTask = stoner.getMercenary().getTask();
      mercenaryAmount = stoner.getMercenary().getAmount();
      experience = stoner.getProfession().getExperience();
      professionsGrade = stoner.getProfession().getGrades();
      gender = stoner.getGender();
      appearance = (stoner.getAppearance().clone());
      colours = (stoner.getColors().clone());
      left = stoner.getSkulling().getLeft();
      skullIcon = stoner.getSkulling().getSkullIcon();
      spec = stoner.getSpecialAssault().getAmount();
      assaultStyle = stoner.getEquipment().getAssaultStyle();
      assaultType = stoner.getAssaultType();
      energy = stoner.getRunEnergy().getEnergy();
      chillPoints = stoner.getChillPoints();

      teleblockTime = stoner.getTeleblockTime();

      summoningAssault = stoner.getSummoning().isAssault();
      summoningTime = stoner.getSummoning().getTime();
      summoningSpecialAmount = stoner.getSummoning().getSpecialAmount();
      summoningFamiliar =
          (stoner.getSummoning().getFamiliarData() != null
              ? stoner.getSummoning().getFamiliarData().mob
              : -1);

      logPackets = stoner.getClient().isLogStoner();

      running = stoner.getRunEnergy().isRunning();

      pestPoints = stoner.getPestPoints();
      arenaPoints = stoner.getArenaPoints();

      soundVolume = stoner.getSoundVolume();

      deaths = stoner.getDeaths();
      kills = stoner.getKills();
      rogueKills = stoner.getRogueKills();
      rogueRecord = stoner.getRogueRecord();
      hunterKills = stoner.getHunterKills();
      hunterRecord = stoner.getHunterRecord();

      lastKilledStoners = stoner.getLastKilledStoners();

      bountyPoints = stoner.getBountyPoints();

      musicVolume = stoner.getMusicVolume();

      dragonFireShieldCharges = stoner.getMage().getDragonFireShieldCharges();

      degrading = stoner.getItemDegrading().getDegrading();

      lastLoginDay = stoner.getLastLoginDay();
      lastLoginYear = stoner.getLastLoginYear();

      yearCreated = stoner.getYearCreated();
      dayCreated = stoner.getDayCreated();

      mercenaryPoints = stoner.getMercenaryPoints();
      mercenaryDifficulty = stoner.getMercenary().getCurrent();

      if (stoner.getAttributes().get("recoilhits") != null)
        recoilStage = stoner.getAttributes().getInt("recoilhits");
      else {
        recoilStage = -1;
      }

      bright = stoner.getScreenBrightness();
      multipleMouse = stoner.getMultipleMouseButtons();
      chatEffects = stoner.getChatEffectsEnabled();
      splitPrivate = stoner.getSplitPrivateChat();
      transparentPanel = stoner.getTransparentPanel();
      transparentChatbox = stoner.getTransparentChatbox();
      sideStones = stoner.getSideStones();
      acceptAid = stoner.getAcceptAid();

      savedArrows = stoner.getSagittarius().getSavedArrows().getItems();
      professionGoals = stoner.getProfessionGoals();
      expCounter = stoner.getCounterExp();

      stonerAchievements = stoner.getStonerAchievements();
      achievementsPoints = stoner.getAchievementsPoints();

      unlockedCredits = stoner.getUnlockedCredits();

      int k = 0;
      friends = new String[stoner.getPrivateMessaging().getFriends().size()];
      for (String i : stoner.getPrivateMessaging().getFriends()) {
        friends[k] = i;
        k++;
      }

      k = 0;
      ignores = new String[stoner.getPrivateMessaging().getIgnores().size()];
      for (String i : stoner.getPrivateMessaging().getIgnores()) {
        ignores[k] = i;
        k++;
      }

      advancePoints = stoner.getAdvancePoints();
      advances = stoner.getProfessionAdvances();
      totalAdvances = stoner.getTotalAdvances();

      blowpipe = stoner.getToxicBlowpipe();
      seasTrident = stoner.getSeasTrident();
      swampTrident = stoner.getSwampTrident();
      serpentineHelmet = stoner.getSerpentineHelment();

      stonerProperties = new HashMap<>();

      for (Object attribute : stoner.getAttributes().getAttributes().keySet()) {
        if (String.valueOf(attribute).startsWith(StonerProperties.ATTRIBUTE_KEY)) {
          stonerProperties.put(attribute, stoner.getAttributes().getAttributes().get(attribute));
        }
      }
    }

    public static boolean loadDetails(Stoner stoner) throws Exception {
      BufferedReader reader = null;
      try {
        File file =
            new File(
                "./data/characters/details/"
                    + NameUtil.uppercaseFirstLetter(stoner.getUsername())
                    + ".json");

        if (!file.exists()) {
          return false;
        }

        reader = new BufferedReader(new FileReader(file));

        StonerDetails details = StonerSave.GSON.fromJson(reader, StonerDetails.class);
        stoner.setUsername(details.username);
        stoner.setPassword(details.password);
        stoner.getLocation().setAs(new Location(details.x, details.y, details.z));
        stoner.setRights(details.rights);

        if (details.lastKnownUID != null) {
          stoner.setLastKnownUID(details.lastKnownUID);
        }

        stoner.setYellTitle(details.yellTitle);
        stoner.setStonerTitle(details.stonerTitle);

        if (details.unlockedTitles != null && !details.unlockedTitles.isEmpty()) {
          stoner.unlockedTitles.addAll(details.unlockedTitles);
        }

        stoner.setMoneyPouch(details.moneyPouch);
        stoner.setShopCollection(details.shopCollection);
        stoner.setWeaponPoints(details.weaponPoints);
        stoner.setShopMotto(details.shopMotto);
        stoner.setShopColor(details.shopColor);
        stoner.setLastLike(details.lastLike);
        stoner.setLikesGiven(details.likesGiven);
        stoner.setLikes(details.likes);
        stoner.setDislikes(details.dislikes);
        stoner.setProfileViews(details.profileViews);
        stoner.setRetaliate(details.retaliate);
        stoner.getProfession().setExpLock(details.expLock);
        stoner.getMercenary().setAmount(details.mercenaryAmount);
        stoner.getMercenary().setTask(details.mercenaryTask);
        stoner.setPoisonDamage(details.poisonDmg);
        stoner.getSpecialAssault().setSpecialAmount(details.spec);
        stoner.getRunEnergy().setEnergy(details.energy);
        stoner.getSummoning().setAssault(details.summoningAssault);
        stoner.getSummoning().setSpecial(details.summoningSpecialAmount);
        stoner.getSummoning().setTime(details.summoningTime);
        stoner.setPestPoints(details.pestPoints);
        stoner.setArenaPoints(details.arenaPoints);
        if (details.summoningFamiliar != -1) {
          stoner
              .getAttributes()
              .set("summoningfamsave", Integer.valueOf(details.summoningFamiliar));
        }
        stoner.getClient().setLogStoner(details.logPackets);
        stoner.getRunEnergy().setRunning(details.running);
        stoner.setTeleblockTime(details.teleblockTime);
        stoner.getRareDropEP().setEp(details.rareDropEP);
        stoner.getRareDropEP().setReceived(details.rareDropsReceived);
        stoner.setDeaths(details.deaths);
        stoner.setKills(details.kills);
        stoner.setRogueKills(details.rogueKills);
        stoner.setRogueRecord(details.rogueRecord);
        stoner.setHunterKills(details.hunterKills);
        stoner.setHunterRecord(details.hunterRecord);
        stoner.setLastKilledStoners(details.lastKilledStoners);
        stoner.setPin(details.pins);
        stoner.setCredits(details.cannacredits);
        stoner.setBountyPoints(details.bountyPoints);
        stoner.setMusicVolume((byte) details.musicVolume);
        stoner.setSoundVolume((byte) details.soundVolume);
        stoner.setMercenaryPoints(details.mercenaryPoints);
        stoner.setBlackMarks(details.blackMarks);
        stoner.getMage().setDragonFireShieldCharges(details.dragonFireShieldCharges);
        if (details.degrading != null) {
          stoner.getItemDegrading().setDegrading(details.degrading);
        }
        if (details.savedArrows != null) {
          stoner.getSagittarius().getSavedArrows().setItems(details.savedArrows);
        }
        stoner.setChillPoints(details.chillPoints);
        if (details.assaultStyle != null) {
          stoner.getEquipment().setAssaultStyle(details.assaultStyle);
        }
        if (details.assaultType != null) {
          stoner.setAssaultType(details.assaultType);
        }
        if (details.recoilStage != -1) {
          stoner.getAttributes().set("recoilhits", Integer.valueOf(details.recoilStage));
        }

        stoner.getSkulling().setLeft(details.left);
        if (stoner.getSkulling().isSkulled()) {
          stoner.getSkulling().setSkullIcon(stoner, details.skullIcon);
        }
        if (details.host != null) {
          stoner.getClient().setHost(details.host);
        }
        if (details.mercenaryDifficulty != null) {
          stoner.getMercenary().setCurrent(details.mercenaryDifficulty);
        }
        stoner.setMember(details.isMember);
        stoner.setYearCreated(details.yearCreated);
        stoner.setDayCreated(details.dayCreated);

        stoner.setLastLoginDay(details.lastLoginDay);
        stoner.setLastLoginYear(details.lastLoginYear);
        stoner.setScreenBrightness(details.bright);
        stoner.setMultipleMouseButtons(details.multipleMouse);
        stoner.setChatEffects(details.chatEffects);
        stoner.setSplitPrivateChat(details.splitPrivate);
        stoner.setTransparentPanel(details.transparentPanel);
        stoner.setTransparentChatbox(details.transparentChatbox);
        stoner.setSideStones(details.sideStones);
        stoner.setAcceptAid(details.acceptAid);
        stoner.getJadDetails().setStage(details.fightCavesWave);
        if (details.friends != null) {
          for (String i : details.friends) {
            stoner.getPrivateMessaging().getFriends().add(i);
          }
        }

        if (details.ignores != null) {
          for (String i : details.ignores) {
            stoner.getPrivateMessaging().getIgnores().add(i);
          }
        }

        if ((details.poisonDmg > 0) && (details.poisoned)) {
          stoner.poison(details.poisonDmg);
        }

        stoner.setPouchPayment(details.pouchPayment);

        stoner.setGender(details.gender);

        if (details.appearance != null) {
          for (int i = 0; i < details.appearance.length; i++)
            stoner.getAppearance()[i] = details.appearance[i];
        }
        if (details.colours != null) {
          for (int i = 0; i < details.colours.length; i++)
            stoner.getColors()[i] = details.colours[i];
        }
        if (details.experience != null) {
          for (int i = 0; i < details.experience.length; i++) {
            stoner.getProfession().getExperience()[i] = details.experience[i];
          }
        }
        if (details.professionsGrade != null) {
          for (int i = 0; i < details.professionsGrade.length; i++) {
            stoner.getGrades()[i] = (short) details.professionsGrade[i];
          }
        }
        if (details.experience != null) {
          for (int i = 0; i < details.experience.length; i++) {
            stoner.getMaxGrades()[i] =
                stoner.getProfession().getGradeForExperience(i, details.experience[i]);
          }
        }

        if (details.gwkc != null) {
          stoner.getMinigames().setGWKC(details.gwkc);
        }

        boolean banned = details.banned;
        boolean muted = details.muted;
        boolean jailed = details.jailed;

        if ((banned)
            && (TimeUnit.MILLISECONDS.toSeconds(details.banLength - System.currentTimeMillis()) > 0
                || details.banLength == -1)) {
          stoner.setBanned(true);
          stoner.setBanLength(details.banLength);
        }

        if ((jailed)
            && (TimeUnit.MILLISECONDS.toSeconds(details.jailLength - System.currentTimeMillis()) > 0
                || details.jailLength == -1)) {
          stoner.setJailed(true);
          stoner.setJailLength(details.jailLength);
        }

        if ((muted)
            && (TimeUnit.MILLISECONDS.toSeconds(details.muteLength - System.currentTimeMillis()) > 0
                || details.muteLength == -1)) {
          stoner.setMuted(true);
          stoner.setMuteLength(details.muteLength);
        }

        if (details.mageBook > 0) {
          stoner.getMage().setMageBook(details.mageBook);
        }

        if (details.necromanceBook > 0) {
          stoner.setNecromanceInterface(details.necromanceBook);
        }

        if (details.professionGoals != null) {
          stoner.setProfessionGoals(details.professionGoals);
        }

        if (details.stonerAchievements != null) {
          stoner.getStonerAchievements().putAll(details.stonerAchievements);
        }

        if (details.achievementsPoints > 0) {
          stoner.addAchievementPoints(details.achievementsPoints);
        }

        if (details.expCounter > 0) {
          stoner.addCounterExp(details.expCounter);
        }

        if (details.cluesCompleted != null) {
          stoner.setCluesCompleted(details.cluesCompleted);
        }

        if (details.lastClanChat != null) {
          stoner.lastClanChat = details.lastClanChat;
        }

        stoner.setNecromanceInterface(5608);

        if (details.quickNecromances != null) {
          stoner.getNecromance().setQuickNecromances(details.quickNecromances);
        }

        stoner.setAdvancePoints(details.advancePoints);

        if (details.advances != null) {
          stoner.setProfessionAdvances(details.advances);
        }

        stoner.setTotalAdvances(details.advancePoints);

        if (details.advances != null) {
          stoner.setTotalAdvances(details.totalAdvances);
        }

        if (details.blowpipe != null) {
          stoner.setToxicBlowpipe(details.blowpipe);
        }

        if (details.seasTrident != null) {
          stoner.setSeasTrident(details.seasTrident);
        }

        if (details.swampTrident != null) {
          stoner.setSwampTrident(details.swampTrident);
        }

        if (details.serpentineHelmet != null) {
          stoner.setSerpentineHelment(details.serpentineHelmet);
        }

        stoner.getProperties().setDefaults();

        if (details.stonerProperties != null) {
          for (Object attribute : details.stonerProperties.keySet()) {
            stoner
                .getAttributes()
                .getAttributes()
                .put(attribute, details.stonerProperties.get(attribute));
          }
        }

        if (details.unlockedCredits != null) {
          stoner.getUnlockedCredits().addAll(details.unlockedCredits);
        }

        if (details.quickNecromances != null) {
          stoner.getNecromance().setQuickNecromances(details.quickNecromances);
        }

        return true;
      } finally {
        if (reader != null)
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
      }
    }

    public void parseDetails() throws Exception {
      BufferedWriter writer = null;
      try {
        writer =
            new BufferedWriter(
                new FileWriter("./data/characters/details/" + username + ".json", false));
        writer.write(StonerSave.GSON.toJson(this));
        writer.flush();
      } finally {
        if (writer != null) writer.close();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    GameDefinitionLoader.declare();
    GameDefinitionLoader.loadNpcDefinitions();
    File[] files = new File("./data/characters/details/").listFiles();
    int searches = 0;
    HashMap<String, String> map = new HashMap<>();
    for (File file : files) {
      Stoner stoner = new Stoner();
      stoner.setUsername(file.getName().replace(".json", ""));
      try {
        if (StonerDetails.loadDetails(stoner)) {

          if (stoner.getLastLoginDay() > 259) {
            if (stoner.getLastKnownUID() != null && !map.containsKey(stoner.getLastKnownUID())) {
              map.put(stoner.getLastKnownUID(), stoner.getUsername());
            } else {
              System.out.println(stoner.getUsername() + " " + stoner.getLastKnownUID());
            }
            searches++;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    System.out.println("Searched thorugh " + searches + " files.");
  }

  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static synchronized boolean load(Stoner p) throws Exception {
    if (!StonerDetails.loadDetails(p)) {
      return false;
    }

    if (!StonerContainer.loadDetails(p)) {
      return false;
    }

    return StonerCultivation.loadDetails(p);
  }

  public static synchronized void save(Stoner p) {
    try {
      new StonerDetails(p).parseDetails();
      new StonerContainer(p).parseDetails(p);
      new StonerCultivation(p).parseDetails(p);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
