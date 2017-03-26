#include <iostream>
#include <sstream>
#include <map>
#include <string>
#include "random_scan.h"
#include "util.h"

using namespace std;

map<string, struct tracker*> trackmap;

int find_time_diff(string oldtime, string newtime) {

	size_t old_pos_colon = oldtime.find_last_of(':');
	size_t new_pos_colon = newtime.find_last_of(':');

	if (oldtime.substr(0, old_pos_colon) == newtime.substr(0, new_pos_colon)) {
		stringstream ss;
		int old_sec, new_sec;
		ss << oldtime.substr(old_pos_colon+1, oldtime.length()-old_pos_colon-1);
		ss >> old_sec;
		ss.str("");
		ss.clear();
		ss << newtime.substr(new_pos_colon+1, newtime.length()-new_pos_colon-1);
		ss >> new_sec;

		return new_sec - old_sec;
	}

	return EXP_TIME + 1;
}

bool contains(vector<string> v, string s) {

	for(int i = 0; i < v.size(); i++) {
		if (v[i] == s) return true;
	}

	return false;
}

void random_scan(string src, string dst, string proto, string timestamp, string line) {

	struct tracker* t;
	string srcip = src.substr(0, src.find_last_of('.'));
	string dstip = dst.substr(0, dst.find_last_of('.'));

	if (proto == "TCP" && line.find("Flags [S]") == string::npos) return;

	if (trackmap.count(srcip) > 0) {

		t = trackmap.find(srcip)->second;
		int diff = find_time_diff(t->timestamp, timestamp);
		
		t->timestamp = timestamp;
		t->time_elapsed += diff;

		if (t->time_elapsed <= EXP_TIME) {
			
			if (diff == 1) {
				if (t->time_elapsed == EXP_TIME) t->time_elapsed = 1;
				t->tcp_dsts = t->last_tcp_dsts;
				t->last_tcp_dsts.clear();
				t->udp_dsts = t->last_udp_dsts;
				t->last_udp_dsts.clear();
			}

		} else {

			t->time_elapsed = 0;
			t->tcp_dsts.clear();
			t->last_tcp_dsts.clear();
			t->udp_dsts.clear();
			t->last_udp_dsts.clear();
		}

		if (proto == "TCP") {
			if (!contains(t->tcp_dsts, dstip)) t->tcp_dsts.push_back(dstip);
			if (!contains(t->last_tcp_dsts, dstip)) t->last_tcp_dsts.push_back(dstip);
		} else {
			if (!contains(t->udp_dsts, dstip)) t->udp_dsts.push_back(dstip);
			if (!contains(t->last_udp_dsts, dstip)) t->last_udp_dsts.push_back(dstip);
		}

		if (t->tcp_dsts.size() >= PKT_LIMIT || t->udp_dsts.size() >= PKT_LIMIT) {
			cout << "[Potential random scan]: att:" << srcip << endl;
			cout.flush();
			delete t;
			trackmap.erase(srcip);
		}

	} else {
		
		t = new tracker;
		t->timestamp = timestamp;
		t->time_elapsed = 0;

		if (proto == "TCP") {
			t->tcp_dsts.push_back(dstip);
			t->last_tcp_dsts.push_back(dstip);
		} else {
			t->udp_dsts.push_back(dstip);
			t->last_udp_dsts.push_back(dstip);
		}

		trackmap.insert(pair<string, struct tracker*>(srcip, t));
	}
}