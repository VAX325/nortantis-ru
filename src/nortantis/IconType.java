package nortantis;

import nortantis.util.Localization;

public enum IconType
{
	// These names must match the folder names in assets/icons.
	mountains, hills, sand, trees, cities, decorations;

	public String getSingularName()
	{
        return switch (this) {
            case mountains -> Localization.get("#MountainSingular");
            case hills -> Localization.get("#HillsSingular");
            case sand -> Localization.get("#SandSingular");
            case trees -> Localization.get("#TreesSingular");
            case cities -> Localization.get("#CitiesSingular");
            case decorations -> Localization.get("#DecorationsSingular");
            default -> {
                assert false;
                yield toString();
            }
        };
	}

	public String getNameForGUI()
	{
		if (this == sand)
		{
			return Localization.get("#DunesGUI");
		}
		// Capitalize first letter.
		return toString().substring(0, 1).toUpperCase() + toString().substring(1);
	}
}