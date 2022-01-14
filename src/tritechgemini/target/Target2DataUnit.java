package tritechgemini.target;

import java.awt.Point;

import javax.swing.JPopupMenu;

import PamUtils.LatLong;
import PamUtils.PamCalendar;
import PamUtils.time.CalendarControl;
import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.datamenus.DataMenuParent;
import tritechgemini.GeminiControl;
import tritechgemini.GeminiLocationParams;
import tritechgemini.GeminiProcess;

public class Target2DataUnit extends PamDataUnit<PamDataUnit, PamDataUnit> {
/*
 * Bad string: $PTRITAR2, 01112019, 145556.435, 2019/11/01, 14:55:55, S1, 145555040010, Probable, LD20191101_144904_IMG.ecd, 12, 0.663645, 1.223403, 0.985127, 1.389644, 0.672786, 1.210032, 0.990754, 1.374456, 0.826731, 1.301988, -0.268947, -0.078160,  181,     183,     89,         153,        0.053946, 0.316499, -0.146086, 99.335007, -0.161881, -0.078643, 161.000000, 65.000000, 4097.999512,  0.403727, 0.397710, 0.985096,      30,     158,     85,  84.828362, 35.971588, 128,     30,          104,       50,       57.036366,   15.170671, 74 
                       date	     time	                        sonar	 targetID	 Type	    ECDlogfile	                 step	 x.fl	 y.fl	   x.fr	     y.fr	   x.nl	    y.nl	   x.nr	    y.nr	   x	    y	      vx	      vy	 range.min range.max  bearing.min bearing.max dist	    radi	    ddist	 dradi	     dvec.x	     dvec.y	   area	       perim	 length	       pa.ratio	 la.ratio	 lp.ratio	 pixmin	 pixmax	 pixmed	 pixave	    pixsd	 pixrng	  pixpmin	 pixpmax	 pixpmed	 pixpave	 pixpsd	     pixprng 

 */
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
	private Long processed; // date it was processed at for classification

//	/**
//	 * constructor that will unpack the standard UDP string.
//	 * @param timeMilliseconds PAMGuard time i nmilliseconds
//	 * @param udpString UDP received string
//	 */
//	private Target2DataUnit(long timeMilliseconds, String udpString) {
//		super(timeMilliseconds);
//		dataOk = unpackUDPString(udpString);
//	}

	/**
	 * Constructor to use reading from database. Will not do any unpacking.
	 * @param timeMilliseconds
	 */
	public Target2DataUnit(long timeMilliseconds) {
		super(timeMilliseconds);
	}

	public String getSummaryString() {
		String str = "<html>";
		str += "UID: " + getUID() + "<p>";
		PamDataBlock parentDataBlock = getParentDataBlock();
		DataUnitBaseData basicData = getBasicData();
		if (parentDataBlock != null) {
			str += "<i>" + parentDataBlock.getLongDataName() + "</i><p>";
		}
//		str += PamCalendar.formatDateTime(timeMilliseconds) + "<p>";
		str += String.format("%s %s %s<p>", PamCalendar.formatDate(basicData.getTimeMilliseconds(), true),
				PamCalendar.formatTime(basicData.getTimeMilliseconds(), 3, true),
				CalendarControl.getInstance().getTZCode(true));
		if (CalendarControl.getInstance().isUTC() == false) {
			str += String.format("%s %s %s<p>", PamCalendar.formatDate(basicData.getTimeMilliseconds(), false),
					PamCalendar.formatTime(basicData.getTimeMilliseconds(), 3, false),
					"UTC");
		}
		if (sonar != null) {
			str += String.format("Sonar %s, ", sonar);
		}
		double tx = x;
		double ty = y;
		if (parentDataBlock instanceof Target2DataBlock) {
			Target2DataBlock t2DataBlock = (Target2DataBlock) parentDataBlock;
			GeminiControl geminiControl = t2DataBlock.getGeminiProcess().getGeminiControl();
			int id = 0;
			try {
				id = Integer.valueOf(sonar.substring(1))-1;
			}
			catch (NumberFormatException e) {
			}
			GeminiLocationParams geminiLoc = geminiControl.getGeminiParameters().getGeminiLocation(id);
			if (geminiLoc != null) {
				tx += geminiLoc.getOffsetX();
				ty += geminiLoc.getOffsetY();
			}
			if (geminiLoc.isFlipLeftRight()) {
				tx = -tx;
			}
		}
		str += "Type " + targetType + "<p>";
		if (logFile != null) {
			str += String.format("log file %s<p>", logFile);
		}
//		str += String.format("Range %3.1fm, Bear %d<p>", dist, bearing_min);
		double bearing = 90.-Math.atan2(y,tx)*180./Math.PI;
		double range = Math.sqrt(tx*tx + y*y);
		double head = 90.-Math.atan2(vy,vx)*180./Math.PI;
		double speed = Math.sqrt(vx*vx + vy*vy);
		str += String.format("Range %3.1f m, Bearing %3.1f%s<p>", range, bearing, LatLong.deg);
		str += String.format("Heading %3.1f%s, Speed %3.1f m/s<p>", head, LatLong.deg,  speed);
		int nSuper = getSuperDetectionsCount();
		for (int i = 0; i < nSuper; i++) {
			PamDataUnit superDet = getSuperDetection(i);
			if (superDet != null) {
				str += superDet.getSummaryString();
			}
		}
		
		return str;
	}
	/**
	 * Create data unit from a line in one of the csv files (hoping they always have the same format)
	 * @param fileLine
	 * @param timeOffset time offset to take from all lines in this file
	 * @param milliseconds per step to add back on depending on step number
	 * @return new data unit. 
	 */
	public static Target2DataUnit createFromFileLine(String fileLine, long timeOffset, long stepMilliseconds) {
		//date, time, sonar, targetID, Type, ECDlogfile, step, x.fl, y.fl, x.fr, y.fr, x.nl, y.nl, x.nr, y.nr, x, y, vx, vy,  
		//0     1     2      3         4     5           6     7     8     9     10    11    12    13    14    15 16 17  18 
		//range.min, range.max, bearing.min, bearing.max, dist, radi, ddist, dradi, dvec.x, dvec.y, area, perim, length,  pa.ratio, 
		//19         20         21           22           23    24    25     26     27      28      29    30     31       32
		//la.ratio, lp.ratio, pixmin, pixmax, pixmed, pixave, pixsd, pixrng,  pixpmin, pixpmax, pixpmed, pixpave, pixpsd, pixprng
		//33        34        35      36      37      38      39     40       41       42       43       44       45      46
		/**
		 * File data is almost the same as the UDP data, but has three extra fields at the start.
		 * These are $PTRITAR2, Date, Time but of course the date is in a different format to the date at current
		 * position 0.  
		 *  $PTRITAR2, 01112019, 145317.967, 2019/11/01, 14:53:16, S1, 145316140013, Probable, LD20191101_144904_IMG.ecd, 
		 *  14, 0.867806, 1.178108, 1.166908, 1.294858, 0.890795, 1.135268, 1.179021, 1.247772, 1.031518, 1.200203, 0.179411, 
		 *  1.005576,  159, 165, 116, 182, 0.830026, 12.274599, 0.039407, 186.069611, -0.030139, 0.016416, 411.000000, 118.000000, 
		 *  4373.962963,  0.287105, 0.161246, 0.561629, 31, 221, 65, 75.179802, 37.937489, 190,  31, 136, 47, 55.136841, 7.504272, 105
		 *  Common data starts at position 3 in CSV data and 0 in file data.   
		 */

		if (fileLine == null) {
			return null;
		}
		String[] bits = fileLine.split(",");

		long fileTime = GeminiProcess.unpackDateTime2(bits[0], bits[1]);
		
		Target2DataUnit dataUnit = new Target2DataUnit(fileTime);
		dataUnit.unpackCommonBits(fileTime, bits, 0);
		
		// now fix the UTC based on the step number
		long fixedUTC = fileTime-timeOffset + dataUnit.getStep()*stepMilliseconds;
		dataUnit.setTimeMilliseconds(fixedUTC);
		
		return dataUnit;
	}

	/**
	 * Create data unit from a line in one of the csv files (hoping they always have the same format)
	 * @param fileLine
	 * @return new data unit. 
	 */
	public static Target2DataUnit createFromUDPString(long pamguardTime, String udpString) {
		if (udpString == null) {
			return null;
		}
		String[] bits = udpString.split(",");

		long geminiDateTime1 = GeminiProcess.unpackDateTime(bits[1], bits[2]);
		Target2DataUnit dataUnit = new Target2DataUnit(pamguardTime);
		dataUnit.unpackCommonBits(geminiDateTime1, bits, 3);
		return dataUnit;
	}

	/**
	 * Unpack the UDP string. 
	 * @param udpString
	 * @return
	 */
	private boolean unpackCommonBits(long time1, String[] bits, int offset) {
		if (bits == null) {
			return false;
		}

		geminiDateTime1 = time1;
		geminiDateTime2 = GeminiProcess.unpackDateTime2(bits[0+offset], bits[1+offset]);		
		setSonar(bits[2+offset].trim());
		targetID = getLongValue(bits[3+offset]); // 3 in file
		targetType = bits[4+offset].trim(); // 4 in file
		logFile = bits[5+offset].trim(); // 5 in file
		step = getIntValue(bits[6+offset]); //6 in file. 
		x_fl = getFloatValue(bits, 7+offset); //7 in file
		y_fl = getFloatValue(bits, 8+offset); 
		x_fr = getFloatValue(bits, 9+offset);
		y_fr = getFloatValue(bits, 10+offset);
		x_nl = getFloatValue(bits, 11+offset);
		y_nl = getFloatValue(bits, 12+offset);
		x_nr = getFloatValue(bits, 13+offset);
		y_nr = getFloatValue(bits, 14+offset);
		x = getFloatValue(bits, 15+offset); //15 in file
		y = getFloatValue(bits, 16+offset);
		vx = getFloatValue(bits, 17+offset);
		vy = getFloatValue(bits, 18+offset);
		range_min = getIntValue(bits, 19+offset);
		range_max = getIntValue(bits, 20+offset);
		bearing_min = getIntValue(bits, 21+offset);
		bearing_max = getIntValue(bits, 22+offset);
		dist = getFloatValue(bits, 23+offset);
		radi = getFloatValue(bits, 24+offset);
		ddist = getFloatValue(bits, 25+offset);
		dradi = getFloatValue(bits, 26+offset);
		dvec_x = getFloatValue(bits, 27+offset);
		dvec_y = getFloatValue(bits, 28+offset); //28 in file 
		area = getFloatValue(bits, 29+offset);
		perim = getFloatValue(bits, 30+offset);
		length = getFloatValue(bits, 31+offset);
		pa_ratio = getFloatValue(bits, 32+offset);
		la_ratio = getFloatValue(bits, 33+offset);
		lp_ratio = getFloatValue(bits, 34+offset);
		pixmin = getIntValue(bits, 35+offset);
		pixmax = getIntValue(bits, 36+offset);
		pixmed = getIntValue(bits, 37+offset);
		pixave = getFloatValue(bits, 38+offset);
		pixsd = getFloatValue(bits, 39+offset); // 39 in file
		pixrng = getIntValue(bits, 40+offset);
		pixpmin = getIntValue(bits, 41+offset);
		pixpmax = getIntValue(bits, 42+offset);
		pixpmed = getIntValue(bits, 43+offset);
		pixpave = getFloatValue(bits, 44+offset);
		pixpsd = getFloatValue(bits, 45+offset);
		pixprng = getIntValue(bits, 46+offset); //46 in file. 
		
		
		return true;
	}
	
	/**
	 * Unpack the UDP string. 
	 * @param udpString
	 * @return
	 */
//	private boolean unpackUDPString(String udpString) {
//		if (udpString == null) {
//			return false;
//		}
//		String[] bits = udpString.split(",");
//
//		geminiDateTime1 = GeminiProcess.unpackDateTime(bits[1], bits[2]);
//		geminiDateTime2 = GeminiProcess.unpackDateTime2(bits[3], bits[4]);		
//		setSonar(bits[5].trim());
//		targetID = getLongValue(bits[6]); // 3 in file
//		targetType = bits[7].trim(); // 4 in file
//		logFile = bits[8].trim(); // 5 in file
//		step = getIntValue(bits[9]); //6 in file. 
//		x_fl = getFloatValue(bits, 10); //7 in file
//		y_fl = getFloatValue(bits, 11); 
//		x_fr = getFloatValue(bits, 12);
//		y_fr = getFloatValue(bits, 13);
//		x_nl = getFloatValue(bits, 14);
//		y_nl = getFloatValue(bits, 15);
//		x_nr = getFloatValue(bits, 16);
//		y_nr = getFloatValue(bits, 17);
//		x = getFloatValue(bits, 18); //15 in file
//		y = getFloatValue(bits, 19);
//		vx = getFloatValue(bits, 20);
//		vy = getFloatValue(bits, 21);
//		range_min = getIntValue(bits, 22);
//		range_max = getIntValue(bits, 23);
//		bearing_min = getIntValue(bits, 24);
//		bearing_max = getIntValue(bits, 25);
//		dist = getFloatValue(bits, 26);
//		radi = getFloatValue(bits, 27);
//		ddist = getFloatValue(bits, 28);
//		dradi = getFloatValue(bits, 29);
//		dvec_x = getFloatValue(bits, 30);
//		dvec_y = getFloatValue(bits, 31); //28 in file 
//		area = getFloatValue(bits, 32);
//		perim = getFloatValue(bits, 33);
//		length = getFloatValue(bits, 34);
//		pa_ratio = getFloatValue(bits, 35);
//		la_ratio = getFloatValue(bits, 36);
//		lp_ratio = getFloatValue(bits, 37);
//		pixmin = getIntValue(bits, 38);
//		pixmax = getIntValue(bits, 39);
//		pixmed = getIntValue(bits, 40);
//		pixave = getFloatValue(bits, 41);
//		pixsd = getFloatValue(bits, 42); // 39 in file
//		pixrng = getIntValue(bits, 43);
//		pixpmin = getIntValue(bits, 44);
//		pixpmax = getIntValue(bits, 45);
//		pixpmed = getIntValue(bits, 46);
//		pixpave = getFloatValue(bits, 47);
//		pixpsd = getFloatValue(bits, 48);
//		pixprng = getIntValue(bits, 49); //46 in file. 
//		
//		
//		return true;
//	}
	
	private float getFloatValue(String[] bits, int iBit) {
		if (iBit >= bits.length) {
			return Float.NaN;
		}
		else {
			return getFloatValue(bits[iBit]);
		}
	}
	
	private float getFloatValue(String str) {
		try {
			str = str.trim();
			return Float.valueOf(str);
		}
		catch (NumberFormatException e) {
			return Float.NaN;
		}
	}
	
	private long getLongValue(String str) {
		try {
			str = str.trim();
			return Long.valueOf(str);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}
	private int getIntValue(String[] bits, int iBit) {
		if (iBit >= bits.length) {
			return -1;
		}
		else {
			return getIntValue(bits[iBit]);
		}
	}
	private int getIntValue(String str) {
		try {
			str = str.trim();
			return Integer.valueOf(str);
		}
		catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * @return the geminiDateTime1
	 */
	public long getGeminiDateTime1() {
		return geminiDateTime1;
	}

	/**
	 * @param geminiDateTime1 the geminiDateTime1 to set
	 */
	public void setGeminiDateTime1(long geminiDateTime1) {
		this.geminiDateTime1 = geminiDateTime1;
	}

	/**
	 * @return the geminiDateTime2
	 */
	public long getGeminiDateTime2() {
		return geminiDateTime2;
	}

	/**
	 * @param geminiDateTime2 the geminiDateTime2 to set
	 */
	public void setGeminiDateTime2(long geminiDateTime2) {
		this.geminiDateTime2 = geminiDateTime2;
	}

	/**
	 * @return the sonar
	 */
	public String getSonar() {
		return sonar;
	}

	/**
	 * @param sonar the sonar to set
	 */
	public void setSonar(String sonar) {
		this.sonar = sonar;
		/*
		 *  try to find a number in the sonar data and use it to 
		 *  set the channel bitmap field. 
		 */
		if (sonar == null) return;
		
		for (int i = 0; i < sonar.length(); i++) {
			char c = sonar.charAt(i);
			if (Character.isDigit(c)) {
				int num = Character.getNumericValue(c);
				setChannelBitmap(1<<(num-1));
			}
		}
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
	 * @return the targetType
	 */
	public String getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	/**
	 * @return the logFile
	 */
	public String getLogFile() {
		return logFile;
	}

	/**
	 * @param logFile the logFile to set
	 */
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step the step to set
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * @return the x_fl
	 */
	public float getX_fl() {
		return x_fl;
	}

	/**
	 * @param x_fl the x_fl to set
	 */
	public void setX_fl(float x_fl) {
		this.x_fl = x_fl;
	}

	/**
	 * @return the y_fl
	 */
	public float getY_fl() {
		return y_fl;
	}

	/**
	 * @param y_fl the y_fl to set
	 */
	public void setY_fl(float y_fl) {
		this.y_fl = y_fl;
	}

	/**
	 * @return the x_fr
	 */
	public float getX_fr() {
		return x_fr;
	}

	/**
	 * @param x_fr the x_fr to set
	 */
	public void setX_fr(float x_fr) {
		this.x_fr = x_fr;
	}

	/**
	 * @return the y_fr
	 */
	public float getY_fr() {
		return y_fr;
	}

	/**
	 * @param y_fr the y_fr to set
	 */
	public void setY_fr(float y_fr) {
		this.y_fr = y_fr;
	}

	/**
	 * @return the x_nl
	 */
	public float getX_nl() {
		return x_nl;
	}

	/**
	 * @param x_nl the x_nl to set
	 */
	public void setX_nl(float x_nl) {
		this.x_nl = x_nl;
	}

	/**
	 * @return the y_nl
	 */
	public float getY_nl() {
		return y_nl;
	}

	/**
	 * @param y_nl the y_nl to set
	 */
	public void setY_nl(float y_nl) {
		this.y_nl = y_nl;
	}

	/**
	 * @return the x_nr
	 */
	public float getX_nr() {
		return x_nr;
	}

	/**
	 * @param x_nr the x_nr to set
	 */
	public void setX_nr(float x_nr) {
		this.x_nr = x_nr;
	}

	/**
	 * @return the y_nr
	 */
	public float getY_nr() {
		return y_nr;
	}

	/**
	 * @param y_nr the y_nr to set
	 */
	public void setY_nr(float y_nr) {
		this.y_nr = y_nr;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the vx
	 */
	public float getVx() {
		return vx;
	}

	/**
	 * @param vx the vx to set
	 */
	public void setVx(float vx) {
		this.vx = vx;
	}

	/**
	 * @return the vy
	 */
	public float getVy() {
		return vy;
	}

	/**
	 * @param vy the vy to set
	 */
	public void setVy(float vy) {
		this.vy = vy;
	}

	/**
	 * @return the range_min
	 */
	public int getRange_min() {
		return range_min;
	}

	/**
	 * @param range_min the range_min to set
	 */
	public void setRange_min(int range_min) {
		this.range_min = range_min;
	}

	/**
	 * @return the range_max
	 */
	public int getRange_max() {
		return range_max;
	}

	/**
	 * @param range_max the range_max to set
	 */
	public void setRange_max(int range_max) {
		this.range_max = range_max;
	}

	/**
	 * @return the bearing_min
	 */
	public int getBearing_min() {
		return bearing_min;
	}

	/**
	 * @param bearing_min the bearing_min to set
	 */
	public void setBearing_min(int bearing_min) {
		this.bearing_min = bearing_min;
	}

	/**
	 * @return the bearing_max
	 */
	public int getBearing_max() {
		return bearing_max;
	}

	/**
	 * @param bearing_max the bearing_max to set
	 */
	public void setBearing_max(int bearing_max) {
		this.bearing_max = bearing_max;
	}

	/**
	 * @return the dist
	 */
	public float getDist() {
		return dist;
	}

	/**
	 * @param dist the dist to set
	 */
	public void setDist(float dist) {
		this.dist = dist;
	}

	/**
	 * @return the radi
	 */
	public float getRadi() {
		return radi;
	}

	/**
	 * @param radi the radi to set
	 */
	public void setRadi(float radi) {
		this.radi = radi;
	}

	/**
	 * @return the ddist
	 */
	public float getDdist() {
		return ddist;
	}

	/**
	 * @param ddist the ddist to set
	 */
	public void setDdist(float ddist) {
		this.ddist = ddist;
	}

	/**
	 * @return the dradi
	 */
	public float getDradi() {
		return dradi;
	}

	/**
	 * @param dradi the dradi to set
	 */
	public void setDradi(float dradi) {
		this.dradi = dradi;
	}

	/**
	 * @return the dvec_x
	 */
	public float getDvec_x() {
		return dvec_x;
	}

	/**
	 * @param dvec_x the dvec_x to set
	 */
	public void setDvec_x(float dvec_x) {
		this.dvec_x = dvec_x;
	}

	/**
	 * @return the dvec_y
	 */
	public float getDvec_y() {
		return dvec_y;
	}

	/**
	 * @param dvec_y the dvec_y to set
	 */
	public void setDvec_y(float dvec_y) {
		this.dvec_y = dvec_y;
	}

	/**
	 * @return the area
	 */
	public float getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(float area) {
		this.area = area;
	}

	/**
	 * @return the perim
	 */
	public float getPerim() {
		return perim;
	}

	/**
	 * @param perim the perim to set
	 */
	public void setPerim(float perim) {
		this.perim = perim;
	}

	/**
	 * @return the length
	 */
	public float getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(float length) {
		this.length = length;
	}

	/**
	 * @return the pa_ratio
	 */
	public float getPa_ratio() {
		return pa_ratio;
	}

	/**
	 * @param pa_ratio the pa_ratio to set
	 */
	public void setPa_ratio(float pa_ratio) {
		this.pa_ratio = pa_ratio;
	}

	/**
	 * @return the la_ratio
	 */
	public float getLa_ratio() {
		return la_ratio;
	}

	/**
	 * @param la_ratio the la_ratio to set
	 */
	public void setLa_ratio(float la_ratio) {
		this.la_ratio = la_ratio;
	}

	/**
	 * @return the lp_ratio
	 */
	public float getLp_ratio() {
		return lp_ratio;
	}

	/**
	 * @param lp_ratio the lp_ratio to set
	 */
	public void setLp_ratio(float lp_ratio) {
		this.lp_ratio = lp_ratio;
	}

	/**
	 * @return the pixmin
	 */
	public int getPixmin() {
		return pixmin;
	}

	/**
	 * @param pixmin the pixmin to set
	 */
	public void setPixmin(int pixmin) {
		this.pixmin = pixmin;
	}

	/**
	 * @return the pixmax
	 */
	public int getPixmax() {
		return pixmax;
	}

	/**
	 * @param pixmax the pixmax to set
	 */
	public void setPixmax(int pixmax) {
		this.pixmax = pixmax;
	}

	/**
	 * @return the pixmed
	 */
	public int getPixmed() {
		return pixmed;
	}

	/**
	 * @param pixmed the pixmed to set
	 */
	public void setPixmed(int pixmed) {
		this.pixmed = pixmed;
	}

	/**
	 * @return the pixave
	 */
	public float getPixave() {
		return pixave;
	}

	/**
	 * @param pixave the pixave to set
	 */
	public void setPixave(float pixave) {
		this.pixave = pixave;
	}

	/**
	 * @return the pixsd
	 */
	public float getPixsd() {
		return pixsd;
	}

	/**
	 * @param pixsd the pixsd to set
	 */
	public void setPixsd(float pixsd) {
		this.pixsd = pixsd;
	}

	/**
	 * @return the pixrng
	 */
	public int getPixrng() {
		return pixrng;
	}

	/**
	 * @param pixrng the pixrng to set
	 */
	public void setPixrng(int pixrng) {
		this.pixrng = pixrng;
	}

	/**
	 * @return the pixpmin
	 */
	public int getPixpmin() {
		return pixpmin;
	}

	/**
	 * @param pixpmin the pixpmin to set
	 */
	public void setPixpmin(int pixpmin) {
		this.pixpmin = pixpmin;
	}

	/**
	 * @return the pixpmax
	 */
	public int getPixpmax() {
		return pixpmax;
	}

	/**
	 * @param pixpmax the pixpmax to set
	 */
	public void setPixpmax(int pixpmax) {
		this.pixpmax = pixpmax;
	}

	/**
	 * @return the pixpmed
	 */
	public int getPixpmed() {
		return pixpmed;
	}

	/**
	 * @param pixpmed the pixpmed to set
	 */
	public void setPixpmed(int pixpmed) {
		this.pixpmed = pixpmed;
	}

	/**
	 * @return the pixpave
	 */
	public float getPixpave() {
		return pixpave;
	}

	/**
	 * @param pixpave the pixpave to set
	 */
	public void setPixpave(float pixpave) {
		this.pixpave = pixpave;
	}

	/**
	 * @return the pixpsd
	 */
	public float getPixpsd() {
		return pixpsd;
	}

	/**
	 * @param pixpsd the pixpsd to set
	 */
	public void setPixpsd(float pixpsd) {
		this.pixpsd = pixpsd;
	}

	/**
	 * @return the pixprng
	 */
	public int getPixprng() {
		return pixprng;
	}

	/**
	 * @param pixprng the pixprng to set
	 */
	public void setPixprng(int pixprng) {
		this.pixprng = pixprng;
	}

	/**
	 * @return the dataOk
	 */
	public boolean isDataOk() {
		return dataOk;
	}

	/**
	 * @param dataOk the dataOk to set
	 */
	public void setDataOk(boolean dataOk) {
		this.dataOk = dataOk;
	}

	/**
	 * @return the processed
	 */
	public Long getProcessed() {
		return processed;
	}

	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(Long processed) {
		this.processed = processed;
	}

	@Override
	public JPopupMenu getDataUnitPopupMenu(DataMenuParent menuParent, Point mousePosition) {
		/*
		 * for now, annotations are associated with the track, not the target so pass this through 
		 * to the target super detection which should have annotations. 
		 */
		TrackDataUnit track = (TrackDataUnit) getSuperDetection(TrackDataUnit.class);
		if (track != null) {
			return track.getDataUnitPopupMenu(menuParent, mousePosition);
		}
		else {
			return super.getDataUnitPopupMenu(menuParent, mousePosition); // this will be null but call it anyway
		}
	}

}
