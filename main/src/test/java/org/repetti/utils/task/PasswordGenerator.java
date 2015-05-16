package org.repetti.utils.task;

import org.repetti.utils.SecurityHelper;

/**
 * Created on 16/05/15.
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        System.out.println(SecurityHelper.generateReadablePassword(5, 4, 3, 2, 1));
    }
}
