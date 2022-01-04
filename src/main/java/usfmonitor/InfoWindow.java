package usfmonitor;

import java.util.List;

import javax.swing.JFrame;

public class InfoWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private boolean useBot;
	private boolean useSelenium;
	private String botToken;
	private String discordUserID;
	private String chromiumPath;
	private String OASISWebPath;
	private List<String> CRNs;
	
	InfoWindow() {
		
	}
	
	public void getUserInput() {
		InfoDialog dialog = new InfoDialog(this);
		
		useBot = dialog.useBot();
		useSelenium = dialog.useSelenium();
		botToken = dialog.getToken();
		discordUserID = dialog.getID();
		chromiumPath = dialog.getChromiumPath();
		OASISWebPath = dialog.getOASISWebPath();
		CRNs = dialog.getCRNs();
		
		dialog.dispose();
	}
	
	public boolean useBot() {
		return useBot;
	}

	public boolean useSelenium() {
		return useSelenium;
	}

	public String getBotToken() {
		return botToken;
	}

	public String getDiscordUserID() {
		return discordUserID;
	}

	public String getChromiumPath() {
		return chromiumPath;
	}

	public String getOASISWebPath() {
		return OASISWebPath;
	}

	public List<String> getCRNs() {
		return CRNs;
	}

}
