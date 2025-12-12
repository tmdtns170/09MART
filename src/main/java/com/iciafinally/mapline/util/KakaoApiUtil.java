package com.iciafinally.mapline.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iciafinally.kakao.util.KakaoDirections;

import lombok.Getter;
import lombok.Setter;


@Component
public class KakaoApiUtil {
	static final String restApiKey = "c2d2afbc8ab5c59bb69ef67a2be0bb9d";
	
	public static Point getPointByAddress(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/address.json";
		url += "?query=" + URLEncoder.encode(address, "UTF-8");
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + restApiKey)//
				.uri(URI.create(url))//
				.GET()//
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();
		System.out.println(responseBody);
		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<KakaoAddress.Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}
		KakaoAddress.Document document = documents.get(0);
		return new Point(document.getX(), document.getY());
	}

	 
	public static Point getPointByKeyword(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/address.json";
		url += "?query=" + URLEncoder.encode(address, "UTF-8");
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + restApiKey)//
				.uri(URI.create(url))//
				.GET()//
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();
		System.out.println(responseBody);
		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<KakaoAddress.Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}
		KakaoAddress.Document document = documents.get(0);
		return new Point(document.getX(), document.getY());
	}	
	
	public static Point getPointBystopover(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/address.json";
		url += "?query=" + URLEncoder.encode(address, "UTF-8");
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + restApiKey)//
				.uri(URI.create(url))//
				.GET()//
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();
		System.out.println(responseBody);
		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		List<KakaoAddress.Document> documents = kakaoAddress.getDocuments();
		if (documents.isEmpty()) {
			return null;
		}
		KakaoAddress.Document document = documents.get(0);
		return new Point(document.getX(), document.getY());
	}
	/**
	   * 자동차 길찾기
	   * 
	   * @param from 출발지
	   * @param to   도착지
	   * @return 길찾기 결과 정보 KakaoDirections
	   * @throws InterruptedException
	   * @throws IOException
	   */
	  public static KakaoDirections getKakaoDirections(Point from, Point to) throws IOException, InterruptedException {
	    HttpClient client = HttpClient.newHttpClient();
	    String url = "https://apis-navi.kakaomobility.com/v1/directions";
	    url += "?origin=" + from.getX() + "," + from.getY();
	    url += "&destination=" + to.getX() + "," + to.getY();
	    HttpRequest request = HttpRequest.newBuilder()//
	        .header("Authorization", "KakaoAK " + restApiKey)//
	        .header("Content-Type", "application/json")//
	        .uri(URI.create(url))//
	        .GET()//
	        .build();
	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	    String responseBody = response.body();
	    System.out.println(responseBody);

	    KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);

	    return kakaoDirections;
	  }

	

	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class KakaoAddress {
		private List<Document> documents;

		public List<Document> getDocuments() {
			return documents;
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Document {
			Double x;
			Double y;

			public Double getX() {
				return x;
			}

			public Double getY() {
				return y;
			}
		}
	}
	@Setter
	  @Getter
	  public static class Point {
	    private Double x;
	    private Double y;
	    @JsonIgnore
	    private String name;
	    @JsonIgnore
	    private String phone;
	    @JsonIgnore
	    private String adddress;
	    @JsonIgnore
	    private String id;

	    public Point() {

	    }

	    public Point(Double x, Double y) {
	      this.x = x;
	      this.y = y;
	    }

	  }
	}