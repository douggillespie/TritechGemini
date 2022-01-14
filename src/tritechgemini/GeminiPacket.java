package tritechgemini;

public class GeminiPacket {

	int packetType;
	int dataVer;
	int dataLen;
	int messageType;
	int portFramed;
	int flags1;
	long mSeconds;
	int mMicros;
	String stringData;
	public long pamguardUTC;
	public int checkSum;
	
	/**
	 * Check the string checksum. This should be the same as in NMEA string, an 
	 * exclusive OR of everything between the $ and * compared to the 2 digit hex
	 * number after the *
	 * @param udpStr
	 * @return true if checksum OK
	 */
	public boolean checkCheckSum() {
		if (stringData == null) {
			return false;
		}
		byte[] bytes = stringData.getBytes();
		byte sum = 0;//bytes[0];
		for (int i = 1; i < bytes.length; i++) {
			sum ^= bytes[i]; 
//			System.out.printf("ch %d checksum %d\n", i, sum);
		}
		
		return sum == checkSum;
	}
	
}
