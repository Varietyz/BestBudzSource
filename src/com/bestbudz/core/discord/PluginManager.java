package com.bestbudz.core.discord;

import com.bestbudz.core.discord.core.DiscordBot;
import com.bestbudz.core.discord.core.DiscordPlugin;
import com.bestbudz.core.discord.plugins.ChatBridgePlugin;
import com.bestbudz.core.discord.plugins.ExamplePlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PluginManager {
	private static final Logger logger = Logger.getLogger(PluginManager.class.getSimpleName());

	private final DiscordBot bot;
	private final List<DiscordPlugin> plugins = new ArrayList<>();

	public PluginManager(DiscordBot bot) {
		this.bot = bot;
	}

	public void loadPlugins() {
		// Auto-discover and load plugins here
		logger.info("Loading Discord plugins...");

		registerPlugin(new ExamplePlugin());
		registerPlugin(new ChatBridgePlugin());

		logger.info("Loaded " + plugins.size() + " Discord plugins");
	}

	public void unloadPlugins() {
		plugins.forEach(DiscordPlugin::onDisable);
		plugins.clear();
	}

	public void registerPlugin(DiscordPlugin plugin) {
		plugins.add(plugin);
		plugin.onEnable(bot);
		logger.info("Registered plugin: " + plugin.getName());
	}

	public List<DiscordPlugin> getPlugins() {
		return new ArrayList<>(plugins);
	}
}