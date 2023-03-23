#!/usr/bin/env python3
"""Example script for SECG4 that reads and sanitize text."""

import click
import re
import sys
from unidecode import unidecode


def removeDiacritics(text):
    """
    Remove diacritics.
    Only the diacritics are removed, not the whole character.
    The character 'à' becomes 'a'.
    """
    return unidecode(text)


def sanitizeToAlpha(text):
    """
    Sanitize the given string to only lowercase alphabetic characters.

    All characters are converted to lowercase.
    Characters with diacritics are converted to the corresponding
    alphabetic character.
    All remaining non-alphabetic characters are removed.
    """
    return re.sub(r'[^a-z]', '', removeDiacritics(text).lower())


@click.group()
def cli():
    """
    Example script to read and write file/stdin while sanitizing the input.
    """
    pass


@cli.command()
@click.option(
    '--output-newlines/--ignore-newlines', default=True, show_default=True,
    help=('Whether to output or ignore new lines as in the input.')
)
@click.option(
    '--output', 'output_file', type=click.File('w'), default=sys.stdout,
    help="Output file path. Use - to output on stdout. [default: stdout]"
)
@click.argument('input_file', nargs=1, type=click.File('r'), default=sys.stdin)
def sanitize(output_newlines, output_file, input_file):
    """
    Sanitize text.

    Read the text from INPUT_FILE. Use - to read from stdin.
    """
    if output_newlines:
        def write(line):
            print(line, file=output_file)
    else:
        def write(line):
            output_file.write(line)

    for line in input_file:
        write(sanitizeToAlpha(line))


if __name__ == '__main__':
    cli()
