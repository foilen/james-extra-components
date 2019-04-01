/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;

import org.apache.james.core.MailAddress;
import org.apache.james.core.builder.MimeMessageBuilder;
import org.apache.mailet.Mail;
import org.apache.mailet.PerRecipientHeaders.Header;
import org.apache.mailet.base.GenericMailet;
import org.apache.mailet.base.test.FakeMail;
import org.apache.mailet.base.test.FakeMailetConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RemoveHeaderGlobalAndSpecificTest {

    private static final String HEADER1 = "header1";
    private static final String HEADER2 = "header2";
    private static final String RECIPIENT1 = "r1@example.com";
    private static final String RECIPIENT2 = "r2@example.com";

    private GenericMailet mailet;

    @Test
    public void getMailetInfoShouldReturnValue() {
        assertThat(mailet.getMailetInfo()).isEqualTo("RemoveHeaderGlobalAndSpecific Mailet");
    }

    @Test
    public void initShouldThrowWhenInvalidConfig() {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").build();
        assertThatThrownBy(() -> mailet.init(mailetConfig)).isInstanceOf(MessagingException.class);
    }

    @Test
    public void serviceShouldNotRemoveHeaderWhenEmptyConfig() throws MessagingException {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").setProperty("name", "").build();
        mailet.init(mailetConfig);

        Mail mail = FakeMail.fromMessage(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER1, "true").addHeader(HEADER2, "true"));

        mailet.service(mail);

        assertThat(mail.getMessage().getHeader(HEADER1)).isNotNull();
        assertThat(mail.getMessage().getHeader(HEADER2)).isNotNull();
    }

    @Test
    public void serviceShouldNotRemoveHeaderWhenNoneMatching() throws MessagingException {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").setProperty("name", "other1").setProperty("name", "other2").build();
        mailet.init(mailetConfig);

        Mail mail = FakeMail.fromMessage(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER1, "true").addHeader(HEADER2, "true"));

        mailet.service(mail);

        assertThat(mail.getMessage().getHeader(HEADER1)).isNotNull();
        assertThat(mail.getMessage().getHeader(HEADER2)).isNotNull();
    }

    @Test
    public void serviceShouldRemoveHeadersWhenTwoMatching() throws MessagingException {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").setProperty("name", HEADER1 + "," + HEADER2).build();
        mailet.init(mailetConfig);

        Mail mail = FakeMail.fromMessage(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER1, "true").addHeader(HEADER2, "true"));

        mailet.service(mail);

        assertThat(mail.getMessage().getHeader(HEADER1)).isNull();
        assertThat(mail.getMessage().getHeader(HEADER2)).isNull();
    }

    @Test
    public void serviceShouldRemoveHeaderWhenOneMatching() throws MessagingException {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").setProperty("name", HEADER1).build();
        mailet.init(mailetConfig);

        Mail mail = FakeMail.fromMessage(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER1, "true").addHeader(HEADER2, "true"));

        mailet.service(mail);

        assertThat(mail.getMessage().getHeader(HEADER1)).isNull();
        assertThat(mail.getMessage().getHeader(HEADER2)).isNotNull();
    }

    @Test
    public void serviceShouldRemoveSpecificHeaderWhenOneMatching() throws MessagingException {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").setProperty("name", HEADER1).build();
        mailet.init(mailetConfig);

        Mail mail = FakeMail.fromMessage(MimeMessageBuilder.mimeMessageBuilder().addToRecipient(RECIPIENT1).addToRecipient(RECIPIENT2));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER1).value("1").build(), new MailAddress(RECIPIENT1));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER2).value("1").build(), new MailAddress(RECIPIENT2));

        mailet.service(mail);

        assertThat(mail.getMessage().getHeader(HEADER1)).isNull();
        assertThat(mail.getMessage().getHeader(HEADER2)).isNull();
        assertThat(mail.getPerRecipientSpecificHeaders().getHeaderNamesForRecipient(new MailAddress(RECIPIENT1))).isEmpty();
        assertThat(mail.getPerRecipientSpecificHeaders().getHeaderNamesForRecipient(new MailAddress(RECIPIENT2))).isNotEmpty();
    }

    @Test
    public void serviceShouldThrowWhenExceptionOccured() throws MessagingException {
        FakeMailetConfig mailetConfig = FakeMailetConfig.builder().mailetName("Test").setProperty("name", "").build();
        mailet.init(mailetConfig);

        Mail mail = mock(Mail.class);
        when(mail.getMessage()).thenThrow(MessagingException.class);

        assertThatThrownBy(() -> mailet.service(mail)).isInstanceOf(MessagingException.class);
    }

    @BeforeEach
    public void setup() {
        mailet = new RemoveHeaderGlobalAndSpecific();
    }

}
