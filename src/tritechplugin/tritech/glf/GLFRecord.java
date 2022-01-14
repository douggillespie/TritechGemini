package tritechplugin.tritech.glf;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import tritechplugin.tritech.GeminiRecord;

public class GLFRecord extends GLFHeader implements GeminiRecord {
	
	private int iRecord;
	private long filePos;

	public GLFRecord(String filePath, int iRecord, long filePos) {
		this.filePath = filePath;
		this.iRecord = iRecord;
		this.filePos = filePos;
	}
	
	private byte[] unzippedData;
	private byte[][] unzippedData2;

	public int imageVersion;
	public int startRange;
	public int endRange;
	public int rangeCompression;
	public int startBearing;
	public int endBearing;
	public int dataSize;
	public byte[] zippedData;
	public double[] bearingTable;
	
	public int m_uiStateFlags;
	public int m_UiModulationFrequency;
	public float m_fBeamFormAperture;
	public double m_dbTxtime;
	public int m_usPingFlags;
	public float m_sosAtXd;
	public int m_sPercentGain;
	public int m_fChirp;
	public int m_ucSonartype;
	public int m_ucPlatform;
	public byte oneSpare;
	public int dede;
	
	public String filePath;
	
	/*
	 * time reference is 1 January 1980
	 */
	private static final long DATEZERO = 315532800000L;
	
	public byte[] getImageData() {
		if (unzippedData != null) {
			return unzippedData;
		}
		if (zippedData == null) {
			return null;
		}
		Inflater inflater = new Inflater();
		inflater.setInput(zippedData);
		int outSize = (endBearing-startBearing)*(endRange-startRange);
		byte[] unzippedData = new byte[outSize];
		int bytesRead = 0;
		try {
			bytesRead = inflater.inflate(unzippedData);
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
		return unzippedData;
	}

	public byte[][] getImageData2() {
		if (unzippedData2 != null) {
			return unzippedData2;
		}
		if (zippedData == null) {
			return null;
		}
		Inflater inflater = new Inflater();
		inflater.setInput(zippedData);
		int nBearing = (endBearing-startBearing);
		int nRange = (endRange-startRange);
		unzippedData2 = new byte[nBearing][nRange];
		int bRead = 0;
		try {
			for (int i = 0; i < nBearing; i++) {
				bRead += inflater.inflate(unzippedData2[i]);
			}
		} catch (DataFormatException e) {
			e.printStackTrace();
			return null;
		}
		return unzippedData2;		
	}

	@Override
	public long getImageTime() {
		// m_dbTxtime is seconds since 1/1/1980
		return DATEZERO + (long) (m_dbTxtime*1000.);
	}

	@Override
	public double[] getBearingTable() {
		return bearingTable;
	}

	@Override
	public int getnRange() {
		return endRange-startRange;
	}

	@Override
	public double getMaxRange() {
		return endRange*m_UiModulationFrequency/2.;
	}

	@Override
	public String getFilePath() {
		return filePath;
	}

	@Override
	public int getRecordNumber() {
		return iRecord;
	}

	@Override
	public int getSonarType() {
		// TODO Auto-generated method stub
		return m_ucSonartype;
	}

	@Override
	public int getSonarIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSonarPlatform() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDeviceId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
