package rlhdextensions.daynightcycle;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RLHDDayNightCyclePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RLHDDayNightCyclePlugin.class);
		RuneLite.main(args);
	}
}