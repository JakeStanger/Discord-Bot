package com.jakestanger.discordBot.wrapper;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Song
{
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getLyrics()
	{
		return lyrics;
	}
	
	public void setLyrics(String lyrics)
	{
		this.lyrics = lyrics;
	}
	
	public String getArtist()
	{
		return artist;
	}
	
	public void setArtist(String artist)
	{
		this.artist = artist;
	}
	
	public String getLink()
	{
		return link;
	}
	
	public void setLink(String link)
	{
		this.link = link;
	}
	
	private String name;
	private String artist;
	private String link;
	private String lyrics;
	
	public Song(String name, String link, String artist)
	{
		this.name = name;
		this.link = link;
		this.artist = artist;
	}
}
