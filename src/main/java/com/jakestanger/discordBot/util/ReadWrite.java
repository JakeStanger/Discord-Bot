package com.jakestanger.discordBot.util;

import com.google.gson.*;
import com.jakestanger.discordBot.wrapper.Song;
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
	
	public static HashMap<String, String> readSounds()
	{
		HashMap<String, String> sounds = new HashMap<>();
		
		JsonParser parser = new JsonParser();
		try
		{
			JsonObject object = parser.parse(new FileReader("/home/pi/Grandad_Botbags/sounds.json")).getAsJsonObject();
			//JsonObject object = parser.parse(new FileReader("sounds.json")).getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> sound : object.entrySet()) sounds.put(sound.getKey(), sound.getValue().getAsString());
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return sounds;
	}
	
	public static void addSound(String newName, String url)
	{
		HashMap<String, String> sounds = readSounds();
		sounds.put(newName, url);
		
		JsonObject object = new JsonObject();
		for(String name : sounds.keySet()) object.addProperty(name, sounds.get(name));
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(object);
		
		try
		{
			FileWriter writer = new FileWriter("/home/pi/Grandad_Botbags/sounds.json");
			//FileWriter writer = new FileWriter("sounds.json");
			writer.write(json);
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeSounds()
	{
		
	}
	
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
	
	public static List<Song> readLyrics(String artist)
	{
		List<Song> songs = new ArrayList<>();
		
		JsonParser parser = new JsonParser();
		try
		{
			//JsonObject object = parser.parse(new FileReader("artists/" + artist + ".json")).getAsJsonObject();
			JsonObject object = parser.parse(new FileReader("/home/pi/Grandad_Botbags/artists/" + artist + ".json")).getAsJsonObject();
			System.out.println(object);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return songs;
	}
}
