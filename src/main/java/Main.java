import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=OoU4GEUz1cCrheoZOmjnMaF69QtVhR4vtLf7oxwW";
    public static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        ) {
            HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                List<Post> posts = objectMapper.readValue(response.getEntity().getContent().readAllBytes(), new TypeReference<>() {});
                posts.stream().forEach(System.out::println);

                //Работа с картинкой
                String uriForDownloadFile = posts.stream().map(post -> post.getUrl()).collect(Collectors.joining());
                URL url = new URL(uriForDownloadFile);
                String[] stringForNameFile = uriForDownloadFile.split("/");//Используется последний элемент массива
                try (InputStream in = new BufferedInputStream(url.openStream());
                     ByteArrayOutputStream out = new ByteArrayOutputStream()
                ) {
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                    byte[] answer = out.toByteArray();
                    FileOutputStream fos = new FileOutputStream(stringForNameFile[stringForNameFile.length - 1]);
                    fos.write(answer);
                    fos.close();
                }
            }
        }
    }
}
