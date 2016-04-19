package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.utils.SimpleLog;
import net.dv8tion.jda.utils.SimpleLog.Level;

public class ReadWrite
{
	private static final SimpleLog logger = SimpleLog.getLog("ReadWrite");
	
	public static void writeTokenToFile(String token)
	{
		try
		{
			FileWriter file = new FileWriter("token.txt");
			BufferedWriter buffer = new BufferedWriter(file);
			buffer.write(token);
			buffer.close();
			
			logger.log(Level.INFO, "Succesfully wrote token to file.");
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
		HashMap<String, File> files = new HashMap<String, File>();
		
		try
		{
			Files.walk(Paths.get(new ResourceLocation("/audio").getPath())).forEach(filePath -> 
			{
			    if (Files.isRegularFile(filePath)) 
			    {
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
	
	public static List<String> readMutedUsers()
	{
		List<String> users = new ArrayList<String>();
		
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
			
			logger.log(Level.INFO, "Succesfully wrote muted users to file.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}