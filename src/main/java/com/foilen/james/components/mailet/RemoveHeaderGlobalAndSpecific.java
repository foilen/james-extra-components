/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailetException;
import org.apache.mailet.PerRecipientHeaders.Header;
import org.apache.mailet.base.GenericMailet;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * Remove headers in the message (global) and per recipient (specific).
 *
 * Sample configuration:
 *
 * <pre>
 * <code>
 * &lt;mailet match="All" class="com.foilen.james.components.mailet.RemoveHeaderGlobalAndSpecific"&gt;
 * &lt;name&gt;header1,header2&lt;/name&gt;
 * &lt;/mailet&gt;
 * </code>
 * </pre>
 *
 */
public class RemoveHeaderGlobalAndSpecific extends GenericMailet {
    private List<String> headers;

    @Override
    public String getMailetInfo() {
        return "RemoveHeaderGlobalAndSpecific Mailet";
    }

    @Override
    public void init() throws MailetException {
        String header = getInitParameter("name");
        if (header == null) {
            throw new MailetException("Invalid config. Please specify at least one name");
        }
        headers = ImmutableList.copyOf(Splitter.on(",").split(header));
    }

    @Override
    public void service(Mail mail) throws MessagingException {
        // Remove globally
        MimeMessage message = mail.getMessage();
        for (String header : headers) {
            message.removeHeader(header);
        }

        // Remove specific
        mail.getPerRecipientSpecificHeaders().getRecipientsWithSpecificHeaders() //
                .stream().collect(Collectors.toList()) // Streaming for concurrent modifications
                .forEach(recipient -> {
                    Iterator<Header> it = mail.getPerRecipientSpecificHeaders().getHeadersForRecipient(recipient).iterator();
                    while (it.hasNext()) {
                        Header next = it.next();
                        if (headers.contains(next.getName())) {
                            it.remove();
                        }
                    }
                });
    }
}
