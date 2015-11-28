#!/usr/bin/python

import sys, getopt, re, subprocess, shlex

def main(argv):
	outputfile = '~/result.csv'
	min_asn = 100
	max_asn = 65665
	
	try:
		opts, args = getopt.getopt(argv,"ho:",["ofile="])
	except getopt.GetoptError:
		print 'as_data_extractor -o <path/to/outputfile> min_as max_as'
		sys.exit(2);
	for opt, arg in opts:
		if opt == '-h':
			print 'as_data_extractor [-o <path/to/outputfile>] min_as max_as'
			sys.exit(2);
		elif opt in ("-o", "--ofile"):
			outputfile = arg
			
	if(len(args) >= 2):
		min_asn = int(args[0])
		max_asn = int(args[1])
	else:
		print 'supply two arguments for working range, falling back to default range (' + str(min_asn) + ' - ' + str(max_asn) + ')'
	
	print 'Output file is "', outputfile
	print 'min_asn = %d' % min_asn
	print 'max_asn = %d' % max_asn
	
	flush_data = []
	currant_progress = 0
	
	print 'AS Scanner starting...'
	
	for a in range(min_asn, max_asn + 1):
		as_data_arg = 'AS' + str(a)
		subnet_arg = '"-i origin ' + as_data_arg + '"'
		p_data_out = subprocess.check_output(['whois', '-h', 'whois.radb.net', '--', as_data_arg])
		p_data_fields = p_data_out.splitlines()
		if(len(p_data_fields) <= 5):
			continue
		line_result = as_data_arg + ','
		as_name = ''
		descr = []
		imports = []
		exports = []
		for line in p_data_fields:
			words = line.split()
			if(len(words) < 2):
				continue
			if words[0]	== 'as-name:':
				as_name = words[1]
			elif words[0] == 'descr:':
				add_as_data_line(descr, words, 1, len(words))
			elif words[0] == 'import:':
				add_as_data_line(imports, words, 2, 3)
			elif words[0] == 'export:':
				add_as_data_line(exports, words, 2, 3)
		
		p_subnet_out = subprocess.check_output(['whois -h whois.radb.net -- ' + subnet_arg + ' | grep -Eo "([0-9.]+){4}/[0-9]+" | head'], shell=True)
		subnets = p_subnet_out.splitlines()

		if(len(subnets) == 0):
			continue
		
		final_line = ''
		final_line += as_data_arg + ','
		final_line += as_name + ','
		for d in descr:
			final_line += ' ' + d
		final_line += ',imports'
		for i in imports:
			final_line += ' ' + i
		final_line += ',exports'
		for e in exports:
			final_line += ' ' + e
		final_line += ',subnets'
		for s in subnets:
			final_line += ' ' + s
			
		print final_line
		flush_data.append(final_line)
	
	print 'AS Scanner completed'
	print 'Output file write starting...'
	
 	with open(outputfile, 'w+') as out_f:
 		for line in flush_data:
 			out_f.write(line + '\n')
			
	print 'Output file write complete'

def add_line(results, line):
	for word in re.split(',\s|,|\s', line):
		if len(word) > 3:
			results.append(word.strip())
			
def add_as_data_line(results, words, start_index, end_index):
		for i in range(start_index, end_index):
			results.append(words[i].replace(',', ';'))
	

if __name__ == "__main__":
	main(sys.argv[1:])
