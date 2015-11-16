/**
 * PatientType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class PatientType  implements java.io.Serializable {
    private java.lang.String patientName;

    private java.lang.String patientIcn;

    private java.lang.String veteranStatus;

    private java.lang.String patientDob;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType patientSex;

    public PatientType() {
    }

    public PatientType(
           java.lang.String patientName,
           java.lang.String patientIcn,
           java.lang.String veteranStatus,
           java.lang.String patientDob,
           gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType patientSex) {
           this.patientName = patientName;
           this.patientIcn = patientIcn;
           this.veteranStatus = veteranStatus;
           this.patientDob = patientDob;
           this.patientSex = patientSex;
    }


    /**
     * Gets the patientName value for this PatientType.
     * 
     * @return patientName
     */
    public java.lang.String getPatientName() {
        return patientName;
    }


    /**
     * Sets the patientName value for this PatientType.
     * 
     * @param patientName
     */
    public void setPatientName(java.lang.String patientName) {
        this.patientName = patientName;
    }


    /**
     * Gets the patientIcn value for this PatientType.
     * 
     * @return patientIcn
     */
    public java.lang.String getPatientIcn() {
        return patientIcn;
    }


    /**
     * Sets the patientIcn value for this PatientType.
     * 
     * @param patientIcn
     */
    public void setPatientIcn(java.lang.String patientIcn) {
        this.patientIcn = patientIcn;
    }


    /**
     * Gets the veteranStatus value for this PatientType.
     * 
     * @return veteranStatus
     */
    public java.lang.String getVeteranStatus() {
        return veteranStatus;
    }


    /**
     * Sets the veteranStatus value for this PatientType.
     * 
     * @param veteranStatus
     */
    public void setVeteranStatus(java.lang.String veteranStatus) {
        this.veteranStatus = veteranStatus;
    }


    /**
     * Gets the patientDob value for this PatientType.
     * 
     * @return patientDob
     */
    public java.lang.String getPatientDob() {
        return patientDob;
    }


    /**
     * Sets the patientDob value for this PatientType.
     * 
     * @param patientDob
     */
    public void setPatientDob(java.lang.String patientDob) {
        this.patientDob = patientDob;
    }


    /**
     * Gets the patientSex value for this PatientType.
     * 
     * @return patientSex
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType getPatientSex() {
        return patientSex;
    }


    /**
     * Sets the patientSex value for this PatientType.
     * 
     * @param patientSex
     */
    public void setPatientSex(gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType patientSex) {
        this.patientSex = patientSex;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PatientType)) return false;
        PatientType other = (PatientType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.patientName==null && other.getPatientName()==null) || 
             (this.patientName!=null &&
              this.patientName.equals(other.getPatientName()))) &&
            ((this.patientIcn==null && other.getPatientIcn()==null) || 
             (this.patientIcn!=null &&
              this.patientIcn.equals(other.getPatientIcn()))) &&
            ((this.veteranStatus==null && other.getVeteranStatus()==null) || 
             (this.veteranStatus!=null &&
              this.veteranStatus.equals(other.getVeteranStatus()))) &&
            ((this.patientDob==null && other.getPatientDob()==null) || 
             (this.patientDob!=null &&
              this.patientDob.equals(other.getPatientDob()))) &&
            ((this.patientSex==null && other.getPatientSex()==null) || 
             (this.patientSex!=null &&
              this.patientSex.equals(other.getPatientSex())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getPatientName() != null) {
            _hashCode += getPatientName().hashCode();
        }
        if (getPatientIcn() != null) {
            _hashCode += getPatientIcn().hashCode();
        }
        if (getVeteranStatus() != null) {
            _hashCode += getVeteranStatus().hashCode();
        }
        if (getPatientDob() != null) {
            _hashCode += getPatientDob().hashCode();
        }
        if (getPatientSex() != null) {
            _hashCode += getPatientSex().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PatientType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientIcn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-icn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("veteranStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "veteran-status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientDob");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-dob"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientSex");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-sex"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationPatientSexType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
