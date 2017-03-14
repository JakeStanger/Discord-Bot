package com.jakestanger.discordBot.util;

import com.google.gson.*;
import net.dv8tion.jda.core.utils.SimpleLog;
import net.dv8tion.jda.core.utils.SimpleLog.Level;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ReadWrite
{
	private static final SimpleLog logger = SimpleLog.getLog("ReadWrite");
	
	public static HashMap<String, String> readSounds()
	{
		HashMap<String, String> sounds = new HashMap<>();
		
		JsonParser parser = new JsonParser();
		try
		{
			//JsonObject object = parser.parse(new FileReader("/home/pi/Grandad_Botbags/sounds.json")).getAsJsonObject();
			JsonObject object = parser.parse(new FileReader("sounds.json")).getAsJsonObject();
			
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
			//FileWriter writer = new FileWriter("/home/pi/Grandad_Botbags/sounds.json");
			FileWriter writer = new FileWriter("sounds.json");
			writer.write(json);
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeTokenToFile(String token)
	{
		try
		{
			//FileWriter file = new FileWriter("/home/pi/Grandad_Botbags/token.txt");
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
			//file = new FileReader("/home/pi/Grandad_Botbags/token.txt");
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
	
	public static List<String> getSoundsFromFile()
	{
		final String[] EXTENSIONS = {".wav", ".mp3", ".mp4", ".flac", ".ogg", ".wma", ".aac", ".m4a", ".m3u", ".pls"};
		
		try(Stream<Path> paths = Files.walk(Paths.get("")))
		{
			List<String> files = new ArrayList<>();
			paths.forEach(filePath -> {
				if (Files.isRegularFile(filePath))
				{
					String path = filePath.toString().toLowerCase();
					if(Arrays.stream(EXTENSIONS).parallel().anyMatch(path::contains))
						files.add(filePath.toString());
				}
			});
			
			return files;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
