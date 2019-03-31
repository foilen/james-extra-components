/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.james.core.MailAddress;
import org.apache.james.core.MaybeSender;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.MailetContext;
import org.apache.mailet.base.GenericMailet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

public class DumpAllSystemErr extends GenericMailet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumpAllSystemErr.class);

    protected DataSource datasource;

    @Override
    public String getMailetInfo() {
        return "Dumps config and message to System.err";
    }

    @Override
    public void init() throws MessagingException {
        super.init();

        System.err.println("---[Mailet Config]---");
        MailetConfig mailetConfig = getMailetConfig();
        System.err.println("Mailet Name: " + mailetConfig.getMailetName());

        System.err.println("---[Mailet Config - Init Parameters ]---");
        mailetConfig.getInitParameterNames().forEachRemaining(name -> {
            System.err.println(name + " -> " + mailetConfig.getInitParameter(name));
        });

        System.err.println("---[Mailet Config - Context ]---");
        MailetContext mailetContext = mailetConfig.getMailetContext();

        System.err.println("---[Mailet Config - Context - Attributes]---");
        mailetContext.getAttributeNames().forEachRemaining(name -> {
            System.err.println(name + " -> " + mailetContext.getAttribute(name));
        });

        System.err.println("---[Datasource]---");
        if (datasource == null) {
            System.err.println("Is null");
        } else {
            System.err.println("Type -> " + datasource.getClass());

            if (datasource instanceof BasicDataSource) {
                BasicDataSource basicDataSource = (BasicDataSource) datasource;
                System.err.println("Test On Borrow -> " + basicDataSource.getTestOnBorrow());
                System.err.println("Validation Query -> " + basicDataSource.getValidationQuery());
                System.err.println("Validation Query Timeout (sec) -> " + basicDataSource.getValidationQueryTimeout());
            }

            System.err.println("Tables : ");
            try (Connection connection = datasource.getConnection()) {
                ResultSet rs = connection.createStatement().executeQuery("show tables");
                while (rs.next()) {
                    System.err.println("\t" + rs.getString(1));
                }
            } catch (SQLException e) {
                throw new MessagingException("Problem getting the table list", e);
            }

        }

        System.err.println("------------");
    }

    @Override
    public void service(Mail mail) throws MessagingException {
        try {

            // Attributes
            System.err.println("---[Mail - Attributes]---");
            mail.getAttributeNames().forEachRemaining(name -> {
                System.err.println(name + " -> " + mail.getAttribute(name));
            });

            System.err.println("---[Mail - MaybeSender]---");
            MaybeSender maybeSender = mail.getMaybeSender();
            if (maybeSender == null) {
                System.err.println("Sender is null");
            } else {
                System.err.println("Email: " + maybeSender.asString());
            }

            // Recipients
            System.err.println("---[Mail - Recipients]---");
            mail.getRecipients().forEach(it -> {
                System.err.println(it.asString());
            });

            // Headers
            System.err.println("---[Mail - Headers]---");
            Enumeration<Header> headers = mail.getMessage().getAllHeaders();
            while (headers.hasMoreElements()) {
                Header header = headers.nextElement();
                System.err.println(header.getName() + " -> " + header.getValue());
            }
            System.err.println("---[Mail - Headers Per Recipient]---");
            Multimap<MailAddress, org.apache.mailet.PerRecipientHeaders.Header> headersByRecipient = mail.getPerRecipientSpecificHeaders().getHeadersByRecipient();
            headersByRecipient.entries().forEach(entry -> {
                org.apache.mailet.PerRecipientHeaders.Header header = entry.getValue();
                System.err.println(entry.getKey().asString() + " : " + header.getName() + " -> " + header.getValue());
            });

            // Message
            System.err.println("---[Mail - Message]---");
            MimeMessage message = mail.getMessage();
            message.writeTo(System.err);
            System.err.println("------------");
        } catch (IOException e) {
            LOGGER.error("error printing message", e);
        }
    }

    @Inject
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

}
