package com.jakestanger.discordBot;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.*;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import com.github.fedy2.weather.data.unit.Time;
import com.jakestanger.discordBot.audio.GuildMusicManager;
import com.jakestanger.discordBot.util.Lyrics;
import com.jakestanger.discordBot.util.Phrases;
import com.jakestanger.discordBot.util.ReadWrite;
import com.jakestanger.discordBot.wrapper.Song;
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
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Bot extends ListenerAdapter
{
	private static Bot instance;
	
	private SimpleLog logger;
	
	private HashMap<String, String> sounds;
	
	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;
	
	public Bot()
	{
		instance = this;
		logger = SimpleLog.getLog("Bot");
		logger.info("Creating new instance of Bot");
		//sounds = ReadWrite.readSounds();
		
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
		
		if(message.length() == 1 && StringUtils.isNumeric(message) && Integer.parseInt(message) <= 4) checkLyricsAnswer(Integer.parseInt(message), event);
		if(message.startsWith("@Grandad_Botbags")) reply(message.substring("@Grandad_Botbags".length()), channel);
		if(message.startsWith("!"))
		{
			String[] command = message.substring(("!").length()).split(" ");
			switch(command[0].toLowerCase())
			{
				case "play":
					if(command.length > 1)
						if (this.sounds.containsKey(command[1])) loadAndPlay(this.sounds.get(command[1]), channel);
						else loadAndPlay(command[1], channel);
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
				case "add":
					if(command.length > 2) addSound(channel, command[1], command[2]);
					else this.message(Phrases.UnknownCommand.getRandom(), channel);
					break;
				case "join":
					if(command.length > 1) joinChannel(command[1], event);
					else this.message(Phrases.NoChannel.getRandom(), channel);
					break;
				case "leave":
					leaveChannel(event);
					break;
				case "lyrics":
					if(command.length > 2) fetchLyrics(command[1], command[2], channel);
					else this.message(Phrases.UnknownCommand.getRandom(), channel);
					break;
				case "lyricsgame":
					if(command.length > 1) lyricsGame(command, channel);
					else this.message(Phrases.UnknownCommand.getRandom(), channel);
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
	 */
	private void loadAndPlay(String trackUrl, TextChannel channel)
	{
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track)
			{
				message("Adding *" + track.getInfo().title + "* to the queue", channel);
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
				message("*" + trackUrl + "* is not a valid sound source", channel);
			}
			
			@Override
			public void loadFailed(FriendlyException exception)
			{
				message("An error occurred: *" + exception.getMessage() + "*", channel);
				exception.printStackTrace();
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
	 * Adds a sound to the database for quick-playing
	 * @param channel The text channel
	 * @param name The sound friendly name
	 * @param url The sound URL
	 */
	private void addSound(TextChannel channel, String name, String url)
	{
		sounds.put(name, url);
		ReadWrite.addSound(name, url);
		message("Added sound *" + name + "* with url **" + url + "** to database.", channel);
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
				+ "**empty** - Empties the song queue\n"
				+ "**add <sound> <url>** - Add a sound to the local database\n"
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
		List<String> names = new ArrayList<>(sounds.keySet());
		names.sort(Comparator.naturalOrder());
		
		for(String name : names) message.append(name).append("\t\t**||**\t\t");
		
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
	
	private void fetchLyrics(String band, String song, TextChannel channel)
	{
		String urlExt = "/lyrics/" + band + "/" + song + ".html";
		String lyrics = Lyrics.fetchLyrics(urlExt, new Song(song, urlExt, band));
		for(String message : splitString(lyrics)) message(message, channel);
	}
	
	/**
	 * Lyrics trivia game
	 * @param command The full command
	 */
	private void lyricsGame(String command[], TextChannel channel)
	{
		List<String> artists = new ArrayList<>();
		for(String commandPart : command)
			if (!commandPart.equals("lyricsgame")) artists.add(commandPart);
		
		List<Song> allSongs = new ArrayList<>();
		for(String artist : artists)
		{
			List<Song> artistSongs = Lyrics.fetchSongs(artist);
			if(artistSongs != null) allSongs.addAll(artistSongs);
		}
		
		final int OPTIONS = 4;
		List<Song> options = new ArrayList<>();
		Random random = new Random();
		while(options.size() < OPTIONS)
		{
			Song song = allSongs.get(random.nextInt(allSongs.size()));
			if(!options.contains(song)) options.add(song);
		}
		
		int correctAnswerID = random.nextInt(OPTIONS);
		Song correctSong = options.get(correctAnswerID);
		
		String lyrics = Lyrics.fetchLyrics(correctSong.getLink(), correctSong);
		List<String> lyricsSplit = new ArrayList<>(Arrays.asList(lyrics.split("\n")));
		for(int i = lyricsSplit.size()-1; i > 0; i--)
			if(lyricsSplit.get(i).equals("") || lyricsSplit.get(i).equals("\n"))
				lyricsSplit.remove(i);
		
		final int LINES = 3;
		
		int startLine = random.nextInt(lyricsSplit.size()-3);
		StringBuilder displayedLyrics = new StringBuilder();
		for(int i = 0; i < LINES; i++)
			if(lyricsSplit.get(startLine+i) != null)
				displayedLyrics.append(lyricsSplit.get(startLine+i))
						.append("\n");
		
		StringBuilder finalMessage = new StringBuilder();
		finalMessage.append("Send a number between `1` and `" + OPTIONS + "` to select:\n\n");
		for(int i = 0; i < options.size(); i++)
			finalMessage.append((i+1))
					.append(". ")
					.append(options.get(i).getName())
					.append("\n");
		finalMessage.append("\n**").append(displayedLyrics).append("**");
		
		message(finalMessage.toString(), channel);
		
		Lyrics.GameData.setInLyricsMode(true);
		Lyrics.GameData.setCorrectAnswerID(correctAnswerID);
	}
	
	private void checkLyricsAnswer(int answer, MessageReceivedEvent event)
	{
		if(answer == Lyrics.GameData.getCorrectAnswerID() + 1)
		{
			Lyrics.GameData.incrementScore(event.getMember());
			message(event.getMember().getAsMention() + " Correct answer!", event);
			message("Your score is now `" + Lyrics.GameData.getScore(event.getMember()) + "`.", event);
		}
		else
		{
			Lyrics.GameData.resetScore(event.getMember());
			message(event.getMember().getAsMention() + " Wrong. Your score has been reset to 0", event);
			message("Your high score is `" + Lyrics.GameData.getHighScore(event.getMember()) + "`.", event);
		}
		
		Lyrics.GameData.setInLyricsMode(false);
	}
	
	/**
	 * Sends a message to CleverBot.
	 * Sends a message with the reply.
	 * @param message The message to process.
	 * @param channel The text channel
	 */
	private void reply(String message, TextChannel channel)
	{
		try
		{
			message = message.replace(" ", "%20"); //Encode spaces
			
			String ID = channel.getGuild().getId();
			
			URL url = new URL("http://api.acobot.net/get?bid=580&key=dZQNmytiaEu7QOAU&uid=" + ID + "&msg=" + message);
			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();
			
			String encoding = connection.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(stream, encoding);
			
			JSONObject json = new JSONObject(body);
			message(json.getString("cnt"), channel);
		}
		catch(Exception e)
		{
			logger.fatal("An error occurred when I tried to think.");
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
	private void message(String message, MessageReceivedEvent event)
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
