import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainMethods {

    private static final String BEARER_TOKEN = "v2.local.4jDUqTFzmwuJ0y4_fH5S629IcHsM-S-HUeSyzkrIs3Rt4c10-N0Mv4-xHOwQuxLlbOpG4-BAm5Wfu4A1M4aQTmEk-Bh3-XDV_jW7SaGBgfuLsPwuN2nA2EXW9oUQYrtBXAPKDpXAgWvlKrwrM6xT-MhhiDqeHzokxTLk7oraM_gaq-anKD64yanaLbQvqjUSLx03VvfKwFEIIDn1b1q5KIJ6sA1bTJtNfTx0TJqo8GyEWhnvaqTvzbIPQsRDMIhB4q0.bnVsbA";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://192.168.1.193:8080";

        // Increase timeout settings to avoid connection issues
        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 30000)  // 30 seconds socket timeout
                        .setParam("http.connection.timeout", 30000)); // 30 seconds connection timeout
    }

    @Test
    public void testCreateGlobalMedia() {
        // Prepare test files
        List<File> testFiles = prepareTestFiles("C://2091_1653477126.jpg", "C://2091_1653477126.jpg");

        // Ensure at least one valid file exists
        if (testFiles.isEmpty()) {
            System.err.println("No valid files found for upload.");
            return; // Exit test if no files found
        }

        // Create request specification
        RequestSpecification request = RestAssured.given()
                .auth().oauth2(BEARER_TOKEN)  // Set authorization token
                .contentType("multipart/form-data")
                .multiPart("media_type", "1")
                .multiPart("gallery_type", "Main")
                .multiPart("entity_id", "187")
                .multiPart("entity_type_id", "3");

        // Add files dynamically
        for (File file : testFiles) {
            request.multiPart(
                    new MultiPartSpecBuilder(file)
                            .controlName("files")
                            .fileName(file.getName())
                            .mimeType("image/jpeg")
                            .build());
        }

        // Send request, verify response, and log all details
        request.when()
                .post("/api/dashboard/createGlobalMedia")
                .then()
                .statusCode(200)  // Adjust expected status code as needed
                .log().all(); // Log full response details
    }

    // Helper method to prepare test files from provided file paths
    private List<File> prepareTestFiles(String... filePaths) {
        List<File> files = new ArrayList<>();
        for (String path : filePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                files.add(file);
            } else {
                System.err.println("File not found or invalid: " + path);
            }
        }
        return files;
    }
}
