package br.ufrj.coppe.pinel.ligeiro.provider;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.coppe.pinel.ligeiro.data.Result;

/**
 * @author Roque Pinel
 *
 */
public class ResultsTableProvider
{
	private List<Result> results;

	/**
	 * Default constructor.
	 */
	public ResultsTableProvider()
	{
		results = new ArrayList<Result>();
	}

	/**
	 * @return the results
	 */
	public List<Result> getResults()
	{
		return results;
	}
}
