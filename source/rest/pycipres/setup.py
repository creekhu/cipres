from setuptools import setup

setup(name='pycipres',
    version='0.1',
    description='python cipres rest client',
    url='',
    author='Terri Liebowitz Schwartz',
    author_email='terri@sdsc.edu',
    license='',
    packages=['pycipres'],
    install_requires=[
        "pymysql == 0.5",
        "requests == 1.1.0",
		"pystache == 0.5.3",
    ],
    scripts=[
        'bin/runtemplate',
        'bin/batchtest',
        'bin/validatejobs',
    ],

    zip_safe=False)
