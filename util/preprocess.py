import unicodedata
import re

def removeDiacritics(text):
    """
    Remove diacritics.
    Only the diacritics are removed, not the whole character.
    The character 'à' becomes 'a'.
    """
    return ''.join(c for c in unicodedata.normalize('NFD', text)
                   if not unicodedata.combining(c))


def sanitizeToAlpha(text):
    """
    Sanitize the given string to only lowercase alphabetic characters.

    All characters are converted to lowercase.
    Characters with diacritics are converted to the corresponding
    alphabetic character.
    All remaining non-alphabetic characters are removed.
    """
    return re.sub(r'[^a-z]', '', removeDiacritics(text.lower()))