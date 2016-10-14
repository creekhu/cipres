#!/bin/bash


# First three commands complain about GET not supported, next three complain about 
# invalid version number. 

echo ==== GET $TUS_URL ====
curl  $CREDS -i -H 'Tus-Resumable: 1.0.0' $TUS_URL 
echo ==== GET $TUS_URL/abc ====
curl  $CREDS -i -H 'Tus-Resumable: 1.0.0' $TUS_URL/abc
echo ==== GET $TUS_URL/abc/def ====
curl  $CREDS -i -H 'Tus-Resumable: 1.0.0' $TUS_URL/abc/def
echo ==== GET $TUS_URL ====
curl  $CREDS -i $TUS_URL 
echo ==== GET $TUS_URL/abc ====
curl  $CREDS -i $TUS_URL/abc
echo ==== GET $TUS_URL/abc/def ====
curl  $CREDS -i $TUS_URL/abc/def

