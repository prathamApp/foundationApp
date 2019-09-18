package com.pratham.foundation.custumView.fancy_loaders_library.exception;

public class InvalidNumberOfPulseException extends Exception {
    @Override public String getMessage() {
        return "The number of pulse must be between 2 and 6";
    }
}
