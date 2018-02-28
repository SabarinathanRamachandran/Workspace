package vehicleinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class verifyVehicleDetails {
 
		static WebDriver driver = new ChromeDriver();
		
		/*============================================================================================================================================
		 This Function: Is to wait for the window to load and return the actual window title
		 Date Modified: 27/02/2018
		 Modified By  : Saba
		 Comments     : 
		 ============================================================================================================================================*/		
		public static String waitUntillWindowTitle(String wtitle) throws IOException{
			//Verify that window title is a expected
			try{
			(new WebDriverWait(driver,10)).until(new ExpectedCondition<Boolean>(){
				public Boolean apply(WebDriver d){
					return d.getTitle().startsWith(wtitle);
				}
			});
			}catch (Exception e){
				return null;
			}
			String actualTitle = driver.getTitle();
			writeToExecutionResult("Page Found - "+actualTitle);
			return actualTitle;
		}
		
		/*============================================================================================================================================
		 This Function: Is to verify the field value expected vs actual
		 Date Modified: 27/02/2018
		 Modified By  : Saba
		 Comments     : 
		 ============================================================================================================================================*/		
		public static void verifyFieldValue(String lblStr, String expStr,String actStr) throws IOException{
			//Remove the  blank space if any in the registration number
			String trimmedExpString = expStr.replaceAll("\\s","");;
			String trimmedActString = actStr.replaceAll("\\s","");;
			
			if (trimmedExpString.equalsIgnoreCase(trimmedActString)){
				writeToExecutionResult(lblStr + " MATCH. " + actStr );
			}else{
				writeToExecutionResult(lblStr + " NOT MATCH. Expected: " + expStr + " - Actual: " + actStr);
			}
		}

		/*============================================================================================================================================
		 This Function: Is to verify whether element visible and the text is correct
		 Date Modified: 27/02/2018
		 Modified By  : Saba
		 Comments     : 
		 ============================================================================================================================================*/		
		public static Boolean verifyElementVisible(String xpathStr, String lblStr) throws IOException{
			//find the elements
			Boolean isPresent = driver.findElements(By.xpath(xpathStr)).size() > 0;
			
			if (isPresent){
				String rStr = driver.findElement(By.xpath(xpathStr)).getText();
				if (rStr.equals(lblStr)){
					return true;	
				}
			}
			return false;
		}

		/*============================================================================================================================================
		 This Function: Is to print the execution log and display the result
		 Date Modified: 27/02/2018
		 Modified By  : Saba
		 Comments     : 
		 ============================================================================================================================================*/		
		public static void writeToExecutionResult(String lblStr) throws IOException{
			//Execution result can be written to the separate text file. Currently printing.
			Date currentTime = new Date();
			System.out.println(currentTime +": "+ lblStr);
		}
		
		/*============================================================================================================================================
		 * ============================================================================================================================================
		 Main: Run the Selenium Test 
		 Date Modified: 28/02/2018
		 Modified By  : Saba
		 Comments     : 
		 ============================================================================================================================================
		 ============================================================================================================================================*/		
		public static void RunTheTest(String[] vehicleDetails, int rCnt) throws IOException, InterruptedException{

			String dvlaurl = "https://www.gov.uk/get-vehicle-information-from-dvla";
			String Page1 = "Get vehicle information from DVLA - GOV.UK";
			
			String actualTitle = "";
			String expRegNumber = "";
			String expMake = "";
			String expColour = "";
			String actRegNumber = "";
			String actMake = "";
			String actColour = "";		
			
			//After extracting the vehicle details, test the same.
			if (vehicleDetails != null && vehicleDetails.length > 0){
				// Extract the vehicle details
				expRegNumber = vehicleDetails[0];
				expMake = vehicleDetails[1];
				expColour = vehicleDetails[2];
				
				if (rCnt == 0){
					//Navigate to the URL and Maximise the window
					driver.get(dvlaurl);
					driver.manage().window().maximize();
					writeToExecutionResult("ChromeDriver Navigated to " + dvlaurl);
		
					//Verify that window
					actualTitle = waitUntillWindowTitle(Page1);
					if (actualTitle != null){
						driver.findElement(By.xpath("//*[@id=\"get-started\"]/a")).click();
						writeToExecutionResult("Pressed the start button.");
					}else{
						writeToExecutionResult("Err: Unexpected Page: "+actualTitle);
					}					
				}else{
					actualTitle = "Same Page";
				}

				//Verify that window
				//Check whether vehicle details displayed or not
				Boolean isPresent = verifyElementVisible("//*[@id=\"content\"]/form/div/div/h1","Enter the registration number of the vehicle");
				if (isPresent){
					WebElement txtRegNo = driver.findElement(By.id("Vrm"));
					txtRegNo.clear();
					writeToExecutionResult("Seach for Registration Number: "+expRegNumber);
					txtRegNo.sendKeys(expRegNumber);
					
					driver.findElement(By.name("Continue")).click();
					Thread.sleep(3000);

					//Check whether vehicle details displayed or not
					isPresent = verifyElementVisible("//*[@id=\"pr3\"]/div/h1","Is this the vehicle you are looking for?");
					if (isPresent){
						writeToExecutionResult("Search Success. Vehicle details found in the screen.");
						writeToExecutionResult("Comparison Result:");
						
						//Extract the actual details shown in the screen
						actRegNumber = driver.findElement(By.xpath("//*[@id=\"pr3\"]/div/ul/li[1]/span[2]")).getText();
						actMake = driver.findElement(By.xpath("//*[@id=\"pr3\"]/div/ul/li[2]/span[2]/strong")).getText();
						actColour = driver.findElement(By.xpath("//*[@id=\"pr3\"]/div/ul/li[3]/span[2]/strong")).getText();
						
						//Verify the vehicle details in this page
						verifyFieldValue("Registration Number	:",expRegNumber,actRegNumber);
						verifyFieldValue("Make			:",expMake,actMake);
						verifyFieldValue("Colour			:",expColour,actColour);
						
						//Press the back link to go to previous screen.
						driver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/a")).click();

						//Verify the Registration Number Entry Page comes up.
						isPresent = verifyElementVisible("//*[@id=\"content\"]/form/div/div/h1","Enter the registration number of the vehicle");
						if (isPresent){
							writeToExecutionResult("Back to previous page to search next record.");
							System.out.println("-");
						}else{
							writeToExecutionResult("Err: Back to previous page failed. Registration Number text field not found.");
							System.out.println("-");
						}							
					}else{
						//Check the negative case - You must enter your registration number in a valid format
						isPresent = verifyElementVisible("//*[@id=\"content\"]/form/div/div/div[1]/ul/a","You must enter your registration number in a valid format");
						if (isPresent){
								writeToExecutionResult("Invalid Registration Number.");
								System.out.println("-");
							}else{
								//Check the negative case -  Vehicle details could not be found
								isPresent = verifyElementVisible("//*[@id=\"content\"]/h3","Vehicle details could not be found");
								if (isPresent){
										writeToExecutionResult("Vehicle details could not be found.");
										System.out.println("-");
									}else{
										writeToExecutionResult("Err: Unexpected Page.");
										System.out.println("-");
										}
							}
						//System.out.println("***********************************");
					}
				}
				else{
					writeToExecutionResult("Redirected to Wrong Page - "+actualTitle);
				}
			}else{
				writeToExecutionResult("Vehicle Data Not Found in the source file.");
			}			
		}
		
		/*============================================================================================================================================
		 This Function: Main function to test the webpage
		 Date Modified: 27/02/2018
		 Modified By  : Saba
		 Comments     : 
		 ============================================================================================================================================*/
		public static void main(String[] args) throws IOException, InterruptedException{

			String line;
			String[] vehicleDetails = null;
			
			
			int tCnt = 1;
			int rCnt = 0;
			int maxCnt = 0;
					
			//Get the vehicle details from the file
			List<FileBean> fileList = new FileService().getSupportedMIMEtypeFiles("D:/Selenium/ToolsQA/InputFilesVehicleDetails/");
			if (fileList.size() > 0){
				for (FileBean file : fileList) {
					System.out.println("=========================================================================================");
					writeToExecutionResult("File:"+tCnt);
					tCnt = tCnt + 1;
					System.out.println("=========================================================================================");
				
					//read csv file
					if("csv".equals(file.getFileExtention())){
						//Read the content from the csv file 
						try (BufferedReader br = new BufferedReader(new FileReader(file.getFilePath()))) {
							while ((line = br.readLine()) != null) {
								// use comma as separator to read the data
								vehicleDetails = line.split(",");
								//System.out.println("Vehicle details read from - "+file.getFilename());
								writeToExecutionResult("Record:"+(rCnt+1));
								System.out.println("***************************************");
								RunTheTest(vehicleDetails,rCnt);
								rCnt = rCnt + 1;
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}				
					}
					
					// read excel file
					if("xlsx".equals(file.getFileExtention())){
						 try {
							 // Read the content from the excel file
							 File src = new File(file.getFilePath());
							 FileInputStream fs = new FileInputStream(src);
						 
							 // Load the workbook
							 XSSFWorkbook wBook = new XSSFWorkbook(fs);
							 // Load the first sheet
							 XSSFSheet sheet1= wBook.getSheetAt(0);
							 maxCnt = sheet1.getPhysicalNumberOfRows();
							 
							 for (int i=0;i<maxCnt;i++) {
								 writeToExecutionResult("Record:"+(i+1));
								 System.out.println("***************************************");
								 line = sheet1.getRow(i).getCell(0).getStringCellValue()+","+sheet1.getRow(i).getCell(1).getStringCellValue()+","+sheet1.getRow(i).getCell(2).getStringCellValue();
								 vehicleDetails = line.split(",");
								 RunTheTest(vehicleDetails,i);								 
							 }

							 //System.out.println("Vehicle details read from - "+file.getFilename());
							 wBook.close();
						} catch (Exception e) {
							writeToExecutionResult(e.getMessage());
							}
					}
				}
				driver.quit();
			}else{
				writeToExecutionResult("No files found with the configured MIME type."+vehicleDetails);
			}
		}
	}

