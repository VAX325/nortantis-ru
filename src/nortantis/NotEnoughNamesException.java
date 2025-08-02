package nortantis;

import nortantis.util.Localization;

@SuppressWarnings("serial")
public class NotEnoughNamesException extends RuntimeException
{
	public NotEnoughNamesException()
	{
	}

	@Override
	public String getMessage()
	{
		return Localization.get("#NotEnoughNames");
	}
}