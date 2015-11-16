/**
 * FaultTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class FaultTO  implements java.io.Serializable {
    private java.lang.String type;

    private java.lang.String message;

    private gov.va.med.vistaweb.WebServices.SiteService.ArrayOfString stackTrace;

    private java.lang.String suggestion;

    public FaultTO() {
    }

    public FaultTO(
           java.lang.String type,
           java.lang.String message,
           gov.va.med.vistaweb.WebServices.SiteService.ArrayOfString stackTrace,
           java.lang.String suggestion) {
           this.type = type;
           this.message = message;
           this.stackTrace = stackTrace;
           this.suggestion = suggestion;
    }


    /**
     * Gets the type value for this FaultTO.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this FaultTO.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the message value for this FaultTO.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this FaultTO.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the stackTrace value for this FaultTO.
     * 
     * @return stackTrace
     */
    public gov.va.med.vistaweb.WebServices.SiteService.ArrayOfString getStackTrace() {
        return stackTrace;
    }


    /**
     * Sets the stackTrace value for this FaultTO.
     * 
     * @param stackTrace
     */
    public void setStackTrace(gov.va.med.vistaweb.WebServices.SiteService.ArrayOfString stackTrace) {
        this.stackTrace = stackTrace;
    }


    /**
     * Gets the suggestion value for this FaultTO.
     * 
     * @return suggestion
     */
    public java.lang.String getSuggestion() {
        return suggestion;
    }


    /**
     * Sets the suggestion value for this FaultTO.
     * 
     * @param suggestion
     */
    public void setSuggestion(java.lang.String suggestion) {
        this.suggestion = suggestion;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FaultTO)) return false;
        FaultTO other = (FaultTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.stackTrace==null && other.getStackTrace()==null) || 
             (this.stackTrace!=null &&
              this.stackTrace.equals(other.getStackTrace()))) &&
            ((this.suggestion==null && other.getSuggestion()==null) || 
             (this.suggestion!=null &&
              this.suggestion.equals(other.getSuggestion())));
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
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getStackTrace() != null) {
            _hashCode += getStackTrace().hashCode();
        }
        if (getSuggestion() != null) {
            _hashCode += getSuggestion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FaultTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "FaultTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stackTrace");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "stackTrace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ArrayOfString"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suggestion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "suggestion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
