package com.bestbudz.tools;

import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class JsonSaver {

  private final Gson serializer =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final JsonArray array = new JsonArray();

  private final boolean singletonTable;

  private JsonObject currentWriter = new JsonObject();

  public JsonSaver(boolean singletonTable) {
    this.singletonTable = singletonTable;
  }

  public JsonSaver() {
    this(false);
  }

  @Override
  public String toString() {
    if (singletonTable) return serializer.toJson(currentWriter);
    if (currentWriter.entrySet().size() > 0) split();
    return serializer.toJson(array);
  }

  public void split() {
    array.add(currentWriter);
    currentWriter = new JsonObject();
  }

  public JsonObject current() {
    return currentWriter;
  }

  public Gson serializer() {
    return serializer;
  }

  public void publish(String path) {
    try (FileWriter fw = new FileWriter(path)) {
      fw.write(toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
