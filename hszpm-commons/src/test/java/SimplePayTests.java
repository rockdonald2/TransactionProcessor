import edu.pay.metrics.SimplePayMetrics;
import edu.pay.processor.PayProcessor;
import edu.pay.processor.PayProcessorFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;

public class SimplePayTests {

	private static final PayProcessor testProcessor;
	private static SimplePayMetrics testMetrics = null;

	static {
		PayProcessorFactory processorFactory = new PayProcessorFactory();
		testProcessor = processorFactory.getProcessor("simple");
		try {
			testProcessor.process(new FileInputStream(SimplePayTests.class.getResource("/testData.csv").getPath()), null);
			testMetrics = (SimplePayMetrics) testProcessor.getProcessedMetrics();
		} catch (FileNotFoundException | NullPointerException ignored) {}
	}

	public static class TestPaymentMinors {

		@Test
		public void correctAnswer() {
			Assert.assertEquals(2, testMetrics.paymentsByMinors());
		}

	}

	public static class TestTotalAmountCapitalCity {

		@Test
		public void correctAnswer() {
			Assert.assertEquals(new BigDecimal("199.68"), testMetrics.totalAmountCapitalCity());
		}

	}

	public static class TestForeigners {

		@Test
		public void correctAnswer() {
			Assert.assertEquals(0, testMetrics.foreigners());
		}

	}

	public static class TestSmallPayments {

		@Test
		public void correctAnswer() {
			Assert.assertEquals(5, testMetrics.smallPayments());
		}

	}

	public static class TestBigPayments {

		@Test
		public void correctAnswer() {
			Assert.assertEquals(1, testMetrics.bigPayments());
		}

	}

	public static class TestAverage {

		@Test
		public void correctAnswer() {
			Assert.assertEquals(new BigDecimal("1339.72"), testMetrics.averagePaymentAmount());
		}

	}

}
