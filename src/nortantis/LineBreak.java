package nortantis;

import nortantis.util.Localization;

public enum LineBreak
{
	Auto, One_line, Two_lines;

    public String toStringNonLocalized()
    {
        return name().replace("_", " ");
    }

    @Override
	public String toString()
	{
        return switch (this) {
            case Auto -> Localization.get("#Auto");
            case One_line -> Localization.get("#OneLine");
            case Two_lines -> Localization.get("#TwoLines");
            default -> name().replace("_", " ");
        };
	}
}
