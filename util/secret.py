def find_key(text):
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    letter_frequencies = {}
    for letter in text:
        if letter in alphabet:
            if letter in letter_frequencies:
                letter_frequencies[letter] += 1
            else:
                letter_frequencies[letter] = 1
    sorted_frequencies = sorted(letter_frequencies.items(), key=lambda x: x[1], reverse=True)
    most_frequent_letter = sorted_frequencies[0][0]
    most_frequent_letter_index = alphabet.find(most_frequent_letter)
    key = (most_frequent_letter_index - alphabet.find('e')) % 26
    return key

def extend_key(key, text):
    long_key = ''
    for i in range(len(text)):
        long_key += key[i % len(key)]
    return long_key

def bitwise_xor(a, b):
    if((a^b)>=26):
        return (a^b)-26
    return a^b