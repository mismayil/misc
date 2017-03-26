ids: ids.o util.o addr_check.o access_check.o host_check.o random_scan.o codered_check.o conficker_check.o
	g++ ids.o util.o addr_check.o access_check.o host_check.o random_scan.o codered_check.o conficker_check.o -o ids

ids.o: ids.cc addr_check.h addr_check.cc access_check.h access_check.cc host_check.h host_check.cc random_scan.h random_scan.cc codered_check.h codered_check.cc conficker_check.h conficker_check.cc util.h util.cc
	g++ -g -c ids.cc

conficker_check.o: conficker_check.h conficker_check.cc util.h util.cc
	g++ -g -c conficker_check.cc

codered_check.o: codered_check.h codered_check.cc util.h util.cc
	g++ -g -c codered_check.cc

random_scan.o: random_scan.h random_scan.cc util.h util.cc
	g++ -g -c random_scan.cc

host_check.o: host_check.h host_check.cc util.h util.cc
	g++ -g -c host_check.cc

access_check.o: access_check.h access_check.cc util.h util.cc
	g++ -g -c access_check.cc 

addr_check.o: addr_check.h addr_check.cc util.h util.cc
	g++ -g -c addr_check.cc 

util.o: util.h util.cc
	g++ -g -c util.cc

clean:
	rm *.o
	rm ids