package ru.serykhd.ipinfo;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringEncoder;
import lombok.NonNull;
import ru.serykhd.common.Validator;
import ru.serykhd.common.integer.RangeUtils;
import ru.serykhd.common.net.address.utils.IPAddressUtils;
import ru.serykhd.http.HttpClient;
import ru.serykhd.http.callback.HttpCallback;
import ru.serykhd.http.exception.HttpBreakException;
import ru.serykhd.http.exception.HttpIOException;
import ru.serykhd.http.mime.MimeType;
import ru.serykhd.http.requst.WHttpRequestBuilder;
import ru.serykhd.http.response.WHttpRequstResponse;
import ru.serykhd.http.utils.RequestUtils;
import ru.serykhd.http.utils.RequstResponseUtils;
import ru.serykhd.ipinfo.error.IPInfoError;
import ru.serykhd.ipinfo.response.IPInfoResponse;

import java.util.List;
import java.util.Map;

public class IPInfo {

    // 50k req per 30d
    // first 7d all futures

    // https://www.tempmail.us.com/russian/

    private final HttpClient httpClient;

    private final String token;

    public IPInfo() {
        this("bd57118fa81f01");
    }

    public IPInfo(@NonNull String token) {
        this.httpClient = new HttpClient();
        this.token = token;
    }

    public void execute(@NonNull String ip, @NonNull HttpCallback<IPInfoResponse> response) {
        IPAddressUtils.checkValid(ip);

        QueryStringEncoder query = new QueryStringEncoder("http://ipinfo.io/" + ip);

        query.addParam("token", token);

        WHttpRequestBuilder req = httpClient.create(query.toString(), new HttpCallback<>() {
            @Override
            public void done(WHttpRequstResponse responseReq) {
                RequstResponseUtils.checkOk(responseReq, response);

                if (responseReq.getContent().toString().contains("\"status\": ")) {
                    try {
                        IPInfoError error = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), IPInfoError.class);

                        response.cause(new HttpIOException(String.format("failed on fetch | %s", RequestUtils.getGson().fromJson(RequestUtils.getGson().toJson(error), IPInfoError.class))));
                        throw HttpBreakException.INSTANCE;
                    } catch (Throwable ignore) {}
                }


                IPInfoResponse ipresponse = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), IPInfoResponse.class);

                response.done(ipresponse);
            }

            @Override
            public void cause(Throwable throwable) {
                response.cause(throwable);
            }
        });

        req.acceptType(MimeType.JSON);

        httpClient.execute(req.createRequst());
    }

    public void execute(@NonNull List<String> ips, @NonNull HttpCallback<List<IPInfoResponse>> response) {
        Validator.isTrue(RangeUtils.exclusiveBetween(1, 1000, ips.size()));

        IPAddressUtils.checkValid(ips);


        QueryStringEncoder query = new QueryStringEncoder("http://ipinfo.io/batch");

        query.addParam("token", token);

        WHttpRequestBuilder req = httpClient.create(query.toString(), new HttpCallback<WHttpRequstResponse>() {
            @Override
            public void done(WHttpRequstResponse responseReq) {
                RequstResponseUtils.checkOk(responseReq, response);

                if (responseReq.getContent().toString().contains("\"status\": ")) {
                    try {
                        Map<String, IPInfoError> errorMap = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), new TypeToken<Map<String, IPInfoError>>() {}.getType());

                        response.cause(new HttpIOException(String.format("failed on fetch | %s", responseReq.getContent().toString())));
                        throw HttpBreakException.INSTANCE;
                    } catch (Throwable ignore) {}
                }

                Map<String, IPInfoResponse> ipresponse = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), new TypeToken<Map<String, IPInfoResponse>>() {}.getType());

                response.done(ipresponse.values().stream().toList());
            }

            @Override
            public void cause(Throwable throwable) {
                response.cause(throwable);
            }
        });

        req.requestMethod(HttpMethod.POST);
        req.setBytes(RequestUtils.getGson().toJson(ips).getBytes());

        req.acceptType(MimeType.JSON);
        req.contentType(MimeType.JSON);

        httpClient.execute(req.createRequst());
    }
}
