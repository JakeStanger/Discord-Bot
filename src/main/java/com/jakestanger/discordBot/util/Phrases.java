package com.jakestanger.discordBot.util;

/**
 * A class full of enums for stock phrases.
 * Hopefully pretty self-explanatory.
 * @author Roboguy99
 *
 */
public class Phrases
{
	public enum NoChannel 
	{
		  A("Join what?!"),
		  B("Am I supposed to be a genius?"),
		  C("Err...what?"),
		  D("Join bananas? Join UKIP? Join you on CS:GO in 10 minutes?"),
		  E("WHERE ARE YOUR UNITS?"),
		  F("If you don't tell me where to go, I can't go anywhere."),
		  G("Try again."),
		  H("You're wasting everyone's time"),
		  I("There is no channel called \"\". Although that does quite nicely represent your brain size"),
		  J("Go on....oh that was it. Ok");

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
		  A("Who?!"),
		  B("Am I supposed to be a genius?"),
		  C("Err...who?"),
		  D("Feel free to try again."),
		  E("Telepathy is not a trait I possess."),
		  F("I can complete billions of operations per second, and I still don't know wht you want."),
		  G("I don't know who you mean."),
		  H("Well if you don't tell me, I'm not going to be able to do anything, am I?"),
		  I("Nice name."),
		  J("You failed.");

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
		  F("No."),
		  G("That not a task I know how to do."),
		  H("I suggest you stop."),
		  I("I'm quite limited, you know"),
		  J("That command should work in 5 minutes; if not, wait another 5.");

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
		  F("No."),
		  G("Well if I had a file called that, I'd consider it"),
		  H("Pfft. Inferior sound taste. I refuse."),
		  I("Not today"),
		  J("That just wouldn't be funny though, would it?");

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
		  F("No."),
		  G("Where?"),
		  H("I can't find anything with that name. Which is your fault."),
		  I("If you make a channel called that, then I might"),
		  J("After you.");

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
		  F("No."),
		  G("Quiet everybody. You hear that? Me neither."),
		  H("Well you attempted to break physics. And failed."),
		  I("Please explain how you intend how I do this."),
		  J("Hang on, let me just consult God and ask him to change physics for me");

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
		  F("No."),
		  G("Ok so this *is* my fault for once. I can't read that."),
		  H("Maybe later"),
		  I("And just give in to your every command like that? I don't think so"),
		  J("Nah.");

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
			  C("The one with the face"),
			  D("Queenibabes"),
			  E("Your leader's head"),
			  F("Up!"),
			  G("Face"),
			  H("Charles. Soon."),
			  I("The royal one"),
			  J("I'm not quite sure, but I think it's a picture of a coin head's side up");

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
			  D("The side with that cool building (most of the time, anyway)"),
			  E("Not the queen"),
			  F("Down!"),
			  G("Well I can't see any people on it..."),
			  H("The side with the year on it. Ha - now you have to look at a coin!"),
			  I("Y'know that thing from Sonic? That thing."),
			  J("I can fly with my tail. My name.");

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
		  D("you have won a free iPhone 5. Click here to claim."),
		  E("someone wants you."),
		  F("you're wanted"),
		  G("you win"),
		  H("something or other. Don't ask me."),
		  I("I believe you've won something."),
		  J("would you mind responding please?");

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
		  B("Nope."),
		  C("No."),
		  D("I'm afraid that's not part of your package."),
		  E("That's just greedy."),
		  F("Not even I get to do that."),
		  G("Well I would let you. But I won't. So pah."),
		  H("Pfft."),
		  I("Funny."),
		  J("That would involve muting me. I'm not stupid.");

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
		  D("No"),
		  E("You can't mute bots."),
		  F("We're here to help."),
		  G("When pigs fly."),
		  H("Not a chance."),
		  I("Ha ha. No."),
		  J("I'm not stupid.");

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
		  G("That's not a somebody"),
		  H("Who?"),
		  I("Nice typo."),
		  J("Try again.");

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
		  D("I'm going to need to see ID for that."),
		  E("This. Is. Democracy. Manifest."),
		  F("Yeah...no..."),
		  G("I'm sorry....it's just I don't think you're up for the job."),
		  H("That's not a thing you're allowed to do."),
		  I("Don't worry. I planned for people like you."),
		  J("Nope.");

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
	
	public static class Weather
	{
		public enum Intro
		{
			A("Ok listen up people, here's what the sky is up to."),
			B("You people can't be bothered to look out your com.jakestanger.discordBot.window, so I've sourced the outdoors for you.");
			
			public String message;

			private Intro(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Temp
		{
			A("How hot is it? It's exactly "),
			B("Perhaps knowing you're too hot or too cold isn't accurate enough? Well here's accuracy: ");
			
			public String message;

			private Temp(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Description
		{
			A("And here's a vague description of everything: "),
			B("If this means anything to you, you'd be glad to know it's ");
			
			public String message;

			private Description(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Wind
		{
			A("I can't tell if this is enough to knock over bins, but here's how fast the wind is: "),
			B("Speed of the mass net movement of air particles from a high concentration to a low concentration if you cared: ");
			
			public String message;

			private Wind(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Direction
		{
			A("But the question we've all been asking: what way are the particles moving? "),
			B("Here's which direction your bins will fall in: ");
			
			public String message;

			private Direction(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Chill
		{
			A("As if it wasnt cold enough already, the wind is making it feel this much colder: "),
			B("Because you rely on water so much, evapouration will make you feel this much colder: ");
			
			public String message;

			private Chill(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Humidity
		{
			A("Here's how much water is in the air: "),
			B("God knows what it's a percentage of, but here's the wetness of the air: ");
			
			public String message;

			private Humidity(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Pressure
		{
			A("Here's a thing representing the amount of air there is: "),
			B("Even though this means very little to you, I'm going to give you the pressure anyway. It's ");
			
			public String message;

			private Pressure(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum PressureState
		{
			A("The amount of air is "),
			B("Enjoy this vague term describing the change in quantity of air around you: ");
			
			public String message;

			private PressureState(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Visibility
		{
			A("If there weren't bloody houses in the way you'd be able to see for "),
			B("Useless fact: if you were on the sea you could see for ");
			
			public String message;

			private Visibility(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Sunrise
		{
			A("Fire Ball Alpha came into sight at exactly "),
			B("You could see things without a torch today at any point past ");
			
			public String message;

			private Sunrise(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		
		public enum Sunset
		{
			A("If it wasn't for light pollution, stars would come out after "),
			B("The sun's cerfew tonight is ");
			
			public String message;

			private Sunset(String message)
			{
				this.message = message;
			}
			  
			 public static String getRandom() 
			{
				 return values()[(int) (Math.random() * values().length)].message;
			}
		}
		public static class Forecast
		{
			public enum Intro
			{
				A("And if that wasn't enough, here's some vague information for the rest of the week."),
				B("Now to watch me look into the future.");
				
				public String message;

				private Intro(String message)
				{
					this.message = message;
				}
				  
				 public static String getRandom() 
				{
					 return values()[(int) (Math.random() * values().length)].message;
				}
			}
			
			public enum Day
			{
				A("On "),
				B("When the calender declares it is a ");
				
				public String message;

				private Day(String message)
				{
					this.message = message;
				}
				  
				 public static String getRandom() 
				{
					 return values()[(int) (Math.random() * values().length)].message;
				}
			}
			
			public enum High
			{
				A("This is as hot as it will get: "),
				B("It might get to a whopping ");
				
				public String message;

				private High(String message)
				{
					this.message = message;
				}
				  
				 public static String getRandom() 
				{
					 return values()[(int) (Math.random() * values().length)].message;
				}
			}
			
			public enum Low
			{
				A("At one point, it'll be as cold as "),
				B("Put on your jumpers, at one time it's only ");
				
				public String message;

				private Low(String message)
				{
					this.message = message;
				}
				  
				 public static String getRandom() 
				{
					 return values()[(int) (Math.random() * values().length)].message;
				}
			}
			
			public enum Desc
			{
				A("Here's my favourite vague statement again: "),
				B("Vague outlook: ");
				
				public String message;

				private Desc(String message)
				{
					this.message = message;
				}
				  
				 public static String getRandom() 
				{
					 return values()[(int) (Math.random() * values().length)].message;
				}
			}
		}
	}
}
