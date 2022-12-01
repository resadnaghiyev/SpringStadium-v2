package com.rashad.loginwithsocial.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String email) {

        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}



// Gmail special (username+something@gmail.com)
//  "^(?=.{1,64}@)[A-Za-z0-9_-+]+(\\.[A-Za-z0-9_-+]+)*@[^-][A-Za-z0-9-+]+(\\.[A-Za-z0-9-+]+)*(\\.[A-Za-z]{2,})$"