New RAxML interface: raxmlhpc8_rest_xsede

toolId=RAXMLHPC8_REST_XSEDE

The new interface was constructed to simplify submissions from both web browser and rest services.

This document is intended to communicate the structure and options in the new interface. 
We think the new interface is a huge improvement, and we recommend that rest users adopt it, 
as some options available through the browser interface are not accessible through rest using
the old interface because of ctrl conflicts. However, the old interface will continue to be
supported. And anyone who is concerned only with -f d and -fa analyses will not necessarily 
need to change to the new interface.  However, the new interface is preferred for exposing the
"generic" option: the ability for a user to enter a command line flag that is not natively supported
by the interface. This is available in the REST interface only. 

Because the restructuring makes it easier to add options, we will expand on the analyses available
through the new RAXMLHPC8_XSEDE interface. We are in the process of generating a new workflow interface
so several RAxML steps can be chained, as they are in raxmlGUI. We will follow with more information on
that soon.

Configuring the analysis

The restructuring is based on the fact that each of the -f <letter> options that RAxML
offers are mutually exclusive. They are all gathered under a single parameter element,
"select_analysis"

the default value =fd, which calls the raxml default analysis: -f d.

The naming convention for the analyses available is:
select_analysis_=<value> where <value> is the raxml native flag with any dashes and white
space removed. So the -f a option is called:

select_analysis_= fa

the -f A option is called 

select_analysis_= fA

the -f o option is called 

select_analysis_= fo,  and so forth.

In addition, there are three other options I believe to be mutually exclusive with 
-f <whatever> that can  also be selected with the "select_analysis" parameter. 
These are -J (Compute majority rule consensus tree), -y (generate a parsimony tree 
and quit), and -I (a posteriori bootstopping) and correspond to select_analysis_=J 
select_analysis_=y and select_analysis_=I respectively.

For select_analysis_=J, a second parameter, "specify_mr" is set. By default, if 
select_analysis_=J is submitted and no value for specify_mr_  is submitted, 
specify_mr_=MR (Majority rule). It can be set to MRE, Extended majority rule, STRICT, 
Strict Majority, MR_DROP, or STRICT_DROP.

Similarly, for select_analysis_=I, a second parameter aposterior_bootstopping_=  is used 
to specify the majority rule criteria. Default is autoMRE if select_analysis_=J is 
submitted and no value for specify_mr_  is submitted. Allowed values are autoFC, autoMR, 
autoMRE, and autoMRE_IGN. These are analogous to the auto-bootstopping values supported by raxml.
Bootstrapping: Setting select_analysis_= fo, fa, and fd enables the bootstrapping 
parameter  "choose_bootstrap"

You can set choose_bootstrap_=x (or b) to activate bootstrapping.

Then you can choose how bootstrapping is stopped with the choose_bootstop parameter. 
By "specify" a specific number of bootstraps or choose "bootstop" for automatic 
bootstopping.

choose_bootstop_=specify, means "use a specific number of bootstrap iterations".  
The default is 100 bootstraps it can be set to any integer up to 1000 using 

bootstrap_value_=<integer>

choose_bootstop__=bootstop means that raxml will stop the bootstrapping automatically.
The stopping criterion is chosen with the bootstopping_type parameter, where
allowed values are autoFC, autoMR, autoMRE, and autoMRE_IGN and the default is autoMRE. 
bootstopping_type_=autoMRE

Alternative runs: 
Alternative runs can be set using
specify_runs_=1

and the number of runs is set using

altrun_number_= any integer up to 1000. default is 10.

runs that require a tree via -t can be set using the Inputfile
treetop_=<file name>

and files with a bunch of tree topologies (-z) are set with 
bunchotops_=filename

Mesquite option

the mesquite option is enabled here using 

mesquite_=1 (default =0)

but it is not allowed on runs where bootstrapping or alternative runs are used, because
these runs call hybrid code. And hybrid code chokes on the mesquite option currently.

Generic parameter
The generic parameter allows users to enter something that isn’t supported by our interface.

generic_= <some parameter or parameters> 

there are restrictions on what can be added, and params that require file uploads are not
currently supported using the generic parameter 

Other common raxml options are available through fairly expected ways.

outgroup:

outgroup_=abc,def 

auxiliary file uploads: 

these are configured in testInputproperties
partition_=<partition filename>
constraint_=<constraint filename>
and so forth.
There are test examples in the read-only svn.
 They are found here:
https://svn.sdsc.edu/repo/scigap/trunk/rest/pycipres/testdata/tooltests/variants/raxmlhpc8_xsede

Samples:
A rapid bootstrap example:
toolId=RAXMLHPC8_XSEDE
runtime_=0.50
dna_gtrcat_=GTRGAMMA
select_analysis_=fa
choose_bootstrap_=x
disable_seqcheck_=1
outgroup_=Hetinte
printbrlength_=1
and
infile_=infile.txt
produces: 
raxmlHPC-HYBRID (-T 8 will be added by the wrapper) -f a -n result -s infile.txt -N 100 -p 12345 -m GTRGAMMA -x 12345 -O -k  -o Hetinte 

Ancestral states example:
toolId=RAXMLHPC8_XSEDE
runtime_=0.50
dna_gtrcat_=GTRGAMMA
select_analysis_=fA
outsuffix_=result
disable_seqcheck_=1
also:
infile_=infile.txt
treetop_=tree.tre
produces:
raxmlHPC-PTHREADS (-T 8 will be added by the wrapper) -f A -n result -s infile.txt -t tree.tre -p 12345 -m GTRGAMMA -O
toolId=RAXMLHPC8_XSEDE
runtime_=0.50
specify_runs_=1
altrun_number_=1
dna_gtrcat_=GTRGAMMA
parsimony_seed_val_=362
outsuffix_=infile
disable_seqcheck_=1
also
infile_=infile.txt
produces:
      raxmlHPC-HYBRID (-T 8)  -f d -m GTRGAMMA -N 1 -O -p 362  -s infile.txt -n infile
