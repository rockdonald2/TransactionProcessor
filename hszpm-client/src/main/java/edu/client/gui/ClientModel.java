package edu.client.gui;

import edu.cnp.parts.CnpParts;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientModel {

  private File input;
  private File output;
  private Map<CnpParts, List<BigDecimal>> customers;
  private JSONObject metrices;

  public File getInput() {
    return input;
  }

  public void setInput(File input) {
    this.input = input;
  }

  public File getOutput() {
    return output;
  }

  public void setOutput(File output) {
    this.output = output;
  }

  public void setCustomers(Map<CnpParts, List<BigDecimal>> mapOfCustomers) {
    this.customers = mapOfCustomers;
  }

  public Map<CnpParts, List<BigDecimal>> getCustomers() {
    return customers;
  }

  public JSONObject getMetrices() {
    return metrices;
  }

  public void setMetrices(JSONObject metrices) {
    this.metrices = metrices;
  }

}
