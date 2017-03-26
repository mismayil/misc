#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include "host_check.h"
#include "conficker_check.h"
#include "util.h"

using namespace std;

vector<string> malhosts;

void load_hosts() {

	ifstream f("domains.txt");

	string line;

	while (getline(f, line)) {
		malhosts.push_back(trim(line));
	}

	f.close();
}

void check_host(string src, string dst, string line) {

	size_t pos_dot = dst.find_last_of('.');
	string dstport = dst.substr(pos_dot+1, dst.length()-pos_dot-1);

	if (dstport == "53" && line.find("udp") != string::npos && line.find("A?") != string::npos) {
		
		size_t pos_question = line.find("?");
		string buf = line.substr(pos_question+1, line.length()-pos_question-1);
		pos_dot = buf.find_last_of(".");
		string host = trim(buf.substr(0, pos_dot));

		for (int i = 0; i < malhosts.size(); i++) {
			if (host == malhosts[i]) {
				cout << "[Malicious host lookup]: src:" << src << ", host:" << host << endl;
				cout.flush();
				break;
			}
		}

		for (int i = 0; i < fake_domains.size(); i++) {
			if (host == fake_domains[i]) {
				cout << "[Conficker worm]: src:" << src << endl;
				cout.flush();
				return;
			}
		}
	}
}