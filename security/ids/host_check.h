#ifndef HOST_CHECK_H
#define HOST_CHECK_H

#include <string>
#include <vector>

extern std::vector<std::string> malhosts;

void load_hosts();
void check_host(std::string src, std::string dst, std::string line);

#endif