package pages;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class is to declare the page objects in the Get Vehicle Information from DVLA Pages
 * Date Modified	: 07/04/2018 , Modified By: Sabarinathan Ramachandran
 * Comments			: 
 */
public class PageObjectRepository {
	
	private WebDriver driver;
    public By START_BUTTON = By.xpath("//*[@id=\"get-started\"]/a");
    public By REGNO_INPUTTEXT = By.id("Vrm");
    public By REGNO_NOTVALID_LABEL = By.xpath("//*[@id=\"content\"]/form/div/div/div[1]/ul/a");
    public By CONTINUE_BUTTON = By.name("Continue");
    public By BACK_LINK = By.xpath("//*[@id=\"content\"]/div/a");
    public By SEARCH_AGAIN_LINK = By.xpath("//*[@id=\"content\"]/div/div/a");
    public By VEHICLE_DETAILS_LOOKINGFOR_LABEL = By.xpath("//*[@id=\"pr3\"]/div/h1");
    public By VEHICLE_DETAILS_NOTFOUND_LABEL = By.xpath("//*[@id=\"content\"]/div/h3");
    public By VEHICLE_REGNO = By.xpath("//*[@id=\"pr3\"]/div/ul/li[1]/span[2]");
    public By VEHICLE_MAKE = By.xpath("//*[@id=\"pr3\"]/div/ul/li[2]/span[2]/strong");
    public By VEHICLE_COLOUR = By.xpath("//*[@id=\"pr3\"]/div/ul/li[3]/span[2]/strong");
	public static final String INVALID_FORMAT_LABEL = "You must enter your registration number in a valid format";
	public static final String PLEASE_ENTER_REGISTRATION_NUMBER_LABEL = "Please enter your registration number";
    
	//Constructor
	public PageObjectRepository(WebDriver webDriver) {
		this.driver = webDriver;
        // wait for page to finish loading
        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(START_BUTTON));

        // see if we're on the right page
        if (!driver.getTitle().startsWith("Get vehicle information from DVLA"))
        {
            throw new IllegalStateException("This is not the DVLA start page. Current URL: " + driver.getCurrentUrl());
        }
	}

	public void clickStartNowButton() {
		driver.findElement(START_BUTTON).click();
	}

	public void enterRegisterationNumber(String regNo) {
		driver.findElement(REGNO_INPUTTEXT).clear();
		driver.findElement(REGNO_INPUTTEXT).sendKeys(regNo);
	}

	public void clickContinueButton() {
		driver.findElement(CONTINUE_BUTTON).click();
	}
	
	public void clickBackHyperLink() {
		driver.findElement(BACK_LINK).click();
	}	
	
	public void clickSearchAgainHyperLink() {
		driver.findElement(SEARCH_AGAIN_LINK).click();
	}
	public String getVehicleRegNumber(){
		return driver.findElement(VEHICLE_REGNO).getText();
	}
	
	public String getVehicleMake(){
		return driver.findElement(VEHICLE_MAKE).getText();
	}
	
	public String getVehicleColour(){
		return driver.findElement(VEHICLE_COLOUR).getText();
	}

	 /**
	 * This Function is to verify whether element visible
	 */
	public Boolean isElementVisible(By elementStr) throws IOException{
		//check the element
		try{
		(new WebDriverWait(driver,3)).until(new ExpectedCondition<Boolean>(){
			public Boolean apply(WebDriver d){
				return driver.findElement(elementStr).isDisplayed();
			}
		});
		}catch (Exception e){
			return false;
		}
		return true;
	}

	 /**
	 * This Function is to check invalid format error
	 */		
	public Boolean isInvalidRegistrationNumberError(String lblStr) throws IOException{
		//check the element
		try{
		(new WebDriverWait(driver,3)).until(new ExpectedCondition<Boolean>(){
			public Boolean apply(WebDriver d){
				return driver.findElement(REGNO_NOTVALID_LABEL).isDisplayed();
			}
		});
		}catch (Exception e){
			return false;
		}
		
		//check the string
		String rStr = driver.findElement(REGNO_NOTVALID_LABEL).getText();
		if (rStr.equals(lblStr)){
			return true;	
		}else{
			return false;
		}
	}

	 /**
	 * This is to check vehicle details not found error
	 */		
	public Boolean isVehicleDetailsNotFound() throws IOException{
		//check the element
		try{
		(new WebDriverWait(driver,3)).until(new ExpectedCondition<Boolean>(){
			public Boolean apply(WebDriver d){
				return driver.findElement(VEHICLE_DETAILS_NOTFOUND_LABEL).isDisplayed();
			}
		});
		}catch (Exception e){
			return false;
		}
		
		//check the string
		String rStr = driver.findElement(VEHICLE_DETAILS_NOTFOUND_LABEL).getText();
		if (rStr.equals("Vehicle details could not be found")){
			return true;	
		}else{
			return false;
		}
	}

}
