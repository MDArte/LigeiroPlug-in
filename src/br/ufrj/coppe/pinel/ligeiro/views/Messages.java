package br.ufrj.coppe.pinel.ligeiro.views;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Get the right message for the view.
 * 
 * @author Roque Pinel
 *
 */
public class Messages
{
	private static final String BUNDLE_NAME = "br.ufrj.coppe.pinel.ligeiro.views.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
		// empty
	}

	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
