package vehicleinfo;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import service.FileInfoBean;
import service.FileService;

public class FileServiceTest {

	@Test
	public void testAllFiles() throws IOException {
		String inputfilePath = "inputfiles";
		List<FileInfoBean> fileList = new FileService().getFileInfo(inputfilePath);
		assertEquals(fileList.size() , 10);
	}
	
	@Test
	public void testMIMESupportedFiles()  throws Exception {
		String inputfilePath = "inputfiles";
		List<FileInfoBean> fileList = new FileService().getSupportedMIMEtypeFiles(inputfilePath);
		assertEquals(fileList.size() , 2);
	}

}
