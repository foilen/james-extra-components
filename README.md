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
