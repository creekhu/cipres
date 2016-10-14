#!/bin/sh
# This should be run by cron on portal2prod.  It logs into each of the
# teragrid hosts and runs the copy of ws_cleanup.sh that's installed there
# to clean up the old job directories.
#
# TO DO: replace CIPRES_PASSWORD with tgup password for "cipres" account,
# Then copy this file somewhere, and do chmod 700 on it.

# HOSTNAME=8ball
source $HOME/.bashrc

# Make sure we've got a credential for the cipres community account. File used by 
# myproxy-logon and gsissh is /tmp/x509_<uid_number>.  Portal uses a different file.
echo CIPRES_PASSWORD | myproxy-logon -S -l cipres

# Log into abe and run the script.  CA=cipres@login-abe.ncsa.teragrid.org
gsissh $CA ngbw/contrib/tools/bin/ws_cleanup.sh

# Log into abe and run the script.  
gsissh $CL ngbw/contrib/tools/bin/ws_cleanup.sh
