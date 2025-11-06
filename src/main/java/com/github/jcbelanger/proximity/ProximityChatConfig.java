package com.github.jcbelanger.proximity;

import net.runelite.api.events.ClientTick;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.eventbus.Subscribe;

@ConfigGroup("example")
public interface ProximityChatConfig extends Config {
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting(){
		return "Hello";
	}

}
