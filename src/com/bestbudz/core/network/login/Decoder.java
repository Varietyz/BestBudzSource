package com.bestbudz.core.network.login;

import com.bestbudz.core.network.ISAACCipher;
import com.bestbudz.core.network.ReceivedPacket;
import com.bestbudz.core.util.Utility;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {

	private final ISAACCipher cipher;
	private int opcode = -1;
	private int size = -1;

	public Decoder(ISAACCipher cipher) {
		this.cipher = cipher;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		if (opcode == -1) {
			if (buffer.readableBytes() < 1) {
				return;
			}
			opcode = buffer.readByte() & 0xFF;
			opcode = (opcode - cipher.getNextValue()) & 0xFF;
			size = Utility.packetLengths[opcode];
		}

		if (size == -1) {
			if (buffer.readableBytes() < 1) {
				return;
			}
			size = buffer.readByte() & 0xFF;
		}

		if (buffer.readableBytes() < size) {
			return;
		}

		byte[] data = new byte[size];
		buffer.readBytes(data);

		ByteBuf payload = Unpooled.wrappedBuffer(data);
		out.add(new ReceivedPacket(opcode, size, payload));

		opcode = -1;
		size = -1;
	}
}
