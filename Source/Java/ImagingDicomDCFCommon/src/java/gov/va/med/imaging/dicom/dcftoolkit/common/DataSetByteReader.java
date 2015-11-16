package gov.va.med.imaging.dicom.dcftoolkit.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.log4j.Logger;

import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomFileOutput;
import com.lbs.DCS.DicomSessionSettings;
import com.lbs.DCS.DicomStreamWriter;
import com.lbs.DCS.FileMetaInformation;

/**
 * DataSetByteReader: This class is used to output a DicomDataSet in file-ready
 * format, but rather than writing directly to a file (as if using
 * DicomFileOutput directly), a java.io.InputStream can be retrieved that will
 * provide the file contents to a user that wishes to read from that stream. The
 * stream can be copied to an output file, or can be handed to another sink that
 * may do something else with that data. This class works works by creating a
 * matched pair of java.io.PipedOutputStream and java.io.PipedInputStream. A
 * DicomStreamWriter is created with that OutputStream and a DicomFileOutput is
 * created with that DicomWriter. The first time that getInputStream() is
 * called, a producer thread is created. That thread calls writeDataSet on the
 * DicomFileOutput which produces data into the PipedOutputStream. That thread
 * will block as needed until the main (consumer) thread reads the data from the
 * returned PipedInputStream. Any exception caught by the producer thread is
 * propogated to the main (consumer) thread, and will be re-thrown when
 * closeInputStream() is called.
 */
public class DataSetByteReader
{
	/**
	 * The input stream that can be used to read the file contents
	 */
	private PipedInputStream inputStream_;
	/**
	 * The output stream to which the file contents are written
	 */
	private PipedOutputStream outputStream_;
	/**
	 * DCF object that wraps OutputStream
	 */
	private DicomStreamWriter streamWriter_;
	/**
	 * The data set to be written
	 */
	private DicomDataSet dataSet_;
	/**
	 * Output Transfer Syntax UID
	 */
	private String tsuid_;
	/**
	 * True for part-10 format output
	 */
	private boolean f_enable_part_10_;
	/**
	 * True if group-0002 elements should be created if they don't exist in data
	 * set.
	 */
	private boolean f_invent_group_2_data_;
	/**
	 * Can be used to override all group-0002 elements
	 */
	private FileMetaInformation fmi_;
	/**
	 * Can be used to override the part-10 file preamble
	 */
	private byte[] preamble_;
	/**
	 * Options for i/o operations
	 */
	private DicomSessionSettings session_settings_;

	/**
	 * Flag to prevent getInputStream() from starting I/O more than once.
	 */
	private boolean f_output_has_started_;

	/**
	 * The object that writes the DICOM part 10 data to the pipe
	 */
	private DicomFileOutput dfo_;

	/**
	 * Thread which writes the Dicom part 10 data to the pipe
	 */
	private Thread producerThread_;

	/**
	 * Used to pass exception from the producer thread to the main thread.
	 */
	private Exception producerException_;

	/**
	 * Log4Net Logger
	 */
    private static Logger logger = Logger.getLogger(DataSetByteReader.class);


	/**
	 * Constructor.
	 * 
	 * @param dataset
	 *            the dataset that will be encoded.
	 * @param tsuid
	 *            The transfer syntax uid to be used to encode the output. If
	 *            f_enable_part_10 is true, then the part 10 header is written
	 *            in explicit-little-endian, and the rest of the file is written
	 *            with this transfer syntax.
	 * @param f_enable_part_10
	 *            true to create chapter 10 format, false for non-chapter-10
	 *            format.
	 * @param f_invent_group_2_data
	 *            if true certain File-Meta-Info (group 0x0002) elements will be
	 *            auto created. If false they are to be included in the dataset
	 *            parameter.
	 * @param fmi
	 *            chapter 10 File Meta Information, which defines, among other
	 *            things, the transfer syntax that will be used to produce the
	 *            encoded output. If this is null, then part-10 meta info is
	 *            either created or assumed to exist in the dataset parameter.
	 * @param preamble
	 *            preamble information to be written in the chapter-10
	 *            pre-header. This must be either null, or point to a buffer
	 *            containing 132 bytes of data.
	 * @param session_settings
	 *            I/O settings for this object.
	 * @throws DCSException
	 *             if an error occurs.
	 */
	public DataSetByteReader(DicomDataSet dataset, String tsuid, boolean f_enable_part_10, boolean f_invent_group_2_data,
			FileMetaInformation fmi, byte[] preamble, DicomSessionSettings session_settings) throws DCSException
	{
		try
		{
			dataSet_ = dataset;
			tsuid_ = tsuid;
			f_enable_part_10_ = f_enable_part_10;
			f_invent_group_2_data_ = f_invent_group_2_data;
			fmi_ = fmi;
			preamble_ = preamble;
			session_settings_ = session_settings;
			inputStream_ = new PipedInputStream();
			outputStream_ = new PipedOutputStream(inputStream_);
			streamWriter_ = new DicomStreamWriter(outputStream_, session_settings_);

			dfo_ = new DicomFileOutput(streamWriter_, null, // filename
					tsuid_, f_enable_part_10_, // create part 10 header
					f_invent_group_2_data_, // invent group 2 data
					fmi_, // FileMetaInformation
					preamble_, // preamble
					session_settings_);
			f_output_has_started_ = false;
		}
		catch (IOException e)
		{
			throw new DCSException("", e);
		}
	}

	/**
	 * Start the output producer thread which will write into the
	 * PipedOutputStream, and return the connected PipedInputStream. If this is
	 * called a second or additional time, simply return the stream.
	 */
	public synchronized InputStream getInputStream() throws Exception
	{
		if (!f_output_has_started_)
		{
			//
			// we need a separate "producer" thread to do the write, as it will
			// block, waiting for the consumer thread to read from the pipe
			// as the buffer fills. Our "run" method will do the dfo_.write()
			// in the new thread while this thread will proceed to read from the
			// PipedInputStream and write to the actual output file.
			//
			producerThread_ = new Thread()
			{
				public void run()
				{
					try
					{
						logger.debug("Entered run method.");
						dfo_.writeDataSet(dataSet_);
						dfo_.close();
						logger.debug("Completed writing data set. Exiting run method.");
					}
					catch (Exception e)
					{
						logger.debug("Caught exception during run. Setting producerException for main thread to see:", e);
						producerException_ = e;
					}
				}
			};
			producerThread_.start();
			f_output_has_started_ = true;
		}

		//
		// An earlier example (and DataSetByteReader) provided both the
		// ReadableByteChannel and InputStream interfaces. A ReadableByteChannel
		// can be created from an InputStream using the
		// java.nio.channels.Channels
		// utility class.
		//
		return inputStream_;

	}

	/**
	 * Close the PipedInputStream if it is still open, and wait for the producer
	 * thread to exit. If an exception was caught by the producer thread,
	 * rethrow it now on the calling thread.
	 */
	public void closeInputStream() throws Exception
	{
		try
		{
			if (inputStream_ != null)
			{
				try
				{
					// This will force producer thread to quit if it
					// isn't already..
					inputStream_.close();
				}
				catch (Exception e)
				{
				}
			}

			//
			// Wait for producer thread to complete.
			// If it caught an exception, rethrow on this thread.
			//
			if (producerThread_ != null)
			{
				logger.debug("writeFileToDisk: wait for producer thread to exit.");
				producerThread_.join(2000);
				logger.debug("writeFileToDisk: join of producer thread complete.");
			}

			if (producerException_ != null)
			{
				logger.debug("Rethrowing thread from producer thread on main thread:");
				throw producerException_;
			}
		}
		finally
		{
			inputStream_ = null;
			producerThread_ = null;
			dataSet_ = null;
			dfo_ = null;
		}
	}
}

