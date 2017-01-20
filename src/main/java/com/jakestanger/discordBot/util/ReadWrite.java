package com.jakestanger.discordBot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.core.utils.SimpleLog;
import net.dv8tion.jda.core.utils.SimpleLog.Level;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
			FileWriter file = new FileWriter("token.txt");
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
			file = new FileReader("token.txt");
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
	
	public static HashMap<String, File> readSounds()
	{
		HashMap<String, File> files = new HashMap<>();
		
		//File[] audioFiles = new File("/home/pi/Grandad_Botbags/audio/").listFiles(); //You may need to change this on certain OSs
		File[] audioFiles = new File("audio/").listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 

		if(audioFiles != null && audioFiles.length != 0)
		{
			for (File file : audioFiles) 
			{
			    if (file.isFile()) 
			    {
			        files.put(file.getName().substring(0,  file.getName().lastIndexOf(".")), file);
			    }
			}
		}
		else logger.info("No audio files located. Play command will not work.");
		
		return files;
	}
	
	public static List<String> readMutedUsers()
	{
		List<String> users = new ArrayList<>();
		
		FileReader file;
		try
		{
			file = new FileReader("muted.txt");
			BufferedReader buffer = new BufferedReader(file);
			
			String line;
			while((line = buffer.readLine()) != null) users.add(line);
			
			buffer.close();
			
			logger.log(Level.INFO, "Loaded muted users from file.");
		}
		catch(FileNotFoundException e)
		{
			logger.log(Level.INFO, "No muted user list file found.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return users;
	}
	
	public static void writeMutedUsers(List<String> mutedUsers)
	{
		try
		{
			FileWriter file = new FileWriter("muted.txt");
			BufferedWriter buffer = new BufferedWriter(file);
			for(String userId : mutedUsers) buffer.write(userId);
			buffer.close();
			
			logger.log(Level.INFO, "Successfully wrote muted users to file.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
