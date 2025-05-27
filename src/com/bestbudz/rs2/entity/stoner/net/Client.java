package com.bestbudz.rs2.entity.stoner.net;

import com.bestbudz.core.network.ISAACCipher;
import com.bestbudz.core.network.ReceivedPacket;
import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.Utility.Stopwatch;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.PacketHandler;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {

	private final List<Mob> mobs = new ArrayList<>();
	private final Queue<ReceivedPacket> incomingPackets = new ConcurrentLinkedQueue<ReceivedPacket>();
	private final Utility.Stopwatch timeoutStopwatch = new Utility.Stopwatch();
	private Map<Integer, TinterfaceText> interfaceText;

	private Channel channel;
	private Queue<OutgoingPacket> outgoingPackets = new ConcurrentLinkedQueue<OutgoingPacket>();
	private Stages stage = Stages.LOGGING_IN;
	private ISAACCipher encryptor;
	private ISAACCipher decryptor;
	private Stoner stoner;
	private PacketHandler packetHandler;
	private String host;

	private long hostId = 0;

	private boolean logStoner = false;

	private String enteredPassword = null;

	private String lastStonerOption = "";

	private long lastPacketTime = World.getCycles();

	public Client(Channel channel) {
		try {
			this.channel = channel;

			if (channel != null) {
				if (channel.remoteAddress() instanceof java.net.InetSocketAddress) {
					java.net.InetSocketAddress addr = (java.net.InetSocketAddress) channel.remoteAddress();
					host = addr.getAddress().getHostAddress();
				} else {
					host = "unknown";
				}

				hostId = Utility.nameToLong(host);
			} else {
				host = "none";
				hostId = -1;
			}

			stoner = new Stoner(this);
			packetHandler = new PacketHandler(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void disconnect() {
		if (outgoingPackets != null) {
			outgoingPackets.clear();
		}
		if (channel != null) {
			channel.close();
		}
	}



	public synchronized ISAACCipher getDecryptor() {
	return decryptor;
	}

	public void setDecryptor(ISAACCipher decryptor) {
	this.decryptor = decryptor;
	}

	public synchronized ISAACCipher getEncryptor() {
	return encryptor;
	}

	public void setEncryptor(ISAACCipher encryptor) {
	this.encryptor = encryptor;
	}

	public String getEnteredPassword() {
	return enteredPassword;
	}

	public void setEnteredPassword(String enteredPassword) {
	this.enteredPassword = enteredPassword;
	}

	public String getHost() {
	return host;
	}

	public void setHost(String host) {
	this.host = host;
	}

	public long getHostId() {
	return hostId;
	}

	public long getLastPacketTime() {
	return lastPacketTime;
	}

	public String getLastStonerOption() {
	return lastStonerOption;
	}

	public void setLastStonerOption(String lastStonerOption) {
	this.lastStonerOption = lastStonerOption;
	}

	public List<Mob> getNpcs() {
	return mobs;
	}

	public Queue<OutgoingPacket> getOutgoingPackets() {
	return outgoingPackets;
	}

	public Stoner getStoner() {
	return stoner;
	}

	public Stages getStage() {
	return stage;
	}

	public void setStage(Stages stage) {
	this.stage = stage;
	}

	public Stopwatch getTimeoutStopwatch() {
	return timeoutStopwatch;
	}

	public boolean isLogStoner() {
	return logStoner;
	}

	public void setLogStoner(boolean logStoner) {
	this.logStoner = logStoner;
	}

	public void processIncomingPackets() {
	ReceivedPacket p = null;

	try {
		if (outgoingPackets == null) {
			return;
		}
		while ((p = incomingPackets.poll()) != null) {
			packetHandler.handlePacket(p);
		}


	} catch (Exception e) {
		e.printStackTrace();
		stoner.logout(true);
	}
	}

	public void processOutgoingPackets() {
	if (channel == null || outgoingPackets == null) {
		return;
	}

	try {

			OutgoingPacket p;
			while ((p = outgoingPackets.poll()) != null) {
				p.execute(this);
			}


	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	public void queueIncomingPacket(ReceivedPacket packet) {
	resetLastPacketReceived();

		incomingPackets.offer(packet);
	}

	public void queueOutgoingPacket(OutgoingPacket o) {
		if (outgoingPackets != null) {
			outgoingPackets.offer(o);
		}
	}


	public void reset() {
	packetHandler.reset();
	}

	public void resetLastPacketReceived() {
	lastPacketTime = World.getCycles();
	}

	public void send(ByteBuf buffer) {
	try {

		if (channel == null || !channel.isActive()) {

			return;
		}
		channel.writeAndFlush(buffer);

	} catch (Exception e) {
		e.printStackTrace();
	}
	}

	public boolean checkSendString(String text, int id) {
		if (interfaceText == null) {
			interfaceText = new HashMap<>();
		}

		if (!interfaceText.containsKey(id)) {
		interfaceText.put(id, new TinterfaceText(text, id));
	} else {
		TinterfaceText t = interfaceText.get(id);
		if (text.equals(t.currentState)) {
			return false;
		}
		t.currentState = text;
	}
	return true;
	}

	public enum Stages {
		CONNECTED,
		LOGGING_IN,
		LOGGED_IN,
		LOGGED_OUT
	}

	public class TinterfaceText {
		public int id;
		public String currentState;

		public TinterfaceText(String s, int id) {
		this.currentState = s;
		this.id = id;
		}
	}
}
