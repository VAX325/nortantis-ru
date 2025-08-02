package nortantis;

import nortantis.util.Localization;

public enum TextType
{
	Title, Region, Mountain_range, Other_mountains, City, Lake, River;

	public String toString()
	{
        return switch (this) {
            case Title -> Localization.get("#Title");
            case Region -> Localization.get("#Region");
            case Mountain_range -> Localization.get("#MountainRange");
            case Other_mountains -> Localization.get("#OtherMountains");
            case City -> Localization.get("#City");
            case Lake -> Localization.get("#Lake");
            case River -> Localization.get("#River");
            default -> name().replace("_", " ");
        };
	}
}