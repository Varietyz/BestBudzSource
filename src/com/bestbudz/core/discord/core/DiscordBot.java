package com.bestbudz.core.discord.core;

import com.bestbudz.core.discord.PluginManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class DiscordBot extends ListenerAdapter {
	private static final Logger logger = Logger.getLogger(DiscordBot.class.getSimpleName());

	private JDA jda;
	private DiscordConfig config;
	private PluginManager pluginManager;

	public DiscordBot(DiscordConfig config) {
		this.config = config;
		this.pluginManager = new PluginManager(this);
	}

	public CompletableFuture<Void> connect() {
		return CompletableFuture.runAsync(() -> {
			try {
				jda = JDABuilder.createDefault(config.getBotToken())
					.setActivity(Activity.playing("BestBudz while high"))
					.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
					.addEventListeners(this)
					.build();

				jda.awaitReady();
				pluginManager.loadPlugins();

			} catch (Exception e) {
				logger.severe("Failed to connect: " + e.getMessage());
				throw new RuntimeException(e);
			}
		});
	}

	public void disconnect() {
		if (jda != null) {
			pluginManager.unloadPlugins();
			jda.shutdown();
		}
	}

	public boolean isConnected() {
		return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
	}

	public JDA getJDA() {
		return jda;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}
}