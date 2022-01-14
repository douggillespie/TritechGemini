package tritechplugin.tritech.glf;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import tritechplugin.tritech.ecd.CountingInputStream;
import tritechplugin.tritech.ecd.LittleEndianDataInputStream;

public class GLFFile {

	private File glfFile;

	private boolean fileOk;

	private InputStream is;

	private static final int DE = 0xDE;

	public GLFFile(File glfFile) {

		this.glfFile = glfFile;

		is = null;
		try {
			is = findDataInputStream(glfFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

//		try {
			catalogFile(is);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}



	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream findDataInputStream(File genesisArchive) throws IOException {
		boolean glf = genesisArchive.getName().endsWith(".glf");
		boolean dat = genesisArchive.getName().endsWith(".dat");
		if (dat) {
			return new FileInputStream(genesisArchive);
		}
		if (glf) {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(glfFile));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				String entryName = zipEntry.getName();
				if (entryName.endsWith(".dat")) {
					return zis;
				}
				zipEntry = zis.getNextEntry();
			}
		}
		throw new IOException("Input stream unavailable in archive file " + genesisArchive);
	}

	/**
	 * Pull a dat file out of a glf file. 
	 * @param glfFile
	 * @return
	 * @throws IOException 
	 */
	private File extractDatFile(File glfFile) throws IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(glfFile));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			System.out.println(zipEntry.getName());
			zipEntry = zis.getNextEntry();
		}
		return null;
	}

	private boolean catalogFile(InputStream inputStream)  {

		if (glfFile.exists() == false) {
			return false;
		}
		long fileLength = glfFile.length();
		/*
		 * Using a buffered input stream brings down the file read time from 18s to 322 millis (x56 speed up)
		 * i've also tried various combinations of random access files and they are not ideal since they go even 
		 * slower than a basic unbuffered file input stream. 
		 */
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		CountingInputStream cis = new CountingInputStream(bis);
		DataInput dis = new LittleEndianDataInputStream(cis);

		int nRec = 0;
		long t1 = System.currentTimeMillis();
		try {
		while (true) {

			GLFRecord glfImage = new GLFRecord(glfFile.getAbsolutePath(), nRec, cis.getPos());
			
			glfImage.m_idChar = dis.readByte();
			glfImage.m_version = dis.readUnsignedByte();
			if (glfImage.m_version == DE) {
				break;
			}
			glfImage.m_length = dis.readInt();
			glfImage.m_timestamp = dis.readDouble();
			glfImage.m_dataType = dis.readUnsignedByte();
			glfImage.tm_deviceId = dis.readUnsignedShort();
			glfImage.m_utility = dis.readUnsignedShort();
			glfImage.m_spare = dis.readShort();
			
			int imageRec = dis.readUnsignedShort();
			int efef = dis.readUnsignedShort();
			if (efef != 0xEFEF) {
				System.out.printf("Unrecognised byte pattern ox%X at position %d in file\n", efef, cis.getPos());
			}
			
			glfImage.imageVersion = dis.readUnsignedShort();
			glfImage.startRange = dis.readInt();
			glfImage.endRange = dis.readInt();
			glfImage.rangeCompression = dis.readUnsignedShort();
			glfImage.startBearing = dis.readInt();
			glfImage.endBearing = dis.readInt();
			glfImage.dataSize = dis.readInt();
			glfImage.zippedData = new byte[glfImage.dataSize];
			dis.readFully(glfImage.zippedData);
			int nBearing = glfImage.endBearing-glfImage.startBearing;
			glfImage.bearingTable = new double[nBearing];
			for (int i = 0; i < nBearing; i++) {
				glfImage.bearingTable[i] = dis.readDouble();
			}
			glfImage.m_uiStateFlags = dis.readInt();
			glfImage.m_UiModulationFrequency = dis.readInt();
			glfImage.m_fBeamFormAperture = dis.readFloat();
			glfImage.m_dbTxtime = dis.readDouble();
			glfImage.m_usPingFlags = dis.readUnsignedShort();
			glfImage.m_sosAtXd = dis.readFloat();
			glfImage.m_sPercentGain = dis.readUnsignedShort();
			glfImage.m_fChirp = dis.readUnsignedByte();
			glfImage.m_ucSonartype = dis.readUnsignedByte();
			glfImage.m_ucPlatform = dis.readUnsignedByte();
			glfImage.oneSpare = dis.readByte();
			glfImage.dede = dis.readUnsignedShort();
			if (glfImage.dede != 0xDEDE) {
				System.out.printf("Unrecognised DEDE byte pattern ox%X at position %d in file\n", efef, cis.getPos());
				break;
			}
			byte[][] imageMatrix = glfImage.getImageData2();
			nRec++;

		}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis();
		System.out.printf("%d records read from file in %d milliseconds\n", nRec, t2-t1);


		return true;

	}


	//		//int logStart = dis.readInt();
	////		int fileVersion = dis.readUnsignedShort();
	//		byte[] hStr = new byte[56];
	//		dis.readFully(hStr);
	//		
	//			byte[] genString = new byte[19108-27]; // to get cr and lf as well. 
	//			dis.readFully(genString); // first character is <, start of xml section
	//			String genStr = new String(genString);
	//			System.out.println(genStr);
	//			int endind = genStr.indexOf("</genesisLog>");
	//			int a = 1+2;
	//		
	//			byte[] b = new byte[1000];
	//			dis.readFully(b); //has same start as hStr


	//		
	//		while (1 < 0) {
	//			long filePos = cis.getPos();
	//			int type = dis.readUnsignedShort();
	//			if (filePos >= fileLength) {
	//				break;
	//			}
	//
	//
	//		}

}
