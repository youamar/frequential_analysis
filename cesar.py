import argparse
from util import preprocess, secret

def cesar(input_file, output_file, key=None, mode='encode'):
    with open(input_file, 'r', encoding='utf-8') as file:
        text = file.read()
    
    text_no_dia = preprocess.remove_diacritics(text)
    sanitized_text = preprocess.sanitize_to_alpha(text_no_dia)
    alphabet = 'abcdefghijklmnopqrstuvwxyz'
    
    if key is None:
        if mode == 'encode':
            raise ValueError("Cannot encode without a key.")
        else:
            key = secret.find_key(sanitized_text)
            print(f"Using key {key} for decoding.")
    
    result = ''
    for letter in sanitized_text:
        if letter in alphabet:
            letter_index = alphabet.find(letter)
            if mode == 'encode':
                result += alphabet[(letter_index + key) % 26]
            elif mode == 'decode':
                result += alphabet[(letter_index - key) % 26]
        else:
            result += letter

    with open(output_file, 'w', encoding='utf-8') as file:
        file.write(result)
    
    return result

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Cesar cipher encoder/decoder')
    parser.add_argument('input_file', type=str, help='Input file')
    parser.add_argument('output_file', type=str, help='Output file')
    parser.add_argument('--key', type=int, help='Key to use for encoding/decoding')
    parser.add_argument('--mode', type=str, help='Mode to use (encode/decode)', default='encode')
    args = parser.parse_args()
    cesar(args.input_file, args.output_file, args.key, args.mode)