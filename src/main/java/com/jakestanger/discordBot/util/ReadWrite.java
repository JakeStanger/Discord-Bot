package com.jakestanger.discordBot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.core.utils.SimpleLog;
import net.dv8tion.jda.core.utils.SimpleLog.Level;

import java.io.*;
import java.util.Map;

public class ReadWrite
{
	private static final SimpleLog logger = SimpleLog.getLog("ReadWrite");
	
	public static void readSettings()
	{
		JsonParser parser = new JsonParser();
		try
		{
			JsonObject object = parser.parse(new FileReader("settings.json")).getAsJsonObject();
			
			//Commands
			JsonObject commands = object.getAsJsonObject("commands");
			for(Map.Entry commandEntry : commands.entrySet())
			{
				JsonObject command = ((JsonObject)commandEntry.getValue());
				
				JsonArray arguments = command.getAsJsonArray("arguments");
				String method = command.get("method").getAsString();
				String desc = command.get("description").getAsString();
				
				//Method method = Bot.getInstance().getClass().getMethod(method, )
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeTokenToFile(String token)
	{
		try
		{
			FileWriter file = new FileWriter("/home/pi/Grandad_Botbags/token.txt");
			//FileWriter file = new FileWriter("token.txt");
			BufferedWriter buffer = new BufferedWriter(file);
			buffer.write(token);
			buffer.close();
			
			logger.log(Level.INFO, "Successfully wrote token to file.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String readTokenFromFile()
	{
		String token = null;
		
		FileReader file;
		try
		{
			file = new FileReader("/home/pi/Grandad_Botbags/token.txt");
			//file = new FileReader("token.txt");
			BufferedReader buffer = new BufferedReader(file);
			token = buffer.readLine();
			buffer.close();
			
			logger.log(Level.INFO, "Loaded token from file.");
		}
		catch(FileNotFoundException e)
		{
			logger.log(Level.INFO, "No token file found.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return token;
	}
}
