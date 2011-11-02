package br.ufrj.cos.pinel.ligeiro.plugin.comparator;


/**
 * @author Roque Pinel
 *
 */
public interface ITableComparator
{
	/**
	 * @return
	 */
	public int getDirection();

	/**
	 * @param column
	 */
	public void setColumn(int column);
}
