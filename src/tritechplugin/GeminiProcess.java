package tritechplugin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import PamController.PamController;
import PamUtils.PamCalendar;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamProcess;
import generalDatabase.DBControlUnit;
import pamScrollSystem.AbstractPamScroller;
import pamScrollSystem.AbstractScrollManager;
import qa.database.QASubTableLogging;
import tritechgemini.annotation.TrackAnnotationHandler;
import tritechgemini.dataselect.ECDDataSelectCreator;
import tritechgemini.status.GeminiStatusDataBlock;
import tritechgemini.status.GeminiStatusDataUnit;
import tritechgemini.status.GeminiStatusLogging;
import tritechgemini.swing.ECDOverlayGraphics;
import tritechgemini.swing.ECDSymbolManager;
import tritechgemini.swing.GeminiSymbolManager;
import tritechgemini.swing.Target2Graphics;
import tritechgemini.target.Target2DataBlock;
import tritechgemini.target.Target2DataUnit;
import tritechgemini.target.Target2Logging;
import tritechgemini.target.TrackDataBlock;
import tritechgemini.target.TrackDataUnit;
import tritechgemini.target.TrackLogging;
import tritechgemini.tritech.ECDDataBlock;

public class GeminiProcess extends PamProcess {

	private GeminiControl geminiControl;
	
//	private GeminiDetectionBlock geminiDetectionBlock;

	private Thread rxThread;
	
	private UDPThread udpThread;
	
	private GeminiStatusDataBlock geminiStatusDataBlock;
		
	/*
	 * See https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
	 */
	private static String[] dateFormats = {"ddMMyyyy_HHmmss.SSS", "ddMMyyyy_HHmmss"};
	private static String[] dateFormats2 = {"yyyy/MM/dd HH:mm:ss"};
	/*
	 * Bad string: $PTRITAR2, 01112019, 145317.437, 2019/11/01, 14:53:16, S1, 145316090013, Probable, LD20191101_144904_IMG.ecd, 20, 0.695967, 1.058081, 1.182440, 1.290411, 0.823435, 0.890336, 1.232785, 1.085834, 0.820399, 0.637576, 0.174564, 0.942181,  115, 147, 0, 92, 1.583212, -179.065445, -0.033429, -347.475311, 0.022629, 0.011805, 1014.000000, 742.000000, 8961.385607,  0.731755, 0.096062, 0.131275, 30, 233, 61, 78.535568, 53.550953, 203,  30, 142, 41, 47.355999, 14.979986, 112 
Bad string: $PTRITAR2, 01112019, 145317.967, 2019/11/01, 14:53:16, S1, 145316140013, Probable, LD20191101_144904_IMG.ecd, 14, 0.867806, 1.178108, 1.166908, 1.294858, 0.890795, 1.135268, 1.179021, 1.247772, 1.031518, 1.200203, 0.179411, 1.005576,  159, 165, 116, 182, 0.830026, 12.274599, 0.039407, 186.069611, -0.030139, 0.016416, 411.000000, 118.000000, 4373.962963,  0.287105, 0.161246, 0.561629, 31, 221, 65, 75.179802, 37.937489, 190,  31, 136, 47, 55.136841, 7.504272, 105 
Bad string: $PTRITAR2, 01112019, 145317.987, 2019/11/01, 14:53:16, S1, 145316140013, Probable, LD20191101_144904_IMG.ecd, 14, 0.867806, 1.178108, 1.166908, 1.294858, 0.890795, 1.135268, 1.179021, 1.247772, 0.931504, 0.857817, -0.397400, -0.590375,  159, 165, 116, 182, 0.830026, 12.274599, 0.039407, 186.069611, -0.030139, 0.016416, 411.000000, 118.000000, 4373.962963,  0.287105, 0.161246, 0.561629, 31, 221, 65, 75.179802, 37.937489, 190,  31, 136, 47, 55.136841, 7.504272, 105 
Bad string: $PTRITAR2, 01112019, 145556.415, 2019/11/01, 14:55:55, S1, 145555040010, Probable, LD20191101_144904_IMG.ecd, 11, 0.723923, 1.262501, 1.035358, 1.407236, 0.732405, 1.248703, 1.040436, 1.391856, 0.882260, 1.329233, 0.000298, 0.053946,  181, 183, 101, 163, 0.200033, -99.018501, 0.000000, 0.000000, 0.053900, 0.027380, 169.000000, 44.000000, 3845.999480,  0.260355, 0.367055, 1.409824, 30, 145, 65, 69.625000, 35.411716, 115,  30, 86, 52, 51.657143, 10.339548, 56 
Bad string: $PTRITAR2, 01112019, 145556.435, 2019/11/01, 14:55:55, S1, 145555040010, Probable, LD20191101_144904_IMG.ecd, 12, 0.663645, 1.223403, 0.985127, 1.389644, 0.672786, 1.210032, 0.990754, 1.374456, 0.826731, 1.301988, -0.268947, -0.078160,  181, 183, 89, 153, 0.053946, 0.316499, -0.146086, 99.335007, -0.161881, -0.078643, 161.000000, 65.000000, 4097.999512,  0.403727, 0.397710, 0.985096, 30, 158, 85, 84.828362, 35.971588, 128,  30, 104, 50, 57.036366, 15.170671, 74 
                       date	     time	                        sonar	 targetID	 Type	    ECDlogfile	             step	 x.fl	 y.fl	   x.fr	     y.fr	   x.nl	    y.nl	   x.nr	    y.nr	   x	    y	      vx	      vy	   range.min	 range.max	 bearing.min	 bearing.max	 dist	 radi	 ddist	 dradi	 dvec.x	 dvec.y	 area	 perim	 length	  pa.ratio	 la.ratio	 lp.ratio	 pixmin	 pixmax	 pixmed	 pixave	 pixsd	 pixrng	  pixpmin	 pixpmax	 pixpmed	 pixpave	 pixpsd	 pixprng 

	 */
//	String exPTRITAR2 = "$PTRITAR2, 01112019, 145556.435, 2019/11/01, 14:55:55, S1, 145555040010, Probable, LD20191101_144904_IMG.ecd, 12, 0.663645, 1.223403, 0.985127, 1.389644, 0.672786, 1.210032, 0.990754, 1.374456, 0.826731, 1.301988, -0.268947, -0.078160,  181, 183, 89, 153, 0.053946, 0.316499, -0.146086, 99.335007, -0.161881, -0.078643, 161.000000, 65.000000, 4097.999512,  0.403727, 0.397710, 0.985096, 30, 158, 85, 84.828362, 35.971588, 128,  30, 104, 50, 57.036366, 15.170671, 74";
//
//	private byte[] sample =	{0, 1, 89, 0, 0, 0, 5, 1, -98, 12, 0, 0, -16, 85, 0, 0, 
//			36, 80, 84, 82, 73, 83, 84, 65, 44, 48, 44, 49, 44, 50, 56, 49, 48, 50, 48, 
//			49, 57, 44, 49, 55, 52, 50, 51, 53, 46, 56, 52, 50, 44, 76, 68, 50, 48, 49, 
//			57, 49, 48, 50, 56, 95, 49, 55, 52, 50, 50, 48, 95, 73, 77, 71, 46, 101, 99,
//			100, 44, 54, 51, 50, 44, 32, 49, 52, 56, 51, 46, 52, 48, 42, 53, 52, 13, 10, 0};

	private Target2DataBlock target2DataBlock; 
	
	private TrackDataBlock trackDataBlock;
	
	private TrackDataBlock developingTracks;

	private TrackLogging trackLogging;

	private Target2Logging target2Logging;

	private TrackAnnotationHandler annotationHandler;

	private ECDDataBlock ecdDataBlock;
	
	public GeminiProcess(GeminiControl geminiControl) {
		super(geminiControl, null);
		this.geminiControl = geminiControl;
		
		/**
		 * Test code without Gemini present 
		 */
//		GeminiPacket gp = getPacket(sample);
//		processGeminiData(gp);
		
//		new Target2DataUnit(System.currentTimeMillis(), exPTRITAR2);
		if (geminiControl.isViewer()) {
			ecdDataBlock = new ECDDataBlock(this);
			addOutputDataBlock(ecdDataBlock);
			ecdDataBlock.setDataSelectCreator(new ECDDataSelectCreator(geminiControl, ecdDataBlock));
			ecdDataBlock.setPamSymbolManager(new ECDSymbolManager(geminiControl, ecdDataBlock));
			ecdDataBlock.setOverlayDraw(new ECDOverlayGraphics(geminiControl, ecdDataBlock));
		}
		
		geminiStatusDataBlock = new GeminiStatusDataBlock(geminiControl, this);
		addOutputDataBlock(geminiStatusDataBlock);
		geminiStatusDataBlock.SetLogging(new GeminiStatusLogging(geminiStatusDataBlock));
		
		target2DataBlock = new Target2DataBlock(this);
		addOutputDataBlock(target2DataBlock);
		target2Logging = new Target2Logging(geminiControl, target2DataBlock);
		if (geminiControl.isViewer()) {
			target2DataBlock.SetLogging(target2Logging);
		}
		target2DataBlock.setOverlayDraw(new Target2Graphics(geminiControl));
		target2DataBlock.setPamSymbolManager(new GeminiSymbolManager(target2DataBlock, Target2Graphics.defaultSymbol.getSymbolData()));
		
		trackDataBlock = new TrackDataBlock(this);
		addOutputDataBlock(trackDataBlock);
		trackLogging = new TrackLogging(geminiControl, trackDataBlock);
		trackDataBlock.SetLogging(trackLogging);
		trackLogging.setSubLogging(target2Logging);
		
		developingTracks = new TrackDataBlock(this);
		developingTracks.setNaturalLifetime(30);
		
		annotationHandler = new TrackAnnotationHandler(geminiControl, trackDataBlock);
		trackDataBlock.setAnnotationHandler(annotationHandler);
		
//		AbstractScrollManager.getScrollManager().addToSpecialDatablock(trackDataBlock, 60000L, 0);
	}

	public TrackAnnotationHandler getAnnotationHandler() {
		return annotationHandler;
	}

	public void setAnnotationHandler(TrackAnnotationHandler annotationHandler) {
		this.annotationHandler = annotationHandler;
	}

	@Override
	public void notifyModelChanged(int changeType) {
		super.notifyModelChanged(changeType);
		switch (changeType) {
		case PamController.INITIALIZATION_COMPLETE:
			annotationHandler.loadAnnotationChoices();
			sortSQLLogging();
			break;
		}
	}

	/**
	 * Check all the SQL Logging additions are set up correctly. 
	 */
	protected void sortSQLLogging() {
		trackLogging.setTableDefinition(trackLogging.createBaseTableDef());
		if (annotationHandler.addAnnotationSqlAddons(trackLogging) > 0) {
			// will have to recheck the table in the database. 
			DBControlUnit dbc = DBControlUnit.findDatabaseControl();
			if (dbc != null) {
				dbc.getDbProcess().checkTable(trackLogging.getTableDefinition());
			}
		}
	}
	
	@Override 
	public void pamStart() {
//		target2DataBlock.addPamData(new Target2DataUnit(System.currentTimeMillis(), exPTRITAR2));
	}

	@Override
	public void pamStop() {
		
	}
	private class UDPThread implements Runnable {
		
		public volatile boolean keepgoing = true;
		
		@Override
		public void run() {
			acquireUDPData();
		}

		public void acquireUDPData() {
			DatagramSocket socket = null;
			InetAddress host = null; 
			try {
				host = InetAddress.getByName("127.0.0.1");
				int port = geminiControl.getGeminiParameters().rxUDPPort;
				socket = new DatagramSocket(port, host);
				System.out.println("Opened socket on port " + port);
				socket.setSoTimeout(5000);
			} catch (SocketException e) {
				setWarning(2, "Unable to open Gemini comms: " + e.getMessage());
				return;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] buffer = new byte[8192];
			DatagramPacket rxDataGram = new DatagramPacket(buffer, buffer.length);
			while (keepgoing) {
				try {
					socket.receive(rxDataGram);
				} catch (SocketTimeoutException e) {
//					e.printStackTrace();
					setWarning(1, "No Gemini data sent in last 5 seconds");
					continue;
				} catch (IOException e) {
					setWarning(2, "Gemini socket exception: " + e.getMessage());
					break;
				}
				setWarning(0, null);
				GeminiPacket gp = getPacket(rxDataGram.getData());
				if (gp == null) {
					setWarning(1, "Gemini data packet error");
					continue;
				}
				else {
					processGeminiData(gp, rxDataGram);
				}
			}
			socket.close();
			System.out.println("Gemini socket closed");
		}
	}
	
	/**
	 * Unpack raw data from a datagram and return a Gemin data packet. 
	 * @param data raw byte data
	 * @return unpacked data
	 */
	private GeminiPacket getPacket(byte[] data) {
		/*
		 * The basic structure is:

unsigned char        m_packet_type;                                  // Packet type                                                  //1 byte
unsigned char        m_packet_version;                        // Packet version                                               //1 byte
unsigned short           m_datalength;                         // Number of bytes in this packet following this header         //2 bytes
unsigned short  m_message_type;             //need to differentiate between sonar and hydrophone messages       //2 bytes****
unsigned char m_port_framed;                           // Port number receiving the serial data                            //1 byte
unsigned char m_flags1_framed;                  // Bit flags...                                                     //1 byte
                                                // Bit 0 = UTC reference missing
                                                // Bits 1-7 reserved for future use
unsigned long m_seconds_framed;                 // Receive time of first char, //4 bytes
unsigned long m_microsecs_framed;               
		 */
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		GeminiPacket gp = new GeminiPacket();
		try {
			gp.pamguardUTC = PamCalendar.getTimeInMillis();
			gp.packetType = dis.readUnsignedByte();
			gp.dataVer = dis.readUnsignedByte();
			gp.dataLen = Short.reverseBytes(dis.readShort());
			gp.messageType = Short.reverseBytes(dis.readShort());
			gp.portFramed = dis.readUnsignedByte();
			gp.flags1 = dis.readUnsignedByte();
			gp.mSeconds = Integer.reverseBytes(dis.readInt());
			if (gp.mSeconds < 0) gp.mSeconds += 1L<<32;
			gp.mMicros = Integer.reverseBytes(dis.readInt());
			
			String str = new String(data);
			int dollar = str.indexOf('$');
//			System.out.println("Dollar at " + dollar);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String str = stripGeminiString(new String(data, 16, data.length-16));
		if (str == null) {
			return null;
		}
		int starPos = str.indexOf('*');
		if (starPos < 0) {
//			System.out.println("Bad string: " + str);
//			return null;
			/*
			 * Take the entire string - not all of them have a checksum .
			 */
			gp.stringData = str;
			gp.checkSum = 0;
		}
		else {
			gp.stringData = str.substring(0, starPos);
			String endChs = str.substring(starPos+1); 
			try {
				gp.checkSum = Integer.parseInt(endChs, 16);
			}
			catch (NumberFormatException e) {
				gp.checkSum = 0;
				//			return null;
			}
		}
		
		return gp;
	}
	/**
	 * Strip off extra characters, line breaks, anything before the $ symbol, etc. 
	 * @param geminiString
	 * @return stripped string
	 */
	public String stripGeminiString(String geminiString) {
		if (geminiString == null) {
			return null;
		}
		int dollar = geminiString.indexOf('$');
		if (dollar >= 0) {
			geminiString = geminiString.substring(dollar);
		}
		int ech = geminiString.indexOf('\r');
		if (ech > 0) {
			geminiString = geminiString.substring(0,ech);
		}
		ech = geminiString.indexOf('\n');
		if (ech > 0) {
			geminiString = geminiString.substring(0,ech);
		}
		return geminiString;
		
	}

	/**
	 * Quick pass through to function of same name in controller. 
	 * @param level
	 * @param warning
	 */
	private synchronized void setWarning(int level, String warning) {
		geminiControl.setWarning(level, warning);
	}
	

	/**
	 * Process a string received over UDP. 
	 * @param rxDataGram 
	 * @param geminiString string from UDP receiver. 
	 */
	public void processGeminiData(GeminiPacket geminiPacket, DatagramPacket rxDataGram) {
		String geminiString = geminiPacket.stringData;
		boolean checkSumOk = geminiPacket.checkCheckSum();
		
		if (checkSumOk == false) {
//			setWarning(1, "Bad checksum in Gemini string " + geminiString);
//			return;
		}
		String[] stringParts = geminiString.split(",");
//		System.out.println("Received: " + geminiString);
		if (stringParts.length > 0) {
			switch (stringParts[0]) {
			case "$PTRISTA":
				processStatusString(geminiPacket, stringParts);
				break;
			case "$PTRITAR2":
				processTarget2String(geminiPacket);
//				System.out.println("Target string: " + geminiPacket.stringData);
				break;
			case "$PTRITAR":
				break;
			default:
				System.out.println("Unknown string: " + geminiPacket.stringData);
				System.out.println(new String(rxDataGram.getData()));
				break;
				/*
				 * 
Unknown string: $PTRITAR, 01112019, 144103.151, 144101230018, 0.93, -36.912643, 
$PTRITAR, 01112019, 144442.693, 144442010004, 1.30, -22.930971, 
Unknown string: $PTRISTA,0,1,01112019,144103.477,LD20191101_144045_IMG.ecd,912, 1464.10
Unknown string: $PTRISTA,0,1,01112019,144104.538,LD20191101_144045_IMG.ecd,964, 1464.10
				 */
			}
				
		}
	}

	private void processTarget2String(GeminiPacket geminiPacket) {
		Target2DataUnit t2DataUnit = Target2DataUnit.createFromUDPString(geminiPacket.pamguardUTC, geminiPacket.stringData); 
//				new Target2DataUnit(geminiPacket.pamguardUTC, geminiPacket.stringData);
		newTargetData(t2DataUnit);
	}

	private void processStatusString(GeminiPacket geminiPacket, String[] stringParts) {
		/*
		 * Typical string is
		 * $PTRISTA,0,1,28102019,174235.842,LD20191028_174220_IMG.ecd,632, 1483.40*54
		 */
		int version = 0, status = -1;
		String fileName = null;
		int frame = -1;
		float speedOfSound = 0;
		try {
			version = Integer.valueOf(stringParts[1]);
			status = Integer.valueOf(stringParts[2]);
			fileName = stringParts[5];
			frame = Integer.valueOf(stringParts[6]);
			speedOfSound = Float.valueOf(stringParts[7]);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}

		//
		long date = unpackDateTime(stringParts[3], stringParts[4]);
		if (date == 0) {
			return;
		}
//		System.out.println(PamCalendar.formatTime(date,  true));
		GeminiStatusDataUnit gsdu = new GeminiStatusDataUnit(geminiPacket.pamguardUTC, date, version, status, fileName, frame, speedOfSound);
//		System.out.println(gsdu);
		geminiStatusDataBlock.addPamData(gsdu);
	}

	/**
	 * Unpack date and time strings into a millis time. Should (I think) be UTC
	 * @param dateStr date string
	 * @param timeStr time string
	 * @return time in milliseconds. 
	 */
	public static long unpackDateTime(String dateStr, String timeStr) {
		String totString = dateStr.trim() + "_" + timeStr.trim();
		for (int i = 0; i < dateFormats.length; i++) {
			try {
				String fmt = dateFormats[i];
				SimpleDateFormat df = new SimpleDateFormat(fmt);
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date date = df.parse(totString);
				return date.getTime();
			}
			catch (java.text.ParseException ex) {
				
			}
		}
		return 0;
	}/**
	 * Unpack date and time strings into a millis time. Should (I think) be UTC
	 * but is in a different format: 2019/11/01, 14:53:16
	 * @param dateStr date string
	 * @param timeStr time string
	 * @return time in milliseconds. 
	 */
	public static long unpackDateTime2(String dateStr, String timeStr) {
		String totString = dateStr.trim() + " " + timeStr.trim();
		for (int i = 0; i < dateFormats2.length; i++) {
			try {
				String fmt = dateFormats2[i];
				SimpleDateFormat df = new SimpleDateFormat(fmt);
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date date = df.parse(totString);
				return date.getTime();
			}
			catch (java.text.ParseException ex) {
				
			}
		}
		return 0;
	}

	protected void startUDPThread() {
		udpThread = new UDPThread();
		rxThread = new Thread(udpThread);
		rxThread.start();
	}

	/**
	 * Adds a target data unit to it's datablock, but also creates track data units
	 * so that PAMGuard super detections can be used to label data at the track level.
	 * @param target2DataUnit
	 * @return parent track data unit
	 */
	public TrackDataUnit newTargetData(Target2DataUnit target2DataUnit) {
		// assign a UID and datablock right away, since this miget get stored as soon as the superdet is ready. 
		target2DataUnit.setUID(target2DataBlock.getUidHandler().getNextUID(target2DataUnit));
		target2DataUnit.setParentDataBlock(target2DataBlock);
		TrackDataUnit trackDU = trackDataBlock.findTargetTrack(target2DataUnit.getTargetID());
		boolean developedTrack;
		if (trackDU == null) {
			trackDU = developingTracks.findTargetTrack(target2DataUnit.getTargetID());
			developedTrack = false;
		}
		else {
			developedTrack = true;
		}
//		boolean newTrack = false;
		int minScore = geminiControl.getGeminiParameters().minTrackScore;
		if (trackDU == null) {
			trackDU = new TrackDataUnit(target2DataUnit);
			if (trackDU.getHighScore() >= minScore) {
				trackDataBlock.addPamData(trackDU);
				developedTrack = true;
			}
			else {
				developingTracks.addPamData(trackDU);
				developedTrack = false;
			}
		}
		else {
			trackDU.addSubDetection(target2DataUnit);
			if (developedTrack) { // track was already in main block, so send update. 
				trackDataBlock.updatePamData(trackDU, target2DataUnit.getTimeMilliseconds());
				target2DataBlock.addPamData(target2DataUnit, target2DataUnit.getUID());
			}
			else if (trackDU.getHighScore() >= minScore) {
				// track needs moving from developing to main datablock. 
				developingTracks.remove(trackDU);
				trackDU.setParentDataBlock(null);
//				trackDU.setUID(0); // should force it to get the next UId. 
				trackDataBlock.addPamData(trackDU, trackDataBlock.getUidHandler().getNextUID(trackDU));
				/**
				 * Now move all it's sub detections into their correct datablock
				 * Since the superdet is now saved, they should get the correct cross ref.
				 */
				ArrayList<PamDataUnit<?, ?>> subDets = trackDU.getSubDetections();
				for (PamDataUnit subDet : subDets) {
					target2DataBlock.addPamData((Target2DataUnit) subDet, subDet.getUID());
				}
//				trackDataBlock.getLogging().
			}
//			else {
//				No need to update developingTracks if the du didn't get moved to the main block
//              since nothing is observing it. 
//			}
		}
		
		
		return trackDU;
	}
	
	/**
	 * @return the geminiStatusDataBlock
	 */
	public GeminiStatusDataBlock getGeminiStatusDataBlock() {
		return geminiStatusDataBlock;
	}

	/**
	 * @return the trackDataBlock
	 */
	public TrackDataBlock getTrackDataBlock() {
		return trackDataBlock;
	}

	/**
	 * @return the geminiControl
	 */
	public GeminiControl getGeminiControl() {
		return geminiControl;
	}

	/**
	 * @return the ecdDataBlock
	 */
	public ECDDataBlock getEcdDataBlock() {
		return ecdDataBlock;
	}

	/**
	 * @return the target2DataBlock
	 */
	public Target2DataBlock getTarget2DataBlock() {
		return target2DataBlock;
	}

//	/**
//	 * @return the target2DataBlock
//	 */
//	public Target2DataBlock getTarget2DataBlock() {
//		return target2DataBlock;
//	}
	

}
