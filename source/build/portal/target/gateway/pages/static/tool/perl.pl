#!/usr/bin/perl
opendir(DIR, "./tool_original") or die "$!";
my @files = grep {/html$/} readdir DIR;
close DIR;
foreach my $file (@files)
{
	print "$file....";
	open (FH, "./tool_original/$file") or die "$!";
	open (OUT, ">$file") or die "$!";

	#print OUT "<head>\n\t<meta name=\"menu\" content=\"Toolkit\"/>\n</head>\n";
	while (<FH>)
	{
		s/<a href=\"\w+\.php\"/<a href="#"/;
		s/\/tool_tests/tool_tests/g;
		print OUT $_;
	}
	close(OUT);
}
closedir(DIR);
