package com.softevol.ipop;

import java.util.regex.Pattern;

public class ValidateUtils {
    public static class Email {
        private static Pattern rfc2822 = Pattern.compile(
                "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
        );

        public static boolean validate(String email) {
            if (rfc2822.matcher(email).matches()) return true;
            return false;
        }
    }

    public static class Phone {
        private static Pattern pattern = Pattern.compile(
                "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$\n"
        );

        public static boolean validate(String phone) {
            if (pattern.matcher(phone).matches()) return true;
            return false;
        }
    }
}
