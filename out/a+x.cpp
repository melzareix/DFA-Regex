// #######################################################################
// AUTOGENERATED FILE DON'T MODIFY.
// FILE IS AUTOGENERATED FROM Main.java TO RECOGNIZE THE REGEX a+x.
// #######################################################################
#include <iostream>
#include <string>

int main(int argc, char* argv[]) {
	int result = -1;
	int idx = 0;
	char c;
	std::string str(argv[1]);
	state3:
		if (str.size() <= idx) {
			 std::cout << result << std::endl;
			 return 0;
		}
		c = str[idx++];
		if (c == 'a') {
			goto state2;
		}
		goto state1;
	state0:
		// final state
		result = idx;
		std::cout << "Found starting at:" << result << std::endl;
		return 0;
	state2:
		if (str.size() <= idx) {
			 std::cout << result << std::endl;
			 return 0;
		}
		c = str[idx++];
		if (c == 'a') {
			goto state2;
		}
		if (c == 'x') {
			goto state0;
		}
		goto state1;
	state1:
		// reject state
		std::cout << "Failed to match:" << result << std::endl;
		return 0;
}
