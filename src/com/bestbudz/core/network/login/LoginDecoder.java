package com.bestbudz.core.network.login;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.network.ISAACCipher;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.security.PasswordEncryption;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.content.io.sqlite.SQLiteDB;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LoginDecoder extends ByteToMessageDecoder {

	private static final int CLIENT_VERSION = 420;
	private static final SecureRandom secureRandom = new SecureRandom();

	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;
	private int state = CONNECTED;

	public static void sendReturnCode(ChannelHandlerContext ctx, int code) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(1);
		out.writeByte(code);
		ctx.writeAndFlush(out.getBuffer()).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Validates password against stored password (handles both encrypted and legacy plaintext)
	 * Creates new user if username doesn't exist
	 */
	private boolean validatePassword(String username, String enteredPassword) {
		Connection conn = SQLiteDB.getConnection();
		String sql = "SELECT password, password_encrypted FROM player WHERE username = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					System.out.println("[LoginDecoder] New user detected: " + username);
					return true;
				}

				String storedPassword = rs.getString("password");
				boolean isEncrypted = rs.getInt("password_encrypted") == 1;

				if (isEncrypted) {
					// Password is encrypted - verify using encryption utility
					return PasswordEncryption.verify(enteredPassword, storedPassword);
				} else {
					// Legacy plaintext password - direct comparison
					boolean matches = enteredPassword.equals(storedPassword);

					if (matches) {
						// Encrypt the password for future use
						migrateUserPassword(username, enteredPassword);
					}

					return matches;
				}
			}
		} catch (SQLException e) {
			System.err.println("[LoginDecoder] Database error during password validation: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Creates a new user with encrypted password
	 */
	private boolean createNewUser(String username, String password) {
		try {
			String encryptedPassword = PasswordEncryption.encrypt(password);
			if (encryptedPassword == null) {
				System.err.println("[LoginDecoder] Failed to encrypt password for new user: " + username);
				return false;
			}

			Connection conn = SQLiteDB.getConnection();
			String insertSql = "INSERT INTO player (username, password, password_encrypted) VALUES (?, ?, 1)";
			try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
				ps.setString(1, username);
				ps.setString(2, encryptedPassword);
				ps.executeUpdate();
				System.out.println("[LoginDecoder] Created new user: " + username);
				return true;
			}
		} catch (SQLException e) {
			System.err.println("[LoginDecoder] Failed to create new user " + username + ": " + e.getMessage());
			return false;
		}
	}

	/**
	 * Migrates a single user's password to encrypted format
	 */
	private void migrateUserPassword(String username, String plainPassword) {
		try {
			String encryptedPassword = PasswordEncryption.encrypt(plainPassword);
			if (encryptedPassword != null) {
				Connection conn = SQLiteDB.getConnection();
				String updateSql = "UPDATE player SET password = ?, password_encrypted = 1 WHERE username = ?";
				try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
					ps.setString(1, encryptedPassword);
					ps.setString(2, username);
					ps.executeUpdate();
					System.out.println("[LoginDecoder] Migrated password for user: " + username);
				}
			}
		} catch (SQLException e) {
			System.err.println("[LoginDecoder] Failed to migrate password for user " + username + ": " + e.getMessage());
		}
	}

	private Client login(ChannelHandlerContext ctx, ISAACCipher inCipher, ISAACCipher outCipher,
						 int version, String name, String pass, String uid) {

		if (name == null || name.trim().isEmpty() || name.length() > 12 || !name.matches("[A-Za-z0-9 ]+")) {
			sendReturnCode(ctx, Utility.LOGIN_RESPONSE_INVALID_CREDENTIALS);
			return null;
		}

		if (BestbudzConstants.STAFF_ONLY) {
			boolean isStaff = false;
			for (String usernames : BestbudzConstants.STAFF_MEMBERS) {
				if (name.equalsIgnoreCase(usernames)) {
					isStaff = true;
					break;
				}
			}
			if (!isStaff) {
				sendReturnCode(ctx, Utility.LOGIN_RESPONSE_INVALID_USERNAME);
				return null;
			}
		}

		for (String word : BestbudzConstants.BAD_USERNAMES) {
			if (name.toLowerCase().contains(word.toLowerCase())) {
				sendReturnCode(ctx, Utility.LOGIN_RESPONSE_INVALID_USERNAME);
				return null;
			}
		}

		if (World.worldUpdating) {
			sendReturnCode(ctx, Utility.LOGIN_RESPONSE_SERVER_BEING_UPDATED);
			return null;
		}

		name = name.trim();

		// Validate password using new encryption-aware method
		if (!validatePassword(name, pass)) {
			sendReturnCode(ctx, Utility.LOGIN_RESPONSE_INVALID_CREDENTIALS);
			return null;
		}

		ChannelPipeline pipeline = ctx.pipeline();
		pipeline.remove(this);
		pipeline.addFirst("decoder", new Decoder(inCipher));

		Client client = new Client(ctx.channel());
		Stoner stoner = client.getStoner();
		stoner.setUid(uid);
		stoner.setUsername(name);
		stoner.setDisplay(name);
		stoner.setPassword(pass); // Store the plaintext password for the session
		client.setEnteredPassword(pass);
		client.setEncryptor(outCipher);

		return client;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!ctx.channel().isActive()) return;

		if (state == CONNECTED) {
			if (in.readableBytes() < 2) return;

			int request = in.readUnsignedByte();
			if (request != 14) {
				System.out.println("Invalid login request: " + request);
				sendReturnCode(ctx, Utility.LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN);
				return;
			}

			in.readUnsignedByte(); // ignore
			StreamBuffer.OutBuffer resp = StreamBuffer.newOutBuffer(17);
			resp.writeLong(0);
			resp.writeByte(0);
			resp.writeLong(secureRandom.nextLong() ^ secureRandom.nextLong());
			ctx.writeAndFlush(resp.getBuffer());

			state = LOGGING_IN;
			return;
		}

		if (state == LOGGING_IN) {
			if (in.readableBytes() < 2) return;

			int loginType = in.readUnsignedByte();
			if (loginType != 16 && loginType != 18) {
				System.out.println("Invalid login type: " + loginType);
				ctx.close();
				return;
			}

			int blockLength = in.readUnsignedByte();
			int loginEncryptSize = blockLength - (36 + 1 + 1 + 2);
			if (loginEncryptSize <= 0 || in.readableBytes() < blockLength) {
				System.out.println("Invalid or incomplete encrypted login block");
				ctx.close();
				return;
			}

			in.readUnsignedByte(); // ignore
			int clientVersion = in.readUnsignedShort();
			int expectedVersion = CLIENT_VERSION;

			if (clientVersion != expectedVersion) {
				System.out.println("Invalid client version: " + clientVersion + " != " + expectedVersion);
				StreamBuffer.OutBuffer updateResp = StreamBuffer.newOutBuffer(3);
				updateResp.writeByte(Utility.LOGIN_RESPONSE_UPDATED);
				updateResp.writeByte(0);
				updateResp.writeByte(0);
				ctx.writeAndFlush(updateResp.getBuffer()).addListener(ChannelFutureListener.CLOSE);
				return;
			}

			in.readByte(); // memory version
			for (int i = 0; i < 9; i++) in.readInt(); // junk

			in.readByte(); // magic ID
			int rsaOpcode = in.readByte();
			if (rsaOpcode != 100) {
				System.err.println("RSA block decode failed.");
				ctx.close();
				return;
			}

			long clientHalf = in.readLong();
			long serverHalf = in.readLong();
			int[] seed = {
				(int) (clientHalf >> 32),
				(int) clientHalf,
				(int) (serverHalf >> 32),
				(int) serverHalf
			};

			ISAACCipher inCipher = new ISAACCipher(seed.clone());
			for (int i = 0; i < seed.length; i++) seed[i] += 50;
			ISAACCipher outCipher = new ISAACCipher(seed);

			int version = in.readInt();
			String uid = Utility.getRS2String(in);
			String name = Utility.getRS2String(in).trim();
			String pass = Utility.getRS2String(in);

			Client client = login(ctx, inCipher, outCipher, version, name, pass, uid);
			if (client != null) out.add(client);
		}
	}
}