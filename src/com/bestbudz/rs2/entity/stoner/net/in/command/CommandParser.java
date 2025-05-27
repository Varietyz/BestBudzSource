package com.bestbudz.rs2.entity.stoner.net.in.command;

public class CommandParser {

  private String[] arguments;

  private int argumentIndex = 0;

  public static CommandParser create(String command) {
    CommandParser parser = new CommandParser();
    parser.arguments = command.split(" ");
    return parser;
  }

  public final int nextInt() throws NumberFormatException {
    return Integer.parseInt(nextString());
  }

  public final double nextDouble() throws NumberFormatException {
    return Double.parseDouble(nextString());
  }

  public final long nextLong() throws NumberFormatException {
    return Long.parseLong(nextString());
  }

  public final byte nextByte() throws NumberFormatException {
    return Byte.parseByte(nextString());
  }

  public final short nextShort() throws NumberFormatException {
    return Short.parseShort(nextString());
  }

  public final String nextString() throws ArrayIndexOutOfBoundsException {
    if (argumentIndex + 1 >= arguments.length)
      throw new ArrayIndexOutOfBoundsException(
          "The next argument does not exist. [Size: "
              + arguments.length
              + ", Attempted: "
              + (argumentIndex + 1)
              + "]");

    return arguments[++argumentIndex];
  }

  public final boolean hasNext() {
    return hasNext(1);
  }

  public final boolean hasNext(int length) {
    return argumentIndex + length < arguments.length;
  }

  public final String getCommand() {
    return arguments[0];
  }

  @Override
  public String toString() {
    String string = "";
    for (int i = 0; i < arguments.length; i++) {
      string += arguments[i] + " ";
    }
    return string.trim();
  }
}
