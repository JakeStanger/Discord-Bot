package bot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.text.WordUtils;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Astronomy;
import com.github.fedy2.weather.data.Atmosphere;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Condition;
import com.github.fedy2.weather.data.Forecast;
import com.github.fedy2.weather.data.Item;
import com.github.fedy2.weather.data.Wind;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import com.github.fedy2.weather.data.unit.Time;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.SimpleLog;
import util.Phrases;
import util.ReadWrite;

public class Bot extends ListenerAdapter
{
	private static Bot instance;
	
	private SimpleLog logger;
	private Player player = null;
	private Random random;
	
	private HashMap<String, File> sounds;
	private List<String> mutedUsers;
	
	ChatterBotFactory factory;
	ChatterBot cleverbot;
	ChatterBotSession cleverbotSession;
	
	public Bot()
	{
		instance = this;
		logger = SimpleLog.getLog("GrandadBotbags");
		logger.info("Creating new instance of bot");
		random = new Random();
		sounds = ReadWrite.readSounds();
		mutedUsers = ReadWrite.readMutedUsers();
		
		factory = new ChatterBotFactory();
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
            new JDABuilder().setBotToken(token).addListener(this).buildBlocking();
            //jda.getAccountManager().setAvatar(AvatarUtil.getAvatar(new File(new ResourceLocation("/images/avatar.png").getPath()))).update(); Only enable when updating avatar
            
            cleverbot = factory.create(ChatterBotType.CLEVERBOT);
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
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		if(!(this.mutedUsers.contains(event.getAuthor().getId())))
		{
			String message = event.getMessage().getContent();
			
			this.grammarNazi(message, event);
			if(message.startsWith("@Grandad_Botbags")) cleverbot(message.substring("@Grandad_Botbags".length()), event);
			if(message.startsWith("!"))
			{
				String[] command = message.substring(("!").length()).toLowerCase().split(" ");
				switch(command[0])
				{
					case "play":
						if(command.length > 1) playSound(command[1], event);
						else this.helpPlay(event);;
						break;
					case "stop":
						stopSound(event);
						break;
					case "join":
						if(command.length > 1) joinChannel(command[1], event);
						else this.message(Phrases.NoChannel.getRandom(), event);
						break;
					case "leave":
						leaveChannel(event);
						break;
					case "flip":
						coin(event);
						break;
					case "roll":
						dice(event);
						break;
					case "randuser":
						randUser(event);
						break;
					case "mute":
						if(command.length > 1) muteUser(command[1], event);
						else this.message(Phrases.NoUser.getRandom(), event);
						break;
					case "help":
						help(event);
						break;
					case "reload":
						reload(event);
						break;
					case "weather":
						weather(event);
						break;
					default:
						this.message(Phrases.UnknownCommand.getRandom(), event);
				}
			}
		}
		else event.getMessage().deleteMessage();
	}

	/**
	 * Plays the given sound
	 * @param sound A string referring to the sound file excluding the extension
	 * @param event
	 */
	private void playSound(String sound, GuildMessageReceivedEvent event)
	{
		if(event.getAuthor().getJDA().getAudioManager(event.getGuild()).getConnectedChannel() != null)
		{
			File audioFile = null;
	        try
	        {
	            //Get sound
	        	if(this.sounds.containsKey(sound)) audioFile = this.sounds.get(sound);
	            else
	            {
	            	this.message(Phrases.UnknownSound.getRandom(), event); //Unknown sound
	            	return;
	            }
	
	            //Play sound
	        	player = new FilePlayer(audioFile);
	            event.getGuild().getAudioManager().setSendingHandler(player);
	            player.play();
	        }
	        catch (IOException e)
	        {
	            this.message(Phrases.UnknownSound.getRandom(), event); //Error reading sound
	            e.printStackTrace(); //TODO investigate the cause of sound errors more closely
	        }
	        catch (UnsupportedAudioFileException e)
	        {
	           this.message(Phrases.UnreadableSound.getRandom(), event); //Unknown file type
	        }
		}
		else this.message(Phrases.BadPermission.getRandom(), event); //Not in voice channel
	}
	
	/**
	 * Stops the currently playing sound
	 * @param event
	 */
	private void stopSound(GuildMessageReceivedEvent event)
	{
		if(player != null) player.stop();
		else message(Phrases.StopSilence.getRandom(), event); //If no sound was playing
	}
	
	/**
	 * Joins the given channel
	 * @param channelName
	 * @param event
	 */
	private void joinChannel(String channelName, GuildMessageReceivedEvent event)
	{
		if(event.getChannel().checkPermission(event.getAuthor(), Permission.VOICE_CONNECT))
		{
			if(event.getGuild().getAudioManager() != null) event.getGuild().getAudioManager().closeAudioConnection(); //Close existing voice connection
			//Scans through the VoiceChannels in this Guild, looking for one with a case-insensitive matching name.
	        VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vChan -> vChan.getName().equalsIgnoreCase(channelName)).findFirst().orElse(null); 
	        if (channel == null)
	        {
	            this.message(Phrases.UnknownChannel.getRandom(), event);
	            return;
	        }
	        event.getGuild().getAudioManager().openAudioConnection(channel);
		}
		else this.message(Phrases.BadPermission.getRandom(), event);
	}
	
	/**
	 * Leaves the current channel
	 * @param event
	 */
	private void leaveChannel(GuildMessageReceivedEvent event)
	{
		if(event.getChannel().checkPermission(event.getAuthor(), Permission.VOICE_CONNECT)) event.getGuild().getAudioManager().closeAudioConnection();
		else this.message(Phrases.BadPermission.getRandom(), event);
	}
	
	/**
	 * Flips a coin
	 * @param event
	 */
	private void coin(GuildMessageReceivedEvent event)
	{
		this.message(this.random.nextInt(2) == 0 ? Phrases.Coin.Heads.getRandom() : Phrases.Coin.Tails.getRandom(), event);
	}
	
	/**
	 * Rolls a 6-sided dice
	 * @param event
	 */
	private void dice(GuildMessageReceivedEvent event)
	{
		this.message(Integer.toString(this.random.nextInt(5)+1), event);
	}
	
	/**
	 * Gets a random user
	 * @param event
	 */
	private void randUser(GuildMessageReceivedEvent event)
	{
		if(event.getChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MENTION_EVERYONE))
		{
			List<User> users = event.getChannel().getUsers();
			User user = users.get(random.nextInt(users.size()));
			this.message(user.getAsMention() +", " +  Phrases.UserMention.getRandom(), event);
		}
		else this.message(Phrases.BadPermission.getRandom(), event);
	}
	
	/**
	 * Mutes the given user
	 * @param userMention the user to mute
	 * @param event
	 */
	private void muteUser(String userMention, GuildMessageReceivedEvent event)
	{
		if(event.getChannel().checkPermission(event.getAuthor(), Permission.MESSAGE_MANAGE))
		{
			if(userMention.equals("@everyone")) //Don't even try to mute everyone. It won't work.
			{
				message(Phrases.MuteEveryone.getRandom(), event);
				return;
			}
			
			List<User> users = event.getChannel().getUsers();
			for(User user : users) //Look through list of users until you find the mentioned one
			{
				if(("@" + user.getUsername()).toLowerCase().equals(userMention))
				{
					if(!user.isBot())
					{
						String id = user.getId();
						if(!this.mutedUsers.contains(id)) //Mute
						{
							this.mutedUsers.add(id);
							message("Muted " + user.getAsMention(), event);
						}
						else //Unmute
						{
							this.mutedUsers.remove(id);
							message("Unmuted " + user.getAsMention(), event);
						}
						ReadWrite.writeMutedUsers(mutedUsers); //Update muted file.
					}
					else message(Phrases.MuteBot.getRandom(), event); //Attempted to mute bot
					return;
				}
			}
			message(Phrases.UnknownUser.getRandom(), event); //User not found in list
		}
		else message(Phrases.BadPermission.getRandom(), event); //Bad permission
	}
	
	/**
	 * Shows a list of commands and their purpose.
	 * A little hard-coded, but there you go.
	 * @param event
	 */
	private void help(GuildMessageReceivedEvent event)
	{
		this.message("***Commands:***\n"
				+ "**play [sound]** - Shows a list of sounds or [Plays the given sound]\n"
				+ "**stop** - Stops playing the current sound\n"
				+ "**join <channel>** - Joins the given voice channel\n"
				+ "**leave** - Leaves the current voice channel\n"
				+ "**flip** - Flips a coin\n"
				+ "**roll** - Rolls a dice\n"
				+ "**randUser** - Gets a random user\n"
				+ "**mute <@user>** - Mutes the given user in all text channels\n"
				+ "**help** - Shows a list of commands\n", event);
	}
	
	/**
	 * Shows a list of sounds
	 * @param event
	 */
	private void helpPlay(GuildMessageReceivedEvent event)
	{
		StringBuilder message = new StringBuilder();
		boolean bold = true;
		for(String command : this.sounds.keySet()) //Loop over every sound file
		{
			message.append((bold ? "**" : "") + command + (bold ? "**" : "" )+ "    ");
			bold = !bold;
		}
		message(message.toString(), event);
	}
	
	private void reload(GuildMessageReceivedEvent event)
	{
		if(event.getChannel().checkPermission(event.getAuthor(), Permission.MANAGE_SERVER))
		{
			this.sounds = ReadWrite.readSounds();
			this.message("Sounds reloaded", event);
		}
		else this.message(Phrases.BadPermission.getRandom(), event);
	}
	
	/**
	 * Corrects people's annoying SPAG mistakes.
	 * @param message
	 * @param event
	 */
	private void grammarNazi(String message, GuildMessageReceivedEvent event)
	{
		message = message.toLowerCase();
		if(message.equals("ping")) message("pong", event);
		if(message.contains(" alot ") || message.contains(" alot") || message.contains("alot ")) message("*A lot", event);
		if(message.contains("any one")) message("*Anyone", event);
		if(message.contains("reminder that")) message("You've just earned yourself a one-way ticket to hell.", event);
		if(message.contains(" u ")) message("Text talk? How about a good old knife to the throat?", event);
		if(message.contains("yeh")) message("*Yeah", event);
	}
	
	/**
	 * Sends a message to CleverBot.
	 * Sends a message with the reply.
	 * @param message
	 * @param event
	 */
	private void cleverbot(String message, GuildMessageReceivedEvent event)
	{
		try
		{
			message(this.cleverbotSession.think(message), event);
		}
		catch(Exception e)
		{
			logger.fatal("An error occurred getting Cleverbot to think. Sorry about that.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the current weather
	 * @param event
	 */
	private void weather(GuildMessageReceivedEvent event)
	{
		try
		{
			YahooWeatherService service = new YahooWeatherService();
			Channel channel = service.getForecast("15051", DegreeUnit.CELSIUS);
			
			StringBuilder report = new StringBuilder();
			
			Astronomy astronomy = channel.getAstronomy();
			Time sunrise = astronomy.getSunrise();
			Time sunset = astronomy.getSunset();
			
			String sunriseS = sunrise.getHours() + ":" + sunrise.getMinutes() + sunrise.getConvention();
			String sunsetS = sunset.getHours() + ":" + sunset.getMinutes() + sunset.getConvention();
			
			Atmosphere atmosphere = channel.getAtmosphere();
			String humidity = Integer.toString(atmosphere.getHumidity()) + "%";
			String pressure = Float.toString(atmosphere.getPressure()) + "mb";
			String pressureState = WordUtils.capitalize(atmosphere.getRising().name().toLowerCase());
			String visibility = Float.toString(atmosphere.getVisibility()) + "km";
			
			Wind wind = channel.getWind();
			String speed = Float.toString(wind.getSpeed()) + "km/h";
			String direction = Integer.toString(wind.getDirection()) + "\u00B0";
			String chill = Float.toString(Math.round(((wind.getChill()-32f)/1.8f) * 10f) / 10f) + "\u00B0C"; //For some reason this is in Fahrenheit. It also needs rounding.
			
			Item item = channel.getItem();
			Condition condition = item.getCondition();
			String temperature = Integer.toString(condition.getTemp()) + "\u00B0C";
			String description = condition.getText();
			
			List<Forecast> forecasts = item.getForecasts();
			List<String> forecastsS = new ArrayList<String>();
			for(Forecast forecast : forecasts)
			{
				StringBuilder forecastS = new StringBuilder();
				
				String day = WordUtils.capitalize(forecast.getDay().name().toLowerCase());
				String high = Integer.toString(forecast.getHigh()) + "\u00B0C";
				String low = Integer.toString(forecast.getLow()) + "\u00B0C";
				String forecastDesc = forecast.getText();
				
				forecastS.append(Phrases.Weather.Forecast.Day.getRandom() + "**" + day + "**\n");
				forecastS.append(Phrases.Weather.Forecast.High.getRandom() + "**" + high + "**\n");
				forecastS.append(Phrases.Weather.Forecast.Low.getRandom() + "**" + low + "**\n");
				forecastS.append(Phrases.Weather.Forecast.Desc.getRandom() + "**" + forecastDesc + "**\n\n");
				
				forecastsS.add(forecastS.toString());
			}
			
			report.append("***" + Phrases.Weather.Intro.getRandom() + "***\n\n");
			
			report.append(Phrases.Weather.Temp.getRandom() + "**" + temperature + "**\n");
			report.append(Phrases.Weather.Description.getRandom() + "**" + description + "**\n\n");
			
			report.append(Phrases.Weather.Wind.getRandom() + "**" + speed + "**\n");
			report.append(Phrases.Weather.Direction.getRandom() + "**" + direction + "**\n");
			report.append(Phrases.Weather.Chill.getRandom() + "**" + chill + "**\n");
			report.append(Phrases.Weather.Humidity.getRandom() + "**" + humidity + "**\n\n");
			
			report.append(Phrases.Weather.Pressure.getRandom() + "**" + pressure + "** (This appears to be completely broken :(\n"); //TODO Investigate why this is broken
			report.append(Phrases.Weather.PressureState.getRandom() + "**" + pressureState + "**\n");
			report.append(Phrases.Weather.Visibility.getRandom() + "**" + visibility + "**\n\n");
			
			report.append(Phrases.Weather.Sunrise.getRandom() + "**" + sunriseS + "**\n");
			report.append(Phrases.Weather.Sunset.getRandom() + "**" + sunsetS + "**\n\n");
			
			StringBuilder forecastSB = new StringBuilder();
			forecastSB.append("***" + Phrases.Weather.Forecast.Intro.getRandom() + "***\n \n");
			for(String forecast : forecastsS) forecastSB.append(forecast);
			
			message(report.toString(), event);
			message(forecastSB.toString(), event);
		}
		catch(JAXBException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message on both the bot and server
	 * @param message
	 * @param event
	 */
	public void message(String message, GuildMessageReceivedEvent event)
	{
		System.out.println("[Message] " + message);
		event.getChannel().sendMessage(message);
	}
}
