package bot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.SimpleLog;
import util.ResourceLocation;

public class Bot extends ListenerAdapter
{
	private static Bot instance;
	
	private SimpleLog logger;
	private Player player = null;
	private Random random;
	private HashMap<String, File> sounds;
	
	private static final String NAME = "@Grandad_Botbags";
	
	public Bot()
	{
		instance = this;
		logger = SimpleLog.getLog("GrandadBotbags");
		random = new Random();
		sounds = this.getSounds();
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
        boolean success = true;
		
		try
        {
            new JDABuilder().setBotToken(token).addListener(new Bot()).buildBlocking();
        }
        catch (IllegalArgumentException e)
        {
            logger.fatal("The config was not populated. Please enter an email and password.");
            success = false;
        }
        catch (LoginException e)
        {
        	logger.fatal("The provided email / password combination was incorrect. Please provide valid details.");
            success = false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            success = false;
        }
		
		return success;
    }
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		String message = event.getMessage().getContent();
		
		this.grammarNazi(message, event);
		if(message.contains(NAME))
		{
			String[] command = message.substring((NAME + " ").length()).split(" ");
			switch(command[0])
			{
				case "play":
					if(command[1] != null) playSound(command[1], event);
					else this.message("Play what?!", event);
					break;
				case "stop":
					stopSound();
					break;
				case "join":
					if(command[1] != null) joinChannel(command[1], event);
					else this.message("Join what?!", event);
					break;
				case "leave":
					leaveChannel(event);
					break;
				case "coin":
					coin(event);
					break;
				case "dice":
					dice(event);
					break;
				case "randuser":
					randUser(event);
					break;
				default:
					this.message("You might as well have just spoken in Greek.", event);
			}
		}
	}
	
	public void message(String message, GuildMessageReceivedEvent event)
	{
		System.out.println("[Message] " + message);
		event.getChannel().sendMessage(message);
	}
	
	private void playSound(String sound, GuildMessageReceivedEvent event)
	{
		File audioFile = null;
        try
        {
            if(this.sounds.containsKey(sound)) audioFile = this.sounds.get(sound);
            else
            {
            	this.message("That's not a thing.", event);
            	return;
            }

            player = new FilePlayer(audioFile);
            event.getGuild().getAudioManager().setSendingHandler(player);
            player.play();
        }
        catch (IOException e)
        {
            this.message("That's not a thing.", event);
        }
        catch (UnsupportedAudioFileException e)
        {
           this.message("I can't read that sort of file.", event);
        }
	}
	
	private void stopSound()
	{
		player.stop();
	}
	
	private void joinChannel(String channelName, GuildMessageReceivedEvent event)
	{
		//Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
        VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vChan -> vChan.getName().equalsIgnoreCase(channelName)).findFirst().orElse(null);  
        //If there isn't a matching name, return null.
        if (channel == null)
        {
            this.message("There isn't a VoiceChannel in this Guild with the name: '" + channelName + "'", event);
            return;
        }
        event.getGuild().getAudioManager().openAudioConnection(channel);
	}
	
	private void leaveChannel(GuildMessageReceivedEvent event)
	{
		event.getGuild().getAudioManager().closeAudioConnection();
	}
	
	private void coin(GuildMessageReceivedEvent event)
	{
		this.message(this.random.nextInt(2) == 0 ? "Heads" : "Tails", event);
	}
	
	private void dice(GuildMessageReceivedEvent event)
	{
		this.message(Integer.toString(this.random.nextInt(5)+1), event);
	}
	
	private void randUser(GuildMessageReceivedEvent event)
	{
		List<User> users = event.getChannel().getUsers();
		User user = users.get(random.nextInt(users.size()));
		this.message("**" + user.getAsMention() + "**, I choose you!", event);
	}
	
	private void help(GuildMessageReceivedEvent event)
	{
		this.message("Coming soon.", event);
	}
	
	private void helpMusic(GuildMessageReceivedEvent event)
	{
		this.message("Coming soon.", event);
	}
	
	private void grammarNazi(String message, GuildMessageReceivedEvent event)
	{
		if(message.contains("ping")) event.getChannel().sendMessage("pong");
	}
	
	private HashMap<String, File> getSounds()
	{
		HashMap<String, File> files = new HashMap<String, File>();
		
		try
		{
			Files.walk(Paths.get(new ResourceLocation("/audio").getPath())).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			        files.put(filePath.getFileName().toString().substring(0, filePath.getFileName().toString().lastIndexOf(".")), filePath.toFile());
			    }
			});
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return files;
	}
}
