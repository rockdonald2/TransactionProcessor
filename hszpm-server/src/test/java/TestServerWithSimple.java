import edu.cnp.parts.CnpParts;
import edu.network.FileTransfer;
import edu.server.Server;
import edu.server.ServerFactory;
import edu.server.network.NetworkClientHandle;
import edu.utils.PropertyProvider;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestServerWithSimple {

	private static JSONObject output;
	private static ServerSocket server;

	@BeforeClass
	public static void startServer() throws IOException, InterruptedException, ClassNotFoundException {
		server = new ServerSocket(Integer.parseInt(PropertyProvider.getServerProperty("port")));
		Socket s = new Socket("localhost", Integer.parseInt(PropertyProvider.getServerProperty("port")));

		final ExecutorService exService = Executors.newSingleThreadExecutor();
		exService.execute(() -> {
			try {
				new NetworkClientHandle(server.accept()).start();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});

		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		PropertyProvider.setClientProperty("processor.type", "simple", false);
		PropertyProvider.setClientProperty("input.format", "csv", false);
		PropertyProvider.setClientProperty("output.format", "json", false);
		File input = new File(TestServerWithSimple.class.getResource("testData.csv").getPath());
		out.writeObject(new FileTransfer(Files.readAllBytes(Path.of(input.getPath()))));
		out.flush();

		File output = new File(TestServerWithSimple.class.getResource("testResult.json").getPath());
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		FileTransfer inFt = (FileTransfer) in.readObject();
		Files.write(Path.of(output.getPath()), inFt.getData());
		Map<CnpParts, List<BigDecimal>> customers = (Map<CnpParts, List<BigDecimal>>) in.readObject();
		TestServerWithSimple.output = new JSONObject(IOUtils.toString(new FileInputStream(output), StandardCharsets.UTF_8));

		s.close();
		out.close();
		in.close();
		exService.awaitTermination(10, TimeUnit.SECONDS);
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
