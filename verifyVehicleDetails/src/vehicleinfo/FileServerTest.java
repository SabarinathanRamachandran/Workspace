package vehicleinfo;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FileServerTest {
	FileService fs = new FileService();
	String fPath = "D:\\Selenium\\ToolsQA\\InputFilesVehicleDetails";
	
	@Test
	public void getFileInfoReturnsCorrectly() throws IOException{
		List <FileBean> fileList = fs.getFileInfo(fPath);
		Assert.assertEquals(fileList.size(),10);
	}

	@Test
	public void getSupportedMIMEtypeFilesReturnsCorrectly() throws IOException{
		List <FileBean> fileList = fs.getSupportedMIMEtypeFiles(fPath);
		Assert.assertEquals(fileList.size(),2);
	}

}
