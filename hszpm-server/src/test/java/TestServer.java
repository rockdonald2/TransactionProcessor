import edu.cnp.parts.CnpParts;
import edu.network.ClientHandle;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestServer {

	private static JSONObject output;
	private static ServerSocket server;

	@BeforeClass
	public static void startServer() throws IOException, InterruptedException, ClassNotFoundException {
		server = new ServerSocket(11111);
		Socket s = new Socket("localhost", 11111);

		final ExecutorService exService = Executors.newSingleThreadExecutor();
		exService.execute(() -> {
			try {
				new ClientHandle(server.accept()).start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});

		PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		// TODO: függetleníteni a beállított input és output formátumtól is
		out.println(TestServer.class.getResource("testData.csv").getPath());
		out.println(TestServer.class.getResource("testResult.json").getPath());
		out.flush();

		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		Map<CnpParts, List<BigDecimal>> customers = (Map<CnpParts, List<BigDecimal>>) in.readObject();
		output = new JSONObject(IOUtils.toString(new FileInputStream(TestServer.class.getResource("testResult.json").getPath()), StandardCharsets.UTF_8));

		s.close();
		out.close();
		in.close();
		exService.awaitTermination(30, TimeUnit.SECONDS);
		exService.shutdownNow();
	}

	@AfterClass
	public static void shutdownServer() throws IOException {
		server.close();
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
