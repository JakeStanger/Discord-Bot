package discordBot;

import bot.Bot;
import window.Window;

public class DiscordBot
{	
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			new Window();
			new Bot();
		}
		
		else new Bot().init(args[0]);
	}
}
