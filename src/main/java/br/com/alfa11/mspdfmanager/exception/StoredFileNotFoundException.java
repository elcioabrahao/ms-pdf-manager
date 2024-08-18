package br.com.alfa11.mspdfmanager.exception;

public class StoredFileNotFoundException extends RuntimeException {

    public StoredFileNotFoundException(String message)
    {
        super(message);
    }
}