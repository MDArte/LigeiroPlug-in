package br.ufrj.cos.pinel.ligeiro.plugin.common;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.prefs.BackingStoreException;

import br.ufrj.cos.pinel.ligeiro.plugin.Activator;

/**
 * @author Roque Pinel
 *
 */
public class LigeiroPreferences
{
	private static String KEY_STATISTIC_FILES_CSV = "LIGEIRO_STATISTIC_FILES_CSV";
	private static String KEY_DEPENDENCY_FILES_CSV = "LIGEIRO_DEPENDENCY_FILES_CSV";
	private static String KEY_FPA_CONFIGURATION_FILE = "LIGEIRO_FPA_CONFIGURATION_FILE";

	private static IEclipsePreferences getPreferences()
	{
		IEclipsePreferences preferences = new InstanceScope().getNode(Activator.PLUGIN_ID);

		try
		{
			preferences.sync();
		}
		catch (BackingStoreException e)
		{
			// ignoring
		}

		return preferences;
	}

	/**
	 * Clears all preferences.
	 */
	public static void clear()
	{
		IEclipsePreferences preferences = getPreferences();

		try
		{
			preferences.clear();
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			// ignoring
		}
	}

	/**
	 * @param table the to be changed
	 */
	public static void loadStatisticFiles(Table table)
	{
		String pathsCSV = getPreferences().get(KEY_STATISTIC_FILES_CSV, null);

		if (!br.ufrj.cos.pinel.ligeiro.common.Util.isEmptyOrNull(pathsCSV))
		{
			String[] paths = br.ufrj.cos.pinel.ligeiro.common.Util.getCSVColumns(pathsCSV, Constants.CSV_DELIMITER);

			for (String path : paths)
			{
				Util.addInputFile(table, path);
			}
		}
	}

	public static void saveStatisticFiles(Table table)
	{
		IEclipsePreferences preferences = getPreferences();

		preferences.put(KEY_STATISTIC_FILES_CSV, Util.getInputFileTableCSV(table));

		try
		{
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			// ignoring
		}
	}

	/**
	 * @param table the to be changed
	 */
	public static void loadDependencyFiles(Table table)
	{
		String pathsCSV = getPreferences().get(KEY_DEPENDENCY_FILES_CSV, null);

		if (!br.ufrj.cos.pinel.ligeiro.common.Util.isEmptyOrNull(pathsCSV))
		{
			String[] paths = br.ufrj.cos.pinel.ligeiro.common.Util.getCSVColumns(pathsCSV, Constants.CSV_DELIMITER);

			for (String path : paths)
			{
				Util.addInputFile(table, path);
			}
		}
	}

	public static void saveDependencyFiles(Table table)
	{
		IEclipsePreferences preferences = getPreferences();

		preferences.put(KEY_DEPENDENCY_FILES_CSV, Util.getInputFileTableCSV(table));

		try
		{
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			// ignoring
		}
	}

	/**
	 * @return the configuration file path
	 */
	public static String getFPAConfigurationFile()
	{
		return getPreferences().get(KEY_FPA_CONFIGURATION_FILE, "");
	}

	/**
	 * @param path The path to be setted
	 */
	public static void setFPAConfigurationFile(String path)
	{
		IEclipsePreferences preferences = getPreferences();

		preferences.put(KEY_FPA_CONFIGURATION_FILE, path);

		try
		{
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			// ignoring
		}
	}
}
