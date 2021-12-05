import edu.cnp.parts.CnpParts;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TestServer {

	private Map<CnpParts, List<BigDecimal>> customers;
	private JSONObject output;

	@Before
	public void setUp() throws IOException, ClassNotFoundException {
		Socket s = new Socket("localhost", 11111);

		PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		out.println(TestServer.class.getResource("testData.csv").getPath());
		out.println(TestServer.class.getResource("testResult.json").getPath());
		out.flush();

		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		customers = (Map<CnpParts, List<BigDecimal>>) in.readObject();
		output = new JSONObject(IOUtils.toString(new FileInputStream(TestServer.class.getResource("testResult.json").getPath()), StandardCharsets.UTF_8));

		s.close();
		out.close();
		in.close();
	}

	@Test
	public void testPaymentsByMinors() {
		Assert.assertEquals(2, output.getInt("paymentsByMinors"));
	}

	@Test
	public void testSmallPayments() {
		Assert.assertEquals(5, output.getInt("smallPayments"));
	}

	@Test
	public void testForeigners() {
		Assert.assertEquals(0, output.getInt("foreigners"));
	}

	@Test
	public void testBigPayments() {
		Assert.assertEquals(1, output.getInt("bigPayments"));
	}

	@Test
	public void testErrors() {
		Assert.assertEquals(new JSONArray().toString(), output.getJSONArray("errors").toString());
	}

	@Test
	public void testAveragePaymentAmount() {
		Assert.assertEquals(1339.72, output.getDouble("averagePaymentAmount"), 0.1);
	}

	@Test
	public void testAmountCapitalCity() {
		Assert.assertEquals(199.68, output.getDouble("totalAmountCapitalCity"), 0.1);
	}

}
