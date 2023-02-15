package ru.serykhd.ipapi;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringEncoder;
import lombok.NonNull;
import ru.serykhd.common.Validator;
import ru.serykhd.common.integer.RangeUtils;
import ru.serykhd.common.net.address.utils.IPAddressUtils;
import ru.serykhd.common.primitive.BooleanReverse;
import ru.serykhd.common.ratelimiter.impl.RateLimiter;
import ru.serykhd.common.thread.SThreadFactory;
import ru.serykhd.http.HttpClient;
import ru.serykhd.http.callback.HttpCallback;
import ru.serykhd.http.exception.HttpIOException;
import ru.serykhd.http.exception.HttpTooManyRequestsExeption;
import ru.serykhd.http.mime.MimeType;
import ru.serykhd.http.requst.WHttpRequestBuilder;
import ru.serykhd.http.response.WHttpRequstResponse;
import ru.serykhd.http.utils.RequestUtils;
import ru.serykhd.http.utils.RequstResponseUtils;
import ru.serykhd.ipapi.error.IPAPIError;
import ru.serykhd.ipapi.fields.FieldType;
import ru.serykhd.ipapi.locale.Localization;
import ru.serykhd.ipapi.response.IPResponse;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

public class IPAPI extends HttpClient {

	private final int weight;
	private final Localization locale;

	private final RateLimiter singleLimiter = new RateLimiter(45, Duration.ofMinutes(1));
	private final RateLimiter batchLimiter = new RateLimiter(15, Duration.ofMinutes(1));
	
	public IPAPI(@NonNull FieldType... types) {
		this(Localization.Russian, types);
	}
	
	public IPAPI(@NonNull Localization locale, @NonNull FieldType... types) {
		super(new SThreadFactory("IPAPI HttpClient Group"));

		this.weight = Stream.concat(Stream.of(types), Stream.of(FieldType.QUERY, FieldType.STATUS)).distinct().mapToInt(FieldType::getWeight).sum();
		this.locale = locale;
	}

	public void execute(@NonNull String ip, @NonNull HttpCallback<IPResponse> response) {
		IPAddressUtils.checkValid(ip);

		if (isDirect()) {
			if (BooleanReverse.reverse(singleLimiter.tryAcquire())) {
				throw new HttpTooManyRequestsExeption();
			}
		}

		QueryStringEncoder query = new QueryStringEncoder("http://ip-api.com/json/" + ip);
		
		query.addParam("fields", Integer.toString(weight));
		
		if (locale != Localization.English) {
			query.addParam("lang", locale.getLocale());
		}

		WHttpRequestBuilder req = create(query.toString(), new HttpCallback<>() {
			@Override
			public void done(WHttpRequstResponse responseReq) {
				RequstResponseUtils.checkOk(responseReq, response);

				//
				/*
				RequestsLimit requestsLimit = new RequestsLimit(
						responseReq.getResponse().headers().getInt("X-Rl"),
						responseReq.getResponse().headers().getInt("X-Ttl"));

				*/
				IPResponse ipresponse = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), IPResponse.class);

				//	ipresponse.setRequestsLimit(requestsLimit);


				if ("fail".equals(ipresponse.getStatus())) {
					response.cause(new HttpIOException(String.format("failed on fetch | %s", RequestUtils.getGson().fromJson(RequestUtils.getGson().toJson(ipresponse), IPAPIError.class))));
					return;
				}

				response.done(ipresponse);
			}

			@Override
			public void cause(Throwable throwable) {
				response.cause(throwable);
			}
		});

		req.acceptType(MimeType.JSON);

		execute(req.createRequst());
	}
	
	public void execute(@NonNull List<String> ips, @NonNull HttpCallback<List<IPResponse>> response) {
		Validator.isTrue(RangeUtils.exclusiveBetween(1, 100, ips.size()));

		IPAddressUtils.checkValid(ips);

		if (isDirect()) {
			if (BooleanReverse.reverse(batchLimiter.tryAcquire())) {
				throw new HttpTooManyRequestsExeption();
			}
		}
		
		QueryStringEncoder query = new QueryStringEncoder("http://ip-api.com/batch");
		
		query.addParam("fields", Integer.toString(weight));
		
		if (locale != Localization.English) {
			query.addParam("lang", locale.getLocale());
		}

		WHttpRequestBuilder req = create(query.toString(), new HttpCallback<WHttpRequstResponse>() {
			@Override
			public void done(WHttpRequstResponse responseReq) {
				RequstResponseUtils.checkOk(responseReq, response);

				//
				/*
				RequestsLimit requestsLimit = new RequestsLimit(
						responseReq.getResponse().headers().getInt("X-Rl"),
						responseReq.getResponse().headers().getInt("X-Ttl"));

				 */
				//
				//IPResponse ipresponse = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), IPResponse.class);

				// 21
				List<IPResponse> ipresponse1 = RequestUtils.getGson().fromJson(responseReq.getContent().toString(), new TypeToken<List<IPResponse>>() {}.getType());

				for (IPResponse ipresponse : ipresponse1) {
				//	ipresponse.setRequestsLimit(requestsLimit);

					if ("fail".equals(ipresponse.getStatus())) {
						response.cause(new HttpIOException(String.format("failed on fetch | %s", RequestUtils.getGson().fromJson(RequestUtils.getGson().toJson(ipresponse), IPAPIError.class))));
						return;
					}
				}

				response.done(ipresponse1);
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
		
		execute(req.createRequst());
	}
}
