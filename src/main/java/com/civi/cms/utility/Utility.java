package com.civi.cms.utility;

import java.security.SecureRandom;

public class Utility {
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getWelcomeEmailBoday(String firstName, String email, String password) {
        String body = "<html>" +
                "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2 style='color: #2E86C1;'>Welcome to the Case Management Portal!</h2>" +
                "<p>Dear <strong>" + firstName + "</strong>,</p>" +
                "<p>We're excited to have you onboard. Your account has been successfully created.</p>" +
                "<p><strong>Here are your login details:</strong></p>" +
                "<table style='border: 1px solid #ccc; padding: 10px; border-collapse: collapse;'>"
                + "<tr><td style='padding: 8px;'><strong>Email</strong></td><td style='padding: 8px;'>" + email + "</td></tr>"
                + "<tr><td style='padding: 8px;'><strong>Password</strong></td><td style='padding: 8px;'>" + password + "</td></tr>"
                + "</table>" +
                "<p style='margin-top: 20px;'>For security reasons, please <strong>change your password</strong> after logging in for the first time.</p>" +
                "<p>If you have any questions or need help, feel free to contact our support team.</p>" +
                "<br>" +
                "<p>Best regards,</p>" +
                "<p><strong>Case Management Team</strong></p>" +
                "</body>" +
                "</html>";
        return body;
    }

}
