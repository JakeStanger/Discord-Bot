package com.jakestanger.discordBot;

import com.jakestanger.discordBot.audio.GuildMusicManager;
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
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.SimpleLog;

import javax.security.auth.login.LoginException;
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
		sounds = ReadWrite.readSounds();
		
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
					else this.message("Missing argument", channel);
					break;
				case "join":
					if(command.length > 1) joinChannel(command[1], event);
					else this.message("Unknown channel", channel);
					break;
				case "leave":
					leaveChannel(event);
					break;
				case "reload":
					reload(event);
					break;
				case "help":
					help(channel);
					break;
				default:
					this.message("Unknown command.", channel);
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
	            this.message("Unknown channel.", event.getTextChannel());
	            return;
	        }
	        event.getGuild().getAudioManager().openAudioConnection(channel);
		}
		else this.message("You do not have permission.", event.getTextChannel());
	}
	
	/**
	 * Leaves the current channel
	 * @param event The message received event.
	 */
	private void leaveChannel(MessageReceivedEvent event)
	{
		if(event.getMember().hasPermission(Permission.VOICE_CONNECT)) event.getGuild().getAudioManager().closeAudioConnection();
		else this.message("You do not have permission.", event.getTextChannel());
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
		else this.message("You do not have permission.", event.getTextChannel());
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
}
