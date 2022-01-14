package tritechplugin.tritech.ecd;

import java.io.File;
import java.io.IOException;

import PamUtils.PamCalendar;
import PamguardMVC.PamDataUnit;
import tritechplugin.tritech.ECDDataUnit;

/*
 * ECD files always come in threes, a target image, a ping tail and an acoustic zoom
 * Allow it to extend data unit, so that it can be used with tooltips in the viewer. 
 */
public class ECDRecordSet extends PamDataUnit {

	/**
	 * Position of the start of the target image record so that it can be
	 * reread with full data. 
	 */
	private long filePosition;
	
	private long timeMillis;
	
	private GeminiTargetImage targetImage;
	
	private GeminiPingTail pingTail;

	private GeminiAcousticZoom acousticZoom;

	private ECDFile ecdFile;

	private int frameNumber;

	private ECDDataUnit ecdDataUnit;
	
	public ECDRecordSet(ECDFile ecdFile, long filePosition, GeminiTargetImage targetImage, int frameNumber) {
		super(targetImage.getM_TimeMills());
		this.ecdFile = ecdFile;
		this.filePosition = filePosition;
		this.timeMillis = targetImage.getM_TimeMills();
		this.targetImage = targetImage;
		this.frameNumber = frameNumber;
//		freeImageData();
	}
	
	public boolean readFully() {
		try {
			ecdFile.fullReadRecordSet(this);
		}
		catch (IOException e) {
			System.out.println("Error on full read of ECD image set: " + e.getMessage());
		}
		return true;
	}
	
	public boolean isComplete() {
		if (targetImage == null) {
			return false;
		}
		if (targetImage.getcData() == null) {
			return false;
		}
		if (pingTail == null) {
			return false;
		}
		if (acousticZoom == null) {
			return false;
		}
		return true;
	}
	
	public void freeImageData() {
		if (targetImage != null) {
			targetImage.freeCData();
		}
	}

	/**
	 * @return the targetImage
	 */
	public GeminiTargetImage getTargetImage() {
		return targetImage;
	}

	/**
	 * @param targetImage the targetImage to set
	 */
	public void setTargetImage(GeminiTargetImage targetImage) {
		this.targetImage = targetImage;
	}

	/**
	 * @return the pingTail
	 */
	public GeminiPingTail getPingTail() {
		return pingTail;
	}

	/**
	 * @param pingTail the pingTail to set
	 */
	public void setPingTail(GeminiPingTail pingTail) {
		this.pingTail = pingTail;
	}

	/**
	 * @return the acousticZoom
	 */
	public GeminiAcousticZoom getAcousticZoom() {
		return acousticZoom;
	}

	/**
	 * @param acousticZoom the acousticZoom to set
	 */
	public void setAcousticZoom(GeminiAcousticZoom acousticZoom) {
		this.acousticZoom = acousticZoom;
	}

	/**
	 * @return the filePosition
	 */
	public long getFilePosition() {
		return filePosition;
	}

	/**
	 * @return the timeMillis
	 */
	public long getTimeMillis() {
		return timeMillis;
	}

	/**
	 * @return the ecdFile
	 */
	public ECDFile getEcdFile() {
		return ecdFile;
	}

	/**
	 * @return the sonar
	 */
	public int getSonar() {
		return targetImage.getM_pid2();
	}

	@Override
	public String getSummaryString() {
		String str = String.format("<html>Gemini image data file %s", PamCalendar.formatDBDateTime(getTimeMillis()));
		if (targetImage != null) {
			str += "<br>File: " + targetImage.getEcdFile(); 
			str += "<br>Frame: " + frameNumber;
			str += String.format("<br>SoS: %6.1fm/s", targetImage.getM_sosAvg());
			str += String.format("<br>Range: %3.1fm", targetImage.getMaxRange());
//			str += "<br>Gain: " + targetImage.getM_mainGain();
		}
		str += "</html>";
		return str;
	}

	/**
	 * @return the frameNumber
	 */
	public int getFrameNumber() {
		return frameNumber;
	}

	
}
