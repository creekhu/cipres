#!/bin/csh

#---------------------------------------------------------------------
#
#	~/.cshrc script for workstations at SDSC
#
#	Version 1.6: January 3, 1997
#
#       Questions?  Either submit a Remedy ticket
#			or E-mail to consult@sdsc.edu
#			or call (858) 534-5100
#
#	Please run "man cshell" for more information on using this file
#
#	See comments below for help
#
#---------------------------------------------------------------------

#Prompt
alias setprompt 'set prompt="${cwd}% "'
setprompt  # to set the initial prompt
alias cd 'chdir \!* && setprompt'

# Run the SDSC standard C shell configurations
setenv SHELLDIR /usr/local/apps/cshell/bin
if ( -r $SHELLDIR/CSHRC ) then
	source $SHELLDIR/CSHRC
else
	echo "SDSC standard configuration script CSHRC missing."
	echo "Please call the consultants at (858) 534-5100."
endif


#---------------------------------------------------------------------
#
# Put your favorite configurations below here. Commands to include in
# your ~/.cshrc are: umask, set, and alias.
#
#	umask NNN			File creation mask
#
#	set variable			Define a "set" variable
#
#	set path = ( $path /new/path )	Add a path (see examples below)
#
#	alias ll 'ls -al'		Define a new command
#	alias ls 'ls -CF'		Default options for ls
#
# Examples are given below. Uncomment and edit them to suit your needs.
#
# Commands intended for only one architecture (sunsparc, sunsparcsolaris,
# sgi4d, decalpha, or crayt3e) should be enclosed in an if statement to
# prevent them from running on other systems:
#
#	if ( "$MACHID" == "sunsparcsolaris" ) then
#		Sun SPARC only commands here
#	endif
#
# Interactive commands (alias and set) go below the two exit commands.
# Exception: "set path" goes above them. See examples below.
#
#---------------------------------------------------------------------

# Deny others access to newly created files (use 022 to allow vpr to work).
# umask 077

# Add paths
setenv JAVA_HOME /usr/local/apps/java-1.5

set path = ( $JAVA_HOME/bin $path /projects/ngbw_db1/contrib/tools/bin/ia32 )

# 10/27/08 Terri - put /fs/cipres/bin in front of /usr/local/bin so we pick up the
# right svn.  Did this in order to build cipres framework for bwbatch on snooker. 

# To pick up a newer version of autoconf we need /usr/local/bin in front of /usr/bin.
# Terri added this on 10/21/2008
set path = ( /usr/local/bin $path )

# We need to use python link that's in /fs/cipres/bin
set path = ( /fs/cipres/bin $path )


# Phy.fi software uses libgd which is in /usr/local/lib, a directory that isn't searched by default.
setenv PYTHONPATH /fs/cipres/misc/python-gd/lib/python
if ${?LD_LIBRARY_PATH} then
	setenv LD_LIBRARY_PATH /usr/local/lib:${LD_LIBRARY_PATH};
else
	setenv LD_LIBRARY_PATH /usr/local/lib;
endif


# Create an alias to an executable (instead of appending path):
# alias gnuplot /usr/local/apps/gnuplot/bin/gnuplot
alias ls 'ls --color'
alias ll 'ls -Al'
alias la 'ls -A'

setenv MFOLDDAT /projects/ngbw_db1/contrib/tools/mfold/data
setenv BOXDIR /projects/ngbw_db1/contrib/tools/boxshade/3.31/src
setenv TACGLIB /projects/ngbw_db1/contrib/tools/tacg/3.50/src/Data
setenv BLIMPS_DIR /projects/ngbw_db1/contrib/tools/blimps/3.9/

# For Cipres framework
setenv CIPRES_HOME /fs/cipres/cipres_fw
setenv CIPRES_ROOT $CIPRES_HOME/CIPRES-and-deps/cipres/build
setenv CIPRES_USER_DIR /fs/cipres/bwbatch_cipres_user_dir
set path = ( /fs/cipres/bin $path $CIPRES_ROOT/bin)
# setenv WB_CIPRES_TIMEOUT 4320 

# Stop here if not interactive (i.e. we're running a script)
if ( ! $?prompt ) then
	exit 0
endif

# Stop here if this is an NQS job (don't need interactive setup for an NQS job)
if ( $?ENVIRONMENT ) then
	if ( "$ENVIRONMENT" == BATCH ) exit 0
endif

# Some aliases you might like
# alias ll 'ls -al'
# alias h 'history'
# alias t 'cd /scratch/s1/$USER'
# alias lo 'logout'

# Reset a couple C shell variables back to their defaults
# unset noclobber ignoreeof savehist

# Adjust the command history mechanism
# set history=25
# set savehist

# Prompt with hostname
# set prompt="`hostname` % "

# Prompt with hostname and command number
# set prompt = "`hostname` (\!) % "

# Prompt with hostname and current directory
# if ( $?cwd ) then
#	alias cd 'cd \!* ; set prompt="`hostname`:$cwd % "'
#	alias pushd 'pushd \!* ; set prompt="`hostname`:$cwd % "'
#	alias popd 'popd \!* ; set prompt="`hostname`:$cwd % "'
#	cd .
# endif
