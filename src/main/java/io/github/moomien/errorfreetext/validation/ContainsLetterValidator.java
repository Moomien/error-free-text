package io.github.moomien.errorfreetext.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContainsLetterValidator implements ConstraintValidator<ContainsLetter, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.codePoints().
                anyMatch(Character::isLetter);
    }
}
