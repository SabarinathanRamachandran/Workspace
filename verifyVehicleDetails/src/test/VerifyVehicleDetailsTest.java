package test;

import pages.PageObjectRepository;
import service.FileInfoBean;
import service.FileService;
import utils.CSVutil;
import utils.Excelutil;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Main
 * Date Modified	: 07/04/2018 , Modified By: Sabarinathan Ramachandran
 * Comments			: 
 */
public class VerifyVehicleDetailsTest {
	private static Excelutil excelUtil;
	private static CSVutil csvUtil;    	
	private static WebDriver driver = new ChromeDriver();
	private static PageObjectRepository repPage; 
	private static String dvlaURL = "https://www.gov.uk/get-vehicle-information-from-dvla";
	private static PrintWriter executionlog;
	
    /**
     * Startup
     * @throws UnsupportedEncodingException 
     * @throws FileNotFoundException 
     */	
	public static void Setup() throws FileNotFoundException, UnsupportedEncodingException {
		String logfilePath = "logs";
		String currentTime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
       try {
    	   executionlog = new PrintWriter(logfilePath+"/ExecutionLog_"+currentTime+".txt", "UTF-8");
        } catch (Exception e) {
        	e.printStackTrace();
        }		
		executionlog.println("START Time:"+currentTime);
		
		//Navigate to the URL and Maximise the window
		driver.get(dvlaURL);
		executionlog.println("Navigated to DVLA site.");
		driver.manage().window().maximize();
	}
	
    /**
     * Close down
     */	
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        String currentTime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        executionlog.println("Test Execution Completed.");
        executionlog.println("END Time:"+currentTime);
        executionlog.close();
    }
	
    /**
     * Main: Read the vehicle details form the input files and verify the same in the web application
     *
     */	
	public static void main(String[] args) throws IOException, InterruptedException{
		int tCnt = 1;
		int maxCnt;
		int i;
		String line;

		Setup();
		
		repPage = new PageObjectRepository(driver);
		String inputfilePath = "inputfiles";
		
		List<FileInfoBean> fileList = new FileService().getSupportedMIMEtypeFiles(inputfilePath);
		executionlog.println("Extracted the list of files with supported MIME type.");
		
		excelUtil = new Excelutil();
		csvUtil = new CSVutil();
		
		if (fileList.size() > 0){
			for (FileInfoBean fileInfo : fileList) {
				tCnt = tCnt + 1;

				switch (fileInfo.getFileExtention()) { 
				case "csv"://CSV file 
					executionlog.println("CSV file found.");
					
					System.out.println("=======" +fileInfo.getFilename()+"=======");
					csvUtil.openCSVFile(fileInfo.getFilePath());
					maxCnt = csvUtil.getRowCount();
					for (i=0;i<maxCnt;i++) {
						System.out.println("Record:"+(i+1));
						line = csvUtil.getRowData(i);
						RunTheTest(line,i);
						System.out.println(" ");
					}					 
					break;
				case "xlsx":// Excel file
					executionlog.println("Excel file found.");
					
					System.out.println("=======" +fileInfo.getFilename()+"=======");
					excelUtil.openExcelFile(fileInfo.getFilePath());
					maxCnt = excelUtil.getRowCount();
					for (i=0;i<maxCnt;i++) {
						System.out.println("Record:"+(i+1));
						line = excelUtil.getRowData(i);
						RunTheTest(line,i);
						System.out.println(" ");
					}					
					break;
				}
			}
			
			tearDown();
		}else{
			writeToExecutionResult("No files found with the configured MIME type.");
		}
	}


   /**
     * Run the test by entering the details in the web application.
     * Compare the values again the given data in the input file
     */	
	public static void RunTheTest(String line, int rCnt) throws IOException, InterruptedException{
		
		String[] vehicleDetails = null;
		String expRegNumber = "";
		String expMake = "";
		String expColour = "";
		String actRegNumber = "";
		String actMake = "";
		String actColour = "";		
		vehicleDetails = line.split(",");
		String errFilePath = "screenshots";
		
		//After extracting the vehicle details, test the same.
		if (vehicleDetails != null && vehicleDetails.length > 1){
			executionlog.println("Running the test for - "+line);
			
			// Vehicle details from the input file
			expRegNumber = vehicleDetails[0];
			expMake = vehicleDetails[1];
			expColour = vehicleDetails[2];

			Boolean isPresent = repPage.isElementVisible(repPage.START_BUTTON);
			if (isPresent) {//Test Case 1
				repPage.clickStartNowButton();
			}
			
			isPresent = repPage.isElementVisible(repPage.REGNO_INPUTTEXT);
			if (isPresent) {
				//Enter the vehicle registration number and click on continue button
				repPage.enterRegisterationNumber(expRegNumber);
				repPage.clickContinueButton();
				
				isPresent = repPage.isElementVisible(repPage.VEHICLE_DETAILS_LOOKINGFOR_LABEL);
				if (isPresent) { //Test Case 2
					//Extract the actual details shown in the screen
					actRegNumber = repPage.getVehicleRegNumber();
					actMake = repPage.getVehicleMake();
					actColour = repPage.getVehicleColour();	

					//Verify the vehicle details in this page and write the execution result.
					verifyFieldValue("Registration Number	:",expRegNumber,actRegNumber);
					verifyFieldValue("Make			:",expMake,actMake);
					verifyFieldValue("Colour		:",expColour,actColour);
					
					//Press the back link to go to previous screen.
					repPage.clickBackHyperLink();
					
					//Verify the Registration Number Entry Page comes up.
					isPresent = repPage.isElementVisible(repPage.REGNO_INPUTTEXT);
					if (!isPresent){
						writeToExecutionResult("Err: Back to previous page failed. Registration Number text field not found.");
					}							
				}else{
					//Check the negative case - You must enter your registration number in a valid format
					isPresent = repPage.isInvalidRegistrationNumberError(PageObjectRepository.PLEASE_ENTER_REGISTRATION_NUMBER_LABEL);
					if (isPresent){//Test Case 3
						writeToExecutionResult("Registration number not entered.");
					}else{
						isPresent = repPage.isInvalidRegistrationNumberError(PageObjectRepository.INVALID_FORMAT_LABEL);
						if (isPresent){//Test Case 4
								writeToExecutionResult("Invalid registration number "+expRegNumber);
						}else{
							//Check the negative case -  Vehicle details could not be found
							isPresent = repPage.isVehicleDetailsNotFound();
							if (isPresent){//Test Case 5
								//Press the search again link to go to previous screen.
								repPage.clickSearchAgainHyperLink();
								writeToExecutionResult("Vehicle details could not be found for registration number "+expRegNumber);
							}else{
								// Unexpected Error - Take a screenshot 
								File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE); 
								// now copy the screenshot to desired location using copyFile //method 
								String currentTime = new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date());
				                FileUtils.copyFile(src, new File(errFilePath+"/Errscreenshot_"+currentTime+".png"));
				                
				                writeToExecutionResult("Err: Unexpected Page. Screenshot taken. Filename- Errscreenshot_"+currentTime+".png");
				                
								//on unexpected error navigate to the home page
								driver.get(dvlaURL);
							}
						}
					}
				}
			}else{
				writeToExecutionResult("Registration input text field not found");
				//on unexpected error navigate to the home page
				driver.get(dvlaURL);				
			}
		}
	}

	 /**
     * Is to verify the field value expected vs actual.
     */		
	public static void verifyFieldValue(String lblStr, String expStr,String actStr) throws IOException{
		//Remove the  blank space if any in the registration number
		String trimmedExpString = expStr.replaceAll("\\s","");;
		String trimmedActString = actStr.replaceAll("\\s","");;
		
		if (trimmedExpString.equalsIgnoreCase(trimmedActString)){
			writeToExecutionResult("P: " + lblStr + actStr);
		}else{
			writeToExecutionResult("F: " + lblStr + "Expected: "+ expStr + " - Actual: " + actStr);
		}
	}

	 /**
	 * Is to print the execution log and display the result
	 */		
	public static void writeToExecutionResult(String lblStr) throws IOException{
		//Execution result can be written to the separate text file. Currently printing in the console
		//executionlog.println(lblStr);
		System.out.println(lblStr);
	}
}
