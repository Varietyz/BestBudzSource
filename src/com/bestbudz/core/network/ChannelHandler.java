package com.bestbudz.core.network;

import com.bestbudz.core.LoginThread;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ChannelHandler extends ChannelInboundHandlerAdapter {

	private Client client;

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (client != null) {
			client.getStoner().logout(true);
			client.disconnect();
			client = null;
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (!(ctx.channel().isActive())) return;

			if (msg instanceof Client) {
				client = (Client) msg;

				if (!ClientMap.allow(client)) {
					StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(3);
					resp.writeByte(Utility.LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED);
					resp.writeByte(0);
					resp.writeByte(0);
					ctx.writeAndFlush(resp.getBuffer()).addListener(ChannelFutureListener.CLOSE);
				} else {
					final Stoner p = client.getStoner();
					LoginThread.queueLogin(p);
				}

			} else if (msg instanceof ReceivedPacket) {
				client.queueIncomingPacket((ReceivedPacket) msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
