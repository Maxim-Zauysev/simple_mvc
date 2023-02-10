package org.example.app.exeptions;

public class FIleToUploadException extends Exception{
    public String message;

    public FIleToUploadException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
