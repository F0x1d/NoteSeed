package com.f0x1d.notes.utils.translations;

public class IncorrectTranslationError extends Exception {

    public IncorrectTranslationError() {
        super();
    }

    public IncorrectTranslationError(String message) {
        super(message);
    }

    public IncorrectTranslationError(Throwable cause) {
        super(cause);
    }
}
