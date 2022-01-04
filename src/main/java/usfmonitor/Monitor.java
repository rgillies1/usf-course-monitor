package usfmonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import net.dv8tion.jda.api.entities.Activity;

public class Monitor {
	private static boolean useBot;
	private static boolean useSelenium;
	private static String token;
	private static String userID;
	private static String chromiumPath;
	private static String OASISLoginPath;
	private static List<String> CRNs;
	private static ArrayList<String> registrationsMade;
	
	private static final int DEFAULT_TIMEOUT_TIME = 120;
	private static final Activity DEFAULT_ACTIVITY = Activity.watching("them courses");
	private static final String COURSE_INVENTORY_PAGE_SUBSTRING = "https://usfonline.admin.usf.edu/pls/prod/bwckschd.p_disp_detail_sched?term_in=202201&crn_in=";
	private static final int MILLISECONDS_BETWEEN_CHECKS = 10000;
	private static final int ITERATIONS_PER_REFRESH = 3;
	
	static Bot monitorBot;
	static OASISWebDriver webDriver;
	
	public static void main(String[] args) {
		registrationsMade = new ArrayList<>();
		getInfoFromDialog();
		
		try {
			if(useBot) {
				monitorBot = new Bot(token);
				
				if(monitorBot.initialize(DEFAULT_ACTIVITY)) {
					monitorBot.sendMessage(userID, "Bot initialized successfully!");
				} else {
					showErrorAndExit("There was a problem initializing the bot.");
				}
			}
			if(useSelenium) {
				webDriver = new OASISWebDriver(chromiumPath, OASISLoginPath, DEFAULT_TIMEOUT_TIME);
				webDriver.initialize();
				webDriver.startLogin();
				if(useBot) monitorBot.setWebDriver(webDriver);
			}
		} catch (IllegalStateException ille) {
			showErrorAndExit("The program could not find the Chromium installation.");
		} catch (LoginException loge) {
			showErrorAndExit("The provided bot token was invalid.");
		} catch (IllegalArgumentException illarge) {
			showErrorAndExit("The provided Discord User ID was invalid.\nNOTE: The program requires a Discord ID, not a Discord tag.");
		} catch (InvalidArgumentException inve) {
			showErrorAndExit("The provided path to the registration page was not valid.");
		} catch (TimeoutException time) {
			showErrorAndExit("The user did not log in within the default timeout time (" + DEFAULT_TIMEOUT_TIME + " seconds).");
		} catch (Exception e) {
			e.printStackTrace();
			showErrorAndExit("An unknown error occured.");
		}
		
		beginMonitoring();
		
	}
	
	private static void getInfoFromDialog() {
		InfoWindow window = new InfoWindow();
		
		window.getUserInput();
		
		useBot = window.useBot();
		useSelenium = window.useSelenium();
		token = window.getBotToken();
		userID = window.getDiscordUserID();
		OASISLoginPath = window.getOASISWebPath();
		chromiumPath = window.getChromiumPath();
		CRNs = window.getCRNs();
		
		window.dispose();
	}
	
	private static void beginMonitoring() {
		int monitorIters = 0;
		while(true) {
			try {
				for(String CRN : CRNs) {
					if(CRN.isBlank()) showErrorAndExit("No CRNs were entered.");
					String fullPage = COURSE_INVENTORY_PAGE_SUBSTRING + CRN;
					Document d = Jsoup.connect(fullPage).get();
					
					Elements body = d.select("body");
					if(body.get(0).getElementsByClass("errortext").size() != 0) {
						showErrorAndExit("An error occured while getting the course information for the following CRN: " + CRN + ".");
					}
					
					Elements tableHeaders = body.select("th");
					String courseName = tableHeaders.get(0).ownText();
					Elements tableData = body.select("td");
					int iters = 0;
					int remain = -1;
					for(Element e : tableData) {
						String text = e.ownText();
						if(text.length() < 10 && text.length() > 0) {
							if(iters == 2) {
								remain = Integer.parseInt(text);
								break;
							}
							iters++;
						}
					}
					if(remain > 0) {
						boolean alreadyRegistered = false;
						for(String s : registrationsMade) {
							if(s.equals(CRN)) alreadyRegistered = true;
						}
						if(!alreadyRegistered) {
							String message;
							if(remain == 1) message = courseName + " - There is " + remain + " seat remaining in this section!";
							else message = courseName + " - There are " + remain + " seats remaining in this section!";
							if(useBot) monitorBot.sendMessage(userID, message);
							if(useSelenium) {
								if(useBot) monitorBot.sendMessage(userID, "Attempting to register...");
								String resultMessage;
								System.out.println(courseName);
								String attemptResult = webDriver.attemptRegistration(CRN);
								
								if(attemptResult.isEmpty()) {
									resultMessage = "Successfully registered for " + courseName + " (" + CRN + ")";
								} else {
									resultMessage = "Failed to register for " + courseName + " (" + CRN + ")\n";
									resultMessage += "The following error information was provided: " + attemptResult;
								}
								if(attemptResult.isEmpty() || attemptResult.equals("redundant")) registrationsMade.add(CRN);
								if(useBot) monitorBot.sendMessage(userID, resultMessage);
							}
						}
					}
				}
				if(useSelenium) {
					if(monitorIters == 2) {
						webDriver.refresh();
					}
					monitorIters++;
					monitorIters %= ITERATIONS_PER_REFRESH;
				}
				Thread.sleep(MILLISECONDS_BETWEEN_CHECKS);

			} catch(IOException ioe) {
				ioe.printStackTrace();
			} catch (InterruptedException inte) {
				inte.printStackTrace();
			} catch (WebDriverException webe) {
				showErrorAndExit("Connection to the browser could not be established - did you close it?");
			} 
		}
	}
	
	private static void showErrorAndExit(String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage + "\n\nThe program will now exit.", "Error", JOptionPane.ERROR_MESSAGE);
		if(useSelenium && webDriver != null) webDriver.exit();
		System.exit(-1);
	}
}
