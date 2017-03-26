#!/usr/bin/python

import sys
import base64
import binascii
import random
import httplib

# constants
BLOCK_SIZE = 16
UGSTER_MACHINE = "ugster20.student.cs.uwaterloo.ca:4555"
LOCALHOST = "localhost:4555"
HOST = LOCALHOST
OK = 200
NOT_FOUND = 404


def random_block(n):
	r = []

	for i in range(n):
		r.append(random.randint(0, 255))

	return r


def pad(s, n):
	return "0" * (n-len(s)) + s


def xor(a, b):

	if (len(a) != len(b)): 
		print "should not have happened"
		return

	x = []

	for i in range(len(a)):
		x.append(a[i] ^ b[i])

	return x

def ints_to_hexs(s):
	hexs = ""

	for t in s:
		hexs += pad(hex(t)[2:], 2)

	return hexs


def send(c):
	conn = httplib.HTTPConnection(HOST)
	conn.request("GET", "/", "", {"Cookie": "user=" + c})
	response = conn.getresponse()

	return response.status


def O(s):

	binary = binascii.unhexlify(ints_to_hexs(s))
	cookie = base64.b64encode(binary)

	status = send(cookie)

	if (status == OK or status == NOT_FOUND): 
		return 1
	else: 
		return 0

def last_word_oracle(y):
	t = random_block(BLOCK_SIZE)
	b = BLOCK_SIZE
	r = t[:]
	i = 0

	while (O(r+y) == 0):
		i += 1
		r[-1] = t[-1] ^ i

	for n in range(b, 1, -1):
		r[b-n] = r[b-n] ^ 1

		if O(r+y) == 0:

			k = 1
			for j in range(b-n, b):
				r[j] = r[j] ^ k
				k += 1

			return r[b-n:]

	return [r[-1] ^ 1]


def word_oracle(a, y):
	t = random_block(BLOCK_SIZE)
	j = BLOCK_SIZE - len(a)
	b = BLOCK_SIZE
	
	m = 0

	for k in range(j, b):
		t[k] = a[m] ^ (m + 2)
		m += 1

	i = 0
	r = t[:]

	while (O(r+y) == 0): 
		i += 1
		r[j-1] = t[j-1] ^ i

	return r[j-1] ^ 1


def block_oracle(y):
	p = last_word_oracle(y)
	j = BLOCK_SIZE - len(p)

	for i in range(j):
		p.insert(0, word_oracle(p, y))

	return p

def decrypt_oracle(b):
	d = []
	
	for i in range(len(b)):
		d.append(block_oracle(b[i]))

	return d

def get_blocks(h):
	num_blocks = len(h) / 2 / BLOCK_SIZE
	blocks = []

	for i in range(num_blocks):
	
		buf = h[i*BLOCK_SIZE*2:(i+1)*BLOCK_SIZE*2]
		block = []
	
		for j in range(BLOCK_SIZE):
			block.append(int(buf[j*2:(j+1)*2], 16))

		blocks.append(block)

	return blocks

def decrypt(ciphers, blocks):
	p = []

	for i in range(len(blocks)):
		p += xor(ciphers[i], blocks[i])

	hexs = ints_to_hexs(p)

	n = int(hexs[-2:], 16)
	hexs = hexs[:-(n*2)]	
	return binascii.unhexlify(hexs)


# main
if __name__ == "__main__":
	cookie = sys.argv[1]
	bincookie = base64.b64decode(cookie)
	hexcookie = binascii.hexlify(bincookie)
	ciphertexts = get_blocks(hexcookie)
	plaintext = decrypt(ciphertexts, decrypt_oracle(ciphertexts[1:]))
	print plaintext


