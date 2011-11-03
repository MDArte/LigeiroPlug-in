package br.ufrj.cos.pinel.ligeiro.plugin.data;

import java.io.File;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import br.ufrj.cos.pinel.ligeiro.plugin.messages.Messages;

/**
 * @author Roque Pinel
 *
 */
public class InputFile implements IPropertySource
{
	private static final String FILENAME_ID = "InputFile.filename"; //$NON-NLS-1$
	private static final String PATH_ID = "InputFile.path"; //$NON-NLS-1$
	private static final IPropertyDescriptor[] DESCRIPTORS =
		{
			new PropertyDescriptor(FILENAME_ID, Messages.LigeiroView_files_properties_filename),
			new PropertyDescriptor(PATH_ID, Messages.LigeiroView_files_properties_path)
		};

	private String path;

	/**
	 * Default constructor.
	 */
	public InputFile()
	{
		// empty
	}

	/**
	 * @param path the file path.
	 */
	public InputFile(String path)
	{
		setPath(path);
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return the FileName
	 */
	public String getFileName()
	{
		return new File(path).getName();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getFileName();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj instanceof InputFile)
		{
			InputFile inputFile = (InputFile) obj;
			if (getPath().equals(inputFile.getPath()))
				return true;
		}

		return false;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue()
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return DESCRIPTORS;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		try
		{
			if(PATH_ID.equals(id))
			{
				return getPath();
			}
			else if(FILENAME_ID.equals(id))
			{
				return getFileName();
			}
		}
		catch(Exception e)
		{
			// ignoring
		}

		return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id)
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id)
	{
		// empty
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value)
	{
		// empty
	}
}
