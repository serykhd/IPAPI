package ru.serykhd.ipinfo;

import com.google.gson.GsonBuilder;
import ru.serykhd.http.callback.HttpCallback;
import ru.serykhd.ipinfo.response.IPInfoResponse;

public class IPInfoTest {

    public static void main(String[] args) throws InterruptedException {
        IPInfo ipInfo = new IPInfo();

        /*
        ipInfo.execute(Arrays.asList("77.88.8.8", "77.88.8.5"), new HttpCallback<>() {
            @Override
            public void done(List<IPInfoResponse> ipInfoResponse) {
                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ipInfoResponse));


            }

            @Override
            public void cause(Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        */
        ipInfo.execute("77.88.8.81", new HttpCallback<>() {
            @Override
            public void done(IPInfoResponse ipInfoResponse) {
                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(ipInfoResponse));
            }

            @Override
            public void cause(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
