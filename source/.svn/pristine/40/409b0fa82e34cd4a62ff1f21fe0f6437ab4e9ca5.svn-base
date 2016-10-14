# Properties.py

import collections
import re


class Properties(collections.MutableMapping):

    class Value(str):

        def __new__(cls, value, position, *args, **kwargs):
            new_value = str.__new__(cls, value)

            new_value.position = position

            return new_value


    def __init__(self, filename=None):
        self._props = collections.OrderedDict()
        self._lines = []

        if filename is not None:
            self.loadFromFile(filename)

    def __getitem__(self, key):
        return self._props[key]

    def __setitem__(self, key, value):
        position = None

        if key in self._props:
            position = self._props[key].position
            parts = re.match("([^=:]*[^=:\\s])([ \\t]*[=:][ \\t]*)?.*", self._lines[position])

            line_key = parts.group(1)
            line_separator = parts.group(2)

            if line_separator is None:
                line_separator = "="

            self._lines[position] = "%s%s%s\n" % (line_key, line_separator, value)

        else:
            position = len(self._lines)

            self._lines.append("%s=%s\n" % (key, value))

        self._props[key] = self.Value(value, position)

    def __delitem__(self, key):
        if key in self._props:
            position = self._props[key].position

            del self._props[key]
            del self._lines[position]

            for value in self._props.itervalues():
                if value.position > position:
                    value.position -= 1

    def __iter__(self):
        return iter(self._props)

    def __len__(self):
        return len(self._props)

    def load(self, stream):
        pair_pattern = re.compile("\\s*([^=:]*[^=:\\s])(?:\\s*[=:]\\s*)?(.*)\\n?")

        while True:
            line = stream.readline();

            if not line:
                break

            self._lines.append(line)

            if line[0] == "#" or line[0] == "!":
                continue

            pair = pair_pattern.match(line)

            if pair:
                key = pair.group(1)
                value = pair.group(2)

                self._props[key] = self.Value(value, len(self._lines) - 1)

    def loadFromFile(self, filename):
        input_file = open(filename, "r")

        try:
            self.load(input_file)

        finally:
            input_file.close()

    def save(self, stream, props_only=True):
        if props_only:
            for value in self._props.itervalues():
                stream.write(self._lines[value.position])
        else:
            for value in self._lines:
                stream.write(value)

    def saveToFile(self, filename, props_only=True):
        output_file = open(filename, "w")

        try:
            self.save(output_file, props_only)

        finally:
            output_file.close()
