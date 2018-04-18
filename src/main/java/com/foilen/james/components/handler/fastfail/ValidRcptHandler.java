/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.handler.fastfail;

import org.apache.james.protocols.smtp.MailAddress;
import org.apache.james.protocols.smtp.SMTPSession;

import com.foilen.james.components.common.RedirectionManager;

public class ValidRcptHandler extends org.apache.james.smtpserver.fastfail.ValidRcptHandler {

    @Override
    protected boolean isValidRecipient(SMTPSession session, MailAddress recipient) {

        boolean result = false;

        // Normal
        result |= super.isValidRecipient(session, recipient);

        try {
            // Redirection
            org.apache.mailet.MailAddress recipientMailAddress = new org.apache.mailet.MailAddress(recipient.getLocalPart(), recipient.getDomain());
            result |= !RedirectionManager.getRedirections(recipientMailAddress).isEmpty();

            // Catch-all
            result |= !RedirectionManager.getCatchAllRedirections(recipientMailAddress).isEmpty();
        } catch (Exception e) {
            session.getLogger().info("Unable to access Redirection", e);
        }

        return result;
    }
}