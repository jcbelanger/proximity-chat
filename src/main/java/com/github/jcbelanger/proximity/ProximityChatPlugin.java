package com.github.jcbelanger.proximity;

import com.google.common.io.Closer;
import com.google.inject.Provides;
import com.sun.jna.Callback;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.github.jcbelanger.proximity.Discord.*;
import static com.github.jcbelanger.proximity.Discord.Discord_AuthorizationArgs_Drop;
import static com.github.jcbelanger.proximity.Discord.Discord_AuthorizationArgs_SetClientId;
import static com.github.jcbelanger.proximity.Discord.Discord_AuthorizationCodeVerifier_Challenge;
import static com.github.jcbelanger.proximity.Discord.Discord_AuthorizationCodeVerifier_Drop;
import static com.github.jcbelanger.proximity.Discord.Discord_Client_CreateAuthorizationCodeVerifier;
import static com.github.jcbelanger.proximity.Discord.Discord_Client_Init;
import static com.github.jcbelanger.proximity.Discord.Discord_RunCallbacks;

@Slf4j
@PluginDescriptor(name = "Proximity Chat")
public class ProximityChatPlugin extends Plugin {

	public static final long APPLICATION_ID = 1526593769569521715L;

	private Discord_Client discordClient;

	@Inject
	private ScheduledExecutorService executor;

	private ScheduledFuture<?> discordCallbacksTask;

	@Inject
	private Client client;

	@Inject
	private ProximityChatConfig config;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	List<Callback> preventGCCallbacks = new ArrayList<>();

	public <T extends Callback> T preventGC(T cb) {
		preventGCCallbacks.add(cb);
		return cb;
	}


	@Override
	protected void startUp() throws Exception {
		Native.setProtected(developerMode);

		log.debug("Proximity started!");

		discordClient = new Discord_Client();
		Discord_Client_Init(discordClient);

		Discord_Client_AddLogCallback(discordClient, preventGC((message, severity, userData) -> {
			String msg = Discord.discordStringToJava(message);
			if (severity == Discord_LoggingSeverity.Discord_LoggingSeverity_Verbose) {
				log.trace(msg);
			} else if (severity == Discord_LoggingSeverity.Discord_LoggingSeverity_Info) {
				log.info(msg);
			} else if (severity == Discord_LoggingSeverity.Discord_LoggingSeverity_Warning) {
				log.warn(msg);
			} else if (severity == Discord_LoggingSeverity.Discord_LoggingSeverity_Error) {
				log.error(msg);
			} else {
				log.info(msg);
			}
		}), null, null, Discord_LoggingSeverity.Discord_LoggingSeverity_Verbose);

		Discord_Client_SetStatusChangedCallback(discordClient, preventGC((status, error, errorDetail, userData) -> {
			var statusDStr = new Discord_String();
			Discord_Client_StatusToString(status, statusDStr);
			log.info("Status changed {}", statusDStr);

			if (status == Discord_Client_Status.Discord_Client_Status_Ready) {
				log.info("✅ Client is ready! You can now call SDK functions");
			} else if (error != Discord_Client_Error.Discord_Client_Error_None) {
				var errorDStr = new Discord_String();
				Discord_Client_ErrorToString(error, errorDStr);
				log.error("❌ Connection Error: {}  - Details: {}", errorDStr, errorDetail);
			}
		}), null, null);

		// Set up authentication arguments
		var codeVerifier = new Discord_AuthorizationCodeVerifier();
		Discord_Client_CreateAuthorizationCodeVerifier(discordClient, codeVerifier);
//			closer.register(() -> Discord_AuthorizationCodeVerifier_Drop(codeVerifier));

		var defaultScopes = new Discord_String();
		Discord_Client_GetDefaultPresenceScopes(defaultScopes);

		var challenge = new Discord_AuthorizationCodeChallenge();
		Discord_AuthorizationCodeVerifier_Challenge(codeVerifier, challenge);
//			closer.register(() -> Discord_AuthorizationCodeChallenge_Drop(challenge));

		var args = new Discord_AuthorizationArgs();
		Discord_AuthorizationArgs_Init(args);
//			closer.register(() -> Discord_AuthorizationArgs_Drop(args));
		Discord_AuthorizationArgs_SetClientId(args, APPLICATION_ID);
		Discord_AuthorizationArgs_SetScopes(args, convertToByValue(defaultScopes));
		Discord_AuthorizationArgs_SetCodeChallenge(args, challenge);

		// Begin authentication process
		Discord_Client_Authorize(discordClient, args, preventGC((result, code, redirectUri, userData) -> {
			if (Discord_ClientResult_Successful(result) == 0) {
				var errorDStr = new Discord_String();
				Discord_ClientResult_Error(result, errorDStr);
				log.error("❌ Authentication Error: {}", errorDStr);
				return;
			} else {
				log.info("✅ Authorization successful! Getting access token...");
			}

			var verifier = new Discord_String.ByValue();
			Discord_AuthorizationCodeVerifier_Verifier(codeVerifier, verifier);

			Discord_Client_GetToken(discordClient, APPLICATION_ID, code, verifier, redirectUri, preventGC((result1, accessToken, refreshToken, tokenType, expiresIn, scopes, userData1) -> {
				log.info("🔓 Access token received! Establishing connection...");

				Discord_AuthorizationArgs_Drop(args);
				Discord_AuthorizationCodeChallenge_Drop(challenge);
				Discord_AuthorizationCodeVerifier_Drop(codeVerifier);
			}), null, null);

		}), null, null);

		discordCallbacksTask = executor.scheduleAtFixedRate(Discord::Discord_RunCallbacks, 1, 10, TimeUnit.MILLISECONDS);
		log.debug("Discord Social SDK loaded");
	}

	@Override
	protected void shutDown() throws Exception {
		if (discordCallbacksTask != null) {
			discordCallbacksTask.cancel(true);
		}
		if (discordClient != null) {
			Discord_Client_Drop(discordClient);
			discordClient = null;
		}
		preventGCCallbacks.clear();
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
