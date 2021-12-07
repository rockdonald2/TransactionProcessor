package edu.cnp;

import edu.cnp.exception.cnp.*;
import edu.cnp.parts.*;
import edu.cnp.utils.CnpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;

/*
TODO
* Jobb lenne, ha egy ValidatorPool-t hoznek letre, mert elmeletben a jelenlegi static es synchronized megoldassal egyszerre egyetlen kliens szolgalhatnank ki,
* hiszen meghivodik a ProcessorUtils getCustomers metodusa, ami beolvassa az allomanyt, stb, majd az hivja a validatort,
* az egesz allomany beolvasas megkellene tortenjen, mielott egy ujabb kliensre ternenk,
* valojaban ezen a ponton tul mar mukodik a parhuzamossag, azonban itt nem
* */

public class CnpValidator {

  private enum CnpPart {

    SEX_AND_CENTURY_CODE(0, 1),
    BIRTH_DATE(1, 7),
    BIRTH_COUNTY_CODE(7, 9),
    ORDER_NUMBER(9, 12),
    CONTROL_CODE(12, 13);

    private final int startIndex;
    private final int endIndex;

    CnpPart(final int startIndex, final int endIndex) {
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    /**
     * Kiszedi a CNP komponenseit
     *
     * @param cnp Ellenőrízendő CNP
     * @return megfelelő komponens
     */
    public String extractFrom(final String cnp) {
      return cnp.trim().substring(startIndex, endIndex);
    }

  }

  // ! Nem szükséges a szinkronizáltság, thread-local változók jelennek csak meg
  public static CnpParts validate(final String cnp) throws CnpException {
    validateLength(cnp);
    validateFormat(cnp);
    var sex = getSex(cnp);
    var foreigner = getForeignStatus(cnp);
    var birthDate = getBirthDate(cnp);
    var birthCounty = getBirthCounty(cnp);
    var orderNumber = getOrderNumber(cnp);
    validateControlNumber(cnp);

    return new CnpParts.Builder().withCnp(cnp).ofSex(sex).isForeigner(foreigner).withBirth(birthDate).withOrder(orderNumber).inCounty(birthCounty).build();
  }

  /**
   * Ellenőrzi, hogy a CNP csak számokból áll-e.
   *
   * @param cnp ellenőrízendő CNP
   * @throws CnpException ha a CNP érvénytelen
   */
  private static void validateFormat(final String cnp) throws CnpException {
    if (!StringUtils.isNumeric(cnp)) {
      throw new CnpFormatException("Non-digit character appears in CNP");
    }
  }

  /**
   * Ellenőrzi, hogy a CNP 13 számjegyből áll-e.
   *
   * @param cnp ellenőrízendő CNP
   * @throws CnpException ha a CNP érvénytelen
   */
  private static void validateLength(final String cnp) throws CnpException {
    if (cnp.length() < CnpUtils.CNP_LENGTH) {
      throw new CnpFormatException("Missing digits from CNP");
    } else if (cnp.length() > CnpUtils.CNP_LENGTH) {
      throw new CnpFormatException("Excess digits in CNP");
    }
  }

  /**
   * Ellenőrzi és visszatéríti a személy nemét.
   *
   * @param cnp cnp
   * @return nem
   * @throws CnpException ha a CNP érvénytelen
   */
  private static Sex getSex(final String cnp) throws CnpException {
    try {
      return Sex.getByCode(CnpPart.SEX_AND_CENTURY_CODE.extractFrom(cnp));
    } catch (CnpException e) {
      throw e;
    }
  }

  /**
   * Ellenőrzi és visszatéríti, hogy rezidens a személy.
   *
   * @param cnp cnp
   * @return logikai érték, külföldi-e
   * @throws CnpException ha a CNP érvénytelen
   */
  private static boolean getForeignStatus(final String cnp) throws CnpException {
    try {
      return switch (Byte.parseByte(CnpPart.SEX_AND_CENTURY_CODE.extractFrom(cnp))) {
        case 7, 8, 9 -> true;
        case 1, 2, 3, 4, 5, 6 -> false;
        default -> throw new InvalidSexException("Invalid sex code");
      };
    } catch (Exception e) {
      throw new InvalidSexException("Invalid sex code");
    }
  }

  /**
   * Ellenőrzi és visszatéríti a személy születési dátumát.
   *
   * @param cnp cnp
   * @return születési dátum CalDate példányként
   * @throws CnpException ha a CNP érvénytelen
   */
  private static CalDate getBirthDate(final String cnp) throws CnpException {
    final String dateString = CnpPart.BIRTH_DATE.extractFrom(cnp);

    var centuryString = getCentury(cnp);
    if (centuryString.isEmpty()) {
      centuryString = CnpUtils.guessForeignerCentury(dateString.substring(0, 2));
    }

    final var returnDate = CnpUtils.composeDate(dateString.toCharArray(), centuryString);

    if (!GenericValidator.isDate(returnDate, "yyyy-MM-dd", true)) {
      throw new InvalidBirthDateException("Invalid birth date");
    }

    return new CalDate.Builder().ofDate(returnDate).build();
  }

  /**
   * Ellenőrzi és visszatéríti az évszázadot, amelyben a személy született.
   *
   * @param cnp a CNP alkotóeleme, amely a születési év kikövetkeztetésére szolgál
   * @return évszázad
   * @throws CnpException ha a CNP érvénytelen
   */
  private static String getCentury(final String cnp) throws CnpException {
    try {
      return switch (Byte.parseByte(CnpPart.SEX_AND_CENTURY_CODE.extractFrom(cnp))) {
        case 1, 2 -> "19";
        case 3, 4 -> "18";
        case 5, 6 -> "20";
        case 7, 8, 9 -> "";
        default -> throw new InvalidCenturyException("Invalid sex/century code");
      };
    } catch (Exception e) {
      throw new InvalidCenturyException("Invalid sex/century code");
    }
  }

  /**
   * Ellenőrzi és visszatéríti a születési megyét.
   *
   * @param cnp cnp
   * @return County
   * @throws CnpException ha a CNP érvénytelen
   */
  private static County getBirthCounty(final String cnp) throws CnpException {
    try {
      return County.getByCode(CnpPart.BIRTH_COUNTY_CODE.extractFrom(cnp));
    } catch (CnpException e) {
      throw e;
    }
  }


  /**
   * Visszatéríti a személy sorszámát.
   *
   * @param cnp CNP alkotóeleme, amely a sorszám kikövetkeztetésére szolgál
   * @return sorszám
   * @throws CnpException ha a CNP érvénytelen
   */
  private static Short getOrderNumber(final String cnp) throws CnpException {
    try {
      return Short.parseShort(CnpPart.ORDER_NUMBER.extractFrom(cnp));
    } catch (Exception e) {
      throw new InvalidOrderNumberException("Invalid order number");
    }
  }

  /**
   * Ellenőrzi a CNP kontrollszámát.
   *
   * @param cnp CNP
   * @throws CnpException ha a CNP érvénytelen
   */
  private static void validateControlNumber(final String cnp) throws CnpException {
    final var cnpDigitArr = CnpUtils.createByteCnpFromString(cnp);
    int digitSum = CnpUtils.calculateWeightedSum(cnpDigitArr);
    byte controlNumber = cnpDigitArr[cnpDigitArr.length - 1];

    if (((digitSum % 11 == 10) && controlNumber != 1)) {
      throw new InvalidControlNumberException("Invalid control number");
    }

    if (controlNumber != (digitSum % 11) && (digitSum % 11 != 10)) {
      throw new InvalidControlNumberException("Invalid control number");
    }
  }

}