package com.jakestanger.discordBot;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.*;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import com.github.fedy2.weather.data.unit.Time;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.jakestanger.discordBot.audio.GuildMusicManager;
import com.jakestanger.discordBot.util.Phrases;
import com.jakestanger.discordBot.util.ReadWrite;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.commons.lang3.text.WordUtils;

import javax.security.auth.login.LoginException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Bot extends ListenerAdapter
{
	private static Bot instance;
	
	private SimpleLog logger;
	
	private HashMap<String, File> sounds;
	
	private ChatterBotFactory chatterBotFactory;
	private ChatterBotSession cleverbotSession;
	
	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;
	
	public Bot()
	{
		instance = this;
		logger = SimpleLog.getLog("GrandadBotbags");
		logger.info("Creating new instance of com.jakestanger.discordBot.commands");
		sounds = ReadWrite.readSounds();
		
		chatterBotFactory = new ChatterBotFactory();
		
		musicManagers = new HashMap<>();
		playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}
	
	public static Bot getInstance()
	{
		return instance;
	}
	
	/**
	 * Starts the application and joins the bot to the server
	 * @return Whether or not the initialisation succeeded
	 */
	public boolean init(String token)
    {
        logger.info("Initialising bot");
		boolean success = true;
		
		try
        {
            new JDABuilder(AccountType.BOT).setToken(token).addListener(this).buildBlocking();
            //jda.getAccountManager().setAvatar(AvatarUtil.getAvatar(new File(new ResourceLocation("/images/avatar.png").getPath()))).update(); //Only enable when updating avatar
	
	        ChatterBot cleverbot = chatterBotFactory.create(ChatterBotType.CLEVERBOT);
            cleverbotSession = cleverbot.createSession();
        }
        catch (IllegalArgumentException e)
        {
            logger.fatal("The config was not populated. Please enter a token.");
            success = false;
        }
        catch (LoginException e)
        {
        	logger.fatal("The provided token was incorrect. Please provide valid details.");
            success = false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            success = false;
        }
		catch(Exception e)
		{
			logger.fatal("Unknown error starting bot");
			e.printStackTrace();
		}
		
		return success;
    }
	
	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild)
	{
		long guildId = Long.parseLong(guild.getId());
		GuildMusicManager musicManager = musicManagers.computeIfAbsent(guildId, k -> new GuildMusicManager(playerManager));
		
		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
		
		return musicManager;
	}
	 
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		
		String message = event.getMessage().getContent();
		TextChannel channel = event.getTextChannel();
		
		this.grammarNazi(message, event);
		if(message.startsWith("@Grandad_Botbags")) cleverbot(message.substring("@Grandad_Botbags".length()), event);
		if(message.startsWith("!"))
		{
			String[] command = message.substring(("!").length()).split(" ");
			switch(command[0].toLowerCase())
			{
				case "play":
					if(command.length > 1)
					{
						if(this.sounds.containsKey(command[1]))
							loadAndPlay(this.sounds.get(command[1]).getAbsolutePath(), channel, command[1]);
						else loadAndPlay(command[1], channel, null);
					}
					else this.helpPlay(channel);
					break;
				case "pause":
					togglePause(channel);
					break;
				case "skip":
					skipTrack(channel);
					break;
				case "empty":
					emptyQueue(channel);
					break;
				case "join":
					if(command.length > 1) joinChannel(command[1], event);
					else this.message(Phrases.NoChannel.getRandom(), channel);
					break;
				case "leave":
					leaveChannel(event);
					break;
				case "lyrics":
					fetchLyrics(command[1], command[2], channel);
					break;
				case "weather":
					weather(channel);
					break;
				case "reload":
					reload(event);
					break;
				case "help":
					help(channel);
					break;
				default:
					this.message(Phrases.UnknownCommand.getRandom(), channel);
			}
		}
		else event.getMessage().deleteMessage();
	}
	
	/**
	 * Load and play the given audio stream
	 * @param trackUrl The path to audio stream
	 * @param channel The text channel
	 * @param trackName The name of the track (instead of the metadata title)
	 */
	private void loadAndPlay(String trackUrl, TextChannel channel, String trackName)
	{
		System.out.println(trackName);
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track)
			{
				String title = track.getInfo().title;
				if(trackName != null) message("Adding  *" + trackName + "* to the queue", channel);
				else message("Adding *" + title + "* to the queue", channel);
				
				play(channel.getGuild(), musicManager, track);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist)
			{
				AudioTrack firstTrack = playlist.getSelectedTrack();
				
				if(firstTrack == null) firstTrack = playlist.getTracks().get(0);
				
				message("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")", channel);
				
				play(channel.getGuild(), musicManager, firstTrack);
			}
			
			@Override
			public void noMatches()
			{
				if(trackName == null) message("*" + trackUrl + "* is not a valid sound source", channel);
			}
			
			@Override
			public void loadFailed(FriendlyException exception)
			{
				message("An error occurred: *" + exception.getMessage() + "*", channel);
			}
		});
	}
	
	/**
	 * Play the given track
	 * @param guild The server
	 * @param musicManager The music manager
	 * @param track The track to play
	 */
	private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track)
	{
		connectToFirstVoiceChannel(guild.getAudioManager());
		musicManager.scheduler.queue(track);
	}
	
	/**
	 * Skip to the next track in the queue
	 * @param channel The text channel
	 */
	private void skipTrack(TextChannel channel)
	{
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.nextTrack();
		
		message("Skipped to next track.", channel);
	}
	
	/**
	 * Connect to the primary voice channel
	 * @param audioManager The audio manager
	 */
	private static void connectToFirstVoiceChannel(AudioManager audioManager)
	{
		if(!audioManager.isConnected() && !audioManager.isAttemptingToConnect())
			audioManager.openAudioConnection(audioManager.getGuild().getVoiceChannels().get(0));
	}

	
	/**
	 * Toggles whether the player is paused or not
	 * @param channel The text channel the command was sent from
	 */
	private void togglePause(TextChannel channel)
	{
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.player.setPaused(!musicManager.player.isPaused());
	}
	
	/**
	 * Remove all the tracks from the queue.
	 * Method is a bit unclean.
	 * @param channel The text channel.
	 */
	private void emptyQueue(TextChannel channel)
	{
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		while(musicManager.player.getPlayingTrack() != null) musicManager.scheduler.nextTrack();
	}
	
	/**
	 * Joins the given channel
	 * @param channelName The name of the channel to join.
	 * @param event The message received event.
	 */
	private void joinChannel(String channelName, MessageReceivedEvent event)
	{
		if(event.getMember().hasPermission(Permission.VOICE_CONNECT))
		{
			if(event.getGuild().getAudioManager() != null) event.getGuild().getAudioManager().closeAudioConnection(); //Close existing voice connection
			//Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
	        VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vChan -> vChan.getName().equalsIgnoreCase(channelName)).findFirst().orElse(null);
	        if (channel == null)
	        {
	            this.message(Phrases.UnknownChannel.getRandom(), event.getTextChannel());
	            return;
	        }
	        event.getGuild().getAudioManager().openAudioConnection(channel);
		}
		else this.message(Phrases.BadPermission.getRandom(), event.getTextChannel());
	}
	
	/**
	 * Leaves the current channel
	 * @param event The message received event.
	 */
	private void leaveChannel(MessageReceivedEvent event)
	{
		if(event.getMember().hasPermission(Permission.VOICE_CONNECT)) event.getGuild().getAudioManager().closeAudioConnection();
		else this.message(Phrases.BadPermission.getRandom(), event.getTextChannel());
	}
	
	/**
	 * Shows a list of commands and their purpose.
	 * A little hard-coded, but there you go.
	 * @param channel The message received event.
	 */
	private void help(TextChannel channel)
	{
		this.message("***Commands:***\n"
				+ "**play [sound]** - Shows a list of sounds or [plays the given sound]\n"
				+ "**pause** - Toggle-pauses the audio player\n"
				+ "**skip** - Skips the current audio track\n"
				+ "**join <channel>** - Joins the given voice channel\n"
				+ "**leave** - Leaves the current voice channel\n"
				+ "**weather** - Gives a *very* useful weather report\n"
				+ "**lyrics <band> <song>** - Shows the lyrics for the given song\n"
				+ "**help** - Shows a list of commands\n", channel);
	}
	
	/**
	 * Shows a list of sounds
	 * @param channel The message received event.
	 */
	private void helpPlay(TextChannel channel)
	{
		StringBuilder message = new StringBuilder();
		boolean bold = true;
		for(String command : this.sounds.keySet()) //Loop over every sound file
		{
			message.append(bold ? "**" : "").append(command).append(bold ? "**" : "").append("    ");
			bold = !bold;
		}
		message(message.toString(), channel);
	}
	
	private void reload(MessageReceivedEvent event)
	{
		if(event.getMember().hasPermission(Permission.MANAGE_SERVER))
		{
			this.sounds = ReadWrite.readSounds();
			this.message("Sounds reloaded", event.getTextChannel());
		}
		else this.message(Phrases.BadPermission.getRandom(), event.getTextChannel());
	}
	
	/**
	 * Corrects people's annoying SPAG mistakes.
	 * @param message The message to correct.
	 * @param event The message received event.
	 */
	private void grammarNazi(String message, MessageReceivedEvent event)
	{
		//SpellResponse spellResponse = this.spellChecker.check(message);
		//for(SpellCorrection correction : spellResponse.getCorrections()) this.message(correction.getValue(), event);
		
		message = message.toLowerCase();
		if(event.getMessage().getContent().equals("^")) event.getMessage().deleteMessage();
		if(message.equals("ping")) message("pong", event.getTextChannel());
		if(message.contains(" alot ") || message.contains(" alot") || message.contains("alot ")) message("*A lot", event.getTextChannel());
		if(message.contains("any one")) message("*Anyone", event.getTextChannel());
		if(message.contains("reminder that")) message("You've just earned yourself a one-way ticket to hell.", event.getTextChannel());
		if(message.contains(" u ")) message("Text talk? How about a good old knife to the throat?", event.getTextChannel());
		if(message.contains("yeh")) message("*Yeah", event.getTextChannel());
	}
	
	private void fetchLyrics(String band, String song, TextChannel channel)
	{
		try
		{
			String allHTML = new Scanner(new URL("http://www.azlyrics.com/lyrics/" + band + "/" + song + ".html").openStream(), "UTF-8").useDelimiter("\\A").next();
			String unformatted = String.valueOf(allHTML.split("<div>")[1]).split("</div>")[0];
			String formatted = unformatted.replace("<br>", "")
					.replace("<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->", "")
					.replace("<i>", "*").replace("</i>", "*")
					.replace("<b>", "**").replace("</b>", "**")
					.replace("&quot", "\"");
			
			for(String part : splitString(formatted))
			{
				System.out.println(part.length());
				message(part, channel);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to CleverBot.
	 * Sends a message with the reply.
	 * @param message The message to process.
	 * @param event The message received event.
	 */
	private void cleverbot(String message, MessageReceivedEvent event)
	{
		try
		{
			message(this.cleverbotSession.think(message), event.getTextChannel());
		}
		catch(Exception e)
		{
			logger.fatal("An error occurred getting Cleverbot to think. Sorry about that.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the current weather
	 * @param textChannel The message received event.
	 */
	private void weather(TextChannel textChannel)
	{
		message("***Hold on a second...***", textChannel);
		
		try
		{
			YahooWeatherService service = new YahooWeatherService();
			Channel forecastChannel = service.getForecast("15051", DegreeUnit.CELSIUS);
			
			StringBuilder report = new StringBuilder();
			
			Astronomy astronomy = forecastChannel.getAstronomy();
			Time sunrise = astronomy.getSunrise();
			Time sunset = astronomy.getSunset();
			
			String sunriseS = sunrise.getHours() + ":" + sunrise.getMinutes() + sunrise.getConvention();
			String sunsetS = sunset.getHours() + ":" + sunset.getMinutes() + sunset.getConvention();
			
			Atmosphere atmosphere = forecastChannel.getAtmosphere();
			String humidity = Integer.toString(atmosphere.getHumidity()) + "%";
			String pressure = Float.toString(atmosphere.getPressure()) + "mb"; //Pressure returns very broken value
			String pressureState = WordUtils.capitalize(atmosphere.getRising().name().toLowerCase());
			String visibility = Float.toString(atmosphere.getVisibility()) + "km";
			
			Wind wind = forecastChannel.getWind();
			String speed = Float.toString(wind.getSpeed()) + "km/h";
			String direction = Integer.toString(wind.getDirection()) + "\u00B0";
			String chill = Float.toString(Math.round(((wind.getChill()-32f)/1.8f) * 10f) / 10f) + "\u00B0C"; //For some reason this is in Fahrenheit. It also needs rounding.
			
			Item item = forecastChannel.getItem();
			Condition condition = item.getCondition();
			String temperature = Integer.toString(condition.getTemp()) + "\u00B0C";
			String description = condition.getText();
			
			List<Forecast> forecasts = item.getForecasts();
			List<String> forecastsS = new ArrayList<>();
			for(Forecast forecast : forecasts)
			{
				StringBuilder forecastS = new StringBuilder();
				
				String day = WordUtils.capitalize(forecast.getDay().name().toLowerCase());
				String high = Integer.toString(forecast.getHigh()) + "\u00B0C";
				String low = Integer.toString(forecast.getLow()) + "\u00B0C";
				String forecastDesc = forecast.getText();
				
				forecastS.append(Phrases.Weather.Forecast.Day.getRandom()).append("**").append(day).append("**\n");
				forecastS.append(Phrases.Weather.Forecast.High.getRandom()).append("**").append(high).append("**\n");
				forecastS.append(Phrases.Weather.Forecast.Low.getRandom()).append("**").append(low).append("**\n");
				forecastS.append(Phrases.Weather.Forecast.Desc.getRandom()).append("**").append(forecastDesc).append("**\n\n");
				
				forecastsS.add(forecastS.toString());
			}
			
			report.append("***").append(Phrases.Weather.Intro.getRandom()).append("***\n\n");
			
			report.append(Phrases.Weather.Temp.getRandom()).append("**").append(temperature).append("**\n");
			report.append(Phrases.Weather.Description.getRandom()).append("**").append(description).append("**\n\n");
			
			report.append(Phrases.Weather.Wind.getRandom()).append("**").append(speed).append("**\n");
			report.append(Phrases.Weather.Direction.getRandom()).append("**").append(direction).append("**\n");
			report.append(Phrases.Weather.Chill.getRandom()).append("**").append(chill).append("**\n");
			report.append(Phrases.Weather.Humidity.getRandom()).append("**").append(humidity).append("**\n\n");
			
			//report.append(Phrases.Weather.Pressure.getRandom()).append("**").append(pressure).append("** (This appears to be completely broken :(\n"); //TODO Investigate why this is broken
			report.append(Phrases.Weather.PressureState.getRandom()).append("**").append(pressureState).append("**\n");
			report.append(Phrases.Weather.Visibility.getRandom()).append("**").append(visibility).append("**\n\n");
			
			report.append(Phrases.Weather.Sunrise.getRandom()).append("**").append(sunriseS).append("**\n");
			report.append(Phrases.Weather.Sunset.getRandom()).append("**").append(sunsetS).append("**\n\n");
			
			StringBuilder forecastSB = new StringBuilder();
			forecastSB.append("***").append(Phrases.Weather.Forecast.Intro.getRandom()).append("***\n \n");
			forecastsS.forEach(forecastSB::append);
			
			message(report.toString(), textChannel);
			message(forecastSB.toString(), textChannel);
		}
		catch(JAXBException | IOException | IllegalStateException e)
		{
			e.printStackTrace();
			message("Something went wrong. You'll have to look outside. Sorry.", textChannel);
		}
	}
	
	/**
	 * Sends a message on both the bot and server
	 * @param message The message to send.
	 * @param event The message received event.
	 */
	private void message(String message, GuildMessageReceivedEvent event)
	{
		System.out.println("[Message] " + message);
		event.getChannel().sendMessage(message).queue();
	}
	
	/**
	 * Sends a message on both the bot and server
	 * @param message The message to send.
	 * @param channel The text channel
	 */
	private void message(String message, TextChannel channel)
	{
		System.out.println("[Message] " + message);
		channel.sendMessage(message).queue();
	}
	
	private String[] splitString(String s)
	{
		int interval = 2000;
		
		int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
		String[] result = new String[arrayLength];
		
		int j = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++)
		{
			result[i] = s.substring(j, j + interval);
			j += interval;
		} //Add the last bit
		result[lastIndex] = s.substring(j);
		
		return result;
	}
}
