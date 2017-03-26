#ifndef RANDOM_SCAN_H
#define RANDOM_SCAN_H

#include <string>
#include <vector>
#include <map>

#define PKT_LIMIT 10
#define EXP_TIME 2

extern std::map<std::string, struct tracker*> trackmap;

struct tracker {
	std::string timestamp;
	int time_elapsed;
	std::vector<std::string> tcp_dsts;
	std::vector<std::string> last_tcp_dsts;
	std::vector<std::string> udp_dsts;
	std::vector<std::string> last_udp_dsts;
};

void random_scan(std::string src, std::string dst, std::string proto, std::string timestamp, std::string line);

#endif