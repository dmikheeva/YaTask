package ru.daria.singers;

/**
 * Created by dsukmanova on 24.07.16.
 */

public class DataLoaderResult  <T>{
    private Exception exception;
    private T result;

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Exception getException() {

        return exception;
    }

    public T getResult() {
        return result;
    }
}
