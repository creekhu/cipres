# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
  . ~/.bashrc
fi

# User specific environment and startup programs

export MFOLDDAT=/projects/ngbw_db1/contrib/tools/mfold/data
export BOXDIR=/projects/ngbw_db1/contrib/tools/boxshade/3.31/src
export TACGLIB=/projects/ngbw_db1/contrib/tools/tacg/3.50/src/Data
export BLIMPS_DIR=/projects/ngbw_db1/contrib/tools/blimps/3.9/

# For Cipres framework
export CIPRES_HOME=/fs/cipres/cipres_fw
export CIPRES_ROOT=$CIPRES_HOME/CIPRES-and-deps/cipres/build
export CIPRES_USER_DIR=/fs/cipres/bwbatch_cipres_user_dir

# Phy.fi software uses libgd which is in /usr/local/lib, a directory that isn't searched by default.
export PYTHONPATH=/fs/cipres/misc/python-gd/lib/python
if [ -n "$LD_LIBRARY_PATH" ]; then
  export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH;
else
  export LD_LIBRARY_PATH=/usr/local/lib;
fi

export JAVA_HOME=/usr/local/apps/java-1.5

# 10/27/08 Terri - put /fs/cipres/bin in front of /usr/local/bin so we pick up the
# right svn.  Did this in order to build cipres framework for bwbatch on snooker. 

# To pick up a newer version of autoconf we need /usr/local/bin in front of /usr/bin.
# Terri added this on 10/21/2008

# We need to use python link that's in /fs/cipres/bin
PATH=/fs/cipres/bin:/usr/local/bin:$JAVA_HOME/bin:$PATH:/projects/ngbw_db1/contrib/tools/bin/ia32:$CIPRES_ROOT/bin

export PATH
unset USERNAME
