package br.ufrj.cos.pinel.ligeiro.plugin.common;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.prefs.BackingStoreException;

import br.ufrj.cos.pinel.ligeiro.plugin.LigeiroPlugin;

/**
 * @author Roque Pinel
 *
 */
public class LigeiroPreferences
{
	private static String KEY_SECTION_FILES_EXPANDED = "LIGEIRO_SECTION_FILES_EXPANDED"; //$NON-NLS-1$
	private static String KEY_SECTION_CONTROL_EXPANDED = "LIGEIRO_SECTION_CONTROL_EXPANDED"; //$NON-NLS-1$
	private static String KEY_SECTION_RESULTS_EXPANDED = "LIGEIRO_SECTION_RESULTS_EXPANDED"; //$NON-NLS-1$

	private static String KEY_STATISTIC_FILES_CSV = "LIGEIRO_STATISTIC_FILES_CSV"; //$NON-NLS-1$
	private static String KEY_DEPENDENCY_FILES_CSV = "LIGEIRO_DEPENDENCY_FILES_CSV"; //$NON-NLS-1$
	private static String KEY_FPA_CONFIGURATION_FILE = "LIGEIRO_FPA_CONFIGURATION_FILE"; //$NON-NLS-1$

	private static IEclipsePreferences getPreferences()
	{
		IEclipsePreferences preferences = LigeiroPlugin.getPreferences();

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
	 * Clears all fields from preferences.
	 */
	public static void clearFields()
	{
		IEclipsePreferences preferences = getPreferences();

		try
		{
			preferences.remove(KEY_STATISTIC_FILES_CSV);
			preferences.remove(KEY_DEPENDENCY_FILES_CSV);
			preferences.remove(KEY_FPA_CONFIGURATION_FILE);

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
		put(KEY_STATISTIC_FILES_CSV, Util.getInputFileTableCSV(table));
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
		put(KEY_DEPENDENCY_FILES_CSV, Util.getInputFileTableCSV(table));
	}

	/**
	 * @return the configuration file path
	 */
	public static String getFPAConfigurationFile()
	{
		return getPreferences().get(KEY_FPA_CONFIGURATION_FILE, ""); //$NON-NLS-1$
	}

	/**
	 * @param path The path to be setted
	 */
	public static void setFPAConfigurationFile(String path)
	{
		put(KEY_FPA_CONFIGURATION_FILE, path);
	}

	/**
	 * @return if the files section is expanded
	 */
	public static boolean isSectionFilesExpanded()
	{
		return getPreferences().getBoolean(KEY_SECTION_FILES_EXPANDED, true);
	}

	/**
	 * @param value The value to be setted
	 */
	public static void setSectionFilesExpanded(boolean value)
	{
		put(KEY_SECTION_FILES_EXPANDED, value);
	}

	/**
	 * @return if the control section is expanded
	 */
	public static boolean isSectionControlExpanded()
	{
		return getPreferences().getBoolean(KEY_SECTION_CONTROL_EXPANDED, true);
	}

	/**
	 * @param value The value to be setted
	 */
	public static void setSectionControlExpanded(boolean value)
	{
		put(KEY_SECTION_CONTROL_EXPANDED, value);
	}

	/**
	 * @return if the results section is expanded
	 */
	public static boolean isSectionResultsExpanded()
	{
		return getPreferences().getBoolean(KEY_SECTION_RESULTS_EXPANDED, true);
	}

	/**
	 * @param value The value to be setted
	 */
	public static void setSectionResultsExpanded(boolean value)
	{
		put(KEY_SECTION_RESULTS_EXPANDED, value);
	}

	// GENERAL

	/**
	 * Puts a <code>String</code> value.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	private static void put(String key, String value)
	{
		IEclipsePreferences preferences = getPreferences();

		preferences.put(key, value);

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
	 * Puts a <code>boolean</code> value.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	private static void put(String key, boolean value)
	{
		IEclipsePreferences preferences = getPreferences();

		preferences.putBoolean(key, value);

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
