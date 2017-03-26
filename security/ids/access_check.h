#ifndef ACCESS_CHECK_H
#define ACCESS_CHECK_H

#include <string>

void load_kwrange();
void check_access(std::string src, std::string dst, std::string line);

#endif