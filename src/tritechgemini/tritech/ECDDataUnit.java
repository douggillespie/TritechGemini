package tritechgemini.tritech;

import java.util.ArrayList;

import PamguardMVC.PamDataUnit;
import tritechgemini.tritech.ecd.ECDFile;
import tritechgemini.tritech.ecd.ECDRecordSet;

public class ECDDataUnit extends PamDataUnit {

	private ECDFile ecdFile;

	public ECDDataUnit(long timeMilliseconds, ECDFile ecdFile) {
		super(timeMilliseconds);
		this.ecdFile = ecdFile;
	}
	/**
	 * Used in viewer to get a single image as close in time as possible to the 
	 * requested time
	 * @param timeMillis
	 * @param iSonar sonar id (indexed from 1)
	 * @return a record set (or null)
	 */
	public ECDRecordSet findRecordSet(long timeMillis, int iSonar) {
		if (ecdFile == null) {
			return null;
		}
		ArrayList<ECDRecordSet> ecdRecs = ecdFile.getEmptyECDRecords();
		for (ECDRecordSet ecdRecordSet : ecdRecs) {
			if (ecdRecordSet.getTimeMillis() > timeMillis) { //   in summer ! - (3600000L)
				if (ecdRecordSet.getSonar() == iSonar) {
					return ecdRecordSet;
				}
			}
		}
		return null;
	}

	public void clearRecordImages() {
		ArrayList<ECDRecordSet> ecdRecs = ecdFile.getEmptyECDRecords();
		for (ECDRecordSet ecdRecordSet : ecdRecs) {
			ecdRecordSet.freeImageData();
		}
		
	}

}
