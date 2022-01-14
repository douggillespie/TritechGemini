package tritechplugin.tritech.ecd;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import tritechplugin.tritech.image.ECDImage;
import tritechplugin.tritech.image.ECDImageMaker;

public class EcdFileTest {

//	private static String sample = "E:\\Gemini\\LD20201110\\101933_IMG.ecd";
//	private static String sample = "C:\\Users\\Dougl\\OneDrive - University of St Andrews\\Documents\\Genesis\\Log Files20210414\\data_2021-04-14-120707.ecd";
//	private static String sample = "F:\\AAM data\\LD20180823\\084721_IMG.ecd";
	private static String sample = "C:\\ProjectData\\RobRiver\\ecdexamples\\data_2021-04-12-124025.ecd";
//	private static String sample = "E:\\Gemini\\LD20201111\\000028_IMG.ecd";
	
	private String fileName;

	public static void main(String[] args) {
		new EcdFileTest(sample).run();
	}
	
	public EcdFileTest(String fileName) {
		this.fileName = fileName;
	}

	private void run() {
		File aFile = new File(fileName);
		long t0 = System.currentTimeMillis();
			long firstTime = ECDFile.findFirstRecordTime(aFile);
		long t1 = System.currentTimeMillis();
		ECDFile ecdFile = new ECDFile(aFile);
		long t2 = System.currentTimeMillis();
		/*
		 * Now just check that we can go throuh each record one by one and reload it. 
		 * Once properly deployed, this bit won't run. 
		 * 
		 */
		ECDImageMaker imageMaker = new ECDImageMaker();
		for (ECDRecordSet ecdR : ecdFile.getEmptyECDRecords()) {
//			ecdR.readFully();
			ECDImage ecdImage = imageMaker.extractImage(ecdR);
//			imageMaker.copyToClipboard(ecdImage);
			ecdR.freeImageData();
		}
		
		long t3 = System.currentTimeMillis();
		System.out.printf("Time to read %d images from file = %d then %d millis and to create all images =%d \n", ecdFile.getnImage(), t1-t0, t2-t1, t3-t2);
	}
	
}
