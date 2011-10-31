package br.ufrj.cos.pinel.ligeiro.plugin.data;

import java.io.File;

/**
 * @author Roque Pinel
 *
 */
public class InputFile
{
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
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return new File(path).getName();
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
}
