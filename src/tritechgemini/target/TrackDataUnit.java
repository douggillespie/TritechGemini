package tritechgemini.target;

import java.awt.Point;

import javax.swing.JPopupMenu;

import PamguardMVC.PamDataUnit;
import PamguardMVC.datamenus.DataMenuParent;
import PamguardMVC.superdet.SuperDetection;

public class TrackDataUnit extends SuperDetection<Target2DataUnit> {

	private long targetID;
	
	private Float classResult = null;
	
	private int nPoints;
	
	private long endTime;
	
	private int highScore = 0;

	/**
	 * constructor for new data in real time or imported from csv files. 
	 * @param firstDataUnit
	 */
	public TrackDataUnit(Target2DataUnit firstDataUnit) {
		super(firstDataUnit.getTimeMilliseconds());
		addSubDetection(firstDataUnit);
		targetID = firstDataUnit.getTargetID();
		nPoints = 1;
		endTime = firstDataUnit.getTimeMilliseconds();
	}

	/**
	 * constructor for reading tracks back out of database. 
	 * @param timeMilliseconds
	 * @param databaseIndex
	 * @param tId
	 * @param cls
	 */
	public TrackDataUnit(long timeMilliseconds, int databaseIndex, long tId, Float cls) {
		super(timeMilliseconds);
		setDatabaseIndex(databaseIndex);
		setTargetID(tId);
		setClassResult(cls);
		nPoints = 0;
		endTime = timeMilliseconds;
	}



	@Override
	public int addSubDetection(Target2DataUnit subDetection) {
		if (subDetection.getDatabaseIndex() == 0) { // must be new
			nPoints++;
			endTime = Math.max(endTime, subDetection.getTimeMilliseconds());
		}
		int newScore = TargetType.getScore(subDetection.getTargetType());
		highScore = Math.max(highScore, newScore);
		return super.addSubDetection(subDetection);
	}

	/**
	 * @return the targetID
	 */
	public long getTargetID() {
		return targetID;
	}

	/**
	 * @param targetID the targetID to set
	 */
	public void setTargetID(long targetID) {
		this.targetID = targetID;
	}

	/**
	 * @return the classResult
	 */
	public Float getClassResult() {
		return classResult;
	}

	/**
	 * @param classResult the classResult to set
	 */
	public void setClassResult(Float classResult) {
		this.classResult = classResult;
	}

	/**
	 * @return the nPoints
	 */
	public int getnPoints() {
		return nPoints;
	}

	/**
	 * @param nPoints the nPoints to set
	 */
	public void setnPoints(int nPoints) {
		this.nPoints = nPoints;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
		setDurationInMilliseconds(endTime-getTimeMilliseconds());
	}

	@Override
	public String getSummaryString() {
		String sumStr =  super.getSummaryString();
		int fP = sumStr.indexOf("Frequency");
		if (fP > 0) {
			sumStr = sumStr.substring(0, fP);
		}
		sumStr += String.format("Track Id %d<p>Best target: %s", targetID, TargetType.getType(highScore));
		if (classResult != null) {
			sumStr += String.format("<br>Classification Score %5.3f", classResult);
		}
		return sumStr;
	}

	/**
	 * @return the highScore
	 */
	public int getHighScore() {
		return highScore;
	}


}
