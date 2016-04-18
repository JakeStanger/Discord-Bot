package bot;

import javax.security.auth.login.LoginException;

import commands.Play;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.utils.SimpleLog;

public class Bot
{
	private static Bot instance;
	private SimpleLog logger;
	
	public Bot()
	{
		instance = this;
		logger = SimpleLog.getLog("GrandadBotbags");
	}
	
	public static Bot getInstance()
	{
		return instance;
	}
	
	/**
	 * Starts the application and joins the bot to the server
	 * @return Whether or not the initialisation succeeded
	 */
	public boolean init(String token)
    {
        boolean success = true;
		
		try
        {
            new JDABuilder().setBotToken(token).addListener(new Play()).buildBlocking();
        }
        catch (IllegalArgumentException e)
        {
            logger.fatal("The config was not populated. Please enter an email and password.");
            success = false;
        }
        catch (LoginException e)
        {
        	logger.fatal("The provided email / password combination was incorrect. Please provide valid details.");
            success = false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            success = false;
        }
		
		return success;
    }
}
