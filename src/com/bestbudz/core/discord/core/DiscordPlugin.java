package com.bestbudz.core.discord.core;

public abstract class DiscordPlugin {

	public abstract String getName();

	public abstract void onEnable(DiscordBot bot);

	public abstract void onDisable();

	public String getVersion() {
		return "1.0.0";
	}

	public String getDescription() {
		return "";
	}
}