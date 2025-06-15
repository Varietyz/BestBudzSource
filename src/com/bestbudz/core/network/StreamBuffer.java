package com.bestbudz.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class StreamBuffer {

  public static final int[] BIT_MASK = {
    0,
    0x1,
    0x3,
    0x7,
    0xf,
    0x1f,
    0x3f,
    0x7f,
    0xff,
    0x1ff,
    0x3ff,
    0x7ff,
    0xfff,
    0x1fff,
    0x3fff,
    0x7fff,
    0xffff,
    0x1ffff,
    0x3ffff,
    0x7ffff,
    0xfffff,
    0x1fffff,
    0x3fffff,
    0x7fffff,
    0xffffff,
    0x1ffffff,
    0x3ffffff,
    0x7ffffff,
    0xfffffff,
    0x1fffffff,
    0x3fffffff,
    0x7fffffff,
    -1
  };
  private AccessType accessType = AccessType.BYTE_ACCESS;
  private int bitLocation = 0;

  public static final InBuffer newInBuffer(ByteBuf data) {
    return new InBuffer(data);
  }

  public static final OutBuffer newOutBuffer(int size) {
    return new OutBuffer(size);
  }

  public AccessType getAccessType() {
    return accessType;
  }

  public void setAccessType(AccessType accessType) {
    this.accessType = accessType;
    switchAccessType(accessType);
  }

  public int getBitLocation() {
    return bitLocation;
  }

  public void setBitLocation(int bitLocation) {
    this.bitLocation = bitLocation;
  }

  abstract void switchAccessType(AccessType type);

  public enum AccessType {
    BYTE_ACCESS,
    BIT_ACCESS
  }

  public enum ByteOrder {
    LITTLE,
    BIG,
    MIDDLE,
    INVERSE_MIDDLE
  }

  public enum ValueType {
    STANDARD,
    A,
    C,
    S
  }

  public static final class InBuffer extends StreamBuffer {

	  private final ByteBuf buffer;


	  public InBuffer(ByteBuf buffer) {
      this.buffer = buffer;
    }

    public ByteBuf getBuffer() {
      return buffer;
    }

    public int readByte() {
      return readByte(true, ValueType.STANDARD);
    }

    public int readByte(boolean signed) {
      return readByte(signed, ValueType.STANDARD);
    }

    public int readByte(boolean signed, ValueType type) {
      int value = buffer.readByte();
      switch (type) {
        case A:
          value = value - 128;
          break;
        case C:
          value = -value;
          break;
        case S:
          value = 128 - value;
          break;
        default:
          break;
      }
      return signed ? value : value & 0xff;
    }

    public int readByte(ValueType type) {
      return readByte(true, type);
    }

    public byte[] readBytes(int amount) {
      return readBytes(amount, ValueType.STANDARD);
    }

    public byte[] readBytes(int amount, ValueType type) {
      byte[] data = new byte[amount];
      for (int i = 0; i < amount; i++) {
        data[i] = (byte) readByte(type);
      }
      return data;
    }

    public byte[] readBytesReverse(int amount, ValueType type) {
      byte[] data = new byte[amount];
      int dataLocation = 0;
      for (int i = buffer.readerIndex() + amount - 1; i >= buffer.readerIndex(); i--) {
        int value = buffer.getByte(i);
        switch (type) {
          case A:
            value -= 128;
            break;
          case C:
            value = -value;
            break;
          case S:
            value = 128 - value;
            break;
          default:
            break;
        }
        data[dataLocation++] = (byte) value;
      }
      return data;
    }

    public int readInt() {
      return (int) readInt(true, ValueType.STANDARD, ByteOrder.BIG);
    }

    public long readInt(boolean signed) {
      return readInt(signed, ValueType.STANDARD, ByteOrder.BIG);
    }

    public long readInt(boolean signed, ByteOrder order) {
      return readInt(signed, ValueType.STANDARD, order);
    }

    public long readInt(boolean signed, ValueType type) {
      return readInt(signed, type, ByteOrder.BIG);
    }

    public long readInt(boolean signed, ValueType type, ByteOrder order) {
      long value = 0;
      switch (order) {
        case BIG:
          value |= (long) readByte(false) << 24;
          value |= (long) readByte(false) << 16;
          value |= (long) readByte(false) << 8;
          value |= readByte(false, type);
          break;
        case MIDDLE:
          value |= (long) readByte(false) << 8;
          value |= readByte(false, type);
          value |= (long) readByte(false) << 24;
          value |= (long) readByte(false) << 16;
          break;
        case INVERSE_MIDDLE:
          value |= (long) readByte(false) << 16;
          value |= (long) readByte(false) << 24;
          value |= readByte(false, type);
          value |= (long) readByte(false) << 8;
          break;
        case LITTLE:
          value |= readByte(false, type);
          value |= (long) readByte(false) << 8;
          value |= (long) readByte(false) << 16;
          value |= (long) readByte(false) << 24;
          break;
      }
      return signed ? value : value & 0xffffffffL;
    }

    public int readInt(ByteOrder order) {
      return (int) readInt(true, ValueType.STANDARD, order);
    }

    public int readInt(ValueType type) {
      return (int) readInt(true, type, ByteOrder.BIG);
    }

    public int readInt(ValueType type, ByteOrder order) {
      return (int) readInt(true, type, order);
    }

    public long readLong() {
      return readLong(ValueType.STANDARD, ByteOrder.BIG);
    }

    public long readLong(ByteOrder order) {
      return readLong(ValueType.STANDARD, order);
    }

    public long readLong(ValueType type) {
      return readLong(type, ByteOrder.BIG);
    }

    public long readLong(ValueType type, ByteOrder order) {
      long value = 0;
      switch (order) {
        case BIG:
          value |= (long) readByte(false) << 56L;
          value |= (long) readByte(false) << 48L;
          value |= (long) readByte(false) << 40L;
          value |= (long) readByte(false) << 32L;
          value |= (long) readByte(false) << 24L;
          value |= (long) readByte(false) << 16L;
          value |= (long) readByte(false) << 8L;
          value |= readByte(false, type);
          break;
        case MIDDLE:
          throw new UnsupportedOperationException("middle-endian long is not implemented!");
        case INVERSE_MIDDLE:
          throw new UnsupportedOperationException("inverse-middle-endian long is not implemented!");
        case LITTLE:
          value |= readByte(false, type);
          value |= (long) readByte(false) << 8L;
          value |= (long) readByte(false) << 16L;
          value |= (long) readByte(false) << 24L;
          value |= (long) readByte(false) << 32L;
          value |= (long) readByte(false) << 40L;
          value |= (long) readByte(false) << 48L;
          value |= (long) readByte(false) << 56L;
          break;
      }
      return value;
    }

    public int readShort() {
      return readShort(true, ValueType.STANDARD, ByteOrder.BIG);
    }

    public int readShort(boolean signed) {
      return readShort(signed, ValueType.STANDARD, ByteOrder.BIG);
    }

    public int readShort(boolean signed, ByteOrder order) {
      return readShort(signed, ValueType.STANDARD, order);
    }

    public int readShort(boolean signed, ValueType type) {
      return readShort(signed, type, ByteOrder.BIG);
    }

    public int readShort(boolean signed, ValueType type, ByteOrder order) {
      int value = 0;
      switch (order) {
        case BIG:
          value |= readByte(false) << 8;
          value |= readByte(false, type);
          break;
        case MIDDLE:
          throw new UnsupportedOperationException("Middle-endian short is impossible!");
        case INVERSE_MIDDLE:
          throw new UnsupportedOperationException("Inverse-middle-endian short is impossible!");
        case LITTLE:
          value |= readByte(false, type);
          value |= readByte(false) << 8;
          break;
      }
      return signed ? value : value & 0xffff;
    }

    public int readShort(ByteOrder order) {
      return readShort(true, ValueType.STANDARD, order);
    }

    public int readShort(ValueType type) {
      return readShort(true, type, ByteOrder.BIG);
    }

    public int readShort(ValueType type, ByteOrder order) {
      return readShort(true, type, order);
    }

    public String readString() {
      byte temp;
      StringBuilder b = new StringBuilder();
      while ((temp = (byte) readByte()) != 10) {
        b.append((char) temp);
      }
      return b.toString();
    }

    public void reset() {
      buffer.readerIndex(0);
    }

    @Override
    void switchAccessType(AccessType type) {
      if (type == AccessType.BIT_ACCESS) {
        throw new UnsupportedOperationException("Reading bits is not implemented!");
      }
    }

	  public boolean readable() {
		  return buffer != null && buffer.isReadable();
	  }

	  public int readableBytes() {
		  return buffer != null ? buffer.readableBytes() : 0;
	  }
  }

  public static final class OutBuffer extends StreamBuffer {

    private ByteBuf buffer;
    private int lengthLocation = 0;

    private OutBuffer(int size) {
      buffer = Unpooled.buffer(size);
    }

    public void finishVariablePacketHeader() {
      buffer.setByte(lengthLocation, (byte) (buffer.writerIndex() - lengthLocation - 1));
    }

    public void finishVariableShortPacketHeader() {
      buffer.setShort(lengthLocation, (short) (buffer.writerIndex() - lengthLocation - 2));
    }

    public ByteBuf getBuffer() {
      return buffer;
    }

    @Override
    void switchAccessType(AccessType type) {
      switch (type) {
        case BIT_ACCESS:
          setBitLocation(buffer.writerIndex() * 8);
          break;
        case BYTE_ACCESS:
          buffer.writerIndex((getBitLocation() + 7) / 8);
          break;
      }
    }

    public void writeBit(boolean flag) {
      writeBits(1, flag ? 1 : 0);
    }

    public void writeBits(int amount, int value) {
      if (getAccessType() != AccessType.BIT_ACCESS) {
        throw new IllegalStateException("Illegal access type.");
      }
      if (amount < 0 || amount > 32) {
        throw new IllegalArgumentException("Number of bits must be between 1 and 32 inclusive.");
      }

      int bytePos = getBitLocation() >> 3;
      int bitOffset = 8 - (getBitLocation() & 7);
      setBitLocation(getBitLocation() + amount);
      int requiredSpace = bytePos - buffer.writerIndex() + 1;
      requiredSpace += (amount + 7) / 8;
      if (buffer.writableBytes() < requiredSpace) {
        ByteBuf old = buffer;
        buffer = Unpooled.buffer(old.capacity() + requiredSpace);
        buffer.writeBytes(old);
      }
      for (; amount > bitOffset; bitOffset = 8) {
        byte tmp = buffer.getByte(bytePos);
        tmp &= ~BIT_MASK[bitOffset];
        tmp |= (value >> (amount - bitOffset)) & BIT_MASK[bitOffset];
        buffer.setByte(bytePos++, tmp);
        amount -= bitOffset;
      }
      if (amount == bitOffset) {
        byte tmp = buffer.getByte(bytePos);
        tmp &= ~BIT_MASK[bitOffset];
        tmp |= value & BIT_MASK[bitOffset];
        buffer.setByte(bytePos, tmp);
      } else {
        byte tmp = buffer.getByte(bytePos);
        tmp &= ~(BIT_MASK[amount] << (bitOffset - amount));
        tmp |= (value & BIT_MASK[amount]) << (bitOffset - amount);
        buffer.setByte(bytePos, tmp);
      }
    }

    public void writeByte(long value) {
      writeByte(value, ValueType.STANDARD);
    }

    public void writeByte(long value, ValueType type) {
      if (getAccessType() != AccessType.BYTE_ACCESS) {
        throw new IllegalStateException("Illegal access type.");
      }
      switch (type) {
        case A:
          value += 128;
          break;
        case C:
          value = -value;
          break;
        case S:
          value = 128 - value;
          break;
        default:
          break;
      }
      buffer.writeByte((byte) value);
    }

    public void writeBytes(byte[] from) {
      buffer.writeBytes(from);
    }

    public void writeBytes(ByteBuf from) {
      for (int i = 0; i < from.writerIndex(); i++) {
        writeByte(from.getByte(i));
      }
    }

    public void writeBytesReverse(byte[] data) {
      for (int i = data.length - 1; i >= 0; i--) {
        writeByte(data[i]);
      }
    }

    public void writeHeader(ISAACCipher cipher, int value) {
      writeByte(value + cipher.getNextValue());
    }

    public void writeInt(int value) {
      writeInt(value, ValueType.STANDARD, ByteOrder.BIG);
    }

    public void writeInt(long value, ByteOrder order) {
      writeInt(value, ValueType.STANDARD, order);
    }

    public void writeInt(int value, ValueType type) {
      writeInt(value, type, ByteOrder.BIG);
    }

    public void writeInt(long value, ValueType type, ByteOrder order) {
      switch (order) {
        case BIG:
          writeByte(value >> 24);
          writeByte(value >> 16);
          writeByte(value >> 8);
          writeByte(value, type);
          break;
        case MIDDLE:
          writeByte(value >> 8);
          writeByte(value, type);
          writeByte(value >> 24);
          writeByte(value >> 16);
          break;
        case INVERSE_MIDDLE:
          writeByte(value >> 16);
          writeByte(value >> 24);
          writeByte(value, type);
          writeByte(value >> 8);
          break;
        case LITTLE:
          writeByte(value, type);
          writeByte(value >> 8);
          writeByte(value >> 16);
          writeByte(value >> 24);
          break;
      }
    }

    public void writeLong(long value) {
      writeLong(value, ValueType.STANDARD, ByteOrder.BIG);
    }

    public void writeLong(long value, ByteOrder order) {
      writeLong(value, ValueType.STANDARD, order);
    }

    public void writeLong(long value, ValueType type) {
      writeLong(value, type, ByteOrder.BIG);
    }

    public void writeLong(long value, ValueType type, ByteOrder order) {
      switch (order) {
        case BIG:
          writeByte((int) (value >> 56));
          writeByte((int) (value >> 48));
          writeByte((int) (value >> 40));
          writeByte((int) (value >> 32));
          writeByte((int) (value >> 24));
          writeByte((int) (value >> 16));
          writeByte((int) (value >> 8));
          writeByte((int) value, type);
          break;
        case MIDDLE:
          throw new UnsupportedOperationException("Middle-endian long is not implemented!");
        case INVERSE_MIDDLE:
          throw new UnsupportedOperationException("Inverse-middle-endian long is not implemented!");
        case LITTLE:
          writeByte((int) value, type);
          writeByte((int) (value >> 8));
          writeByte((int) (value >> 16));
          writeByte((int) (value >> 24));
          writeByte((int) (value >> 32));
          writeByte((int) (value >> 40));
          writeByte((int) (value >> 48));
          writeByte((int) (value >> 56));
          break;
      }
    }

    public void writeShort(int value) {
      writeShort(value, ValueType.STANDARD, ByteOrder.BIG);
    }

    public void writeShort(int value, ByteOrder order) {
      writeShort(value, ValueType.STANDARD, order);
    }

    public void writeShort(int value, ValueType type) {
      writeShort(value, type, ByteOrder.BIG);
    }

    public void writeShort(int value, ValueType type, ByteOrder order) {
      switch (order) {
        case BIG:
          writeByte(value >> 8);
          writeByte(value, type);
          break;
        case MIDDLE:
          throw new IllegalArgumentException("Middle-endian short is impossible!");
        case INVERSE_MIDDLE:
          throw new IllegalArgumentException("Inverse-middle-endian short is impossible!");
        case LITTLE:
          writeByte(value, type);
          writeByte(value >> 8);
          break;
      }
    }

    public void writeString(String string) {
      for (byte value : string.getBytes()) {
        writeByte(value);
      }
      writeByte(10);
    }

    public void writeVariablePacketHeader(ISAACCipher cipher, int value) {
      writeHeader(cipher, value);
      lengthLocation = buffer.writerIndex();
      writeByte(0);
    }

    public void writeVariableShortPacketHeader(ISAACCipher cipher, int value) {
      writeHeader(cipher, value);
      lengthLocation = buffer.writerIndex();
      writeShort(0);
    }

  }


}
