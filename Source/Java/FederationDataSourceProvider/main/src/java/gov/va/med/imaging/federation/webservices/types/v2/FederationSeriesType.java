/**
 * FederationSeriesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v2;

public class FederationSeriesType  implements java.io.Serializable {
    private java.lang.String seriesId;

    private java.lang.String dicomUid;

    private java.lang.Integer dicomSeriesNumber;

    private java.lang.String description;

    private int imageCount;

    private gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesTypeComponentInstances componentInstances;

    private java.lang.String seriesModality;

    private gov.va.med.imaging.federation.webservices.types.v2.ObjectOriginType objectOrigin;

    public FederationSeriesType() {
    }

    public FederationSeriesType(
           java.lang.String seriesId,
           java.lang.String dicomUid,
           java.lang.Integer dicomSeriesNumber,
           java.lang.String description,
           int imageCount,
           gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesTypeComponentInstances componentInstances,
           java.lang.String seriesModality,
           gov.va.med.imaging.federation.webservices.types.v2.ObjectOriginType objectOrigin) {
           this.seriesId = seriesId;
           this.dicomUid = dicomUid;
           this.dicomSeriesNumber = dicomSeriesNumber;
           this.description = description;
           this.imageCount = imageCount;
           this.componentInstances = componentInstances;
           this.seriesModality = seriesModality;
           this.objectOrigin = objectOrigin;
    }


    /**
     * Gets the seriesId value for this FederationSeriesType.
     * 
     * @return seriesId
     */
    public java.lang.String getSeriesId() {
        return seriesId;
    }


    /**
     * Sets the seriesId value for this FederationSeriesType.
     * 
     * @param seriesId
     */
    public void setSeriesId(java.lang.String seriesId) {
        this.seriesId = seriesId;
    }


    /**
     * Gets the dicomUid value for this FederationSeriesType.
     * 
     * @return dicomUid
     */
    public java.lang.String getDicomUid() {
        return dicomUid;
    }


    /**
     * Sets the dicomUid value for this FederationSeriesType.
     * 
     * @param dicomUid
     */
    public void setDicomUid(java.lang.String dicomUid) {
        this.dicomUid = dicomUid;
    }


    /**
     * Gets the dicomSeriesNumber value for this FederationSeriesType.
     * 
     * @return dicomSeriesNumber
     */
    public java.lang.Integer getDicomSeriesNumber() {
        return dicomSeriesNumber;
    }


    /**
     * Sets the dicomSeriesNumber value for this FederationSeriesType.
     * 
     * @param dicomSeriesNumber
     */
    public void setDicomSeriesNumber(java.lang.Integer dicomSeriesNumber) {
        this.dicomSeriesNumber = dicomSeriesNumber;
    }


    /**
     * Gets the description value for this FederationSeriesType.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this FederationSeriesType.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the imageCount value for this FederationSeriesType.
     * 
     * @return imageCount
     */
    public int getImageCount() {
        return imageCount;
    }


    /**
     * Sets the imageCount value for this FederationSeriesType.
     * 
     * @param imageCount
     */
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }


    /**
     * Gets the componentInstances value for this FederationSeriesType.
     * 
     * @return componentInstances
     */
    public gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesTypeComponentInstances getComponentInstances() {
        return componentInstances;
    }


    /**
     * Sets the componentInstances value for this FederationSeriesType.
     * 
     * @param componentInstances
     */
    public void setComponentInstances(gov.va.med.imaging.federation.webservices.types.v2.FederationSeriesTypeComponentInstances componentInstances) {
        this.componentInstances = componentInstances;
    }


    /**
     * Gets the seriesModality value for this FederationSeriesType.
     * 
     * @return seriesModality
     */
    public java.lang.String getSeriesModality() {
        return seriesModality;
    }


    /**
     * Sets the seriesModality value for this FederationSeriesType.
     * 
     * @param seriesModality
     */
    public void setSeriesModality(java.lang.String seriesModality) {
        this.seriesModality = seriesModality;
    }


    /**
     * Gets the objectOrigin value for this FederationSeriesType.
     * 
     * @return objectOrigin
     */
    public gov.va.med.imaging.federation.webservices.types.v2.ObjectOriginType getObjectOrigin() {
        return objectOrigin;
    }


    /**
     * Sets the objectOrigin value for this FederationSeriesType.
     * 
     * @param objectOrigin
     */
    public void setObjectOrigin(gov.va.med.imaging.federation.webservices.types.v2.ObjectOriginType objectOrigin) {
        this.objectOrigin = objectOrigin;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationSeriesType)) return false;
        FederationSeriesType other = (FederationSeriesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.seriesId==null && other.getSeriesId()==null) || 
             (this.seriesId!=null &&
              this.seriesId.equals(other.getSeriesId()))) &&
            ((this.dicomUid==null && other.getDicomUid()==null) || 
             (this.dicomUid!=null &&
              this.dicomUid.equals(other.getDicomUid()))) &&
            ((this.dicomSeriesNumber==null && other.getDicomSeriesNumber()==null) || 
             (this.dicomSeriesNumber!=null &&
              this.dicomSeriesNumber.equals(other.getDicomSeriesNumber()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            this.imageCount == other.getImageCount() &&
            ((this.componentInstances==null && other.getComponentInstances()==null) || 
             (this.componentInstances!=null &&
              this.componentInstances.equals(other.getComponentInstances()))) &&
            ((this.seriesModality==null && other.getSeriesModality()==null) || 
             (this.seriesModality!=null &&
              this.seriesModality.equals(other.getSeriesModality()))) &&
            ((this.objectOrigin==null && other.getObjectOrigin()==null) || 
             (this.objectOrigin!=null &&
              this.objectOrigin.equals(other.getObjectOrigin())));
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
        if (getSeriesId() != null) {
            _hashCode += getSeriesId().hashCode();
        }
        if (getDicomUid() != null) {
            _hashCode += getDicomUid().hashCode();
        }
        if (getDicomSeriesNumber() != null) {
            _hashCode += getDicomSeriesNumber().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        _hashCode += getImageCount();
        if (getComponentInstances() != null) {
            _hashCode += getComponentInstances().hashCode();
        }
        if (getSeriesModality() != null) {
            _hashCode += getSeriesModality().hashCode();
        }
        if (getObjectOrigin() != null) {
            _hashCode += getObjectOrigin().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationSeriesType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", "FederationSeriesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("seriesId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "series-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dicomUid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dicom-uid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dicomSeriesNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dicom-series-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("componentInstances");
        elemField.setXmlName(new javax.xml.namespace.QName("", "component-instances"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", ">FederationSeriesType>component-instances"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("seriesModality");
        elemField.setXmlName(new javax.xml.namespace.QName("", "series-modality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectOrigin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "object-origin"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", "ObjectOriginType"));
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
