package com.bestbudz.rs2.content.io.sqlite;

import java.sql.*;

public final class SQLiteUtils {

  private SQLiteUtils() {}

  /** Executes a single SQL update statement with parameters. */
  public static void execute(String sql, SQLConsumer<PreparedStatement> binder) {
    try (Connection conn = SQLiteDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      binder.accept(ps);
      ps.executeUpdate();
    } catch (SQLException e) {
      System.err.println("[SQLiteUtils] SQL execution error:\n" + sql);
      e.printStackTrace();
    }
  }

  /** Executes a SELECT and lets the consumer process the ResultSet. */
  /*public static void query(
      String sql, SQLConsumer<PreparedStatement> binder, SQLConsumer<ResultSet> reader) {
    try (Connection conn = SQLiteDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      binder.accept(ps);
      try (ResultSet rs = ps.executeQuery()) {
        reader.accept(rs);
      }
    } catch (SQLException e) {
      System.err.println("[SQLiteUtils] SQL query error:\n" + sql);
      e.printStackTrace();
    }
  }*/ // this is a generic SELECT helper using functional/lambda style—very handy if i ever want to add admin commands, diagnostics, or future DAO methods.


  // --- Support functional interface for SQL lambdas ---
  @FunctionalInterface
  public interface SQLConsumer<T> {
    void accept(T t) throws SQLException;
  }
}
