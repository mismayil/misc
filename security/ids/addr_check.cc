#include <iostream>
#include <string>
#include "addr_check.h"
#include "util.h"

using namespace std;

void check_addr(string src, string dst) {
	if (src.substr(0, 5) != "10.97" && dst.substr(0, 5) != "10.97") {
		cout << "[Spoofed IP address]: src:" << src << ", dst:" << dst << endl;
		cout.flush();
	}
}