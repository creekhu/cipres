#!/bin/sh
echo updating ~/.globus/certificates


source $HOME/.bashrc

# Download and install new ~/.globus/certificates
cd ~/.globus
rm -rf ./tmp
mkdir ./tmp
cd ./tmp

wget --no-check-certificate https://software.xsede.org/security/xsede-certs.tar.gz
if [ !  -e xsede-certs.tar.gz ]; then
	echo failed to retrieve archive
	exit 1
fi

gunzip xsede-certs.tar.gz 
tar xf xsede-certs.tar 
if [ ! -d certificates ] ; then
	echo no certificates directory in archive
	exit 1
fi
rm -rf ../certificates
cp -r ./certificates/ ..
cd ..
rm -rf ./tmp

echo ran OK


