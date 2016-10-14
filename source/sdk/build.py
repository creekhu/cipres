#!/usr/bin/env python

import cipres
import os
import re
import shutil
import sys

from os import path


def get_usage_message(script_name):
    return "usage: %s topdir confdir propfile target" % (path.basename(script_name))


def verify_script_location(dirname, script):
    for path_dir in os.environ["PATH"].split(os.pathsep):
        path_name = path.join(path_dir, script)

        if path.exists(path_name):
            if path_dir != dirname:
                raise cipres.BuildError("Path should be configured such that '%s' executes %s\nbut first instance is %s path_dir (%s) dirname (%s)" % (script, path.join(dirname, script), path.join(path_dir, script), path_dir, dirname))

            return

    raise cipres.BuildError("Couldn't find %s on the execution path" % (script))


def main(argv):
    try:
        sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)

        if len(argv) != 5:
            raise cipres.BuildError(get_usage_message(argv[0]))

        top_dir = argv[1]
        conf_dir = argv[2]
        prop_file = argv[3]
        clean_target = False
        dependencies_target = False
        prepare_target = False
        package_target = False
        deploy_target = False

        if argv[4] == "clean":
            clean_target = True
        elif argv[4] == "dependencies":
            dependencies_target = True
        elif argv[4] == "prepare":
            prepare_target = True
        elif argv[4] == "package":
            package_target = True
        elif argv[4] == "deploy":
            deploy_target = True
        else:
            raise cipres.BuildError("unknown argument %s\n%s" % (argv[4], get_usage_message(argv[0])))

        if clean_target:
            return 0

        if dependencies_target:
            print "emailbean"

            return 0

        module_dir = path.dirname(argv[0])
        module_build_dir = path.join(top_dir, "build", "sdk")
        build_props = cipres.Properties(prop_file)

        if prepare_target:
            print "\npreparing the sdk source code..."

            module_build_src_dir = path.join(module_build_dir, "src")
            module_build_scripts_dir = path.join(module_build_dir, "scripts")
            conf_module_src_dir = path.join(conf_dir, "sdk", "src")
            conf_module_scripts_dir = path.join(conf_dir, "sdk", "scripts")

            if not path.exists(module_build_dir):
                os.mkdir(module_build_dir, 0775)

            cipres.copy_tree(None, path.join(module_dir, "src"), module_build_src_dir)

            if path.exists(conf_module_src_dir):
                cipres.copy_tree(None, conf_module_src_dir, module_build_src_dir)

            cipres.copy_tree(None, path.join(module_dir, "scripts"), module_build_scripts_dir)

            if path.exists(conf_module_scripts_dir):
                cipres.copy_tree(None, conf_module_scripts_dir, module_build_scripts_dir)

        if package_target:
            print "\nbuilding the sdk...\n"

            python_install = build_props.get("python.install", "develop")

            os.chdir(module_dir)

            cipres.exec_command(["mvn", "-Dpython.install=%s" % (python_install), "install"])

        if deploy_target:
            print "\ndeploying the sdk..."

            output_dir = path.join(module_build_dir, "target")
            cog_prop_file = path.join(output_dir, "classes", "cog.properties")
            cog_dest_dir = build_props.get("grid.globus.dir", path.join(os.environ["HOME"], ".globus"))

            shutil.copy2(cog_prop_file, cog_dest_dir)

            sdk_versions_dir = build_props.get("build.sdk.versionsDir", os.environ.get("SDK_VERSIONS"))

            if sdk_versions_dir is None or len(sdk_versions_dir) == 0:
                raise cipres.BuildError("property build.sdk.versionsDir is missing or empty")

            scripts = build_props["build.sdk.deployedScripts"]
            script_names = re.split("\\s*,\\s*", scripts)
            output_script_dir = path.join(output_dir, "scripts")

            for name in script_names:
                src_name = path.join(output_script_dir, name)
                dest_name = path.join(sdk_versions_dir, name)

                shutil.copy2(src_name, dest_name)

                executable = False
                index = name.rfind(".")

                if index < 0:
                    executable = True
                else:
                    extension = name[index:]

                    if extension == ".py" or extension == ".sh":
                        executable = True

                if executable:
                    os.chmod(dest_name, 0755)
                else:
                    os.chmod(dest_name, 0600)

            shutil.copy2(path.join(output_dir, "sdk-2.0-jar-with-dependencies.jar"), sdk_versions_dir)

            exec_path = os.environ["PATH"]

            os.environ["PATH"] = "%s%s%s" % (exec_path, os.pathsep, sdk_versions_dir)
            os.environ["SDK_VERSIONS"] = sdk_versions_dir

            verify_script_location(sdk_versions_dir, "loadResultsD")
            verify_script_location(sdk_versions_dir, "piseEval")

            print "Starting cipres daemons"
            cipres.exec_command(["checkJobsD", "restart"])
            cipres.exec_command(["loadResultsD", "restart"])
            cipres.exec_command(["submitJobD", "restart"])
            cipres.exec_command(["recoverResultsD", "restart"])

        return 0

    except (IOError, OSError, cipres.BuildError) as err:
        sys.stderr.write("%s\n" % str(err))

        return 1


if __name__ == "__main__":
    sys.exit(main(sys.argv))
