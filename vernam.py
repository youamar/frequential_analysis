import argparse
from util import secret

def vernam(input_file, output_file, key=None, mode='encode'):
    with open(input_file, 'r', encoding='utf-8') as f:
        text = f.read()

    if key is None and mode == 'encode':
        raise ValueError("Cannot encode without a key.")
    elif key is None:
        key = secret.find_vernam_key(text)
        print()

    print(f"Using key '{key}' to {mode}")
    result = secret.vernam_cipher(text, key)

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
