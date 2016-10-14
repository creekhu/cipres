#!/bin/sh
cp /etc/shibboleth/* etc_shibboleth
cp /etc/httpd/conf.d/* etc_httpd/conf.d
cp /etc/httpd/conf/* etc_httpd/conf
svn status
echo "remember to svn add and commit"
