package org.example.web.exeptions;

public class BookShelfLoginExeption extends Exception {
    private final String message;

    public BookShelfLoginExeption(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
