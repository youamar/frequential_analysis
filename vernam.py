import argparse
from util import preprocess, secret

def vernam(input_file, output_file, key=None, mode='encode'):
    with open(input_file, 'r', encoding='utf-8') as file:
        text = file.read()
    
    text_no_dia = preprocess.remove_diacritics(text)
    sanitized_text = preprocess.sanitize_to_alpha(text_no_dia)
    key_no_dia = preprocess.remove_diacritics(key)
    sanitized_key = preprocess.sanitize_to_alpha(key_no_dia)
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    
    if sanitized_key is None:
        if mode == 'encode':
            raise ValueError("Cannot encode without a key.")
        else:
            #sanitized_key = #todo find most likely key
            print(f"Using key {sanitized_key} for decoding.")
    
    new_key = secret.extend_key(sanitized_key, sanitized_text)
    
    result = ''
    for i in range(len(sanitized_text)):
        if sanitized_text[i] in alphabet:
            letter_index = alphabet.find(sanitized_text[i])
            key_index = alphabet.find(new_key[i])
            if mode == 'encode':
                result += alphabet[secret.bitwise_xor(letter_index, key_index)]
            elif mode == 'decode':
                result += alphabet[secret.bitwise_xor(letter_index, key_index)]
        else:
            result += sanitized_text[i]
    
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write(result)
    
    return result

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Vernam cipher encoder/decoder')
    parser.add_argument('input_file', type=str, help='Input file')
    parser.add_argument('output_file', type=str, help='Output file')
    parser.add_argument('--key', type=str, help='Key to use for encoding/decoding')
    parser.add_argument('--mode', type=str, help='Mode to use (encode/decode)', default='encode')
    args = parser.parse_args()
    vernam(args.input_file, args.output_file, args.key, args.mode)