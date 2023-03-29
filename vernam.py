import argparse
from util import preprocess, secret

def vernam(input_file, output_file, key=None, mode='encode'):
    with open(input_file, 'r', encoding='utf-8') as f:
        text = preprocess.sanitize_to_alpha(preprocess.remove_diacritics(f.read()))
    
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    sanitized_key = None
    if key is not None:
        sanitized_key = preprocess.sanitize_to_alpha(preprocess.remove_diacritics(key))
    elif mode == 'encode':
        raise ValueError("Cannot encode without a key.")
    else:
        # sanitized_key = # todo find most likely key
        print("Using default key for decoding.")
    
    new_key = secret.extend_key(sanitized_key, text)
    
    result = ''
    for i, char in enumerate(text):
        if char not in alphabet:
            result += char
            continue
        letter_index = alphabet.find(char)
        key_index = alphabet.find(new_key[i])
        result += alphabet[secret.bitwise_xor(letter_index, key_index)]
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(result)
    
    return result


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Vernam cipher encoder/decoder')
    parser.add_argument('input_file', type=str, help='Input file')
    parser.add_argument('output_file', type=str, help='Output file')
    parser.add_argument('--key', type=str, help='Key to use for encoding/decoding')
    parser.add_argument('--mode', type=str, help='Mode to use (encode/decode)', default='encode')
    args = parser.parse_args()
    vernam(args.input_file, args.output_file, args.key, args.mode)