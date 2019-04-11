/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.matcher;

import java.util.Collection;

import org.apache.james.core.MailAddress;
import org.apache.james.core.MaybeSender;
import org.apache.mailet.AttributeName;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMatcher;

import com.google.common.collect.ImmutableList;

/**
 * @deprecated Normal auth in James is now doing it.
 */
@Deprecated
public class SenderIsLocalAndSameAsSMTPAuth extends GenericMatcher {

    @Override
    public Collection<MailAddress> match(Mail mail) {

        String authUser = mail.getAttribute(AttributeName.of(Mail.SMTP_AUTH_USER_ATTRIBUTE_NAME)).toString();
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
