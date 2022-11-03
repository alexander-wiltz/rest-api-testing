import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class TestingGitHubAPI {

    /***
     * Testing RESTful APIs with JAVA
     * ref: https://docs.github.com/en/rest
     * example from: https://www.baeldung.com/integration-testing-a-rest-api
     * another help from: https://www.baeldung.com/java-http-request
     *
     * @author Alexander Wiltz
     * @date 02-11-2022
     *
     */

    @Test
    public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived() throws IOException {
        String name = RandomStringUtils.randomAlphabetic(8);
        HttpUriRequest request = new HttpGet("https://api.github.com/users/" + name);

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertThat(httpResponse.getCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void givenUserExists_whenUserInfoIsRetrieved_then200IsReceived() throws IOException {
        String name = "alexander-wiltz";
        HttpUriRequest request = new HttpGet("https://api.github.com/users/" + name);

        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        assertThat(httpResponse.getCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() throws IOException {
        String expected = "application/json; charset=utf-8";

        String name = "alexander-wiltz";
        URL url = new URL("https://api.github.com/users/" + name);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertThat(con.getHeaderField("Content-Type")).isEqualTo(expected);
    }

    @Test
    public void givenUserExists_whenUserInformationIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {
        String name = "alexander-wiltz";
        URL url = new URL("https://api.github.com/users/" + name);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine.trim());
        }
        in.close();

        JsonObject convertedObject = new Gson().fromJson(content.toString(), JsonObject.class);

        assertThat(convertedObject.isJsonObject()).isEqualTo(true);
    }

    @Test
    public void apiRateLimitExceeded() throws IOException {
        // just true, when access limit reached
        String name = "alexander-wiltz";
        URL url = new URL("https://api.github.com/users/" + name);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertThat(con.getResponseCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
    }
}