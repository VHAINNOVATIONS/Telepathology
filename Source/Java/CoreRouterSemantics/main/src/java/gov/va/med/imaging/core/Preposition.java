package gov.va.med.imaging.core;

import java.util.regex.Pattern;

/**
 * ENUM of all permissible prepositions with synonyms.
 */
public enum Preposition
{
	BY(new String[] { "[bB]y" }), 
	LIKE(new String[] { "[lL]ike" });

	private String[] expression;

	Preposition(String[] expression)
	{
		this.expression = expression;
	}

	public String[] getExpression()
	{
		return expression;
	}

	public String getNominalExpression()
	{
		return this.expression[0];
	}

	// for purposes of lookup the synonyms are case-insensitive
	public static Preposition getPreposition(String synonym)
	{
		if (synonym == null)
			return null;

		synonym = synonym.toLowerCase();
		for (Preposition preposition : Preposition.values())
			for (String prepositionSynonym : preposition.getExpression())
				if(Pattern.matches(prepositionSynonym, synonym))
					return preposition;
		return null;
	}

	/**
	 * @return
	 */
	public Object toStringFirstCharUpperCase()
	{
		String result = this.name().toLowerCase();
		result = Character.toUpperCase(result.charAt(0))
				+ result.substring(1);
		return result;
	}
}