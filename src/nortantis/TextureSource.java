package nortantis;

import nortantis.util.Localization;

public enum TextureSource
{
	Assets, File;

    public String toStringNonLocalized()
    {
        return name();
    }

    @Override
    public String toString()
    {
        return switch (this) {
            case Assets -> Localization.get("#Assets");
            case File -> Localization.get("#File");
        };
    }
}