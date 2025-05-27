package com.bestbudz.rs2.entity;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReportHandler {

    public static String[] savedNames = new String[500];
    public static String[] savedSpeach = new String[500];
    public static String[] savedTimes = new String[500];

    public static void addText(String name, byte[] data, int dataLength) {
    for (int i = 499; i > 0; i--) {
        savedNames[i] = savedNames[i - 1];
        savedSpeach[i] = savedSpeach[i - 1];
        savedTimes[i] = savedTimes[i - 1];
    }
    savedNames[0] = name;
    savedSpeach[0] = Utility.textUnpack(data, dataLength, false);
    String minute = new SimpleDateFormat("mm").format(new Date());
    String second = new SimpleDateFormat("ss").format(new Date());
    String hour = new SimpleDateFormat("hh").format(new Date());
    savedTimes[0] = hour + ":" + minute + ":" + second;
    }

    public static boolean hasSpoke(String s) {
    for (int i = 0; i < 500; i++) {
        if (savedNames[i] != null) {
            if (savedNames[i].equalsIgnoreCase(s))
                return true;
        }
    }
    return false;
    }

    public static void handleReport(Stoner stoner) {

    if (stoner.getInterfaceManager().main != 41750) {
        stoner.send(new SendRemoveInterfaces());
        return;
    }

    if (stoner.reportName == "") {
        stoner.send(new SendMessage("Enter a name, but we wont review it."));
        return;
    }

    Stoner offending = World.getStonerByName(stoner.reportName);

    if (offending == null) {
        stoner.send(new SendMessage(stoner.reportName + " couldnt be reported, your too stoned and a shitty source"));
        return;
    }

    if (offending == stoner) {
        stoner.send(new SendMessage("Unfortunatly, you need to get someone else to report your ass!"));
        return;
    }

    if (stoner.lastReported.equalsIgnoreCase(stoner.reportName) && (System.currentTimeMillis() - stoner.lastReport) < 60000) {
        stoner.send(new SendMessage("Our system is slow asf, wait 60s, you fucking Karen."));
        return;
    }

    if (stoner.reportClicked == 0) {
        stoner.send(new SendMessage("Your not the brightest of the bunch, are ya?"));
        return;
    }

    ReportData data = ReportData.reports.get(stoner.reportClicked);

    if (data == null) {
        return;
    }

    if (hasSpoke(offending.getUsername())) {
        String sendText = "";
        for (int i = 499; i > 0; i--) {
            if (savedNames[i] != null) {
                if (savedNames[i].equalsIgnoreCase(stoner.getUsername()) || savedNames[i].equalsIgnoreCase(offending.getUsername())) {
                    sendText += " -[" + savedTimes[i] + ": " + savedNames[i] + "]: " + savedSpeach[i] + "\r\n";
                }
            }
        }

        sendText = sendText.replaceAll("'", " ");
        String month = getMonth(new SimpleDateFormat("MM").format(new Date()));
        String day = new SimpleDateFormat("dd").format(new Date());
        writeReport(offending.getUsername() + " was karen'd by " + stoner.getUsername() + ", " + data.getRule() + ", " + month + ", " + day, sendText + ".", offending.getUsername());
        stoner.send(new SendMessage("Thank you, we wont review this, your a Karen, now die in hole."));
        stoner.lastReported = offending.getUsername();
        stoner.lastReport = System.currentTimeMillis();
	} else {
        stoner.send(new SendMessage("Reporting mime's, thats ridicolous xD."));
	}

    }

    public static String getMonth(String s) {
    try {
        int i = Integer.parseInt(s);
        String[] months = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
        return months[i];
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "Unknown";
    }

    public static void writeReport(String data, String text, String file) {
    BufferedWriter bw = null;
    try {
        bw = new BufferedWriter(new FileWriter("./data/reports/" + file + ".txt", true));
        bw.write(data);
        bw.newLine();
        bw.write(text);
        bw.newLine();
        bw.newLine();
        bw.flush();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    } finally {
        if (bw != null)
            try {
                bw.close();
            } catch (IOException ioe2) {
                System.out.println("Error writing system log.");
                ioe2.printStackTrace();
            }
    }
    }

    public static void writeLog(String text, String file, String dir) {
    BufferedWriter bw = null;
    try {
        bw = new BufferedWriter(new FileWriter(dir + file + ".txt", true));
        bw.write(text);
        bw.newLine();
        bw.flush();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    } finally {
        if (bw != null)
            try {
                bw.close();
            } catch (IOException ioe2) {
                System.out.println("Error writing system log.");
            }
    }
    }

    public enum ReportData {
        RULE_1(163034, "Showed weener"),
        RULE_2(163035, "Didnt bring weed"),
        RULE_3(163036, "Doesnt like bankstanding"),
        RULE_4(163037, "Plays RS3"),
        RULE_5(163038, "Is xp wasting"),
        RULE_6(163039, "Doesnt login enough"),
        RULE_7(163040, "Is a noob"),
        RULE_8(163041, "Did something wrong"),
        RULE_9(163042, "Threw crap at the zookeeper"),
        RULE_10(163043, "Gave me monkey nuts"),
        RULE_11(163044, "The reporter is a Karen"),
        RULE_12(163045, "Broke a none-existent rule");

        private static final HashMap<Integer, ReportData> reports = new HashMap<Integer, ReportData>();

        static {
            for (final ReportData report : ReportData.values()) {
                ReportData.reports.put(report.buttonID, report);
            }
        }

        private final int buttonID;
        private final String rule;

        ReportData(int buttonID, String rule) {
        this.buttonID = buttonID;
        this.rule = rule;
        }

        public static ReportData get(int id) {
        return reports.get(id);
        }

        public int getButton() {
        return buttonID;
        }

        public String getRule() {
        return rule;
        }
    }

}