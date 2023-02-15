package ru.serykhd.ipinfo.error;

public class IPInfoError {

    private int status;
    private Error error;

    public static class Error {

        private String title;
        private String message;
    }
}
