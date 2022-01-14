package tritechplugin.target;

import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamSubtableDefinition;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;
import tritechplugin.GeminiControl;
import tritechplugin.status.GeminiStatusLogging;

public class Target2Logging extends SQLLogging {

	private Target2DataBlock target2DataBlock;
	private PamTableItem geminiDateTime1, geminiDateTime2;
	private PamTableItem sonar;
	private PamTableItem targetID;
	private PamTableItem targetType;
	private PamTableItem logFile;
	private PamTableItem step;
	private PamTableItem x_fl, y_fl, x_fr,	y_fr, x_nl, y_nl, x_nr, y_nr;
	private PamTableItem x, y, vx, vy;
	private PamTableItem range_min, range_max, bearing_min, bearing_max;
	private PamTableItem dist, radi;
	private PamTableItem ddist, dradi,	dvec_x, dvec_y;
	private PamTableItem area, perim;
	private PamTableItem length, pa_ratio, la_ratio, lp_ratio;
	private PamTableItem pixmin, pixmax, pixmed;
	private PamTableItem pixave, pixsd;
	private PamTableItem pixrng;
	private PamTableItem pixpmin, pixpmax, pixpmed;
	private PamTableItem pixpave, pixpsd;
	private PamTableItem pixprng;
	private PamTableItem dataOk; 
	private PamTableItem processed;
	private GeminiControl geminiControl;

	public Target2Logging(GeminiControl geminiControl, Target2DataBlock target2DataBlock) {
		super(target2DataBlock);
		this.geminiControl = geminiControl;
		this.target2DataBlock = target2DataBlock;
		String tableName = geminiControl.getUnitName() + " Targets2";
		PamTableDefinition tableDef = new PamSubtableDefinition(tableName);
		/*
		 * 
		 private long geminiDateTime1, geminiDateTime2;
	private String sonar;
	private long targetID;
	private String targetType;
	private String logFile;
	private int step;
	private float x_fl, y_fl, x_fr,	y_fr, x_nl, y_nl, x_nr, y_nr;
	private float x, y, vx, vy;
	private int range_min, range_max, bearing_min, bearing_max;
	private float dist, radi;
	private float ddist, dradi,	dvec_x, dvec_y;
	private float area, perim;
	private float length, pa_ratio, la_ratio, lp_ratio;
	private int pixmin, pixmax, pixmed;
	private float pixave, pixsd;
	private int pixrng;
	private int pixpmin, pixpmax, pixpmed;
	private float pixpave, pixpsd;
	private int pixprng;
	private boolean dataOk; 
		 */
		tableDef.addTableItem(geminiDateTime1 = new PamTableItem("geminiDateTime1", Types.TIMESTAMP));
		tableDef.addTableItem(geminiDateTime2 = new PamTableItem("geminiDateTime2", Types.TIMESTAMP));
		tableDef.addTableItem(sonar = new PamTableItem("sonar", Types.CHAR, 5));
		tableDef.addTableItem(targetID = new PamTableItem("targetID", Types.BIGINT));
		tableDef.addTableItem(targetType = new PamTableItem("targetType", Types.CHAR, 20));
		tableDef.addTableItem(logFile = new PamTableItem("logFile", Types.CHAR, GeminiStatusLogging.FILENAMELEN));
		tableDef.addTableItem(step = new PamTableItem("step", Types.INTEGER));
		tableDef.addTableItem(x_fl = new PamTableItem("x_fl", Types.REAL));
		tableDef.addTableItem(y_fl = new PamTableItem("y_fl", Types.REAL));
		tableDef.addTableItem(x_fr = new PamTableItem("x_fr", Types.REAL));
		tableDef.addTableItem(y_fr = new PamTableItem("y_fr", Types.REAL));
		tableDef.addTableItem(x_nl = new PamTableItem("x_nl", Types.REAL));
		tableDef.addTableItem(y_nl = new PamTableItem("y_nl", Types.REAL));
		tableDef.addTableItem(x_nr = new PamTableItem("x_nr", Types.REAL));
		tableDef.addTableItem(y_nr = new PamTableItem("y_nr", Types.REAL));
		tableDef.addTableItem(x = new PamTableItem("x", Types.REAL));
		tableDef.addTableItem(y = new PamTableItem("y", Types.REAL));
		tableDef.addTableItem(vx = new PamTableItem("vx", Types.REAL));
		tableDef.addTableItem(vy = new PamTableItem("vy", Types.REAL));
		tableDef.addTableItem(range_min = new PamTableItem("range_min", Types.INTEGER));
		tableDef.addTableItem(range_max = new PamTableItem("range_max", Types.INTEGER));
		tableDef.addTableItem(bearing_min = new PamTableItem("bearing_min", Types.INTEGER));
		tableDef.addTableItem(bearing_max = new PamTableItem("bearing_max", Types.INTEGER));
		tableDef.addTableItem(dist = new PamTableItem("dist", Types.REAL));
		tableDef.addTableItem(radi = new PamTableItem("radi", Types.REAL));
		tableDef.addTableItem(ddist = new PamTableItem("ddist", Types.REAL));
		tableDef.addTableItem(dradi = new PamTableItem("dradi", Types.REAL));
		tableDef.addTableItem(dvec_x = new PamTableItem("dvec_x", Types.REAL));
		tableDef.addTableItem(dvec_y = new PamTableItem("dvec_y", Types.REAL));
		tableDef.addTableItem(area = new PamTableItem("area", Types.REAL));
		tableDef.addTableItem(perim = new PamTableItem("perim", Types.REAL));
		tableDef.addTableItem(length = new PamTableItem("length", Types.REAL));
		tableDef.addTableItem(pa_ratio = new PamTableItem("pa_ratio", Types.REAL));
		tableDef.addTableItem(la_ratio = new PamTableItem("la_ratio", Types.REAL));
		tableDef.addTableItem(lp_ratio = new PamTableItem("lp_ratio", Types.REAL));
		tableDef.addTableItem(pixmin = new PamTableItem("pixmin", Types.INTEGER));
		tableDef.addTableItem(pixmax = new PamTableItem("pixmax", Types.INTEGER));
		tableDef.addTableItem(pixmed = new PamTableItem("pixmed", Types.INTEGER));
		tableDef.addTableItem(pixave = new PamTableItem("pixave", Types.REAL));
		tableDef.addTableItem(pixsd = new PamTableItem("pixsd", Types.REAL));
		tableDef.addTableItem(pixrng = new PamTableItem("pixrng", Types.INTEGER));
		tableDef.addTableItem(pixpmin = new PamTableItem("pixpmin", Types.INTEGER));
		tableDef.addTableItem(pixpmax = new PamTableItem("pixpmax", Types.INTEGER));
		tableDef.addTableItem(pixpmed = new PamTableItem("pixpmed", Types.INTEGER));
		tableDef.addTableItem(pixpave = new PamTableItem("pixpave", Types.REAL));
		tableDef.addTableItem(pixpsd = new PamTableItem("pixpsd", Types.REAL));
		tableDef.addTableItem(pixprng = new PamTableItem("pixprng", Types.INTEGER));
		tableDef.addTableItem(dataOk = new PamTableItem("dataOk", Types.BIT));
		tableDef.addTableItem(processed = new PamTableItem("ProcessedDate", Types.TIMESTAMP));

		setTableDefinition(tableDef);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit dataUnit) {
		Target2DataUnit tdu = (Target2DataUnit) dataUnit;

		//		 private long geminiDateTime1, geminiDateTime2;
		geminiDateTime1.setValue(sqlTypes.getTimeStamp(tdu.getGeminiDateTime1()));
		geminiDateTime2.setValue(sqlTypes.getTimeStamp(tdu.getGeminiDateTime2()));
		//		private String sonar;
		//		private long targetID;
		//		private String targetType;
		//		private String logFile;
		//		private int step;
		sonar.setValue(tdu.getSonar());
		targetID.setValue(tdu.getTargetID());
		targetType.setValue(tdu.getTargetType());
		logFile.setValue(tdu.getLogFile());
		step.setValue(tdu.getStep());

		//		private float x_fl, y_fl, x_fr,	y_fr, x_nl, y_nl, x_nr, y_nr;
		x_fl.setValue(tdu.getX_fl());
		y_fl.setValue(tdu.getY_fl());
		x_fr.setValue(tdu.getX_fr());
		y_fr.setValue(tdu.getY_fr());
		x_nl.setValue(tdu.getX_nl());
		y_nl.setValue(tdu.getY_nl());
		x_nr.setValue(tdu.getX_nr());
		y_nr.setValue(tdu.getY_nr());

		//		private float x, y, vx, vy;
		x.setValue(tdu.getX());
		y.setValue(tdu.getY());
		vx.setValue(tdu.getVx());
		vy.setValue(tdu.getVy());
		//		private int range_min, range_max, bearing_min, bearing_max;
		range_min.setValue(tdu.getRange_min());
		range_max.setValue(tdu.getRange_max());
		bearing_min.setValue(tdu.getBearing_min());
		bearing_max.setValue(tdu.getBearing_max());
		//		private float dist, radi;
		//		private float ddist, dradi,	dvec_x, dvec_y;
		dist.setValue(tdu.getDist());
		radi.setValue(tdu.getRadi());
		ddist.setValue(tdu.getDdist());
		dradi.setValue(tdu.getDradi());
		dvec_x.setValue(tdu.getDvec_x());
		dvec_y.setValue(tdu.getDvec_y());
		//		private float area, perim;
		area.setValue(tdu.getArea());
		perim.setValue(tdu.getPerim());
		//		private float length, pa_ratio, la_ratio, lp_ratio;
		length.setValue(tdu.getLength());
		pa_ratio.setValue(tdu.getPa_ratio());
		la_ratio.setValue(tdu.getLa_ratio());
		lp_ratio.setValue(tdu.getLp_ratio());
		//		private int pixmin, pixmax, pixmed;
		//		private float pixave, pixsd;
		pixmin.setValue(tdu.getPixmin());
		pixmax.setValue(tdu.getPixmax());
		pixmed.setValue(tdu.getPixmed());
		pixave.setValue(tdu.getPixave());
		pixsd.setValue(tdu.getPixsd());
		//		private int pixrng;
		//		private int pixpmin, pixpmax, pixpmed;
		//		private float pixpave, pixpsd;
		pixrng.setValue(tdu.getPixrng());
		pixpmin.setValue(tdu.getPixpmin());
		pixpmax.setValue(tdu.getPixpmax());
		pixpmed.setValue(tdu.getPixpmed());
		pixpave.setValue(tdu.getPixpave());
		pixpsd.setValue(tdu.getPixpsd());

		//		private int pixprng;
		pixprng.setValue(tdu.getPixprng());
		dataOk.setValue(tdu.isDataOk());
		
		if (tdu.getProcessed() != null) {
			processed.setValue(sqlTypes.getTimeStamp(tdu.getProcessed()));
		}
		else {
			processed.setValue(null);
		}
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		Target2DataUnit tdu = new Target2DataUnit(timeMilliseconds);
		
		try {
			tdu.setGeminiDateTime1(SQLTypes.millisFromTimeStamp(geminiDateTime1.getValue()));
			tdu.setGeminiDateTime2(SQLTypes.millisFromTimeStamp(geminiDateTime2.getValue()));
			tdu.setSonar(sonar.getDeblankedStringValue());
			tdu.setTargetID(targetID.getLongValue());
			tdu.setTargetType(targetType.getDeblankedStringValue());
			tdu.setLogFile(logFile.getDeblankedStringValue());
			tdu.setStep(step.getIntegerValue());

			tdu.setX_fl(x_fl.getFloatValue());
			tdu.setY_fl(y_fl.getFloatValue());
			tdu.setX_fr(x_fr.getFloatValue());
			tdu.setY_fr(y_fr.getFloatValue());
			tdu.setX_nl(x_nl.getFloatValue());
			tdu.setY_nl(y_nl.getFloatValue());
			tdu.setX_nr(x_nr.getFloatValue());
			tdu.setY_nr(y_nr.getFloatValue());

			//		private float x, y, vx, vy;
			tdu.setX(x.getFloatValue());
			tdu.setY(y.getFloatValue());
			tdu.setVx(vx.getFloatValue());
			tdu.setVy(vy.getFloatValue());
			//		private int range_min, range_max, bearing_min, bearing_max;
			tdu.setRange_min(range_min.getIntegerValue());
			tdu.setRange_max(range_max.getIntegerValue());
			tdu.setBearing_min(bearing_min.getIntegerValue());
			tdu.setBearing_max(bearing_max.getIntegerValue());
			//		private float dist, radi;
			//		private float ddist, dradi,	dvec_x, dvec_y;
			tdu.setDist(dist.getFloatValue());
			tdu.setRadi(radi.getFloatValue());
			tdu.setDdist(ddist.getFloatValue());
			tdu.setDradi(dradi.getFloatValue());
			tdu.setDvec_x(dvec_x.getFloatValue());
			tdu.setDvec_y(dvec_y.getFloatValue());
			//		private float area, perim;
			tdu.setArea(area.getFloatValue());
			tdu.setPerim(perim.getFloatValue());
			//		private float length, pa_ratio, la_ratio, lp_ratio;
			tdu.setLength(length.getFloatValue());
			tdu.setPa_ratio(pa_ratio.getFloatValue());
			tdu.setLa_ratio(la_ratio.getFloatValue());
			tdu.setLp_ratio(lp_ratio.getFloatValue());
			//		private int pixmin, pixmax, pixmed;
			//		private float pixave, pixsd;
			tdu.setPixmin(pixmin.getIntegerValue());
			tdu.setPixmax(pixmax.getIntegerValue());
			tdu.setPixmed(pixmed.getIntegerValue());
			tdu.setPixave(pixave.getFloatValue());
			tdu.setPixsd(pixsd.getFloatValue());
			//		private int pixrng;
			//		private int pixpmin, pixpmax, pixpmed;
			//		private float pixpave, pixpsd;
			tdu.setPixrng(pixrng.getIntegerValue());
			tdu.setPixpmin(pixpmin.getIntegerValue());
			tdu.setPixpmax(pixpmax.getIntegerValue());
			tdu.setPixpmed(pixpmed.getIntegerValue());
			tdu.setPixpave(pixpave.getFloatValue());
			tdu.setPixpsd(pixpsd.getFloatValue());

			//		private int pixprng;
			tdu.setPixprng(pixprng.getIntegerValue());
			tdu.setDataOk(dataOk.getIntegerValue() != 0 ? true : false);
			
			Object proc = processed.getValue();
			if (proc != null) {
				tdu.setProcessed(SQLTypes.millisFromTimeStamp(proc));
			}
		}
		catch (Exception e) {
			System.out.println("Exception unpacking Gemini Traget2 data: " + e.getMessage());
		}
		return tdu;
	}


}
