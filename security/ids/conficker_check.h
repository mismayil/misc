#ifndef CONFICKER_CHECK_H
#define CONFICKER_CHECK_H

#include <string>
#include <vector>

#define CON_RAND 1680041781
#define CON_DBL 0.737565675

extern std::vector<std::string> fake_domains;

void generate_conficker();

#endif