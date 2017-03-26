#!/usr/bin/python

from decrypt import *

#main
if __name__ == "__main__":
	plaintext = sys.argv[1]
	hexp = binascii.hexlify(plaintext)
	n = BLOCK_SIZE - len(plaintext) % BLOCK_SIZE

	for i in xrange(n):
		hexp += pad(hex(i+1)[2:], 2)

	pblocks = get_blocks(hexp)

	cblocks = []
	n = len(pblocks)
	c = random_block(BLOCK_SIZE)
	cblocks.append(c)

	for i in xrange(n):
		b = block_oracle(c)
		c = xor(b, pblocks[n-i-1])
		cblocks.insert(0, c)

	ciphertext = ""
	for i in xrange(len(cblocks)):
		ciphertext += binascii.unhexlify(ints_to_hexs(cblocks[i]))

	print base64.b64encode(ciphertext)

