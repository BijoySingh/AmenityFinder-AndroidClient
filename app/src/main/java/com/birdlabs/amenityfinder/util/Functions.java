package com.birdlabs.amenityfinder.util;

import com.github.bijoysingh.starter.util.LocaleManager;

/**
 * Common functions
 * Created by bijoy on 3/4/16.
 */
public class Functions {
    public static String setPrecision(Double d, Integer precision) {
        String dbl = LocaleManager.toString(d);
        Boolean decimalStarted = false;
        Integer precisionAcquired = 0;
        StringBuilder builder = new StringBuilder();
        for (int position = 0; position < dbl.length(); position++) {
            builder.append(dbl.charAt(position));
            if (dbl.charAt(position) == '.') {
                decimalStarted = true;
            } else if (decimalStarted) {
                precisionAcquired += 1;
                if (precisionAcquired.equals(precision)) {
                    break;
                }
            }
        }

        return builder.toString();
    }
}
