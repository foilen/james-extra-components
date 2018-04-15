# About

Some Mailets and Matchers for Apache James

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

The resolving is done recursively and it supports loops. 

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
