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

		Result result = new Result();
		result.setElement("ReadStudent");
		result.setRet_ftr(1);
		result.setDet(2);
		result.setComplexity("Low");
		result.setComplexityValue(4);
		results.add(result);
	}

	/**
	 * @return the results
	 */
	public List<Result> getResults()
	{
		System.out.println("FOCA");
		return results;
	}
}
