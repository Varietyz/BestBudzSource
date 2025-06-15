package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.ReportHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PublicChatPacket extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
		int effects = in.readByte(false, StreamBuffer.ValueType.S);
		int color = in.readByte(false, StreamBuffer.ValueType.S);
		int chatLength = length - 2;
		byte[] text = in.readBytesReverse(chatLength, StreamBuffer.ValueType.A);

		if (!stoner.getController().canTalk()) {
			stoner.send(new SendMessage("You cannot talk right now."));
			return;
		}

		stoner.setChatEffects(effects);
		stoner.setChatColor(color);
		stoner.setChatText(text);

		if (stoner.isMuted()) {
			if (stoner.getMuteLength() == -1) {
				stoner.send(new SendMessage("You are permanently muted on this account."));
			} else {
				long muteHours = TimeUnit.MILLISECONDS.toMinutes(stoner.getMuteLength() - System.currentTimeMillis());
				String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
				if (muteHours < 60) {
					if (muteHours <= 0) {
						stoner.send(new SendMessage("Your mute has been lifted!"));
						stoner.setMuted(false);
						stoner.setChatUpdateRequired(true);
						return;
					}
					timeUnit = "minute" + (muteHours > 1 ? "s" : "");
				} else {
					muteHours = TimeUnit.MINUTES.toHours(muteHours);
				}
				stoner.send(new SendMessage("You are muted, you will be unmuted in " + muteHours + " " + timeUnit + "."));
			}
		} else {
			System.out.println(stoner + " said: " + Arrays.toString(text) + " | Length: " + chatLength);
			stoner.setChatUpdateRequired(true);
			ChatBridgeManager.broadcastToDiscord(stoner, text, chatLength);
			ReportHandler.addText(stoner.getUsername(), text, chatLength);

		}
	}
}
