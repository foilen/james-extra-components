/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.matcher;

import java.util.Collection;

import org.apache.james.core.MailAddress;
import org.apache.james.core.MaybeSender;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMatcher;

import com.google.common.collect.ImmutableList;

public class SenderIsLocalAndSameAsSMTPAuth extends GenericMatcher {

    @Override
    public Collection<MailAddress> match(Mail mail) {

        String authUser = (String) mail.getAttribute(Mail.SMTP_AUTH_USER_ATTRIBUTE_NAME);
        MaybeSender maybeSender = mail.getMaybeSender();
        if (maybeSender == null) {
            return ImmutableList.of();
        }

        String senderEmail = maybeSender.asString();
        if (authUser != null && senderEmail.equalsIgnoreCase(authUser)) {
            return mail.getRecipients();
        } else {
            return ImmutableList.of();
        }

    }

}
