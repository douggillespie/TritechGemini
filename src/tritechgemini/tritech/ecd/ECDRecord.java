package tritechgemini.tritech.ecd;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;

import tritechgemini.tritech.GeminiRecord;

public abstract class ECDRecord  {


	public static final int TYPE_SENSOR_RECORD = 1;
	public static final int TYPE_TARGET_RECORD = 2;
	public static final int TYPE_TARGET_IMAGE_RECORD = 3;
	public static final int TYPE_PING_TAIL_RECORD = 4;
	public static final int TYPE_ACOUSTIC_ZOOM_RECORD = 5;

	public static final int VER_SENSOR_RECORD = 0xEFEF;
	public static final int VER_TARGET_RECORD = 0xDFDF;
	public static final int VER_TARGET_IMAGE_RECORD = 0xCFCF;
	public static final int VER_PING_TAIL_RECORD = 0xBFBF;
	public static final int VER_ACOUSTIC_ZOOM_RECORD = 0xAFAF;

	public static final int END_TAG = 0xDEDE;
	
	public static final int HALF_END_TAG = 0xDE;

	private int recordType;
	
	private int recordVersion;
	
	private int endTag;
	
	private File ecdFile;

	public ECDRecord(File ecdFile, int recordType, int recordVersion) {
		super();
		this.ecdFile = ecdFile;
		this.recordType = recordType;
		this.recordVersion = recordVersion;
	}

	/**
	 * @return the recordType
	 */
	public int getRecordType() {
		return recordType;
	}

	/**
	 * @return the recordVersion
	 */
	public int getRecordVersion() {
		return recordVersion;
	}

	/**
	 * @return the ecdFile
	 */
	public File getEcdFile() {
		return ecdFile;
	}
	
	public abstract boolean readDataFile(DataInput dis) throws IOException;

	public int getEndTag() {
		return endTag;
	}

	/**
	 * Set the file end tag and return true if it's correct. 
	 * @param endTag
	 * @return
	 */
	public boolean setEndTag(int endTag) {
		this.endTag = endTag;
		return endTag == END_TAG;
	}

	/**
	 * check for consistency between record type and version numbers
	 * @param type record type
	 * @param ver record version
	 * @return OK if recognised pair of values
	 */
	public static boolean checkTypeVersion(int type, int ver) {
		switch (type) {
		case TYPE_SENSOR_RECORD:
			return ver == VER_SENSOR_RECORD;
		case TYPE_TARGET_RECORD:
			return ver == VER_TARGET_RECORD;
		case TYPE_TARGET_IMAGE_RECORD:
			return ver == VER_TARGET_IMAGE_RECORD;
		case TYPE_PING_TAIL_RECORD:
			return ver == VER_PING_TAIL_RECORD;
		case TYPE_ACOUSTIC_ZOOM_RECORD:
			return ver == VER_ACOUSTIC_ZOOM_RECORD;
		}
		return false;
	}
	
	public int moveToEnd(DataInput dis) throws IOException { 
		int prevByte;
		int thisByte = 0;
		int count = 0;
		while (true) {
			count++;
			prevByte = thisByte;
			thisByte = dis.readUnsignedByte();
			if (prevByte == HALF_END_TAG && thisByte == HALF_END_TAG) {
				break;
			}
		}
		return count;
	}
	
}
