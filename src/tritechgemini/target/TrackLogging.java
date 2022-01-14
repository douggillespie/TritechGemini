package tritechgemini.target;

import java.sql.Types;

import PamController.PamViewParameters;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamConnection;
import generalDatabase.PamSubtableDefinition;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;
import generalDatabase.SuperDetLogging;
import pamScrollSystem.ViewLoadObserver;
import tritechgemini.GeminiControl;

public class TrackLogging extends SuperDetLogging {

	private PamTableItem targetID, endTime, nTarget, classResult;
	private GeminiControl geminiControl;

	public TrackLogging(GeminiControl geminiControl, TrackDataBlock trackDataBlock) {
		super(trackDataBlock, true);
		this.geminiControl = geminiControl;
		setTableDefinition(createBaseTableDef());
	}
	
	public PamTableDefinition createBaseTableDef() {
		String name = geminiControl.getUnitName() + " Tracks";
		PamTableDefinition tableDef = new PamTableDefinition(name);

		tableDef.addTableItem(targetID = new PamTableItem("targetID", Types.BIGINT));
		tableDef.addTableItem(endTime = new PamTableItem("End Time", Types.TIMESTAMP));
		tableDef.addTableItem(nTarget = new PamTableItem("N Targets", Types.INTEGER));
		tableDef.addTableItem(classResult = new PamTableItem("ClassificationResult", Types.REAL));
		
		return tableDef;
		
	}
	

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		TrackDataUnit tdu = (TrackDataUnit) pamDataUnit;
		endTime.setValue(sqlTypes.getTimeStamp(tdu.getEndTime()));
		nTarget.setValue(tdu.getnPoints());
		targetID.setValue(tdu.getTargetID());
		classResult.setValue(tdu.getClassResult());
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		long tId = targetID.getLongValue();
		Float cls = null;
		if (classResult.getValue() != null) {
			cls = classResult.getFloatValue();
		};
		TrackDataUnit tdu = new TrackDataUnit(timeMilliseconds, databaseIndex, tId, cls);
		int n = nTarget.getIntegerValue();
		if (endTime.getValue() != null) {
			long end = sqlTypes.millisFromTimeStamp(endTime.getValue());
			tdu.setEndTime(end);
		}
		tdu.setnPoints(n);
		
		return tdu;
	}
	@Override
	public boolean loadViewData(PamConnection con, PamViewParameters pamViewParameters, ViewLoadObserver loadObserver) {
		return super.loadViewData(con, pamViewParameters, loadObserver);
	}

}
