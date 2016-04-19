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
}
