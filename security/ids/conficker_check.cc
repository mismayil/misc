#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <ctime>
#include <cmath>
#include <cstdlib>
#include "conficker_check.h"
#include "util.h"

using namespace std;

unsigned long long prng_key;
string TLDLIST[] = {"com", "net", "org", "info", "biz"};
vector<string> fake_domains;

int rand_val() {
	double res1;
	double d2;
	double s_val;
	unsigned long long prod;

	d2 = prng_key;
	prod = prng_key * CON_RAND;

	s_val = sin(d2);

	res1 = ((((prod + s_val) * d2) + CON_DBL) * d2);
	res1 += log(d2);

	*(double*)&prng_key = res1;

	return prng_key;
}

void generate_conficker() {

	time_t timestamp = time(0);
	struct tm *now = gmtime(&timestamp);
	now->tm_hour = 0;
	now->tm_min = 0;
	now->tm_sec = 0;

	unsigned long long filetime = (mktime(now) + 11644473600LL) * 10000000;
	
	const unsigned long long const1 = 18855122550LL, const2 = 6048000000000LL, const3 = 3026875959LL;

	prng_key = filetime * const1 / const2 + const3;
	
	int n;
	string domain, tld;

	for (int i = 0; i < 250; ++i) {
		n = abs(rand_val()) % 4 + 8;
		domain = "";

		for(int j = 0; j < n; j++) {
			domain += (char) (abs(rand_val()) % 26) + 97;
		}

		tld = TLDLIST[abs(rand_val()) % 5];
		domain += "." + tld;
		fake_domains.push_back(domain);
	}
}