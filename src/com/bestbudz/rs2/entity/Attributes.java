package com.bestbudz.rs2.entity;

import java.util.HashMap;
import java.util.Map;

public final class Attributes {

  private final Map<Object, Object> attributes = new HashMap<Object, Object>();

  public Object get(Object key) {
    return attributes.get(key);
  }

  public Object get(Object key, Object fail) {
    Object value = attributes.get(key);
    if (value == null) {
      return fail;
    }
    return value;
  }

  public byte getByte(Object key) {
    Number n = (Number) get(key);
    if (n == null) {
      return 0;
    }
    return n.byteValue();
  }

  public double getDouble(Object key) {
    Number n = (Number) get(key);
    if (n == null) {
      return 0.0D;
    }
    return n.doubleValue();
  }

  public int getInt(Object key) {
    Number n = (Number) get(key);
    if (n == null) {
      return -1;
    }
    return n.intValue();
  }

  public long getLong(Object key) {
    Number n = (Number) get(key);
    if (n == null) {
      return 0L;
    }
    return n.longValue();
  }

  public short getShort(Object key) {
    Number n = (Number) get(key);
    if (n == null) {
      return 0;
    }
    return n.shortValue();
  }

  public boolean is(Object key) {
    Boolean b = (Boolean) get(key);
    if (b == null) {
      return false;
    }
    return b.booleanValue();
  }

  public boolean isSet(Object key) {
    return attributes.containsKey(key);
  }

  public void remove(Object key) {
    attributes.remove(key);
  }

  public void set(Object key, Object value) {
    attributes.remove(key);

    attributes.put(key, value);
  }

  public Map<Object, Object> getAttributes() {
    return attributes;
  }
}
