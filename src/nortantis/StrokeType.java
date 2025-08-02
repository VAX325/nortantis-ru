package nortantis;

import nortantis.util.Localization;

public enum StrokeType
{
	Solid, Dashes, Rounded_Dashes, Dots;

    public String toStringNonLocalized() {
        return name().replace("_", " ");
    }

    @Override
	public String toString()
	{
        return switch (this) {
            case Solid -> Localization.get("#Solid");
            case Dashes -> Localization.get("#Dashes");
            case Rounded_Dashes -> Localization.get("#RoundedDashes");
            case Dots -> Localization.get("#Dots");
            default -> name().replace('_', ' ');
        };
	}
}
