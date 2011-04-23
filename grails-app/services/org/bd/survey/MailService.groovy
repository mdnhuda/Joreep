package org.bd.survey

import javax.mail.MessagingException
import javax.mail.internet.AddressException
import javax.mail.Transport
import javax.mail.Address
import javax.mail.internet.InternetAddress
import org.bd.survey.utils.Utils
import javax.mail.internet.MimeMessage

class MailService {

    static transactional = true

    def sendMail(String subject) {
        sendMail(Utils.ADMIN_EMAIL, subject, subject)
    }

    def sendMail(String subject, String body) {
        sendMail(Utils.ADMIN_EMAIL, subject, body)
    }

    def sendMail(def recipients, String subject, String body) {
        //TODO: do something about the 8 per minute quota

        Properties props = new Properties()
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null)

        try {
            javax.mail.Message msg = new MimeMessage(session)
            msg.setFrom(new InternetAddress(Utils.ADMIN_EMAIL))

            def addresses
            if (recipients instanceof String) {
                addresses = [new InternetAddress(recipients)]
            } else {
                addresses = recipients?.collect { new InternetAddress(it) }
            }

            msg.addRecipients(javax.mail.Message.RecipientType.TO, addresses as Address[])
            msg.setSubject("Joreepbd :: ${subject}")
            msg.setText(body)

            Transport.send(msg)

        } catch (AddressException e) {
            println("Exception: ${e.getMessage()}")
        } catch (MessagingException e) {
            println("Exception: ${e.getMessage()}")
        }
    }
}
