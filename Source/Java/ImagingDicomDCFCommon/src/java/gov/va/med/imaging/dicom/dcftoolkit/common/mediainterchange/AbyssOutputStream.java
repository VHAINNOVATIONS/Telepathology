package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange;

import java.io.IOException;
import java.io.OutputStream;

public class AbyssOutputStream extends OutputStream {

	public AbyssOutputStream() {
		
	}

	@Override
	public void write(byte[] b) throws IOException {
		//Do nothing
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		//Do nothing
	}

	@Override
	public void flush() throws IOException {
		//Do nothing
	}

	@Override
	public void close() throws IOException {
		//Do nothing
	}

	@Override
	public void write(int b) throws IOException {
		//do nothing

	}

}
