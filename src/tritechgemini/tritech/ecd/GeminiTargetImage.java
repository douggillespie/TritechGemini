package tritechgemini.tritech.ecd;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;

import PamUtils.PamUtils;
import tritechgemini.tritech.GeminiRecord;


public class GeminiTargetImage extends ECDRecord implements GeminiRecord {

	private double[] bearingTable;
	int m_version;
	int m_pid;
	int m_halfArr;
	int m_txLength;
	int m_scanRate;
	float m_sosAtXd;
	int m_shading;
	int m_mainGain;
	int m_gainBlank;
	int m_adcInput;
	int m_spreadGain; 
	int m_absorbGain; 
	int m_bfFocus;
	int m_bfGain;
	float m_bfAperture;
	int m_txStart;
	int m_txLen;
	float m_txRadius;
	float m_txRng; 
	int m_modFreq;
	int m_numBeams;
	float m_sosAtXd_2;
	int m_rx1;
	int m_rx2;
	int m_tx1;
	int m_pingFlags;
	int m_rx1Arr; 
	int m_rx2Arr;
	int m_tx1Arr; 
	int m_tx2Arr;
	int m_tid;
	int m_pid2;
	double m_txTime;
	double m_endTime;
	double m_txAngle;
	double m_sosAvg;
	int mask;
	int m_bpp; // bytes per pixel.
	int m_nRngs;
	int m_b0;
	int m_b1; 
	int m_r0;
	int m_r1;
	int dual;
	int m_nBrgs;
	int m_Brgs_2;
	int cSize;
	byte[] cData;
	int sCount;
	private long m_TimeMills;

	public GeminiTargetImage(File ecdFile, int recordType, int recordVersion) {
		super(ecdFile, recordType, recordVersion);
	}

	@Override
	public boolean readDataFile(DataInput dis) throws IOException {
		// start of record CPing
		m_version = dis.readShort(); // def 0
		m_pid = dis.readUnsignedByte(); // unique id for ping
		m_halfArr = dis.readInt(); 
		m_txLength = dis.readUnsignedByte(); 
		m_scanRate = dis.readUnsignedByte(); 
		m_sosAtXd = dis.readFloat(); 
		m_shading = dis.readShort(); 
		m_mainGain = dis.readShort(); 
		m_gainBlank = dis.readShort(); 
		m_adcInput = dis.readShort(); 
		m_spreadGain = dis.readShort(); 
		m_absorbGain = dis.readShort(); 
		m_bfFocus = dis.readInt(); 
		m_bfGain = dis.readShort(); 
		m_bfAperture = dis.readFloat(); 
		m_txStart = dis.readShort();
		m_txLen = dis.readShort(); // correct to here. 
		m_txRadius = dis.readFloat(); 
		m_txRng = dis.readFloat(); 
		m_modFreq = dis.readInt(); 
		m_numBeams = dis.readShort(); 
		m_sosAtXd_2 = dis.readFloat(); // speed of sound
		m_rx1 = dis.readShort(); 
		m_rx2 = dis.readShort(); 
		m_tx1 = dis.readShort(); 
		m_pingFlags = dis.readShort(); 
		m_rx1Arr = dis.readUnsignedByte(); 
		m_rx2Arr = dis.readUnsignedByte(); 
		m_tx1Arr = dis.readUnsignedByte(); 
		m_tx2Arr = dis.readUnsignedByte();  
		//End of data from CPing
		m_tid = dis.readUnsignedShort();  //From CTgtRec
		m_pid2 = dis.readUnsignedShort(); // oscillates between 2 and 1. Is this the sonar number ? 
		m_txTime = dis.readDouble(); // typical 1.2894707737033613E9
		m_endTime = dis.readDouble(); 
		m_TimeMills = cDateToMillis(m_txTime);
		double dt = m_endTime-m_txTime; // comes out at 0 every time. 
		m_txAngle = dis.readDouble(); 
		m_sosAvg = dis.readDouble();  //End of data from CTgtRec - looks like a reasonable value for speed of sound
		mask = dis.readInt();  //From CTgtImg
		m_bpp = dis.readUnsignedByte(); 
		m_nRngs = dis.readInt();  // this changes with set range. On old Gemini's 1m=122, 5.1m=677 101m=1545 , New gemini 50.1m 763 ranges. 
		m_b0 = dis.readInt(); 
		m_b1 = dis.readInt(); 
		m_r0 = dis.readInt(); 
		m_r1 = dis.readInt(); 
		dual = dis.readInt(); 
		m_nBrgs = dis.readInt();
		bearingTable = new double[m_numBeams];
		for (int i = 0; i < m_numBeams; i++) {
			/*
			 * Sweet - clearly OK at this point since I get to read an array of 512 angles 
			 * in radians that goes from +60 deg to -60 deg.. 
			 */
			bearingTable[i] = dis.readDouble();
		}
		m_Brgs_2 = dis.readInt();
		cSize = dis.readInt();
		cData = new byte[cSize];
		dis.readFully(cData);
//		dis.skip(cSize);
		sCount = dis.readInt();
		int tag = dis.readUnsignedShort(); // correct tag for end of frame!

//		uncompressData();

		return setEndTag(tag);
	}
	
	/**
	 * Get the maximum range using eq' provided by Phil. 
	 * @return Max range 
	 */
	public double getMaxRange() {
		/**
(CTgtImg->m_nRngs * (CPing->m_sosAtXD/2.0) / CPing->m_modFreq
I would try to avoid using the PingTail Extension record, unless you think there is something vital in there.
		 */
		return m_nRngs * (m_sosAtXd/2.)/m_modFreq;
	}
	
	private final double[]  defaultBearingRange = {Math.toRadians(-60), Math.toRadians(60)};
	
	/**
	 * Get the maximum distance in the x (bearing) dimensions based on the bearind range and max horizontal range. 
	 * @return
	 */
	public double[] getXRange() {
		double maxRange = getMaxRange();
		double[] bearings = getBearingTable();
		double[] bearingLimits;
		if (bearings != null) {
			bearingLimits = defaultBearingRange;
		}
		else {
			bearingLimits = PamUtils.getMinAndMax(bearings);
		}
		double[] xLimits = new double[2];
		for (int i = 0; i < 2; i++) {
			xLimits[i] = Math.sin(bearingLimits[i]) * maxRange;
		}
		return xLimits;
	}
	
	private long cDateToMillis(double cDate) {
//		cDate is ref's to 1980 in secs, Java in millis from 1970. 
		int days = 3652;
		int secsPerDay = 3600*24;
		return (long) ((cDate+days*secsPerDay)*1000.);
	}

	public byte[] uncompressData() {
		int m_dataSize = m_nBrgs*m_nRngs*m_bpp;
		byte[] pData = new byte[m_nBrgs*m_nRngs*m_bpp]; // have to use short ?
		//		byte[] pBlockLn = cData;
		if (cData == null) {
			return null;
		}
		int size = cData.length; // size of input data
		int iC = 0, iU = 0;
		int nZeros = 0;
		byte maxByte = (byte) 0xFF;
		int posPix = 0;

		while ( iC < size && iU < m_dataSize)
		{
			if (cData[iC] == 0 && iC < (size - 1))
			{
				iC++;
				nZeros = Byte.toUnsignedInt(cData[iC++]);
				if (nZeros == maxByte)
					if (cData[iC] == maxByte)
					{
						iC++;
					}
				while (nZeros != 0 && iU < m_dataSize)
				{
					pData[iU++] = 0;
					nZeros--;
				}
			}
			else if (cData[iC] == 1)
			{
				iC++;
				pData[iU++] = 0;
			}
			else
			{
				pData[iU++] = cData[iC++];
				posPix++;
			}
		}
		

		return pData;
	}
	
	
	public void freeCData() {
		cData = null;
	}

	/**
	 * @return the bearingTable
	 */
	public double[] getBearingTable() {
		return bearingTable;
	}

	/**
	 * @return the m_version
	 */
	public int getM_version() {
		return m_version;
	}

	/**
	 * @return the m_pid
	 */
	public int getM_pid() {
		return m_pid;
	}

	/**
	 * @return the m_halfArr
	 */
	public int getM_halfArr() {
		return m_halfArr;
	}

	/**
	 * @return the m_txLength
	 */
	public int getM_txLength() {
		return m_txLength;
	}

	/**
	 * @return the m_scanRate
	 */
	public int getM_scanRate() {
		return m_scanRate;
	}

	/**
	 * @return the m_sosAtXd
	 */
	public float getM_sosAtXd() {
		return m_sosAtXd;
	}

	/**
	 * @return the m_shading
	 */
	public int getM_shading() {
		return m_shading;
	}

	/**
	 * @return the m_mainGain
	 */
	public int getM_mainGain() {
		return m_mainGain;
	}

	/**
	 * @return the m_gainBlank
	 */
	public int getM_gainBlank() {
		return m_gainBlank;
	}

	/**
	 * @return the m_adcInput
	 */
	public int getM_adcInput() {
		return m_adcInput;
	}

	/**
	 * @return the m_spreadGain
	 */
	public int getM_spreadGain() {
		return m_spreadGain;
	}

	/**
	 * @return the m_absorbGain
	 */
	public int getM_absorbGain() {
		return m_absorbGain;
	}

	/**
	 * @return the m_bfFocus
	 */
	public int getM_bfFocus() {
		return m_bfFocus;
	}

	/**
	 * @return the m_bfGain
	 */
	public int getM_bfGain() {
		return m_bfGain;
	}

	/**
	 * @return the m_bfAperture
	 */
	public float getM_bfAperture() {
		return m_bfAperture;
	}

	/**
	 * @return the m_txStart
	 */
	public int getM_txStart() {
		return m_txStart;
	}

	/**
	 * @return the m_txLen
	 */
	public int getM_txLen() {
		return m_txLen;
	}

	/**
	 * @return the m_txRadius
	 */
	public float getM_txRadius() {
		return m_txRadius;
	}

	/**
	 * @return the m_txRng
	 */
	public float getM_txRng() {
		return m_txRng;
	}

	/**
	 * @return the m_modFreq
	 */
	public int getM_modFreq() {
		return m_modFreq;
	}

	/**
	 * @return the m_numBeams
	 */
	public int getM_numBeams() {
		return m_numBeams;
	}

	/**
	 * @return the m_sosAtXd_2
	 */
	public float getM_sosAtXd_2() {
		return m_sosAtXd_2;
	}

	/**
	 * @return the m_rx1
	 */
	public int getM_rx1() {
		return m_rx1;
	}

	/**
	 * @return the m_rx2
	 */
	public int getM_rx2() {
		return m_rx2;
	}

	/**
	 * @return the m_tx1
	 */
	public int getM_tx1() {
		return m_tx1;
	}

	/**
	 * @return the m_pingFlags
	 */
	public int getM_pingFlags() {
		return m_pingFlags;
	}

	/**
	 * @return the m_rx1Arr
	 */
	public int getM_rx1Arr() {
		return m_rx1Arr;
	}

	/**
	 * @return the m_rx2Arr
	 */
	public int getM_rx2Arr() {
		return m_rx2Arr;
	}

	/**
	 * @return the m_tx1Arr
	 */
	public int getM_tx1Arr() {
		return m_tx1Arr;
	}

	/**
	 * @return the m_tx2Arr
	 */
	public int getM_tx2Arr() {
		return m_tx2Arr;
	}

	/**
	 * @return the m_tid
	 */
	public int getM_tid() {
		return m_tid;
	}

	/**
	 * @return the m_pid2
	 */
	public int getM_pid2() {
		return m_pid2;
	}

	/**
	 * @return the m_txTime
	 */
	public double getM_txTime() {
		return m_txTime;
	}

	/**
	 * @return the m_endTime
	 */
	public double getM_endTime() {
		return m_endTime;
	}

	/**
	 * @return the m_txAngle
	 */
	public double getM_txAngle() {
		return m_txAngle;
	}

	/**
	 * @return the m_sosAvg
	 */
	public double getM_sosAvg() {
		return m_sosAvg;
	}

	/**
	 * @return the mask
	 */
	public int getMask() {
		return mask;
	}

	/**
	 * @return the m_bpp
	 */
	public int getM_bpp() {
		return m_bpp;
	}

	/**
	 * @return the m_nRngs
	 */
	public int getM_nRngs() {
		return m_nRngs;
	}

	/**
	 * @return the m_b0
	 */
	public int getM_b0() {
		return m_b0;
	}

	/**
	 * @return the m_b1
	 */
	public int getM_b1() {
		return m_b1;
	}

	/**
	 * @return the m_r0
	 */
	public int getM_r0() {
		return m_r0;
	}

	/**
	 * @return the m_r1
	 */
	public int getM_r1() {
		return m_r1;
	}

	/**
	 * @return the dual
	 */
	public int getDual() {
		return dual;
	}

	/**
	 * @return the m_nBrgs
	 */
	public int getM_nBrgs() {
		return m_nBrgs;
	}

	/**
	 * @return the m_Brgs_2
	 */
	public int getM_Brgs_2() {
		return m_Brgs_2;
	}

	/**
	 * @return the cSize
	 */
	public int getcSize() {
		return cSize;
	}

	/**
	 * @return the cData
	 */
	public byte[] getcData() {
		return cData;
	}

	/**
	 * @return the sCount
	 */
	public int getsCount() {
		return sCount;
	}

	/**
	 * @return the m_TimeMills
	 */
	public long getM_TimeMills() {
		return m_TimeMills;
	}

	@Override
	public long getImageTime() {
		return getM_TimeMills();
	}

	@Override
	public byte[] getImageData() {
		return getcData();
	}

	@Override
	public int getnRange() {
		return m_nRngs;
	}

	@Override
	public String getFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRecordNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSonarType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSonarPlatform() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSonarIndex() {
		return m_pid;
	}

	@Override
	public int getDeviceId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
