package br.ufrj.cos.pinel.ligeiro.plugin;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * @author Roque Pinel
 *
 */
public class LigeiroPlugin extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "LigeiroPlug-in"; //$NON-NLS-1$

	public static final String IMG_LIGEIRO = "icons/ligeiro.gif"; //$NON-NLS-1$
	public static final String IMG_RUN = "icons/run.gif"; //$NON-NLS-1$
	public static final String IMG_TRASH = "icons/trash.gif"; //$NON-NLS-1$

	// The shared instance
	private static LigeiroPlugin plugin;

	/**
	 * The constructor
	 */
	public LigeiroPlugin()
	{
		// empty
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LigeiroPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns the Eclipse Preferences for the plugin.
	 * 
	 * @return The preferences
	 */
	public static IEclipsePreferences getPreferences()
	{
		return new InstanceScope().getNode(PLUGIN_ID);
	}
}
