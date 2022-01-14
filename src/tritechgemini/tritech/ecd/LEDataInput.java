package tritechgemini.tritech.ecd;

import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LEDataInput implements DataInput {

	private DataInput leInputStream;
	
	public LEDataInput(DataInput beInputStream) {
		super();
		this.leInputStream = beInputStream;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		leInputStream.readFully(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		leInputStream.readFully(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return leInputStream.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return leInputStream.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return leInputStream.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return leInputStream.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		short v = leInputStream.readShort();
		return Short.reverseBytes(v);
	}

	@Override
	public int readUnsignedShort() throws IOException {
		int b1 = leInputStream.readUnsignedByte();
		int b2 = leInputStream.readUnsignedByte();
		return b2<<8 | b1;
	}

	@Override
	public char readChar() throws IOException {
		char c = leInputStream.readChar();
		return Character.reverseBytes(c);
	}

	@Override
	public int readInt() throws IOException {
		return Integer.reverseBytes(leInputStream.readInt());
	}

	@Override
	public long readLong() throws IOException {
		return Long.reverseBytes(leInputStream.readLong());
	}

	@Override
	public float readFloat() throws IOException {
		return leInputStream.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return leInputStream.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		return leInputStream.readLine();
	}

	@Override
	public String readUTF() throws IOException {
		return leInputStream.readUTF();
	}

}
