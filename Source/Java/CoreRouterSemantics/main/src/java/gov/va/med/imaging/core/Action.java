package gov.va.med.imaging.core;

import java.util.regex.Pattern;

/**
 * ENUM of all permissible action verbs, with synonyms.
 */
public enum Action
{
	GET(new String[]{ "[gG]et", "[rR]ead" }), 
	POST(new String[]{ "[pP]ost", "[cC]reate", "[sS]tore" }), 
	PUT(new String[]{ "[pP]ut", "[uU]pdate" }), 
	DELETE(new String[]{ "[dD]elete" }),
	HEAD(new String[]{ "[hH]ead" }),
	TRACE(new String[]{ "[tT]race" }),
	OPTIONS(new String[]{ "[oO]ptions" }),
	PREFETCH(new String[]{ "[pP]refetch" }),
	PROCESS(new String[]{ "[pP]rocess" }),
	ENQUEUE(new String[]{ "[eE]nqueue" }),
	DEQUEUE(new String[]{ "[dD]equeue" });

	private String[] expression;

	Action(String[] expression)
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
	public static Action getAction(String synonym)
	{
		if (synonym == null)
			return null;

		for (Action action : Action.values())
			for (String actionSynonym : action.getExpression())
				if(Pattern.matches(actionSynonym, synonym))
					return action;
		return null;
	}
}