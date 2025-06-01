package com.bestbudz.rs2.entity.stoner.net.in;

import com.bestbudz.core.network.ReceivedPacket;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.in.impl.BankAllButOne;
import com.bestbudz.rs2.entity.stoner.net.in.impl.BankModifiableX;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeRegionPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChatInterfacePacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.clickbuttons.ClickButtonPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.CloseInterfacePacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.CommandPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.DockPanelPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.FlashingSideIconPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.InputFieldPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.InterfaceAction;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ItemPackets;
import com.bestbudz.rs2.entity.stoner.net.in.impl.MovementPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.NPCPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ObjectPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.PrivateMessagingPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.PublicChatPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ReceiveString;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ResetCounter;
import com.bestbudz.rs2.entity.stoner.net.in.impl.StonerOptionPacket;
import com.bestbudz.rs2.entity.stoner.net.in.impl.StringInputPacket;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

  private static final IncomingPacket[] packets = new IncomingPacket[350];
  private static final IncomingPacket buttonPacket = new ClickButtonPacket();
  private static final IncomingPacket publicChatPacket = new PublicChatPacket();
  private static final IncomingPacket commandPacket = new CommandPacket();
  private static final IncomingPacket movementPacket = new MovementPacket();
  private static final IncomingPacket itemPacket = new ItemPackets();
  private static final IncomingPacket appearancePacket = new ChangeAppearancePacket();
  private static final IncomingPacket chatInterfacePacket = new ChatInterfacePacket();
  private static final IncomingPacket closeInterfacePacket = new CloseInterfacePacket();
  private static final IncomingPacket p9 = new FlashingSideIconPacket();
  private static final IncomingPacket npcPacket = new NPCPacket();
  private static final IncomingPacket objectPacket = new ObjectPacket();
  private static final IncomingPacket stonerOptionPacket = new StonerOptionPacket();
  private static final IncomingPacket p13 = new PrivateMessagingPacket();
  private static final IncomingPacket p14 = new ChangeRegionPacket();
  private final Client client;
  private final Stoner stoner;
  private final Map<IncomingPacket, Integer> packetMap = new HashMap<IncomingPacket, Integer>();

  public PacketHandler(Client client) {
    this.client = client;
    stoner = client.getStoner();
  }

  public static void declare() {
    packets[150] = new InputFieldPacket();
    packets[140] = new BankAllButOne();
    packets[141] = new BankModifiableX();
    packets['\u00B9'] = buttonPacket;
    packets[4] = publicChatPacket;
    packets[103] = commandPacket;
    packets['\u00F8'] = movementPacket;
    packets['\u00A4'] = movementPacket;
    packets[98] = movementPacket;
    packets['\u0091'] = itemPacket;
    packets[41] = itemPacket;
    packets[117] = itemPacket;
    packets[43] = itemPacket;
    packets['\u0081'] = itemPacket;
    packets[122] = itemPacket;
    packets['\u00D6'] = itemPacket;
    packets[87] = itemPacket;
    packets['\u00EC'] = itemPacket;
    packets[16] = itemPacket;
    packets[75] = itemPacket;
    packets[53] = itemPacket;
    packets[25] = itemPacket;
    packets['\u00ED'] = itemPacket;
    packets['\u00B5'] = itemPacket;
    packets['\u00FD'] = itemPacket;

    packets[121] = p14;
    packets[148] = new ResetCounter();
    packets[101] = appearancePacket;
    packets[40] = chatInterfacePacket;
    packets['\u0087'] = chatInterfacePacket;
    packets['\u00D0'] = chatInterfacePacket;
    packets['\u0082'] = closeInterfacePacket;
    packets['\u0098'] = p9;

    packets['\u009B'] = npcPacket;
    packets[17] = npcPacket;
    packets[21] = npcPacket;
    packets[18] = npcPacket;
    packets[72] = npcPacket;
    packets['\u0083'] = npcPacket;
    packets[57] = npcPacket;

    packets['\u00C0'] = objectPacket;
    packets['\u0084'] = objectPacket;
    packets['\u00FC'] = objectPacket;
    packets[70] = objectPacket;
    packets['\u00EA'] = objectPacket;
    packets[35] = objectPacket;

    packets['\u0080'] = stonerOptionPacket;
    packets[73] = stonerOptionPacket;
    packets['\u0080'] = stonerOptionPacket;
    packets['\u008B'] = stonerOptionPacket;
    packets['\u0088'] = stonerOptionPacket;
    packets['\u0099'] = stonerOptionPacket;
    packets['\u00F9'] = stonerOptionPacket;
    packets[14] = stonerOptionPacket;
    packets[39] = stonerOptionPacket;

    packets['\u00BC'] = p13;
    packets['\u00D7'] = p13;
    packets['\u0085'] = p13;
    packets[74] = p13;
    packets[126] = p13;
    packets[76] = p13;

    packets[60] = new StringInputPacket();
    packets[127] = new ReceiveString();
    packets[213] = new InterfaceAction();
	packets[222] = new DockPanelPacket();

  }

  public void handlePacket(ReceivedPacket packet) {
    client.resetLastPacketReceived();
    int opcode = packet.getOpcode();
    int length = packet.getSize();

    StreamBuffer.InBuffer in = StreamBuffer.newInBuffer(packet.getPayload());
    try {
      if (packets[opcode] != null) {
        if (packetMap.get(packets[opcode]) == null) {
          packetMap.put(packets[opcode], Integer.valueOf(0));
        }

        stoner.setLastAction(System.currentTimeMillis());

        int count =
            packetMap.get(packets[opcode]) == null ? 0 : packetMap.get(packets[opcode]).intValue();

        if (count < packets[opcode].getMaxDuplicates()) {
          packets[opcode].handle(stoner, in, opcode, length);

          if (count != 0) {
            packetMap.remove(packets[opcode]);
          }

          packetMap.put(packets[opcode], Integer.valueOf(++count));
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void reset() {
    if (packetMap.size() > 0) {
      packetMap.clear();
    }
  }
}
