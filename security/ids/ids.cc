#include <iostream>
#include <string>
#include <map>
#include "util.h"
#include "addr_check.h"
#include "access_check.h"
#include "host_check.h"
#include "random_scan.h"
#include "codered_check.h"
#include "conficker_check.h"

using namespace std;

void run_ids() {

	string line, buf, proto, timestamp, data;
	size_t pos_arrow, pos_colon;

	load_kwrange();
	load_hosts();
	generate_conficker();

	getline(cin, line);

	while (1) {

		if (cin.eof()) break;

		data = "";
		buf = trim(line);

		if (line.find("proto") != string::npos) {

			if (line.find("TCP") != string::npos) proto = "TCP";
			if (line.find("UDP") != string::npos) proto = "UDP";

			buf = trim(line);
			timestamp = buf.substr(0, buf.find("."));

			getline(cin, line);

			pos_arrow = line.find(">");
			pos_colon = line.find(":");

			string src = line.substr(0, pos_arrow);
		    string dst = line.substr(pos_arrow+1, pos_colon-pos_arrow-1);

		    src = trim(src);
		    dst = trim(dst);

		    check_addr(src, dst);
			check_access(src, dst, line);
			check_host(src, dst, line);
		    random_scan(src, dst, proto, timestamp, line);

		    while(getline(cin, line)) {
		    	
		    	buf = trim(line);
		    	if (buf.find("0x") == string::npos) break;

		    	size_t pkt_start = line.length() - 16;
		    	data += line.substr(pkt_start, 16);
		    }

		    check_codered(src, dst, proto, data);

		} else getline(cin, line); 
	}

	trackmap.erase(trackmap.begin(), trackmap.end());
}

int main() {
	
	run_ids();
	return 0;
}