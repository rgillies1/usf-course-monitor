package usfmonitor;

import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;

/***
 * Represents the Discord bot that the monitor will use to send updates
 * @author rgillies1
 *
 */
public class Bot {
	final String token;
	JDA bot;
	OASISWebDriver webDriver;
	
	Bot(String token) {
		this.token = token;
	}
	
	/**
	 * Builds the Discord bot JDA so it can be used to send messages
	 * @param userID The user this bot will be sending messages to
	 * @param status The status that will display for this bot on Discord
	 * @return true if the bot was successfully built, otherwise false
	 * @throws LoginException 
	 */
	public boolean initialize(Activity status) throws LoginException {
		JDABuilder builder = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES);
		builder.setActivity(status);
		
		builder.addEventListeners(new ListenerAdapter() {
			@Override
			public void onMessageReceived(MessageReceivedEvent event) {
				Message msg = event.getMessage();
				if(msg.getContentRaw().equals("~clear")) {
					try {
						MessageChannel channel = event.getChannel();

						List<Message> messages = channel.getHistory().retrievePast(50).complete();
						messages.removeIf(m -> m.getAuthor().equals(msg.getAuthor()));
						channel.purgeMessages(messages);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		bot = builder.build();
		if(bot == null) return false;
		else return true;
	}
	
	public void setWebDriver(OASISWebDriver webDriver) {
		this.webDriver = webDriver;
	}
	
	public void sendMessage(String userID, String message) {
		RestAction<User> recipient = bot.retrieveUserById(userID);
		recipient.queue(user -> {
			user.openPrivateChannel().flatMap(channel -> channel.sendMessage(message)).queue();
		});
	}
}
