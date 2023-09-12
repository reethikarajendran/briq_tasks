import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SFGovDataExport {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://data.sfgov.org/resource/p4e4-a5a7.json");

        try {
            HttpResponse response = httpClient.execute(httpGet);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(response.getEntity().getContent());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy-HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String csvFileName = "/HOME/briq/sfgov_" + timestamp + ".csv";
            FileWriter fileWriter = new FileWriter(csvFileName);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT
                    .withHeader("timestamp", "description", "is_roof"));
            for (JsonNode entry : jsonData) {
                String timestampField = entry.get("timestamp").asText();
                String description = entry.get("description").asText();
                boolean isRoof = description.toLowerCase().contains("roof");
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                Date parsedDate = inputDateFormat.parse(timestampField);
                String formattedTimestamp = outputDateFormat.format(parsedDate);
                csvPrinter.printRecord(formattedTimestamp, description, isRoof);
            }
            csvPrinter.close();
            System.out.println("Data exported to " + csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
