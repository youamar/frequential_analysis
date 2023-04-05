import string
from math import gcd
import functools, operator

def cesar_cipher(key, text, mode):
    """
    This function applies the Caesar cipher on a given text using the specified key and mode of operation.
    The Caesar cipher is a substitution cipher where each letter in the plaintext is shifted by a fixed number of positions down the alphabet.
    The key is an integer indicating the number of positions to shift.
    The mode parameter is a string, either 'encode' or 'decode', specifying whether to encrypt or decrypt the text, respectively.
    """
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    shift = -key if mode == 'decode' else key
    return ''.join(alphabet[(alphabet.find(letter) + shift) % 26] if letter in alphabet else letter for letter in text)

def vernam_cipher(text, key):
    """
    This function applies the Vernam cipher on a given text using the specified key.
    The Vernam cipher is a type of encryption that uses the XOR operation between each character of the plaintext and the corresponding character in the key.
    The key is repeated until it matches the length of the plaintext.
    """
    key = (key * (len(text) // len(key) + 1))[:len(text)]
    return ''.join(chr(ord(a) ^ ord(b)) for a, b in zip(text, key))

def find_cesar_key(text):
    """
    This function finds the Caesar cipher key used to encrypt a given text.
    The function uses the frequency analysis technique to find the most frequent letter in the text and calculates
    the key based on the difference between its index and the index of the most frequent letter in the English alphabet.
    """
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    progress = 0
    print(f"Progress: {progress}%", end='', flush=True)
    letter_frequencies = {letter: text.count(letter) for letter in alphabet if letter in text}
    progress = 50
    print(f"\rProgress: {progress}%", end='', flush=True)
    sorted_frequencies = sorted(letter_frequencies.items(), key=lambda x: x[1], reverse=True)
    most_frequent_letter = sorted_frequencies[0][0]
    most_frequent_letter_index = alphabet.index(most_frequent_letter)
    key = (most_frequent_letter_index - alphabet.index('e')) % 26
    progress = 100
    print(f"\rProgress: {progress}%", end='', flush=True)
    print()
    return key

def find_vernam_key(ciphertext):
    """
    This function finds the key used to encrypt a given ciphertext using the Vernam cipher.
    The function uses the index of coincidence (IOC) method to analyze the frequency distribution of the plaintext characters.
    The key length is first determined by finding the repeating trigrams in the ciphertext, and then by computing the
    greatest common divisor (GCD) of the distances between the trigrams.
    The key is then found by applying the Vernam cipher with each possible character and computing the IOC for each result, selecting the
    character with the highest IOC for each position in the key.
    """
    frequencies = [8.167, 1.492, 2.782, 4.253, 12.702, 2.228, 2.015, 6.094, 6.966,
                   0.153, 0.772, 4.025, 2.406, 6.749, 7.507, 1.929, 0.095, 5.987, 
                   6.327, 9.056, 2.758, 0.978, 2.360, 0.150, 1.974, 0.074]
    key_length = find_key_length(ciphertext)
    blocks = [ciphertext[i::key_length] for i in range(key_length)]
    key = ""
    block_count = len(blocks)
    for i, block in enumerate(blocks):
        max_ioc = -1
        best_char = None
        for j in range(26):
            char = chr(ord('a') + j)
            plaintext = vernam_cipher(block, char * len(block))
            freqs = [plaintext.count(letter) / len(plaintext) for letter in string.ascii_lowercase]
            ioc = sum(freq * frequencies[j] / 100 for j, freq in enumerate(freqs))
            if ioc > max_ioc:
                max_ioc = ioc
                best_char = char
        key += best_char
        progress = (i + 1) / block_count * 100
        print(f"\rProgress: {int(progress)}%", end='', flush=True)
    return key

def find_key_length(data):
    """
    This function finds the length of the key used to encrypt a given ciphertext using the Vernam cipher.
    The function uses the index of coincidence (IOC) method to analyze the frequency distribution of the plaintext characters.
    The key length is first determined by finding the repeating trigrams in the ciphertext, and then by computing the greatest
    common divisor (GCD) of the distances between the trigrams.
    """
    list = []
    chars = data
    for size in range(3, 4):
        beginPattern = 0
        map = {}
        while beginPattern < len(chars) - size + 1:
            pattern = ''.join(chars[beginPattern + shift] for shift in range(size))
            population = map.get(pattern, 0) + 1
            map[pattern] = population
            beginPattern += 1
        list.append(map)
    repeatingTrigrams = find_repeating_trigrams(list)
    indexesOfRepeatedTrigrams = []
    listOfGcd = []
    find_distances_of_repetition(data, repeatingTrigrams, indexesOfRepeatedTrigrams, listOfGcd)
    array = [listOfGcd[i] for i in range(len(listOfGcd))]
    map = {}
    for i in array:
        count = map.get(i, 0)
        map[i] = count + 1
    frequentGcd = max(map, key=map.get)
    listOfSets = []
    primeArray = find_prime_factors(frequentGcd)
    find_power_set(primeArray, listOfSets)
    best = max_set(listOfSets)
    return product(best)

def find_repeating_trigrams(lst):
    """
    This function finds the repeating trigrams in a list of dictionaries, where each dictionary contains the frequency of occurrence of trigrams in a block of text.
    """
    return [key for d in lst for key, value in d.items() if value > 1]

def find_distances_of_repetition(data, repeatingTrigrams, indexesOfRepeatedTrigrams, listOfGcd):
    """
    This function takes four arguments as input and returns a list of distances between the repetitions of the given trigrams.
    """
    distancesOfRepetition = [calculate_distances(find_indexes(trigram, data)) for trigram in repeatingTrigrams]
    listOfGcd.extend([find_all_gcd(distances) for distances in distancesOfRepetition])
    indexesOfRepeatedTrigrams.extend(distancesOfRepetition)
    return distancesOfRepetition

def calculate_distances(indexes):
    """
    This function takes a list of indexes as input and returns a list of distances between the indexes.
    """
    return [indexes[i+1] - indexes[i] for i in range(len(indexes)-1)]

def find_all_gcd(distances):
    """
    This function takes a list of distances as input and returns the greatest common divisor of the distances.
    """
    r = distances[0]
    for e in distances:
        r = gcd(r, e)
        if r == 1:
            return r
    return r

def find_indexes(trigram, data):
    """
    This function takes a trigram and a string as input and returns a list of indexes where the trigram is found in the string.
    """
    o = []
    i = data.find(trigram)
    while i >= 0:
        o.append(i)
        i = data.find(trigram, i+1)
    return o

def find_prime_factors(nb):
    """
    This function takes a positive integer nb and returns a list of its prime factors.
    The function first initializes an empty list res, then loops through all integers
    between 2 and nb-1 (inclusive) and divides nb by each integer to see if it is a factor.
    If it is a factor, the factor is appended to the list res and nb is divided by the factor.
    This process repeats until nb is reduced to 1 or the loop reaches the end.
    """
    res = []
    for i in range(2, nb):
        while nb % i == 0:
            res.append(i)
            nb //= i
    if nb > 1:
        res.append(nb)
    return res

def find_power_set(S, listofsets):
    """
    This function takes a list S of unique elements and returns a list of all possible subsets of S (including the empty set and S itself).
    The function first sorts the list S in ascending order, then initializes an empty list out
    to hold the subsets, and calls a helper function print_power_set with arguments S, out, and len(S) - 1.
    """
    S.sort()
    out = []
    print_power_set(S, out, len(S) - 1, listofsets)

def print_power_set(S, out=[], i=None, listofsets=[]):
    """
    This is a helper function for find_power_set.
    It recursively generates all subsets of the list S, and appends each subset to the list out.
    The function takes the arguments S, a list of unique elements; out, an empty list to hold the subsets; i, the index of the
    current element being considered (initialized to len(S)-1); and listofsets, an empty list to hold the subsets.
    The function first checks if i is less than 0. If it is, it means all elements of S have been considered and the current subset can be added to the list out.
    The function then appends a copy of out to listofsets, so that changes to out after this point do not affect the subsets that have already been generated.
    If i is not less than 0, the function calls itself twice: once without including the element at index i, and once including the element at index i.
    The function then pops the last element from out to restore it to its previous state.
    """
    if i is None:
        i = len(S) - 1
    if i < 0:
        listofsets.append(out[:])
        return
    print_power_set(S, out, i-1, listofsets)
    out.append(S[i])
    print_power_set(S, out, i-1, listofsets)
    out.pop()
    while i > 0 and S[i] == S[i-1]:
        i -= 1
    print_power_set(S, out, i-1, listofsets)

def max_set(sets):
    """
    This function takes a list of sets as input and returns the set with the largest product of its elements that is less than or equal to 26.
    """
    max_set = max((s for s in sets if 0 < (p:=product(s)) <= 26), default=[])
    return list(max_set)
    
def product(set):
    """
    This function takes a set as input and returns the product of its elements.
    """
    return 0 if not set else functools.reduce(operator.mul, set)