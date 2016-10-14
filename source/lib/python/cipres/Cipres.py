# Cipres.py

import os
import re
import shutil
import StringIO
import subprocess
import sys

from BuildError import BuildError
from Properties import Properties
from os import path


def __filter(props, text, dest):
    filter_pattern = re.compile("@\\{([a-zA-Z0-9._\\-]+)\\}", re.MULTILINE)

    def filter_replace(match):
        key = match.group(1)

        if key in props:
            return props[key]
        elif key[:4] == "env.":
            env_key = key[4:]

            if env_key in os.environ:
                return os.environ[env_key]

        return match.group(0)

    dest_file = open(dest, "w")

    try:
        if props is not None:
            text = filter_pattern.sub(filter_replace, text)

        dest_file.write(text)

    finally:
        dest_file.close()


def filter_file(props, src, dest):
    src_file = open(src, "r")

    try:
        text = src_file.read()

        __filter(props, text, dest)

    finally:
        src_file.close()


def filter_props(props, src, dest):
    self_ref_pattern = re.compile("^([a-zA-Z0-9._\\-]+)\\s*=.*@\\{\\1\\}.*$", re.MULTILINE)

    while True:
        src_text = StringIO.StringIO()

        try:
            src.save(src_text)

            __filter(props, src_text.getvalue(), dest)

        finally:
            src_text.close()

        dest_props = Properties(dest)
        dest_text = StringIO.StringIO()

        try:
            dest_props.save(dest_text)

            match = self_ref_pattern.search(dest_text.getvalue())

            if match is not None:
                raise BuildError("self-referencing value found for property %s" % (match.group(1)))

        finally:
            dest_text.close()

        modified = False

        for key, value in src.iteritems():
            if dest_props[key] != value:
                modified = True

                break

        if not modified:
            break

        src = dest_props


def copy_tree(props, src, dest):
    name_pattern = re.compile(".+\\.properties")

    if not path.exists(dest):
        os.mkdir(dest, 0775)

    for root, subdirs, files in os.walk(src):
        dest_dir = "%s%s" % (dest, root[len(src):])

        for name in subdirs:
            dest_subdir = path.join(dest_dir, name)

            if not path.exists(dest_subdir):
                os.mkdir(dest_subdir, 0775)

        for name in files:
            src_name = path.join(root, name)
            dest_name = path.join(dest_dir, name)

            if path.islink(src_name):
                link_target = os.readlink(src_name)

                os.symlink(link_target, dest_name)

            elif path.exists(dest_name) and name_pattern.match(name):
                src_props = Properties(src_name)
                dest_props = Properties(dest_name)

                for key, value in src_props.iteritems():
                    dest_props[key] = value

                filter_props(props, dest_props, dest_name)

            else:
                filter_file(props, src_name, dest_name)

            shutil.copystat(src, dest)


def exec_command(args):
    print "exec_command: ", args
    process = subprocess.Popen(args, stdout=sys.stdout, stderr=sys.stderr)

    returncode = process.wait()
    if returncode != 0:
        raise BuildError("%s returned exit code %d" % (args[0], returncode))


def shell_command(args):
    print "shell_command: ", args
    process = subprocess.Popen(args, stdout=subprocess.PIPE, shell=True)

    (out, err) = process.communicate()
    returncode = process.wait()
    if returncode != 0:
        message = "%s returned exit code %d" % (args[0], returncode)

        if err is not None:
            message = "%s: %s" % (message, err)

        raise BuildError(message)
    return out


