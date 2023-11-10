package com.finman.finmanbackend.util.validator;

@FunctionalInterface
public interface Validator<T, R extends ValidationResult> {
    R validate(T element);

    default Validator<T, R> and(Validator<T, R> another) {
        return element -> {
            R partial = this.validate(element);
            return partial.isFailure() ? partial : another.validate(element);
        };
    }
}
