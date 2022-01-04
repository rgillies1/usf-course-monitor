package usfmonitor;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OASISWebDriver {
	final String chromiumPath;
	final String OASISWebPath;
	final Duration timeout;
	WebDriver OASISDriver;
	String registrationScreenWebPath;
	WebDriverWait wait;
	
	OASISWebDriver(String chromiumPath, String OASISWebPath, long timeoutSeconds) {
		this.chromiumPath = chromiumPath;
		this.OASISWebPath = OASISWebPath;
		timeout = Duration.ofSeconds(timeoutSeconds);
	}
	
	public void initialize() {
		System.setProperty("webdriver.chrome.driver", chromiumPath);
		
		OASISDriver = new ChromeDriver();
		wait = new WebDriverWait(OASISDriver, timeout);
	}
	
	public boolean startLogin() throws InvalidArgumentException {
		if(OASISDriver == null || wait == null) return false;
		OASISDriver.get(OASISWebPath);

		
		// OASIS logs in using Microsoft's central authority. Wait until the user logs in completely (or timeout)
		// "headerwrapperdiv" is the OASIS banner class name - its presence indicates we are successfully logged in to OASIS
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("headerwrapperdiv")));
		
		// Navigate to registration screen
		OASISDriver.get("https://usfonline.admin.usf.edu/pls/prod/bwskfreg.P_AltPin");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("datadisplaytable")));
		
		return true;
	}
	
	public String attemptRegistration(String CRN) {
		String result = "";

		if(!isRegistered(CRN)) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("datadisplaytable")));
			WebElement CRNTable = OASISDriver.findElement(By.className("dataentrytable"));
			WebElement firstField = CRNTable.findElements(By.tagName("input")).get(1);
			firstField.sendKeys(CRN);
			for(WebElement e : OASISDriver.findElements(By.tagName("input"))) {
				if(e.getDomAttribute("value") != null && e.getDomAttribute("value").equals("Submit Changes")) {
					e.click();
					break;
				}
			}
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("datadisplaytable")));
			result = getErrors();
		} else result = "redundant";
		OASISDriver.get("https://usfonline.admin.usf.edu/pls/prod/bwskfreg.P_AltPin");
		return result;
	}
	
	private boolean isRegistered(String CRN) {
		for(WebElement element : OASISDriver.findElements(By.tagName("td"))) {
			if(element.getText().equals(CRN)) return true;
		}
		return false;
	}
	
	private String getErrors() {
		String error = "";
		for(WebElement e : OASISDriver.findElements(By.className("datadisplaytable"))) {
			if(e.getDomAttribute("summary") != null && e.getDomAttribute("summary").equals("This layout table is used to present Registration Errors.")) {
				for(WebElement tableElement : e.findElements(By.tagName("td"))) {
					if(!tableElement.getText().isBlank() && !tableElement.getDomAttribute("class").equals("ddheader"))
						error += tableElement.getText();
				}
			}
		}
		return error;
	}
	
	public void refresh() {
		OASISDriver.navigate().refresh();
	}
	
	public void exit() {
		try {
			if(OASISDriver != null) OASISDriver.close();
		} catch(WebDriverException e) {
			System.exit(-1);
		}
	}
}
