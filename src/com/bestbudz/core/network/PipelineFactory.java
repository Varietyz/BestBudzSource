package com.bestbudz.core.network;

import com.bestbudz.core.network.ChannelHandler;
import com.bestbudz.core.network.login.Encoder;
import com.bestbudz.core.network.login.LoginDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class PipelineFactory extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("encoder", new Encoder());       // your custom encoder
		pipeline.addLast("decoder", new LoginDecoder());  // your login decoder
		pipeline.addLast("handler", new ChannelHandler()); // your game logic handler
	}
}
