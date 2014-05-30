
package {package_name}.data.api.dummyapi;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;


@Module(complete = false, library = true)
public class DummyApiModule {
    private static final String API_URL = "http://someapiurl.com"; // TODO: Modify
    private static final String API_KEY = "your_key"; // TODO: Modify

    @Provides @Singleton @DummyApi Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(API_URL);
    }

    @Provides @Singleton @DummyApi Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides @Singleton @DummyApiKey String provideApiKey() {
        return API_KEY;
    }

    @Provides @Singleton @DummyApi RequestInterceptor provideRequestInterceptor(
            @DummyApiKey String apiKey) {
        return new DummyApiRequestInterceptor(apiKey);
    }

    @Provides @Singleton @DummyApi RestAdapter provideRestAdapter(@DummyApi Endpoint endpoint,
                                                              @DummyApi Client client, @DummyApi RequestInterceptor requestInterceptor, Gson gson) {
        return new RestAdapter.Builder() //
                .setClient(client) //
                .setEndpoint(endpoint) //
                .setConverter(new GsonConverter(gson)) //
                .setRequestInterceptor(requestInterceptor) //
                .setLog(new RestAdapter.Log() {
                    @Override public void log(String message) {
                        Timber.d(message);
                    }
                }) //
                .build();
    }

    @Provides @Singleton DummyApiService provideDummyApiService(@DummyApi RestAdapter restAdapter) {
        return restAdapter.create(DummyApiService.class);
    }

    @Provides @Singleton DummyApiDatabase provideDummyApiDatabase(DummyApiService service) {
        return new DummyApiDatabase(service);
    }
}

