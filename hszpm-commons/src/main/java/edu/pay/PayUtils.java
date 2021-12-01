package edu.pay;

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
	static int getTotalTranzactionNumber(Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		int counter = 0;

		for (var customer : mapOfCustomers.keySet()) {
			for (var v : mapOfCustomers.get(customer)) {
				counter++;
			}
		}

		return counter;
	}

	/**
	 * Visszatéríti a tranzakciók összegét.
	 * @param mapOfCustomers
	 * 											tranzakciók
	 * @return BigDecimal
	 */
	static BigDecimal sumTranzactions(Map<CnpParts, ArrayList<BigDecimal>> mapOfCustomers) {
		var sum = BigDecimal.ZERO;

		for (var customer : mapOfCustomers.keySet()) {
			for (var v : mapOfCustomers.get(customer)) {
				sum = sum.add(v);
			}
		}

		return sum;
	}

}
