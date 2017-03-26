#include <iostream>
#include <sstream>
#include <fstream>
#include <vector>
#include <string>
#include "access_check.h"
#include "util.h"

using namespace std;

vector<string> kw_range;

string get_bits(string byte) {
	int dec;
    stringstream ss;
    ss << byte;
    ss >> dec;
    ss.str("");
    ss.clear();

    for(int i = 7; i >= 0; i--) {
    	ss << ((dec >> i) & 1);
    }

    return ss.str();
}

string get_subnet_bits(string ipaddr, int subnet) {

	stringstream ss;

	ss << ipaddr;
	string byte, bits;
	string bitaddr = "";

	while (getline(ss, byte, '.')) {
		bits = get_bits(byte);
		bitaddr += bits;
	}

	return bitaddr.substr(0, subnet);
}

bool in_kwrange(string addr) {

	for(int i = 0; i < kw_range.size(); i++) {
		
		int size = kw_range[i].length();
	
		if (get_subnet_bits(addr, size) == kw_range[i]) return true;
	}

	return false;
}

void load_kwrange() {

	ifstream f("GeoLiteCity.csv");

	string line, ipaddr;
	stringstream ss;

	int subnet;
	size_t pos_slash;

	while (getline(f, line)) {
		if (line.find("Canada") != string::npos && 
		   (line.find("Kitchener") != string::npos || line.find("Waterloo") != string::npos)) {

			ss << line;
			getline(ss, line, ',');
			ss.str("");
			ss.clear();

			pos_slash = line.find("/");
			ipaddr = line.substr(0, pos_slash);
			ss << line.substr(pos_slash+1, line.length()-pos_slash-1);
			ss >> subnet;

			ss.str("");
			ss.clear();

			line = get_subnet_bits(ipaddr, subnet);
			kw_range.push_back(line);
		}
	}

	f.close();
}

void check_access(string src, string dst, string line) {
	
	string srcip = src.substr(0, src.find_last_of('.'));
	string dstip = dst.substr(0, dst.find_last_of('.'));

	if (line.find("Flags [S]") != string::npos) {
		if (srcip.substr(0, 5) != "10.97" && !in_kwrange(srcip) && dstip.substr(0, 5) == "10.97") {
			cout << "[Attempted server connection]: rem:" << src << ", srv:" << dst << endl;
			cout.flush();
		}
	}

	if (line.find("Flags [S.]") != string::npos) {
		if (!in_kwrange(dstip) && srcip.substr(0, 5) == "10.97" && dstip.substr(0, 5) != "10.97") {
			cout << "[Accepted server connection]: rem:" << dst << ", srv:" << src << endl;
			cout.flush();
		}
	}
}