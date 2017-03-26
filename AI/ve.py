# Variable Elimination library

'''
Factor is one dimensional array of probabilities with the variable names as a string in the first element
The order of variables in the string determines the values of those variables at specific index
For example, if the factor is ['XY', 0.1, 0.2, 0.3, 0.4], then 0.1 corresponds to FF, 0.2 to FT, 0.3 to TF and 0.4 to TT
where F is False and T is True. It supports only binary variables for now.
'''

# restricts variable to some value in a given factor
def restrict(factor, var, val):
    f = [factor[v+1] for v in range(0, len(factor)-1) if var in factor[0] and (v >> (len(factor[0]) - factor[0].index(var) - 1)) & 1 == val]
    index = factor[0].find(var)
    if f != []: return [factor[0][0:index] + factor[0][index+1:len(factor[0])]] + f
    return factor

# multiplies two factors
def multiply(factor1, factor2):
    print 'multiplying', factor1, 'by', factor2
    allvars = factor1[0] + factor2[0]
    product = []
    cartesian = []
    cartesian.append(allvars)
    product.append(''.join([j for i,j in enumerate(cartesian[0]) if j not in cartesian[0][:i]]))

    for v1 in range(0, len(factor1)-1):
        for v2 in range(0, len(factor2)-1):
            cartesian.append(factor1[v1+1] * factor2[v2+1])

    for p in range(0, len(cartesian)-1):
        valid = True
        for v in range(0, len(cartesian[0])):
            for n in range(v+1, len(cartesian[0])):
                if cartesian[0][v] == cartesian[0][n]:
                    np = (len(allvars) - len(bin(p)[2:])) * '0' + bin(p)[2:]
                    if np[v] != np[n]:
                        valid = False
                        break
            if not valid: break
        if valid: product.append(cartesian[p+1])

    print 'product =', product
    return product

# sums out a variable
def sumout(factor, var):
    print 'summing out ', factor, 'to', var
    vars = factor[0]
    index = vars.index(var)
    nfactor = [factor[0][:index] + factor[0][index+1:]]

    for v in range(0, (len(factor) - 1)):
        found = False
        vtmp = bin(v)[2:]
        vtmp = (len(vars) - len(vtmp)) * '0' + vtmp
        vrest = vtmp[:index] + vtmp[index+1:]
        sum = factor[v+1]

        for n in range(v + 1, len(factor) - 1):
            ntmp = bin(n)[2:]
            ntmp = (len(vars) - len(ntmp)) * '0' + ntmp
            nrest = ntmp[:index] + ntmp[index+1:]

            if nrest == vrest:
                sum += factor[n+1]
                found = True

        if found: nfactor.append(sum)

    print 'sum =', nfactor
    return nfactor

# normalize a factor
def normalize(factor):
    print 'normalizing', factor
    sum = 0

    for p in factor[1:]:
        sum += p

    for v in range(0, len(factor)-1):
        factor[v+1] /= sum

    print 'normalized:', factor
    return factor

# computes P(qvars | evs) by variable elimination
def inference(factors, qvars, hvars, evs):

    # restrict factors according to evidences
    print 'factor list before restriction to evidence list:'
    print factors
    for e in evs:
        for f in range(0, len(factors)):
            r = restrict(factors.pop(f), e[0], e[1])
            factors.insert(f, r)
    print 'factor list after restriction to evidence list:'
    print factors

    # sumout hidden variables from product of the factors in factor list
    plist = []
    for v in hvars:
        for f in factors[:]:
            if v in f[0]:
                factors.remove(f)
                plist.append(f)

        if plist != []:
            product = plist[0]

            for p in range(1, len(plist)):
                product = multiply(plist[p], product)

            factors.append(sumout(product, v))
            plist = []

    product = factors[0]
    for f in factors[1:]:
        product = multiply(f, product)

    return normalize(product)

# T = Trav
# F = Fraud
# O = OCR
# G = FP
# I = IP
# C = CRP
f1 = ['T', 0.95, 0.05]
f2 = ['TF', 0.996, 0.004, 0.99, 0.01]
f3 = ['O', 0.25, 0.75]
f4 = ['TFG', 0.99, 0.01, 0.9, 0.1, 0.1, 0.9, 0.1, 0.9]
f5 = ['FOI', 0.999, 0.001, 0.99, 0.01, 0.989, 0.011, 0.98, 0.02]
f6 = ['OC', 0.999, 0.001, 0.9, 0.1]
print 'bayesian network factors:'
print f1
print f2
print f3
print f4
print f5
print f6

print '\n'

print '1. prior probability that current transaction is fraud:'
print inference([f1, f2], ['F'], ['T'], [])
print '\n'

print '2. probability that current transaction is a fraud if foreign transaction, not internet purchase, computer related accessory purchased last week:'
print inference([f1, f2, f3, f4, f5, f6], ['F'], ['T', 'G', 'I', 'O', 'C'], [('G', 1), ('I', 0), ('C', 1)])
print '\n'

print '3.probability that current transaction is a fraud if foreign transaction, not internet purchase, computer related accessory purchased last week and card holder is traveling:'
print inference([f1, f2, f3, f4, f5, f6], ['F'], ['T', 'G', 'I', 'O', 'C'], [('G', 1), ('I', 0), ('C', 1), ('T', 1)])
print '\n'

print '4.probability that current transaction is a fraud if an internet purchase and computer related accessory purchased last week:'
print inference([f1, f2, f3, f4, f5, f6], ['F'], ['T', 'G', 'I', 'O', 'C'], [('I', 1), ('C', 1)])
