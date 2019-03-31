/*
    James Extra Components
    https://github.com/foilen/james-extra-components
    Copyright (c) 2018-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components;

import javax.mail.MessagingException;

public class MessagingExceptionWrapper {

    private MessagingException messagingException;

    public MessagingException getMessagingException() {
        return messagingException;
    }

    public void setMessagingException(MessagingException messagingException) {
        this.messagingException = messagingException;
    }

}
