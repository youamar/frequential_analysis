def cesar(text, key, mode):
    #file = open('./files/uncipher.txt', 'r')
    #for line in file:
    #    print(line)
    #file.close()
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    result = ''
    for letter in text:
        if letter in alphabet:
            letter_index = alphabet.find(letter)
            if mode == 'encode':
                result += alphabet[(letter_index + key) % 26]
            elif mode == 'decode':
                result += alphabet[(letter_index - key) % 26]
        else:
            result += letter
    print(result)
    return result

cesar('hello', 1, 'encode')