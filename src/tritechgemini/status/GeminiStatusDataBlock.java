package tritechgemini.status;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamProcess;
import tritechgemini.GeminiControl;
import tritechgemini.GeminiProcess;

public class GeminiStatusDataBlock extends PamDataBlock<GeminiStatusDataUnit> {

	private GeminiControl geminiControl;

	public GeminiStatusDataBlock(GeminiControl geminiControl, PamProcess geminiProcess) {
		super(GeminiStatusDataUnit.class, "Gemini Status", geminiProcess, 0);
		this.geminiControl = geminiControl;
	}

	private GeminiStatusDataUnit lastLoggedUnit = null;

	@Override
	public boolean getShouldLog(PamDataUnit pamDataUnit) {
		if (super.getShouldLog(pamDataUnit) == false) {
			return false;
		};
		/*
		 * log it if the interval has passed or the status has changes. 
		 */
		GeminiStatusDataUnit thisUnit = (GeminiStatusDataUnit) pamDataUnit;
		boolean shouldLog = false;
		if (lastLoggedUnit == null) {
			shouldLog = true;
		}
		else if (thisUnit.getTimeMilliseconds() - lastLoggedUnit.getTimeMilliseconds() >= 
			geminiControl.getGeminiParameters().statusStorageIntervalS * 1000) {
			shouldLog = true;
		}
		else if (isChange(thisUnit,lastLoggedUnit)) {
			shouldLog = true;
		}
		if (shouldLog) {
			lastLoggedUnit = thisUnit;
		}
		return shouldLog;
	}
	
	/**
	 * See if the status has changed = a status change in itself of a file name change. 
	 * @param unit1 
	 * @param unit2
	 * @return true if there is a change. 
	 */
	private boolean isChange(GeminiStatusDataUnit unit1, GeminiStatusDataUnit unit2) {
		if (unit1 == unit2) {
			return false; // hits here if both null or both the same reference
		}
		if (unit1 == null || unit2 == null) {
			return true;
		}
		// if here, both must be not null
		if (unit1.getStatus() != unit2.getStatus()) {
			return true;
		}
		String file1 = unit1.getFileName();
		String file2 = unit2.getFileName();
		if (file1 == null && file2 == null) {
			return false;
		}
		if (file1 == null || file2 == null) {
			return true;
		}
		return (file1.equals(file2) == false);
		
	}

}
