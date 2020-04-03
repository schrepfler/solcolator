package solcolator.io.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import solcolator.io.api.ISolcolatorResultsWriter;
import solcolator.percolator.common.FileUtils;

/**
 * This writer is designed to write percolator results to file
 * This writer can be used for testing and integration purposes
 * Parameters which must be provided: file path and fields list
 * 
 * Config for example:
 	<str name="filePath">C:\\Solrs\\solr-6.5.1\\solr-6.5.1\\example\\cloud\\percolator\\out\\out.csv</str>
	<str name="fileFl">item_id,Item_Kind_s</str>
			
 * @author 
 *
 */
public class SolcolatorResultsFileWriter implements ISolcolatorResultsWriter, AutoCloseable {
	private static final String FILE_PATH = "filePath";
	private static final String FILE_FL = "fileFl";
	private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private List<String> fl;
	private BufferedWriter bw;
	
	@Override
	public void init(NamedList<?> outputConfig) throws IOException {
		this.fl = Arrays.asList(((String) outputConfig.get(FILE_FL)).split(","));
		String filePath = (String) outputConfig.get(FILE_PATH);
		
		if (!FileUtils.fileExist(filePath)) {
			try {
				new File(filePath).createNewFile();
			} catch (IOException e) {
				throw new IOException(String.format("File %s doesn't exist and can't be created", filePath));
			}
		}
				
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false), StandardCharsets.UTF_8));
	}
	
	@Override
	public void writeSolcolatorResults(Map<String, SolrInputDocument> docs) throws IOException {
		bw.append(String.format("%s\n", gson.toJson(docs)));
		bw.flush();
	}
	
	@Override
	public List<String> getFl() {
		return fl;
	}

	@Override
	public void close() throws IOException {
		if (bw != null) {
			bw.close();
		}
	}
}