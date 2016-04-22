package discordBot;

import bot.Bot;
import window.Window;

public class DiscordBot
{	
	public static void main(String[] args)
	{
		Bot bot = new Bot();
		
		if(args.length == 0)
		{
			System.out.println("No arguments specified. Will start GUI.");
			new Window();
		}
		else 
		{
			System.out.println("Token argument given. Will run from terminal.");
			bot.init(args[0]);
		}
	}
}
