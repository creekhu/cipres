from setuptools import setup
import shutil
import os

setup(name='pycipressdk',
    version='0.1',
    description='cipres sdk scripts prerequisites',
    url='',
    author='Terri Liebowitz Schwartz',
    author_email='terri@sdsc.edu',
    license='',
    packages=['pycipressdk'],
    install_requires=[
        "pymysql == 0.5",
        "pystache == 0.5.3"
    ],
    zip_safe=False)
if (os.path.isdir('./build')) :
    shutil.rmtree('./build')
if (os.path.isdir('./dist')) :
    shutil.rmtree('./dist')
if (os.path.isdir('./pycipressdk.egg-info')) :
    shutil.rmtree('./pycipressdk.egg-info')
