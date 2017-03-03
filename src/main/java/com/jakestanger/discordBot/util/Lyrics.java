package com.jakestanger.discordBot.util;

import com.jakestanger.discordBot.wrapper.Song;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Lyrics
{
	private static HashMap<String, String> artistCache = new HashMap<>();
	private static HashMap<Song, String> lyricsCache = new HashMap<>();
	
	public static List<Song> fetchSongs(String artist)
	{
		String formatted = "";
		if(artistCache.containsKey(artist)) formatted = artistCache.get(artist);
		else
		{
			try
			{
				URL url = new URL("http://www.azlyrics.com/" + artist.toCharArray()[0] + "/" + artist + ".html");
				String allHTML = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
				formatted = allHTML.split("<div id=\"listAlbum\">")[1]
						.split("<script type=\"text/javascript\">")[0];
						
				artistCache.put(artist, formatted);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			List<String> albums = new ArrayList<>(Arrays.asList(formatted.split("<div class=\"album\">")));
			
			List<Song> songList = new ArrayList<>();
			for(String album : albums)
			{
				List<String> songs = new ArrayList<>(Arrays.asList(album.split("\n")));
				songs.remove(0); //Remove nametag
				
				for(String song : songs)
				{
					if(!song.contains("a id=") && !song.equals("\n") && !song.equals(""))
					{
						try
						{
							//Convert into easier to process format
							String[] info = song.replaceAll("<a href=\"..", "")
									.replaceAll("\" target=\"_blank\">", "##")
									.replaceAll("</a><br>", "")
									.split("##");
							
							songList.add(new Song(info[1], info[0], artist));
						}
						catch(ArrayIndexOutOfBoundsException e)
						{
							System.out.println("Out of bounds. Probably processing next album.");
						}
					}
				}
			}
			
			return songList;
			
		}
		catch (StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String fetchLyrics(String URL, Song song)
	{
		if(lyricsCache.containsKey(song)) return lyricsCache.get(song);
		else
		{
			try
			{
				String allHTML = new Scanner(new URL("http://www.azlyrics.com" + URL).openStream(), "UTF-8").useDelimiter("\\A").next();
				String unformatted = String.valueOf(allHTML.split("<div>")[1]).split("</div>")[0];
				String formatted = unformatted.replace("<br>", "")
						.replace("<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->", "")
						.replace("<i>", "*").replace("</i>", "*")
						.replace("<b>", "**").replace("</b>", "**")
						.replace("&quot", "\"");
				
				lyricsCache.put(song, formatted);
				return formatted;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static class GameData
	{
		private static boolean inLyricsMode = false;
		private static int correctAnswerID;
		
		private static HashMap<Member, Integer> scores = new HashMap<>();
		private static HashMap<Member, Integer> highScores = new HashMap<>();
		
		public static boolean isInLyricsMode()
		{
			return inLyricsMode;
		}
		
		public static void setInLyricsMode(boolean inLyricsMode)
		{
			GameData.inLyricsMode = inLyricsMode;
		}
		
		public static int getCorrectAnswerID()
		{
			return correctAnswerID;
		}
		
		public static void setCorrectAnswerID(int correctAnswerID)
		{
			GameData.correctAnswerID = correctAnswerID;
		}
		
		public static HashMap<Member, Integer> getScores()
		{
			return scores;
		}
		
		public static void setScores(HashMap<Member, Integer> scores)
		{
			GameData.scores = scores;
		}
		
		public static HashMap<Member, Integer> getHighScores()
		{
			return highScores;
		}
		
		public static void setHighScores(HashMap<Member, Integer> highScores)
		{
			GameData.highScores = highScores;
		}
		
		/**
		 * Increments the player's score by one, and updates high score if applicable
		 * @param member The player
		 */
		public static void incrementScore(Member member)
		{
			int currentScore = GameData.scores.containsKey(member) ? GameData.scores.get(member) : 0;
			currentScore++;
			GameData.scores.put(member, currentScore);
			
			int highScore = GameData.highScores.containsKey(member) ? GameData.highScores.get(member) : 0;
			if(currentScore > highScore) GameData.highScores.put(member, currentScore);
		}
		
		public static void resetScore(Member member)
		{
			if(GameData.scores.containsKey(member)) GameData.scores.put(member, 0);
		}
		
		public static int getScore(Member member)
		{
			return GameData.scores.get(member);
		}
		
		public static int getHighScore(Member member)
		{
			return GameData.highScores.get(member);
		}
	}
}
