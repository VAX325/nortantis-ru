package nortantis;

import nortantis.util.Localization;

public enum BorderColorOption
{
	Ocean_color, Choose_color;

    public String toStringNonLocalized() {
        return name().replace("_", " ");
    }

    @Override
    public String toString()
	{
        return switch (this) {
            case Ocean_color -> Localization.get("#OceanColor");
            case Choose_color -> Localization.get("#ChooseColor");
        };
	}
}