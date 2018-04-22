/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.stub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.james.core.MailAddress;
import org.apache.mailet.Mail;
import org.apache.mailet.PerRecipientHeaders;
import org.apache.mailet.PerRecipientHeaders.Header;

public class MailStub implements Mail {

    private static final long serialVersionUID = 1L;

    private List<MailAddress> recipients = new ArrayList<>();

    @Override
    public void addSpecificHeaderForRecipient(Header header, MailAddress recipient) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Serializable getAttribute(String name) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Iterator<String> getAttributeNames() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getErrorMessage() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Date getLastUpdated() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public MimeMessage getMessage() throws MessagingException {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public long getMessageSize() throws MessagingException {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getName() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public PerRecipientHeaders getPerRecipientSpecificHeaders() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Collection<MailAddress> getRecipients() {
        return recipients;
    }

    @Override
    public String getRemoteAddr() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getRemoteHost() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public MailAddress getSender() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getState() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public boolean hasAttributes() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void removeAllAttributes() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Serializable removeAttribute(String name) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Serializable setAttribute(String name, Serializable object) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setErrorMessage(String msg) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setLastUpdated(Date lastUpdated) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setMessage(MimeMessage message) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setName(String newName) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setRecipients(Collection<MailAddress> recipients) {
        this.recipients = new ArrayList<>(recipients);
    }

    @Override
    public void setState(String state) {
        throw new IllegalAccessError("Not Implemented");
    }

}
