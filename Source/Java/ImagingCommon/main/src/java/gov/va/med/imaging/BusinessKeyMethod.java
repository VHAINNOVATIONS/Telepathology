package gov.va.med.imaging;

/**
 * @author vhaiswgraver
 * An enumeration of ways a BusinessKey can be used:
 *    Comparing equality
 *    Generating a hash code
 *    Converting to a string representation
 */
public enum BusinessKeyMethod {
    ALL, NONE, EQUALS, HASH_CODE, TO_STRING
}

