def vernam(text, key):
    text = [bin(ord(i))[2:].zfill(8) for i in text]
    key = [bin(ord(i))[2:].zfill(8) for i in key]
    encoded = [bin(int(text[i], 2) ^ int(key[i], 2))[2:].zfill(8) for i in range(len(text))]
    encoded = [chr(int(i, 2)) for i in encoded]
    return ''.join(encoded)

vernam('helloworld', 'helloworld')