package com.bestbudz.core.discord.events;

public abstract class DiscordEvent {
	private boolean cancelled = false;

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}