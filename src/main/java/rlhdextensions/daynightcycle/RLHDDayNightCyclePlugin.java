package rlhdextensions.daynightcycle;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
@PluginDescriptor(
	name = "RLHD Day/Night Cycle"
)
public class RLHDDayNightCyclePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private RLHDDayNightCycleConfig config;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ConfigManager configManager;

	@Override
	protected void startUp() throws Exception
	{
		if(config.minBrightness() > config.maxBrightness()){
			log.info("Min brightness cannot be greater than max brightness");
			stopPlugin();
		}

		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	private boolean ran = false;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			if (configManager != null && !ran) {
				ran = true;

				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Config manager not null",null);
			}
		}
	}

	private int currentBrightness;

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		var timeOfDay = DayLight.getTimeOfDay(LocalTime.now());

		var minBrightness = config.minBrightness();
		var maxBrightness = config.maxBrightness();

		if(config.cycleMode() == DayNightCycleMode.REALTIME){
			if(timeOfDay == DayLight.NIGHT) {
				setBrightness(minBrightness);
				return;
			}

			var realTimeBrightness = realtimeCycleBrightness(timeOfDay, minBrightness,maxBrightness);
			setBrightness(realTimeBrightness);

			return;
		}

		var customStartTime = LocalTime.of(config.customCycleStartHour(), 0);
		var acceleratedBrightness = customCycleBrightness(customStartTime, minBrightness, maxBrightness);
		setBrightness(acceleratedBrightness);
	}

	private int realtimeCycleBrightness(DayLight timeOfDay, int minBrightness, int maxBrightness) {
		var daylightPct = timeOfDay.percentageOfDaylight(LocalTime.now());

		float brightnessPct = 0;
		if(daylightPct < 0.25){
			brightnessPct = (daylightPct * 4) * 100;
		} else if(daylightPct < 0.75){
			brightnessPct = 100;
		} else {
			// 0.75 to 1.00
			brightnessPct = (1 - (daylightPct)) * 100;
		}

		var brightness = (int) (minBrightness + ((maxBrightness - minBrightness) * (brightnessPct / 100)));

		var msg = "Daylight %: " + daylightPct + " Brightness: " + brightness;
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg,null);

		return brightness;
	}

	private int customCycleBrightness(LocalTime startTime, int minBrightness, int maxBrightness) {
		var cycleHours = config.customCycleHours();
		var cycleLengthInMs = cycleHours * 60 * 60 * 1000;

		var durationBetween = Duration.between(LocalTime.now(), startTime).toMillis();

		var daylightPct = (Math.abs((float)durationBetween) % cycleLengthInMs) / cycleLengthInMs;

		float brightnessPct = 0;
		if(daylightPct < 0.25){
			brightnessPct = (daylightPct * 4) * 100;
		} else if(daylightPct < 0.75){
			brightnessPct = 100;
		} else {
			// 0.75 to 1.00
			brightnessPct = (1 - (daylightPct)) * 4 * 100;
		}

		var brightness = (int) (minBrightness + ((maxBrightness - minBrightness) * (brightnessPct / 100)));

		var msg = "Daylight %: " + daylightPct + " Brightness: " + brightness;
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg,null);

		return brightness;
	}

	private void setBrightness(int brightness) {
		if(brightness == currentBrightness){
			return;
		}

		currentBrightness = brightness;
		configManager.setConfiguration("hd","brightness2", brightness);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if(event.getGroup() == "hdDayNight"){
			String key = event.getKey();
			switch(key) {
				case "daynightenabled":

			}
		}

		if(event.getGroup() == "hd"){
			var configChangedStr = event.getGroup() + " " + event.getKey() + " " + event.getNewValue();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Config Changed: " + configChangedStr,null);
		}
	}

	@Provides
	RLHDDayNightCycleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RLHDDayNightCycleConfig.class);
	}

	private void stopPlugin()
	{
		SwingUtilities.invokeLater(() ->
		{
			try
			{
				pluginManager.setPluginEnabled(this, false);
				pluginManager.stopPlugin(this);
			}
			catch (PluginInstantiationException ex)
			{
				log.error("error stopping plugin", ex);
			}
		});
	}
}
