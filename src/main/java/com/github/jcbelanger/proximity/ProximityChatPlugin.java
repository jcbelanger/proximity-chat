package com.github.jcbelanger.proximity;

import com.github.jcbelanger.proximity.mumble.win32.MumbleLink;
import com.github.jcbelanger.proximity.mumble.win32.MumbleLinkConnection;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Proximity Chat")
public class ProximityChatPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ProximityChatConfig config;

	private MumbleLinkConnection mumble;

	@Override
	protected void startUp() throws Exception {
		log.debug("Proximity Chat started!");
		connect();
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("Proximity Chat stopping.");
		disconnect();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			onLoggedIn();
		} else if (gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
			onConnectionLost();
		} else if (gameStateChanged.getGameState() == GameState.HOPPING) {
			onHopping();
		} else if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
			onLoginScreen();
		}
	}

	private void onLoginScreen() {
		mumble.update(lm -> {
			lm.setIdentity(null);
			lm.setContext(null);
		});
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick) {
		mumble.update(lm -> {
			//https://www.mumble.info/documentation/developer/positional-audio/create-plugin/#data
			WorldPoint loc = client.getLocalPlayer().getWorldLocation();
			LocalPoint camera = client.getLocalPlayer().getCameraFocus();

			lm.fAvatarPosition[0] = loc.getX();
			lm.fAvatarPosition[1] = loc.getY();
			lm.fAvatarPosition[2] = loc.getPlane();

			lm.fCameraPosition[0] = lm.fAvatarPosition[0];
			lm.fCameraPosition[1] = lm.fAvatarPosition[1];
			lm.fCameraPosition[2] = lm.fAvatarPosition[2];
//
//			lm.fCameraFront[0] = (float) client.getCameraFpX();
//			lm.fCameraFront[1] = (float) client.getCameraFpY();
//			lm.fCameraFront[2] = (float) client.getCameraFpZ();
//
//			lm.fCameraTop[0] = (float) client.
//			lm.fCameraTop[1] = (float) client.
//			lm.fCameraTop[2] = (float) client.

			if (lm.fAvatarPosition[0] == 0 && lm.fAvatarPosition[1] == 0 && lm.fAvatarPosition[2] == 0) {
				lm.fAvatarPosition[0] = 0.001f;
			}

			lm.setContext(getContext());
		});
	}

	private String getContext() {
		var context = new JsonObject();
		context.addProperty("world", client.getWorld());
		context.addProperty("plane", client.getLocalPlayer().getWorldLocation().getPlane());
		return context.toString();
	}

	private void onLoggedIn() {
		mumble.update(lm -> {
			lm.setIdentity(getIdentity());
			lm.setContext(getContext());
		});
	}

	private String getIdentity() {
		var identity = new JsonObject();
		identity.addProperty("username", client.getLocalPlayer().getName());
		identity.addProperty("team", client.getLocalPlayer().getTeam());
		return identity.toString();
	}

	private void onConnectionLost() {
		mumble.update(lm -> {
			lm.setIdentity(null);
			lm.setContext(null);
		});
	}

	private void onHopping() {
		if (mumble == null) return;
		mumble.update(lm -> lm.setContext(null));
	}

	private void disconnect() {
        try {
			if (mumble != null) {
				mumble.close();
				mumble = null;
			}
        } catch (Exception e) {
			log.error("Error disconnecting from mumble", e);
        }
	}

	private void connect() {
		try {
			if (mumble != null) {
				disconnect();
			}
			mumble = MumbleLink.connect();
		} catch (MumbleLink.MumbleConnectException e) {
			log.error("Error connecting to mumble", e);
		}
	}

	@Provides
	ProximityChatConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ProximityChatConfig.class);
	}

}
