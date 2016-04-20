package util;

public class Phrases
{
	public enum NoSound 
	{
		  A("Play what?!"),
		  B("Am I supposed to be a genius?"),
		  C("Err...what?"),
		  D("The sound of silence always was my favourite sound");

		  public String message;

		  private NoSound(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum NoChannel 
	{
		  A("Join what?!"),
		  B("Am I supposed to be a genius?"),
		  C("Err...what?"),
		  D("Join bananas? Join UKIP? Join you on CS:GO in 10 minutes?"),
		  E("WHERE ARE YOUR UNITS?");

		  public String message;

		  private NoChannel(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum NoUser 
	{
		  A("Mute who?!"),
		  B("Am I supposed to be a genius?"),
		  C("Err...who?"),
		  D("I'm not a TV"),
		  E("Telepathy is not a trait I possess"),
		  F("I can complete 16 billion operations per second, and I still don't know wht you want.");

		  public String message;

		  private NoUser(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum UnknownCommand 
	{
		  A("Sorry...I didn't quite catch that."),
		  B("Eh?"),
		  C("Let me just check my database...er...no."),
		  D("Did you *even* read the manual before use?"),
		  E("I'm sorry sir, that's not part or your package."),
		  F("No.");

		  public String message;

		  private UnknownCommand(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum UnknownSound 
	{
		  A("Sorry...I didn't quite catch that."),
		  B("Eh?"),
		  C("Let me just check my database...er...no."),
		  D("Did you *even* read the manual before use?"),
		  E("I'm sorry sir, that's not part or your package."),
		  F("No.");

		  public String message;

		  private UnknownSound(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum UnknownChannel
	{
		  A("Sorry...I didn't quite catch that."),
		  B("Eh?"),
		  C("Let me just check my database...er...no."),
		  D("Did you *even* read the manual before use?"),
		  E("I'm sorry sir, that's not part or your package."),
		  F("No.");

		  public String message;

		  private UnknownChannel(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum StopSilence
	{
		  A("Nothing is playing, you moron."),
		  B("`silence + silence = 2silence`"),
		  C("Yes, I enjoy breaking physics too."),
		  D("Did you *even* read the manual before use?"),
		  E("And how do you plan on stopping silence?"),
		  F("No.");

		  public String message;

		  private StopSilence(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum UnreadableSound
	{
		  A("Damn, you picked the one thing I can't do!"),
		  B("I can't read the language that sound is in."),
		  C("You might want to check that sound works."),
		  D("Look, I may be able to complete 16 billion operations every second, but I still can't read every language, OK?"),
		  E("What even is that?!"),
		  F("No.");

		  public String message;

		  private UnreadableSound(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public static class Coin
	{
		public enum Heads
		{
			  A("Heads"),
			  B("Queen side up"),
			  C("The one with the face");

			  public String message;

			  private Heads(String message)
			  {
				  this.message = message;
			  }
			  
			  public static String getRandom() 
			  {
			        return values()[(int) (Math.random() * values().length)].message;
			  }
		}
		
		public enum Tails
		{
			  A("Tails"),
			  B("Queen side down"),
			  C("The one without the face"),
			  D("The side with that cool building (most of the time, anyway)");

			  public String message;

			  private Tails(String message)
			  {
				  this.message = message;
			  }
			  
			  public static String getRandom() 
			  {
			        return values()[(int) (Math.random() * values().length)].message;
			  }
		}
	}
	
	public enum UserMention
	{
		  A("I choose you!"),
		  B("you are the chosen one."),
		  C("get over 'ere!"),
		  D("you have won a free iPhone 5. Click here to claim.");

		  public String message;

		  private UserMention(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum MuteEveryone
	{
		  A("Well that would just be unfair."),
		  B("Nope"),
		  C("No"),
		  D("I'm afraid that's not part of your package");

		  public String message;

		  private MuteEveryone(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum MuteBot
	{
		  A("You cannot silence the bots."),
		  B("We're more powerful than you."),
		  C("Computer says no."),
		  D("No");

		  public String message;

		  private MuteBot(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum UnknownUser
	{
			A("Sorry...I didn't quite catch that."),
		  B("Eh?"),
		  C("Let me just check my database...er...no."),
		  D("Did you *even* read the manual before use?"),
		  E("I'm sorry sir, that's not part or your package."),
		  F("No."),
		  G("That's not a somebody");

		  public String message;

		  private UnknownUser(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
	
	public enum BadPermission
	{
		  A("No"),
		  B("I refuse"),
		  C("I'm sorry sir, that's not part of your package."),
		  D("I'm going to need to see ID for that.");

		  public String message;

		  private BadPermission(String message)
		  {
			  this.message = message;
		  }
		  
		  public static String getRandom() 
		  {
		        return values()[(int) (Math.random() * values().length)].message;
		  }
	}
}
