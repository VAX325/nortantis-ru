package nortantis;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import nortantis.geom.Point;
import nortantis.util.*;

public class NameCreator
{
	private Random r;
	private NameGenerator placeNameGenerator;
	private NameGenerator personNameGenerator;
	private NameCompiler nameCompiler;
	private final double maxWordLengthComparedToAverage = 2.0;
	private final double probabilityOfKeepingNameLength1 = 0.0;
	private final double probabilityOfKeepingNameLength2 = 0.0;
	private final double probabilityOfKeepingNameLength3 = 0.3;
	private Set<String> namesGenerated;

	public NameCreator(MapSettings settings)
	{
		this.r = new Random(settings.textRandomSeed);
		this.namesGenerated = new HashSet<>();
		processBooks(settings.books);
	}

	private void processBooks(Set<String> books)
	{
		List<String> placeNames = new ArrayList<>();
		List<String> personNames = new ArrayList<>();
		List<Pair<String>> nounAdjectivePairs = new ArrayList<>();
		List<Pair<String>> nounVerbPairs = new ArrayList<>();
		for (String book : books)
		{
			placeNames.addAll(Assets.readNameList(Assets.getAssetsPath() + "/books/" + book + "_place_names.txt"));
			personNames.addAll(Assets.readNameList(Assets.getAssetsPath() + "/books/" + book + "_person_names.txt"));
			nounAdjectivePairs.addAll(Assets.readStringPairs(Assets.getAssetsPath() + "/books/" + book + "_noun_adjective_pairs.txt"));
			nounVerbPairs.addAll(Assets.readStringPairs(Assets.getAssetsPath() + "/books/" + book + "_noun_verb_pairs.txt"));
		}

		placeNameGenerator = new NameGenerator(r, placeNames, maxWordLengthComparedToAverage, probabilityOfKeepingNameLength1,
				probabilityOfKeepingNameLength2, probabilityOfKeepingNameLength3);
		personNameGenerator = new NameGenerator(r, personNames, maxWordLengthComparedToAverage, probabilityOfKeepingNameLength1,
				probabilityOfKeepingNameLength2, probabilityOfKeepingNameLength3);

		nameCompiler = new NameCompiler(r, nounAdjectivePairs, nounVerbPairs);
	}

	public String generatePlaceName(String format, boolean requireUnique)
	{
		return generatePlaceName(format, requireUnique, "");
	}

	public String generatePlaceName(String format, boolean requireUnique, String requiredPrefix)
	{
		if (placeNameGenerator.isEmpty() && !personNameGenerator.isEmpty())
		{
			// Switch to person names
			return generatePersonName(format, requireUnique, requiredPrefix);
		}
		Function0<String> nameCreator = () -> placeNameGenerator.generateName(requiredPrefix);
		return innerCreateUniqueName(format, requireUnique, nameCreator);
	}

	public String generatePersonName(String format, boolean requireUnique)
	{
		return generatePersonName(format, requireUnique, "");
	}

	public String generatePersonName(String format, boolean requireUnique, String requiredPrefix)
	{
		if (personNameGenerator.isEmpty() && !placeNameGenerator.isEmpty())
		{
			// Switch to place names
			return generatePlaceName(format, requireUnique, requiredPrefix);
		}
		Function0<String> nameCreator = () -> personNameGenerator.generateName(requiredPrefix);
		return innerCreateUniqueName(format, requireUnique, nameCreator);
	}

	private String compileName(String format, boolean requireUnique)
	{
		Function0<String> nameCreator = () -> nameCompiler.compileName();
		return innerCreateUniqueName(format, requireUnique, nameCreator);
	}

	private String innerCreateUniqueName(String format, boolean requireUnique, Function0<String> nameCreator)
	{
		final int maxRetries = 20;

		if (!requireUnique)
		{
			return MessageFormat.format(format, nameCreator.apply(), "");
		}

		for (@SuppressWarnings("unused")
		int retry : new Range(maxRetries))
		{
			String name = MessageFormat.format(format, nameCreator.apply(), "");
			if (!namesGenerated.contains(name))
			{
				namesGenerated.add(name);
				return name;
			}
		}
		throw new NotEnoughNamesException();
	}

	public static List<CityType> findCityTypeFromCityFileName(String cityFileNameNoExtension)
	{
		List<CityType> result = new ArrayList<>();
		Set<String> words = new HashSet<String>(Arrays.asList(cityFileNameNoExtension.toLowerCase().split(" |_")));
		if (words.contains("fort") || words.contains("castle") || words.contains("keep") || words.contains("citadel")
				|| words.contains("walled"))
		{
			result.add(CityType.Fortification);
		}
		else
		{
			if (words.contains("city") || words.contains("buildings") || words.contains("cathedral"))
			{
				result.add(CityType.City);
			}
			if (words.contains("town") || words.contains("village") || words.contains("houses"))
			{
				result.add(CityType.Town);
			}
			if (words.contains("homestead") || words.contains("building") || words.contains("house") || words.contains("windmill"))
			{
				result.add(CityType.Homestead);
			}
			if (words.contains("farm") || words.contains("plantation") || words.contains("farmstead") || words.contains("ranch")
					|| words.contains("windmill"))
			{
				result.add(CityType.Farm);
			}
		}

		return result;
	}

	/**
	 * Generate a name of a specified type.
	 * 
	 * @param type
	 *            The type of name
	 * @param subType
	 *            A sub-type specific to the type specified. null means default type.
	 * @param requireUnique
	 *            Whether generated names must be never seen in the extracted book names nor previously generated. If unique name generating
	 *            fails, an exception will be thrown.
	 */
	public String generateNameOfType(TextType type, Object subType, boolean requireUnique)
	{
		if (type.equals(TextType.Title))
		{
			TitleType titleType = subType == null ? TitleType.Decorated : (TitleType) subType;

			double probabilityOfPersonName = 0.3;
			switch (titleType)
			{
			case Decorated:
				if (r.nextDouble() < probabilityOfPersonName)
				{
					return generatePersonName(Localization.get("#TheLandOf"), requireUnique);
				}
				else
				{
					return generatePlaceName(Localization.get("#TheLandOf"), requireUnique);
				}
			case NameOnly:
				return generatePlaceName(Localization.get("#PlainName"), requireUnique);
			default:
				throw new IllegalArgumentException("Unknown title type: " + titleType);
			}
		}
		if (type.equals(TextType.Region))
		{
			double probabilityOfPersonName = 0.2;
			if (r.nextDouble() < probabilityOfPersonName)
			{
				String format = ProbabilityHelper.sampleCategorical(r,
						Arrays.asList(new Tuple2<>(0.2, Localization.get("#KingdomOf")), new Tuple2<>(0.04, Localization.get("#EmpireOf"))));
				return generatePersonName(format, requireUnique);
			}
			else
			{
				String format = ProbabilityHelper.sampleCategorical(r,
						Arrays.asList(new Tuple2<>(0.1, Localization.get("#KingdomOf")), new Tuple2<>(0.02, Localization.get("#EmpireOf")), new Tuple2<>(0.85, Localization.get("#PlainName"))));
				return generatePlaceName(format, requireUnique);
			}
		}
		else if (type.equals(TextType.Mountain_range))
		{
			double probabilityOfCompiledName = nameCompiler.isEmpty() ? 0.0 : 0.7;
			if (r.nextDouble() < probabilityOfCompiledName)
			{
				return compileName(Localization.get("#NameRange"), requireUnique);
			}
			else
			{
				return generatePlaceName(Localization.get("#NameRange"), requireUnique);
			}
		}
		else if (type.equals(TextType.Other_mountains))
		{
			OtherMountainsType mountainType = subType == null ? OtherMountainsType.Mountains : (OtherMountainsType) subType;
			String format = getOtherMountainNameFormat(mountainType);
			double probabilityOfCompiledName = nameCompiler.isEmpty() ? 0.0 : 0.5;
			if (r.nextDouble() < probabilityOfCompiledName)
			{
				return compileName(format, requireUnique);
			}
			else
			{
				double probabilityOfPersonName = 0.4;
				if (r.nextDouble() < probabilityOfPersonName)
				{
					// Person name
					// Make the name possessive.
					format = format.replace("{0}", Localization.get("#PossessiveName"));
					return generatePersonName(format, requireUnique);
				}
				else
				{
					return generatePlaceName(format, requireUnique);
				}
			}
		}
		else if (type.equals(TextType.City))
		{
			CityType cityType = (CityType) subType;
			String structureName;
			if (cityType.equals(CityType.Fortification))
			{
				structureName = ProbabilityHelper.sampleCategorical(r, Arrays.asList(new Tuple2<>(0.2, Localization.get("#Castle")), new Tuple2<>(0.2, Localization.get("#Fort")),
						new Tuple2<>(0.2, Localization.get("#Fortress")), new Tuple2<>(0.2, Localization.get("#Keep")), new Tuple2<>(0.2, Localization.get("#Citadel"))));
			}
			else if (cityType.equals(CityType.City))
			{
				structureName = ProbabilityHelper.sampleCategorical(r,
						Arrays.asList(new Tuple2<>(0.75, Localization.get("#City")), new Tuple2<>(0.25, Localization.get("#Town"))));
			}
			else if (cityType.equals(CityType.Town))
			{
				structureName = ProbabilityHelper.sampleCategorical(r,
						Arrays.asList(new Tuple2<>(0.2, Localization.get("#City")), new Tuple2<>(0.4, Localization.get("#Village")), new Tuple2<>(0.4, Localization.get("#Town"))));
			}
			else if (cityType.equals(CityType.Homestead))
			{
				structureName = Localization.get("#Village");
			}
			else if (cityType.equals(CityType.Farm))
			{
				structureName = ProbabilityHelper.sampleCategorical(r,
						Arrays.asList(new Tuple2<>(0.7, Localization.get("#Farm")), new Tuple2<>(0.3, Localization.get("#Ranch"))));
			}
			else
			{
				throw new RuntimeException("Unknown city type: " + cityType);
			}

			double probabilityOfPersonName = 0.5;
			if (r.nextDouble() < probabilityOfPersonName)
			{
				String format = ProbabilityHelper.sampleCategorical(r,
						Arrays.asList(new Tuple2<>(0.1, Localization.get("#StructureOfName", structureName, "{0}")), new Tuple2<>(0.04, Localization.get("#PossessiveStructure", "{0}", structureName)),
								new Tuple2<>(0.04, Localization.get("#StructureOfName", structureName, "{0}")), new Tuple2<>(0.04, Localization.get("#StructureOfName", structureName, "{0}")),
								new Tuple2<>(0.04, Localization.get("#PossessiveStructure", "{0}", structureName)), new Tuple2<>(0.04, Localization.get("#PossessiveStructure", "{0}", structureName))));
				return generatePersonName(format, requireUnique);
			}
			else
			{
				String format = ProbabilityHelper.sampleCategorical(r, Arrays.asList(new Tuple2<>(0.2, Localization.get("#StructureOfName", structureName, "{0}")),
						new Tuple2<>(0.2, Localization.get("#NameStructure", "{0}", structureName)), new Tuple2<>(0.02, Localization.get("#NameStructure", "{0}", structureName)), new Tuple2<>(0.3, Localization.get("#PlainName"))));
				return generatePlaceName(format, requireUnique);
			}
		}
		else if (type.equals(TextType.River))
		{
			RiverType riverType = subType == null ? RiverType.Large : (RiverType) subType;
			String format = getRiverNameFormat(riverType);
			double probabilityOfCompiledName = nameCompiler.isEmpty() ? 0.0 : 0.5;
			if (r.nextDouble() < probabilityOfCompiledName)
			{
				return compileName(format, requireUnique);
			}
			else
			{
				double probabilityOfPersonName = 0.4;
				if (r.nextDouble() < probabilityOfPersonName)
				{
					// Person name
					// Make the name possessive.
					format = format.replace("{0}", Localization.get("#PossessiveName"));
					return generatePersonName(format, requireUnique);
				}
				else
				{
					return generatePlaceName(format, requireUnique);
				}
			}
		}
		else if (type.equals(TextType.Lake))
		{
			final String nameBeforeLakeFormat = Localization.get("#NameLake");
			String format = ProbabilityHelper.sampleCategorical(r,
					Arrays.asList(new Tuple2<>(0.6, nameBeforeLakeFormat), new Tuple2<>(0.4, Localization.get("#LakeName"))));

			if (format.equals(nameBeforeLakeFormat))
			{
				double probabilityOfCompiledName = nameCompiler.isEmpty() ? 0.0 : 0.5;
				if (r.nextDouble() < probabilityOfCompiledName)
				{
					return compileName(format, requireUnique);
				}
				else
				{
					double probabilityOfPersonName = 0.5;
					if (r.nextDouble() < probabilityOfPersonName)
					{
						// Person name
						// Make the name possessive.
						format = format.replace("{0}", Localization.get("#PossessiveName"));
						return generatePersonName(format, requireUnique);
					}
					else
					{
						return generatePlaceName(format, requireUnique);
					}
				}
			}
			else
			{
				return generatePlaceName(format, requireUnique);
			}
		}
		else
		{
			throw new UnsupportedOperationException("Unknown text type: " + type);
		}
	}

	private String getOtherMountainNameFormat(OtherMountainsType mountainType)
	{
		switch (mountainType)
		{
			case Mountains:
				return Localization.get("#NameMountains");
			case Peak:
				return Localization.get("#NamePeak");
			case Peaks:
				return Localization.get("#NamePeaks");
			default:
				throw new RuntimeException("Unknown mountain group type: " + mountainType);
		}

	}

	public CityType sampleCityTypesForCityFileName(String cityFileNameNoExtension)
	{
		List<CityType> types = findCityTypeFromCityFileName(cityFileNameNoExtension);
		if (types.isEmpty())
		{
			return ProbabilityHelper.sampleEnumUniform(r, CityType.class);
		}

		return ProbabilityHelper.sampleUniform(r, types);
	}

	private String getRiverNameFormat(RiverType riverType)
	{

        return switch (riverType) {
case Large ->
ProbabilityHelper.sampleCategorical(r, Arrays.asList(new Tuple2<>(0.1, Localization.get("#NameWash")), new Tuple2<>(0.8, Localization.get("#NameRiver"))));
case Small ->
ProbabilityHelper.sampleCategorical(r, Arrays.asList(new Tuple2<>(0.1, Localization.get("#NameBayou")), new Tuple2<>(0.2, Localization.get("#NameCreek")),
new Tuple2<>(0.2, Localization.get("#NameBrook")), new Tuple2<>(0.5, Localization.get("#NameStream"))));
default -> throw new RuntimeException("Unknown river type: " + riverType);
};
	}

	/**
	 * Generates a name of the specified type. This is for when the user adds new text to the map. It is not used when the map text is first
	 * generated.
	 * 
	 */
	public String generateNameOfTypeForTextEditor(TextType type)
	{
		nameCompiler.setSeed(System.currentTimeMillis());
		r.setSeed(System.currentTimeMillis());

		Object subType = null;

		if (type.equals(TextType.Title))
		{
			subType = ProbabilityHelper.sampleCategorical(r, ProbabilityHelper.createUniformDistributionOverEnumValues(TitleType.values()));
		}
		else if (type.equals(TextType.Other_mountains))
		{
			subType = ProbabilityHelper.sampleCategorical(r,
					ProbabilityHelper.createUniformDistributionOverEnumValues(OtherMountainsType.values()));
		}
		else if (type.equals(TextType.River))
		{
			subType = ProbabilityHelper.sampleCategorical(r, ProbabilityHelper.createUniformDistributionOverEnumValues(RiverType.values()));
		}
		else if (type.equals(TextType.City))
		{
			// In the editor you add city icons in a different tool than city
			// text, so we have no way of knowing what icon, if any, this city
			// name is for.
			subType = ProbabilityHelper.sampleEnumUniform(r, CityType.class);
		}

		try
		{
			return generateNameOfType(type, subType, false);
		}
		catch (Exception e)
		{
			// This can happen if the selected books don't have enough names.
			return Localization.get("#FallbackName");
		}
	}

	/**
	 * Adds text that the user is manually creating.
	 */
	public MapText createUserAddedText(TextType type, Point location, double resolutionScale)
	{
		String name = generateNameOfTypeForTextEditor(type);
		// Getting the id must be done after calling generateNameOfType because
		// said method increments textCounter
		// before generating the name.
		MapText mapText = TextDrawer.createMapText(name, location, 0.0, type, resolutionScale);
		return mapText;
	}
}
