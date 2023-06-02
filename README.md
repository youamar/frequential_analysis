# Frequential analysis
This project includes two Python scripts to perform encoding and decoding operations using the Caesar and Vernam ciphers.

## Architecture
```
└── util
    ├── preprocess.py
    └── secret.py
└── cesar.py
└── vernam.py
└── plain_text.txt
```

## Build
To build this project, you need to have Python 3 installed on your system.

## Caesar Cipher
To use the Caesar cipher script, you need to run the cesar.py file with the following command:
```
python cesar.py [--key KEY] [--mode MODE] input_file output_file
```
```
    input_file is the path to the file that you want to encode or decode.
    output_file is the path to the file where you want to store the result.
    --key is an optional argument that specifies the key to use for encoding or decoding.
    --mode is an optional argument that specifies the mode to use, which can be either "encode" or "decode". The default mode is "encode".
```

## Vernam Cipher
To use the Vernam cipher script, you need to run the vernam.py file with the following command:
```
python vernam.py [--key KEY] [--mode MODE] input_file output_file
```
```
    input_file is the path to the file that you want to encode or decode.
    output_file is the path to the file where you want to store the result.
    --key is an optional argument that specifies the key to use for encoding or decoding. If no key is provided, the script will try to find a key.
    --mode is an optional argument that specifies the mode to use, which can be either "encode" or "decode". The default mode is "encode".
```

Make sure to replace input_file and output_file with the actual paths to your input and output files. You can also specify the optional arguments according to your needs.
## Examples
Here are some examples to demonstrate how to use the scripts:

### Caesar Cipher
To encode the input.txt file using the key 3 and store the result in output.txt, run the following command:
```
python cesar.py --key 3 --mode encode input.txt output.txt
```

To decode the input.txt file using the key 3 and store the result in output.txt, run the following command:
```
python cesar.py --key 3 --mode decode input.txt output.txt
```

### Vernam Cipher
To encode the input.txt file using the key "secret" and store the result in output.txt, run the following command:
```
python vernam.py --key secret --mode encode input.txt output.txt
```

To decode the input.txt file using the key "secret" and store the result in output.txt, run the following command:
```
python vernam.py --key secret --mode decode input.txt output.txt
```
