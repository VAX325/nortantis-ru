package nortantis.swing;

import nortantis.util.Localization;

public enum UpdateType
{
	Full, Incremental, Text /* Refers to redrawing all text, not incremental text changes in the Text tool */, Fonts, Terrain, GrungeAndFray, ReprocessBooks, OverlayImage;

	@Override
	public String toString()
	{
		return switch (this) {
			case Full -> Localization.get("#Full");
			case Incremental -> Localization.get("#Incremental");
			case Text -> Localization.get("#Text");
			case Fonts -> Localization.get("#Fonts");
			case Terrain -> Localization.get("#Terrain");
			case GrungeAndFray -> Localization.get("#GrungeAndFray");
			case ReprocessBooks -> Localization.get("#ReprocessBooks");
			case OverlayImage -> Localization.get("#OverlayImage");
		};
	}
}
