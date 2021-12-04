import edu.cnp.CnpValidator;
import edu.cnp.exception.cnp.*;
import edu.cnp.parts.CalDate;
import edu.cnp.parts.Sex;
import org.junit.Test;
import org.junit.Assert;

public class CnpTests {

	public static class TestBirthCounty {

		@Test
		public void case1() {
			Assert.assertEquals("BU", CnpValidator.validate("6000722407864").county().getAbrv());
		}

		@Test
		public void case2() {
			Assert.assertEquals("CL", CnpValidator.validate("6000722515408 ").county().getAbrv());
		}

		@Test
		public void case3() {
				Assert.assertEquals("AB", CnpValidator.validate(" 6000722018021").county().getAbrv());
		}

		@Test
		public void case4() {
			Assert.assertEquals("AB",CnpValidator.validate(" 2990722015531 ").county().getAbrv());
		}

		@Test
		public void case5() {
				Assert.assertEquals("DJ", CnpValidator.validate(" 2990722168675").county().getAbrv());
		}

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("2990722  8675"));
		}

		@Test
		public void caseThatThrows2() {
			Assert.assertThrows(CnpFormatException.class, () ->CnpValidator.validate("2990722aa8675"));
		}

		@Test
		public void caseThatThrows3() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("2990722??8675"));
		}

	}

	public static class TestBirthDate {

		@Test
		public void case1() {
				final var expected = new CalDate.Builder().ofDate("2000-07-22").build();
				Assert.assertEquals(expected.toString(), CnpValidator.validate("5000722194033").birthDate().toString());
		}

		@Test
		public void case2() {
			final var expected = new CalDate.Builder().ofDate("2000-5-22").build();
				Assert.assertEquals(expected.toString(),  CnpValidator.validate("6000522405695").birthDate().toString());
		}

		@Test
		public void case3() {
			final var expected = new CalDate.Builder().ofDate("1911-5-22").build();
				Assert.assertEquals(expected.toString(), CnpValidator.validate("1110522326297").birthDate().toString());
		}

		@Test
		public void case4() {
			final var expected = new CalDate.Builder().ofDate("2011-11-1").build();
				Assert.assertEquals(expected.toString(), CnpValidator.validate("6111101098548").birthDate().toString());
		}

		@Test
		public void case5() {
			final var expected = new CalDate.Builder().ofDate("1899-12-12").build();
				Assert.assertEquals(expected.toString(), CnpValidator.validate("3991212152713").birthDate().toString());
		}

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(InvalidBirthDateException.class, () -> CnpValidator.validate("2991322518675"));
		}

		@Test
		public void caseThatThrows2() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("xxxxxxxxxxxxx"));
		}

	}

	public static class TestCentury {

		@Test
		public void caseThatThrows3() {
			Assert.assertThrows(InvalidSexException.class, () -> CnpValidator.validate("0991204018102"));
		}

	}

	public static class TestControlNumber {

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("190122231a102"));
		}

		@Test
		public void caseThatThrows2() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("190122??31102"));
		}

		@Test
		public void caseThatThrows3() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("                "));
		}

		@Test
		public void caseThatThrows4() {
			Assert.assertThrows(InvalidControlNumberException.class, () -> CnpValidator.validate("6010919238862"));
		}

		@Test
		public void caseThatThrows5() {
			Assert.assertThrows(InvalidControlNumberException.class, () -> CnpValidator.validate("6080809255522"));
		}

		@Test
		public void caseThatThrows6() {
			Assert.assertThrows(InvalidControlNumberException.class, () -> CnpValidator.validate("6090809255522"));
		}

		@Test
		public void casesThatWontThrow() {
			var exception = Assert.assertThrows(CnpException.class, () -> {
				CnpValidator.validate("6180328015611");
				CnpValidator.validate("1611109181231");
				CnpValidator.validate("2740903216431");

				throw new InvalidControlNumberException("Didn't throw");
			});

			Assert.assertTrue(exception.getMessage().contains("Didn't throw"));
		}

	}

	public static class TestForeignStatus {

		@Test
		public void case1() {
				Assert.assertTrue(CnpValidator.validate("8990722165726").foreigner());
		}

		@Test
		public void case2() {
				Assert.assertTrue(CnpValidator.validate("7990722169481").foreigner());
		}

		@Test
		public void case3() {
				Assert.assertFalse(CnpValidator.validate("1990722169552").foreigner());
		}

		@Test
		public void case4() {
				Assert.assertFalse(CnpValidator.validate("1500722088076").foreigner());
		}

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("a500722088076"));
		}

		@Test
		public void caseThatThrows2() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("?500722088076"));
		}

	}

	public static class TestSex {

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(InvalidSexException.class, () -> CnpValidator.validate("0000722088076"));
		}

		@Test
		public void case2() {
				Assert.assertEquals(Sex.F, CnpValidator.validate("2500722089125").sex());
		}

		@Test
		public void case3() {
				Assert.assertEquals(Sex.M, CnpValidator.validate("1500722089262").sex());
		}

		@Test
		public void case4() {
				Assert.assertEquals(Sex.M, CnpValidator.validate("5000722085375").sex());
		}

		@Test
		public void case5() {
				Assert.assertEquals(Sex.M, CnpValidator.validate("7000722086784").sex());
		}

	}

	public static class TestValidateFormat {

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("190122231a102"));
		}

		@Test
		public void caseThatThrows2() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("19012A231a102"));
		}

		@Test
		public void caseThatThrows3() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("190B222311102"));
		}

		@Test
		public void caseThatThrows4() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("1F01222{1a102"));
		}

		@Test
		public void caseThatThrows5() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("1F01222{1a102"));
		}

		@Test
		public void caseThatThrows6() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("189c902023((8"));
		}

		@Test
		public void casesThatWontThrow() {
			var exception = Assert.assertThrows(CnpFormatException.class, () -> {
				CnpValidator.validate("1651208211659");
				CnpValidator.validate("2901103243809");
				CnpValidator.validate("1610322161751");

				throw new CnpFormatException("Didn't throw");
			});

			Assert.assertTrue(exception.getMessage().contains("Didn't throw"));
		}

	}

	public static class TestValidateLength {

		@Test
		public void caseThatThrows1() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("161032161751"));
		}

		@Test
		public void caseThatThrows2() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("1"));
		}

		@Test
		public void caseThatThrows3() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate(""));
		}

		@Test
		public void caseThatThrows4() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("4"));
		}

		@Test
		public void caseThatThrows5() {
			Assert.assertThrows(CnpFormatException.class, () -> CnpValidator.validate("413212312321312312313231"));
		}

		@Test
		public void casesThatWontThrow() {
			var exception = Assert.assertThrows(CnpFormatException.class, () -> {
				CnpValidator.validate("2710531138910");
				CnpValidator.validate("2650308082315");
				CnpValidator.validate("1740225335436");

				throw new CnpFormatException("Didn't throw");
			});

			Assert.assertTrue(exception.getMessage().contains("Didn't throw"));
		}

	}

}
