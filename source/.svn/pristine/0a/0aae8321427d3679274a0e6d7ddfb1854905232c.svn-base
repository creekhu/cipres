#!/usr/bin/env python

import os
import shutil
import sys

from os import path

if __name__ == "__main__":
    sys.path.insert(0, path.join(path.dirname(path.abspath(sys.argv[0])), "lib", "python"))

import cipres


def get_usage_message(script_name):
    return "usage: %s [-h | --help] [--conf-dir=dirname] [--prop-file=filename] [--module=dirname ...] [--no-deps] [clean] [prepare] [package] [deploy]" % (path.basename(script_name))


def add_module(dir_name, build_list, index):
    if dir_name in build_list:
        return False

    build_list.insert(index, dir_name)

    return True


def add_dependencies(exec_args, dir_name, build_list, index):
    exec_args[0] = path.join(dir_name, "build.py")
    exec_args[4] = "dependencies"

    output = cipres.shell_command(" ".join(exec_args))

    if len(output) > 0:
        dependencies = output.strip().split("\n")

        for module in dependencies:
            dep_dir_name = path.abspath(module)

            if add_module(dep_dir_name, build_list, index):
                add_dependencies(exec_args, dep_dir_name, build_list, index)

                index += 1


def write_build_properties(defaults, overrides, output):
    default_props = cipres.Properties(defaults)

    if overrides != defaults:
        override_props = cipres.Properties(overrides)

        for key, value in override_props.iteritems():
            default_props[key] = value

    cipres.filter_props(default_props, default_props, output)


def building_module(module_dirs, module_name):
    search_name = "%s%s" % (path.sep, module_name)
    name_length = len(search_name)

    for module in module_dirs:
        if module[-name_length:] == search_name:
            return True

    return False


def run_build_scripts(target, exec_args, module_dirs):
    exec_args[4] = target

    for dir_name in module_dirs:
        exec_args[0] = path.join(dir_name, "build.py")

        cipres.exec_command(exec_args)


def main(argv):
    try:
        clean_target = False
        prepare_target = False
        package_target = False
        deploy_target = False
        add_deps = True
        conf_dir = None
        prop_file = None
        module_dirs = []
        top_dir = path.dirname(path.abspath(argv[0]))

        sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)
        for arg in argv[1:]:
            pair = arg.split("=")

            if pair[0] == "-h" or pair[0] == "--help":
                print get_usage_message(argv[0])

                return 0

            elif pair[0] == "--no-deps":
                add_deps = False
            elif pair[0] == "--conf-dir":
                conf_dir = pair[1]
            elif pair[0] == "--prop-file":
                prop_file = pair[1]
            elif pair[0] == "--module" or pair[0] == "--front-end":
                module_dirs.append(path.abspath(pair[1]))
            elif pair[0] == "clean":
                clean_target = True
            elif pair[0] == "prepare":
                prepare_target = True
            elif pair[0] == "package":
                package_target = True
                prepare_target = True

            elif pair[0] == "deploy":
                deploy_target = True
                prepare_target = True
                package_target = True

            else:
                raise cipres.BuildError("unknown argument %s\n%s" % (arg, get_usage_message(argv[0])))

        if not clean_target and not prepare_target and not package_target and not deploy_target:
            package_target = True
            prepare_target = True

        if len(module_dirs) == 0:
            module_dirs.append(path.join(top_dir, "portal"))

        if conf_dir is None:
            conf_dir = path.join(top_dir, "example")
        else:
            conf_dir = path.abspath(conf_dir)

        if prop_file is None:
            prop_file = path.join(conf_dir, "build.properties")
        else:
            prop_file = path.join(conf_dir, prop_file)

        build_dir = path.join(top_dir, "build")
        build_prop_file = path.join(build_dir, "build.properties")
        build_pom_file = path.join(build_dir, "pom.xml")
        exec_args = ["", top_dir, conf_dir, build_prop_file, ""]
        java_opts = " -Dlog4jdbc.debug.stack.prefix=org.ngbw -Dlog4jdbc.sqltiming.warn.threshold=40 -Dlog4jdbc.sqltiming.error.threshold=40 "

        if "JAVA_OPTS" in os.environ:
            java_opts = "%s%s" % (os.environ["JAVA_OPTS"], java_opts)

        os.environ["JAVA_OPTS"] = java_opts
        os.environ["PYTHONPATH"] = path.join(top_dir, "lib", "python")

        module_build_list = [ ]

        for dir_name in module_dirs:
            index = len(module_build_list)

            if add_module(dir_name, module_build_list, index) and add_deps:
                add_dependencies(exec_args, dir_name, module_build_list, index)

        if clean_target:
            print "\nremoving build output..."

            if path.exists(build_dir):
                shutil.rmtree(build_dir)

            exec_args[4] = "clean"

            for dir_name in module_build_list:
                exec_args[0] = path.join(dir_name, "build.py")

                cipres.exec_command(exec_args)

        if prepare_target:
            print "\npreparing...\n"

            if not path.exists(build_dir):
                os.mkdir(build_dir, 0775)

            write_build_properties(path.join(top_dir, "default", "build.properties.template"), prop_file, build_prop_file)

            build_props = cipres.Properties(build_prop_file)

            # Default is to assume we're doing a portal build
            build_props["use.rest.callback"] = "false"
            build_props["portal2.protected.pages"] = "/statistics.action"
            build_props["rest.tools.disable"] = "true"
            build_props["portal.tools.disable"] = "false"

            if building_module(module_build_list, "rest"):
                build_props["rest.tools.disable"] = "false"

                if not building_module(module_build_list, "portal"):
                    build_props["use.rest.callback"] = "true"
                    build_props["portal2.protected.pages"] = "/*"
                    build_props["portal.tools.disable"] = "true"

            tmp = cipres.shell_command("svn info | grep ^URL | awk '{print $NF}'")
            build_props["scigap.svn.top"] = tmp

            build_props.saveToFile(build_prop_file)

            pom_props = {"build.pom.properties" : "<top.dir>%s</top.dir><filter.file.path>%s</filter.file.path><build.portal.staticUrl>%s</build.portal.staticUrl><build.portal.appName>%s</build.portal.appName><build.rest.appName>%s</build.rest.appName>" %
                (top_dir, build_prop_file, build_props["build.portal.staticUrl"], build_props["build.portal.appName"], build_props["build.rest.appName"])}

            cipres.filter_file(pom_props, path.join(top_dir, "default", "pom.xml.template"), build_pom_file)

            run_build_scripts("prepare", exec_args, module_build_list)

        if package_target:
            print "\nbuilding...\n"

            cipres.exec_command(["mvn", "-f", build_pom_file, "install"])

            run_build_scripts("package", exec_args, module_build_list)

        if deploy_target:
            print "\ndeploying...\n"
            build_props = cipres.Properties(build_prop_file)
            tomcat_dir = build_props.get("build.portal.tomcatDir", os.environ.get("CATALINA_HOME"))

            if tomcat_dir is None or len(tomcat_dir) == 0:
                raise cipres.BuildError("property build.portal.tomcatDir is missing or empty")

            tomcat_bin_dir = path.join(tomcat_dir, "bin")

            print "\nshutting down tomcat...\n"
            try:
                cipres.exec_command([path.join(tomcat_bin_dir, "shutdown.sh")])
            except (cipres.BuildError) as err:
                sys.stderr.write("%s\n" % str(err))
                sys.stderr.write("Continuing anyway.\n")

            print "\nrunning build.py scripts\n"
            run_build_scripts("deploy", exec_args, module_build_list)

            print "\nstarting tomcat...\n"
            cipres.exec_command([path.join(tomcat_bin_dir, "startup.sh")])

        return 0

    except (IOError, OSError, cipres.BuildError) as err:
        sys.stderr.write("%s\n" % str(err))

        return 1


if __name__ == "__main__":
    sys.exit(main(sys.argv))
