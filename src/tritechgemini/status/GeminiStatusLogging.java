package tritechgemini.status;

import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;

public class GeminiStatusLogging extends SQLLogging {

	public static final int FILENAMELEN = 40;

	private GeminiStatusDataBlock geminiStatusDataBlock;
	
	private PamTableItem geminiTime, version, status, fileName, fileFrame, speedOfSound, action, actionTaken;

	public GeminiStatusLogging(GeminiStatusDataBlock geminiStatusDataBlock) {
		super(geminiStatusDataBlock);
		this.geminiStatusDataBlock = geminiStatusDataBlock;
		PamTableDefinition tableDef = new PamTableDefinition("Gemini Status", this.UPDATE_POLICY_WRITENEW);
		tableDef.addTableItem(geminiTime = new PamTableItem("Gemini Time", Types.TIMESTAMP));
		tableDef.addTableItem(version = new PamTableItem("Version", Types.SMALLINT));
		tableDef.addTableItem(status = new PamTableItem("Status", Types.SMALLINT));
		tableDef.addTableItem(fileName = new PamTableItem("File", Types.CHAR, FILENAMELEN));
		tableDef.addTableItem(fileFrame = new PamTableItem("Frame", Types.INTEGER));
		tableDef.addTableItem(speedOfSound = new PamTableItem("SoundSpeed", Types.REAL));
		tableDef.addTableItem(action = new PamTableItem("FileAction", Types.CHAR, FILENAMELEN));
		tableDef.addTableItem(actionTaken = new PamTableItem("ActionTaken", Types.CHAR, FILENAMELEN));
		setTableDefinition(tableDef);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit dataUnit) {
		GeminiStatusDataUnit gsdu = (GeminiStatusDataUnit) dataUnit;
		geminiTime.setValue(sqlTypes.getTimeStamp(gsdu.getGeminiTime()));
		version.setValue((short) gsdu.getVersion());
		status.setValue((short) gsdu.getStatus());
		fileName.setValue(gsdu.getFileName());
		fileFrame.setValue(gsdu.getFrame());
		speedOfSound.setValue(gsdu.getSpeedOfSound());
		action.setValue(gsdu.getFileAction());
		actionTaken.setValue(gsdu.getActionTaken());
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		long gTime = SQLTypes.millisFromTimeStamp(geminiTime.getValue());
		int version = this.version.getIntegerValue();
		int status = this.status.getIntegerValue();
		String fileName = this.fileName.getDeblankedStringValue();
		int frame = this.fileFrame.getIntegerValue();
		float speedOfSound = this.speedOfSound.getFloatValue();
		GeminiStatusDataUnit gsdu = new GeminiStatusDataUnit(timeMilliseconds, gTime, version, status, fileName, frame, speedOfSound);
		gsdu.setFileAction(action.getDeblankedStringValue());
		gsdu.setActionTaken(actionTaken.getDeblankedStringValue());
		return gsdu;
	}

}
