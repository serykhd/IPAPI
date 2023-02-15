package ru.serykhd.ipinfo.response;

import lombok.Getter;

import java.util.List;

@Getter
public class IPInfoResponse {

    private String ip;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String postal;
    private String timezone;

    private Asn asn;
    private Company company;
    // nullable
    private Carrier Carrier;
    private Privacy privacy;
    private Abuse abuse;
    private Domains domains;

    @Getter
    public static class Asn {

        private String asn;
        private String name;
        private String domain;
        private String route;
        private String type;

    }

    @Getter
    public static class Company {

        private String name;
        private String domain;
        private String type;
    }

    @Getter
    public static class Carrier {

        private String name;
        private String mcc;
        private String mnc;
    }

    @Getter
    public static class Privacy {

        private boolean vpn;
        private boolean proxy;
        private boolean tor;
        private boolean relay;
        private boolean hosting;
        private String service;
    }

    @Getter
    public static class Abuse {

        private String address;
        private String country;
        private String email;
        private String name;
        private String network;
        private String phone;
    }

    @Getter
    public static class Domains {

        private String ip;
        private int total;
        private List<String> domains;
    }
}
