package bot;

import java.io.File;
import java.io.IOException;
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
import util.ReadWrite;

public class Bot extends ListenerAdapter
{
	private static Bot instance;
	
	private SimpleLog logger;
	private Player player = null;
	private Random random;
	
	private HashMap<String, File> sounds;
	private List<String> mutedUsers;
	
	private static final String NAME = "@Grandad_Botbags";
	
	public Bot()
	{
		instance = this;
		logger = SimpleLog.getLog("GrandadBotbags");
		random = new Random();
		sounds = ReadWrite.readSounds();
		mutedUsers = ReadWrite.readMutedUsers();
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
            //jda.getAccountManager().setAvatar(AvatarUtil.getAvatar(new File(new ResourceLocation("/images/avatar.png").getPath()))).update(); Only enable when updating avatar
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
		/*catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}*/
		
		return success;
    }
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		if(!(this.mutedUsers.contains(event.getAuthor().getId())))
		{
			String message = event.getMessage().getContent();
			
			this.grammarNazi(message, event);
			if(message.contains(NAME))
			{
				String[] command = message.substring((NAME + " ").length()).toLowerCase().split(" ");
				switch(command[0])
				{
					case "play":
						if(command.length > 1) playSound(command[1], event);
						else this.message("Play what?!", event);
						break;
					case "stop":
						stopSound(event);
						break;
					case "join":
						if(command.length > 1) joinChannel(command[1], event);
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
					case "mute":
						if(command.length > 1) muteUser(command[1], event);
						else this.message("Mute who?!", event);
						break;
					case "help":
						if(command.length > 1 && command[1].equals("play")) helpPlay(event);
						else help(event);
						break;
					default:
						this.message("You might as well have just spoken in Greek.", event);
				}
			}
		}
		else event.getMessage().deleteMessage();
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
	
	private void stopSound(GuildMessageReceivedEvent event)
	{
		if(player != null) player.stop();
		else message("And how do you plan on silencing silence?", event);
	}
	
	private void joinChannel(String channelName, GuildMessageReceivedEvent event)
	{
		//Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
        VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vChan -> vChan.getName().equalsIgnoreCase(channelName)).findFirst().orElse(null); 
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
	
	private void muteUser(String userMention, GuildMessageReceivedEvent event)
	{
		List<User> users = event.getChannel().getUsers();
		for(User user : users)
		{
			if(("@" + user.getUsername()).toLowerCase().equals(userMention))
			{
				if(!user.isBot())
				{
					String id = user.getId();
					if(!this.mutedUsers.contains(id))
					{
						this.mutedUsers.add(id);
						message("Muted " + user.getAsMention(), event);
					}
					else
					{
						this.mutedUsers.remove(id);
						message("Unmuted " + user.getAsMention(), event);
					}
					ReadWrite.writeMutedUsers(mutedUsers);
				}
				else message("We cannot be silenced", event);
				return;
			}
		}
		message("I cannot mute those who fail to exist.", event);
	}
	
	private void help(GuildMessageReceivedEvent event)
	{
		this.message("***Commands:***\n"
				+ "**play <sound>** - Plays the given sound\n"
				+ "**stop** - Stops playing the current sound\n"
				+ "**join <channel>** - Joins the given voice channel\n"
				+ "**leave** - Leaves the current voice channel\n"
				+ "**coin** - Flips a coin\n"
				+ "**dice** - Rolls a dice\n"
				+ "**randUser** - Gets a random user\n"
				+ "**mute <user>** - Mutes the given user in all text channels\n"
				+ "**help [play]** - Get a list of commands [sounds]\n", event);
	}
	
	private void helpPlay(GuildMessageReceivedEvent event)
	{
		StringBuilder message = new StringBuilder();
		boolean bold = true;
		for(String command : this.sounds.keySet())
		{
			message.append((bold ? "**" : "") + command + (bold ? "**" : "" )+ "    ");
			bold = !bold;
		}
		message(message.toString(), event);
	}
	
	private void grammarNazi(String message, GuildMessageReceivedEvent event)
	{
		if(message.equals("ping")) event.getChannel().sendMessage("pong");
	}
}
