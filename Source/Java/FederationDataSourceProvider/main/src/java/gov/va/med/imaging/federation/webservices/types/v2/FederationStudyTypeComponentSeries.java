/**
 * FederationStudyTypeComponentSeries.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v2;

public class FederationStudyTypeComponentSeries  implements java.io.Serializable {
    private gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType[] series;

    public FederationStudyTypeComponentSeries() {
    }

    public FederationStudyTypeComponentSeries(
           gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType[] series) {
           this.series = series;
    }


    /**
     * Gets the series value for this FederationStudyTypeComponentSeries.
     * 
     * @return series
     */
    public gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType[] getSeries() {
        return series;
    }


    /**
     * Sets the series value for this FederationStudyTypeComponentSeries.
     * 
     * @param series
     */
    public void setSeries(gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType[] series) {
        this.series = series;
    }

    public gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType getSeries(int i) {
        return this.series[i];
    }

    public void setSeries(int i, gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesType _value) {
        this.series[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationStudyTypeComponentSeries)) return false;
        FederationStudyTypeComponentSeries other = (FederationStudyTypeComponentSeries) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.series==null && other.getSeries()==null) || 
             (this.series!=null &&
              java.util.Arrays.equals(this.series, other.getSeries())));
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
        if (getSeries() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSeries());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSeries(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationStudyTypeComponentSeries.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>component-series"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("series");
        elemField.setXmlName(new javax.xml.namespace.QName("", "series"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", "FederationSeriesType"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
