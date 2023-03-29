def find_key(text):
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    
    # creating a dictionary of letter frequencies
    # note : the `count` method of strings is used to count the number of occurrences of each letter in the text
    letter_frequencies = {letter: text.count(letter) for letter in alphabet if letter in text}
    
    # sorting in descending order of frequency
    sorted_frequencies = sorted(letter_frequencies.items(), key=lambda x: x[1], reverse=True)
    
    # getting the most frequent letter from the sorted list
    most_frequent_letter = sorted_frequencies[0][0]
    
    # getting the index of the most frequent letter in the alphabet using the `index` method of strings
    most_frequent_letter_index = alphabet.index(most_frequent_letter)
    
    # calculating the distance between the most frequent letter and 'e'
    key = (most_frequent_letter_index - alphabet.index('e')) % 26
    
    return key

def extend_key(key, text):
    n = len(key)
    long_key = key * (len(text) // n) + key[:len(text) % n]
    return long_key

def bitwise_xor(a, b):
    return (a^b) % 26