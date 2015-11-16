/**
 * FederationVistaRadActiveExamsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationVistaRadActiveExamsType  implements java.io.Serializable {
    private gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType[] activeExams;

    private java.lang.String siteNumber;

    private java.lang.String rawHeader1;

    private java.lang.String rawHeader2;

    public FederationVistaRadActiveExamsType() {
    }

    public FederationVistaRadActiveExamsType(
           gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType[] activeExams,
           java.lang.String siteNumber,
           java.lang.String rawHeader1,
           java.lang.String rawHeader2) {
           this.activeExams = activeExams;
           this.siteNumber = siteNumber;
           this.rawHeader1 = rawHeader1;
           this.rawHeader2 = rawHeader2;
    }


    /**
     * Gets the activeExams value for this FederationVistaRadActiveExamsType.
     * 
     * @return activeExams
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType[] getActiveExams() {
        return activeExams;
    }


    /**
     * Sets the activeExams value for this FederationVistaRadActiveExamsType.
     * 
     * @param activeExams
     */
    public void setActiveExams(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType[] activeExams) {
        this.activeExams = activeExams;
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType getActiveExams(int i) {
        return this.activeExams[i];
    }

    public void setActiveExams(int i, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType _value) {
        this.activeExams[i] = _value;
    }


    /**
     * Gets the siteNumber value for this FederationVistaRadActiveExamsType.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this FederationVistaRadActiveExamsType.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the rawHeader1 value for this FederationVistaRadActiveExamsType.
     * 
     * @return rawHeader1
     */
    public java.lang.String getRawHeader1() {
        return rawHeader1;
    }


    /**
     * Sets the rawHeader1 value for this FederationVistaRadActiveExamsType.
     * 
     * @param rawHeader1
     */
    public void setRawHeader1(java.lang.String rawHeader1) {
        this.rawHeader1 = rawHeader1;
    }


    /**
     * Gets the rawHeader2 value for this FederationVistaRadActiveExamsType.
     * 
     * @return rawHeader2
     */
    public java.lang.String getRawHeader2() {
        return rawHeader2;
    }


    /**
     * Sets the rawHeader2 value for this FederationVistaRadActiveExamsType.
     * 
     * @param rawHeader2
     */
    public void setRawHeader2(java.lang.String rawHeader2) {
        this.rawHeader2 = rawHeader2;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationVistaRadActiveExamsType)) return false;
        FederationVistaRadActiveExamsType other = (FederationVistaRadActiveExamsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.activeExams==null && other.getActiveExams()==null) || 
             (this.activeExams!=null &&
              java.util.Arrays.equals(this.activeExams, other.getActiveExams()))) &&
            ((this.siteNumber==null && other.getSiteNumber()==null) || 
             (this.siteNumber!=null &&
              this.siteNumber.equals(other.getSiteNumber()))) &&
            ((this.rawHeader1==null && other.getRawHeader1()==null) || 
             (this.rawHeader1!=null &&
              this.rawHeader1.equals(other.getRawHeader1()))) &&
            ((this.rawHeader2==null && other.getRawHeader2()==null) || 
             (this.rawHeader2!=null &&
              this.rawHeader2.equals(other.getRawHeader2())));
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
        if (getActiveExams() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getActiveExams());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getActiveExams(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSiteNumber() != null) {
            _hashCode += getSiteNumber().hashCode();
        }
        if (getRawHeader1() != null) {
            _hashCode += getRawHeader1().hashCode();
        }
        if (getRawHeader2() != null) {
            _hashCode += getRawHeader2().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationVistaRadActiveExamsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadActiveExamsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeExams");
        elemField.setXmlName(new javax.xml.namespace.QName("", "activeExams"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadActiveExamType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawHeader1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-header-1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawHeader2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-header-2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
