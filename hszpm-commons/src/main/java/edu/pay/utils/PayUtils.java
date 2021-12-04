package edu.pay.utils;

import edu.cnp.CnpParts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class PayUtils {

	/**
	 * Visszatéríti az össztranzakciók számát.
	 * @param mapOfCustomers
	 * 											tranzakciók
	 * @return összfizetések száma Integer
	 */
	public static int getTotalTransactionNumber(Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return mapOfCustomers.keySet().size();
	}

	/**
	 * Visszatéríti a tranzakciók összegét.
	 * @param mapOfCustomers
	 * 											tranzakciók
	 * @return BigDecimal
	 */
	public static BigDecimal sumTransactions(Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		return mapOfCustomers.keySet().stream().map(customer -> mapOfCustomers.get(customer).stream().reduce(BigDecimal.ZERO, BigDecimal::add))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
