package tritechplugin.status;

import PamUtils.PamCalendar;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;

public class GeminiStatusDataUnit extends PamDataUnit {
	
	private long geminiTime;
	private int version, status;
	private String fileName;
	private int frame;
	float speedOfSound;
	private String fileAction;
	private String actionTaken;

	/**
	 * @param timeMilliseconds
	 * @param geminiTime
	 * @param version
	 * @param status
	 * @param fileName
	 * @param frame
	 * @param speedOfSound
	 */
	public GeminiStatusDataUnit(long pamguardMilliseconds, long geminiTime, int version, int status, String fileName,
			int frame, float speedOfSound) {
		super(pamguardMilliseconds);
		this.geminiTime = geminiTime;
		this.version = version;
		this.status = status;
		this.fileName = fileName;
		this.frame = frame;
		this.speedOfSound = speedOfSound;
	}

	@Override
	public String toString() {
		return String.format("PAM Time %s, Gem time %s, ver %d, Status %d, file \"%s\", frame %d, sos %3.2f", 
				PamCalendar.formatDBDateTime(getTimeMilliseconds()), PamCalendar.formatDBDateTime(geminiTime),
				version, status, fileName, frame, speedOfSound);
	}

	/**
	 * @return the geminiTime
	 */
	public long getGeminiTime() {
		return geminiTime;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the frame
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * @return the speedOfSound
	 */
	public float getSpeedOfSound() {
		return speedOfSound;
	}

	/**
	 * @return the fileAction
	 */
	public String getFileAction() {
		return fileAction;
	}

	/**
	 * @param fileAction the fileAction to set
	 */
	public void setFileAction(String fileAction) {
		this.fileAction = fileAction;
	}

	/**
	 * @return the actionTaken
	 */
	public String getActionTaken() {
		return actionTaken;
	}

	/**
	 * @param actionTaken the actionTaken to set
	 */
	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}





}
