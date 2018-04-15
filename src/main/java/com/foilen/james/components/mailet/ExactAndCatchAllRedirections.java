/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMailet;

import com.foilen.james.components.RedirectionCacheLoader;
import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Resources;

public class ExactAndCatchAllRedirections extends GenericMailet {

    private static final String CATCH_ALL_FROM_USER = "*";
    protected DataSource datasource;

    private LoadingCache<MailAddress, List<MailAddress>> redirectionsByRecipient;

    protected List<MailAddress> getRedirections(MailAddress recipient) throws MessagingException {
        try {
            return redirectionsByRecipient.get(recipient);
        } catch (ExecutionException e) {
            throw new MessagingException("Cannot load the list", e);
        }
    }

    @Override
    public void init() throws MessagingException {

        int cacheMaxTimeInSeconds = Integer.valueOf(getInitParameter("cacheMaxTimeInSeconds", "2"));
        int cacheMaxEntries = Integer.valueOf(getInitParameter("cacheMaxEntries", "1000"));

        // Prepare the cache
        redirectionsByRecipient = CacheBuilder.newBuilder() //
                .expireAfterWrite(cacheMaxTimeInSeconds, TimeUnit.SECONDS) //
                .maximumSize(cacheMaxEntries) //
                .build(new RedirectionCacheLoader(datasource));

        // Create the table if missing
        try (Connection connection = datasource.getConnection()) {
            log("Creating the table and index");
            connection.createStatement().execute(Resources.asCharSource(getClass().getResource("ExactAndCatchAllRedirections.mariadb-01-table.sql"), Charsets.UTF_8).read());
            connection.createStatement().execute(Resources.asCharSource(getClass().getResource("ExactAndCatchAllRedirections.mariadb-02-index.sql"), Charsets.UTF_8).read());

        } catch (SQLException | IOException e) {
            throw new MessagingException("Problem creating the table and index", e);
        }
    }

    protected Collection<MailAddress> process(MailAddress recipient, Set<MailAddress> allProcessed) throws MessagingException {

        if (!allProcessed.add(recipient)) {
            return Collections.emptyList();
        }

        // Check exact redirection
        List<MailAddress> redirections = getRedirections(recipient);
        if (!redirections.isEmpty()) {
            Set<MailAddress> finalRedirections = new HashSet<>();
            for (MailAddress redirection : redirections) {
                finalRedirections.addAll(process(redirection, allProcessed));
            }
            return finalRedirections;
        }

        // Check local account
        if (getMailetContext().isLocalEmail(recipient)) {
            return Collections.singletonList(recipient);
        }

        // Check catch-all redirection
        redirections = getRedirections(new MailAddress(CATCH_ALL_FROM_USER, recipient.getDomain()));
        if (!redirections.isEmpty()) {
            Set<MailAddress> finalRedirections = new HashSet<>();
            for (MailAddress redirection : redirections) {
                finalRedirections.addAll(process(redirection, allProcessed));
            }
            return finalRedirections;
        }

        // As it
        return Collections.singletonList(recipient);

    }

    @Override
    public void service(Mail mail) throws MessagingException {

        Set<MailAddress> finalRecipients = new HashSet<>();
        Set<MailAddress> allProcessed = new HashSet<>();

        for (MailAddress recipient : mail.getRecipients()) {
            finalRecipients.addAll(process(recipient, allProcessed));
        }

        mail.setRecipients(finalRecipients);

    }

    @Inject
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

}