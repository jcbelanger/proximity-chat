package com.github.jcbelanger.proximity;

import com.github.jcbelanger.proximity.discord.DiscordSocialSDK;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Proximity Chat")
public class ProximityChatPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private ProximityChatConfig config;

	@Override
	protected void startUp() throws Exception {
		log.debug("Proximity started!");

		DiscordSocialSDK.DiscordNative lib = DiscordSocialSDK.getInstance();
		DiscordSocialSDK.Discord_Client client = new DiscordSocialSDK.Discord_Client();
		lib.Discord_Client_Init(client);

		log.debug("Discord Social SDK loaded");
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("Proximity stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Proximity says " + config.greeting(), null);
		}
	}

	@Provides
	ProximityChatConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ProximityChatConfig.class);
	}
}
