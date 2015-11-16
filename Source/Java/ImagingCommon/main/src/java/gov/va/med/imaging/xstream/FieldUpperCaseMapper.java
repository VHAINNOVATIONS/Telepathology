package gov.va.med.imaging.xstream;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class FieldUpperCaseMapper extends MapperWrapper {
    public FieldUpperCaseMapper(Mapper wrapped) {
        super(wrapped);
    }

    public String serializedMember(Class type, String memberName) {
        memberName = memberName.substring(0, 1).toUpperCase() + memberName.substring(1);
        return super.serializedMember(type, memberName);
    }
    
    public String realMember(Class type, String memberName) {
        memberName = memberName.substring(0, 1).toLowerCase() + memberName.substring(1);
        return super.serializedMember(type, memberName);
    }
}

