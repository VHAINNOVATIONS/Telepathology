/**
 * PatientSensitivityLevelType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class PatientSensitivityLevelType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PatientSensitivityLevelType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _RPC_FAILURE = "RPC_FAILURE";
    public static final java.lang.String _NO_ACTION_REQUIRED = "NO_ACTION_REQUIRED";
    public static final java.lang.String _DISPLAY_WARNING = "DISPLAY_WARNING";
    public static final java.lang.String _DISPLAY_WARNING_REQUIRE_OK = "DISPLAY_WARNING_REQUIRE_OK";
    public static final java.lang.String _DISPLAY_WARNING_CANNOT_CONTINUE = "DISPLAY_WARNING_CANNOT_CONTINUE";
    public static final java.lang.String _ACCESS_DENIED = "ACCESS_DENIED";
    public static final PatientSensitivityLevelType RPC_FAILURE = new PatientSensitivityLevelType(_RPC_FAILURE);
    public static final PatientSensitivityLevelType NO_ACTION_REQUIRED = new PatientSensitivityLevelType(_NO_ACTION_REQUIRED);
    public static final PatientSensitivityLevelType DISPLAY_WARNING = new PatientSensitivityLevelType(_DISPLAY_WARNING);
    public static final PatientSensitivityLevelType DISPLAY_WARNING_REQUIRE_OK = new PatientSensitivityLevelType(_DISPLAY_WARNING_REQUIRE_OK);
    public static final PatientSensitivityLevelType DISPLAY_WARNING_CANNOT_CONTINUE = new PatientSensitivityLevelType(_DISPLAY_WARNING_CANNOT_CONTINUE);
    public static final PatientSensitivityLevelType ACCESS_DENIED = new PatientSensitivityLevelType(_ACCESS_DENIED);
    public java.lang.String getValue() { return _value_;}
    public static PatientSensitivityLevelType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        PatientSensitivityLevelType enumeration = (PatientSensitivityLevelType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PatientSensitivityLevelType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PatientSensitivityLevelType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientSensitivityLevelType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
