package gov.va.med.imaging.dicom.dcftoolkit.test;

import gov.va.med.imaging.dicom.common.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomElement;


public class MockDicomElement implements IDicomElement {

	
	private String tagName;
	private int vm;
	private short vr;

	private String stringValue;
	private String[] stringValueArray;
	
	private double decimalValue;
	private double[] decimalValueArray;
	
	private String seqTagName;
	private int intValue;
	private int[] intValueArray;

	private String seqTagValue; // faked storage for a string field tag value in a sequence
	private String[] seqTagValueArray; // faked storage for a string array of tag values in a sequence item array


	/**
	 * Constructor for a mock DICOM element whose value is a single String
	 * @param stringValue
	 * @param tagName
	 * @param vm
	 * @param vr
	 */	
	public MockDicomElement(String tagName, int vm, short vr, String stringValue) {
		this.stringValue = stringValue;
		initializeStandardFields(tagName, vm, vr);
	}

	/**
	 * Constructor for a mock DICOM element whose value is an array of Strings
	 * @param stringValueArray
	 * @param tagName
	 * @param vm
	 * @param vr
	 */
	public MockDicomElement(String tagName, int vm, short vr, String[] stringValueArray) {
		this.stringValueArray = stringValueArray;
		initializeStandardFields(tagName, vm, vr);
	}

	/**
	 * Constructor for a mock DICOM element whose value is a single decimal
	 * @param decimalValue
	 * @param tagName
	 * @param vm
	 * @param vr
	 */
	public MockDicomElement(String tagName, int vm, short vr, double decimalValue) {
		this.decimalValue = decimalValue;
		initializeStandardFields(tagName, vm, vr);
	}

	/**
	 * Constructor for a mock DICOM element whose value is an array of decimals
	 * @param decimalValueArray
	 * @param tagName
	 * @param vm
	 * @param vr
	 */
	public MockDicomElement(String tagName, int vm, short vr, double[] decimalValueArray) {
		this.decimalValueArray = decimalValueArray;
		initializeStandardFields(tagName, vm, vr);
	}


	/**
	 * Constructor for a mock DICOM element whose value is a single integer
	 * @param intValue
	 * @param tagName
	 * @param vm
	 * @param vr
	 */
	public MockDicomElement(String tagName, int vm, short vr, int intValue) {
		this.intValue = intValue;
		initializeStandardFields(tagName, vm, vr);
	}

	/**
	 * Constructor for a mock DICOM element whose value is an array of integers
	 * @param intValueArray
	 * @param tagName
	 * @param vm
	 * @param vr
	 */
	public MockDicomElement(String tagName, int vm, short vr, int[] intValueArray) {
		this.intValueArray = intValueArray;
		initializeStandardFields(tagName, vm, vr);
	}
	
	private void initializeStandardFields(String tagName, int vm, short vr) {
		// 
		this.tagName = tagName;
		this.vm = vm;
		this.vr = vr;
	}

	/**
	 * Constructor for a mock DICOM element whose value is a single String
	 * @param stringValue
	 * @param tagName
	 * @param vm
	 * @param vr
	 */	
	public MockDicomElement(String tagName, String seqTagName, String seqTagValue) {
		this.seqTagValue = seqTagValue;
		this.seqTagName = seqTagName;
		initializeStandardFields(tagName, 1, Constants.DICOM_VR_SQ);
	}

	/**
	 * Constructor for a mock DICOM element whose value is an array of Strings
	 * @param stringValueArray
	 * @param tagName
	 * @param vm
	 * @param vr
	 */
	public MockDicomElement(String tagName, String seqTagName, String[] seqTagValueArray) {
		this.seqTagValueArray = seqTagValueArray;
		this.seqTagName = seqTagName;
		int len = seqTagValueArray.length;
		initializeStandardFields(tagName, len, Constants.DICOM_VR_SQ);
	}


	public double getDecimalValue() {
		// 
		return this.decimalValue;
	}

	public double getDecimalValue(int i) {
		// 
		return this.decimalValueArray[i];
	}

	public int getIntValue() {
		// 
		return this.intValue;
	}

	public int getIntValue(int i) {
		// 
		return this.intValue;
	}

	public String getStringValue() {
		// 
		return this.stringValue;
	}

	public String getStringValue(int i) {
		// 
		return this.stringValueArray[i];
	}

	public String getSequenceElementStringValue(String seqtag){
		// 
		if (seqtag == seqTagName)
			return this.seqTagValue;
		else
			return "";
	}
	
	public String getSequenceElementStringValue(String seqtag, int i) {
		// 
		if ((seqtag == seqTagName) && (i < seqTagValueArray.length))
			return this.seqTagValueArray[i];
		else
			return "";
	}
	
	public int vm() {
		// 
		return this.vm;
	}

	
	public short vr() {
		// 
		return this.vr;
	}
	
	public String getTagName()
	{
		return this.tagName;
	}

}
