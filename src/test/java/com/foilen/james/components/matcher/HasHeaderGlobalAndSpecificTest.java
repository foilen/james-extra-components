/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.matcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.james.core.MailAddress;
import org.apache.james.core.builder.MimeMessageBuilder;
import org.apache.james.util.MimeMessageUtil;
import org.apache.mailet.Mail;
import org.apache.mailet.Matcher;
import org.apache.mailet.PerRecipientHeaders.Header;
import org.apache.mailet.base.test.FakeMail;
import org.apache.mailet.base.test.FakeMatcherConfig;
import org.apache.mailet.base.test.MailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HasHeaderGlobalAndSpecificTest {

    private static final String HEADER_NAME_1 = "JUNIT";
    private static final String HEADER_NAME_2 = "defaultHeaderName";
    private static final String HEADER_VALUE_1 = "defaultHeaderValue";
    private static final String HEADER_VALUE_2 = "defaultHeaderValue2";

    private FakeMail mockedMail;
    private Matcher matcher;

    @Test
    public void matchShouldFindTheRightHeaderLineWhenUsedWithValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "=" + HEADER_VALUE_2).build());

        Mail mail = MailUtil.createMockMail2Recipients(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER_NAME_1, HEADER_VALUE_1).addHeader(HEADER_NAME_1, HEADER_VALUE_2).build());

        assertThat(matcher.match(mail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldIgnoreExtraEquals() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1 + "=any").build());

        assertThat(matcher.match(mockedMail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldNotMatchMailsWithNoHeaderWhenValueNotSpecified() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1).build());

        Mail mail = MailUtil.createMockMail2Recipients(MailUtil.createMimeMessage());

        assertThat(matcher.match(mail)).isNull();
    }

    @Test
    public void matchShouldNotMatchMailsWithNoHeaderWhenValueSpecified() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1).build());

        Mail mail = MailUtil.createMockMail2Recipients(MailUtil.createMimeMessage());

        assertThat(matcher.match(mail)).isNull();
    }

    @Test
    public void matchShouldReturnAddressesWhenAllConditionsMatch() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "+" + HEADER_NAME_2).build());

        Mail mail = MailUtil.createMockMail2Recipients(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER_NAME_1, HEADER_VALUE_1).addHeader(HEADER_NAME_2, HEADER_VALUE_2).build());

        assertThat(matcher.match(mail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldReturnAddressesWhenAllConditionsMatchGlobalAndSpecific() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "+" + HEADER_NAME_2).build());

        Mail mail = MailUtil.createMockMail2Recipients(MimeMessageBuilder.mimeMessageBuilder().addHeader(HEADER_NAME_1, HEADER_VALUE_1).build());
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_2).value(HEADER_VALUE_2).build(), new MailAddress("test@james.apache.org"));

        assertThat(matcher.match(mail)).containsAll(Arrays.asList(new MailAddress("test@james.apache.org")));
    }

    @Test
    public void matchShouldReturnAddressesWhenAllConditionsMatchSpecific() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "+" + HEADER_NAME_2).build());

        Mail mail = MailUtil.createMockMail2Recipients(MimeMessageBuilder.mimeMessageBuilder().build());
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_1).value(HEADER_VALUE_1).build(), new MailAddress("test@james.apache.org"));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_2).value(HEADER_VALUE_2).build(), new MailAddress("test@james.apache.org"));

        assertThat(matcher.match(mail)).containsAll(Arrays.asList(new MailAddress("test@james.apache.org")));
    }

    @Test
    public void matchShouldReturnAddressesWhenAllValueConditionsMatchGlobalAndSpecific() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1 + "+" + HEADER_NAME_2)
                .build());

        Mail mail = FakeMail.builder() //
                .name(MailUtil.newId()) //
                .mimeMessage(MimeMessageBuilder.mimeMessageBuilder().build()) //
                .recipients(new MailAddress("test@james.apache.org"), new MailAddress("test2@james.apache.org"), new MailAddress("test3@james.apache.org")) //
                .build();
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_1).value("wrong value").build(), new MailAddress("test@james.apache.org"));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_1).value(HEADER_VALUE_1).build(), new MailAddress("test2@james.apache.org"));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_2).value("any value").build(), new MailAddress("test2@james.apache.org"));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_1).value("wrong value").build(), new MailAddress("test3@james.apache.org"));
        mail.addSpecificHeaderForRecipient(Header.builder().name(HEADER_NAME_2).value("any value").build(), new MailAddress("test3@james.apache.org"));

        assertThat(matcher.match(mail)).containsAll(Arrays.asList(new MailAddress("test2@james.apache.org")));
    }

    @Test
    public void matchShouldReturnAddressesWhenGoodHeaderNameAndValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "=" + HEADER_VALUE_1).build());

        assertThat(matcher.match(mockedMail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldReturnAddressesWhenRightHeaderNameWithoutValue() throws MessagingException {

        FakeMatcherConfig mci = FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1).build();

        matcher.init(mci);

        assertThat(matcher.match(mockedMail)).containsAll(mockedMail.getRecipients());
    }

    @Test
    public void matchShouldReturnNullWhenOneConditionIsNotTrue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "+" + HEADER_NAME_2).build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldReturnNullWhenWrongHeaderNameWithoutValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_2).build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldReturnNullWhenWrongHeaderNameWithValueSpecified() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_2 + "=" + HEADER_VALUE_2).build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldReturnNullWhenWrongValue() throws MessagingException {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition(HEADER_NAME_1 + "=" + HEADER_VALUE_2).build());

        assertThat(matcher.match(mockedMail)).isNull();
    }

    @Test
    public void matchShouldSupportEncodedHeaders() throws Exception {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition("To=Benoît TELLIER <tellier@linagora.com>").build());

        Mail mail = MailUtil.createMockMail2Recipients(MimeMessageUtil.mimeMessageFromStream(ClassLoader.getSystemResourceAsStream("mime/gmail.mime")));

        assertThat(matcher.match(mail)).containsAll(mail.getRecipients());
    }

    @Test
    public void matchShouldSupportFoldedHeaders() throws Exception {
        matcher.init(FakeMatcherConfig.builder().matcherName("com.foilen.james.components.matcher.HasHeaderGlobalAndSpecific").condition("From=aduprat <duprat@linagora.com>").build());

        Mail mail = MailUtil.createMockMail2Recipients(MimeMessageUtil.mimeMessageFromStream(ClassLoader.getSystemResourceAsStream("mime/headerFolded.mime")));

        assertThat(matcher.match(mail)).containsAll(mail.getRecipients());
    }

    @BeforeEach
    public void setUp() throws Exception {
        MimeMessage mimeMessage = MailUtil.createMimeMessage(HEADER_NAME_1, HEADER_VALUE_1);
        mockedMail = MailUtil.createMockMail2Recipients(mimeMessage);
        matcher = new HasHeaderGlobalAndSpecific();
    }
}
