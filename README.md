# About

Some Mailets and Matchers for Apache James

# Get the binaries

Available here: https://dl.bintray.com/foilen/maven/com/foilen/james-extra-components/

# Mailet - DumpAllSystemErr

## Description

This is used to display the config and all the messages properties to stderr.

## Configuration

In *mailetcontainer.xml*, you can add it at any stage with: 

```
<mailet match="All" class="com.foilen.james.components.mailet.DumpAllSystemErr" />
```

# Mailet - ExactAndCatchAllRedirections

## Description

This is used to map some emails to one or multiple redirections.

Logic:
* If the recipient is an exact match, change it for its *toEmails* then exit
* If the recipient is a local account, then exit
* If the recipient's domain has a catch-all, change it for its catch-all's *toEmails* then exit
  * Catch-all are defined with FROM_USER='*'
* Anything else, we do not care since they are messages to relay or for accounts that do not exist

The resolving is done recursively and it supports loops. If there were a redirection done, the _isRedirection_ header is set on the email.

## Configuration

In *mailetcontainer.xml*, you can change 

```
<mailet match="All" class="RecipientRewriteTable" />
```
to
 
```
<mailet match="All" class="com.foilen.james.components.mailet.ExactAndCatchAllRedirections">
	<cacheMaxTimeInSeconds>2</cacheMaxTimeInSeconds>
	<cacheMaxEntries>1000</cacheMaxEntries>
</mailet>

<mailet match="HasHeader=isRedirection" class="ToProcessor">
  <processor>transport</processor>
</mailet>
```

In *smtpserver.xml*, you can change 

```
<handler class="org.apache.james.smtpserver.fastfail.ValidRcptHandler" />
```
to
 
```
<handler class="com.foilen.james.components.handler.fastfail.ValidRcptHandler" />
```

## MySQL/MariaDB schema

That schema is automatically called when the mailet is being initialized. 

```
CREATE TABLE IF NOT EXISTS `FOILEN_REDIRECTIONS` (
  `FROM_USER` varchar(100) NOT NULL,
  `FROM_DOMAIN` varchar(100) NOT NULL,
  `TO_EMAIL` varchar(100) NOT NULL
);

CREATE INDEX IF NOT EXISTS `FOILEN_REDIRECTIONS` ON `FOILEN_REDIRECTIONS` (FROM_DOMAIN, FROM_USER);
```

# Mailet - LogInfo

## Description

This is used to create an info log entry with the mail id and the `text`.

## Configuration

In *mailetcontainer.xml*, you can add it at any stage with: 

```
<mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
	<text>Some text</text>
</mailet>
```

# Mailet - RemoveHeaderGlobalAndSpecific

## Description

This is used to remove all the named headers from the mail message and for specific header per recipient.

## Configuration

In *mailetcontainer.xml*, you can add it at any stage with: 

```
<mailet match="All" class="com.foilen.james.components.mailet.RemoveHeaderGlobalAndSpecific">
	<name>header1,header2</name>
</mailet>
```

# Matcher - SenderIsLocalAndSameAsSMTPAuth

## Description

Matches when the user is auth (like the standard SMTPAuthSuccessful), but also checks that the sender is the currently logged-in user. This is to ensure there is no impersonation of other users.

## Configuration

In *mailetcontainer.xml*, you can add it at any stage with: 

```
<mailet match="com.foilen.james.components.matcher.SenderIsLocalAndSameAsSMTPAuth" class="ToProcessor">
	<processor>auth-user-relay</processor>
</mailet>
```
