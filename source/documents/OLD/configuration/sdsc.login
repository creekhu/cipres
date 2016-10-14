#!/bin/csh

#---------------------------------------------------------------------
#
#	~/.login script for workstations at SDSC
#
#	Version 1.6: January 3, 1997
#
#	Questions?  Either submit a Remedy ticket
#		   or E-mail to consult@sdsc.edu
#		   or call (858) 534-5100
#
#       Please run "man cshell" for more information on using this file
#
#	See comments below for help
#
#---------------------------------------------------------------------


# Run the SDSC standard C shell configurations
# SHELLDIR is defined in ~/.cshrc

if ( -r $SHELLDIR/LOGIN ) then
	source $SHELLDIR/LOGIN
else
	echo "SDSC standard configuration script LOGIN missing."
	echo "Please call the consultants at (858) 534-5100."
endif

# Edit and uncomment this command and you will not be asked its value:
# setenv TERM term-type

# If this is the console, then we know to use the current host for $DISPLAY:

if ( "`tty`" == "/dev/console" ) then
	set display="`hostname | cut -d. -f1`.sdsc.edu:0.0"
	setenv DISPLAY $display
	unset display
endif

if ( ! $?DISPLAY ) then

	set DIS = `(who | grep $USER)`
	set DIS2 = `expr "$DIS" : '[^(]*(\([^)]*\)'`

#  Check the BSD 4.3 form, "(host.sdsc.edu)":

	if ( "x$DIS2" == "x" ) then	# Try the Sys V way
# Check the Sys V form:

		set DIS = `(w | grep $USER)`
		set DIS2 = `expr "$DIS" : '[^ ]* *[^ ]* *\([^ ]*\)'`
	endif

	if ( "x$DIS2" != "x:0.0" && "x$DIS2" != "x" ) then

#  "host.sdsc.edu":

		setenv DISPLAY $DIS2":0.0"
	else
		setenv DISPLAY `hostname`:0.0
	endif
endif

# Run the SDSC standard terminal configurations
if ( -r $SHELLDIR/TERM.INIT ) then
	source $SHELLDIR/TERM.INIT
else
	echo "SDSC standard configuration script TERM.INIT missing."
	echo "Please call the consultants at (858) 534-5100."
endif

#---------------------------------------------------------------------
#
# Put your favorite configurations below here. Commands to include in
# your ~/.login are: setenv and terminal setup.
#
#	setenv PRINTER xerox	Set default printer
#
#	setenv TERM vt100	Set default terminal type
#
#	stty erase '^?'		Set backspace key
#	stty erase '^H'		Set alternate backspace key
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
# Interactive commands (stty, resize, and tset) go below the exit command
# or they may interfere with NQS jobs.
#
#---------------------------------------------------------------------

#   Run the SDSC standard configurations stored in $SHELLDIR
#
if ( -r $SHELLDIR/LOGIN ) then
	source $SHELLDIR/LOGIN
else
	echo "SDSC standard configuration script LOGIN missing."
	echo "Please call the consultants at (858) 534-5100."
endif

# Specify default printers for lpr, roff, and vpr
# setenv PRINTER 'printer-name'
# setenv PSPRINTER 'printer-name'
# setenv VPRINTER 'colorpaper'

# Use a special mail folder, and keep a record of outgoing mail
# setenv folder ~/Mail
# setenv record $folder/log
# setenv MBOX $folder/mbox
# setenv DEAD $folder/dead.letter

# Append a path to the linker's library search path
# setenv LD_LIBRARY_PATH "LD_LIBRARY_PATH:/new/path"

# Stop here if this is an NQS job (don't need interactive setup for an NQS job)
if ( $?ENVIRONMENT ) then
	if ( "$ENVIRONMENT" == BATCH ) exit 0
endif

# Fix the backspace key.  Use one of the following two commands
# stty erase '^H'
# stty erase '^?'

# Tell X where to find those custom application defaults files:
# setenv XAPPLRESDIR $HOME/.app-defaults

# Specify some default options for vi (or put these in ~/.exrc)
# setenv EXINIT 'set ignorecase nowrapscan autoindent autowrite'

# Specify organization for InterNet news reader trn or rn
# setenv ORGANIZATION 'My organization name'

# Run resize if on an xterm (rs defined in $SHELLDIR/CSHRC)
# if ( "$TERM" == "xterm" ) then
#	rs
# endif

