#include <iostream>
#include <sstream>
#include <string>
#include "util.h"

using namespace std;

string trim(string s) {
	stringstream ss;
	ss << s;
	ss >> s;
	return s;
}