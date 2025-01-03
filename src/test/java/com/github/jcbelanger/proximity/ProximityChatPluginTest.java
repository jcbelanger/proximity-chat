package com.github.jcbelanger.proximity;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ProximityChatPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ProximityChatPlugin.class);
		RuneLite.main(args);
	}
}