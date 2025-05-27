package com.bestbudz.rs2.content;

import com.bestbudz.core.util.NameUtil;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendFriendUpdate;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendPMServer;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendPrivateMessage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrivateMessaging {
	private final Stoner stoner;
	private final List<String> friends = new LinkedList<String>();
	private final List<String> ignores = new LinkedList<String>();
	private int messagesReceived = 0;

	public PrivateMessaging(Stoner stoner) {
	this.stoner = stoner;
	}

	public void addFriend(long id) {
	String name = NameUtil.longToName(id).toLowerCase().replaceAll("_", " ");

	friends.add(name);

	stoner.getClient().queueOutgoingPacket(new SendFriendUpdate(id, World.getStonerByName(name) == null ? 0 : 1));
	}

	public void addFriend(String name) {
	name = name.toLowerCase();

	long id = NameUtil.nameToLong(name);
	friends.add(name);

	stoner.getClient().queueOutgoingPacket(new SendFriendUpdate(id, World.getStonerByName(name) == null ? 0 : 1));
	}

	public void addIgnore(long id) {
	ignores.add(NameUtil.longToName(id).replaceAll("_", " "));
	}

	public void addIgnore(String name) {
	ignores.add(name);
	}

	public void connect() {
	stoner.getClient().queueOutgoingPacket(new SendPMServer(2));

	for (Iterator<String> i = friends.iterator(); i.hasNext();) {
		String name = i.next();
		stoner.getClient().queueOutgoingPacket(new SendFriendUpdate(NameUtil.nameToLong(name), World.getStonerByName(name) == null ? 0 : 1));
	}

	}

	public List<String> getFriends() {
	return friends;
	}

	public List<String> getIgnores() {
	return ignores;
	}

	public int getNextMessageId() {
	messagesReceived += 1;
	return messagesReceived;
	}

	public boolean ignored(String n) {
	return ignores.contains(n.toLowerCase());
	}

	public void removeFriend(long id) {
	friends.remove(NameUtil.longToName(id).toLowerCase().replaceAll("_", " "));
	}

	public void removeIgnore(long id) {
	ignores.remove(NameUtil.longToName(id).replaceAll("_", " "));
	}

	public void sendPrivateMessage(long id, int size, byte[] text) {
	String name = NameUtil.longToName(id).replaceAll("_", " ");
	Stoner sentTo = World.getStonerByName(name);

	if (sentTo != null) {
		if (sentTo.getPrivateMessaging().ignored(stoner.getUsername())) {
			return;
		}

		if (stoner.isMuted()) {
			if (stoner.getMuteLength() == -1) {
				stoner.send(new SendMessage("You are permanently muted on this account."));
				return;
			} else {
				long muteHours = TimeUnit.MILLISECONDS.toMinutes(stoner.getMuteLength() - System.currentTimeMillis());
				String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
				if (muteHours < 60) {
					if (muteHours <= 0) {
						stoner.send(new SendMessage("Your mute has been lifted!"));
						stoner.setMuted(false);
					}
					timeUnit = "minute" + (muteHours > 1 ? "s" : "");
				} else {
					muteHours = TimeUnit.MINUTES.toHours(muteHours);
				}
				if (stoner.isMuted()) {
					stoner.send(new SendMessage("You are muted, you will be unmuted in " + muteHours + " " + timeUnit + "."));
					return;
				}
			}
		}
		if (name == sentTo.getUsername()) {
			stoner.send(new SendMessage("You may not send a message to yourself!"));
			return;
		}
		sentTo.getClient().queueOutgoingPacket(new SendPrivateMessage(NameUtil.nameToLong(stoner.getUsername()), stoner.getRights(), text, sentTo.getPrivateMessaging().getNextMessageId()));
	} else {
		stoner.getClient().queueOutgoingPacket(new SendMessage("Your private message could not be delivered."));
	}
	}

	public void updateOnlineStatus(Stoner connectedStoner, boolean connected) {
	String name = connectedStoner.getUsername().toLowerCase();

	if (friends.contains(name))
		stoner.getClient().queueOutgoingPacket(new SendFriendUpdate(NameUtil.nameToLong(name), connected ? 1 : 0));
	}
}
