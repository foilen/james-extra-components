/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.apache.james.core.MailAddress;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.util.MimeUtil;
import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMatcher;

/**
 * use:
 *
 * <pre>
 * <code>&lt;mailet match="com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific={&lt;header&gt;[=value]}+" class="..." /&gt;</code>
 * </pre>
 * <p/>
 * <p>
 * This matcher checks if the header is present in the message (global) and per recipient (specific). It complements the AddHeader mailet.
 * </p>
 */
public class HasHeaderGlobalAndSpecific extends GenericMatcher {

    private interface HeaderCondition {
        Collection<MailAddress> isMatching(Mail mail) throws MessagingException;
    }

    private static class HeaderNameCondition implements HeaderCondition {
        private final String headerName;

        public HeaderNameCondition(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public Collection<MailAddress> isMatching(Mail mail) throws MessagingException {
            // Check global
            String[] headerArray = mail.getMessage().getHeader(headerName);
            if (headerArray != null && headerArray.length > 0) {
                return mail.getRecipients();
            }

            // Check specific
            Set<MailAddress> addressesWithHeader = new HashSet<>();
            mail.getPerRecipientSpecificHeaders().getHeadersByRecipient().forEach((mailAddress, header) -> {
                if (headerName.equals(header.getName())) {
                    addressesWithHeader.add(mailAddress);
                }
            });

            return addressesWithHeader;
        }
    }

    private static class HeaderValueCondition implements HeaderCondition {
        private final String headerName;
        private final String headerValue;

        public HeaderValueCondition(String headerName, String headerValue) {
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public Collection<MailAddress> isMatching(Mail mail) throws MessagingException {
            // Check global
            String[] headerArray = mail.getMessage().getHeader(headerName);
            if (headerArray != null && headerArray.length > 0 && //
                    Arrays.stream(headerArray).anyMatch(value -> headerValue.equals(sanitizeHeaderField(value)))) {
                return mail.getRecipients();
            }

            // Check specific
            Set<MailAddress> addressesWithHeader = new HashSet<>();
            mail.getPerRecipientSpecificHeaders().getHeadersByRecipient().forEach((mailAddress, header) -> {
                if (headerName.equals(header.getName()) && headerValue.equals(sanitizeHeaderField(header.getValue()))) {
                    addressesWithHeader.add(mailAddress);
                }
            });

            return addressesWithHeader;
        }
    }

    private static final String CONDITION_SEPARATOR = "+";

    private static final String HEADER_VALUE_SEPARATOR = "=";

    private static String sanitizeHeaderField(String headerName) {
        return DecoderUtil.decodeEncodedWords(MimeUtil.unfold(headerName), DecodeMonitor.SILENT);
    }

    private List<HeaderCondition> headerConditions;

    @Override
    public void init() throws MessagingException {
        headerConditions = new ArrayList<>();
        StringTokenizer conditionTokenizer = new StringTokenizer(getCondition(), CONDITION_SEPARATOR);
        while (conditionTokenizer.hasMoreTokens()) {
            headerConditions.add(parseHeaderCondition(conditionTokenizer.nextToken().trim()));
        }
    }

    @Override
    public Collection<MailAddress> match(Mail mail) throws javax.mail.MessagingException {
        Set<MailAddress> matchingRecipients = new HashSet<>();
        boolean first = true;
        for (HeaderCondition headerCondition : headerConditions) {
            if (first) {
                first = false;
                // Keep the first list
                matchingRecipients.addAll(headerCondition.isMatching(mail));
            } else {
                // Ensure intersection (all must be true)
                Collection<MailAddress> currentMatching = headerCondition.isMatching(mail);
                matchingRecipients.removeIf(it -> !currentMatching.contains(it));
            }
        }
        return matchingRecipients.isEmpty() ? null : matchingRecipients;
    }

    private HeaderCondition parseHeaderCondition(String element) throws MessagingException {
        StringTokenizer valueSeparatorTokenizer = new StringTokenizer(element, HEADER_VALUE_SEPARATOR, false);
        if (!valueSeparatorTokenizer.hasMoreElements()) {
            throw new MessagingException("Missing headerName");
        }
        String headerName = valueSeparatorTokenizer.nextToken().trim();
        if (valueSeparatorTokenizer.hasMoreTokens()) {
            return new HeaderValueCondition(headerName, valueSeparatorTokenizer.nextToken().trim());
        } else {
            return new HeaderNameCondition(headerName);
        }
    }
}
