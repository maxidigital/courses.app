package main.server;

import com.google.api.services.sheets.v4.SheetsScopes;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GoogleAuthUrlBuilder
{

    private static final String AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private final String clientId;
    private final String redirectUri;
    private final List<String> scopes;

    /*public static void main(String[] args) {
        GoogleAuthUrlBuilder aaa = new GoogleAuthUrlBuilder();
        System.out.println(aaa.build());
    }*/

    public GoogleAuthUrlBuilder() {
        this.clientId = "295879412418-uvv9mcjum2mfc3p9i2aeid0qjg4fgsv4.apps.googleusercontent.com";
        this.redirectUri = "http://16.171.159.158:2002/";
        this.scopes = new ArrayList<>(SheetsScopes.all());
    }

    public GoogleAuthUrlBuilder(String clientId, String redirectUri, List<String> scopes) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
    }

    public String build() {
        String scopeString = scopes.stream()
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                .collect(Collectors.joining(" "));

        String state = UUID.randomUUID().toString();

        return AUTH_URI + "?"
                + "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + scopeString
                + "&access_type=offline"
                + "&state=" + state;
    }
}
