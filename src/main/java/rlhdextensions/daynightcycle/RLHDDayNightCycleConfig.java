package rlhdextensions.daynightcycle;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("hdDayNight")
public interface RLHDDayNightCycleConfig extends Config
{
	@Range(
			min = 1,
			max = 50
	)
	@ConfigItem(
			keyName = "minBrightness",
			name = "Minimum brightness (Night)",
			description = "Darkest the night will be",
			position = 1
	)
	default int minBrightness()	{ return 1; }

	@Range(
			min = 1,
			max = 50
	)
	@ConfigItem(
			keyName = "maxBrightness",
			name = "Maximum brightness (Day)",
			description = "Brightest the day will be",
			position = 2
	)
	default int maxBrightness()	{ return 20; }

	@ConfigItem(
			keyName = "cycleMode",
			name = "Day/Night Cycle Mode",
			description = "Configures the day/night cycle mode",
			position = 3
	)
	default DayNightCycleMode cycleMode()
	{
		return DayNightCycleMode.REALTIME;
	}

	@Range (
			min = 1,
			max = 12
	)
	@ConfigItem(
			keyName = "customCycleHours",
			name = "Custom Cycle Length (hrs)",
			description = "Length of custom day/night cycle (hours)",
			position = 4
	)
	default int customCycleHours()
	{
		return 12;
	}

	@ConfigItem(
			keyName = "customCycleStartHour",
			name = "Custom Cycle Start Hour",
			description = "Starting hour of custom cycle (default midnight)",
			hidden = true
	)
	default int customCycleStartHour()
	{
		return 0;
	}
}



