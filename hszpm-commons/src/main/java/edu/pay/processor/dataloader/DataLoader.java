package edu.pay.processor.dataloader;

import java.io.InputStream;
import java.util.List;

public interface DataLoader {

	List<String[]> loadData(InputStream is);

}
