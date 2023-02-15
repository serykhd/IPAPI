package ru.serykhd.ipapi;

import com.google.gson.GsonBuilder;
import ru.serykhd.http.callback.HttpCallback;
import ru.serykhd.ipapi.fields.FieldType;
import ru.serykhd.ipapi.response.IPResponse;

import java.util.Arrays;
import java.util.List;

public class IPAPITest {

    public static void main(String[] args) throws InterruptedException {
        IPAPI api = new IPAPI(FieldType.values());

        api.execute("127.0.0.1", new HttpCallback<IPResponse>() {
            @Override
            public void done(IPResponse ipResponse) {
                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ipResponse));
            }

            @Override
            public void cause(Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        api.execute(Arrays.asList("2.2.2.2"), new HttpCallback<List<IPResponse>>() {
            @Override
            public void done(List<IPResponse> ipResponse) {
                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ipResponse));
            }

            @Override
            public void cause(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
