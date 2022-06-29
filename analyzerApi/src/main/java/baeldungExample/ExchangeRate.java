package baeldungExample;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ExchangeRate {

	static ServiceLoader<ExchangeRateProvider> loader = ServiceLoader.load(ExchangeRateProvider.class);

	static public Iterator<ExchangeRateProvider> providers(boolean refresh) {
		if (refresh) {
			loader.reload();
		}
		return loader.iterator();
	}
}
